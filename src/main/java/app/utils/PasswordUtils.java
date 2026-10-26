package app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    private PasswordUtils() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password må ikke være tomt"
            );
        }

        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(
            String password,
            String hashedPassword
    ) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

