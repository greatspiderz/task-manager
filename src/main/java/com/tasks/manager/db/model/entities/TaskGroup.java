package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */
@Data
@Entity
@Table(name = "task_group")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true)
public class TaskGroup extends BaseEntity{
    @OneToMany(mappedBy = "task_group", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "tasks")
    private List<TaskAttributes> tasks = new ArrayList<>();
}
