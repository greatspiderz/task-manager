package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.github.greatspiderz.tasks.manager.db.model.entities.Relation;
import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.RelationDaoImpl;

import java.util.List;

/**
 * Created by shlok.chaurasia on 13/11/15.
 */
@ImplementedBy(RelationDaoImpl.class)
public interface RelationDao extends BaseDao<Relation> {

    List<Relation> fetchByTaskGroupId(long taskGroupId);

}
