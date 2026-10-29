package app.route;

import app.controllers.GameSelectionController;
import app.security.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameSelectionRoute {

    private final GameSelectionController controller =
            new GameSelectionController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/names", controller::getGameNames, Role.AUTHENTICATED);
            get("/boards", controller::getGameBoards, Role.AUTHENTICATED);
        };
    }
}