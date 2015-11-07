package com.tasks.manager.db.dao.jpa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tasks.manager.BindingClassForTests;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
public class TaskDaoImplTest {
    TaskManagerServiceImpl serviceToTest;
    DateTime defaultDateTime;
    @Before
    public void setUp()
    {
        defaultDateTime = DateTime.parse("2015-10-09");
        Injector injector = Guice.createInjector(new BindingClassForTests(), new JpaPersistModule("test"));
        PersistService persistService = injector.getInstance(PersistService.class);
        persistService.start();
        serviceToTest = injector.getInstance(TaskManagerServiceImpl.class);
    }

    @Test
    public void testCreateTask() {
        TaskAttributes ta = new TaskAttributes();
        ta.setAttribute_name("attribute_name");
        ta.setAttribute_value("attribute_value");
        Task task = new Task();
        task.setStatus(TaskStatus.NEW.name());
        task.setType("PICK");
        task.setStartTime(defaultDateTime);
        task.setEndTime(defaultDateTime);
        task.setTaskAttributes(new ArrayList<>(Arrays.asList(ta)));
        ta.setTask(task);
        TaskGroup taskGrp = new TaskGroup();
        task.setTaskGroup(taskGrp);
        taskGrp.setTasks(new ArrayList<>(Arrays.asList(task)));
        TaskGroup tskGrpCreated = serviceToTest.createTaskGroup(taskGrp);


        System.out.println(tskGrpCreated.getId()+"ID Created");
        System.out.println(serviceToTest.fetchTaskGroup(tskGrpCreated.getId()).getCreatedAt()+ "-Status");
        System.out.println(serviceToTest.fetchTaskGroup(tskGrpCreated.getId()).getTasks());
//        System.out.println(serviceToTest.fetchTaskGroup(tskGrpCreated.getId()).getTasks().get(0).getType()+ "-Type");
    }
}

