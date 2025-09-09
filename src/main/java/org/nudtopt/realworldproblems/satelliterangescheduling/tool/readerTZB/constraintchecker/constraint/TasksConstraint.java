package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint;


import javafx.util.Pair;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Task;

import java.util.Map;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class TasksConstraint<T extends Pair<Task, Task>> implements Constraint<T> {
    private Map<Long, Antenna> antennaMap;

    public int intCheck(T taskPair) {
        Task task1 = taskPair.getKey();
        Task task2 = taskPair.getValue();
        // 两个任务的状态标记
        boolean sameAntenna   = task1.getArc().getAntennaIndex() == task2.getArc().getAntennaIndex();
        boolean sameSatellite = task1.getSatelliteIndex() == task2.getSatelliteIndex();
        boolean sameOrbit     = task1.getArc().getOrbitIndex() == task2.getArc().getOrbitIndex();
        boolean sameTaskType  = task1.getTaskType().equals(task2.getTaskType());
        boolean isOverlapping = this.checkIsTaskOverlapping(task1, task2);
        // #特殊情况: 检查任务是不是同一个任务 = 不违反
        if (task1.equals(task2))
            return 0;
        // #特殊情况: 有一个任务没配置弧段 = 不违反
        if (task1.getArc() == null || task2.getArc() == null)
            return 0;
        // #特殊情况: 不同天线 不同卫星 = 不违反
        if (!sameAntenna && !sameSatellite)
            return 0;

        // 不同天线 同一个星
        if (!sameAntenna && sameSatellite) {
            // #TZB 同星同圈不同类任务-加强约束：同卫星，同一圈, 两任务类型相同 = 违反 - 2
            if (sameOrbit && sameTaskType)
                return 2;
        }
        // 同一天线 不同卫星
        if (sameAntenna && !sameSatellite) {
            // 天线占用冲突 = 违反 - 1
            if (isOverlapping)
                return 1;
        }
        //同一天线 同一卫星
        if (sameAntenna && sameSatellite) {
            if (isOverlapping) {
                // #TZB TTC+DDT天线特殊工作条件-放宽约束：同TTC+DDT天线 同星 不同任务类型  = 不违反
                if (this.antennaMap.get(task1.getArc().getAntennaIndex()).getFunction().equals("TTC+DDT") && !sameTaskType)
                    return 0;
                // 否则是正常违反的情况：天线占用冲突 = 违反 - 1
                else
                    return 1;
            }
            // #TZB 同星同圈不同类任务-加强约束：同卫星，同一圈, 两任务类型相同 = 违反 - 2
            if (sameOrbit && sameTaskType)
                return 2;
        }
        //如果以上的违反条件都没触发 = 不违反
        return 0;
    }

    private boolean checkIsTaskOverlapping(Task task1, Task task2) {
        long beginTime1 = task1.getArc().getStartTime() - task1.getPrepareTime();
        long endTime1   = task1.getArc().getEndTime() + task1.getReleaseTime();
        long beginTime2 = task2.getArc().getStartTime() - task2.getPrepareTime();
        long endTime2   = task2.getArc().getEndTime() + task2.getReleaseTime();
        return !(endTime1 <= beginTime2 || endTime2 <= beginTime1);
    }


    public TasksConstraint(Map<Long, Antenna> antennaMap) {
        this.antennaMap = antennaMap;
    }

    public TasksConstraint() {
    }
}
