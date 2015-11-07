package com.tasks.manager.dto;

import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.enums.TaskStatus;
import lombok.Data;

/**
 * Created by divya.rai on 05/11/15.
 */
@Data
public class SearchDto {

    String status;
    Long taskGroupId;
    String type;
    Integer level;
    Actor actor;
    private Integer firstResult;
    private Integer maxResults;

}
