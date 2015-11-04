package com.tasks.manager.db.entities;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Embeddable
@Data
public class Subject {
    private String type;
    private String id;
}
