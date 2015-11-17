package com.tasks.manager.dto;

import com.tasks.manager.db.model.entities.Task;

import org.jgrapht.graph.DefaultEdge;


/**
 * Created by shlok.chaurasia on 17/11/15.
 */
public class TaskGraphEdge extends DefaultEdge {

    @Override
    public Task getSource()
    {
        return (Task)super.getSource();
    }

    @Override
    public Task getTarget()
    {
        return (Task)super.getTarget();
    }

}
