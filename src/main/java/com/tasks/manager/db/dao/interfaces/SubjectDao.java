package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.SubjectDaoImpl;
import com.tasks.manager.db.model.entities.Relation;
import com.tasks.manager.db.model.entities.Subject;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
@ImplementedBy(SubjectDaoImpl.class)
public interface SubjectDao extends BaseDao<Subject>{
    Subject fetchByExternalId(String externalId);
}
