package app.dao;

import app.config.HibernateConfig;
import app.entities.Game;
import app.entities.GameName;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class GameDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public Game create(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(
                    em,
                    () -> em.persist(game)
            );
            return game;
        }
    }

    public Game findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Game.class, id);
        }
    }

    public List<Game> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT g FROM Game g",
                    Game.class
            ).getResultList();
        }
    }

    public Game update(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(
                    em,
                    () -> em.merge(game)
            );
        }
    }

    public void remove(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(em, () -> {
                Game game = em.find(Game.class, id);

                if (game != null) {
                    em.remove(game);
                }
            });
        }
    }

    public List<GameName> findAllWithCardDeck() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT DISTINCT gn
                    FROM GameName gn
                    JOIN FETCH gn.cardDeck
                    ORDER BY gn.id
                    """, GameName.class)
                    .getResultList();
        }
    }
}