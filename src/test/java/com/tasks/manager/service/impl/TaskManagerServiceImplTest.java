package com.tasks.manager.service.impl;

import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

import com.tasks.manager.db.exception.IllegalTaskStateTransitionException;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.entities.TaskSubjectRelation;
import com.tasks.manager.dto.AddRelationDto;
import com.tasks.manager.dto.AddSubjectsDto;
import com.tasks.manager.dto.CreateTaskDto;
import com.tasks.manager.dto.SearchActorDto;
import com.tasks.manager.dto.SearchSubjectDto;
import com.tasks.manager.dto.SearchTaskDto;
import com.tasks.manager.dto.SearchTaskGroupDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.dto.UpdateRelationDto;
import com.tasks.manager.dto.UpdateSubjectDto;
import com.tasks.manager.dto.UpdateTaskDto;
import com.tasks.manager.enums.TaskStatusEnum;
import com.tasks.manager.enums.TaskTriggerEnum;
import com.tasks.manager.factory.TaskManagerServiceFactory;
import com.tasks.manager.service.api.TaskManagerService;

import org.jgrapht.DirectedGraph;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
public class TaskManagerServiceImplTest {

    TaskManagerService taskManagerService;
    DateTime defaultDateTime;
    TaskStatusEnum defaultTaskStatus;
    String defaultTaskType;
    String defaultAttributeName;
    String defaultAttributeValue;
    PersistService persistService;
    Injector injector;
    EntityManager entityManager;

    @Before
    public void setUp() {
        injector = TaskManagerServiceFactory.getTestInjector();
        persistService = injector.getInstance(PersistService.class);
        persistService.start();
        entityManager = injector.getInstance(EntityManager.class);
        entityManager.getTransaction().begin();
        emptyDatabases();
        taskManagerService = TaskManagerServiceFactory.getTaskManagerServiceForTest();
        defaultDateTime = DateTime.parse("2016-02-16"); // Date when the first commit of the revamped Library was made :)
        defaultTaskStatus = TaskStatusEnum.NEW;
        defaultTaskType = "PICK";
        defaultAttributeName = "test_attribute";
        defaultAttributeValue = "test_value";
    }

