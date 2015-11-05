package com.tasks.manager.dto;

import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.enums.TaskStatus;

/**
 * Created by divya.rai on 05/11/15.
 */
public class SearchDto {

    TaskStatus status;
    Long taskGroupId;
    String type;
    Integer level;
    Actor actor;
}
