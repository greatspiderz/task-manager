package com.tasks.manager.db.dao.interfaces;

import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public interface BaseDao<T> {

    void save(T entity);

    T fetchById(long id);

    Class<T> getEntityClass();

    EntityManager getEntityManager();

}
