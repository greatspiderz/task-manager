package com.tasks.manager.util;

import com.google.inject.Provider;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.enums.TaskTriggerEnum;

/**
 * Created by divya.rai on 05/11/15.
 */
public class StateMachineProvider implements Provider<StateMachineConfig> {

    @Override
    public StateMachineConfig get() {
        StateMachineConfig<TaskStatus, TaskTriggerEnum> taskStateMachineConfig = new StateMachineConfig<>();

        taskStateMachineConfig.configure(TaskStatus.NEW)
                .permit(TaskTriggerEnum.CANCEL, TaskStatus.CANCELLED);

        taskStateMachineConfig.configure(TaskStatus.NEW)
                .permit(TaskTriggerEnum.IN_PROGRESS, TaskStatus.IN_PROGRESS);

        /* from IN_PROGRESS */
        taskStateMachineConfig.configure(TaskStatus.IN_PROGRESS)
                .permit(TaskTriggerEnum.CANCEL, TaskStatus.CANCELLED);

        taskStateMachineConfig.configure(TaskStatus.IN_PROGRESS)
                .permit(TaskTriggerEnum.COMPLETE, TaskStatus.COMPLETED);

        /* from completed */
        taskStateMachineConfig.configure(TaskStatus.COMPLETED)
                .permit(TaskTriggerEnum.CANCEL, TaskStatus.CANCELLED);

        return taskStateMachineConfig;

    }
}
