package com.github.greatspiderz.tasks.manager.util;

import com.github.greatspiderz.tasks.manager.db.model.entities.Relation;
import com.github.greatspiderz.tasks.manager.db.model.entities.Task;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskAttributes;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskGroup;
import com.github.greatspiderz.tasks.manager.enums.TaskStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by palash.v on 04/03/16.
 */
public class TaskManagerUtility {

    private static List<TaskStatusEnum> nonActiveTaskStatuses = new ArrayList<TaskStatusEnum>(){{
        add(TaskStatusEnum.CANCELLED);
        add(TaskStatusEnum.CANCELLED_AND_MERGED);
        add(TaskStatusEnum.NON_EXECUTABLE);
    }};

    public static boolean isTaskActive(TaskStatusEnum taskStatus) {
        return !nonActiveTaskStatuses.contains(taskStatus);
    }

    public static TaskGroup bindRelation(Task task, TaskGroup taskGroup, Long parentTaskId) {
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
        newTask.setTaskSubjectRelations(task.getTaskSubjectRelations());
        newTask.setType(task.getType());
        newTask.setStartTime(task.getStartTime());
        newTask.setEndTime(task.getEndTime());
        newTask.setEta(task.getEta());
        newTask.setDescription(task.getDescription());
        for(TaskAttributes tskattr: task.getTaskAttributes()) {
            tskattr.setTask(newTask);
        }
        newTask.setTaskAttributes(task.getTaskAttributes());
        return newTask;
    }

}