package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskDao extends BaseDao<Task> {

    @Inject
    public TaskDao(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Task.class;
    }

    public void updateSubject(long id, Subject subject) throws TaskNotFoundException{
        Task task = fetchById(id);
        if(task != null)
        {
            task.setSubject(subject);
            this.save(task);
            return;
        }
        throw new TaskNotFoundException(id);
    }

    public void updateActor(long id, Actor actor) throws TaskNotFoundException
    {
        Task task = fetchById(id);
        if(task != null)
        {
            task.setActor(actor);
            this.save(task);
            return;
        }
        throw new TaskNotFoundException(id);
    }

    public void updateETA(long id, long eta) throws TaskNotFoundException
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

    public void updateStatus(long id, TaskStatus taskStatus) throws TaskNotFoundException
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
        Criterion statusCriterion = Restrictions.eq("status", searchDto.getStatus());
        Criterion taskGroupIdCriterion = Restrictions.eq("task_group_id", searchDto.getTaskGroupId());
        Criterion tasktypeCriterion = Restrictions.eq("type", searchDto.getType());
        Criterion levelCriterion = Restrictions.eq("level", searchDto.getLevel());
        //TODO :  Ask about column
        Criterion actorIdCriterion = Restrictions.eq("actor_id", searchDto.getActor().getId());
        Criterion actorTypeCriterion = Restrictions.eq("actor_type", searchDto.getActor().getType());
        // TODO:  Ask for Or/And
        Criterion searchCriterion = Restrictions.or(
                statusCriterion,
                taskGroupIdCriterion,
                tasktypeCriterion,
                levelCriterion,
                actorIdCriterion,
                actorTypeCriterion
        );

        return findByCriteria(searchCriterion);
    }

    public List<Task> getAll(List<Long> taskIds)
    {
        Criterion listIdsCriterion = Restrictions.in("id", taskIds);
        return findByCriteria(listIdsCriterion);
    }

}
