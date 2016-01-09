package com.tasks.manager.dto;

import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.enums.TaskStatus;
import lombok.Data;

import java.util.List;

/**
 * Created by divya.rai on 05/11/15.
 */
@Data
public class SearchDto {

    TaskStatus status;
    String type;
    Integer level;
    List<Actor> actors;
    Subject subject;
    private Integer firstResult;
    private Integer maxResults;

}
