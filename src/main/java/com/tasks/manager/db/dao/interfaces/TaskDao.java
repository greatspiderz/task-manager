package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;

import com.tasks.manager.db.dao.jpa.TaskDaoImpl;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.dto.SearchTaskDto;

import java.util.List;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@ImplementedBy(TaskDaoImpl.class)
public interface TaskDao extends BaseDao<Task>{

    List<Task> search(SearchTaskDto searchTaskDto);

}
