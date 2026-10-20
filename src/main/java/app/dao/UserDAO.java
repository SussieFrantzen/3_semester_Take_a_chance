package app.dao;
import app.entities.User;
import app.exeption.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

public class UserDAO extends JPADAO<User, Integer> {

    public UserDAO() {
        super(User.class);
    }

    public User create(User user) {
        if (user == null) {
            throw new ApiException(400, "Bruger er påkrævet");
        }

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            return user;

        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new ApiException(
                    500,
                    "Oprettelse fejlede: " + e.getMessage()
            );

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new ApiException(
                    500,
                    "Der opstod en uventet fejl ved oprettelse af bruger"
            );

        } finally {
            em.close();
        }
    }

    public User getUserById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Bruger id er påkrævet");
        }

        try {
            return findById(id).orElseThrow(
                    () -> new ApiException(404, "Bruger ikke fundet")
            );

        } catch (PersistenceException e) {
            throw new ApiException(
                    500,
                    "Hent bruger fejlede: " + e.getMessage()
            );
        }
    }

    public User getUserByUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new ApiException(400, "Brugernavn påkrævet");
        }

        try (EntityManager em = emf.createEntityManager()) {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :userName",
                            User.class
                    )
                    .setParameter("userName", userName)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                throw new ApiException(
                        401,
                        "Forkert brugernavn eller password"
                );
            }

            return user;

        } catch (PersistenceException e) {
            throw new ApiException(
                    500,
                    "Hent af bruger fejlede: " + e.getMessage()
            );
        }
    }
}



/*
public class UserDAO {

    private EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();


    public User create(User user) {
        if (user == null) {
            throw new ApiException(400, "Bruger er påkrævet");
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(user);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Oprettelse fejlede: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Der opstod en uventet fejl ved oprettelse af bruger");
            }
        }
        return user;
    }


    public User getUserById(Integer id) {
        if (id == null) {
            throw new ApiException(400, "Bruger id er påkrævet");
        }
        try (EntityManager em = emf.createEntityManager()) {
            try {
                User user = em.find(User.class, id);
                if (user != null) {
                    return user;
                }
                throw new ApiException(404, "Bruger ikke fundet");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Hent bruger fejlede: " + e.getMessage());
            }
        }
    }



    public User getUserByUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new ApiException(400, "Brugernavn påkrævet");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                User user = em.createQuery(
                                "SELECT u FROM User u WHERE u.username = :userName",
                                User.class)
                        .setParameter("userName", userName)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

                if (user == null) {
                    throw new ApiException(401, "Forkert brugernavn eller password");
                }

                return user;

            } catch (PersistenceException e) {
                throw new ApiException(
                        500,
                        "Hent af bruger fejlede: " + e.getMessage()
                );
            }
        }
    }
}
*/