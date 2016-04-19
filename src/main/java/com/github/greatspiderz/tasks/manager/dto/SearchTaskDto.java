package com.github.greatspiderz.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskAttributes;
import com.github.greatspiderz.tasks.manager.enums.TaskStatusEnum;

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
public class SearchTaskDto {
    private List<Long> taskIds;
    private List<TaskStatusEnum> taskStatuses;
    private List<String> actorExternalIds;
    private List<String> subjectExternalIds;
    private List<String> taskTypes;
    private List<String> tenantIds;
    private List<TaskAttributes> taskAttributes;
    private Long createdAtFromInMillis;
    private Long createdAtToInMillis;
    private Long updatedAtFromInMillis;
    private Long updatedAtToInMillis;
    private Long startTimeFromInMillis;
    private Long startTimeToInMillis;
    private Long endTimeFromInMillis;
    private Long endTimeToInMillis;
    private Integer firstResult;
    private Integer maxResults;
}
