package app.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}