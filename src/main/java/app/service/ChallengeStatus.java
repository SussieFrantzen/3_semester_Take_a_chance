package app.service;

public record ChallengeStatus(
        String startingPlayer,
        int coconutCount,
        int playerCount,
        boolean active,
        String message
) {}
