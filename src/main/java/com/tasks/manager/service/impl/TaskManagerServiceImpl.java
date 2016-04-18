package com.tasks.manager.service.impl;

import com.google.inject.Inject;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.tasks.manager.db.dao.interfaces.ActorDao;
import com.tasks.manager.db.dao.interfaces.RelationDao;
import com.tasks.manager.db.dao.interfaces.SubjectDao;
import com.tasks.manager.db.dao.interfaces.TaskDao;
import com.tasks.manager.db.dao.interfaces.TaskGroupDao;
import com.tasks.manager.db.dao.interfaces.TaskSubjectRelationDao;
import com.tasks.manager.db.exception.IllegalTaskStateTransitionException;
import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.Actor;
import com.tasks.manager.db.model.entities.Relation;
import com.tasks.manager.db.model.entities.Subject;
import com.tasks.manager.db.model.entities.Task;
import com.tasks.manager.db.model.entities.TaskAttributes;
import com.tasks.manager.db.model.entities.TaskGroup;
import com.tasks.manager.db.model.entities.TaskSubjectRelation;
import com.tasks.manager.dto.AddRelationDto;
import com.tasks.manager.dto.AddSubjectsDto;
import com.tasks.manager.dto.CreateTaskDto;
import com.tasks.manager.dto.SearchActorDto;
import com.tasks.manager.dto.SearchSubjectDto;
import com.tasks.manager.dto.SearchTaskDto;
import com.tasks.manager.dto.SearchTaskGroupDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.dto.UpdateRelationDto;
import com.tasks.manager.dto.UpdateSubjectDto;
import com.tasks.manager.dto.UpdateTaskDto;
import com.tasks.manager.enums.TaskStatusEnum;
import com.tasks.manager.enums.TaskTriggerEnum;
import com.tasks.manager.service.api.EventPublisher;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.util.StateMachineProvider;

import org.apache.commons.collections.ListUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by palash.v on 17/02/16.
 */
