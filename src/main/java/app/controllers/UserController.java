package app.controllers;

import app.dao.UserDAO;
import app.dtos.UserDTO;
import app.exeption.ApiException;
import app.service.UserService;

import io.javalin.http.Context;

public class UserController {

    private final UserService userService = new UserService();
    private final UserDAO userDAO = new UserDAO();

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

        ctx.json(user);
    }

    public void logout(Context ctx) {
        ctx.status(204);
    }

    public void getCurrentUser(Context ctx) {
        throw new ApiException(
                401,
                "Du skal være logget ind"
        );
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