package com.tasks.manager.db.model.entities;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.persist.Transactional;
import com.tasks.manager.db.model.listeners.BaseEntityListener;
import com.tasks.manager.util.JodaDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

@Data
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty(value = "id")
    private Long id;

    @Version
    @Column(name = "version")
    @JsonIgnore
    private Long version;

    @Column(name = "created_at")
    @JsonIgnore
    @Convert(converter = JodaDateTimeConverter.class)
    private DateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    @Convert(converter = JodaDateTimeConverter.class)
    private DateTime updatedAt;
}
