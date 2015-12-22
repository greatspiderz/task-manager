package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.RelationDaoImpl;
import com.tasks.manager.db.model.entities.Relation;

import java.util.List;


/**
 * Created by shlok.chaurasia on 13/11/15.
 */
@ImplementedBy(RelationDaoImpl.class)
public interface RelationDao extends BaseDao<Relation> {
    List<Relation> fetchByTaskGroupId(long taskGroupId);
    List<Relation> fetchByTaskId(long taskId);
    List<Relation> fetchByParentTaskId(Long taskId);
}
