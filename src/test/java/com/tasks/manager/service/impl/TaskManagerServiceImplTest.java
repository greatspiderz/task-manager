package com.tasks.manager.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tasks.manager.BindingClassForTests;
import com.tasks.manager.db.dao.jpa.BaseDaoImpl;
import com.tasks.manager.db.dao.jpa.TaskDaoImpl;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@Transactional
public class TaskManagerServiceImplTest {
    TaskManagerServiceImpl taskManagerService;
    DateTime defaultDateTime;
    String defaultTaskStatus;
    String defaultTaskType;
    String defaultAttributeName;
    String defaultAttributeValue;
    PersistService persistService;
    Injector injector;
    @Before
    public void setUp(){
        injector = Guice.createInjector(new BindingClassForTests(), new JpaPersistModule("test"));

        persistService = injector.getInstance(PersistService.class);
        persistService.start();
        emptyDatabases();
        taskManagerService = injector.getInstance(TaskManagerServiceImpl.class);
        defaultDateTime = DateTime.parse("2015-10-09");
        defaultTaskStatus = TaskStatus.NEW.name();
        defaultTaskType = "PICK";
        defaultAttributeName = "test_attribute";
        defaultAttributeValue = "test_value";
    }

    @Test
    public void testCreateAndFetchTaskGroup() {
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        assertNotNull(fetchedTaskGroup);
        assertEquals(createdTaskGroupId, (long)fetchedTaskGroup.getId());
        List<Task> taskList =  fetchedTaskGroup.getTasks();
        assertEquals(1, taskList.size());
        Task task = taskList.get(0);
        assertEquals(task.getStatus(), defaultTaskStatus);
        assertEquals(task.getType(), defaultTaskType);
        List<TaskAttributes> taskAttributeList = task.getTaskAttributes();
        assertEquals(1, taskAttributeList.size());
        TaskAttributes taskAttribute = taskAttributeList.get(0);
        assertEquals(taskAttribute.getAttribute_name(), defaultAttributeName);
        assertEquals(taskAttribute.getAttribute_value(), defaultAttributeValue);
    }

    @Test
    public void testUpdateActor(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        long actorId = 1;
        String actorType = "Hero";
        Actor actor = new Actor();
        actor.setId(actorId);
        actor.setType(actorType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.fetchTask(fetchedTaskGroup.getTasks().get(0).getId());
        try{
            taskManagerService.updateActor(task.getId(), actor);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating actor");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(actorId, updatedTask.getActor().getId());
        assertEquals(actorType, updatedTask.getActor().getType());
    }

    @Test
    public void testUpdateSubject(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        long subjectId = 1;
        String subjectType = "Store";
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setType(subjectType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.fetchTask(fetchedTaskGroup.getTasks().get(0).getId());
        try{
            taskManagerService.updateSubject(task.getId(), subject);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating subject");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(subjectId, updatedTask.getSubject().getId());
        assertEquals(subjectType, updatedTask.getSubject().getType());
    }

    @Test
    public void testUpdateTaskStatus(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.fetchTask(fetchedTaskGroup.getTasks().get(0).getId());
        try{
            taskManagerService.updateStatus(task.getId(), newStatus);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating actor");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(newStatus, updatedTask.getStatus());
    }

    @Test
    public void testUpdateETA(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        long eta = 125;
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.fetchTask(fetchedTaskGroup.getTasks().get(0).getId());
        try{
            taskManagerService.updateETA(task.getId(), eta);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating task");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(eta, (long)updatedTask.getEta());
    }

    @Test
    public void testFailedUpdate(){
        long randomTaskGroupId = 12;
        long eta = 125;
        try{
            taskManagerService.updateETA(randomTaskGroupId, eta);
            fail("Expected Exception to be thrown.But no exception thrown");
        }
        catch(TaskNotFoundException e){
        }
    }

    @Test
    public void testSearchTasks(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, "TRAVEL");

        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.fetchTask(fetchedTaskGroup.getTasks().get(0).getId());
        SearchDto searchDto = new SearchDto();
        searchDto.setType(task.getType());
        searchDto.setStatus(task.getStatus());
        List<Task> searchedTasks = taskManagerService.findTasks(searchDto);
        assertEquals(1, searchedTasks.size());
        Task searchedTask = searchedTasks.get(0);
        assertEquals(task.getStatus(), searchedTask.getStatus());
        assertEquals(task.getType(), searchedTask.getType());
    }

    private long createTestTaskGroupWithTask(String attributeName, String attributeValue, String status, String type)
    {
        TaskAttributes ta = new TaskAttributes();
        ta.setAttribute_name(attributeName);
        ta.setAttribute_value(attributeValue);
        Task task = new Task();
        task.setStatus(status);
        task.setType(type);
        task.setStartTime(defaultDateTime);
        task.setEndTime(defaultDateTime);
        task.setTaskAttributes(new ArrayList<>(Arrays.asList(ta)));
        ta.setTask(task);
        TaskGroup taskGrp = new TaskGroup();
        task.setTaskGroup(taskGrp);
        taskGrp.setTasks(new ArrayList<>(Arrays.asList(task)));
        TaskGroup tskGrpCreated = taskManagerService.createTaskGroup(taskGrp);
        return tskGrpCreated.getId();
    }

    private void emptyDatabases(){
        BaseDaoImpl baseDaoImpl = injector.getInstance(BaseDaoImpl.class);
        baseDaoImpl.executeQuery("Delete from task");
        baseDaoImpl.executeQuery("Delete from task_group");
        baseDaoImpl.executeQuery("Delete from task_attributes");
    }

}

