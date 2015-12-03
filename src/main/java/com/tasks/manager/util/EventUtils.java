package com.tasks.manager.util;

import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.dto.TaskEvent;
import org.joda.time.DateTime;

/**
 * Created by shlok.chaurasia on 28/11/15.
 */
public class EventUtils {
    public static TaskEvent getTaskEvent(Task task){
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setTaskType(task.getType());
        taskEvent.setTaskId(task.getId());
        taskEvent.setActor(task.getActor());
        taskEvent.setSubject(task.getSubject());
        taskEvent.setStatus(task.getStatus());
        taskEvent.setAttributes(task.getTaskAttributes());
        taskEvent.setEventDate(DateTime.now());
        return taskEvent;
    }
}
