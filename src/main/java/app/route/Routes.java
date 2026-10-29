package app.route;

import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.service.OpenAiQuestionCardService;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameRoute gameRoute = new GameRoute();

    private final UserRoute userRoute = new UserRoute();

    private final DiceRoute diceRoute = new DiceRoute();

    private final CoinRoute coinRoute = new CoinRoute();
    private final GameSelectionRoute gameSelectionRoute = new GameSelectionRoute();


    private final QuestionCardRoute questionCardRoute;

    public Routes(
            QuestionCardDAO questionCardDAO,
            CardDeckDAO cardDeckDAO,
            OpenAiQuestionCardService service) {

        questionCardRoute = new QuestionCardRoute(
                questionCardDAO,
                cardDeckDAO,
                service
        );
    }

    public EndpointGroup getRoutes() {
        return () -> {

            path("/games", gameRoute.getRoutes());

            path("/users", userRoute.getRoutes());

            path("/dice", diceRoute.getRoutes());

            path("/coin", coinRoute.getRoutes());

            path("/game-selection", gameSelectionRoute.getRoutes());

            path("/question-cards", questionCardRoute.getRoutes());
        };
    }
}

