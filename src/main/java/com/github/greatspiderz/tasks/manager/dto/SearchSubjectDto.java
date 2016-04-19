package com.github.greatspiderz.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
public class SearchSubjectDto {
    private List<String> externalIds;
    private List<String> types;
    private List<Long> taskIds;
    private Integer firstResult;
    private Integer maxResults;

}
