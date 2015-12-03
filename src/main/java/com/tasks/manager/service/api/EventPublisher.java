package com.tasks.manager.service.api;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.enums.TaskEventType;
import com.tasks.manager.service.impl.EventPublisherImpl;

import java.util.List;

/**
 * Created by akshay.kesarwan on 03/12/15.
 */
@ImplementedBy(EventPublisherImpl.class)
public interface EventPublisher {
    void publishTaskCreationEvent(Task task);
    void publishTaskStatusChangeEvent(Task task, TaskStatus oldStatus);
    void publishTaskAttributeChangeEvent(Task task, List<TaskAttributes> oldAttributes);
    void publishActorAssignmentEvent(Task task, Actor oldActor);
}
