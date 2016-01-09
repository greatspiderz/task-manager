package com.tasks.manager.db.dao.jpa;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.ActorDao;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Relation;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by shlok.chaurasia on 25/11/15.
 */
public class ActorDaoImpl extends BaseDaoImpl<Actor> implements ActorDao{
    @Inject
    public ActorDaoImpl(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
        entityClass = Actor.class;
    }
    public Actor fetchByExternalId(String externalId){
        StringBuilder queryString = new StringBuilder("FROM Actor a WHERE external_id = (:external_id)");
        ImmutableMap.Builder<String, Object> namedParamMapBuilder = ImmutableMap.<String, Object>builder();
        namedParamMapBuilder.put("external_id", externalId);
        ImmutableMap<String, Object> namedParamMap = namedParamMapBuilder.build();
        List<Actor> actors = findByQueryAndNamedParams(null, null, queryString.toString(), namedParamMap);
        if (actors.size()>0)
            return actors.get(0);
        return null;
    }

    public void updateActorStatus(Long id, String status){
        Actor actor = fetchById(id);
        actor.setStatus(status);
        save(actor);
    }
}
