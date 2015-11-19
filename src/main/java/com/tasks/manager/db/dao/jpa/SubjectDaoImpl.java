package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.SubjectDao;
import com.tasks.manager.db.dao.interfaces.TaskAttributesDao;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.TaskAttributes;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
public class SubjectDaoImpl extends BaseDaoImpl<Subject> implements SubjectDao {
    @Inject
    public SubjectDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Subject.class;
    }
}
