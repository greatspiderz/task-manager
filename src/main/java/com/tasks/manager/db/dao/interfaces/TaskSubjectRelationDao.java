package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;

import com.tasks.manager.db.dao.jpa.TaskSubjectRelationDaoImpl;
import com.tasks.manager.db.model.entities.TaskSubjectRelation;
import com.tasks.manager.dto.UpdateSubjectDto;

/**
 * Created by sarathkumar.k on 17/02/16.
 */
@ImplementedBy(TaskSubjectRelationDaoImpl.class)
public interface TaskSubjectRelationDao extends BaseDao<TaskSubjectRelation> {

    void updateSubject(UpdateSubjectDto updateSubjectDto);

}
