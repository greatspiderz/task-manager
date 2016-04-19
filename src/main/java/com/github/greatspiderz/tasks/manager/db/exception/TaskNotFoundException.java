package com.github.greatspiderz.tasks.manager.db.exception;

/**
 * Created by shlok.chaurasia on 05/11/15.
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(long taskId){
        super("Task not found with ID: " + taskId);
    }
}
