
package app.controllers;

import app.entities.Game;
import app.service.GameCreationService;
import app.service.GameService;

import app.dtos.CreateGameRequest;
import app.exeption.ApiException;
import app.service.PlayerProgressService;
import app.service.TurnResult;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;
import java.util.NoSuchElementException;

public class GameController {

    private final GameCreationService gameCreationService =
            new GameCreationService();

    private final GameService gameService =
            new GameService();

    private final PlayerProgressService playerProgressService =
            new PlayerProgressService();


    // Opret et nyt spil
    public Handler createGame = ctx -> {
        try {
            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            CreateGameRequest request =
                    ctx.bodyAsClass(CreateGameRequest.class);

            Game createdGame =
                    gameCreationService.createGame(userId, request);

            Map<String, Object> response = Map.of(
                    "id", createdGame.getId(),
                    "gameBoardId", createdGame.getGameBoard().getId(),
                    "boardName", createdGame.getGameBoard().getBoardName(),
                    "currentPlayerIndex",
                    createdGame.getCurrentPlayerIndex(),
                    "players", createdGame.getPlayers().stream()
                            .map(player -> Map.of(
                                    "id", player.getId(),
                                    "name", player.getName(),
                                    "age", player.getAge(),
                                    "position", player.getPosition(),
                                    "points", player.getPoints()
                            ))
                            .toList()
            );

            ctx.status(HttpStatus.CREATED).json(response);

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt ID."));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(Map.of(
                            "error",
                            "Oprettelsen af spillet blev afbrudt."
                    ));

        } catch (IllegalArgumentException | IllegalStateException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(Map.of("error", "Kunne ikke oprette spillet."));
        }
    };


    // Hent et eksisterende spil
    public Handler getGame = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            Game game = gameService.getGameForUser(
                    gameId,
                    userId
            );

            ctx.json(game);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));
        }
    };



    public Handler playTurn = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            TurnResult result =
                    gameService.playGoOrBadLuck(gameId, userId);

            ctx.json(result);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };



    public Handler completeMonkeyChallenge = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            gameService.completeMonkeyChallenge(gameId, userId);

            ctx.status(HttpStatus.NO_CONTENT);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };



    public Handler completeCardAction = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer userId = ctx.sessionAttribute("userId");

            if (userId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            gameService.completeCardAction(gameId, userId);

            ctx.status(HttpStatus.NO_CONTENT);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };



    public Handler markPlayerAsPlayed = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer sessionUserId =
                    ctx.sessionAttribute("userId");

            if (sessionUserId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            Integer requestedUserId =
                    Integer.parseInt(ctx.pathParam("userId"));


            gameService.getGameForUser(gameId, sessionUserId);

            playerProgressService.markAsPlayed(
                    gameId,
                    requestedUserId
            );

            ctx.status(HttpStatus.NO_CONTENT);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };



    public Handler checkPlayerHasPlayed = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            Integer sessionUserId =
                    ctx.sessionAttribute("userId");

            if (sessionUserId == null) {
                ctx.status(HttpStatus.UNAUTHORIZED)
                        .json(Map.of(
                                "error",
                                "Du skal være logget ind."
                        ));
                return;
            }

            Integer requestedUserId =
                    Integer.parseInt(ctx.pathParam("userId"));


            gameService.getGameForUser(gameId, sessionUserId);

            boolean hasPlayed =
                    playerProgressService.hasPlayedBefore(
                            gameId,
                            requestedUserId
                    );

            ctx.json(Map.of("hasPlayedBefore", hasPlayed));

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt ID."));

        } catch (ApiException e) {
            ctx.status(e.getCode())
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));
        }
    };
}
