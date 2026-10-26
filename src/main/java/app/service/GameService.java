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

        if (existingGame != null) { // tjekker om spillet findes i aktivGames allerede.
            return existingGame;
        }

        EntityManager em = emf.createEntityManager();

        try {
            Game game = em.find(Game.class, gameId);  // forsøger at finde spil fra database ud fra spil-ID

            if (game == null) {
                throw new NoSuchElementException(  // TODO bør finde måde at rette brugervenlighed
                        "Spillet blev ikke fundet."
                );
            }

            game.getPlayers().size(); // henter information om spiller antal
            game.getGameBoard().getFields().size(); // antal spilfelter.

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


    public synchronized TurnResult playGoOrBadLuck(Long gameId) {

        Game game = getGame(gameId); // bruger metoden ovenover

        if (monkeyService.isChallengeActive(gameId)) { // tjekker om abeudfordringen er igang
            throw new IllegalStateException(
                    "Abeudfordringen skal afsluttes først."
            );
        }

        Player player = game.getCurrentPlayer(); // spilleren hvis tur det er hentes

        boolean go = ThreadLocalRandom.current().nextBoolean();  // Tilfældigt udfald: GO eller BAD LUCK.

        String outcome = go ? "GO" : "BAD LUCK"; // bruger en ternary operator til at vælge en tekst:

        int movedFields = 0; // variabel for antal felter der må rykkes
        boolean turnContinues = false; // variabel for om spillerens turen kan forsættes eller afsluttes.

        if (go) {
            game.setGoCount(game.getGoCount() + 1);

            if (game.getGoCount() == 1) { // Første GO giver et nyt forsøg.
                turnContinues = true;
            } else {
                movedFields = 2;// Anden GO flytter spilleren to felter.
            }

        } else {
            if (game.getGoCount() == 1) { // GO efterfulgt af BAD LUCK:
                movedFields = 1; // ét felt frem.
            }

            // BAD LUCK ved første forsøg vil spilleren bliver stående derfor ikke behøves en if for det.
        }

        String landedFieldType = null; // Variabel for typen af det felt, spilleren lander på.
        FateCard fateCard = null; // objekt af skæbnekort der måske trækkes.
        QuestionCard questionCard = null; // objekt af spørgsmålskort der måske trækkes
        boolean challengeStarted = false; // boolean og en udfordring er igang.

        if (movedFields > 0) { // hvis variablen for antal felter der må rykkes er større end 0
            moveForward(game, player, movedFields); // flyttes spilleren frem med det angivet antal felter der må rykkes

            landedFieldType = getCurrentFieldType(game, player); // det landefelt tjekkes hvilken type det er spilleren er landet på

            if ("FATEFIELD".equals(landedFieldType)) { // hvis det er et skæbnefelt
                CardDeck deck = game.getGameBoard().getCardDeck(); // kortbunken hentes

                if (deck == null) {
                    throw new IllegalStateException(
                            "Spillet mangler et CardDeck."
                    );
                }

                fateCard = deck.drawFateCard(); // der trækkes et skæbnekort fra bunken

                if (fateCard == null) {
                    throw new IllegalStateException(
                            "Der er ingen FateCard i kortbunken."
                    );
                }


                if ("MONKEY_CHALLENGE".equalsIgnoreCase( // Hvis skæbnekortet er en Monkey_challenge aktiveres abeudfordringen.
                        fateCard.getAction())) {

                    monkeyService.startChallenge(game, player, dice);

                    challengeStarted = true;
                }

            } else if ("QUESTIONFIELD".equals(landedFieldType)) { // hvis feltet er et spørgsmålsfelt
                CardDeck deck = game.getGameBoard().getCardDeck(); // hentes kortbunken

                if (deck == null) {
                    throw new IllegalStateException(
                            "Spillet mangler en kortbunke."
                    );
                }

                String ageGroup = ageGroupService.getAgeGroup(player.getAge()); // spillerens alder hentes ned

                List<QuestionCard> matchingCards =
                        questionCardDAO.findByDeckAndAgeGroup(
                                deck.getId(),
                                ageGroup
                        );

                if (matchingCards.isEmpty()) {
                    throw new IllegalStateException(
                            "Der findes ingen spørgsmål til aldersgruppen "
                                    + ageGroup
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


        if (questionCard != null || fateCard != null) { //Registrer, om der er trukket et kort.
            pendingCardActions.put(gameId, true);
        }


        if (!turnMustWait) {
            finishTurn(game);
            saveGame(game);
        }

        return new TurnResult( player.getName(), outcome, movedFields, player.getPosition(), landedFieldType,
                turnContinues, challengeStarted, fateCard, questionCard, turnMustWait
        );
    }

    private void moveForward( Game game, Player player, int amount) {
        List<Field> fields = game.getGameBoard().getFields();

        if (fields.isEmpty()) {
            throw new IllegalStateException(
                    "Spillebanen har ingen felter."
            );
        }

        int newPosition = Math.min(
                player.getPosition() + amount, fields.size() - 1  // flytter spilleren men forhindre i at komme længere end antal felter spillet udgør.
        );

        player.setPosition(newPosition); // den nye position sættes.
    }

    private String getCurrentFieldType(Game game, Player player) {// finder ud af hvad type felt der er landet på.

        List<Field> fields = game.getGameBoard().getFields();

        int position = player.getPosition(); // angiver hvor spilleren står

        if (position < 0 || position >= fields.size()) {
            throw new IllegalStateException(
                    "Spillerens position er uden for banen."
            );
        }

        return fields.get(position).getType().toUpperCase(Locale.ROOT); // returnere hvad hvilken type felt spilleren er på.
    }



    public synchronized void completeMonkeyChallenge(Long gameId) {

        Game game = getGame(gameId);

        if (!monkeyService.isChallengeActive(gameId)) { //undersøger, om der er en aktiv abeudfordring.
            throw new IllegalStateException(
                    "Der er ingen aktiv abeudfordring."
            );
        }
        monkeyService.finishChallenge(gameId); // kalder metode der tjekker om det kan sluttes
        finishTurn(game); // turen afsluttes
        saveGame(game); // gemmer spillets status.
    }


    public synchronized void completeCardAction(Long gameId) {
        Game game = getGame(gameId);

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

    private void saveGame(Game game) { // gemmer spil til databasen
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

    public record TurnResult( // TODO overvej om denne skal rykkes til egen klasse.
            String playerName,
            String outcome,
            int movedFields,
            int position,
            String landedFieldType,
            boolean turnContinues,
            boolean challengeStarted,
            FateCard fateCard,
            QuestionCard questionCard,
            boolean turnMustWait
    ) {
    }
}
