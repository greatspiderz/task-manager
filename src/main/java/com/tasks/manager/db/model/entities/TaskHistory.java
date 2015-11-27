package com.tasks.manager.db.model.entities;

import com.fasterxml.jackson.annotation.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Created by shlok.chaurasia on 27/11/15.
 */
@Entity
@Data
@Table(name = "task_history")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=true)
public class TaskHistory extends BaseEntity {


    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonProperty(value = "task_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Task task;

}
