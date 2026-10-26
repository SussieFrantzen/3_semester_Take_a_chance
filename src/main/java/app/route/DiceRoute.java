package app.route;

import app.controllers.DiceController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DiceRoute {

    private final DiceController diceController =
            new DiceController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/roll", diceController.rollDice);
        };
    }
}