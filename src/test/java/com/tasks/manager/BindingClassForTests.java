package com.tasks.manager;

import com.google.inject.AbstractModule;
import com.google.inject.persist.UnitOfWork;
import com.tasks.manager.db.dao.interfaces.*;
import com.tasks.manager.db.dao.jpa.*;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;

/**
 * Created by shlok.chaurasia on 07/11/15.
 */
public class BindingClassForTests extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManagerService.class).to(TaskManagerServiceImpl.class);
        bind(TaskDao.class).to(TaskDaoImpl.class);
        bind(TaskGroupDao.class).to(TaskGroupDaoImpl.class);
        bind(TaskAttributesDao.class).to(TaskAttributesDaoImpl.class);
        bind(SubjectDao.class).to(SubjectDaoImpl.class);
        bind(TaskHistoryDao.class).to(TaskHistoryDaoImpl.class);

    }

}
