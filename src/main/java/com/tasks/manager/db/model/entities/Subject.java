package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Entity
@Data
@Table(name = "subject")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true, exclude = {"taskSubjectRelations"})
@JsonSnakeCase
@ToString(callSuper = true, exclude = {"taskSubjectRelations"})
public class Subject extends BaseEntity {

    @Column(name = "type")
    private String type;

    @Column(name = "external_id")
    @JsonProperty(value = "external_id")
    private String externalId;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TaskSubjectRelation> taskSubjectRelations;

}
