package org.nudtopt.realworldproblems.apiforsatellite.resource.platform;

import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.realworldproblems.apiforsatellite.resource.Resource;
import org.nudtopt.realworldproblems.apiforsatellite.task.Task;

import java.util.ArrayList;
import java.util.List;

public class Platform extends Resource {

    private List taskList = new ArrayList<>();              // 该平台(可)执行的任务

    // getter & setter
    public <T extends Task> List<T> getTaskList() {
        return taskList;
    }
    public <T extends Task> void setTaskList(List<T> taskList) {
        this.taskList = taskList;
    }


    @Override
    public String toString() {
        if(name != null)        return name;
        else                    return super.toString();
    }

/* class ends */
}
