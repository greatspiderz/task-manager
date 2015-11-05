package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskDao extends BaseDaoJPA<Task> {

    @Inject
    public TaskDao(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Task.class;
    }

    public Task updateSubject(long id, Subject subject) throws TaskNotFoundException{
        Task task = fetchById(id);
        if(task != null)
        {
            task.setSubject(subject);
            return this.save(task);
        }
        throw new TaskNotFoundException(id);
    }

    public Task updateActor(long id, Actor actor) throws TaskNotFoundException
    {
        Task task = fetchById(id);
        if(task != null)
        {
            task.setActor(actor);
            return this.save(task);
        }
        throw new TaskNotFoundException(id);
    }

    public Task updateETA(long id, long eta) throws TaskNotFoundException
    {
        Task task = fetchById(id);
        if(task != null)
        {
            task.setEta(eta);
            return this.save(task);
        }
        throw new TaskNotFoundException(id);
    }

//    public List<Task> searchTask(){
//
//    }

}
