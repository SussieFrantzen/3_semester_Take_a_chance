package app.dao;

import app.config.HibernateTestConfig;
import app.entities.User;
import app.testutils.UserTestPopulator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {

    private final EntityManagerFactory emf =
            HibernateTestConfig.getEntityManagerFactory();

    private EntityManager em;
    private UserDAO userDAO;
    private Map<String, User> seeded;

    @BeforeEach
    void beforeEach() {
        seeded = UserTestPopulator.populate(emf);

        em = emf.createEntityManager();
        userDAO = new UserDAO(em);
    }

    @AfterEach
    void afterEach() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @AfterAll
    void shutdown() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void create() {
        User user = new User(
                "newuser",
                "newPassword123",
                "newuser@example.com"
        );

        User result = userDAO.create(user);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());

        User found = userDAO.findById(result.getId());

        assertNotNull(found);
        assertEquals(result.getId(), found.getId());
        assertEquals("newuser", found.getUsername());
        assertEquals("newuser@example.com", found.getEmail());
    }

    @Test
    void findById() {
        User expected = seeded.get("user1");

        User result = userDAO.findById(expected.getId());

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getEmail(), result.getEmail());
    }

    @Test
    void findByIdReturnsNullWhenUserDoesNotExist() {
        User result = userDAO.findById(-1);

        assertNull(result);
    }

    @Test
    void update() {
        User user = seeded.get("user2");

        user.setUsername("updatedUser");
        user.setEmail("updated@example.com");

        User result = userDAO.update(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());

        User updatedUser = userDAO.findById(user.getId());

        assertNotNull(updatedUser);
        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void remove() {
        User user = seeded.get("user3");
        int id = user.getId();

        assertNotNull(userDAO.findById(id));

        boolean result = userDAO.remove(id);

        assertTrue(result);
        assertNull(userDAO.findById(id));
    }

    @Test
    void removeReturnsFalseWhenUserDoesNotExist() {
        boolean result = userDAO.remove(-1);

        assertFalse(result);
    }

    @Test
    void findByUsername() {
        User expected = seeded.get("user1");

        User result = userDAO.findByUsername("testuser1");

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getEmail(), result.getEmail());
    }

    @Test
    void findByUsernameReturnsNullWhenUserDoesNotExist() {
        User result = userDAO.findByUsername("unknownUser");

        assertNull(result);
    }

    @Test
    void existsByEmailReturnsTrueWhenEmailExists() {
        boolean result =
                userDAO.existsByEmail("testuser1@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmailReturnsFalseWhenEmailDoesNotExist() {
        boolean result =
                userDAO.existsByEmail("unknown@example.com");

        assertFalse(result);
    }

    @Test
    void existsByUsernameReturnsTrueWhenUsernameExists() {
        boolean result = userDAO.existsByUsername("testuser2");

        assertTrue(result);
    }

    @Test
    void existsByUsernameReturnsFalseWhenUsernameDoesNotExist() {
        boolean result = userDAO.existsByUsername("unknownUser");

        assertFalse(result);
    }
}
