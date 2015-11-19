# Task Manager

This is a library written for modelling business use cases into a Workflow with set of tasks. Provides generic CRUD
capabilities for task/task group. Particular set of tasks are mapped to a task group.

To use this library just include the following stuff in your project: -

* maven ```{ url 'http://artifactory.nm.flipkart.com:8081/artifactory/libs-snapshot' }``` in your build.gradle dependency repositories.

* Add compile dependency ```'com.fquick:task-manager:0.0.6-SNAPSHOT'``` in build.gradle.

* Start by adding the Entity Declarations to your persistence.xml. Task Manager uses TaskGroup, Task, TaskAttributes,
 Relation, Actor, Subject .
```
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" version="2.0">
    <persistence-unit name="fquick-spider" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
            <class>com.tasks.manager.db.model.entities.TaskGroup</class>
            <class>com.tasks.manager.db.model.entities.Task</class>
            <class>com.tasks.manager.db.model.entities.TaskAttributes</class>
            <class>com.tasks.manager.db.model.entities.Relation</class>
            <class>com.tasks.manager.db.model.entities.Actor</class>
            <class>com.tasks.manager.db.model.entities.Subject</class>
            <exclude-unlisted-classes>false</exclude-unlisted-classes>
    </persistence-unit>
</persistence>
```

* The library provides funtionalities to view the whole WF for a particular set of task group as a graph. For that
jgraphT ```{ url 'https://github.com/jgrapht/jgrapht' }``` library is used.

* For maintaining state transitions(life cycle) for the tasks stateless4j ```{ url 'https://github.com/oxo42/stateless4j' }```
 library is used.

* creating a Task Group:
```
TaskGroup taskGroup = new TaskGroup();
taskManagerServiceImpl.createTaskGroup(TaskGroup taskGroup)
```

* creating a Task:
```
Task task = new Task();
task.setType("Pick");
taskManagerServiceImpl.createTask(task, taskGroup.getId());
```

* For fetching the graph of a Task Group with Tasks and corresponding relation:
```
DirectedGraph<Task, TaskGraphEdge> graph = taskManagerServiceImpl.getTaskGraphForTaskGroup(taskGroup.getId());
```


