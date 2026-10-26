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
    private static final int MIN_QUESTION_CARDS = 20;
    private static final int MAX_QUESTION_CARDS = 200;
    private static final int CARDS_PER_GENERATION = 10;

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

            // Behandl kun hver aldersgruppe én gang.
            if (!preparedAgeGroups.add(ageGroup)) {
                continue;
            }

            List<QuestionCard> cards =
                    questionCardDAO.findBySubjectAndAgeGroup(
                            subject, ageGroup
                    );

            int existingCount = cards.size();

            // Hvis der allerede er mindst 20 spørgsmål,
            // skal der ikke genereres flere.
            while (existingCount < MIN_QUESTION_CARDS
                    && existingCount < MAX_QUESTION_CARDS) {

                int amountToGenerate = Math.min(
                        CARDS_PER_GENERATION,
                        MAX_QUESTION_CARDS - existingCount
                );

                List<QuestionCard> generated =
                        aiService.generateAndSaveCards(
                                subject,
                                ageGroup,
                                deck,
                                amountToGenerate
                        );

                // Hent det faktiske antal fra databasen igen.
                cards = questionCardDAO.findBySubjectAndAgeGroup(
                        subject, ageGroup
                );

                int newCount = cards.size();

                // Undgå et uendeligt loop, hvis AI'en
                // kun returnerer dubletter.
                if (newCount <= existingCount) {
                    throw new IllegalStateException(
                            "Der kunne ikke gemmes nok nye spørgsmål "
                                    + "til aldersgruppen " + ageGroup
                                    + ". Prøv igen."
                    );
                }

                existingCount = newCount;
            }

            if (existingCount < MIN_QUESTION_CARDS) {
                throw new IllegalStateException(
                        "Der er færre end 20 spørgsmål til aldersgruppen "
                                + ageGroup
                );
            }

            // Sørg for, at kortene er knyttet til den aktuelle bunke.
            for (QuestionCard card : cards) {
                deck.addQuestionCard(card);
            }
        }
    }

   /*
    private static final int MIN_QUESTION_CARDS = 20;
    private static final int MAX_QUESTION_CARDS = 200;
    private static final int CARDS_PER_GENERATION = 10;

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

            // Undgå at kontrollere samme aldersgruppe flere gange.
            if (!preparedAgeGroups.add(ageGroup)) {
                continue;
            }

            List<QuestionCard> cards =
                    questionCardDAO.findBySubjectAndAgeGroup(
                            subject, ageGroup
                    );

            int existingCount = cards.size();

// Hvis der er mindst 20 spørgsmål, skal der ikke genereres flere.
            if (existingCount >= MIN_QUESTION_CARDS) {
                continue;
            }

// Beregn, hvor mange pladser der er tilbage op til 200.
            int remainingCapacity = MAX_QUESTION_CARDS - existingCount;

            if (remainingCapacity <= 0) {
                continue;
            }

// Generér højst 10 spørgsmål og aldrig flere end den resterende plads.
            int amountToGenerate =
                    Math.min(CARDS_PER_GENERATION, remainingCapacity);

// Generér det beregnede antal spørgsmål.
            aiService.generateAndSaveCards(
                    subject,
                    ageGroup,
                    deck,
                    amountToGenerate
            );
        }
    }

    */
/*
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
                aiService.generateAndSaveCards(subject, ageGroup, deck);

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

 */
}