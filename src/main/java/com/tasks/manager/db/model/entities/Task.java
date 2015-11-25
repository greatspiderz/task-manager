package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.*;
import javax.validation.constraints.NotNull;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.util.JodaDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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
    @Column(name = "from_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus fromStatus = null;

    @NotNull
    @Column(name = "to_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus toStatus = TaskStatus.NEW;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    @JsonProperty(value = "actor_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Actor actor;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    @JsonProperty(value = "subject_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Subject subject;

    @NotNull
    @Column(name = "type")
    private String type;

    @Convert(converter = JodaDateTimeConverter.class)
    @Column(name = "start_time")
    private DateTime startTime;

    @Convert(converter = JodaDateTimeConverter.class)
    @Column(name = "end_time")
    private DateTime endTime;

    @Column(name = "eta")
    private Long eta;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "task_attributes")
    private List<TaskAttributes> taskAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "relation")
    private List<Relation> relations;


}
