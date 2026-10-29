package app.service;

import app.config.HibernateConfig;
import app.dao.GameBoardDAO;
import app.dao.QuestionCardDAO;
import app.dao.UserDAO;
import app.dtos.CreateGameRequest;
import app.entities.CardDeck;
import app.entities.Game;
import app.entities.GameBoard;
import app.entities.Player;
import app.entities.QuestionCard;
import app.entities.User;
import app.exeption.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameCreationService {

    private static final int MIN_QUESTION_CARDS = 20;
    private static final int MAX_QUESTION_CARDS = 200;
    private static final int CARDS_PER_GENERATION = 10;

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    private final GameBoardDAO gameBoardDAO = new GameBoardDAO();
    private final QuestionCardDAO questionCardDAO =
            new QuestionCardDAO();

    private final AgeGroupService ageGroupService =
            new AgeGroupService();

    private final OpenAiQuestionCardService aiService =
            new OpenAiQuestionCardService(questionCardDAO);



    public Game createGame(Integer userId, CreateGameRequest request) throws IOException, InterruptedException {

        if (userId == null) {
            throw new ApiException(401, "Du skal være logget ind.");
        }

        if (request == null || request.gameBoardId() == null) {
            throw new ApiException(
                    400,
                    "Du skal vælge en spilleplade."
            );
        }

        validatePlayers(request.players());


        User user;
        GameBoard board;

        try (EntityManager em = emf.createEntityManager()) {

            UserDAO userDAO = new UserDAO(em);

            user = userDAO.findById(userId);

            if (user == null) {
                throw new ApiException(
                        404,
                        "Brugeren blev ikke fundet."
                );
            }

            board = em.find(
                    GameBoard.class,
                    request.gameBoardId()
            );

            if (board == null) {
                throw new ApiException(
                        404,
                        "Den valgte spilleplade blev ikke fundet."
                );
            }

            // Initialiser de relationer, der skal bruges efter lukning.
            if (board.getCardDeck() != null) {
                board.getCardDeck().getId();
            }
        }


        CardDeck deck = board.getCardDeck();

        if (deck == null || deck.getId() == null) {
            throw new ApiException(
                    409,
                    "Den valgte spilleplade mangler en kortbunke."
            );
        }

        if (board.getSubject() == null
                || board.getSubject().isBlank()) {
            throw new ApiException(
                    409,
                    "Spillepladen mangler et emne."
            );
        }

        // Sørg for, at spørgsmålskort findes til spillerne.
        prepareQuestionCards(board, deck, request.players());

        // Opret et nyt spil fra serverens validerede data.
        Game game = new Game();
        game.setUser(user);
        game.setGameBoard(board);
        game.setCurrentPlayerIndex(0);
        game.setGoCount(0);

        for (CreateGameRequest.CreatePlayerRequest playerRequest
                : request.players()) {

            Player player = new Player(
                    playerRequest.name().trim(),
                    playerRequest.age(),
                    null
            );

            game.addPlayer(player);
        }

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            Game savedGame = em.merge(game);
            em.getTransaction().commit();
            savedGame.getPlayers().size();
            savedGame.getGameBoard().getBoardName();
            savedGame.getGameBoard().getGameName().getName();

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

    private void validatePlayers(
            List<CreateGameRequest.CreatePlayerRequest> players
    ) {
        if (players == null || players.isEmpty()) {
            throw new ApiException(
                    400,
                    "Spillet skal have mindst én spiller."
            );
        }

        if (players.size() > 4) {
            throw new ApiException(
                    400,
                    "Der må højst være fire spillere."
            );
        }

        for (CreateGameRequest.CreatePlayerRequest player : players) {
            if (player == null
                    || player.name() == null
                    || player.name().isBlank()) {
                throw new ApiException(
                        400,
                        "Alle spillere skal have et navn."
                );
            }

            if (player.name().trim().length() > 100) {
                throw new ApiException(
                        400,
                        "Spillernavne må højst være 100 tegn."
                );
            }

            if (player.age() < 1 || player.age() > 120) {
                throw new ApiException(
                        400,
                        "Spillerens alder skal være mellem 1 og 120."
                );
            }
        }
    }

    private void prepareQuestionCards(
            GameBoard board,
            CardDeck deck,
            List<CreateGameRequest.CreatePlayerRequest> players
    ) throws IOException, InterruptedException {

        Set<String> preparedAgeGroups = new HashSet<>();

        for (CreateGameRequest.CreatePlayerRequest player : players) {

            String ageGroup =
                    ageGroupService.getAgeGroup(player.age());

            if (!preparedAgeGroups.add(ageGroup)) {
                continue;
            }

            List<QuestionCard> cards =
                    questionCardDAO.findByDeckAndAgeGroup(
                            deck.getId(),
                            ageGroup
                    );

            int existingCount = cards.size();

            while (existingCount < MIN_QUESTION_CARDS
                    && existingCount < MAX_QUESTION_CARDS) {

                int amountToGenerate = Math.min(
                        CARDS_PER_GENERATION,
                        MAX_QUESTION_CARDS - existingCount
                );

                aiService.generateAndSaveCards(
                        board.getSubject(),
                        ageGroup,
                        deck,
                        amountToGenerate
                );

                cards = questionCardDAO.findByDeckAndAgeGroup(
                        deck.getId(),
                        ageGroup
                );

                int newCount = cards.size();

                if (newCount <= existingCount) {
                    throw new ApiException(
                            503,
                            "Der kunne ikke genereres nok nye spørgsmål "
                                    + "til aldersgruppen " + ageGroup + "."
                    );
                }

                existingCount = newCount;
            }

            if (existingCount < MIN_QUESTION_CARDS) {
                throw new ApiException(
                        503,
                        "Der findes ikke nok spørgsmål til aldersgruppen "
                                + ageGroup + "."
                );
            }
        }
    }
}