package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */
@Entity
@Data
@Table(name = "task_attributes")
@EqualsAndHashCode(callSuper=false, exclude = {"task"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAttributes extends BaseEntity{

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonProperty(value = "task_id")
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
    private Task task;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
