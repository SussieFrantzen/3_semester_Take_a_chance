package app.service;

import app.config.HibernateConfig;
import app.entities.Player;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class PlayerProgressService {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public void markAsPlayed(Long gameId, Integer userId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            List<Player> players = em.createQuery(
                            "SELECT p FROM Player p " +
                                    "WHERE p.game.id = :gameId " +
                                    "AND p.user.id = :userId",
                            Player.class
                    )
                    .setParameter("gameId", gameId)
                    .setParameter("userId", userId)
                    .getResultList();

            if (players.isEmpty()) {
                throw new IllegalStateException(
                        "Brugeren har ingen spiller i dette spil."
                );
            }

            if (players.size() > 1) {
                throw new IllegalStateException(
                        "Der findes flere spillerposter for brugeren i spillet."
                );
            }

            Player player = players.get(0);
            player.setHasPlayedBefore(true);

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public boolean hasPlayedBefore(Long gameId, Integer userId) {
        EntityManager em = emf.createEntityManager();

        try {
            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Player p " +
                                    "WHERE p.game.id = :gameId " +
                                    "AND p.user.id = :userId " +
                                    "AND p.hasPlayedBefore = true",
                            Long.class
                    )
                    .setParameter("gameId", gameId)
                    .setParameter("userId", userId)
                    .getSingleResult();

            return count > 0;

        } finally {
            em.close();
        }
    }
}