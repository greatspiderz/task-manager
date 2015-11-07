package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.*;
import javax.validation.constraints.NotNull;
import com.tasks.manager.db.model.enums.TaskStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Data
@Entity
@Table(name = "task")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true, exclude = {"taskAttributes"})
public class Task extends BaseEntity{

    @NotNull
    @Column(name = "status", columnDefinition = "enum('NEW','COMPLETED')")
    private TaskStatus status;

    @Embedded
    private Actor actor;

    @Embedded
    private Subject subject;

    @NotNull
    @Column(name = "type")
    private String type;

    @Column(name = "start_time")
    private DateTime startTime;

    @Column(name = "end_time")
    private DateTime endTime;

    @Column(name = "eta")
    private Long eta;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "task_attributes")
    private List<TaskAttributes> taskAttributes = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "task_group_id")
    @JsonProperty(value = "task_group_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private TaskGroup taskGroup;

}
