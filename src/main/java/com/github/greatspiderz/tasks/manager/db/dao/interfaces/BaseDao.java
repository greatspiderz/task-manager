package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.BaseDaoImpl;
import com.github.greatspiderz.tasks.manager.db.model.entities.BaseEntity;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
@ImplementedBy(BaseDaoImpl.class)
public interface BaseDao<T> {

    void save(T entity);

    T fetchById(long id);

    Class<T> getEntityClass();

    <T extends BaseEntity> List<T> bulkInsert(List<T> entities);

    EntityManager getEntityManager();

    List<T> findByQueryAndNamedParams(final Integer firstResult, final Integer maxResults,
                                      @NotNull final String queryStr, @NotNull final Map<String, ?> params);

}
