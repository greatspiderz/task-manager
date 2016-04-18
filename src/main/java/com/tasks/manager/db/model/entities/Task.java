package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tasks.manager.enums.TaskStatusEnum;
import com.tasks.manager.util.JodaDateTimeConverter;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Data
@Entity
@Table(name = "task")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true, of = {"type"})
@ToString(callSuper = true, exclude = {"relations", "taskSubjectRelations"})
public class Task extends BaseEntity {

    @NotNull
    @Audited
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum status = TaskStatusEnum.NEW;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id")
    @JsonProperty(value = "actor_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "external_id")
    @JsonIdentityReference(alwaysAsId = true)
    private Actor actor;

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

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonProperty(value = "task_attributes")
    private List<TaskAttributes> taskAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Relation> relations;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TaskSubjectRelation> taskSubjectRelations;

    @JsonProperty(value = "tenant_id")
    @Column(name = "tenant_id")
    private String tenantId;

}
