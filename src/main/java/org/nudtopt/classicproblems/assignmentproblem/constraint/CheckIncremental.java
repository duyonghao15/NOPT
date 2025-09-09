package org.nudtopt.classicproblems.assignmentproblem.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.classicproblems.assignmentproblem.model.Assignment;
import org.nudtopt.classicproblems.assignmentproblem.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckIncremental extends Check {


    private Map<Task, Long> taskCostMap = new HashMap<>();                      // 记录当前任务-资源指派成本, 在每一次迭代的时候增量更新


    @Override
    public Score calScore() {
        Assignment assignment = (Assignment) solution;
        // 1. 初始化情况
        if(operator == null) {
            Score score = super.calScore();
            assignment.getTaskList().forEach(task -> taskCostMap.put(task, task.getCost()));
            return score;
        }

        // 2. 迭代过程中增量式计算分差
        long changedCost = 0;
        for(DecisionEntity changedEntity : operator.getDecisionEntityList()) {  // 遍历本次邻域动作中被改变的决策实体
            Task task = (Task) changedEntity;                                   // 在本次邻域动作中被更改的任务 (只有此部分任务对收益有影响)
            long oldCost = taskCostMap.get(task);
            long newCost = task.getCost();
            changedCost = changedCost - oldCost + newCost;
            taskCostMap.put(task, newCost);
        }

        // 3. 计分
        Score score = solution.getScore();
        score.setMeanScore(score.getMeanScore() - changedCost);
        return score;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }


}
