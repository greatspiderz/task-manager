package com.tasks.manager.db.dao.jpa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tasks.manager.BindingClassTestModule;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
//        Provider<EntityManager> entityManagerProvider = new com.google.inject.Provider<EntityManager>() {
//            @Override
//            public EntityManager get() {
//                EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");
//                return factory.createEntityManager();
//            }
//        };
//        TaskDao taskDao = new TaskDaoImpl(entityManagerProvider);;
//        TaskGroupDao taskGroupDao = new TaskGroupDaoImpl(entityManagerProvider);
//        TaskAttributesDao taskAttributesDao = new TaskAttributesDaoImpl(entityManagerProvider);
//        StateMachineProvider stateMachineProvider = new StateMachineProvider();
//        tmpl = new TaskManagerServiceImpl(taskDao,taskGroupDao,taskAttributesDao,stateMachineProvider);
//        TaskGroup t =tmpl.fetchTaskGroup(1);
//        System.out.print(t.getCreatedAt() + "- ttttt");

public class TaskDaoImplTest {
    TaskManagerServiceImpl serviceToTest;

    @Before
    public void setUp()
    {
        Injector injector = Guice.createInjector(new BindingClassTestModule(), new JpaPersistModule("test"));
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();
        serviceToTest = injector.getInstance(TaskManagerServiceImpl.class);
    }

    @Test
    public void testCreateTask() {
        System.out.println(serviceToTest.fetchTaskGroup(1).getCreatedAt()+ "jksbjhdsb");
    }
}

