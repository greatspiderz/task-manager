package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.ActorDao;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Relation;

import javax.inject.Provider;
import javax.persistence.EntityManager;

/**
 * Created by shlok.chaurasia on 25/11/15.
 */
public class ActorDaoImpl extends BaseDaoImpl<Actor> implements ActorDao{
    @Inject
    public ActorDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Actor.class;
    }

    public void updateActorStatus(Long id, String status){
        Actor actor = fetchById(id);
        actor.setStatus(status);
        save(actor);
    }
}
