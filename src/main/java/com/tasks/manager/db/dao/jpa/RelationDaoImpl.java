package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;

import com.tasks.manager.db.dao.interfaces.RelationDao;
import com.tasks.manager.db.model.entities.Relation;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 13/11/15.
 */
public class RelationDaoImpl extends BaseDaoImpl<Relation> implements RelationDao {

    @Inject
    public RelationDaoImpl(EntityManager entityManager) {
        super(entityManager);
        entityClass = Relation.class;
    }

    public List<Relation> fetchByTaskGroupId(long taskGroupId){
        Criterion listIdsCriterion = Restrictions.eq("taskGroupId", taskGroupId);
        return findByCriteria(listIdsCriterion);
    }

}
