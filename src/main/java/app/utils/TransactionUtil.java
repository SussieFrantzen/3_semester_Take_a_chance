package app.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.function.Supplier;

public class TransactionUtil {

    private TransactionUtil() {
    }

    public static void execute(EntityManager em, Runnable action) {
        executeWithResult(em, () -> {
            action.run();
            return null;
        });
    }

    public static <T> T executeWithResult(
            EntityManager em,
            Supplier<T> action
    ) {
        EntityTransaction transaction = em.getTransaction();

        if (transaction.isActive()) {
            throw new IllegalStateException(
                    "A transaction is already active."
            );
        }

        transaction.begin();

        try {
            T result = action.get();
            transaction.commit();
            return result;
        } catch (RuntimeException | Error e) {
            if (transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }
            throw e;
        }
    }
}

