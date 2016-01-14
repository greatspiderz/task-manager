package com.tasks.manager.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flipkart.restbus.client.core.MessageSender;
import com.flipkart.restbus.client.entity.Event;
import com.flipkart.restbus.client.entity.OutboundMessage;
import com.fquick.resthibernateplugin.core.annotations.AsyncAnnotation;
import com.fquick.resthibernateplugin.core.configs.RestBusConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.RelationDao;
import com.tasks.manager.db.dao.interfaces.TaskDao;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.TaskEvent;
import com.tasks.manager.service.api.EventPublisher;
import com.tasks.manager.util.EventUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

/**
 * Created by akshay.kesarwan on 03/12/15.
 */
@Slf4j
public class EventPublisherImpl implements EventPublisher {

    private final MessageSender sender;
    private String restEnv;
    private ObjectMapper objectMapper;
    private String exchangeName = "fquick.tasks";
    private final RelationDao relationDao;
    private final TaskDao taskDao;

    @Inject
    public EventPublisherImpl(@AsyncAnnotation MessageSender sender, RestBusConfig restBusConfig,
                              RelationDao relationDao, TaskDao taskDao) {
        this.sender = sender;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        this.restEnv = restBusConfig.getRestEnvName();
        this.relationDao = relationDao;
        this.taskDao = taskDao;
    }

    @Override
    public void publishTaskCreationEvent(Task task) {
        Set<Subject> subjects = getSubjects(task);
        TaskEvent taskEvent = EventUtils.getTaskEvent(task,subjects);
        taskEvent.setEvent("TASK_CREATION_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishTaskStatusChangeEvent(Task task, TaskStatus oldStatus) {
        Set<Subject> subjects = getSubjects(task);
        TaskEvent taskEvent = EventUtils.getTaskEvent(task,subjects);
        taskEvent.setOldStatus(oldStatus);
        taskEvent.setEvent("TASK_STATUS_CHANGE_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishTaskAttributeChangeEvent(Task task, List<TaskAttributes> oldAttributes) {
        Set<Subject> subjects = getSubjects(task);
        TaskEvent taskEvent = EventUtils.getTaskEvent(task,subjects);
        taskEvent.setOldAttributes(oldAttributes);
        taskEvent.setEvent("TASK_ATTRIBUTE_CHANGE_EVENT");
        publishTaskEvent(taskEvent.getEvent(), taskEvent);
    }

    @Override
    public void publishActorAssignmentEvent(Task task, Actor oldActor) {
        Set<Subject> subjects = getSubjects(task);
        TaskEvent taskEvent = EventUtils.getTaskEvent(task,subjects);
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

    /**
     * For fetching the subjects for every task. For Merged tasks (eg. Handshake) subjects will be fetched from its parents
     * @param task
     * @return
     */
    private Set<Subject> getSubjects(Task task){
        if (task.getSubject() != null)
            return new HashSet<>(Arrays.asList(task.getSubject()));
        else {
            Set<Subject> subjects = new HashSet<>();
            List<Relation> relations = relationDao.fetchByTaskId(task.getId());
            for (Relation relation : relations) {
                if(relation.getParentTaskId()!=null){
                    Task parentTask = taskDao.fetchById(relation.getParentTaskId());
                    if (parentTask.getSubject() != null)
                        subjects.add(parentTask.getSubject());
                    else {
                        subjects.addAll(getSubjects(parentTask));
                    }
                }
            }
            return subjects;
        }
    }
}
