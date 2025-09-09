package org.nudtopt.classicproblems.jobshopscheduling.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.classicproblems.jobshopscheduling.model.JobShop;
import org.nudtopt.classicproblems.jobshopscheduling.model.Machine;
import org.nudtopt.classicproblems.jobshopscheduling.model.Operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Decoder extends Constraint {

    @Override
    public Score calScore() {
        JobShop jobShop = (JobShop) solution;
        Set<Operation> operationSet = new HashSet<>();
        List<Operation> operationList = new ArrayList<>(jobShop.getOperationList());
        List<Machine> machineList = jobShop.getMachineList();
        operationList.sort(new PriorityComparator());                                   // 工序按决策变量(解码顺序)排序
        machineList.forEach(machine -> machine.getOperationList().clear());             // 机器上的工序清空
        long makespan = 0;     // 最大完工时间
        // 根据工序优先级, 依次顶前安置开始时间
        while(operationList.size() > 0) {
            Operation operation = operationList.get(0);                                 // 找到当前需安置的工序
            while(operation.getPreOperation() != null && !operationSet.contains(operation.getPreOperation())) {
                operation = operation.getPreOperation();                                // 若存在前置工序, 且未安置, 需优先前置工序
            }
            Machine machine = operation.getMachine();
            long process = operation.getProcessTime();
            long beginTime = 0;
            // a. 若机器已安置工序, 则紧接着安排
            if(machine.getOperationList().size() > 0) {
                Operation lastOperation = machine.getOperationList().get(machine.getOperationList().size() - 1);
                beginTime = lastOperation.getEndTime();
            }
            // b. 但若存在前置工序, 需等待前置工序完成
            if(operation.getPreOperation() != null) {
                beginTime = Math.max(beginTime, operation.getPreOperation().getEndTime());
            }
            long endTime = beginTime + process;
            operation.setBeginTime(beginTime);
            operation.setEndTime(endTime);
            makespan = Math.max(makespan, endTime);
            machine.getOperationList().add(operation);
            operationList.remove(operation);                                            // 移除已安置的工序
            operationSet.add(operation);                                                // 存入已安置工序集
        }
        Score score = new Score();
        score.setMeanScore(- makespan);
        return score;
    }


    /**
     * 检查已安排的工序, 是否违反工序顺序邀请
     * 以及机器上的工序是否存在重叠
     */
    public boolean check(JobShop jobShop) {
        // 1. 检查工序顺序约束
        for(Operation operation : jobShop.getOperationList()) {
            Operation preOperation = operation.getPreOperation();
            if(preOperation != null) {
                if(preOperation.getEndTime() > operation.getBeginTime()) {
                    return false;
                }
            }
        }
        // 2. 检查机器上是否存在工序重叠
        for(Machine machine : jobShop.getMachineList()) {
            for(int i = 0 ; i < machine.getOperationList().size() - 1 ; i ++) {
                Operation operation_1 = machine.getOperationList().get(i);
                Operation operation_2 = machine.getOperationList().get(i + 1);
                if(operation_1.getEndTime() > operation_2.getBeginTime()) {
                    return false;
                }
            }
        }
        return true;
    }


}
