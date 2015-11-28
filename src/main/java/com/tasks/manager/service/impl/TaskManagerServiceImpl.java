package com.tasks.manager.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.core.MessageSender;
import com.flipkart.restbus.client.entity.Event;
import com.fquick.resthibernateplugin.core.annotations.AsyncAnnotation;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.*;

import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.util.StateMachineProvider;
import com.tasks.manager.util.Utils;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.persist.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.*;
import org.jgrapht.graph.*;


/**
 * Created by divya.rai on 05/11/15.
 */
@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

    private final TaskDao taskDao;
    private final TaskGroupDao taskGroupDao;
    private final TaskAttributesDao taskAttributesDao;
    private final RelationDao relationDao;
    private final SubjectDao subjectDao;
    private final ActorDao actorDao;
    private final TaskHistoryDao taskHistoryDao;
    private final StateMachineConfig taskStateMachineConfig;
    private final MessageSender sender;
    private final ObjectMapper objectMapper;
    private String restEnv;
    @Inject
    public TaskManagerServiceImpl(TaskDao taskDao,
                                  TaskGroupDao taskGroupDao,
                                  TaskAttributesDao taskAttributesDao,
                                  StateMachineProvider stateMachineProvider, RelationDao relationDao,
                                  SubjectDao subjectDao, ActorDao actorDao, TaskHistoryDao taskHistoryDao,
                                  @AsyncAnnotation MessageSender sender,
                                  ObjectMapper objectMapper) {
        this.taskDao = taskDao;
        this.taskGroupDao = taskGroupDao;
        this.taskAttributesDao = taskAttributesDao;
        this.relationDao = relationDao;
        this.taskStateMachineConfig = stateMachineProvider.get();
        this.subjectDao = subjectDao;
        this.actorDao = actorDao;
        this.taskHistoryDao = taskHistoryDao;
        this.sender = sender;
        this.objectMapper = objectMapper;
    }

    public void setRestEnv(String restEnv){
        this.restEnv=restEnv;
    }

    @Override
    public TaskGroup createTaskGroup(TaskGroup taskgroup) {
        taskGroupDao.save(taskgroup);
        return taskgroup;
    }

    @Override
    public TaskGroup fetchTaskGroup(long tgId) {
        return taskGroupDao.fetchById(tgId);
    }

    @Override
    public Task createTask(Task task, long tgId) {
        TaskGroup taskGroup = taskGroupDao.fetchById(tgId);
        Relation relation = new Relation();
        taskDao.save(task);
        saveTaskHistory(task);
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        if (task.getRelations() == null)
            task.setRelations(new ArrayList<>());
        if (taskGroup.getRelations() == null)
            taskGroup.setRelations(new ArrayList<>());
        task.getRelations().add(relation);
        taskGroup.getRelations().add(relation);
        relationDao.save(relation);
        publishTaskEvent(task, "");
        return task;
    }


    @Override
    public TaskGroup saveTasks(TaskGroup taskGroup) {
        List<Relation> relations = taskGroup.getRelations();
        List<Task> tasks = new ArrayList<>();
        for (Relation relation : relations) {
            tasks.add(relation.getTask());
        }
        taskGroupDao.save(taskGroup);
        for (Task task : tasks) {
            saveTaskHistory(task);
            publishTaskEvent(task, "");
        }

        taskDao.bulkInsert(tasks);
        relationDao.bulkInsert(relations);
        return taskGroup;
    }

    @Override
    public Task createTaskWithParentTasks(Task task, long tgId, List<Long> parentTaskIds) {
        TaskGroup taskGroup = taskGroupDao.fetchById(tgId);
        taskDao.save(task);
        saveTaskHistory(task);
        for (Long parentTaskId : parentTaskIds) {
            Relation relation = new Relation();
            relation.setTaskGroup(taskGroup);
            relation.setTask(task);
            relation.setParentTaskId(parentTaskId);
            if (task.getRelations() == null)
                task.setRelations(new ArrayList<>());
            if (taskGroup.getRelations() == null)
                taskGroup.setRelations(new ArrayList<>());
            task.getRelations().add(relation);
            taskGroup.getRelations().add(relation);
            relationDao.save(relation);
        }
        return task;
    }

    @Override
    public Task fetchTask(long taskId) {
        return taskDao.fetchById(taskId);
    }

    @Override
    public void updateActorStatus(Long actorId, String status) throws TaskNotFoundException {
        actorDao.updateActorStatus(actorId, status);
    }

    @Override
    public Actor createActor(Actor actor) {
        actorDao.save(actor);
        return actor;
    }

    @Override
    public Actor fetchActor(Long actorId) {
        return actorDao.fetchById(actorId);
    }

    public void updateTaskActor(Long taskId, Actor actor) throws TaskNotFoundException {
        createActor(actor);
        taskDao.updateTaskActor(taskId, actor);
        Task task = taskDao.fetchById(taskId);
        actor.getAssociatedTasks().add(task);
    }

    @Override
    public void updateSubject(long taskId, Subject subject) throws TaskNotFoundException {
        Task task = taskDao.fetchById(taskId);
        if (task != null) {
            task.setSubject(subject);
            if (subject.getAssociatedTasks() == null)
                subject.setAssociatedTasks(new ArrayList<>());
            subject.getAssociatedTasks().add(task);
            subjectDao.save(subject);
            taskDao.save(task);
            return;
        }
        throw new TaskNotFoundException(taskId);
    }

    @Override
    public void updateStatus(long taskId, TaskStatus newStatus) throws TaskNotFoundException {
        Task task = fetchTask(taskId);
        updateTaskStateMachine(task, newStatus);
        TaskStatus fromTaskStatus = task.getStatus();
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTaskStatus(newStatus);
        taskHistory.setTask(task);
        task.getTaskHistory().add(taskHistory);
        taskHistoryDao.save(taskHistory);
        taskDao.updateStatus(taskId, newStatus);
        task.setStatus(newStatus);
        publishTaskEvent(task, fromTaskStatus.name());
    }

    private void updateTaskStateMachine(Task task, TaskStatus newStatus) {
        log.info("updating status of task " + task.getId() + " with trigger " + newStatus);
        StateMachine<TaskStatus, TaskStatus> stateMachine = new StateMachine(task.getStatus(), taskStateMachineConfig);
        stateMachine.fire(newStatus);
        task.setStatus(newStatus);
    }

    @Override
    public void updateETA(long taskId, long eta) throws TaskNotFoundException {
        taskDao.updateETA(taskId, eta);

    }

    @Override
    public List<Task> findTasks(SearchDto searchdto) {
        return taskDao.search(searchdto);
    }

    @Override
    public List<Task> getTasksForTaskGroup(Long taskGroupId) {
        List<Task> tasks = new ArrayList<>();
        List<Relation> relations = taskGroupDao.fetchById(taskGroupId).getRelations();
        for (Relation relation : relations) {
            tasks.add(relation.getTask());
        }
        return tasks;
    }

    @Override
    public TaskGroup getTaskGroupForTask(Task task) {
        List<Relation> relations = task.getRelations();
        if (relations.size() > 0) {
            Relation relation = relations.get(0);
            return relation.getTaskGroup();
        }
        return null;
    }

    public List<Task> bulkInsert(List<Task> tasks) {
        return taskDao.bulkInsert(tasks);
    }

    @Override
    public List<Task> findTasksForAttributes(HashMap<String, String> attributeNameValue) {

        List<TaskAttributes> taskAttributes = taskAttributesDao.findTaskAttributes(attributeNameValue);
        Iterator<TaskAttributes> iterator = taskAttributes.iterator();
        List<Task> tasks = new ArrayList<>();
        while (iterator.hasNext()) {
            TaskAttributes taskAttribute = iterator.next();
            tasks.add(taskAttribute.getTask());
        }
        return tasks;
    }

    public List<Task> fetchParentTasks(long taskId) {
        List<Relation> relations = taskDao.fetchById(taskId).getRelations();
        List<Long> parentIds = new ArrayList<>();
        for (Relation relation : relations) {
            parentIds.add(relation.getParentTaskId());
        }
        return taskDao.getAll(parentIds);
    }

    public DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId) {
        return getTaskGraph(taskGroupId);
    }

    public Task createRelation(Task task, TaskGroup taskGroup, long parentTaskId) {
        Relation relation = new Relation();
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        relation.setParentTaskId(parentTaskId);
        task.getRelations().add(relation);
        taskGroup.getRelations().add(relation);
        relationDao.save(relation);
        return task;
    }

    private DirectedGraph<Task, TaskGraphEdge> getTaskGraph(Long taskGrpId) {
        DirectedGraph<Task, TaskGraphEdge> taskGraph = new DefaultDirectedGraph<Task, TaskGraphEdge>(TaskGraphEdge.class);
        List<Relation> relations = taskGroupDao.fetchById(taskGrpId).getRelations();
        List<Task> tasks = new ArrayList<>();
        for (Relation relation : relations) {
            tasks.add(relation.getTask());
        }
        List<Long> taskIdsAddedToGraph = new ArrayList<>();
        for (Task task : tasks) {
            if (!taskIdsAddedToGraph.contains(task.getId())) {
                taskGraph.addVertex(task);
                taskIdsAddedToGraph.add(task.getId());
            }
            List<Task> parentTasks = getParentTasks(relations, tasks, task);
            for (Task parentTask : parentTasks) {
                if (!taskIdsAddedToGraph.contains(parentTask.getId())) {
                    taskGraph.addVertex(parentTask);
                    taskIdsAddedToGraph.add(parentTask.getId());
                }
                taskGraph.addEdge(parentTask, task);
            }

        }
        return taskGraph;
    }

    private List<Task> getParentTasks(List<Relation> relations, List<Task> tasks, Task task) {
        List<Task> parentTasks = new ArrayList<>();
        for (Relation eachRelation : relations) {
            if (eachRelation.getTask().getId() == task.getId()) {
                long parentTaskId = eachRelation.getParentTaskId();
                for (Task eachTask : tasks) {
                    if (eachTask.getId().longValue() == parentTaskId) {
                        parentTasks.add(eachTask);
                        break;
                    }
                }
            }
        }
        return parentTasks;
    }

    private void saveTaskHistory(Task task) {
        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setTaskStatus(task.getStatus());
        task.getTaskHistory().add(taskHistory);
        taskHistoryDao.save(taskHistory);
    }

    private void publishTaskEvent(Task task, String fromTaskStatus) {
        try {
            Event event = new Event("TaskEvent", this.objectMapper.writeValueAsString(Utils.getTaskEvent(task, fromTaskStatus)));
            event.setExchangeName(this.restEnv + ".XXX.YYY.ZZZ");
            event.setExchangeType("topic");
            sender.publish(event);
        } catch (IOException e) {
            log.error("Exception found while publishing event to RestBus", e.getMessage());
        }
    }
}
