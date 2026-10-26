package app.route;

import app.controllers.GameController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameRoute {

    private final GameController gameController =
            new GameController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/{userId}", gameController.createGame);
            get("/{gameId}", gameController.getGame);
            post("/{gameId}/turn", gameController.playTurn);

            post(
                    "/{gameId}/monkey-challenge/complete",
                    gameController.completeMonkeyChallenge
            );

            post(
                    "/{gameId}/card-action/complete",
                    gameController.completeCardAction
            );
        };
    }
}