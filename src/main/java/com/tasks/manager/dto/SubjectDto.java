package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasks.manager.db.model.entities.Subject;

/**
 * Created by akshay.kesarwan on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectDto {
    private String type;

    @JsonProperty(value = "external_id")
    private String externalId;

    public SubjectDto(Subject subject) {
        this.type = subject.getType();
        this.externalId = subject.getExternalId();
    }
}
