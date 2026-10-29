package app.dao;

import app.config.HibernateConfig;
import app.entities.CardDeck;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CardDeckDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public CardDeck create(CardDeck deck) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(
                    em,
                    () -> em.persist(deck)
            );
            return deck;
        }
    }

    public CardDeck findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(CardDeck.class, id);
        }
    }

    public List<CardDeck> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT d FROM CardDeck d",
                    CardDeck.class
            ).getResultList();
        }
    }

    public CardDeck update(CardDeck deck) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(
                    em,
                    () -> em.merge(deck)
            );
        }
    }

    public void remove(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(em, () -> {
                CardDeck deck = em.find(CardDeck.class, id);

                if (deck != null) {
                    em.remove(deck);
                }
            });
        }
    }
}