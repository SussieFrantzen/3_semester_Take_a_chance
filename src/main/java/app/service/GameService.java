package app.service;

import app.config.HibernateConfig;
import app.dao.QuestionCardDAO;
import app.entities.CardDeck;
import app.entities.Dice;
import app.entities.FateCard;
import app.entities.Field;
import app.entities.Game;
import app.entities.Player;
import app.entities.QuestionCard;
import app.exeption.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    private final MonkeyChallengeService monkeyService =
            new MonkeyChallengeService();

    private final AgeGroupService ageGroupService =
            new AgeGroupService();

    private final QuestionCardDAO questionCardDAO =
            new QuestionCardDAO();


    private final Map<Long, Game> activeGames = new HashMap<>();

    private final Dice dice = new Dice();

    private final Map<Long, Boolean> pendingCardActions =
            new HashMap<>();


    public synchronized Game getGame(Long gameId) {
        Game existingGame = activeGames.get(gameId);

        if (existingGame != null) {
            return existingGame;
        }

        EntityManager em = emf.createEntityManager();

        try {
            Game game = em.find(Game.class, gameId);

            if (game == null) {
                throw new NoSuchElementException(
                        "Spillet blev ikke fundet."
                );
            }

            game.getPlayers().size();
            game.getGameBoard().getFields().size();

            CardDeck deck = game.getGameBoard().getCardDeck();

            if (deck != null) {
                deck.getFateCards().size();
                deck.getQuestionCards().size();
            }

            activeGames.put(gameId, game);

            return game;

        } finally {
            em.close();
        }
    }


    public synchronized TurnResult playGoOrBadLuck(Long gameId, Integer userId) {

        Game game = getGameForUser(gameId, userId);

        if (monkeyService.isChallengeActive(gameId)) {
            throw new IllegalStateException(
                    "Abeudfordringen skal afsluttes først."
            );
        }

        Player player = game.getCurrentPlayer();

        boolean go = ThreadLocalRandom.current().nextBoolean();

        String outcome = go ? "GO" : "BAD LUCK";

        int movedFields = 0;
        boolean turnContinues = false;

        if (go) {
            game.setGoCount(game.getGoCount() + 1);

            if (game.getGoCount() == 1) {
                turnContinues = true;
            } else {
                movedFields = 2;
            }

        } else {
            if (game.getGoCount() == 1) {
                movedFields = 1;
            }

        }

        String landedFieldType = null;
        FateCard fateCard = null;
        QuestionCard questionCard = null;
        boolean challengeStarted = false;

        if (movedFields > 0) {
            moveForward(game, player, movedFields);

            landedFieldType = getCurrentFieldType(game, player);

            if ("FATEFIELD".equals(landedFieldType)) {
                CardDeck deck = game.getGameBoard().getCardDeck();

                if (deck == null) {
                    throw new IllegalStateException(
                            "Spillet mangler et CardDeck."
                    );
                }

                fateCard = deck.drawFateCard();

                if (fateCard == null) {
                    throw new IllegalStateException(
                            "Der er ingen FateCard i kortbunken."
                    );
                }


                if ("MONKEY_CHALLENGE".equalsIgnoreCase(
                        fateCard.getAction())) {

                    monkeyService.startChallenge(game, player, dice);

                    challengeStarted = true;
                }

            } else if ("QUESTIONFIELD".equals(landedFieldType)) {
                CardDeck deck = game.getGameBoard().getCardDeck();

                if (deck == null) {
                    throw new IllegalStateException(
                            "Spillet mangler en kortbunke."
                    );
                }

                String ageGroup = ageGroupService.getAgeGroup(player.getAge());

                List<QuestionCard> matchingCards =
                        questionCardDAO.findByDeckAndAgeGroup(
                                deck.getId(),
                                ageGroup
                        );

                if (matchingCards.isEmpty()) {
                    throw new IllegalStateException
                            ("Der findes ingen spørgsmål til aldersgruppen " + ageGroup
                            );
                }

                int index = ThreadLocalRandom.current()
                        .nextInt(matchingCards.size());

                questionCard = matchingCards.get(index);

                if (questionCard == null) {
                    throw new IllegalStateException(
                            "Der findes ingen spørgsmål til aldersgruppen "
                                    + ageGroup
                    );
                }
            }
        }

        boolean turnMustWait = turnContinues
                || challengeStarted
                || questionCard != null
                || fateCard != null;


        if (questionCard != null || fateCard != null) {
            pendingCardActions.put(gameId, true);
        }


        if (!turnMustWait) {
            finishTurn(game);
            saveGame(game);
        }

        return new TurnResult(player.getName(), outcome, movedFields, player.getPosition(), landedFieldType,
                turnContinues, challengeStarted, fateCard, questionCard, turnMustWait
        );
    }

    private void moveForward(Game game, Player player, int amount) {
        List<Field> fields = game.getGameBoard().getFields();

        if (fields.isEmpty()) {
            throw new IllegalStateException(
                    "Spillebanen har ingen felter."
            );
        }

        int newPosition = Math.min(
                player.getPosition() +
                        amount, fields.size() - 1);

        player.setPosition(newPosition);
    }

    private String getCurrentFieldType(Game game, Player player) {

        List<Field> fields = game.getGameBoard().getFields();

        int position = player.getPosition();

        if (position < 0 || position >= fields.size()) {
            throw new IllegalStateException(
                    "Spillerens position er uden for banen."
            );
        }

        return fields.get(position).getType().toUpperCase(Locale.ROOT);
    }


    public synchronized void completeMonkeyChallenge(Long gameId, Integer userId) {

        Game game = getGameForUser(gameId, userId);

        if (!monkeyService.isChallengeActive(gameId)) {
            throw new IllegalStateException(
                    "Der er ingen aktiv abeudfordring."
            );
        }
        monkeyService.finishChallenge(gameId);
        finishTurn(game);
        saveGame(game);
    }


    public synchronized void completeCardAction(Long gameId, Integer userId) {
        Game game = getGameForUser(gameId, userId);

        if (monkeyService.isChallengeActive(gameId)) {
            throw new IllegalStateException(
                    "Abeudfordringen er ikke færdig."
            );
        }

        if (!pendingCardActions.getOrDefault(gameId, false)) {
            throw new IllegalStateException(
                    "Der er ingen ventende kort-handling."
            );
        }

        pendingCardActions.remove(gameId);

        finishTurn(game);
        saveGame(game);
    }

    private void finishTurn(Game game) {
        game.endTurn();
        game.setGoCount(0);
    }

    private void saveGame(Game game) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.merge(game);

            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }


    public synchronized Game getGameForUser(
            Long gameId,
            Integer userId
    ) {
        if (userId == null) {
            throw new ApiException(401, "Du skal være logget ind.");
        }
        Game game = getGame(gameId);

        if (game.getUser() == null
                || !userId.equals(game.getUser().getId())) {
            throw new ApiException(
                    403,
                    "Du har ikke adgang til dette spil."
            );
        }
        return game;
    }

}