package com.tasks.manager.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.core.MessageSender;
import com.flipkart.restbus.client.entity.Event;
import com.flipkart.restbus.client.entity.OutboundMessage;
import com.fquick.resthibernateplugin.core.annotations.AsyncAnnotation;
import com.fquick.resthibernateplugin.core.configs.RestBusConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.TaskEvent;
import com.tasks.manager.service.api.EventPublisher;
import com.tasks.manager.util.EventUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Created by akshay.kesarwan on 03/12/15.
 */
@Slf4j
public class EventPublisherImpl implements EventPublisher {

    private final MessageSender sender;
    private String restEnv;
    private ObjectMapper objectMapper;
    private String exchangeName = "fquick.tasks";

    @Inject
    public EventPublisherImpl(@AsyncAnnotation MessageSender sender, RestBusConfig restBusConfig) {
        this.sender = sender;
        this.objectMapper = new ObjectMapper();
        this.restEnv = restBusConfig.getRestEnvName();
    }

    @Override
    public void publishTaskCreationEvent(Task task) {
        TaskEvent taskEvent = EventUtils.getTaskEvent(task);
        taskEvent.setEvent("TASK_CREATION_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishTaskStatusChangeEvent(Task task, TaskStatus oldStatus) {
        TaskEvent taskEvent = EventUtils.getTaskEvent(task);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setEvent("TASK_STATUS_CHANGE_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishTaskAttributeChangeEvent(Task task, List<TaskAttributes> oldAttributes) {
        TaskEvent taskEvent = EventUtils.getTaskEvent(task);
        taskEvent.setOldAttributes(oldAttributes);
        taskEvent.setEvent("TASK_ATTRIBUTE_CHANGE_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishActorAssignmentEvent(Task task, Actor oldActor) {
        TaskEvent taskEvent = EventUtils.getTaskEvent(task);
        taskEvent.setOldActor(oldActor);
        taskEvent.setEvent("ACTOR_ASSIGNMENT_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    private void publishTaskEvent(String eventName, TaskEvent taskEvent) {
        try {
            Event event = new Event(eventName, objectMapper.writeValueAsString(taskEvent));
            event.setExchangeName(this.restEnv + "." + this.exchangeName);
            event.setExchangeType(OutboundMessage.ExchangeType.topic.name());
            sender.publish(event);
        } catch (IOException e) {
            log.error("Exception found while publishing event to RestBus", e.getMessage());
        }
    }
}
