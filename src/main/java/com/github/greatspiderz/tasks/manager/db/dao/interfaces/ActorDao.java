package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.ActorDaoImpl;
import com.github.greatspiderz.tasks.manager.dto.SearchActorDto;
import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.model.entities.Actor;

import java.util.List;

/**
 * Created by shlok.chaurasia on 25/11/15.
 */
@ImplementedBy(ActorDaoImpl.class)
public interface ActorDao extends BaseDao<Actor> {

    List<Actor> searchActors(SearchActorDto searchActorDto);

}
