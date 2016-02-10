package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.enums.TaskStatus;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchDto {

    @JsonProperty(value = "statuses")
    private List<TaskStatus> statuses = new ArrayList<>();

    @JsonProperty(value = "from_date")
    private DateTime fromDate;

    @JsonProperty(value = "to_date")
    private DateTime toDate;

    //TODO: To be deprecated later
    @JsonProperty(value = "status")
    private TaskStatus status;

    @JsonProperty(value = "created_at")
    private DateTime createdAt;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "level")
    private Integer level;

    @JsonProperty(value = "actors")
    private List<Actor> actors;

    @JsonProperty(value = "subject")
    private Subject subject;

    @JsonProperty(value = "tenant")
    private String tenant;

    @JsonProperty(value = "first_result")
    private Integer firstResult;

    @JsonProperty(value = "max_results")
    private Integer maxResults;

    @JsonProperty(value = "task_attributes")
    private TaskAttributes taskAttributes;

}
