package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.RelationDao;
import com.tasks.manager.db.model.entities.Relation;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by shlok.chaurasia on 13/11/15.
 */
public class RelationDaoImpl extends BaseDaoImpl<Relation> implements RelationDao {
    @Inject
    public RelationDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Relation.class;
    }

    public List<Relation> fetchByTaskGroupId(long taskGroupId){
        Criterion listIdsCriterion = Restrictions.eq("taskGroupId", taskGroupId);
        return findByCriteria(listIdsCriterion);
    }

    public List<Relation> fetchByTaskId(long taskId){
        Criterion listIdsCriterion = Restrictions.eq("taskId", taskId);
        return findByCriteria(listIdsCriterion);
    }

    public List<Relation> fetchByParentTaskId(Long taskId){
        Criterion listIdsCriterion = Restrictions.eq("parentTaskId", taskId);
        return findByCriteria(listIdsCriterion);
    }
}