    @Test
    public void testCreateAndFetchTaskGroup() {

        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        TaskGroup fetchedTaskGroup = taskManagerService.getTaskGroup(new SearchTaskGroupDto(null, createdTaskGroupId));
        assertNotNull(fetchedTaskGroup);
        assertEquals(createdTaskGroupId, (long) fetchedTaskGroup.getId());

        List<Task> taskList = fetchedTaskGroup.getRelations().stream().map(relation -> relation.getTask()).collect(Collectors.toList());
        assertEquals(1, taskList.size());
        Task task = taskList.get(0);
        assertEquals(task.getStatus(), defaultTaskStatus);
        assertEquals(task.getType(), defaultTaskType);

        List<TaskAttributes> taskAttributeList = task.getTaskAttributes();
        assertEquals(1, taskAttributeList.size());
        TaskAttributes taskAttribute = taskAttributeList.get(0);
        assertEquals(taskAttribute.getAttributeName(), defaultAttributeName);
        assertEquals(taskAttribute.getAttributeValue(), defaultAttributeValue);
        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setTaskAttributes(
                new ArrayList<>(Arrays.asList(
                        new TaskAttributes(task, defaultAttributeName, "newAttr1Value"),
                        new TaskAttributes(task, "newAttr2Name", "newAttr1Value")
                ))
        );
        updateTaskDto.setTaskId(task.getId());
        try {
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto));
        } catch (TaskNotFoundException e) {
            fail("Exception thrown while adding new attributes to Task " + task.getId());
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }
        taskAttributeList = task.getTaskAttributes();
        assertEquals(2, taskAttributeList.size());
        TaskAttributes taskAttribute1 = taskAttributeList.get(0);
        TaskAttributes taskAttribute2 = taskAttributeList.get(1);
        assertEquals(taskAttribute1.getAttributeValue(), "newAttr1Value");
        assertEquals(taskAttribute2.getAttributeValue(), "newAttr1Value");

    }

    @Test
    public void testGetActor() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        String actorType = "HERO";
        String actorExternalId = "test_actor";
        Actor actor = new Actor();
        actor.setType(actorType);
        actor.setExternalId(actorExternalId);
        assertNull(task.getActor());
        try {
            UpdateTaskDto updateTaskDto = new UpdateTaskDto(task.getId());
            updateTaskDto.setActor(actor);
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto));
        } catch (TaskNotFoundException e) {
            fail("Exception thrown on updating actor");
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }
        SearchActorDto searchActorDto = new SearchActorDto();
        searchActorDto.setTypes(Arrays.asList(actorType));
        searchActorDto.setExternalIds(Arrays.asList(actorExternalId));
        List<Actor> fetchedActors = taskManagerService.getActors(searchActorDto);
        assertEquals(1, fetchedActors.size());
        Actor fetchedActor = fetchedActors.get(0);
        assertEquals(actor.getId(), fetchedActor.getId());
        assertEquals(actorType, fetchedActor.getType());
        assertEquals(actorExternalId, fetchedActor.getExternalId());
    }

    @Test
    public void testUpdateActor() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        String actorType = "HERO";
        Actor actor = new Actor();
        actor.setType(actorType);
        assertNull(task.getActor());
        try {
            UpdateTaskDto updateTaskDto = new UpdateTaskDto(task.getId());
            updateTaskDto.setActor(actor);
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto));
        } catch (TaskNotFoundException e) {
            fail("Exception thrown on updating actor");
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }
        assertEquals(actor.getId(), task.getActor().getId());
        assertEquals(actorType, task.getActor().getType());
    }


    @Test
    public void testUpdateTaskStatus() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        TaskTriggerEnum triggerEnum = TaskTriggerEnum.IN_PROGRESS;
        try{
            UpdateTaskDto updateTaskDto = new UpdateTaskDto(task.getId());
            updateTaskDto.setTaskTriggerEnum(triggerEnum);
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto));
        }
        catch(TaskNotFoundException e){
            fail("Exception thrown on updating Task Status");
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }
        assertEquals(TaskStatusEnum.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void testBulkInsert() {
        Task t1 = new Task();
        t1.setType("PICK");
        Task t2 = new Task();
        t2.setType("PICK");
        Task t3 = new Task();
        t3.setType("PICK");

        List<CreateTaskDto> createTaskDtos = new ArrayList<>(Arrays.asList(
                new CreateTaskDto(t1),
                new CreateTaskDto(t2),
                new CreateTaskDto(t3)
        ));
        taskManagerService.createTasks(createTaskDtos);

        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setTaskTypes(Arrays.asList("PICK"));
        List<Task> tasks = taskManagerService.getTasks(searchTaskDto);
        assertEquals(3, (long) tasks.size());
    }

    @Test
    public void testFailedUpdate(){
        long randomTaskId = 0;
        String taskType = "PICK";
        UpdateTaskDto updateTaskDto = new UpdateTaskDto(randomTaskId);
        updateTaskDto.setType(taskType);
        try{
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto));
            fail("Expected Exception to be thrown.But no exception thrown");
        }
        catch(TaskNotFoundException e){
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }
    }

    @Test
    public void testSearchTasks(){
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setTaskTypes(Arrays.asList(task.getType()));
        searchTaskDto.setTaskStatuses(Arrays.asList(task.getStatus()));
        searchTaskDto.setCreatedAtFromInMillis(DateTime.now().minusMinutes(10).getMillis());
        searchTaskDto.setCreatedAtToInMillis(DateTime.now().plusMinutes(10).getMillis());
        searchTaskDto.setStartTimeFromInMillis(defaultDateTime.getMillis());
        searchTaskDto.setStartTimeToInMillis(defaultDateTime.getMillis());
        searchTaskDto.setEndTimeFromInMillis(defaultDateTime.getMillis());
        searchTaskDto.setEndTimeToInMillis(defaultDateTime.getMillis());
        List<Task> searchedTasks = taskManagerService.getTasks(searchTaskDto);
        assertEquals(1, searchedTasks.size());
        Task searchedTask = searchedTasks.get(0);
        assertEquals(task.getStatus(), searchedTask.getStatus());
        assertEquals(task.getType(), searchedTask.getType());
    }

    @Test
    public void testGetTaskForActor(){
        Task task1 = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        Task task2 = createTestTask(defaultAttributeName, defaultAttributeValue, TaskStatusEnum.IN_PROGRESS, defaultTaskType);
        Actor actor = new Actor();
        actor.setType("HERO");
        actor.setExternalId("A1233333");

        UpdateTaskDto updateTaskDto1 = new UpdateTaskDto(task1.getId());
        updateTaskDto1.setActor(actor);
        UpdateTaskDto updateTaskDto2 = new UpdateTaskDto(task2.getId());
        updateTaskDto2.setActor(actor);
        try {
            taskManagerService.updateTasks(Arrays.asList(updateTaskDto1, updateTaskDto2));
        } catch (TaskNotFoundException e) {
            fail("Exception occurred while updating actor");
        } catch (IllegalTaskStateTransitionException e) {
            fail("Exception occurred while updating actor");
        }

        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setActorExternalIds(Arrays.asList(actor.getExternalId()));

        List<Task> activeTasks = taskManagerService.getTasks(searchTaskDto);
        assertEquals(2, activeTasks.size());
    }

    @Test
    public void testGetTaskForActorByExternalIdFailed(){
        String randomActorExternalId = "blablabla";
        Actor actor = new Actor();
        actor.setType("HERO");
        actor.setExternalId(randomActorExternalId);
        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setActorExternalIds(Arrays.asList(actor.getExternalId()));
        List<Task> activeTasks = taskManagerService.getTasks(searchTaskDto);
        assertEquals(0, activeTasks.size());
    }


    @Test
    public void testFindActiveTasksWithAttribute(){
        Task createdTask = createTestTask("shipmentID", "S1232", TaskStatusEnum.NEW, defaultTaskType);
        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setTaskAttributes(Arrays.asList(new TaskAttributes(createdTask, "shipmentID", "S1232")));
        List<Task> fetchedTasks = taskManagerService.getTasks(searchTaskDto);
        assertEquals(1, fetchedTasks.size());
    }

    @Test
    public void testCreateTask(){
        String subjectType = "Shipment";
        Subject subject = new Subject();
        subject.setType(subjectType);
        Task task = new Task();
        task.setType("HANDSHAKE");
        task.setTaskSubjectRelations(Arrays.asList(new TaskSubjectRelation(subject, task)));
        taskManagerService.createTasks(Arrays.asList(new CreateTaskDto(task)));
        TaskGroup updatedTaskGroup  = taskManagerService.getTaskGroup(new SearchTaskGroupDto(task.getId(), null));
        List<Task> taskList = updatedTaskGroup.getRelations().stream().map(relation -> relation.getTask()).collect(Collectors.toList());
        assertEquals(1, taskList.size());
        assertEquals(taskList.get(0).getType(), "HANDSHAKE");
    }

    @Test
    public void testFetchTaskGraphStraightFlow() {

        long createdTaskGroupId = createTestTaskGroupWithTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        TaskGroup taskGroup = taskManagerService.getTaskGroup(new SearchTaskGroupDto(null, createdTaskGroupId));

        List<Task> taskList = taskGroup.getRelations().stream().map(relation -> relation.getTask()).collect(Collectors.toList());
        Task parentTask = taskList.get(0);

        Task handShakeTask = new Task();
        handShakeTask.setType("HANDSHAKE");
        handShakeTask.setStatus(TaskStatusEnum.NEW);
        List<Long> parentIds = new ArrayList<>();
        parentIds.add(parentTask.getId());
        taskManagerService.createTasks(Arrays.asList(new CreateTaskDto(handShakeTask, taskGroup)));
        UpdateRelationDto updateRelationDto = new UpdateRelationDto(handShakeTask, null, parentTask.getId());
        taskManagerService.updateRelations(Arrays.asList(updateRelationDto));

        Task travelTask1 = new Task();
        travelTask1.setType("TRAVEL");
        travelTask1.setStatus(TaskStatusEnum.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTasks(Arrays.asList(new CreateTaskDto(travelTask1, taskGroup)));
        UpdateRelationDto updateRelationDto1 = new UpdateRelationDto(travelTask1, null, handShakeTask.getId());
        taskManagerService.updateRelations(Arrays.asList(updateRelationDto1));

        Task travelTask2 = new Task();
        travelTask2.setType("TRAVEL");
        travelTask2.setStatus(TaskStatusEnum.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(handShakeTask.getId());
        taskManagerService.createTasks(Arrays.asList(new CreateTaskDto(travelTask2, taskGroup)));
        UpdateRelationDto updateRelationDto2 = new UpdateRelationDto(travelTask2, null, handShakeTask.getId());
        taskManagerService.updateRelations(Arrays.asList(updateRelationDto2));

        Task deliver = new Task();
        deliver.setType("HANDSHAKE");
        deliver.setStatus(TaskStatusEnum.NEW);
        parentIds = new ArrayList<>();
        parentIds.add(travelTask1.getId());
        parentIds.add(travelTask2.getId());
        taskManagerService.createTasks(Arrays.asList(new CreateTaskDto(deliver, taskGroup)));
        UpdateRelationDto updateRelationDto3 = new UpdateRelationDto(deliver, null, travelTask1.getId());
        taskManagerService.updateRelations(Arrays.asList(updateRelationDto3));
        AddRelationDto addRelationDto = new AddRelationDto(deliver, Arrays.asList(travelTask2.getId()));
        taskManagerService.addRelations(Arrays.asList(addRelationDto));

        DirectedGraph<Task, TaskGraphEdge> taskGraph = taskManagerService.getTaskGraphForTaskGroup(createdTaskGroupId);
        Set<TaskGraphEdge> deliverIncomingEdges     = taskGraph.incomingEdgesOf(deliver);
        Set<TaskGraphEdge> handShakeOutgoingEdges   = taskGraph.outgoingEdgesOf(handShakeTask);
        Set<TaskGraphEdge> parentOutgoingEdges      = taskGraph.outgoingEdgesOf(parentTask);
        Set<TaskGraphEdge> travel1OutgoingEdges     = taskGraph.outgoingEdgesOf(travelTask1);
        Set<TaskGraphEdge> travel2OutgoingEdges     = taskGraph.outgoingEdgesOf(travelTask2);

        assertEquals(2, deliverIncomingEdges.size());
        assertEquals(2, handShakeOutgoingEdges.size());
        assertEquals(1, parentOutgoingEdges.size());
        assertEquals(1, travel1OutgoingEdges.size());
        assertEquals(1, travel2OutgoingEdges.size());

        for(TaskGraphEdge edge: parentOutgoingEdges) {
            assertEquals(handShakeTask.getType(), edge.getTarget().getType());
            assertEquals(handShakeTask.getId(), edge.getTarget().getId());
        }

        for(TaskGraphEdge edge: travel2OutgoingEdges) {
            assertEquals(deliver.getType(), edge.getTarget().getType());
            assertEquals(deliver.getId(), edge.getTarget().getId());
        }

        for(TaskGraphEdge edge: travel1OutgoingEdges) {
            assertEquals(deliver.getType(), edge.getTarget().getType());
            assertEquals(deliver.getId(), edge.getTarget().getId());
        }

    }

    @Test
    public void testGetSubjects() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        SearchSubjectDto searchSubjectDto = new SearchSubjectDto();
        searchSubjectDto.setTypes(Arrays.asList("Shipment"));
        searchSubjectDto.setTaskIds(Arrays.asList(task.getId()));
        List<Subject> subjectList = taskManagerService.getSubjects(searchSubjectDto);
        assertEquals(1, subjectList.size());
    }

    @Test
    public void testAddSubjects() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        Subject subject1 = new Subject();
        subject1.setExternalId("subject1");
        subject1.setType("Shipment");
        AddSubjectsDto addSubjectsDto = new AddSubjectsDto(task, Arrays.asList(subject1));
        taskManagerService.addSubjects(Arrays.asList(addSubjectsDto));
        assertEquals(2, task.getTaskSubjectRelations().size());
    }

    @Test
    public void testUpdateSubjects() {
        Task task = createTestTask(defaultAttributeName, defaultAttributeValue, defaultTaskStatus, defaultTaskType);
        Subject oldSubject = task.getTaskSubjectRelations().get(0).getSubject();
        Subject newSubject = new Subject();
        newSubject.setExternalId("newSubject");
        newSubject.setType("Shipment");
        UpdateSubjectDto updateSubjectDto = new UpdateSubjectDto(task, oldSubject, newSubject);
        taskManagerService.updateSubjects(Arrays.asList(updateSubjectDto));
        assertEquals(1, task.getTaskSubjectRelations().size());
        assertEquals("newSubject", task.getTaskSubjectRelations().get(0).getSubject().getExternalId());
    }

    private long createTestTaskGroupWithTask(String attributeName, String attributeValue, TaskStatusEnum status, String type) {
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
        task.setTaskSubjectRelations(new ArrayList<>(Arrays.asList(new TaskSubjectRelation(subject, task))));
        ta.setTask(task);
        CreateTaskDto createTaskDto = new CreateTaskDto();
        createTaskDto.setTask(task);
        List<Task> createdTasks = taskManagerService.createTasks(Arrays.asList(createTaskDto));
        return createdTasks.get(0).getRelations().get(0).getTaskGroup().getId();
    }

    private Task createTestTask(String attributeName, String attributeValue, TaskStatusEnum status, String type) {
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
        task.setTaskSubjectRelations(new ArrayList<>(Arrays.asList(new TaskSubjectRelation(subject, task))));
        ta.setTask(task);
        CreateTaskDto createTaskDto = new CreateTaskDto();
        createTaskDto.setTask(task);
        List<Task> createdTasks = taskManagerService.createTasks(Arrays.asList(createTaskDto));
        return createdTasks.get(0);
    }

    @After
    public void tearDown() {
        entityManager.getTransaction().commit();
    }

    private void emptyDatabases() {
        executeQuery("Delete from relation");
        executeQuery("Delete from task_attributes");
        executeQuery("Delete from task_AUD");
        executeQuery("Delete from task_subject_relation");
        executeQuery("Delete from task");
        executeQuery("Delete from task_group");
        executeQuery("Delete from subject");
        executeQuery("Delete from actor");
    }

    public int executeQuery(final String queryStr) {
        Query query = entityManager.createNativeQuery(queryStr);
        return query.executeUpdate();
    }

}

