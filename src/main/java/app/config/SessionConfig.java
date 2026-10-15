package app.config;

import java.util.EnumSet;
import jakarta.servlet.SessionTrackingMode;
import org.eclipse.jetty.server.session.SessionHandler;


public class SessionConfig {
    public static SessionHandler sessionConfig() {
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setUsingCookies(true);
        sessionHandler.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        sessionHandler.setHttpOnly(true);
        return sessionHandler;
    }
}
