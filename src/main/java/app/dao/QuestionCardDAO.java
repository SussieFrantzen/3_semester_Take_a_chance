package app.dao;

import app.config.HibernateConfig;
import app.entities.QuestionCard;
import app.utils.TransactionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class QuestionCardDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public QuestionCard create(QuestionCard card) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(
                    em,
                    () -> em.persist(card)
            );
            return card;
        }
    }

    public Optional<QuestionCard> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(
                    em.find(QuestionCard.class, id)
            );
        }
    }

    public List<QuestionCard> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT q FROM QuestionCard q ORDER BY q.id",
                    QuestionCard.class
            ).getResultList();
        }
    }

    public QuestionCard update(QuestionCard card) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(
                    em,
                    () -> em.merge(card)
            );
        }
    }

    public void remove(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            TransactionUtil.execute(em, () -> {
                QuestionCard card =
                        em.find(QuestionCard.class, id);

                if (card != null) {
                    em.remove(card);
                }
            });
        }
    }

    public List<QuestionCard> findByDeckAndAgeGroup(
            Long deckId,
            String ageGroup
    ) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT q
                    FROM QuestionCard q
                    WHERE q.cardDeck.id = :deckId
                      AND q.ageGroup = :ageGroup
                    ORDER BY q.id
                    """, QuestionCard.class)
                    .setParameter("deckId", deckId)
                    .setParameter("ageGroup", ageGroup)
                    .getResultList();
        }
    }

    public Optional<QuestionCard> findByFingerprint(
            String fingerprint
    ) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT q
                    FROM QuestionCard q
                    WHERE q.fingerprint = :fingerprint
                    """, QuestionCard.class)
                    .setParameter("fingerprint", fingerprint)
                    .getResultStream()
                    .findFirst();
        }
    }

    public List<QuestionCard> findBySubjectAndAgeGroup(
            String subject,
            String ageGroup
    ) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                    SELECT q
                    FROM QuestionCard q
                    WHERE q.subject = :subject
                      AND q.ageGroup = :ageGroup
                    ORDER BY q.id
                    """, QuestionCard.class)
                    .setParameter("subject", subject)
                    .setParameter("ageGroup", ageGroup)
                    .getResultList();
        }
    }

    public boolean saveIfNotExists(QuestionCard card) {
        try (EntityManager em = emf.createEntityManager()) {
            return TransactionUtil.executeWithResult(em, () -> {
                Long count = em.createQuery("""
                        SELECT COUNT(q)
                        FROM QuestionCard q
                        WHERE q.fingerprint = :fingerprint
                        """, Long.class)
                        .setParameter(
                                "fingerprint",
                                card.getFingerprint()
                        )
                        .getSingleResult();

                if (count > 0) {
                    return false;
                }

                em.persist(card);
                return true;
            });
        }
    }
}


