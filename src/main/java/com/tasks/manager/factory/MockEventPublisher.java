package com.tasks.manager.factory;

import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.enums.TaskStatusEnum;
import com.tasks.manager.service.api.EventPublisher;

import java.util.List;

/**
 * Created by palash.v on 22/03/16.
 */
public class MockEventPublisher implements EventPublisher {

    @Override
    public void publishTaskCreationEvent(Task task) {

    }

    @Override
    public void publishTaskStatusChangeEvent(Task task, TaskStatusEnum oldStatus) {

    }

    @Override
    public void publishTaskAttributeChangeEvent(Task task, List<TaskAttributes> oldAttributes) {

    }

    @Override
    public void publishActorAssignmentEvent(Task task, Actor oldActor) {

    }

    @Override
    public void publishActorReleaseEvent(Actor actor) {

    }

    @Override
    public void publishTaskTypeChangeEvent(Task task, String oldTaskType) {

    }

    @Override
    public void publishTaskGroupChangeEvent(Task task, TaskGroup oldTaskGroup) {

    }

    @Override
    public void publishTaskRelationChangeEvent(Task task, List<Long> oldParentIds) {

    }

}
