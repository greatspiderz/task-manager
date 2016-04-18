package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskGroup;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by divya.rai on 17/02/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTaskDto {

    @NotNull
    private Task task;
    private TaskGroup taskGroup;

    public CreateTaskDto(Task task) {
        this.task = task;
    }

}
