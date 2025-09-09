package org.nudtopt.classicproblems.jobshopscheduling.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Machine extends NumberedObject {

    private Map<Operation, Long> processTimeMap = new HashMap<>();              // 该机器处理不同工序的加工时间

    private List<Operation> operationList = new ArrayList<>();                  // 该机器分配的工序(含顺序)

    // getter & setter
    public Map<Operation, Long> getProcessTimeMap() {
        return processTimeMap;
    }
    public void setProcessTimeMap(Map<Operation, Long> processTimeMap) {
        this.processTimeMap = processTimeMap;
    }

    public List<Operation> getOperationList() {
        return operationList;
    }
    public void setOperationList(List<Operation> operationList) {
        this.operationList = operationList;
    }

    /**
     * 获取某工序在该机器上的加工时间
     * @param operation 工序
     * @return          加工时间
     */
    public long getProcessTime(Operation operation) {
        Long processTime = processTimeMap.get(operation);
        return processTime == null ? 0L : processTime;
    }


}
