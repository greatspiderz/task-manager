package com.tasks.manager.db.dao.jpa;

import com.google.inject.Inject;

import com.tasks.manager.db.dao.interfaces.TaskSubjectRelationDao;
import com.tasks.manager.db.model.entities.TaskSubjectRelation;
import com.tasks.manager.dto.UpdateSubjectDto;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * Created by sarathkumar.k on 17/02/16.
 */
public class TaskSubjectRelationDaoImpl extends BaseDaoImpl<TaskSubjectRelation> implements TaskSubjectRelationDao {

    @Inject
    public TaskSubjectRelationDaoImpl(EntityManager entityManager) {
        super(entityManager);
        entityClass = TaskSubjectRelation.class;
    }

    @Override
    public void updateSubject(UpdateSubjectDto updateSubjectDto) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria criteria = session.createCriteria(TaskSubjectRelation.class)
                .createAlias("task", "t")
                .createAlias("subject", "s")
                .add(Restrictions.eq("t.id", updateSubjectDto.getTask().getId()))
                .add(Restrictions.eq("s.id", updateSubjectDto.getOldSubject().getId()));
        List<TaskSubjectRelation> taskSubjectRelations = criteria.list();

        if(taskSubjectRelations!=null && taskSubjectRelations.size() > 0){
            TaskSubjectRelation taskSubjectRelation = taskSubjectRelations.get(0);
            taskSubjectRelation.setSubject(updateSubjectDto.getNewSubject());
            this.save(taskSubjectRelation);
        }
    }
}
