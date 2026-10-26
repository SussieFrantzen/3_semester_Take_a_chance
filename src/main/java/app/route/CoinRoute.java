package app.route;

import app.controllers.CoinController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CoinRoute {

    private final CoinController coinController =
            new CoinController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/flip", coinController.flipCoin);
        };
    }
}