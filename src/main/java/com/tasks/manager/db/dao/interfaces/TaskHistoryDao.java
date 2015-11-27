package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.TaskGroupDaoImpl;
import com.tasks.manager.db.dao.jpa.TaskHistoryDaoImpl;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.entities.TaskHistory;

/**
 * Created by shlok.chaurasia on 27/11/15.
 */
@ImplementedBy(TaskHistoryDaoImpl.class)
public interface TaskHistoryDao extends BaseDao<TaskHistory> {

}
