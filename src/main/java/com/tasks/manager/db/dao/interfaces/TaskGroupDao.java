package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.TaskGroupDaoImpl;
import com.tasks.manager.db.model.entities.TaskGroup;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@ImplementedBy(TaskGroupDaoImpl.class)
public interface TaskGroupDao extends BaseDao<TaskGroup> {
}
