package app.route;

import app.controllers.GameController;
import app.security.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameRoute {

    private final GameController gameController = new GameController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/", gameController.createGame, Role.AUTHENTICATED);

            get("/{gameId}", gameController.getGame, Role.AUTHENTICATED);

            post("/{gameId}/turn", gameController.playTurn, Role.AUTHENTICATED);

            post(
                    "/{gameId}/monkey-challenge/complete",
                    gameController.completeMonkeyChallenge,
                    Role.AUTHENTICATED
            );

            post(
                    "/{gameId}/card-action/complete",
                    gameController.completeCardAction,
                    Role.AUTHENTICATED
            );

            post(
                    "/{gameId}/users/{userId}/played",
                    gameController.markPlayerAsPlayed,
                    Role.AUTHENTICATED
            );

            get(
                    "/{gameId}/users/{userId}/played",
                    gameController.checkPlayerHasPlayed,
                    Role.AUTHENTICATED
            );
        };
    }
}