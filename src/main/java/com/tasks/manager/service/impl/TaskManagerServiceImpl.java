package com.tasks.manager.service.impl;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.jpa.TaskAttributesDao;
import com.tasks.manager.db.dao.jpa.TaskDao;
import com.tasks.manager.db.dao.jpa.TaskGroupDao;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.util.StateMachineProvider;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */
@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

    private final TaskDao taskDao;
    private final TaskGroupDao tgDao;
    private final TaskAttributesDao taskAttributesDao;
    private final StateMachineConfig taskStateMachineConfig;

    @Inject
    public TaskManagerServiceImpl(TaskDao taskDao,
                                  TaskGroupDao tgDao,
                                  TaskAttributesDao taskAttributesDao,
                                  StateMachineProvider stateMachineProvider) {
        this.taskDao = taskDao;
        this.tgDao = tgDao;
        this.taskAttributesDao = taskAttributesDao;
        this.taskStateMachineConfig = stateMachineProvider.get();
    }

    @Transactional
    @Override
    public TaskGroup createTaskGroup(TaskGroup taskgroup) {
        tgDao.save(taskgroup);
        return taskgroup;
    }

    @Override
    public TaskGroup fetchTaskGroup(long tgId) {
        return tgDao.fetchById(tgId);
    }

    @Transactional
    @Override
    public Task createTask(Task task, long tgId) {
        taskDao.save(task, tgId);
        return task;
    }

    @Override
    public Task fetchTask(long taskId) {
        return taskDao.fetchById(taskId);
    }

    @Transactional
    @Override
    public void updateActor(long taskId, Actor actor) {
        taskDao.updateActor(taskId,actor);

    }

    @Transactional
    @Override
    public void updateSubject(long taskId, Subject subject) {
        taskDao.updateSubject(taskId, subject);
    }

    @Transactional
    @Override
    public void updateStatus(long taskId, TaskStatus newStatus) {
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

    @Transactional
    @Override
    public void updateETA(long taskId, long eta) {
        taskDao.updateETA(taskId, eta);

    }

    @Override
    public List<Task> findTasks(SearchDto searchdto) {
        return taskDao.search(searchdto);
    }

    @Override
    public List<Task> findTasksForAttributes(String key, String value) {
        return taskAttributesDao.findTask(key, value);
    }
}
