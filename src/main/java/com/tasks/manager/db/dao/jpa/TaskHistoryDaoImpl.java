package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.TaskGroupDao;
import com.tasks.manager.db.dao.interfaces.TaskHistoryDao;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.entities.TaskHistory;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 27/11/15.
 */
public class TaskHistoryDaoImpl extends BaseDaoImpl<TaskHistory> implements TaskHistoryDao {
    @Inject
    public TaskHistoryDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = TaskHistory.class;
    }
}
