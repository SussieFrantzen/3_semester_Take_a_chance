package app.dao;

import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public abstract class JPADAO<T, ID>
        implements DAO<T, ID> {

    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    protected JPADAO(Class<T> entityClass) {
        this.emf = HibernateConfig.getEntityManagerFactory();
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            T saved = em.merge(entity);

            em.getTransaction().commit();

            return saved;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(
                    em.find(entityClass, id)
            );
        }
    }

    @Override
    public List<T> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT e FROM " + entityClass.getSimpleName()
                            + " e",
                    entityClass
            ).getResultList();
        }
    }

    @Override
    public void deleteById(ID id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            T entity = em.find(entityClass, id);

            if (entity != null) {
                em.remove(entity);
            }

            em.getTransaction().commit();
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