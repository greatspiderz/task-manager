package com.tasks.manager.util;

import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.dto.ActorDto;
import com.tasks.manager.dto.SubjectDto;
import com.tasks.manager.dto.TaskAttributeDto;
import com.tasks.manager.dto.TaskEvent;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by shlok.chaurasia on 28/11/15.
 */
public class EventUtils {
    public static TaskEvent getTaskEvent(Task task, Set<Subject> subjects){
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setTaskType(task.getType());
        taskEvent.setTaskId(task.getId());
        taskEvent.setActor(new ActorDto(task.getActor()));
        taskEvent.setStatus(task.getStatus());
        List<TaskAttributeDto> taskAttributeDtos = new ArrayList<>();
        for(TaskAttributes taskAttributes : task.getTaskAttributes())
            taskAttributeDtos.add(new TaskAttributeDto(taskAttributes));
        taskEvent.setAttributes(taskAttributeDtos);
        taskEvent.setEventDate(DateTime.now());
        List<SubjectDto> subjectDtos = new ArrayList<>();
        for(Subject subject : subjects)
            subjectDtos.add(new SubjectDto(subject));
        taskEvent.setSubjects(subjectDtos);
        return taskEvent;
    }
}
