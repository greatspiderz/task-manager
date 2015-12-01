package com.tasks.manager.util;

import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.dto.TaskEvent;

/**
 * Created by shlok.chaurasia on 28/11/15.
 */
public class Utils {
    public static TaskEvent getTaskEvent(Task task, String fromTaskStatus){
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setToTaskStatus(task.getStatus().name());

        taskEvent.setFromTaskStatus(fromTaskStatus);

        taskEvent.setTaskType(task.getType());

        taskEvent.setTaskId(task.getId());

        if(task.getActor()!=null)
            taskEvent.setActorId(task.getActor().getExternalId());
        if(task.getSubject()!=null)
            taskEvent.setSubjectId(task.getSubject().getId());

        return taskEvent;
    }
}
