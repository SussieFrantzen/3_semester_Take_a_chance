/*package app.route;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameRoute gameRoute = new GameRoute();
    private final UserRoute userRoute = new UserRoute();
    public EndpointGroup getRoutes() {
        return () -> {
            path("/games", gameRoute.getRoutes());
            path("/users", userRoute.getRoutes());
        };
    }
}

 */

package app.route;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameRoute gameRoute = new GameRoute();

    private final UserRoute userRoute = new UserRoute();

    private final DiceRoute diceRoute = new DiceRoute();

    private final CoinRoute coinRoute = new CoinRoute();


    public EndpointGroup getRoutes() {
        return () -> {

            path("/games", gameRoute.getRoutes());

            path("/users", userRoute.getRoutes());

            path("/dice", diceRoute.getRoutes());

            path("/coin", coinRoute.getRoutes());
        };
    }
}


/*
package app.route;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameRoute gameRoute =
            new GameRoute();

    private final UserRoute userRoute =
            new UserRoute();

    private final QuestionCardRoute questionCardRoute;

    public Routes(QuestionCardRoute questionCardRoute) {
        this.questionCardRoute = questionCardRoute;
    }

    public EndpointGroup getRoutes() {
        return () -> {

            path("/games", gameRoute.getRoutes());

            path("/users", userRoute.getRoutes());

            path("/api/question-cards", questionCardRoute.getRoutes()
            );
        };
    }
}

 */