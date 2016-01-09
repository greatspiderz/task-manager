package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.ActorDaoImpl;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Relation;

import java.util.List;

/**
 * Created by shlok.chaurasia on 25/11/15.
 */
@ImplementedBy(ActorDaoImpl.class)
public interface ActorDao extends BaseDao<Actor>{
    void updateActorStatus(Long id, String status);
    List<Actor> fetchByExternalId(String externalId);
}
