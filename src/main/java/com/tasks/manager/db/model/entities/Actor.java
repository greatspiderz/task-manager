package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Entity
@Data
@Table(name = "actor")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true)
public class Actor extends BaseEntity{
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "actor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "task")
    private List<Task> associatedTasks;
}
