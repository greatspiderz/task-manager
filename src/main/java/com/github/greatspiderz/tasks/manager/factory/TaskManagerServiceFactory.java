package com.github.greatspiderz.tasks.manager.factory;

import com.github.greatspiderz.tasks.manager.service.api.EventPublisher;
import com.github.greatspiderz.tasks.manager.service.impl.TaskManagerServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;

import javax.persistence.EntityManager;

/**
 * Created by divya.rai on 24/02/16.
 */
public class TaskManagerServiceFactory {

    private static Injector injector;

    public static TaskManagerServiceImpl getTaskManagerServiceForTest() {
        if (injector == null) {
            injector = getTestInjector();
        }
        return injector.getInstance(TaskManagerServiceImpl.class);
    }

    public static TaskManagerServiceImpl getTaskManagerService(EntityManager entityManager) {
        return Guice.createInjector(new BindingClassForServiceFactory(entityManager)).getInstance(TaskManagerServiceImpl.class);
    }

    public static TaskManagerServiceImpl getTaskManagerService(EntityManager entityManager, EventPublisher eventPublisher) {
        return Guice.createInjector(new BindingClassForServiceFactory(entityManager, eventPublisher)).getInstance(TaskManagerServiceImpl.class);
    }

    public static Injector getTestInjector() {
        return injector = Guice.createInjector(new BindingClassForServiceFactory(), new JpaPersistModule("test"));
    }

}
