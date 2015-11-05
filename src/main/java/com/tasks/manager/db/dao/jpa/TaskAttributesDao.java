package com.tasks.manager.db.dao.jpa;


import com.google.inject.Inject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.entities.TaskGroup;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskAttributesDao extends BaseDaoJPA<TaskAttributes> {
    @Inject
    public TaskAttributesDao(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = TaskAttributes.class;
    }

    //TODO : Not sure whether this will be needed.
    public List<TaskAttributes> findTaskAttributes( String key, String value)
    {
        Criterion keyCriterion = Restrictions.eq("key", key);
        Criterion valueCriterion = Restrictions.eq("value", value);
        Criterion searchCriterion = Restrictions.and(keyCriterion, valueCriterion);
        return findByCriteria(searchCriterion);
    }
}
