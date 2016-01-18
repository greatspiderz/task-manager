package com.tasks.manager.service.api;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.enums.TaskTriggerEnum;
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
    Actor fetchActorByExternalId(String actorId);

    void updateStatus( Long taskId, TaskTriggerEnum triggerEnum) throws TaskNotFoundException;

    void updateETA( Long taskId, Long eta) throws TaskNotFoundException;

    List<Task> findTasks(SearchDto searchdto);

    List<Task> getTasksForTaskGroup(Long taskGroupId);
    Task createRelation(Task task, TaskGroup taskGroup, Long parentTaskId);
    DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId);
    TaskGroup getTaskGroupForTask(Task task);
    void updateParentTask(Task task, Long parentTaskId);
    List<Task> getActiveTasksforSubject(String subjectExternalId);
    List<Task> getActiveTasksForActor(String actorExternalId);

    List<Task> getNextTasksForActor(String actorExternalId, Long completedTaskId);
    List<Task> bulkInsertTasks(List<Task> tasks);
    List<TaskGroup> findActiveTaskGroupsWithAttribute(String attributeName, String attributeValue);
    TaskGroup fetchTaskGroupBySubjectExternalId(String externalId);
    void updateAllActiveTasksStatusInTaskGroup(TaskGroup taskGroup, TaskStatus taskStatus);
    Subject fetchSubjectByExternalId(String externalId);
    void updateTaskAttribute(Task task, String attributeName, String attributeValue);
}
