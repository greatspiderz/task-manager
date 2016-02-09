package com.tasks.manager.dto;

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
@JsonSnakeCase
public class SearchDto {

    private List<TaskStatus> statuses = new ArrayList<>();
    private DateTime fromDate;
    private DateTime toDate;

    //TODO: To be deprecated later
    private TaskStatus status;
    private DateTime createdAt;


    private String type;
    private Integer level;
    private List<Actor> actors;
    private Subject subject;
    private String tenant;
    private Integer firstResult;
    private Integer maxResults;
    private TaskAttributes taskAttributes;

}
