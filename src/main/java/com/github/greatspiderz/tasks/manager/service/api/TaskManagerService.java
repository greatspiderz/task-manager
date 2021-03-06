package com.github.greatspiderz.tasks.manager.service.api;

import com.github.greatspiderz.tasks.manager.db.exception.IllegalTaskStateTransitionException;
import com.github.greatspiderz.tasks.manager.db.model.entities.Subject;
import com.github.greatspiderz.tasks.manager.db.model.entities.TaskGroup;
import com.github.greatspiderz.tasks.manager.dto.SearchActorDto;
import com.github.greatspiderz.tasks.manager.dto.SearchTaskGroupDto;
import com.github.greatspiderz.tasks.manager.service.impl.TaskManagerServiceImpl;
import com.google.inject.ImplementedBy;

import com.github.greatspiderz.tasks.manager.db.exception.TaskNotFoundException;
import com.github.greatspiderz.tasks.manager.db.model.entities.Actor;
import com.github.greatspiderz.tasks.manager.db.model.entities.Task;
import com.github.greatspiderz.tasks.manager.dto.AddRelationDto;
import com.github.greatspiderz.tasks.manager.dto.AddSubjectsDto;
import com.github.greatspiderz.tasks.manager.dto.CreateTaskDto;
import com.github.greatspiderz.tasks.manager.dto.SearchSubjectDto;
import com.github.greatspiderz.tasks.manager.dto.SearchTaskDto;
import com.github.greatspiderz.tasks.manager.dto.TaskGraphEdge;
import com.github.greatspiderz.tasks.manager.dto.UpdateRelationDto;
import com.github.greatspiderz.tasks.manager.dto.UpdateSubjectDto;
import com.github.greatspiderz.tasks.manager.dto.UpdateTaskDto;

import org.jgrapht.DirectedGraph;

import java.util.List;

/**
 * Created by palash.v on 17/02/16.
 */
@ImplementedBy(TaskManagerServiceImpl.class)
public interface TaskManagerService {

    /**
     * Retrieve tasks based on parameters
     *
     * @param searchTaskDto
     * @return
     */
    List<Task> getTasks(SearchTaskDto searchTaskDto);

    /**
     * Create Tasks in Bulk
     *
     * @param taskCreateInput
     * @return
     */
    List<Task> createTasks(List<CreateTaskDto> taskCreateInput);

    /**
     * Update Tasks in Bulk
     *
     * @param taskUpdateInput
     * @return
     */
    List<Task> updateTasks(List<UpdateTaskDto> taskUpdateInput) throws TaskNotFoundException, IllegalTaskStateTransitionException;

    /**
     * Add new Relations
     *
     * @param relationAddInput
     * @return
     */
    List<Task> addRelations(List<AddRelationDto> relationAddInput);

    /**
     * Replace existing Relations by new ones
     *
     * @param relationUpdateInput
     * @return
     */
    List<Task> updateRelations(List<UpdateRelationDto> relationUpdateInput);

    /**
     * Search Subjects
     *
     * @param searchSubjectDto
     * @return
     */
    List<Subject> getSubjects(SearchSubjectDto searchSubjectDto);

    /**
     * Add more Subjects to a Task in Bulk
     *
     * @param addSubjectInput
     * @return
     */
    List<Task> addSubjects(List<AddSubjectsDto> addSubjectInput);

    /**
     * Updates the subject for the given list of tasks
     *
     * @param udpateSubjectInput
     * @return
     */
    List<Task> updateSubjects(List<UpdateSubjectDto> udpateSubjectInput);

    /**
     * Search Actors
     *
     * @param searchActorDto
     * @return
     */
    List<Actor> getActors(SearchActorDto searchActorDto);

    /**
     * Get Task Group for task
     * @param searchTaskGroupDto
     * @return
     */
    TaskGroup getTaskGroup(SearchTaskGroupDto searchTaskGroupDto);

    /**
     * Get the task graph for the task group
     * @param taskGroupId
     * @return
     */
    DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId);

}
