package com.github.greatspiderz.tasks.manager.util;

import com.google.inject.Provider;

import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.greatspiderz.tasks.manager.enums.TaskStatusEnum;
import com.github.greatspiderz.tasks.manager.enums.TaskTriggerEnum;

/**
 * Created by divya.rai on 05/11/15.
 */
public class StateMachineProvider implements Provider<StateMachineConfig> {

    @Override
    public StateMachineConfig get() {
        StateMachineConfig<TaskStatusEnum, TaskTriggerEnum> taskStateMachineConfig = new StateMachineConfig<>();

        taskStateMachineConfig.configure(TaskStatusEnum.NEW)
                .permit(TaskTriggerEnum.CANCEL, TaskStatusEnum.CANCELLED);

        taskStateMachineConfig.configure(TaskStatusEnum.NEW)
                .permit(TaskTriggerEnum.IN_PROGRESS, TaskStatusEnum.IN_PROGRESS);

        /* from IN_PROGRESS */
        taskStateMachineConfig.configure(TaskStatusEnum.IN_PROGRESS)
                .permit(TaskTriggerEnum.CANCEL, TaskStatusEnum.CANCELLED);

        taskStateMachineConfig.configure(TaskStatusEnum.IN_PROGRESS)
                .permit(TaskTriggerEnum.COMPLETE, TaskStatusEnum.COMPLETED);

        taskStateMachineConfig.configure(TaskStatusEnum.IN_PROGRESS)
                .permit(TaskTriggerEnum.IN_COMPLETE, TaskStatusEnum.INCOMPLETED);

        taskStateMachineConfig.configure(TaskStatusEnum.NEW)
                .permit(TaskTriggerEnum.NON_EXECUTABLE, TaskStatusEnum.NON_EXECUTABLE);


        /* from completed */
        taskStateMachineConfig.configure(TaskStatusEnum.COMPLETED)
                .permit(TaskTriggerEnum.CANCEL, TaskStatusEnum.CANCELLED);
        taskStateMachineConfig.configure(TaskStatusEnum.COMPLETED)
                .ignore(TaskTriggerEnum.COMPLETE);

        /* from incompleted */
        taskStateMachineConfig.configure(TaskStatusEnum.INCOMPLETED)
                .ignore(TaskTriggerEnum.IN_COMPLETE);
        taskStateMachineConfig.configure(TaskStatusEnum.INCOMPLETED)
                .permit(TaskTriggerEnum.COMPLETE, TaskStatusEnum.COMPLETED);

        return taskStateMachineConfig;

    }
}
