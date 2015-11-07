package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.BaseDaoImpl;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
@ImplementedBy(BaseDaoImpl.class)
public interface BaseDao<T> {

    void save(T entity);

    T fetchById(long id);

    Class<T> getEntityClass();

    EntityManager getEntityManager();
    List<T> findByQuery(final String queryStr);
    List<T> findByQueryAndNamedParams(final Integer firstResult, final Integer maxResults,
                                      @NotNull final String queryStr, @NotNull final Map<String, ?> params);
}
