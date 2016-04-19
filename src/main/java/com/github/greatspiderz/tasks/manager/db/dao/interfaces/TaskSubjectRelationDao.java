package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.github.greatspiderz.tasks.manager.db.model.entities.TaskSubjectRelation;
import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.TaskSubjectRelationDaoImpl;
import com.github.greatspiderz.tasks.manager.dto.UpdateSubjectDto;

/**
 * Created by sarathkumar.k on 17/02/16.
 */
@ImplementedBy(TaskSubjectRelationDaoImpl.class)
public interface TaskSubjectRelationDao extends BaseDao<TaskSubjectRelation> {

    void updateSubject(UpdateSubjectDto updateSubjectDto);

}
