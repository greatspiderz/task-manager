package com.tasks.manager.service.impl;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.inject.Inject;
import com.tasks.manager.db.dao.interfaces.*;

import com.tasks.manager.db.exception.TaskNotFoundException;
import com.tasks.manager.db.model.entities.*;
import com.tasks.manager.db.model.enums.TaskStatus;
import com.tasks.manager.dto.SearchDto;
import com.tasks.manager.dto.TaskGraphEdge;
import com.tasks.manager.enums.TaskEventType;
import com.tasks.manager.enums.TaskTriggerEnum;
import com.tasks.manager.service.api.EventPublisher;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.util.StateMachineProvider;
import com.tasks.manager.util.EventUtils;
import com.tasks.manager.util.TaskManagerUtility;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.joda.time.DateTime;


/**
 * Created by divya.rai on 05/11/15.
 */
@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

    private final TaskDao taskDao;
    private final TaskGroupDao taskGroupDao;
    private final TaskAttributesDao taskAttributesDao;
    private final RelationDao relationDao;
    private final SubjectDao subjectDao;
    private final ActorDao actorDao;
    private final StateMachineConfig taskStateMachineConfig;
    private final EventPublisher eventPublisher;

    @Inject
    public TaskManagerServiceImpl(TaskDao taskDao,
                                  TaskGroupDao taskGroupDao,
                                  TaskAttributesDao taskAttributesDao,
                                  StateMachineProvider stateMachineProvider, RelationDao relationDao,
                                  SubjectDao subjectDao, ActorDao actorDao,
                                  EventPublisher eventPublisher
    ) {
        this.taskDao = taskDao;
        this.taskGroupDao = taskGroupDao;
        this.taskAttributesDao = taskAttributesDao;
        this.relationDao = relationDao;
        this.taskStateMachineConfig = stateMachineProvider.get();
        this.subjectDao = subjectDao;
        this.actorDao = actorDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TaskGroup createTaskGroup(TaskGroup taskgroup) {
        taskGroupDao.save(taskgroup);
        return taskgroup;
    }

    @Override
    public TaskGroup fetchTaskGroup(long tgId) {
        return taskGroupDao.fetchById(tgId);
    }

    @Override
    public Task createTask(Task task, long tgId) {
        TaskGroup taskGroup = taskGroupDao.fetchById(tgId);
        Relation relation = new Relation();
        taskDao.save(task);
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        if (task.getRelations() == null)
            task.setRelations(new ArrayList<>());
        if (taskGroup.getRelations() == null)
            taskGroup.setRelations(new ArrayList<>());
        task.getRelations().add(relation);
        taskGroup.getRelations().add(relation);
        relationDao.save(relation);
        eventPublisher.publishTaskCreationEvent(task);
        return task;
    }


    @Override
    public Task createTaskWithParentTasks(Task task, long tgId, List<Long> parentTaskIds) {
        TaskGroup taskGroup = taskGroupDao.fetchById(tgId);
        taskDao.save(task);
        for (Long parentTaskId : parentTaskIds) {
            Relation relation = new Relation();
            relation.setTaskGroup(taskGroup);
            relation.setTask(task);
            relation.setParentTaskId(parentTaskId);
            if (task.getRelations() == null)
                task.setRelations(new ArrayList<>());
            if (taskGroup.getRelations() == null)
                taskGroup.setRelations(new ArrayList<>());
            task.getRelations().add(relation);
            taskGroup.getRelations().add(relation);
            relationDao.save(relation);
        }
        return task;
    }

    @Override
    public Task fetchTask(long taskId) {
        return taskDao.fetchById(taskId);
    }

    //Remove
    @Override
    public void updateActorStatus(Long actorId, String status) throws TaskNotFoundException {
        actorDao.updateActorStatus(actorId, status);
    }

    @Override
    public Actor createActor(Actor actor) {
        actorDao.save(actor);
        return actor;
    }

    @Override
    public Actor fetchActorByExternalId(String actorId) {
        List<Actor> actors = actorDao.fetchByExternalId(actorId);
        if(actors.size()==0)
            return null;
        return actors.get(0);
    }

    public void updateTaskActor(Long taskId, Actor actor) throws TaskNotFoundException {
        createActor(actor);
        Actor oldActor = taskDao.fetchById(taskId).getActor();
        taskDao.updateTaskActor(taskId, actor);
        Task task = taskDao.fetchById(taskId);
        actor.getAssociatedTasks().add(task);
        eventPublisher.publishActorAssignmentEvent(task, oldActor);
    }

    @Override
    public void updateStatus(Long taskId, TaskTriggerEnum trigger) throws TaskNotFoundException {
        Task task = fetchTask(taskId);
        TaskStatus newStatus = updateTaskStateMachine(task, trigger);
        TaskStatus fromTaskStatus = task.getStatus();
        taskDao.updateStatus(taskId, newStatus);
        task.setStatus(newStatus);
        eventPublisher.publishTaskStatusChangeEvent(task, fromTaskStatus);
    }

    private TaskStatus updateTaskStateMachine(Task task, TaskTriggerEnum trigger) {
        log.info("updating status of task " + task.getId() + " with trigger " + trigger);
        StateMachine<TaskStatus, TaskTriggerEnum> stateMachine = new StateMachine(task.getStatus(), taskStateMachineConfig);
        stateMachine.fire(trigger);
        return stateMachine.getState();
    }

    //Remove
    @Override
    public void updateETA(Long taskId, Long eta) throws TaskNotFoundException {
        taskDao.updateETA(taskId, eta);

    }

    @Override
    public List<Task> findTasks(SearchDto searchdto) {
        return taskDao.search(searchdto);
    }

    @Override
    public List<Task> getTasksForTaskGroup(Long taskGroupId) {
        Set<Task> tasks = new HashSet<>();
        List<Relation> relations = taskGroupDao.fetchById(taskGroupId).getRelations();
        for (Relation relation : relations) {
            tasks.add(relation.getTask());
        }
        return new ArrayList<>(tasks);
    }

    @Override
    public TaskGroup getTaskGroupForTask(Task task) {
        List<Relation> relations = task.getRelations();
        if (relations.size() > 0) {
            Relation relation = relations.get(0);
            return relation.getTaskGroup();
        }
        return null;
    }

    public List<Task> bulkInsertTasks(List<Task> tasks) {
        return taskDao.bulkInsert(tasks);
    }

    @Override
    public DirectedGraph<Task, TaskGraphEdge> getTaskGraphForTaskGroup(Long taskGroupId) {
        return getTaskGraphs(taskGroupId);
    }

    @Override
    public Task createRelation(Task task, TaskGroup taskGroup, Long parentTaskId) {
        Relation relation = new Relation();
        relation.setTaskGroup(taskGroup);
        relation.setTask(task);
        relation.setParentTaskId(parentTaskId);
        task.getRelations().add(relation);
        taskGroup.getRelations().add(relation);
        relationDao.save(relation);
        return task;
    }

    @Override
    public void updateParentTask(Task task, Long parentTaskId) {
        for (Relation relation : task.getRelations()) {
            if (relation.getParentTaskId() == null) {
                relation.setParentTaskId(parentTaskId);
                relationDao.save(relation);
                break;
            }
        }
    }

    @Override
    public List<Task> getActiveTasksforSubject(String externalId) {
        SearchDto searchDto = new SearchDto();
        Subject subject = new Subject();
        subject.setExternalId(externalId);
        searchDto.setSubject(subject);
        List<Task> tasks = taskDao.search(searchDto);
        return tasks.stream().filter(task -> TaskManagerUtility.isTaskActive(task.getStatus())).collect(Collectors.toList());
    }


    @Override
    public List<Task> getActiveTasksForActor(String actorExternalId){
        Actor actor = new Actor();
        actor.setExternalId(actorExternalId);
        SearchDto searchDto = new SearchDto();
        searchDto.setActors(Arrays.asList(actor));
        return taskDao.searchActiveTasksForActor(searchDto);
    }

    @Override
    public List<Task> getNextTasksForActor(String actorExternalId, Long completedTaskId)
    {
        List<Task> tasksForActor = new ArrayList<>();
        List<Long> childIds = new ArrayList<>();
        if(completedTaskId!=null)
        {
            List<Relation> relations = relationDao.fetchByParentTaskId(completedTaskId);

            for(Relation relation:relations)
            {
                Task taskForRelation = relation.getTask();
                Actor taskActor = taskForRelation.getActor();
                childIds.add(taskForRelation.getId());

                if(TaskManagerUtility.isTaskActive(taskForRelation.getStatus()) &&
                        taskActor!=null && taskActor.getExternalId().equals(actorExternalId)){
                    tasksForActor.add(taskForRelation);
                }
            }

        }
        if(tasksForActor.size()>0)
            return tasksForActor;
        else
        {
            List<Task> allChildTasksNextTask = new ArrayList<>();
            for(Long childId:childIds)
            {
                List<Task> childsNextTask = getNextTasksForActor(actorExternalId, childId);
                if(!allChildTasksNextTask.contains(childsNextTask))
                {
                    childsNextTask.removeAll(allChildTasksNextTask);
                    allChildTasksNextTask.addAll(childsNextTask);

                }
            }
            return allChildTasksNextTask;
        }
    }


    //Revisit
    @Override
    public void updateAllActiveTasksStatusInTaskGroup(TaskGroup taskGroup, TaskStatus taskStatus)
    {
        DirectedGraph<Task,TaskGraphEdge> taskGraph = getTaskGraph(taskGroup.getId());
        Set<Task> tasks = taskGraph.vertexSet();
        for(Task task : tasks){
            if(task.getStatus() != TaskStatus.CANCELLED)
            {
                task.setStatus(taskStatus);
                taskDao.save(task);
            }
        }
    }

    //Remove
    @Override
    public TaskGroup fetchTaskGroupBySubjectExternalId(String externalId){
        Subject subject =  subjectDao.fetchByExternalId(externalId);
        List<Task> tasks = taskDao.fetchBySubjectId(subject.getId());
        if(tasks.size()>0)
            return getTaskGroupForTask(tasks.get(0));
        return null;
    }

    //Remove
    @Override
    public Subject fetchSubjectByExternalId(String externalId){
        return subjectDao.fetchByExternalId(externalId);
    }

    @Override
    public void updateTaskAttribute(Task task, String attributeName, String attributeValue) {
        for(TaskAttributes attributes: task.getTaskAttributes()) {
            if (attributes.getAttributeName().equals(attributeName)) {
                attributes.setAttributeValue(attributeValue);
                taskDao.save(task);
                return;
            }
        }
        TaskAttributes newAttribute = new TaskAttributes();
        newAttribute.setAttributeName(attributeName);
        newAttribute.setAttributeValue(attributeValue);
        newAttribute.setTask(task);
        task.getTaskAttributes().add(newAttribute);
        taskDao.save(task);
    }

    @Override
    public List<Task> getAllTasks(List<Long> taskIds) {
        return taskDao.getAll(taskIds);
    }

    @Override
    public List<TaskGroup> findActiveTaskGroupsWithAttribute(String attributeName, String attributeValue){
        List<TaskAttributes> taskAttributes = taskAttributesDao.findTaskAttributes(attributeName, attributeValue);
        List<Task> tasks = new ArrayList<>();
        if(taskAttributes.size()>0){
            for(TaskAttributes taskAttr:taskAttributes){
                Task task = taskAttr.getTask();
                if(!tasks.contains(task) && task.getStatus()!=TaskStatus.CANCELLED ){
                    tasks.add(task);
                }
            }
        }
        Set<TaskGroup> taskGroups = new HashSet<>();
        for(Task taskForAttribute : tasks)
        {
            TaskGroup taskGrp = getTaskGroupForTask(taskForAttribute);
            if(!taskGroups.contains(taskGrp))
                taskGroups.add(taskGrp);
        }
        return new ArrayList<>(taskGroups);
    }

    private DirectedGraph<Task, TaskGraphEdge> getTaskGraphs(Long taskGrpId){
        log.info("Time for fetching the graph : " + DateTime.now());
        DirectedGraph<Task, TaskGraphEdge> taskGraph = new DefaultDirectedGraph<Task, TaskGraphEdge>(TaskGraphEdge.class);
        log.info("Time for fetching the relations : " + DateTime.now());
        List<Relation> relations = taskGroupDao.fetchById(taskGrpId).getRelations();
        log.info("Time for fetching the relations : " + DateTime.now());
        Map<Long, Task> taskMap = new HashMap<>();
        Map<Long, Set<Long>> parentTasksMap = new HashMap<>();
        for (Relation relation : relations) {
            log.info("Relation is " + relation.getId());
        }
        log.info("Forming the maps : " + DateTime.now());
        for (Relation relation : relations) {
            log.info("Forming the maps : " + DateTime.now());
            Task task = relation.getTask();
            log.info("Forming the maps : " + DateTime.now());
            taskMap.put(task.getId(), task);
            log.info("Forming the maps : " + DateTime.now());
            if(relation.getParentTaskId()!=null){
                log.info("Forming the maps : " + DateTime.now());
                Set<Long> parentTasks = parentTasksMap.get(task.getId());
                log.info("Forming the maps : " + DateTime.now());
                if(parentTasks!=null){
                    log.info("Forming the maps : " + DateTime.now());
                    parentTasks.add(relation.getParentTaskId());
                    log.info("Forming the maps : " + DateTime.now());
                    parentTasksMap.put(task.getId(), parentTasks);
                    log.info("Forming the maps : " + DateTime.now());
                }else{
                    log.info("Forming the maps : " + DateTime.now());
                    Set<Long> parentTask = new HashSet<>();
                    log.info("Forming the maps : " + DateTime.now());
                    parentTask.add(relation.getParentTaskId());
                    log.info("Forming the maps : " + DateTime.now());
                    parentTasksMap.put(task.getId(), parentTask);
                    log.info("Forming the maps : " + DateTime.now());
                }
            }
        }
        log.info("Forming the maps : " + DateTime.now());
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
                        log.info("adding the vertex: " + DateTime.now());
                        taskGraph.addVertex(parentTask);
                        log.info("adding the vertex: " + DateTime.now());
                        taskIdsAddedToGraph.add(parentTaskId);
                    }
                    log.info("adding the edge: " + DateTime.now());
                    taskGraph.addEdge(parentTask, task);
                    log.info("adding the egde: " + DateTime.now());
                }
            }
        }
        log.info("Forming the maps : " + DateTime.now());
        log.info("Time for fetching the graph : " + DateTime.now());
        return taskGraph;
    }

    private DirectedGraph<Task, TaskGraphEdge> getTaskGraph(Long taskGrpId) {
        log.info("Time for fetching the graph : " + DateTime.now());
        DirectedGraph<Task, TaskGraphEdge> taskGraph = new DefaultDirectedGraph<Task, TaskGraphEdge>(TaskGraphEdge.class);
        List<Relation> relations = taskGroupDao.fetchById(taskGrpId).getRelations();
        List<Task> tasks = new ArrayList<>();
        for (Relation relation : relations) {
            tasks.add(relation.getTask());
        }
        List<Long> taskIdsAddedToGraph = new ArrayList<>();
        for (Task task : tasks) {
            if (!taskIdsAddedToGraph.contains(task.getId())) {
                taskGraph.addVertex(task);
                taskIdsAddedToGraph.add(task.getId());
            }
            List<Task> parentTasks = getParentTasks(relations, tasks, task);
            for (Task parentTask : parentTasks) {
                if (!taskIdsAddedToGraph.contains(parentTask.getId())) {
                    taskGraph.addVertex(parentTask);
                    taskIdsAddedToGraph.add(parentTask.getId());
                }
                taskGraph.addEdge(parentTask, task);
            }

        }
        log.info("Time for fetching the graph : " + DateTime.now());
        return taskGraph;
    }

    private List<Task> getParentTasks(List<Relation> relations, List<Task> tasks, Task task) {
        List<Task> parentTasks = new ArrayList<>();
        for (Relation eachRelation : relations) {
            if (eachRelation.getTask().getId() == task.getId()) {
                Long parentTaskId = eachRelation.getParentTaskId();
                if (parentTaskId != null) {
                    for (Task eachTask : tasks) {
                        if (eachTask.getId().longValue() == parentTaskId) {
                            parentTasks.add(eachTask);
                            break;
                        }
                    }
                }
            }
        }
        return parentTasks;
    }
}
