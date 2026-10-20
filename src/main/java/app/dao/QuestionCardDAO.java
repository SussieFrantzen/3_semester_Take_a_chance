package app.dao;

import app.config.HibernateConfig;
import app.entities.QuestionCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class QuestionCardDAO extends JPADAO<QuestionCard, Long> {

    public QuestionCardDAO() {
        super(QuestionCard.class);
    }

    public List<QuestionCard> findBySubjectAndAgeGroup(
            String subject,
            String ageGroup) {

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

    public Optional<QuestionCard> findByFingerprint(
            String fingerprint) {

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






/*


public class QuestionCardDAO {

    private final EntityManagerFactory emf;

    public QuestionCardDAO() {
        this.emf = HibernateConfig.getEntityManagerFactory();
    }

    public List<QuestionCard> findBySubjectAndAgeGroup(
            String subject,
            String ageGroup) {

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

    public Optional<QuestionCard> findByFingerprint(
            String fingerprint) {

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



 */

    public boolean saveIfNotExists(QuestionCard card) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Long count = em.createQuery("""
                    SELECT COUNT(q)
                    FROM QuestionCard q
                    WHERE q.fingerprint = :fingerprint
                    """, Long.class)
                    .setParameter("fingerprint", card.getFingerprint())
                    .getSingleResult();

            if (count > 0) {
                em.getTransaction().commit();
                return false;
            }

            em.persist(card);
            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

}