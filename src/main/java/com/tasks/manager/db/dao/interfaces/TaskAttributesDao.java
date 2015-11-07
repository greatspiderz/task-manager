package com.tasks.manager.db.dao.interfaces;

import com.google.inject.ImplementedBy;
import com.tasks.manager.db.dao.jpa.TaskAttributesDaoImpl;
import com.tasks.manager.db.model.entities.TaskAttributes;

import java.util.HashMap;
import java.util.List;

/**
 * Created by shlok.chaurasia on 06/11/15.
 */
@ImplementedBy(TaskAttributesDaoImpl.class)
public interface TaskAttributesDao extends BaseDao<TaskAttributes> {
    List<TaskAttributes> findTaskAttributes(HashMap<String, String> attributeNameValue);
}
