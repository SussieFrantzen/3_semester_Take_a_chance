package app.dao;
import app.entities.User;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;


public class UserDAO {

    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public User create(User user) {
        TransactionUtil.execute(em, () -> em.persist(user));
        return user;
    }

    public User findById(int id) {
        return em.find(User.class, id);
    }

    public User update(User user) {
        return TransactionUtil.executeWithResult(
                em,
                () -> em.merge(user)
        );
    }

    public boolean remove(int id) {
        return TransactionUtil.executeWithResult(em, () -> {
            User user = em.find(User.class, id);

            if (user == null) {
                return false;
            }
            em.remove(user);
            return true;
        });
    }

    public User findByUsername(String username) {
        return em.createQuery(
                        "SELECT u FROM User u WHERE u.username = :username",
                        User.class
                )
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }


    public boolean existsByEmail(String email) {
        return em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                        Long.class
                )
                .setParameter("email", email)
                .getSingleResult() > 0;
    }

    public boolean existsByUsername(String username) {
        return em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                        Long.class
                )
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

}
