package org.nudtopt.classicproblems.assignmentproblem.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.assignmentproblem.model.Assignment;
import org.nudtopt.classicproblems.assignmentproblem.model.Task;

import java.util.List;

public class Check extends Constraint {


    @Override
    public Score calScore() {
        Assignment assignment = (Assignment) solution;
        List<Task> taskList = assignment.getTaskList();
        long totalCost = 0;
        long conflict  = 0;
        for(int i = 0 ; i < taskList.size() ; i ++) {
            Task task = taskList.get(i);
            long cost = task.getCost();
            totalCost += cost;
            /*for(int j = i + 1 ; j < taskList.size() ; j ++) {
                if(task.getResource() == taskList.get(j).getResource()) {   // 两个任务指派给了同一个资源, 则冲突
                    conflict ++;                                            // todo 基于本引擎交换算子, 理论上不可能产生冲突, 故可不判此约束进而提速
                }
            }*/
        }
        Score score = new Score();
        score.setHardScore(-conflict);                                      // 约束: 冲突数
        score.setMeanScore(-totalCost);                                     // 收益: 成本最低
        return score;
    }


}
