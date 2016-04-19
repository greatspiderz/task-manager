package com.github.greatspiderz.tasks.manager.factory;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.ActorDaoImpl;
import com.github.greatspiderz.tasks.manager.db.dao.jpa.TaskDaoImpl;
import com.github.greatspiderz.tasks.manager.db.dao.jpa.TaskGroupDaoImpl;
import com.github.greatspiderz.tasks.manager.db.dao.jpa.TaskSubjectRelationDaoImpl;
import com.github.greatspiderz.tasks.manager.service.api.EventPublisher;
import com.github.greatspiderz.tasks.manager.service.api.TaskManagerService;
import com.github.greatspiderz.tasks.manager.service.impl.TaskManagerServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.github.greatspiderz.tasks.manager.db.dao.interfaces.ActorDao;
import com.github.greatspiderz.tasks.manager.db.dao.interfaces.RelationDao;
import com.github.greatspiderz.tasks.manager.db.dao.interfaces.SubjectDao;
import com.github.greatspiderz.tasks.manager.db.dao.interfaces.TaskDao;
import com.github.greatspiderz.tasks.manager.db.dao.interfaces.TaskGroupDao;
import com.github.greatspiderz.tasks.manager.db.dao.interfaces.TaskSubjectRelationDao;
import com.github.greatspiderz.tasks.manager.db.dao.jpa.RelationDaoImpl;
import com.github.greatspiderz.tasks.manager.db.dao.jpa.SubjectDaoImpl;

import javax.persistence.EntityManager;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class BindingClassForServiceFactory extends AbstractModule {

    private EntityManager entityManager;
    private EventPublisher eventPublisher;

    public BindingClassForServiceFactory(EntityManager entityManager, EventPublisher eventPublisher) {
        this.entityManager = entityManager;
        this.eventPublisher = eventPublisher;
    }

    public BindingClassForServiceFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void configure() {
        bind(TaskManagerService.class).to(TaskManagerServiceImpl.class).in(Singleton.class);
        bind(TaskDao.class).to(TaskDaoImpl.class);
        bind(TaskGroupDao.class).to(TaskGroupDaoImpl.class);
        bind(SubjectDao.class).to(SubjectDaoImpl.class);
        bind(ActorDao.class).to(ActorDaoImpl.class);
        bind(RelationDao.class).to(RelationDaoImpl.class);
        bind(TaskSubjectRelationDao.class).to(TaskSubjectRelationDaoImpl.class);
        if (eventPublisher != null) {
            bind(EventPublisher.class).toInstance(eventPublisher);
        }
        else {
            bind(EventPublisher.class).to(MockEventPublisher.class);
        }
        if (entityManager != null) {
            bind(EntityManager.class).toInstance(entityManager);
        }
    }

}
