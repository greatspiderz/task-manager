package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.TaskDaoImpl;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;

import java.util.List;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@ImplementedBy(TaskDaoImpl.class)
public interface TaskDao extends BaseDao<Task>{
    void updateSubject(long id, Subject subject) throws TaskNotFoundException;
    void updateActor(long id, Actor actor) throws TaskNotFoundException;
    void updateETA(long id, long eta) throws TaskNotFoundException;
    void updateStatus(long id, TaskStatus taskStatus) throws TaskNotFoundException;
    List<Task> search(SearchDto searchDto);
    List<Task> getAll(List<Long> taskIds);
}
