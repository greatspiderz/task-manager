package com.tasks.manager.service.api;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */

@ImplementedBy(TaskManagerServiceImpl.class)
public interface TaskManagerService {

    TaskGroup createTaskGroup ( TaskGroup taskgroup);

    TaskGroup fetchTaskGroup ( long tgId );

    Task createTask(Task task, long tgId);
    Task createTaskWithParentTasks(Task task, long tgId, List<Long> parentTaskIds);

    Task fetchTask ( long taskId );

    void updateActorStatus(Long actorId, String status) throws TaskNotFoundException;
    void updateTaskActor(Long taskId, Actor actor) throws TaskNotFoundException;
    Actor createActor(Actor actor);
    Actor fetchActor(Long actorId);
    void updateSubject(long taskId, Subject subject) throws TaskNotFoundException;

    void updateStatus( long taskId, TaskStatus newStatus) throws TaskNotFoundException;

    void updateETA( long taskId, long eta) throws TaskNotFoundException;

    List<Task> findTasks(SearchDto searchdto);

    List<Task> findTasksForAttributes(HashMap<String, String> attibuteNameValue);

    List<Task> getTasksForTaskGroup(SearchDto searchdto, Long taskGroupId);

    Task createRelation(Task task, TaskGroup taskGroup, long parentTaskId);
    DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId);
    TaskGroup saveTasks(TaskGroup taskGroup);
    TaskGroup getTaskGroupForTask(Task task);
}
