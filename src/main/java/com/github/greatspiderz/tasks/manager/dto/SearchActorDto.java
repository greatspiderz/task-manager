package com.github.greatspiderz.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

/**
 * Created by palash.v on 17/02/16.
 */
@Data
@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchActorDto {
    private List<String> externalIds;
    private List<String> types;
    private Long taskGroupID;
}
