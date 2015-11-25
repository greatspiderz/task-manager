package com.tasks.manager.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tasks.manager.BindingClassForTests;
import com.tasks.manager.db.dao.jpa.BaseDaoImpl;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
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
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        assertNotNull(fetchedTaskGroup);
        assertEquals(createdTaskGroupId, (long) fetchedTaskGroup.getId());
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        assertEquals(1, taskList.size());
        Task task = taskList.get(0);
        assertEquals(task.getFromStatus(), null);
        assertEquals(task.getToStatus(), defaultTaskStatus);
        assertEquals(task.getType(), defaultTaskType);
        List<TaskAttributes> taskAttributeList = task.getTaskAttributes();
        assertEquals(1, taskAttributeList.size());
        TaskAttributes taskAttribute = taskAttributeList.get(0);
        assertEquals(taskAttribute.getAttributeName(), defaultAttributeName);
        assertEquals(taskAttribute.getAttributeValue(), defaultAttributeValue);

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
    public void testUpdateSubject(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        String subjectType = "Shipment";
        Subject subject = new Subject();
        subject.setType(subjectType);
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskManagerService.fetchTask(taskList.get(0).getId());
        try{
            taskManagerService.updateSubject(task.getId(), subject);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating subject");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertNotNull(updatedTask.getSubject());
        assertNotNull("Shipment", updatedTask.getSubject().getType());
        assertEquals(1, updatedTask.getSubject().getAssociatedTasks().size());
        assertEquals(subjectType, updatedTask.getSubject().getType());
    }

    @Test
    public void testUpdateTaskStatus(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;
        TaskGroup fetchedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(fetchedTaskGroup.getId());
        Task task = taskManagerService.fetchTask(taskList.get(0).getId());
        try{
            taskManagerService.updateStatus(task.getId(), newStatus);
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating actor");
        }
        Task updatedTask = taskManagerService.fetchTask(task.getId());
        assertEquals(newStatus, updatedTask.getToStatus());
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
        taskManagerService.bulkInsert(listTasks);
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
        searchDto.setStatus(task.getToStatus());
        List<Task> searchedTasks = taskManagerService.findTasks(searchDto);
        assertEquals(1, searchedTasks.size());
        Task searchedTask = searchedTasks.get(0);
        assertEquals(task.getToStatus(), searchedTask.getToStatus());
        assertEquals(task.getType(), searchedTask.getType());
    }

    @Test
    public void testCreateTask(){
        TaskGroup taskGroup = new TaskGroup();
        taskGroup = taskManagerService.createTaskGroup(taskGroup);
        Task task = new Task();
        task.setType("HAND_SHAKE");
        taskManagerService.createTask(task, taskGroup.getId());
        TaskGroup updatedTaskGroup  = taskManagerService.fetchTaskGroup(taskGroup.getId());
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(updatedTaskGroup.getId());
        assertEquals(1, taskList.size());
        assertEquals(taskList.get(0).getType(),"HAND_SHAKE");
    }

    @Test
    public void testFetchParentTask(){
        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName,defaultAttributeValue,
                defaultTaskStatus, defaultTaskType);
        TaskGroup taskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> taskList = taskManagerService.getTasksForTaskGroup(taskGroup.getId());
        Task parentTask = taskList.get(0);
        Task task = new Task();
        task.setType("HAND_SHAKE");
        task.setToStatus(TaskStatus.CANCELLED);
        Relation relation = new Relation();
        relation.setParentTaskId(parentTask.getId());
        relation.setTask(task);
        relation.setTaskGroup(taskGroup);

        List<Relation> relations = new ArrayList<>();
        relations.add(relation);
        task.setRelations(relations);
        taskGroup.getRelations().add(relation);
        taskManagerService.createTask(task, createdTaskGroupId);
        TaskGroup updatedTaskGroup = taskManagerService.fetchTaskGroup(createdTaskGroupId);
        List<Task> updatedTaskList = taskManagerService.getTasksForTaskGroup(taskGroup.getId());
        Task travelTask = null;
        System.out.print(updatedTaskList.get(1).getType());
        for(Task travel : updatedTaskList)
        {
            if(travel.getType() == "HAND_SHAKE")
            {
                travelTask = travel;
                break;
            }
        }

        List<Task> createdParentTask = taskManagerService.fetchParentTasks(travelTask.getId());
        assertEquals(createdParentTask.size(), 1);
        assertEquals(createdParentTask.get(0).getType(), "PICK");
    }

    @Test
    public void testUpdateActorStatus(){
        Actor actor = new Actor();
        actor.setStatus("IDLE");
        actor.setType("HERO");
        Actor createdActor = taskManagerService.createActor(actor);
        assertEquals("IDLE", createdActor.getStatus());
        try{
            taskManagerService.updateActorStatus(createdActor.getId(), "OFFLINE");}
        catch(Exception e)
        {
            fail("Exception thrown on updating actor");
        }
        Actor updatedActor = taskManagerService.fetchActor(createdActor.getId());
        assertEquals("OFFLINE", createdActor.getStatus());
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
        handShakeTask.setToStatus(TaskStatus.NEW);
        List<Long> parentIds = new ArrayList<>();
        parentIds.add(parentTask.getId());
        taskManagerService.createTaskWithParentTasks(handShakeTask, createdTaskGroupId, parentIds);



        Task travelTask1 = new Task();
        travelTask1.setType("TRAVEL");
        travelTask1.setToStatus(TaskStatus.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTaskWithParentTasks(travelTask1, createdTaskGroupId, parentIds);


        Task travelTask2 = new Task();
        travelTask2.setType("TRAVEL");
        travelTask2.setToStatus(TaskStatus.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTaskWithParentTasks(travelTask2, createdTaskGroupId, parentIds);


        Task deliver = new Task();
        deliver.setType("DELIVER");
        deliver.setToStatus(TaskStatus.NEW);
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

    private long createTestTaskGroupWithTask(String attributeName, String attributeValue, TaskStatus status, String type)
    {
        TaskAttributes ta = new TaskAttributes();
        ta.setAttributeName(attributeName);
        ta.setAttributeValue(attributeValue);
        Task task = new Task();
        task.setToStatus(status);
        task.setType(type);
        task.setStartTime(defaultDateTime);
        task.setEndTime(defaultDateTime);
        task.setTaskAttributes(new ArrayList<>(Arrays.asList(ta)));
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
        baseDaoImpl.executeQuery("Delete from task");
        baseDaoImpl.executeQuery("Delete from task_group");
        baseDaoImpl.executeQuery("Delete from subject");
    }

}

