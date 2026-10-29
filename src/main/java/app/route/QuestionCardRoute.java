package app.route;

import app.controllers.QuestionCardController;
import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.security.Role;
import app.service.OpenAiQuestionCardService;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

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
    public EndpointGroup getRoutes() {
        return () -> {
            get("/", questionCardController::getAllQuestionCards, Role.AUTHENTICATED);
            get("/{id}", questionCardController::getQuestionCardById, Role.AUTHENTICATED);
            post("/generate", questionCardController::generateQuestionCards, Role.AUTHENTICATED);
        };
    }
}

