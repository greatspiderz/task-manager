package com.tasks.manager.util;

import com.tasks.manager.db.model.entities.Relation;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskGroup;

import java.util.ArrayList;

/**
 * Created by shlok.chaurasia on 19/11/15.
 */
public class TaskManagerUtility {
    public static TaskGroup bindRelation(Task task, TaskGroup taskGroup, Long parentTaskId)
    {
        Relation relation = new Relation();
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        relation.setParentTaskId(parentTaskId);
        if (task.getRelations() == null ) {
            task.setRelations(new ArrayList<>());
        }
        task.getRelations().add(relation);
        if (taskGroup.getRelations() == null ) {
            taskGroup.setRelations(new ArrayList<>());
        }
        taskGroup.getRelations().add(relation);
        return taskGroup;
    }
}
