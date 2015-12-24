package com.tasks.manager.db.dao.jpa;


import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.TaskAttributesDao;
import com.tasks.manager.db.model.entities.TaskAttributes;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskAttributesDaoImpl extends BaseDaoImpl<TaskAttributes> implements TaskAttributesDao{
    @Inject
    public TaskAttributesDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = TaskAttributes.class;
    }

    public List<TaskAttributes> findTaskAttributes(String attributeName, String attributeValue)
    {
        Criterion keyCriterion = Restrictions.eq("attributeName", attributeName);
        Criterion valueCriterion = Restrictions.eq("attributeValue", attributeValue);
        Criterion searchCriterion = Restrictions.and(keyCriterion, valueCriterion);
        return findByCriteria(searchCriterion);
    }


}
