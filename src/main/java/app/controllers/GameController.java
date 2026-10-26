
package app.controllers;

import app.entities.Game;
import app.service.GameCreationService;
import app.service.GameService;
import app.service.GameService.TurnResult;

import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;
import java.util.NoSuchElementException;

public class GameController {

    private final GameCreationService gameCreationService =
            new GameCreationService();

    private final GameService gameService =
            new GameService();


    // Opret et nyt spil
    public Handler createGame = ctx -> {
        try {
            Integer userId =
                    Integer.parseInt(ctx.pathParam("userId"));

            Game game = ctx.bodyAsClass(Game.class);

            Game createdGame =
                    gameCreationService.createGame(userId, game);

            ctx.status(HttpStatus.CREATED).json(createdGame);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt bruger-ID."));

        } catch (IllegalArgumentException | IllegalStateException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(Map.of("error", "Oprettelsen blev afbrudt."));

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

            Game game = gameService.getGame(gameId);

            ctx.json(game);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));
        }
    };


    // Udfør en tur med GO eller BAD LUCK
    public Handler playTurn = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            TurnResult result =
                    gameService.playGoOrBadLuck(gameId);

            ctx.json(result);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };


    // Afslut abeudfordring
    public Handler completeMonkeyChallenge = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            gameService.completeMonkeyChallenge(gameId);

            ctx.status(HttpStatus.NO_CONTENT);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };


    // Afslut en kort-handling
    public Handler completeCardAction = ctx -> {
        try {
            Long gameId =
                    Long.parseLong(ctx.pathParam("gameId"));

            gameService.completeCardAction(gameId);

            ctx.status(HttpStatus.NO_CONTENT);

        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST)
                    .json(Map.of("error", "Ugyldigt spil-ID."));

        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND)
                    .json(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            ctx.status(HttpStatus.CONFLICT)
                    .json(Map.of("error", e.getMessage()));
        }
    };
}