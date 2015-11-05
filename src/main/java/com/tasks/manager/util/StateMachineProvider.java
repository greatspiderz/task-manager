package com.tasks.manager.util;

import com.google.inject.Provider;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.tasks.manager.db.model.enums.TaskStatus;

/**
 * Created by divya.rai on 05/11/15.
 */
public class StateMachineProvider implements Provider<StateMachineConfig> {

    @Override
    public StateMachineConfig get() {
        StateMachineConfig<TaskStatus, TaskStatus> taskStateMachineConfig = new StateMachineConfig<>();

        taskStateMachineConfig.configure(TaskStatus.NEW)
                .permit(TaskStatus.CANCELLED, TaskStatus.CANCELLED);

        taskStateMachineConfig.configure(TaskStatus.NEW)
                .permit(TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS);

        /* from IN_PROGRESS */
        taskStateMachineConfig.configure(TaskStatus.IN_PROGRESS)
                .permit(TaskStatus.CANCELLED, TaskStatus.CANCELLED);

        taskStateMachineConfig.configure(TaskStatus.IN_PROGRESS)
                .permit(TaskStatus.COMPLETED, TaskStatus.COMPLETED);

        /* from completed */
        taskStateMachineConfig.configure(TaskStatus.COMPLETED)
                .permit(TaskStatus.CANCELLED, TaskStatus.CANCELLED);

        return taskStateMachineConfig;

    }
}
