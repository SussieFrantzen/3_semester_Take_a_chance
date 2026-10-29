package app.controllers;

import app.dtos.UserDTO;
import app.exeption.ApiException;
import app.service.UserService;
import io.javalin.http.Context;

public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void createUser(Context ctx) {
        CreateUserRequest request =
                ctx.bodyAsClass(CreateUserRequest.class);

        UserDTO user = userService.createUser(
                request.username,
                request.password,
                request.email
        );

        ctx.status(201).json(user);
    }

    public void login(Context ctx) {
        LoginRequest request =
                ctx.bodyAsClass(LoginRequest.class);

        UserDTO user = userService.login(
                request.username,
                request.password
        );

        ctx.req().getSession(true).setAttribute(
                "userId",
                user.getId()
        );

        ctx.json(user);
    }

    public void logout(Context ctx) {
        var session = ctx.req().getSession(false);

        if (session != null) {
            session.invalidate();
        }

        ctx.status(204);
    }

public void getCurrentUser(Context ctx) {
    Integer userId = ctx.sessionAttribute("userId");

    if (userId == null) {
        throw new ApiException(
                401,
                "Du skal være logget ind"
        );
    }

    UserDTO user = userService.getUserById(userId);

    ctx.json(user);
}

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class CreateUserRequest {
        public String username;
        public String password;
        public String email;
    }
}