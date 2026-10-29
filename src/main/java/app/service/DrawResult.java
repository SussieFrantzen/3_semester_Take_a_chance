package app.service;

public record DrawResult(
        String playerName,
        boolean coconutDrawn,
        boolean missed,
        int position,
        boolean shieldActive,
        boolean challengeFinished,
        String nextPlayerName,
        int cardsRemaining,
        String message
) {}