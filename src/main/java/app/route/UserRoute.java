package app.route;

import app.controllers.UserController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoute {

    private final UserController userController =
            new UserController();

    public EndpointGroup getRoutes() {
        return () -> {
            post("/register", userController::createUser);
            post("/login", userController::login);
            post("/logout", userController::logout);
            get("/me", userController::getCurrentUser);
        };
    }
}