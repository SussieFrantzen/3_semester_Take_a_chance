package app.security;

import app.dtos.UserDTO;
import app.service.UserService;
import io.javalin.http.Context;

public class AccessController {

    private final SecurityController securityController;

    public AccessController() {
        this.securityController = new SecurityController();
    }

    public AccessController(UserService userService) {
        this.securityController =
                new SecurityController(userService);
    }

    public void accessHandler(Context ctx) {
        if (ctx.routeRoles().isEmpty()) {
            return;
        }

        if (ctx.routeRoles().contains(Role.ANYONE)) {
            return;
        }

        UserDTO user = securityController.authenticate(ctx);

        ctx.attribute("user", user);
    }
}