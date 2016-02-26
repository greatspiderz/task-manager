package com.tasks.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasks.manager.db.model.entities.TaskAttributes;

/**
 * Created by akshay.kesarwan on 26/02/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAttributeDto {
    @JsonProperty(value = "attribute_name")
    private String attributeName;

    @JsonProperty(value = "attribute_value")
    private String attributeValue;

    public TaskAttributeDto(TaskAttributes taskAttributes) {
        if(taskAttributes != null) {
            this.attributeName = taskAttributes.getAttributeName();
            this.attributeValue = taskAttributes.getAttributeValue();
        }
    }
}
