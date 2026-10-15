package app.service;
import app.dao.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.exeption.ApiException;
import app.utils.PasswordUtils;


public class UserService {

        private final UserDAO userDAO = new UserDAO();


        public UserDTO login(String userName, String password) {

            if (userName == null || userName.isBlank()) {
                throw new ApiException(400, "Brugernavn påkrævet");
            }

            if (password == null || password.isBlank()) {
                throw new ApiException(400, "Password påkrævet");
            }

            User user = userDAO.getUserByUserName(userName);

            if (!PasswordUtils.checkPassword(password,user.getPassword())){
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

            userDAO.create(user);

            return new UserDTO(user.getId(), user.getUsername());
        }


    }
