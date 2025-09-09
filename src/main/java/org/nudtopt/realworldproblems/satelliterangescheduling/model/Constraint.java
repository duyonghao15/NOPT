package org.nudtopt.realworldproblems.satelliterangescheduling.model;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.apiforsatellite.tool.interval.Interval;


public class Constraint extends NumberedObject {

    private double minElevation;
    private long[] scope;

    // getter & setter
    public double getMinElevation() {
        return minElevation;
    }
    public void setMinElevation(double minElevation) {
        this.minElevation = minElevation;
    }

    public long[] getScope() {
        return scope;
    }
    public void setScope(long[] scope) {
        this.scope = scope;
    }

    /**
     * 根据天智杯赛题要求, 判断弧段是否满足该项约束要求
     * @author      杜永浩
     * @param range 弧段
     * @return      true: 满足; false: 不满足
     */
    public boolean check(Range range) {
        long beginTime = range.getBeginTime();                                                      // 弧段开始时间
        long orbitIndexInChina = Long.valueOf(range.getOrbit().getIndex());                         // 境内相对圈号
        long consecutiveNum    = Long.valueOf(range.getOrbit().getComment().split("/")[1]);  // 境内总圈数
        boolean rise     = range.getOrbit().isRise();                                               // 升降轨
        double elevation = range.getElevation();                                                    // 弧段仰角
        if(elevation < minElevation)        return false;                                           // 若弧段仰角 < 最小约束值, 则直接不满足
        switch(id.intValue()) {
            default:
            case 0:
                /* do nothing */
                return true;
            case 1:
                /* do nothing */
                return true;
            case 2:
                long time = Math.abs(beginTime - (scope[1] + 43200)) % 86400 - 43200;
                return time >= -scope[0] && time <= scope[2];
            case 3:
                /* do nothing */
                return true;
            case 4:
                if(scope[1] < 50) {
                    return orbitIndexInChina >= scope[1] - scope[0] && orbitIndexInChina <= scope[1] + scope[2];
                } else {
                    return orbitIndexInChina >= scope[1] - 90 + consecutiveNum - scope[0] && orbitIndexInChina <= scope[1] - 90 + consecutiveNum + scope[2];
                }
            case 5:
                if(scope[1] < 50) {
                    return rise && orbitIndexInChina >= scope[1] - scope[0] && orbitIndexInChina <= scope[1] + scope[2];
                } else {
                    return rise && orbitIndexInChina >= scope[1] - 90 + consecutiveNum - scope[0] && orbitIndexInChina <= scope[1] - 90 + consecutiveNum + scope[2];
                }
            case 6:
                if(scope[1] < 50) {
                    return !rise && orbitIndexInChina >= scope[1] - scope[0] && orbitIndexInChina <= scope[1] + scope[2];
                } else {
                    return !rise && orbitIndexInChina >= scope[1] - 90 + consecutiveNum - scope[0] && orbitIndexInChina <= scope[1] - 90 + consecutiveNum + scope[2];
                }
        }
        /* function ends */
    }


    /**
     * 根据天智杯约束要求, 判断两个任务是否冲突
     * @author     杜永浩
     */
    public static boolean check(Task task1, Task task2) {
        boolean checkOrbit   = checkOrbit(task1, task2);
        boolean checkAntenna = checkAntenna(task1, task2);
        if(!checkOrbit || !checkAntenna)    return false;
        return true;
    }


    /**
     * (同星)同类型任务不同圈
     * @author     杜永浩
     */
    public static boolean checkOrbit(Task task1, Task task2) {
        if(task1 == task2)                      return true;
        Range range1 = task1.getRange();
        Range range2 = task2.getRange();
        if(range1 == null || range2 == null)    return true;
        Orbit orbit1 = range1.getOrbit();
        Orbit orbit2 = range2.getOrbit();
        Satellite satellite1 = orbit1.getSatellite();
        Satellite satellite2 = orbit2.getSatellite();
        if(satellite1 == satellite2 && orbit1 == orbit2 && task1.getType().equals(task2.getType())) {
            return false;               // 同星同圈, 两个任务类型不能相同
        }
        return true;
    }


    /**
     * 设备不能有冲突
     * @author     杜永浩
     */
    public static boolean checkAntenna(Task task1, Task task2) {
        if(task1 == task2)                      return true;
        Range range1 = task1.getRange();
        Range range2 = task2.getRange();
        if(range1 == null || range2 == null)    return true;
        Orbit orbit1 = range1.getOrbit();
        Orbit orbit2 = range2.getOrbit();
        Antenna antenna1 = range1.getAntenna();
        Antenna antenna2 = range2.getAntenna();
        Satellite satellite1 = orbit1.getSatellite();
        Satellite satellite2 = orbit2.getSatellite();
        // 1. 非同一天线, 无冲突
        if(antenna1 != antenna2) {
            return true;                // 非同一天线, 无冲突
        }
        // 2. 同一天线, 且同星同圈, 若选择的弧段及天线都对应的是 TTC+DDT (可同时), 也无冲突
        if(satellite1 == satellite2 && orbit1 == orbit2 && task1.getRangeType().equals("TTC+DDT") && task2.getRangeType().equals("TTC+DDT")) {
            return true;
        }
        long beginTime1 = task1.getBeginTime();
        long endTime1   = task1.getEndTime();
        long beginTime2 = task2.getBeginTime();
        long endTime2   = task2.getEndTime();
        long gap = (long) Interval.getIntervalTime(beginTime1, endTime1, beginTime2, endTime2); // 两个任务间隔时间
        return gap >= 0;
    }


/* class ends */
}
