package com.tasks.manager.util;

import com.tasks.manager.db.model.entities.*;

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

    public static Task cloneTaskObject(Task task){
        Task newTask = new Task();
        newTask.setStatus(task.getStatus());
        newTask.setActor(task.getActor());
        newTask.setSubject(task.getSubject());
        newTask.setType(task.getType());
        newTask.setStartTime(task.getStartTime());
        newTask.setEndTime(task.getEndTime());
        newTask.setEta(task.getEta());
        newTask.setDescription(task.getDescription());
        for(TaskAttributes tskattr: task.getTaskAttributes())
        {
            tskattr.setTask(newTask);
        }
        newTask.setTaskAttributes(task.getTaskAttributes());
        return newTask;
    }

}
