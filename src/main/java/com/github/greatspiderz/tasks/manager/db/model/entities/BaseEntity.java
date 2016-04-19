package com.github.greatspiderz.tasks.manager.db.model.entities;

/**
 * Created by akshay.kesarwan on 04/11/15.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.greatspiderz.tasks.manager.db.model.listeners.BaseEntityListener;
import com.github.greatspiderz.tasks.manager.util.JodaDateTimeConverter;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true, exclude = {"createdAt", "updatedAt"})
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
