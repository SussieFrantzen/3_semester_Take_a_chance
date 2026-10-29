package app.route;

import app.controllers.UserController;
import app.security.Role;
import app.service.UserService;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoute {

    private final UserController userController;

    public UserRoute() {
        this.userController = new UserController();
    }

    public UserRoute(UserService userService) {
        this.userController = new UserController(userService);
    }

    public EndpointGroup getRoutes() {
        return () -> {
            post("/register", userController::createUser, Role.ANYONE);
            post("/login", userController::login, Role.ANYONE);
            post("/logout", userController::logout, Role.AUTHENTICATED);
            get("/me", userController::getCurrentUser, Role.AUTHENTICATED);
        };
    }
}