package com.tasks.manager.db.model.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by akshay.kesarwan on 27/10/15.
 */
@Embeddable
@Data
public class Actor {
    @Column(name = "actor_type")
    private String type;
    @Column(name = "actor_id")
    private String id;
}
