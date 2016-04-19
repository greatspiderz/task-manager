package com.github.greatspiderz.tasks.manager.db.dao.jpa;

import com.github.greatspiderz.tasks.manager.db.dao.interfaces.TaskDao;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import com.github.greatspiderz.tasks.manager.db.model.entities.Task;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskAttributes;
import com.github.greatspiderz.tasks.manager.dto.SearchTaskDto;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskDaoImpl extends BaseDaoImpl<Task> implements TaskDao {

    @Inject
    public TaskDaoImpl(EntityManager entityManager) {
        super(entityManager);
        entityClass = Task.class;
    }

    @Override
    public List<Task> search(SearchTaskDto searchTaskDto) {
        StringBuilder queryString = new StringBuilder("select t from Task t left join t.taskSubjectRelations tsr left join t.taskAttributes ta");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();

        queryString.append(" where ");
        List<String> queryParamStringList = new ArrayList<>();

        if (searchTaskDto.getTaskIds() != null && searchTaskDto.getTaskIds().size() > 0) {
            queryParamStringList.add("t.id in (:taskIds)");
            namedParamMapBuilder.put("taskIds", searchTaskDto.getTaskIds());
        }

        if (searchTaskDto.getTaskStatuses() != null && searchTaskDto.getTaskStatuses().size() > 0) {
            queryParamStringList.add("t.status in (:taskStatuses)");
            namedParamMapBuilder.put("taskStatuses", searchTaskDto.getTaskStatuses());
        }

        if (searchTaskDto.getActorExternalIds() != null && searchTaskDto.getActorExternalIds().size() > 0) {
            queryParamStringList.add("t.actor.externalId in (:actorExternalIds)");
            namedParamMapBuilder.put("actorExternalIds", searchTaskDto.getActorExternalIds());
        }

        if (searchTaskDto.getSubjectExternalIds() != null && searchTaskDto.getSubjectExternalIds().size() > 0) {
            queryParamStringList.add("tsr.subject.externalId in (:subjectExternalIds)");
            namedParamMapBuilder.put("subjectExternalIds", searchTaskDto.getSubjectExternalIds());
        }

        if (searchTaskDto.getTaskTypes() != null && searchTaskDto.getTaskTypes().size() > 0) {
            queryParamStringList.add("t.type in (:taskTypes)");
            namedParamMapBuilder.put("taskTypes", searchTaskDto.getTaskTypes());
        }

        if (searchTaskDto.getTenantIds() != null && searchTaskDto.getTenantIds().size() > 0) {
            queryParamStringList.add("t.tenantId in (:tenantIds)");
            namedParamMapBuilder.put("tenantIds", searchTaskDto.getTenantIds());
        }

        if (searchTaskDto.getTaskAttributes() != null && searchTaskDto.getTaskAttributes().size() > 0) {
            queryParamStringList.add("ta.attributeName in (:taskAttributeName)");
            namedParamMapBuilder.put("taskAttributeName", searchTaskDto.getTaskAttributes().stream().map(TaskAttributes::getAttributeName).collect(Collectors.toList()));
            queryParamStringList.add("ta.attributeValue in (:taskAttributeValue)");
            namedParamMapBuilder.put("taskAttributeValue", searchTaskDto.getTaskAttributes().stream().map(TaskAttributes::getAttributeValue).collect(Collectors.toList()));
        }

        if(searchTaskDto.getCreatedAtFromInMillis() != null) {
            queryParamStringList.add("t.createdAt between :createdAtFrom and :createdAtTo");
            DateTime createdAtFrom = new DateTime(searchTaskDto.getCreatedAtFromInMillis());
            DateTime createdAtTo = (searchTaskDto.getCreatedAtToInMillis() != null) ? new DateTime(searchTaskDto.getCreatedAtToInMillis()) : createdAtFrom.withTimeAtStartOfDay().plusDays(1);
            namedParamMapBuilder.put("createdAtFrom", createdAtFrom);
            namedParamMapBuilder.put("createdAtTo", createdAtTo);
        }

        if(searchTaskDto.getUpdatedAtFromInMillis() != null) {
            queryParamStringList.add("t.updatedAt between :updatedAtFrom and :updatedAtTo");
            DateTime updatedAtFrom = new DateTime(searchTaskDto.getUpdatedAtFromInMillis());
            DateTime updatedAtTo = (searchTaskDto.getUpdatedAtToInMillis() != null) ? new DateTime(searchTaskDto.getUpdatedAtToInMillis()) : updatedAtFrom.withTimeAtStartOfDay().plusDays(1);
            namedParamMapBuilder.put("updatedAtFrom", updatedAtFrom);
            namedParamMapBuilder.put("updatedAtTo", updatedAtTo);
        }

        if(searchTaskDto.getStartTimeFromInMillis() != null) {
            queryParamStringList.add("t.startTime between :startTimeFrom and :startTimeTo");
            DateTime startTimeFrom = new DateTime(searchTaskDto.getStartTimeFromInMillis());
            DateTime startTimeTo = (searchTaskDto.getStartTimeToInMillis() != null) ? new DateTime(searchTaskDto.getStartTimeToInMillis()) : startTimeFrom.withTimeAtStartOfDay().plusDays(1);
            namedParamMapBuilder.put("startTimeFrom", startTimeFrom);
            namedParamMapBuilder.put("startTimeTo", startTimeTo);
        }

        if(searchTaskDto.getEndTimeFromInMillis() != null) {
            queryParamStringList.add("t.endTime between :endTimeFrom and :endTimeTo");
            DateTime endTimeFrom = new DateTime(searchTaskDto.getEndTimeFromInMillis());
            DateTime endTimeTo = (searchTaskDto.getEndTimeToInMillis() != null) ? new DateTime(searchTaskDto.getEndTimeToInMillis()) : endTimeFrom.withTimeAtStartOfDay().plusDays(1);
            namedParamMapBuilder.put("endTimeFrom", endTimeFrom);
            namedParamMapBuilder.put("endTimeTo", endTimeTo);
        }

        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        queryString.append(String.join(" and ", queryParamStringList));
        List<Task> taskResults = new ArrayList<>();
        if (namedParamMap.size() > 0) {
            taskResults = findByQueryAndNamedParams(searchTaskDto.getFirstResult(), searchTaskDto.getMaxResults(),
                    queryString.toString(), namedParamMap);
        }
        Set<Task> tasksSet = new HashSet<>(taskResults);
        return new ArrayList<>(tasksSet);
    }

}
