package com.github.greatspiderz.tasks.manager.db.dao.jpa;

import com.github.greatspiderz.tasks.manager.dto.SearchActorDto;
import com.google.inject.Inject;

import com.github.greatspiderz.tasks.manager.db.dao.interfaces.ActorDao;
import com.github.greatspiderz.tasks.manager.db.model.entities.Actor;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * Created by palash.v on 30/03/16.
 */
public class ActorDaoImpl extends BaseDaoImpl<Actor> implements ActorDao {

    @Inject
    public ActorDaoImpl(EntityManager entityManager) {
        super(entityManager);
        entityClass = Actor.class;
    }

    @Override
    public List<Actor> searchActors(SearchActorDto searchActorDto) {

        List<String> actorTypes = searchActorDto.getTypes();
        List<String> externalIds = searchActorDto.getExternalIds();

        Session session = (Session) getEntityManager().getDelegate();
        Criteria criteria = session.createCriteria(Actor.class);

        if (actorTypes != null && actorTypes.size() > 0) {
            Criterion typeCriterion = (actorTypes.size() == 1) ?
                    Restrictions.eq("type", actorTypes.get(0)) :
                    Restrictions.in("type", actorTypes);
            criteria.add(typeCriterion);
        }

        if (externalIds != null && externalIds.size() > 0) {
            Criterion externalIdCriterion = (externalIds.size() == 1) ?
                    Restrictions.eq("externalId", externalIds.get(0)) :
                    Restrictions.in("externalId", externalIds);
            criteria.add(externalIdCriterion);
        }

        return criteria.list();
    }

}
