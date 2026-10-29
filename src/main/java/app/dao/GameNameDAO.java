package app.dao;

import app.config.HibernateConfig;
import app.entities.GameName;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class GameNameDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public Optional<GameName> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(
                    em.find(GameName.class, id)
            );
        }
    }

    public List<GameName> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT g FROM GameName g ORDER BY g.id",
                    GameName.class
            ).getResultList();
        }
    }

    public List<GameName> findAllWithCardDeck() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT g
                    FROM GameName g
                    WHERE g.cardDeck IS NOT NULL
                    ORDER BY g.id
                    """, GameName.class)
                    .getResultList();
        }
    }
}