@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

    private final TaskDao taskDao;
    private final TaskGroupDao taskGroupDao;
    private final RelationDao relationDao;
    private final SubjectDao subjectDao;
    private final ActorDao actorDao;
    private final TaskSubjectRelationDao taskSubjectRelationDao;
    private final StateMachineConfig taskStateMachineConfig;
    private final EventPublisher eventPublisher;

    @Inject
    public TaskManagerServiceImpl(TaskDao taskDao,
                                  TaskGroupDao taskGroupDao,
                                  RelationDao relationDao,
                                  SubjectDao subjectDao,
                                  ActorDao actorDao,
                                  TaskSubjectRelationDao taskSubjectRelationDao,
                                  StateMachineProvider stateMachineProvider,
                                  EventPublisher eventPublisher) {
        this.taskDao = taskDao;
        this.taskGroupDao = taskGroupDao;
        this.relationDao = relationDao;
        this.subjectDao = subjectDao;
        this.actorDao = actorDao;
        this.taskSubjectRelationDao = taskSubjectRelationDao;
        this.eventPublisher = eventPublisher;
        this.taskStateMachineConfig = stateMachineProvider.get();
    }

    @Override
    public List<Task> createTasks(List<CreateTaskDto> taskCreateInput) {

        List<Task> tasks = new ArrayList<>();

        for (CreateTaskDto createTaskDto : taskCreateInput) {
            Set<Relation> relations = new HashSet<>();
            log.info("Creating new Task");
            Task inputTask = createTaskDto.getTask();

            TaskGroup taskGroup = createTaskDto.getTaskGroup();
            if (taskGroup == null) {
                taskGroup = new TaskGroup();
                taskGroupDao.save(taskGroup);
            }
            else if(taskGroup.getId() == null){
                taskGroupDao.save(taskGroup);
            }
            Relation relation = new Relation();
            relation.setTask(inputTask);
            relation.setTaskGroup(taskGroup);
            relations.add(relation);

            if (inputTask.getRelations() == null ) {
                inputTask.setRelations(new ArrayList<>());
            }
            if (taskGroup.getRelations() == null ) {
                taskGroup.setRelations(new ArrayList<>());
            }
            inputTask.getRelations().addAll(relations);
            taskGroup.getRelations().addAll(relations);
            tasks.add(inputTask);

            eventPublisher.publishTaskCreationEvent(inputTask);
        }

        taskDao.bulkInsert(tasks);
        return tasks;
    }

    /**
     * Update Tasks in Bulk
     */
    @Override
    public List<Task> updateTasks(List<UpdateTaskDto> taskUpdateInput) throws TaskNotFoundException, IllegalTaskStateTransitionException {
        List<Task> updatedTasks = new ArrayList<>();
        for (UpdateTaskDto updateTaskDto : taskUpdateInput) {
            Task currentTask = taskDao.fetchById(updateTaskDto.getTaskId());
            if (currentTask != null) {
                log.info("Updating Task " + currentTask.getId());
                if (updateTaskDto.getActor() != null) {
                    Actor oldActor = currentTask.getActor();
                    Actor actor = updateTaskDto.getActor();
                    log.info("New actor: " + actor.toString());
                    actor.getAssociatedTasks().add(currentTask);
                    if (actor.getId() == null) {
                        actorDao.save(actor);
                    }
                    currentTask.setActor(actor);
                    eventPublisher.publishActorAssignmentEvent(currentTask, oldActor);
                }
                if (updateTaskDto.getTaskAttributes() != null && updateTaskDto.getTaskAttributes().size() > 0) {
                    List<TaskAttributes> oldAttributes = new ArrayList<>(currentTask.getTaskAttributes());
                    for (TaskAttributes taskAttributes : currentTask.getTaskAttributes()) {
                        taskAttributes.setTask(null);
                    }
                    currentTask.setTaskAttributes(updateTaskDto.getTaskAttributes());
                    log.info("New TaskAttributes: " + updateTaskDto.getTaskAttributes().toString());
                    for (TaskAttributes taskAttributes : currentTask.getTaskAttributes()) {
                        taskAttributes.setTask(currentTask);
                    }
                    eventPublisher.publishTaskAttributeChangeEvent(currentTask, oldAttributes);
                }
                if (updateTaskDto.getTaskTriggerEnum() != null) {
                    TaskStatusEnum oldStatus = currentTask.getStatus();
                    log.info("New TaskTrigger: " + updateTaskDto.getTaskTriggerEnum());
                    TaskStatusEnum newStatus = updateTaskStateMachine(currentTask, updateTaskDto.getTaskTriggerEnum());
                    currentTask.setStatus(newStatus);
                    eventPublisher.publishTaskStatusChangeEvent(currentTask, oldStatus);
                }
                if (updateTaskDto.getType() != null) {
                    String oldType = currentTask.getType();
                    log.info("New TaskType: " + updateTaskDto.getType());
                    currentTask.setType(updateTaskDto.getType());
                    eventPublisher.publishTaskTypeChangeEvent(currentTask, oldType);
                }
                if (updateTaskDto.getTaskGroupId() != null) {
                    TaskGroup oldTaskGroup = currentTask.getRelations().get(0).getTaskGroup();
                    List<Long> oldParentIds = currentTask.getRelations().stream().map(r -> r.getParentTaskId()).collect(Collectors.toList());
                    log.info("New TaskGroup: " + updateTaskDto.getTaskGroupId());
                    currentTask.getRelations().forEach(relation -> relation.setTaskGroup(taskGroupDao.fetchById(updateTaskDto.getTaskGroupId())));
                    eventPublisher.publishTaskGroupChangeEvent(currentTask, oldTaskGroup);
                    eventPublisher.publishTaskRelationChangeEvent(currentTask, oldParentIds);
                }
            }
            else {
                throw new TaskNotFoundException(updateTaskDto.getTaskId());
            }
            updatedTasks.add(currentTask);
        }
        return updatedTasks;
    }

    private TaskStatusEnum updateTaskStateMachine(Task task, TaskTriggerEnum trigger) throws IllegalTaskStateTransitionException {
        try {
            log.info("updating status of task " + task.getId() + " with trigger " + trigger);
            StateMachine<TaskStatusEnum, TaskTriggerEnum> stateMachine = new StateMachine(task.getStatus(), taskStateMachineConfig);
            stateMachine.fire(trigger);
            return stateMachine.getState();
        } catch (Exception e) {
            throw new IllegalTaskStateTransitionException(task.getId(), task.getStatus(), trigger);
        }
    }

    @Override
    public List<Task> updateRelations(List<UpdateRelationDto> relationUpdateInput) {
        if (relationUpdateInput != null) {
            log.info("Updating Relations for given input params ", relationUpdateInput.toString());
            List<Task> tasks = new ArrayList<>();
            for (UpdateRelationDto updateRelationDto : relationUpdateInput) {
                Task task = updateRelationDto.getTask();
                tasks.add(task);
                for (Relation relation : task.getRelations()) {
                    if (relation.getParentTaskId() == null || relation.getParentTaskId().equals(updateRelationDto.getOldParentId())) {
                        relation.setParentTaskId(updateRelationDto.getNewParentId());
                    }
                }
            }
            return tasks;
        }
        return null;
    }

    /**
     * Add more Subjects to a Task in Bulk
     */
    @Override
    public List<Task> addSubjects(List<AddSubjectsDto> addSubjectInput) {
        List<Task> updatedTasks = new ArrayList<>();
        for (AddSubjectsDto addSubjectsDto : addSubjectInput) {
            Task currentTask = addSubjectsDto.getTask();
            if (currentTask != null) {
                List<Subject> subjectsToAdd = addSubjectsDto.getSubjectsToAdd();
                log.info("Adding Subjects to Task " + currentTask.getId() + " : " + subjectsToAdd);
                if (subjectsToAdd != null && subjectsToAdd.size() > 0) {
                    List<TaskSubjectRelation> newTaskSubjectRelations = new ArrayList<>();
                    for (Subject subject : subjectsToAdd) {
                        TaskSubjectRelation taskSubjectRelation = new TaskSubjectRelation();
                        taskSubjectRelation.setSubject(subject);
                        taskSubjectRelation.setTask(currentTask);
                        if (subject.getTaskSubjectRelations() == null) {
                            subject.setTaskSubjectRelations(new ArrayList<>());
                        }
                        subject.getTaskSubjectRelations().add(taskSubjectRelation);
                        newTaskSubjectRelations.add(taskSubjectRelation);
                    }
                    currentTask.getTaskSubjectRelations().addAll(newTaskSubjectRelations);
                }
            }
            updatedTasks.add(currentTask);
        }
        return updatedTasks;
    }

    /**
     * Search Actors
     */
    @Override
    public List<Actor> getActors(SearchActorDto searchActorDto) {

        List<Actor> actorsForActorAttributes = null;
        if (searchActorDto.getTypes() != null || searchActorDto.getExternalIds() != null) {
            log.info("Searching Actors for " + searchActorDto.getTypes() + " & " + searchActorDto.getExternalIds());
            actorsForActorAttributes = actorDao.searchActors(searchActorDto);
        }

        List<Actor> actorsForTaskGroup = null;
        if (searchActorDto.getTaskGroupID() != null) {
            log.info("Searching Actors for TaskGroup " + searchActorDto.getTaskGroupID());
            actorsForTaskGroup = relationDao.fetchByTaskGroupId(searchActorDto.getTaskGroupID())
                    .stream()
                    .map(relation -> relation.getTask().getActor())
                    .collect(Collectors.toList());
        }

        if (actorsForActorAttributes != null && actorsForTaskGroup != null) {
            return ListUtils.intersection(actorsForActorAttributes, actorsForTaskGroup);
        }
        else if (actorsForActorAttributes != null) {
            return actorsForActorAttributes;
        }
        else {
            return actorsForTaskGroup;
        }
    }

    @Override
    public List<Subject> getSubjects(SearchSubjectDto searchSubjectDto) {
        if (searchSubjectDto != null) {
            log.info("Searching subjects for input param ", searchSubjectDto.toString());
            return subjectDao.searchSubjects(searchSubjectDto);
        }
        return null;
    }

    @Override
    public TaskGroup getTaskGroup(SearchTaskGroupDto searchTaskGroupDto) {
        log.info("Searching taskgroup for ", searchTaskGroupDto);

        if (searchTaskGroupDto.getTaskId() != null && searchTaskGroupDto.getTaskGroupId() != null) {

            TaskGroup taskGroupForTaskGroupId = getTaskGroupForTaskGroupId(searchTaskGroupDto.getTaskGroupId());
            TaskGroup taskGroupForTaskId = getTaskGroupForTaskId(searchTaskGroupDto.getTaskId());

            if (taskGroupForTaskGroupId != null && taskGroupForTaskId != null) {
                if (taskGroupForTaskGroupId.equals(taskGroupForTaskId)) {
                    return taskGroupForTaskGroupId;
                }
                else {
                    log.info("No TaskGroup found to match both TaskId {} and TaskGroupId {}", searchTaskGroupDto.getTaskId(), searchTaskGroupDto.getTaskGroupId());
                    return null;
                }
            }
            else {
                return null;
            }
        }
        else if (searchTaskGroupDto.getTaskGroupId() != null) {
            return getTaskGroupForTaskGroupId(searchTaskGroupDto.getTaskGroupId());
        }
        else {
            return getTaskGroupForTaskId(searchTaskGroupDto.getTaskId());
        }
    }

    private TaskGroup getTaskGroupForTaskId(Long taskId) {
        log.info("Searching TaskGroup for TaskId: " + taskId);
        try {
            return taskDao.fetchById(taskId).getRelations().stream().map(relation -> relation.getTaskGroup()).collect(Collectors.toList()).get(0);
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private TaskGroup getTaskGroupForTaskGroupId(Long taskGroupId) {
        log.info("Searching TaskGroup for TaskGroupId: " + taskGroupId);
        return taskGroupDao.fetchById(taskGroupId);
    }

    @Override
    public DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId) {
        log.info("Geeting task graph for task group id ", taskGroupId);
        DirectedGraph<Task, TaskGraphEdge> taskGraph = new DefaultDirectedGraph<>(TaskGraphEdge.class);
        List<Relation> relations = taskGroupDao.fetchById(taskGroupId).getRelations();
        Map<Long, Task> taskMap = new HashMap<>();
        Map<Long, Set<Long>> parentTasksMap = new HashMap<>();

        for (Relation relation : relations) {
            Task task = relation.getTask();
            taskMap.put(task.getId(), task);
            if(relation.getParentTaskId()!=null){
                Set<Long> parentTasks = parentTasksMap.get(task.getId());
                if (parentTasks != null) {
                    parentTasks.add(relation.getParentTaskId());
                    parentTasksMap.put(task.getId(), parentTasks);
                }
                else {
                    Set<Long> parentTask = new HashSet<>();
                    parentTask.add(relation.getParentTaskId());
                    parentTasksMap.put(task.getId(), parentTask);
                }
            }
        }
        List<Long> taskIdsAddedToGraph = new ArrayList<>();
        for(Task task : taskMap.values()){
            if (!taskIdsAddedToGraph.contains(task.getId())) {
                taskGraph.addVertex(task);
                taskIdsAddedToGraph.add(task.getId());
            }
            if(parentTasksMap.get(task.getId())!=null){
                Set<Long> parentTasks = parentTasksMap.get(task.getId());
                for(Long parentTaskId : parentTasks){
                    Task parentTask = taskMap.get(parentTaskId);
                    if (!taskIdsAddedToGraph.contains(parentTaskId)) {
                        taskGraph.addVertex(parentTask);
                        taskIdsAddedToGraph.add(parentTaskId);
                    }
                    taskGraph.addEdge(parentTask, task);
                }
            }
        }
        return taskGraph;
    }

    @Override
    public List<Task> updateSubjects(List<UpdateSubjectDto> updateSubjectInput) {
        log.info("Updating subjects ");
        List<Task> tasks = new ArrayList<>();
        for(UpdateSubjectDto updateSubjectDto : updateSubjectInput){
            log.info("Updating subject for task ", updateSubjectDto.getTask().getId() + "new subject " + updateSubjectDto.getNewSubject());
            if(updateSubjectDto.getTask()!=null && updateSubjectDto.getOldSubject()!=null){
                tasks.add(updateSubjectDto.getTask());
                taskSubjectRelationDao.updateSubject(updateSubjectDto);
            }
        }
        SearchTaskDto searchTaskDto = new SearchTaskDto();
        searchTaskDto.setTaskIds(tasks.stream().map(Task::getId).collect(Collectors.toList()));
        List<Task> resultTasks = taskDao.search(searchTaskDto);
        return resultTasks;
    }

    @Override
    public List<Task> getTasks(SearchTaskDto searchTaskDto) {
        return taskDao.search(searchTaskDto);
    }

    @Override
    public List<Task> addRelations(List<AddRelationDto> addRelationInput) {
        log.info("Addign relations");
        List<Task> tasks = new ArrayList<>();
        Iterator<AddRelationDto> iterator = addRelationInput.iterator();
        while(iterator.hasNext()){
            AddRelationDto addRelationDto = iterator.next();
            Task task = addRelationDto.getTask();
            if(task!=null){
                SearchTaskGroupDto searchTaskGroupDto = new SearchTaskGroupDto();
                searchTaskGroupDto.setTaskId(task.getId());
                TaskGroup taskGroup = getTaskGroup(searchTaskGroupDto);
                if(taskGroup!=null){
                    log.info("Adding relations in taskgroup " + taskGroup + "for task " + task);
                    List<Long> parentsTaskIds = addRelationDto.getParentIds();
                    List<Relation> relationsToAdd = new ArrayList<>();
                    for(Long parentTaskId : parentsTaskIds){
                        Relation relation = new Relation();
                        relation.setTask(task);
                        relation.setTaskGroup(taskGroup);
                        relation.setParentTaskId(parentTaskId);
                        task.getRelations().add(relation);
                        taskGroup.getRelations().add(relation);
                        relationsToAdd.add(relation);
                    }
                    relationDao.bulkInsert(relationsToAdd);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

}
