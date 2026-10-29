package app.dao;

import app.config.HibernateConfig;
import app.entities.Player;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class PlayerDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public Player create(Player player) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(
                    em,
                    () -> em.persist(player)
            );
            return player;
        }
    }

    public Player findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Player.class, id);
        }
    }

    public List<Player> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT p FROM Player p",
                    Player.class
            ).getResultList();
        }
    }

    public Player update(Player player) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(
                    em,
                    () -> em.merge(player)
            );
        }
    }

    public void remove(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(em, () -> {
                Player player = em.find(Player.class, id);

                if (player != null) {
                    em.remove(player);
                }
            });
        }
    }
}