package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tasks.manager.db.model.entities.Task;

import java.util.List;

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
public class AddRelationDto {
    private Task task;
    private List<Long> parentIds;
}
