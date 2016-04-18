package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.enums.TaskTriggerEnum;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by palash.v on 17/02/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateTaskDto {

    @NotNull
    private Long taskId;
    private TaskTriggerEnum taskTriggerEnum;
    private Actor actor;
    private String type;
    private List<TaskAttributes> taskAttributes;
    private Long taskGroupId;

    public UpdateTaskDto(Long taskId) {
        this.taskId = taskId;
    }

}
