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
                "Diana Prince",
                "Themyscira",
                "wonder@woman.com"
        );

        User result = userDAO.create(user);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Diana Prince", result.getUsername());
        assertEquals("wonder@woman.com", result.getEmail());

        User found = userDAO.findById(result.getId());

        assertNotNull(found);
        assertEquals(result.getId(), found.getId());
        assertEquals("Diana Prince", found.getUsername());
        assertEquals("wonder@woman.com", found.getEmail());
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
    void findByIdUserNotExist() {
        User result = userDAO.findById(-1);

        assertNull(result);
    }

    @Test
    void update() {
        User user = seeded.get("user2");

        user.setUsername("Clark Kent");
        user.setEmail("superman@return.com");

        User result = userDAO.update(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("Clark Kent", result.getUsername());
        assertEquals("superman@return.com", result.getEmail());

        User updatedUser = userDAO.findById(user.getId());

        assertNotNull(updatedUser);
        assertEquals("Clark Kent", updatedUser.getUsername());
        assertEquals("superman@return.com", updatedUser.getEmail());
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
    void findByUsername() {
        User expected = seeded.get("user1");

        User result = userDAO.findByUsername("Barry Allen");

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getEmail(), result.getEmail());
    }

    @Test
    void findByUsernameUserDoesNotExist() {
        User result = userDAO.findByUsername("unknownUser");

        assertNull(result);
    }

    @Test
    void existsByEmailExists() {
        boolean result =
                userDAO.existsByEmail("The@Flash.com");

        assertTrue(result);
    }

    @Test
    void existsByEmailDoesNotExist() {
        boolean result =
                userDAO.existsByEmail("joker@time.com");

        assertFalse(result);
    }

    @Test
    void existsByUsernameExists() {
        boolean result = userDAO.existsByUsername("Kent Clark");

        assertTrue(result);
    }

    @Test
    void existsByUsernameDoesNotExist() {
        boolean result = userDAO.existsByUsername("unknownUser");

        assertFalse(result);
    }
}
