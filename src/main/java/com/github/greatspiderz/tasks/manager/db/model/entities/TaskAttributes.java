package com.github.greatspiderz.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_attributes")
@EqualsAndHashCode(callSuper=false, exclude = {"task"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true, exclude = {"task"})
public class TaskAttributes extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonProperty(value = "task_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Task task;

    @Column(name = "attribute_name")
    @JsonProperty(value = "attribute_name")
    private String attributeName;

    @Column(name = "attribute_value", columnDefinition = "LONGTEXT")
    @JsonProperty(value = "attribute_value")
    private String attributeValue;
}
