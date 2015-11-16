package com.tasks.manager.service.impl;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.RelationDao;
import com.tasks.manager.db.dao.interfaces.TaskAttributesDao;
import com.tasks.manager.db.dao.interfaces.TaskDao;

import com.tasks.manager.db.dao.interfaces.TaskGroupDao;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.util.StateMachineProvider;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */
@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

    private final TaskDao taskDao;
    private final TaskGroupDao taskGroupDao;
    private final TaskAttributesDao taskAttributesDao;
    private final RelationDao relationDao;
    private final StateMachineConfig taskStateMachineConfig;

    @Inject
    public TaskManagerServiceImpl(TaskDao taskDao,
                                  TaskGroupDao taskGroupDao,
                                  TaskAttributesDao taskAttributesDao,
                                  StateMachineProvider stateMachineProvider, RelationDao relationDao) {
        this.taskDao = taskDao;
        this.taskGroupDao = taskGroupDao;
        this.taskAttributesDao = taskAttributesDao;
        this.relationDao = relationDao;
        this.taskStateMachineConfig = stateMachineProvider.get();
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
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        if(task.getRelations()==null)
            task.setRelations(new ArrayList<>());
        if(taskGroup.getRelations()==null)
            taskGroup.setRelations(new ArrayList<>());
        task.getRelations().add(relation);
        taskGroup.getRelations().add(relation);
        relationDao.save(relation);
        return task;
    }

    @Override
    public Task fetchTask(long taskId) {
        return taskDao.fetchById(taskId);
    }

    @Override
    public void updateActor(long taskId, Actor actor) throws TaskNotFoundException {
        taskDao.updateActor(taskId,actor);

    }

    @Override
    public void updateSubject(long taskId, Subject subject) throws TaskNotFoundException {
        taskDao.updateSubject(taskId, subject);
    }

    @Override
    public void updateStatus(long taskId, TaskStatus newStatus) throws TaskNotFoundException {
        Task task = fetchTask(taskId);
        updateTaskStateMachine(task, newStatus);

        taskDao.updateStatus(taskId, newStatus);
    }

    private void updateTaskStateMachine(Task task, TaskStatus newStatus) {
        log.info("updating status of task " + task.getId() + " with trigger " + newStatus);
        StateMachine<TaskStatus, TaskStatus> stateMachine = new StateMachine(task.getStatus(), taskStateMachineConfig);
        stateMachine.fire(newStatus);
        task.setStatus(newStatus);
    }

    @Override
    public void updateETA(long taskId, long eta) throws TaskNotFoundException{
        taskDao.updateETA(taskId, eta);

    }

    @Override
    public List<Task> findTasks(SearchDto searchdto) {
        return taskDao.search(searchdto);
    }

    public List<Task> findTasksInTaskGroup(Long taskGroupId) {
        List<Task> tasks = new ArrayList<>();
        List<Relation> relations = taskGroupDao.fetchById(taskGroupId).getRelations();
        for (Relation relation: relations) {
            tasks.add(relation.getTask());
        }
        return tasks;
    }

    @Override
    public List<Task> findTasksInTaskGroup(SearchDto searchdto, Long taskGroupId) {
        List<Relation> relations = taskGroupDao.fetchById(taskGroupId).getRelations();
        List<Long> taskIds = new ArrayList<>();
        for (Relation relation: relations) {
            taskIds.add(relation.getTask().getId());
        }

        return taskDao.getAll(taskIds);
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

    public List<Task> fetchParentTasks(long taskId){
        List<Relation> relations = taskDao.fetchById(taskId).getRelations();
        List<Long> parentIds = new ArrayList<>();
        for (Relation relation : relations) {
            parentIds.add(relation.getParentTaskId());
        }
        return taskDao.getAll(parentIds);
    }
}
