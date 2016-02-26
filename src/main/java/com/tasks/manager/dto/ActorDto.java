package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasks.manager.db.model.entities.Actor;

import javax.persistence.Column;

/**
 * Created by akshay.kesarwan on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDto {
    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "external_id")
    private String externalId;

    public ActorDto(Actor actor) {
        this.type = actor.getType();
        this.externalId = actor.getExternalId();
    }
}
