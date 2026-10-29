package app.dao;

import app.config.HibernateConfig;
import app.entities.GameBoard;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class GameBoardDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public GameBoard create(GameBoard board) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(
                    em,
                    () -> em.persist(board)
            );
            return board;
        }
    }

    public Optional<GameBoard> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(
                    em.find(GameBoard.class, id)
            );
        }
    }

    public List<GameBoard> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT b FROM GameBoard b ORDER BY b.id",
                    GameBoard.class
            ).getResultList();
        }
    }

    public GameBoard update(GameBoard board) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(
                    em,
                    () -> em.merge(board)
            );
        }
    }

    public void remove(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(em, () -> {
                GameBoard board = em.find(GameBoard.class, id);

                if (board != null) {
                    em.remove(board);
                }
            });
        }
    }

    public List<GameBoard> findByGameNameId(Long gameNameId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT b
                    FROM GameBoard b
                    WHERE b.gameName.id = :gameNameId
                    ORDER BY b.id
                    """, GameBoard.class)
                    .setParameter("gameNameId", gameNameId)
                    .getResultList();
        }
    }
}