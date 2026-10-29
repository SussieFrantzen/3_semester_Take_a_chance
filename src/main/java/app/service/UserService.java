package app.service;

import app.config.HibernateConfig;
import app.dao.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.exeption.ApiException;
import app.utils.PasswordUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserService {

    private final EntityManagerFactory emf;

    public UserService(){
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    public UserService(EntityManagerFactory emf){
        this.emf = emf;
    }

    public UserDTO login(String username, String password) {

        if (username == null || username.isBlank()) {
            throw new ApiException(400, "Brugernavn påkrævet");
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(400, "Password påkrævet");
        }

        try (EntityManager em = emf.createEntityManager()) {
            UserDAO userDAO = new UserDAO(em);

            User user = userDAO.findByUsername(username);

            if (user == null
                    || !PasswordUtils.checkPassword(
                    password,
                    user.getPassword()
            )) {
                throw new ApiException(
                        401,
                        "Forkert brugernavn eller password"
                );
            }

            return new UserDTO(
                    user.getId(),
                    user.getUsername()
            );
        }
    }

    public UserDTO createUser(
            String username,
            String password,
            String email
    ) {
        if (username == null || username.isBlank()) {
            throw new ApiException(400, "Brugernavn påkrævet");
        }

        if (password == null || password.isBlank()) {
            throw new ApiException(400, "Password påkrævet");
        }

        if (email == null || email.isBlank()) {
            throw new ApiException(400, "Email påkrævet");
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        User user = new User(username, hashedPassword, email);

        try (EntityManager em = emf.createEntityManager()) {
            UserDAO userDAO = new UserDAO(em);

            if (userDAO.existsByUsername(username)) {
                throw new ApiException(
                        409,
                        "Brugernavnet er allerede taget."
                );
            }

            if (userDAO.existsByEmail(email)) {
                throw new ApiException(
                        409,
                        "Emailadressen er allerede registreret."
                );
            }

            userDAO.create(user);

            return new UserDTO(
                    user.getId(),
                    user.getUsername()
            );
        }

    }

    public UserDTO getUserById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            UserDAO userDAO = new UserDAO(em);

            User user = userDAO.findById(id);

            if (user == null) {
                throw new ApiException(
                        404,
                        "Brugeren blev ikke fundet"
                );
            }

            return new UserDTO(
                    user.getId(),
                    user.getUsername()
            );
        }
    }
}
