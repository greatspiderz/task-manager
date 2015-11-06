package com.tasks.manager.service.api;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;

import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */

@ImplementedBy(TaskManagerServiceImpl.class)
public interface TaskManagerService {

    TaskGroup createTaskGroup ( TaskGroup taskgroup);

    TaskGroup fetchTaskGroup ( long tgId );

    Task createTask(Task task, long tgId);

    Task fetchTask ( long taskId );

    void updateActor(long taskId, Actor actor) throws TaskNotFoundException;

    void updateSubject(long taskId, Subject subject) throws TaskNotFoundException;

    void updateStatus( long taskId, TaskStatus newStatus) throws TaskNotFoundException;

    void updateETA( long taskId, long eta) throws TaskNotFoundException;

    List<Task> findTasks(SearchDto searchdto);

    List<Task> findTasksForAttributes(String key, String value);



}
