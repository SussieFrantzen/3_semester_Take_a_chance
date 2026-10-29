package app.config;

import app.dao.CardDeckDAO;
import app.dao.QuestionCardDAO;
import app.dtos.Message;
import app.exeption.ApiException;
import app.route.Routes;
import app.security.AccessController;
import app.service.OpenAiQuestionCardService;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;

public class ServerConfig {

    private final Routes routes;

    public ServerConfig() {
        QuestionCardDAO questionCardDAO =
                new QuestionCardDAO();

        CardDeckDAO cardDeckDAO =
                new CardDeckDAO();

        OpenAiQuestionCardService questionCardService =
                new OpenAiQuestionCardService(questionCardDAO);

        routes = new Routes(
                questionCardDAO,
                cardDeckDAO,
                questionCardService
        );
    }

    public Javalin createServer() {
        AccessController accessController =
                new AccessController();

        Javalin app = Javalin.create(config -> {
            config.router.mount(router -> {
                router.beforeMatched(
                        accessController::accessHandler
                );
            }).apiBuilder(routes.getRoutes());
        });

        app.exception(ApiException.class, (e, ctx) -> {
            ctx.status(e.getCode())
                    .json(new Message(
                            e.getCode(),
                            e.getMessage()
                    ));
        });

        app.exception(UnauthorizedResponse.class, (e, ctx) -> {
            ctx.status(401)
                    .json(new Message(401, e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            System.err.println(
                    "FEJL I REQUEST: " + ctx.method()
                            + " " + ctx.path()
            );

            e.printStackTrace();

            ctx.status(500).json(
                    new Message(500, "Intern serverfejl")
            );
        });

        return app;
    }
}
