package app.service;

import app.entities.Dice;
import app.entities.Game;
import app.entities.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MonkeyChallengeService {

    private final Map<Long, ChallengeState> activeChallenges =
            new HashMap<>();

    public synchronized ChallengeStatus startChallenge(
            Game game,
            Player startingPlayer,
            Dice dice) {

        if (game == null || game.getId() == null) {
            throw new IllegalArgumentException(
                    "Spillet skal være gemt i databasen først."
            );
        }

        if (startingPlayer == null || dice == null) {
            throw new IllegalArgumentException(
                    "Startspiller og terning skal angives."
            );
        }

        if (activeChallenges.containsKey(game.getId())) {
            throw new IllegalStateException(
                    "Spillet har allerede en aktiv abeudfordring."
            );
        }

        List<Player> players = game.getPlayers();

        if (players == null || players.isEmpty()) {
            throw new IllegalStateException(
                    "Spillet har ingen spillere."
            );
        }

        if (players.size() > 4) {
            throw new IllegalStateException(
                    "Der må højst være fire spillere."
            );
        }

        int startingIndex = players.indexOf(startingPlayer);

        if (startingIndex < 0) {
            throw new IllegalArgumentException(
                    "Startspilleren tilhører ikke spillet."
            );
        }

        int coconutCount = dice.roll();

        if (coconutCount < 1 || coconutCount > 6) {
            throw new IllegalStateException(
                    "Terningen skal give et resultat mellem 1 og 6."
            );
        }

        List<Player> turnOrder = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            turnOrder.add(
                    players.get(
                            (startingIndex + i) % players.size()
                    )
            );
        }

        List<Boolean> bag = new ArrayList<>();

        for (int i = 0; i < coconutCount; i++) {
            bag.add(true);
        }

        bag.add(false);
        Collections.shuffle(bag);

        ChallengeState state =
                new ChallengeState(turnOrder, bag, coconutCount);

        activeChallenges.put(game.getId(), state);

        return new ChallengeStatus(
                startingPlayer.getName(),
                coconutCount,
                turnOrder.size(),
                true,
                "Abeudfordringen er startet. "
                        + startingPlayer.getName()
                        + " trækker først."
        );
    }

    public synchronized DrawResult drawCard(Long gameId) {

        ChallengeState state = getActiveChallenge(gameId);

        if (state.isFinished()) {
            throw new IllegalStateException(
                    "Abeudfordringen er allerede afsluttet."
            );
        }

        Player player =
                state.turnOrder.get(state.currentPlayerIndex);

        boolean coconutDrawn = false;
        boolean missed = false;
        String message;

        if (state.bag.isEmpty()) {
            missed = true;
            message = player.getName()
                    + " springer trækket over, fordi posen er tom.";

        } else {
            int randomIndex = ThreadLocalRandom.current()
                    .nextInt(state.bag.size());

            boolean coconut = state.bag.get(randomIndex);

            if (coconut) {
                state.bag.remove(randomIndex);
                coconutDrawn = true;

                message = movePlayerBack(player);

            } else {
                missed = true;

                message = player.getName()
                        + " blev ikke ramt af en kokosnød!";
            }
        }

        state.currentPlayerIndex++;

        if (state.currentPlayerIndex >= state.turnOrder.size()) {
            state.finished = true;
        }

        String nextPlayerName = state.finished
                ? null
                : state.turnOrder
                .get(state.currentPlayerIndex)
                .getName();

        return new DrawResult(
                player.getName(),
                coconutDrawn,
                missed,
                player.getPosition(),
                player.hasShieldOfDestiny(),
                state.finished,
                nextPlayerName,
                state.bag.size(),
                message
        );
    }

    private String movePlayerBack(Player player) {

        if (player.getPosition() <= 0) {
            return player.getName()
                    + " blev ramt, men står allerede på startfeltet.";
        }

        if (player.hasShieldOfDestiny()) {
            player.useShieldOfDestiny();

            return player.getName()
                    + " blev beskyttet af Skæbnens skjold!";
        }

        player.setPosition(player.getPosition() - 1);

        return player.getName()
                + " blev ramt og rykkede ét felt tilbage.";
    }

    public synchronized boolean isChallengeActive(Long gameId) {
        ChallengeState state = activeChallenges.get(gameId);

        return state != null && !state.isFinished();
    }

    public synchronized void finishChallenge(Long gameId) {
        ChallengeState state = getActiveChallenge(gameId);

        if (!state.isFinished()) {
            throw new IllegalStateException(
                    "Alle spillere skal have afsluttet deres træk først."
            );
        }

        activeChallenges.remove(gameId);
    }

    private ChallengeState getActiveChallenge(Long gameId) {
        ChallengeState state = activeChallenges.get(gameId);

        if (state == null || state.isFinished()) {
            throw new IllegalStateException(
                    "Der findes ingen aktiv abeudfordring."
            );
        }

        return state;
    }
}