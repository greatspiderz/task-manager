package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.model.entities.TaskGroup;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskGroupDao extends BaseDaoJPA<TaskGroup>  {

    @Inject
    public TaskGroupDao(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = TaskGroup.class;
    }

}
