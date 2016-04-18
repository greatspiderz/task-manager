package com.tasks.manager.db.exception;

import com.tasks.manager.enums.TaskStatusEnum;
import com.tasks.manager.enums.TaskTriggerEnum;

/**
 * Created by palash.v on 10/03/16.
 */
public class IllegalTaskStateTransitionException extends Exception {
    public IllegalTaskStateTransitionException(long taskId, TaskStatusEnum oldStatus, TaskTriggerEnum taskTrigger){
        super("Task with taskId : " + taskId + " is not permitted to change status from " + oldStatus + " by trigger " + taskTrigger);
    }
}