package com.tasks.manager.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tasks.manager.BindingClassForTests;
import com.tasks.manager.db.dao.jpa.BaseDaoImpl;
import com.tasks.manager.db.exception.IllegalTaskStateTransitionException;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.enums.TaskTriggerEnum;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.*;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
public class TaskManagerServiceImplTest {
    TaskManagerServiceImpl taskManagerService;
    DateTime defaultDateTime;
    TaskStatus defaultTaskStatus;
    String defaultTaskType;
    String defaultAttributeName;
    String defaultAttributeValue;
    PersistService persistService;
    Injector injector;
    BaseDaoImpl transaction;

    @Before
    public void setUp(){

        injector = Guice.createInjector(new BindingClassForTests(), new JpaPersistModule("test"));
        persistService = injector.getInstance(PersistService.class);
        persistService.start();
        transaction = injector.getInstance(BaseDaoImpl.class);
        transaction.getEntityManager().getTransaction().begin();
        emptyDatabases();
        taskManagerService = injector.getInstance(TaskManagerServiceImpl.class);
        defaultDateTime = DateTime.parse("2015-10-09");
        defaultTaskStatus = TaskStatus.NEW;
        defaultTaskType = "PICK";
        defaultAttributeName = "test_attribute";
        defaultAttributeValue = "test_value";
    }

