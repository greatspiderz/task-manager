package com.tasks.manager.db.dao.jpa;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.SubjectDao;
import com.tasks.manager.db.dao.interfaces.TaskAttributesDao;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.TaskAttributes;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
public class SubjectDaoImpl extends BaseDaoImpl<Subject> implements SubjectDao {
    @Inject
    public SubjectDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Subject.class;
    }

    @Override
    public Subject fetchByExternalId(String externalId){
        StringBuilder queryString = new StringBuilder("FROM Subject s WHERE external_id = (:external_id)");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();
        namedParamMapBuilder.put("external_id", externalId);
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        List<Subject> subjects = findByQueryAndNamedParams(0, 1, queryString.toString(), namedParamMap);
        return subjects.get(0);
    }
}
