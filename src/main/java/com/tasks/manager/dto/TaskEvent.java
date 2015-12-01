package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by shlok.chaurasia on 28/11/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskEvent {

    @JsonProperty("to_task_status")
    private String toTaskStatus;

    @JsonProperty("from_task_status")
    private String fromTaskStatus;

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("subject_id")
    private Long subjectId;

    @JsonProperty("actor_id")
    private Long actorId;

    @JsonProperty("task_id")
    private Long taskId;

    @JsonIgnore
    private DateTime eventDate;
    private Long eventDateInMillis;

    public void setEventDateInMillis(Long eventDateInMillis) {
        this.eventDateInMillis = eventDateInMillis;
        this.eventDate = new DateTime(eventDateInMillis);
    }
}

