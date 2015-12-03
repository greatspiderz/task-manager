package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.enums.TaskStatus;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by shlok.chaurasia on 28/11/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskEvent {

    @JsonProperty("event")
    private String event;

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("subject")
    private Subject subject;

    @JsonProperty("actor")
    private Actor actor;

    @JsonProperty("status")
    private TaskStatus status;

    @JsonProperty("attributes")
    private List<TaskAttributes> attributes;


    @JsonProperty("old_status")
    private TaskStatus oldStatus;

    @JsonProperty("old_attributes")
    private List<TaskAttributes> oldAttributes;

    @JsonProperty("old_actor")
    private Actor oldActor;


    @JsonIgnore
    private DateTime eventDate;

    @JsonProperty(value = "event_date_in_millis")
    private Long eventDateInMillis;

    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
        this.eventDateInMillis = eventDate.getMillis();
    }
}

