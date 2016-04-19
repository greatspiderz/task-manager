package com.github.greatspiderz.tasks.manager.service.api;

import com.github.greatspiderz.tasks.manager.enums.TaskStatusEnum;
import com.github.greatspiderz.tasks.manager.db.model.entities.Actor;
import com.github.greatspiderz.tasks.manager.db.model.entities.Task;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskAttributes;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskGroup;

import java.util.List;

/**
 * Created by akshay.kesarwan on 03/12/15.
 */
public interface EventPublisher {

    void publishTaskCreationEvent(Task task);

    void publishTaskStatusChangeEvent(Task task, TaskStatusEnum oldStatus);

    void publishTaskAttributeChangeEvent(Task task, List<TaskAttributes> oldAttributes);

    void publishActorAssignmentEvent(Task task, Actor oldActor);

    void publishActorReleaseEvent(Actor actor);

    void publishTaskTypeChangeEvent(Task task, String oldTaskType);

    void publishTaskGroupChangeEvent(Task task, TaskGroup oldTaskGroup);

    void publishTaskRelationChangeEvent(Task task, List<Long> oldParentIds);

}
