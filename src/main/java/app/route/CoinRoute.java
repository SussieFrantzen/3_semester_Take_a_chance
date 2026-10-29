package app.route;

import app.controllers.CoinController;
import app.security.Role;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class CoinRoute {

    private final CoinController coinController =
            new CoinController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/flip", coinController.flipCoin, Role.AUTHENTICATED);
        };
    }
}