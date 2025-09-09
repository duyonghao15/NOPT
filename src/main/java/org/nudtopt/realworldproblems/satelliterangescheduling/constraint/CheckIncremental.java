package org.nudtopt.realworldproblems.satelliterangescheduling.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckIncremental extends Check {

    private Set<Task> scheduledTaskSet = new HashSet<>();

    @Override
    public Score calScore() {
        if(operator == null) {                                                                          // 算子为null (第一次调用)
            analyzePossibleConflicts((Scenario) solution);                                              // 分析测控任务两两之间存在的潜在冲突
            return super.calScore();                                                                    // 调用父类非增量式计算
        }

        List<DecisionEntity> changedEntityList = operator.getDecisionEntityList();                      // 本次算子, 改动决策变量的全部实体(任务)集合
        boolean conflict = false;
        for(int i = 0 ; i < changedEntityList.size() ; i ++) {                                          // 遍历这些被改动的任务 (因为收益/约束只会受他们影响)
            Task task = (Task) changedEntityList.get(i);                                                // 被改动决策变量的任务
            Range range = task.getRange();                                                              // 当前弧段(决策变量值)
            // 更新已分配机位的飞机集合
            if(range == null) {
                scheduledTaskSet.remove(task);                                                          // 取消分配 (肯定不会产生新冲突)
            } else {
                scheduledTaskSet.add(task);                                                             // 新分配 (有可能产生新冲突)
                for(Task possibleConflictTask : task.getPossibleConflictTasksList()) {                  // 遍历潜在冲突任务, 判断是否真存在冲突
                    if(possibleConflictTask.getRange() == null)                                         continue;
                    if(possibleConflictTask.getRange().getAntenna() != task.getRange().getAntenna())    continue;
                    if(task.getIntervalTo(possibleConflictTask) < 0) {                                  // 若冲突
                        conflict = true;                                                                // 则记录冲突
                        break;                                                                          // 跳出
                    }
                }
            }
        }
        /* function ends */
        Score score = new Score();
        score.setHardScore(conflict ? -1 : 0);                                                          // 约束: 是否冲突
        score.setMeanScore(scheduledTaskSet.size());                                                    // 收益: 安排的测控任务数
        return score;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }


    /**
     * 分析测控任务两两之间存在的潜在冲突
     * 即: 弧段的测站存在交集, 且时间上存在冲突的
     */
    public void analyzePossibleConflicts(Scenario scenario) {
        logger.info("正分析 -> 测控任务两两之间的潜在冲突 ... ...");
        AtomicInteger conflictNum = new AtomicInteger(0);
        scenario.getTaskList().parallelStream().forEach(task -> {
            for(int i = 0 ; i < scenario.getTaskList().size() ; i ++) {
                Task anotherTask = scenario.getTaskList().get(i);
                if(task.possibleConflict(anotherTask)) {
                    task.getPossibleConflictTasksList().add(anotherTask);
                    conflictNum.getAndAdd(1);
                }
            }
        });
        logger.info("已分析 -> 测控弧段潜在冲突 (平均每个任务冲突数 " + conflictNum.get() / scenario.getTaskList().size() + ").\n");
    }


}
