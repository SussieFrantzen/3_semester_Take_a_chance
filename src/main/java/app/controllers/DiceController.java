
package app.controllers;

import app.entities.Dice;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class DiceController {

    private final Dice dice = new Dice();

    // Kast terningen
    public Handler rollDice = ctx -> {
        int result = dice.roll();

        ctx.status(HttpStatus.OK).json(
                Map.of(
                        "value", result,
                        "message", "Terningen landede på " + result
                )
        );
    };
}
