package app.controllers;

import app.entities.Coin;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class CoinController {

    private final Coin coin = new Coin();

    // Kast mønten
    public Handler flipCoin = ctx -> {
        String result = coin.flip();

        ctx.status(HttpStatus.OK).json(
                Map.of(
                        "result", result,
                        "message", "Mønten landede på " + result
                )
        );
    };
}