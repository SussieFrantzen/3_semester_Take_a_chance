package app.route;

import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.service.OpenAiQuestionCardService;
import io.javalin.apibuilder.EndpointGroup;

public class QuestionCardRoute {

    private final QuestionCardController questionCardController;

    public QuestionCardRoute(
            QuestionCardDAO questionCardDAO,
            CardDeckDAO cardDeckDAO,
            OpenAiQuestionCardService service) {

        questionCardController = new QuestionCardController(
                questionCardDAO,
                cardDeckDAO,
                service
        );
    }

    public QuestionCardController getController() {
        return questionCardController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
        };
    }
}

