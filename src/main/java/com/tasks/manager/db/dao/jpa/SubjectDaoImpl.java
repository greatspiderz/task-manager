package com.tasks.manager.db.dao.jpa;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import com.tasks.manager.db.dao.interfaces.SubjectDao;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.dto.SearchSubjectDto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
@Slf4j
public class SubjectDaoImpl extends BaseDaoImpl<Subject> implements SubjectDao {

    @Inject
    public SubjectDaoImpl(EntityManager entityManager) {
        super(entityManager);
        entityClass = Subject.class;
    }

    @Override
    public List<Subject> searchSubjects(SearchSubjectDto searchSubjectDto) {
        StringBuilder queryString = new StringBuilder("select s from Subject s left join s.taskSubjectRelations tsr ");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();
        queryString.append(" where ");
        List<String> queryParamStringList = new ArrayList<>();

        if (searchSubjectDto.getExternalIds() != null) {
            if (searchSubjectDto.getExternalIds().size() == 1) {
                queryParamStringList.add("s.externalId = :externalId");
                namedParamMapBuilder.put("externalId",searchSubjectDto.getExternalIds().get(0));
            } else {
                queryParamStringList.add("s.externalId in :externalIds");
                namedParamMapBuilder.put("externalIds",searchSubjectDto.getExternalIds());
            }

        }
        if (searchSubjectDto.getTypes() != null) {
            if (searchSubjectDto.getTypes().size() == 1) {
                queryParamStringList.add("s.type = :type");
                namedParamMapBuilder.put("type",searchSubjectDto.getTypes().get(0));
            } else {
                queryParamStringList.add("s.type in :types");
                namedParamMapBuilder.put("types",searchSubjectDto.getTypes());
            }
        }
        if (searchSubjectDto.getTaskIds() != null) {
            if (searchSubjectDto.getTaskIds().size() == 1) {
                queryParamStringList.add("tsr.task.id = :taskId");
                namedParamMapBuilder.put("taskId",searchSubjectDto.getTaskIds().get(0));
            } else {
                queryParamStringList.add("tsr.task.id in :taskIds");
                namedParamMapBuilder.put("taskIds",searchSubjectDto.getTaskIds());
            }
        }
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        queryString.append(String.join(" and ", queryParamStringList));
        List<Subject> subjectList = new ArrayList<>();
        if (namedParamMap.size() > 0) {
            subjectList = findByQueryAndNamedParams(searchSubjectDto.getFirstResult(), searchSubjectDto.getMaxResults(),
                    queryString.toString(), namedParamMap);
        }

        if (subjectList.size() == 0)
            log.info("No subjects found for given search criteria");

        return subjectList;
    }

}
