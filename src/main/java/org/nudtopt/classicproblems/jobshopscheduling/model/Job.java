package org.nudtopt.classicproblems.jobshopscheduling.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.List;

public class Job extends NumberedObject {

    private List<Operation> operationList = new ArrayList<>();

    // getter & setter
    public List<Operation> getOperationList() {
        return operationList;
    }
    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }
}
