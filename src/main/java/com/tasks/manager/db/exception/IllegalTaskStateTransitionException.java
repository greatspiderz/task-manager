package com.tasks.manager.db.exception;

import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.enums.TaskTriggerEnum;

/**
 * Created by akshay.kesarwan on 26/02/16.
 */
public class IllegalTaskStateTransitionException extends Exception {
    public IllegalTaskStateTransitionException(long taskId, TaskStatus oldStatus, TaskTriggerEnum taskTrigger){
        super("Task with taskId : " + taskId + " is not permitted to change status from " + oldStatus + " by trigger " + taskTrigger);
    }
}
