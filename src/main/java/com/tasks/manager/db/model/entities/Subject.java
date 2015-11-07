package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Embeddable
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subject {
    @Column(name = "subject_type")
    private String type;
    @Column(name = "subject_id")
    private long id;
}
