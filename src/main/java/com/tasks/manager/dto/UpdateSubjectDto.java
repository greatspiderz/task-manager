package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;

import javax.validation.constraints.NotNull;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sarathkumar.k on 17/02/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateSubjectDto {
    @NotNull
    private Task task;
    @NotNull
    private Subject oldSubject;
    @NotNull
    private Subject newSubject;
}
