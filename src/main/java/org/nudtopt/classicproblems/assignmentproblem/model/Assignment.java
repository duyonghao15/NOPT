package org.nudtopt.classicproblems.assignmentproblem.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class Assignment extends Solution {

    @DecisionEntityList
    private List<Task> taskList = new ArrayList<>();

    private List<Resource> resourceList = new ArrayList<>();

    // getter & setter
    public List<Task> getTaskList() {
        return taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }
    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

}