    @Test
    public void testCreateAndFetchTaskGroup() {
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName, defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        assertNotNull(fetchedTaskGroup);
        assertEquals(createdTaskGroupId, (long) fetchedTaskGroup.getId());
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        assertEquals(1, taskList.size());
        Task task = taskList.get(0);

        assertEquals(task.getStatus(), defaultTaskStatus);
        assertEquals(task.getType(), defaultTaskType);
        List<TaskAttributes> taskAttributeList = task.getTaskAttributes();
        assertEquals(1, taskAttributeList.size());
        TaskAttributes taskAttribute = taskAttributeList.get(0);
        assertEquals(taskAttribute.getAttributeName(), defaultAttributeName);
        assertEquals(taskAttribute.getAttributeValue(), defaultAttributeValue);

        taskManagerService.updateTaskAttribute(task, defaultAttributeName, "newAttr1Value");
        taskManagerService.updateTaskAttribute(task, "newAttr2Name", "newAttr1Value");
        Assert.assertEquals(2, taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId()).get(0).getTaskAttributes().size());
        TaskAttributes taskAttribute1 = taskAttributeList.get(0);
        TaskAttributes taskAttribute2 = taskAttributeList.get(1);
        assertEquals(taskAttribute1.getAttributeValue(), "newAttr1Value");
        assertEquals(taskAttribute2.getAttributeValue(), "newAttr1Value");
    }

    @Test
    public void testUpdateActor(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        String actorType = "Hero";
        Actor actor = new Actor();
        actor.setType(actorType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskList.get(0);
        Assert.assertNull(task.getActor());
        try {
            taskManagerService.updateTaskActor(task.getId(), actor);
        } catch (TaskNotFoundException e) {
            fail("Exception thrown on updating actor");
        }

        Task updatedTask = taskManagerService.fetchTask(task.getId());
        System.out.println(actor.getId());
        System.out.println(updatedTask.getActor().getId());
        assertEquals(actor.getId(), updatedTask.getActor().getId());
        assertEquals(actorType, updatedTask.getActor().getType());
    }


    @Test
    public void testUpdateTaskStatus(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskTriggerEnum triggerEnum = TaskTriggerEnum.IN_PROGRESS;
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskManagerService.fetchTask(taskList.get(0).getId());
        try{
            taskManagerService.updateStatus(task.getId(), triggerEnum);
        }
        catch(TaskNotFoundException | IllegalTaskStateTransitionException e){
            fail("Exception thrown on updating actor");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void testUpdateETA(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        long eta = 125;
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskManagerService.fetchTask(taskList.get(0).getId());
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
    public void testBulkInsert(){
        TaskGroup t = new TaskGroup();
        Task t1 = new Task();
        t1.setType("PICK");
        Task t2 = new Task();
        t2.setType("PICK");
        Task t3 = new Task();
        t3.setType("PICK");
        List<Task> listTasks = new ArrayList<>();
        listTasks.add(t1);
        listTasks.add(t2);
        listTasks.add(t3);
        TaskAttributes ta1 = new TaskAttributes();
        ta1.setAttributeName("testname");
        ta1.setAttributeValue("testvalue");
        ta1.setTask(t1);
        t1.setTaskAttributes(new ArrayList<>(Arrays.asList(ta1)));
        TaskAttributes ta2 = new TaskAttributes();
        ta2.setAttributeName("testname");
        ta2.setAttributeValue("testvalue");
        ta2.setTask(t2);
        t2.setTaskAttributes(new ArrayList<>(Arrays.asList(ta2)));
        TaskAttributes ta3 = new TaskAttributes();
        ta3.setAttributeName("testname");
        ta3.setAttributeValue("testvalue");
        ta3.setTask(t3);
        t3.setTaskAttributes(new ArrayList<>(Arrays.asList(ta3)));
        taskManagerService.bulkInsertTasks(listTasks);
        SearchDto searchdto = new SearchDto();
        searchdto.setType("PICK");
        List<Task> tasks = taskManagerService.findTasks(searchdto);
        assertEquals(3, (long) tasks.size());
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
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskManagerService.fetchTask(taskList.get(0).getId());
        SearchDto searchDto = new SearchDto();
        searchDto.setType(task.getType());
        searchDto.setStatus(task.getStatus());
        List<Task> searchedTasks = taskManagerService.findTasks(searchDto);
        assertEquals(1, searchedTasks.size());
        Task searchedTask = searchedTasks.get(0);
        assertEquals(task.getStatus(), searchedTask.getStatus());
        assertEquals(task.getType(), searchedTask.getType());
    }

    @Test
    public void testGetTaskForActor(){
        Long taskgrp1 =createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        Long taskgrp2 = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                TaskStatus.IN_PROGRESS, defaultTaskType);
        Long taskgrp3 = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup taskGrp1 = taskManagerService.fetchTaskGroup(taskgrp1);
        TaskGroup taskGrp2 = taskManagerService.fetchTaskGroup(taskgrp2);
        Actor actor = new Actor();
        actor.setStatus("ASSIGNED");
        actor.setType("HERO");
        actor.setExternalId("A1233333");
        Actor createdActor = taskManagerService.createActor(actor);
        try {
            taskManagerService.updateTaskActor(taskManagerService.getTasksForTaskGroup(taskGrp1.getId()).get(0).getId(), createdActor);
            taskManagerService.updateTaskActor(taskManagerService.getTasksForTaskGroup(taskGrp2.getId()).get(0).getId(), createdActor);
        }
        catch(TaskNotFoundException e)
        {
            fail("Exception occured while updating task actor");
        }
        List<Task> activeTasks = taskManagerService.getActiveTasksForActor(createdActor.getExternalId());
        assertEquals(1, activeTasks.size());
    }

    @Test
    public void testGetTaskForActorByExternalId(){
        Long taskgrp1 =createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                TaskStatus.IN_PROGRESS, defaultTaskType);
        Long taskgrp2 = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                TaskStatus.IN_PROGRESS, defaultTaskType);
        Long taskgrp3 = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup taskGrp1 = taskManagerService.fetchTaskGroup(taskgrp1);
        TaskGroup taskGrp2 = taskManagerService.fetchTaskGroup(taskgrp2);
        Actor actor = new Actor();
        actor.setStatus("ASSIGNED");
        actor.setType("HERO");
        actor.setExternalId("FQWE123");
        Actor createdActor = taskManagerService.createActor(actor);
        try {
            taskManagerService.updateTaskActor(taskManagerService.getTasksForTaskGroup(taskGrp1.getId()).get(0).getId(), createdActor);
            taskManagerService.updateTaskActor(taskManagerService.getTasksForTaskGroup(taskGrp2.getId()).get(0).getId(), createdActor);
        }
        catch(TaskNotFoundException e)
        {
            fail("Exception occured while updating task actor");
        }
        List<Task> activeTasks = taskManagerService.getActiveTasksForActor(createdActor.getExternalId());
        assertEquals(2, activeTasks.size());
    }

    @Test
    public void testGetTaskForActorByExternalIdFailed(){
        List<Task> activeTasks = taskManagerService.getActiveTasksForActor("AC123");
        assertEquals(0, activeTasks.size());
    }


    @Test
    public void testfindActiveTaskgroupsWithAttribute(){
        Long taskgrp1 =createTestTaskGroupWithTask("shipmentID","S1232",
                TaskStatus.NEW, defaultTaskType);
        List<TaskGroup> taskgrps = taskManagerService.findActiveTaskGroupsWithAttribute("shipmentID", "S1232");
        assertEquals(1, taskgrps.size());
    }
    @Test
    public void testCreateTask(){
        TaskGroup taskGroup = new TaskGroup();
        taskGroup = taskManagerService.createTaskGroup(taskGroup);
        String subjectType = "Shipment";
        Subject subject = new Subject();
        subject.setType(subjectType);
        Task task = new Task();
        task.setType("HAND_SHAKE");
        task.setSubject(subject);
        taskManagerService.createTask(task, taskGroup.getId());
        TaskGroup updatedTaskGroup  = taskManagerService.fetchTaskGroup(taskGroup.getId());
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(updatedTaskGroup.getId());
        assertEquals(1, taskList.size());
        assertEquals(taskList.get(0).getType(),"HAND_SHAKE");
    }

    @Test
    public void testSearchTasksForActor(){
        TaskGroup taskGroup = new TaskGroup();
        taskGroup = taskManagerService.createTaskGroup(taskGroup);
        String subjectType = "Shipment";
        Subject subject = new Subject();
        subject.setType(subjectType);
        Task task = new Task();
        task.setType("HAND_SHAKE");
        task.setSubject(subject);
        Actor actor = new Actor();
        actor.setExternalId("hero123");
        actor.setType("HERO");
        task.setActor(actor);
        TaskAttributes ta = new TaskAttributes();
        ta.setAttributeName("testname");
        ta.setAttributeValue("testvalue");
        ta.setTask(task);
        task.setTaskAttributes(new ArrayList<>(Arrays.asList(ta)));
        taskManagerService.createTask(task, taskGroup.getId());
        TaskGroup updatedTaskGroup  = taskManagerService.fetchTaskGroup(taskGroup.getId());

        SearchDto searchDto = new SearchDto();
        searchDto.setActors(Arrays.asList(actor));
        List<Task> tasks = taskManagerService.findTasks(searchDto);
        assertEquals(1, tasks.size());
        assertEquals(tasks.get(0).getType(),"HAND_SHAKE");
    }

    @Test
    public void testUpdateActorStatus(){
        Actor actor = new Actor();
        actor.setStatus("IDLE");
        actor.setType("HERO");
        actor.setExternalId("H1123");
        Actor createdActor = taskManagerService.createActor(actor);
        assertEquals("IDLE", createdActor.getStatus());
        try{
            taskManagerService.updateActorStatus(createdActor.getId(), "OFFLINE");}
        catch(Exception e)
        {
            fail("Exception thrown on updating actor");
        }
        Actor updatedActor = taskManagerService.fetchActorByExternalId("H1123");
        assertEquals("OFFLINE", createdActor.getStatus());
    }
    @Test
    public void testGetTaskGroupForTask(){
        Long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup createdTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        Task task = taskManagerService.getTasksForTaskGroup(createdTaskGroupId).get(0);
        TaskGroup taskGroup = taskManagerService.getTaskGroupForTask(task);
        assertEquals(taskGroup.getId(), createdTaskGroupId);
    }


    @Test
    public void testFetchTaskGraphStraightFlow()
    {
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup taskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(taskGroup.getId());

        Task parentTask = taskList.get(0);

        Task handShakeTask = new Task();
        handShakeTask.setType("HAND_SHAKE");
        handShakeTask.setStatus(TaskStatus.NEW);
        List<Long> parentIds = new ArrayList<>();
        parentIds.add(parentTask.getId());
        taskManagerService.createTaskWithParentTasks(handShakeTask, createdTaskGroupId, parentIds);



        Task travelTask1 = new Task();
        travelTask1.setType("TRAVEL");
        travelTask1.setStatus(TaskStatus.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTaskWithParentTasks(travelTask1, createdTaskGroupId, parentIds);


        Task travelTask2 = new Task();
        travelTask2.setType("TRAVEL");
        travelTask2.setStatus(TaskStatus.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTaskWithParentTasks(travelTask2, createdTaskGroupId, parentIds);


        Task deliver = new Task();
        deliver.setType("DELIVER");
        deliver.setStatus(TaskStatus.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(travelTask1.getId());
        parentIds.add(travelTask2.getId());
        taskManagerService.createTaskWithParentTasks(deliver, createdTaskGroupId, parentIds);

        Set<TaskGraphEdge> deliverIncomingEdges = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId).incomingEdgesOf(deliver);
        Set<TaskGraphEdge> handShakeOutgoingEdges = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId).outgoingEdgesOf(handShakeTask);
        Set<TaskGraphEdge> parentOutgoingEdges = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId).outgoingEdgesOf(parentTask);
        Set<TaskGraphEdge> travel1OutgoingEdges = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId).outgoingEdgesOf(travelTask1);
        Set<TaskGraphEdge> travel2OutgoingEdges = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId).outgoingEdgesOf(travelTask2);

        Assert.assertEquals(2, deliverIncomingEdges.size());
        Assert.assertEquals(2, handShakeOutgoingEdges.size());
        Assert.assertEquals(1, parentOutgoingEdges.size());
        Assert.assertEquals(1, travel1OutgoingEdges.size());
        Assert.assertEquals(1, travel2OutgoingEdges.size());

        for(TaskGraphEdge edge: parentOutgoingEdges) {
            Assert.assertEquals(handShakeTask.getType(), edge.getTarget().getType());
            Assert.assertEquals(handShakeTask.getId(), edge.getTarget().getId());
        }

        for(TaskGraphEdge edge: travel2OutgoingEdges) {
            Assert.assertEquals(deliver.getType(), edge.getTarget().getType());
            Assert.assertEquals(deliver.getId(), edge.getTarget().getId());
        }

        for(TaskGraphEdge edge: travel1OutgoingEdges) {
            Assert.assertEquals(deliver.getType(), edge.getTarget().getType());
            Assert.assertEquals(deliver.getId(), edge.getTarget().getId());
        }


    }

    @Transactional(rollbackOn = Exception.class)
    private long createTestTaskGroupWithTask(String attributeName, String attributeValue, TaskStatus status, String type)
    {
        TaskAttributes ta = new TaskAttributes();
        ta.setAttributeName(attributeName);
        ta.setAttributeValue(attributeValue);
        String subjectType = "Shipment";
        Subject subject = new Subject();
        subject.setType(subjectType);
        Task task = new Task();
        task.setStatus(status);
        task.setType(type);
        task.setStartTime(defaultDateTime);
        task.setEndTime(defaultDateTime);
        task.setTaskAttributes(new ArrayList<>(Arrays.asList(ta)));
        task.setSubject(subject);
        ta.setTask(task);
        TaskGroup taskGrp = new TaskGroup();
        TaskGroup tskGrpCreated = taskManagerService.createTaskGroup(taskGrp);
        taskManagerService.createTask(task,tskGrpCreated.getId());
        return tskGrpCreated.getId();
    }

    @After
    public void tearDown(){
        transaction.getEntityManager().getTransaction().commit();
    }

    private void emptyDatabases(){
        BaseDaoImpl baseDaoImpl = injector.getInstance(BaseDaoImpl.class);
        baseDaoImpl.executeQuery("Delete from relation");
        baseDaoImpl.executeQuery("Delete from task_attributes");
        baseDaoImpl.executeQuery("Delete from task_AUD");
        baseDaoImpl.executeQuery("Delete from task");
        baseDaoImpl.executeQuery("Delete from task_group");
        baseDaoImpl.executeQuery("Delete from subject");
        baseDaoImpl.executeQuery("Delete from actor");
        baseDaoImpl.executeQuery("Delete from inbound_messages");
        baseDaoImpl.executeQuery("Delete from outbound_messages");
    }

}

