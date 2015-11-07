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

    //TODO : Not sure whether this will be needed.
    public List<TaskAttributes> findTaskAttributes(HashMap<String, String> attibuteNameValue)
    {
        Criterion keyCriterion = Restrictions.in("attribute_name", attibuteNameValue.keySet());
        Criterion valueCriterion = Restrictions.in("attribute_value", attibuteNameValue.values());
        Criterion searchCriterion = Restrictions.and(keyCriterion, valueCriterion);
        return findByCriteria(searchCriterion);
    }


}
