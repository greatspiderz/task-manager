package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Entity
@Data
@Table(name = "subject")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true, exclude = {"associatedTasks"})
@JsonSnakeCase
@ToString(callSuper = true, exclude = {"associatedTasks"})
public class Subject extends BaseEntity{
    @Column(name = "type")
    private String type;

    @Column(name = "external_id")
    @JsonProperty(value = "external_id")
    private String externalId;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Task> associatedTasks;

}
