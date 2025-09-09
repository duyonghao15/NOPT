package org.nudtopt.realworldproblems.satelliterangescheduling.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.apiforsatellite.tool.interval.Interval;

import java.text.SimpleDateFormat;
import java.util.*;

public class Task extends DecisionEntity {

    @DecisionVariable(nullable = true)
    private Range range;
    private List<Range> optionalRangeList;

    private long setup;                                                 // 准备时间
    private long duration;                                              // 持续时间
    private long release;                                               // 释放时间
    private long count;                                                 // (每天)第几次升/降轨

    private Day day;                                                    // 哪一天的任务
    private Day satelliteDay;                                           // 卫星时
    private Satellite satellite;                                        // 哪颗星的任务
    private TaskRequest taskRequest;                                    // 用户提的专门需求
    private List<Constraint> constraintList   = new ArrayList<>();      // 天智杯约束条件列表
    private Map<Range, String> supportTypeMap = new HashMap<>();        // 选择某弧段时, 支持的类型 (NULL, TTC, DDT, TTC/DDT, TTC+DDT)
    private List<Task> possibleConflictTasksList = new ArrayList<>();   // 潜在的冲突任务 (弧段所属测站存在交集的)

    // getter & setter
    public Range getRange() {
        return range;
    }
    public void setRange(Range range) {
        this.range = range;
    }

    public List<Range> getOptionalRangeList() {
        return optionalRangeList;
    }
    public void setOptionalRangeList(List<Range> optionalRangeList) {
        this.optionalRangeList = optionalRangeList;
    }

    public long getSetup() {
        return setup;
    }
    public void setSetup(long setup) {
        this.setup = setup;
    }

    public long getDuration() {
        if(range == null)   return duration;
        else                return range.getCapability() + setup + release;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getRelease() {
        return release;
    }
    public void setRelease(long release) {
        this.release = release;
    }

    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }

    public Day getDay() {
        if(range != null)   return range.getOrbit().getDay();
        return day;
    }
    public void setDay(Day day) {
        this.day = day;
    }

    public Day getSatelliteDay() {
        return satelliteDay;
    }
    public void setSatelliteDay(Day satelliteDay) {
        this.satelliteDay = satelliteDay;
    }

    public Satellite getSatellite() {
        return satellite;
    }
    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    public TaskRequest getTaskRequest() {
        return taskRequest;
    }
    public void setTaskRequest(TaskRequest taskRequest) {
        this.taskRequest = taskRequest;
    }

    public List<Constraint> getConstraintList() {
        return constraintList;
    }
    public void setConstraintList(List<Constraint> constraintList) {
        this.constraintList = constraintList;
    }

    public Map<Range, String> getSupportTypeMap() {
        return supportTypeMap;
    }
    public void setSupportTypeMap(Map<Range, String> supportTypeMap) {
        this.supportTypeMap = supportTypeMap;
    }

    public String getRangeType() {
        if(range == null)   return null;
        else                return supportTypeMap.get(range);
    }

    public List<Task> getPossibleConflictTasksList() {
        return possibleConflictTasksList;
    }
    public void setPossibleConflictTasksList(List<Task> possibleConflictTasksList) {
        this.possibleConflictTasksList = possibleConflictTasksList;
    }

    // ########################################################################################
    // #################################### 以下是辅助计算函数 ##################################
    // ########################################################################################

    // 计算转换时间
    public long getIntervalTo(Task task) {
        long thisBeginTime = range.getBeginTime() - setup;
        long thisEndTime   = range.getEndTime()   + release;
        long thatBeginTime = task.getRange().getBeginTime() - task.getSetup();
        long thatEndTime   = task.getRange().getEndTime()   + task.getRelease();
        return (long) Interval.getIntervalTime(thisBeginTime, thisEndTime, thatBeginTime, thatEndTime);
    }


    // 是否同日, 同星, 同升降轨
    public boolean isNear(Task task) {
        return (range.getOrbit().getDay()       == task.getRange().getOrbit().getDay() &&
                range.getOrbit().isRise()       == task.getRange().getOrbit().isRise() &&
                range.getOrbit().getSatellite() == task.getRange().getOrbit().getSatellite());
    }


    // 是否near且满足相关要求
    public boolean isProperNear(Task task) {
        // 1. 相关约束均为默认值, 即无约束
        if(taskRequest.getMinInterval() <= 0 && taskRequest.getMaxInterval() >= 9999999 &&
           taskRequest.getMinOrbitGap() <= 0 && taskRequest.getMaxOrbitGap() >= 9999999) {
            return true;
        }
        // 2. 非同日、同星、同升降轨, 不对比
        if(!isNear(task))   return true;
        // 3. 计算两任务间隔时间、轨道圈数
        long interval = getIntervalTo(task);
        long orbitGap = Math.abs(range.getOrbit().getId() - task.getRange().getOrbit().getId());
        return interval >= taskRequest.getMinInterval() && interval <= taskRequest.getMaxInterval() &&
               orbitGap >= taskRequest.getMinOrbitGap() && orbitGap <= taskRequest.getMaxOrbitGap();
    }


    /**
     * @return 任务开始时间
     */
    public long getBeginTime() {
        return range == null ? 0 : range.getBeginTime() - setup;
    }


    /**
     * @return 任务结束时间
     */
    public long getEndTime() {
        return range == null ? 0 : range.getEndTime() + release;
    }


    /**
     * 判断两个任务是否可能存在冲突
     * @param task 另一个任务
     * @return     是否可能冲突
     */
    public boolean possibleConflict(Task task) {
        if(this == task)                                                                  return false;                 // 自己和自己不冲突
        for(Range range_1 : optionalRangeList) {
            Antenna antenna_1 = range_1.getAntenna();
            for(Range range_2 : task.getOptionalRangeList()) {
                Antenna antenna_2 = range_2.getAntenna();
                if(antenna_1 != antenna_2)      continue;                                                               // 天线不同, 不可能冲突
                long beginTime_1 = range_1.getBeginTime() - setup;
                long endTime_1   = range_1.getEndTime()   + release;
                long beginTime_2 = range_2.getBeginTime() - task.getSetup();
                long endTime_2   = range_2.getEndTime()   + task.getRelease();
                double interval = Interval.getIntervalTime(beginTime_1, endTime_1, beginTime_2, endTime_2);             // 计算弧段间隔
                if(interval < 0) {                                                                                      // 弧段存在交集
                    return true;                                                                                        // 则可能存在冲突
                }
            }
        }
        return false;
    }

/* class ends */
}
