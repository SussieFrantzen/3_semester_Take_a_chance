package app.service;

import app.config.HibernateConfig;
import app.dao.QuestionCardDAO;
import app.dao.UserDAO;
import app.entities.CardDeck;
import app.entities.Game;
import app.entities.GameBoard;
import app.entities.Player;
import app.entities.QuestionCard;
import app.entities.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GameCreationService {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    private final UserDAO userDAO = new UserDAO();

    private final QuestionCardDAO questionCardDAO =
            new QuestionCardDAO();

    private final AgeGroupService ageGroupService =
            new AgeGroupService();

    private final OpenAiQuestionCardService aiService =
            new OpenAiQuestionCardService(questionCardDAO);


    public Game createGame(Integer userId, Game game)
            throws IOException, InterruptedException {

        if (userId == null || game == null) {
            throw new IllegalArgumentException(
                    "Bruger og spil skal angives."
            );
        }

        User user = userDAO.getUserById(userId);

        if (game.getGameBoard() == null) {
            throw new IllegalStateException(
                    "Spillet mangler en spilleplade."
            );
        }

        if (game.getGameBoard().getCardDeck() == null) {
            throw new IllegalStateException(
                    "Spillepladen mangler en kortbunke."
            );
        }

        if (game.getPlayers().isEmpty()) {
            throw new IllegalStateException(
                    "Spillet skal have mindst én spiller."
            );
        }

        if (game.getPlayers().size() > 4) {
            throw new IllegalStateException(
                    "Der må højst være fire spillere."
            );
        }

        game.setUser(user);

        prepareQuestionCards(game);

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Game savedGame = em.merge(game);

            em.getTransaction().commit();

            return savedGame;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }


    private void prepareQuestionCards(Game game)
            throws IOException, InterruptedException {

        GameBoard board = game.getGameBoard();
        CardDeck deck = board.getCardDeck();

        String subject = board.getSubject();

        if (subject == null || subject.isBlank()) {
            throw new IllegalStateException(
                    "Spillepladen mangler et emne."
            );
        }

        Set<String> preparedAgeGroups = new HashSet<>();

        for (Player player : game.getPlayers()) {

            String ageGroup =
                    ageGroupService.getAgeGroup(player.getAge());

            if (!preparedAgeGroups.add(ageGroup)) {
                continue;
            }

            List<QuestionCard> cards =
                    questionCardDAO.findBySubjectAndAgeGroup(
                            subject, ageGroup
                    );


            if (cards.size() < 200) {
                aiService.generateAndSaveCards(subject, ageGroup);

                cards = questionCardDAO.findBySubjectAndAgeGroup(
                        subject, ageGroup
                );
            }


            if (cards.isEmpty()) {
                throw new IllegalStateException(
                        "Der findes ingen spørgsmål til aldersgruppen "
                                + ageGroup
                );
            }

            for (QuestionCard card : cards) {

                boolean alreadyAdded =
                        deck.getQuestionCards()
                                .stream()
                                .anyMatch(existing ->
                                        existing.getId() != null
                                                && Objects.equals(
                                                existing.getId(),
                                                card.getId()
                                        )
                                );

                if (!alreadyAdded) {
                    deck.addQuestionCard(card);
                }
            }
        }
    }
}