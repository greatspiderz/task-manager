package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.tasks.manager.db.dao.interfaces.BaseDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class BaseDaoImpl<T> implements BaseDao<T> {

    private final Provider<EntityManager> entityManagerProvider;
    protected Class<T> entityClass;

    @Inject
    public BaseDaoImpl(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    @Transactional
    public void save(T entity) {
        EntityManager em = getEntityManager();
        if (em.contains(entity) ) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
        em.flush();
//        return entity;
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
    @Transactional
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

    protected List<T> findByCriteria(final Criterion... criterion) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria crit = session.createCriteria(getEntityClass());

        for (final Criterion c : criterion) {
            crit.add(c);
        }
        final List<T> result = crit.list();
        return result;
    }

    @Override
    @Transactional
    public int executeQuery(final String queryStr)
    {
        Query query = getEntityManager().createNativeQuery(queryStr);
        return query.executeUpdate();
    }

    @Override
    public List<T> findByQueryAndNamedParams(final Integer firstResult, final Integer maxResults,
                                             @NotNull final String queryStr, @NotNull final Map<String, ?> params) {
        Query query = getEntityManager().createQuery(queryStr);
        for (final Map.Entry<String, ? extends Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }

        if(firstResult != null) query.setFirstResult(firstResult);
        if(maxResults != null) query.setMaxResults(maxResults);
        final List<T> result = (List<T>) query.getResultList();
        return result;
    }

}
