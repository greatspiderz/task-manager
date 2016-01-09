package com.tasks.manager.db.dao.jpa;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.TaskDao;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskDaoImpl extends BaseDaoImpl<Task> implements TaskDao{

    @Inject
    public TaskDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Task.class;
    }

    public void updateTaskActor(Long taskId, Actor actor) throws TaskNotFoundException
    {
        Task task = fetchById(taskId);
        if(task != null)
        {
            task.setActor(actor);
            this.save(task);
            return;
        }
        throw new TaskNotFoundException(taskId);
    }

    public void updateETA(Long id, Long eta) throws TaskNotFoundException
    {
        Task task = fetchById(id);
        if(task != null)
        {
            task.setEta(eta);
            this.save(task);
            return;
        }
        throw new TaskNotFoundException(id);
    }

    public void updateStatus(Long id, TaskStatus taskStatus) throws TaskNotFoundException
    {
        Task task = fetchById(id);
        if(task != null)
        {
            task.setStatus(taskStatus);
            this.save(task);
            return;
        }
        throw new TaskNotFoundException(id);
    }

    public List<Task> search(SearchDto searchDto)
    {
        StringBuilder queryString = new StringBuilder("select t from Task t");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();

        queryString.append(" where ");
        List<String> queryParamStringList = new ArrayList<>();
        if(searchDto.getStatus()!=null)
        {
            queryParamStringList.add("t.status = :status");
            namedParamMapBuilder.put("status", searchDto.getStatus());
        }

        if(searchDto.getType()!=null)
        {
            queryParamStringList.add("t.type = :type");
            namedParamMapBuilder.put("type", searchDto.getType());
        }

        if(searchDto.getActors()!=null)
        {
            queryParamStringList.add("t.actor.externalId = :actor_ids");
            namedParamMapBuilder.put("actor_ids", searchDto.getActors().stream().map(actor -> actor.getExternalId()).collect(Collectors.toList()));
        }
        if(searchDto.getSubject()!=null){
            queryParamStringList.add("t.subject.externalId = :subject_id");
            namedParamMapBuilder.put("subject_id", searchDto.getSubject().getExternalId());
        }
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        queryString.append(String.join( " and ", queryParamStringList));
        List<Task> taskResults = new ArrayList<>();
        if(namedParamMap.size() > 0) {
            taskResults = findByQueryAndNamedParams(searchDto.getFirstResult(), searchDto.getMaxResults(),
                    queryString.toString(), namedParamMap);
        }
        return taskResults;
    }

    public List<Task> searchActiveTasksForActor(SearchDto searchDto){
        StringBuilder queryString = new StringBuilder("FROM Task t WHERE status IN (:statuses)");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();
        List<TaskStatus> activeStatuses = new ArrayList<>();
        activeStatuses.add(TaskStatus.IN_PROGRESS);
        namedParamMapBuilder.put("statuses", activeStatuses);
        List<String> queryParamStringList = new ArrayList<>();
        if(searchDto.getActors()!=null)
        {
            queryString.append(" and ");
            queryParamStringList.add("actor_id in :actor_ids");
            namedParamMapBuilder.put("actor_ids", searchDto.getActors().stream().map(actor -> actor.getId()).collect(Collectors.toList()));
        }
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        queryString.append(String.join( " and ", queryParamStringList));
        List<Task> taskResults = new ArrayList<>();
        if(namedParamMap.size() > 0) {
            taskResults = findByQueryAndNamedParams(searchDto.getFirstResult(), searchDto.getMaxResults(),
                    queryString.toString(), namedParamMap);
        }
        return taskResults;
    }

    @Override
    public List<Task> fetchBySubjectId(Long subjectId){
        StringBuilder queryString = new StringBuilder("FROM Task a WHERE subject_id = (:subject_id)");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();
        namedParamMapBuilder.put("subject_id", subjectId);
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        return findByQueryAndNamedParams(null, null, queryString.toString(), namedParamMap);
    }

    public List<Task> getAll(List<Long> taskIds)
    {
        Criterion listIdsCriterion = Restrictions.in("id", taskIds);
        return findByCriteria(listIdsCriterion);
    }

}
