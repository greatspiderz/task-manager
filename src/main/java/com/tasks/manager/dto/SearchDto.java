package com.tasks.manager.dto;

import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.enums.TaskStatus;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */
@Data
@JsonSnakeCase
public class SearchDto {

    TaskStatus status;
    String type;
    Integer level;
    List<Actor> actors;
    Subject subject;
    String tenant;
    private Integer firstResult;
    private Integer maxResults;
    private DateTime createdAt;
    private TaskAttributes taskAttributes;
}
