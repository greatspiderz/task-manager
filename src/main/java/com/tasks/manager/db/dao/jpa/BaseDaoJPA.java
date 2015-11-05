package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.tasks.manager.db.dao.interfaces.BaseDao;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class BaseDaoJPA<T> implements BaseDao<T> {

    private final Provider<EntityManager> entityManagerProvider;
    protected Class<T> entityClass;

    @Inject
    public BaseDaoJPA(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    @Transactional
    public T save(T entity) {
        EntityManager em = getEntityManager();
        if (em.contains(entity) ) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
        em.flush();
        return entity;
    }

    @Override
    @Transactional
    public T fetchById(final long id) {
        EntityManager em = getEntityManager();
        T entity = em.find(getEntityClass(), id);
        em.flush();
        return entity;
    }

    @Override
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                entityClass = (Class<T>) paramType.getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManagerProvider.get();
    }

}
