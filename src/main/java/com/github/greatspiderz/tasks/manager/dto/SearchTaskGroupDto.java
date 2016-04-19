package com.github.greatspiderz.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class SearchTaskGroupDto {
    private Long taskId;
    private Long taskGroupId;
}
