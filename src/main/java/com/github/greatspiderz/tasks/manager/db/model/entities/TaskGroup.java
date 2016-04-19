package com.github.greatspiderz.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */
@Data
@Entity
@Table(name = "task_group")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true, exclude = {"relations"})
@ToString(callSuper = true, exclude = {"relations"})
public class TaskGroup extends BaseEntity {

    @OneToMany(mappedBy = "taskGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonProperty(value = "relation")
    private List<Relation> relations = new ArrayList<>();

}
