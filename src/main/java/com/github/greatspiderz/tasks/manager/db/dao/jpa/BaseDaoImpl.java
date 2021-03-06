package com.github.greatspiderz.tasks.manager.db.dao.jpa;

import com.github.greatspiderz.tasks.manager.db.dao.interfaces.BaseDao;
import com.github.greatspiderz.tasks.manager.db.model.entities.BaseEntity;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
@Slf4j
@NoArgsConstructor
public class BaseDaoImpl<T> implements BaseDao<T> {

    protected Class<T> entityClass;
    private EntityManager entityManager;

    public BaseDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
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
    public <T extends BaseEntity> List<T> bulkInsert(List<T> entities) {
        final List<T> savedEntities = new ArrayList<>(entities.size());
        EntityManager entityManager = getEntityManager();
        int batchCount = 0;
        for (T entity : entities) {
            savedEntities.add(persistOrMerge(entity, entityManager));
            batchCount++;
            if (batchCount % 10 == 0) {
                // Flush a batch of inserts and release memory.
                entityManager.flush();
            }
        }
        entityManager.flush();
        return savedEntities;
    }

    private <T extends BaseEntity> T persistOrMerge(T entity, EntityManager entityManager) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
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
            log.info(type.toString() + "---- Type -----" + ParameterizedType.class);
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
        return this.entityManager;
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
