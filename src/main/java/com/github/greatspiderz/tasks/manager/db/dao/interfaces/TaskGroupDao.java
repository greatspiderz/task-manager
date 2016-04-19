package com.github.greatspiderz.tasks.manager.db.dao.interfaces;

import com.github.greatspiderz.tasks.manager.db.dao.jpa.TaskGroupDaoImpl;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskGroup;
import com.google.inject.ImplementedBy;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@ImplementedBy(TaskGroupDaoImpl.class)
public interface TaskGroupDao extends BaseDao<TaskGroup> {

}
