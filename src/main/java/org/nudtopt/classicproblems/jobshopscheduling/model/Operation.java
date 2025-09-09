package org.nudtopt.classicproblems.jobshopscheduling.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Operation extends DecisionEntity {

    private Job job;                                                // 所属工件
    private Operation preOperation;                                 // 前置工序 (必须在本工序前完成)
    private long beginTime;                                         // 加工开始时间
    private long endTime;                                           // 加工结束时间

    @DecisionVariable
    private Machine machine;                                        // 所选工序
    @DecisionVariable
    private Integer priority;                                       // 优先级

    // decision variable range
    private List<Machine> optionalMachineList  = new ArrayList<>(); // 可选机器
    private List<Integer> optionalPriorityList = new ArrayList<>(); // 可选优先级

    // getter & setter
    public Job getJob() {
        return job;
    }
    public void setJob(Job job) {
        this.job = job;
    }

    public Operation getPreOperation() {
        return preOperation;
    }
    public void setPreOperation(Operation preOperation) {
        this.preOperation = preOperation;
    }

    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Machine getMachine() {
        return machine;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<Machine> getOptionalMachineList() {
        return optionalMachineList;
    }
    public void setOptionalMachineList(List<Machine> optionalMachineList) {
        this.optionalMachineList = optionalMachineList;
    }

    public List<Integer> getOptionalPriorityList() {
        return optionalPriorityList;
    }
    public void setOptionalPriorityList(List<Integer> optionalPriorityList) {
        this.optionalPriorityList = optionalPriorityList;
    }

    @Override
    public String toString() {
        return index != null ? index : super.toString();
    }


    /**
     * 基于分配的机器情况, 获取加工时间
     * @return 加工时间
     */
    public long getProcessTime() {
        return machine == null? 0 : machine.getProcessTime(this);
    }


/* class ends */
}
