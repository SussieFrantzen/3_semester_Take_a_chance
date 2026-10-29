
package app.testutils;

import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public final class UserTestPopulator {

    private UserTestPopulator() {
    }

    public static Map<String, User> populate(EntityManagerFactory emf) {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                em.createNativeQuery(
                        "TRUNCATE TABLE users RESTART IDENTITY CASCADE"
                ).executeUpdate();

                User user1 = new User(
                        "testuser1",
                        "testPassword123",
                        "testuser1@example.com"
                );

                User user2 = new User(
                        "testuser2",
                        "testPassword456",
                        "testuser2@example.com"
                );

                User user3 = new User(
                        "testuser3",
                        "testPassword789",
                        "testuser3@example.com"
                );

                em.persist(user1);
                em.persist(user2);
                em.persist(user3);

                em.flush();
                em.getTransaction().commit();

                Map<String, User> seeded = new LinkedHashMap<>();
                seeded.put("user1", user1);
                seeded.put("user2", user2);
                seeded.put("user3", user3);

                return seeded;

            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}
