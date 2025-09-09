package org.nudtopt.classicproblems.jobshopscheduling.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.List;

public class JobShop extends Solution {

    @DecisionEntityList
    private List<Operation> operationList;

    private List<Job> jobList;

    private List<Machine> machineList;

    // getter & setter
    public List<Operation> getOperationList() {
        return operationList;
    }
    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    public List<Job> getJobList() {
        return jobList;
    }
    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<Machine> getMachineList() {
        return machineList;
    }
    public void setMachineList(List<Machine> machineList) {
        this.machineList = machineList;
    }

}
