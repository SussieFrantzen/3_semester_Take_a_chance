package app.config;

import app.dtos.Message;
import app.exeption.ApiException;
import app.route.UserRoute;
import app.security.AccessController;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class TestServerConfig {

    public Javalin createServer() {
        EntityManagerFactory testEmf =
                HibernateTestConfig.getEntityManagerFactory();

        UserService userService = new UserService(testEmf);

        UserRoute userRoute = new UserRoute(userService);

        AccessController accessController =
                new AccessController(userService);

        Javalin app = Javalin.create(config -> {
            config.router.mount(router -> {
                router.beforeMatched(
                        accessController::accessHandler
                );
            }).apiBuilder(() ->
                    path("/users", userRoute.getRoutes())
            );
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
            e.printStackTrace();
            ctx.status(500)
                    .json(new Message(500, "Intern serverfejl"));
        });

        return app;
    }
}