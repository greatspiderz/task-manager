package com.tasks.manager.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Entity
@Table(name = "Task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

    @Column(name = "state")
    private String state;

    @Column(name = "task_id")
    private String taskId;

    @Embedded
    private Actor actor;

    @Embedded
    private Subject subject;

}
