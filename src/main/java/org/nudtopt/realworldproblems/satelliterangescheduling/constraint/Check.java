package org.nudtopt.realworldproblems.satelliterangescheduling.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Check extends Constraint {

    @Override
    public Score calScore() {
        // 1. 根据停机位(决策变量)分配情况, 进行分组
        List<Task> totalTaskList = ((Scenario) solution).getTaskList();
        List<Task> scheduledPlaneList = totalTaskList.stream().filter(task -> task.getRange() != null).collect(Collectors.toList());  // 已分配弧段的
        Map<Antenna, List<Task>> antennaTasksMap = scheduledPlaneList.stream()
                .sorted(Comparator.comparing(Task::getBeginTime))                               // 按弧段开始时间排序
                .collect(Collectors.groupingBy(task -> task.getRange().getAntenna()));          // 按弧段对应的天线
        // 2. 遍历停机位, 记录冲突数
        boolean conflict = false;
        for(Antenna antenna : antennaTasksMap.keySet()) {
            List<Task> taskList = antennaTasksMap.get(antenna);
            for(int i = 0 ; i < taskList.size() - 1 ; i ++) {
                Task task_1 = taskList.get(i);
                Task task_2 = taskList.get(i + 1);
                if(task_1.getIntervalTo(task_2) < 0) {
                    conflict = true;
                    break;
                }
            }
        }
        /* function ends */
        Score score = new Score();
        score.setHardScore(conflict ? -1 : 0);                                                  // 约束: 是否冲突
        score.setMeanScore(scheduledPlaneList.size());                                          // 收益: 安排的测控任务数
        return score;
    }

}
