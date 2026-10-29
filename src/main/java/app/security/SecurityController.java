package app.security;

import app.dtos.UserDTO;
import app.exeption.ApiException;
import app.service.UserService;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class SecurityController {

    private final UserService userService;
    public SecurityController() {
        this.userService = new UserService();
    }

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    public UserDTO authenticate(Context ctx) {
        Integer userId = ctx.sessionAttribute("userId");

        if (userId == null) {
            throw new UnauthorizedResponse(
                    "Du skal være logget ind."
            );
        }

        try {
            return userService.getUserById(userId);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                var session = ctx.req().getSession(false);

                if (session != null) {
                    session.invalidate();
                }
                throw new UnauthorizedResponse(
                        "Din session er ugyldig. Log ind igen."
                );
            }

            throw e;
        }
    }


}