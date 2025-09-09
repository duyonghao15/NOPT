package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model;

import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint.ArcConstraint;

import java.util.List;
import java.util.Map;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class Task {
    private long   snIndex;
    private long   subIndex;
    private long   dayIndex;
    private String taskType;
    private long   satelliteIndex;

    private long prepareTime;                                           // 准备时间
    private long durationTime;                                          // 持续时间
    private long releaseTime;                                           // 释放时间

    private List<ArcConstraint<Arc>> arcConstraintList;           // 天智杯约束条件列表
    private Map<Long, String>        availableAntennaMap;

    private List<Arc> optionalArcList;
    private Arc       arc;

    public boolean isArcAvailable(Arc arc) {
        for (ArcConstraint<Arc> arcConstraint : this.getArcConstraintList()) {
            if (arcConstraint.boolCheck(arc)) {
                return true;
            }
        }
        return false;
    }

    public long getSnIndex() {
        return snIndex;
    }

    public void setSnIndex(long snIndex) {
        this.snIndex = snIndex;
    }

    public long getSubIndex() {
        return subIndex;
    }

    public void setSubIndex(long subIndex) {
        this.subIndex = subIndex;
    }

    public long getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(long dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public long getSatelliteIndex() {
        return satelliteIndex;
    }

    public void setSatelliteIndex(long satelliteIndex) {
        this.satelliteIndex = satelliteIndex;
    }

    public long getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(long prepareTime) {
        this.prepareTime = prepareTime;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public List<ArcConstraint<Arc>> getArcConstraintList() {
        return arcConstraintList;
    }

    public void setArcConstraintList(List<ArcConstraint<Arc>> arcConstraintList) {
        this.arcConstraintList = arcConstraintList;
    }

    public Map<Long, String> getAvailableAntennaMap() {
        return availableAntennaMap;
    }

    public void setAvailableAntennaMap(Map<Long, String> availableAntennaMap) {
        this.availableAntennaMap = availableAntennaMap;
    }

    public List<Arc> getOptionalArcList() {
        return optionalArcList;
    }

    public void setOptionalArcList(List<Arc> optionalArcList) {
        this.optionalArcList = optionalArcList;
    }

    public Arc getArc() {
        return arc;
    }

    public void setArc(Arc arc) {
        this.arc = arc;
    }

    public Task() {
    }

    public Task(long snIndex, long subIndex, long dayIndex, String taskType, long satelliteIndex, long prepareTime, long durationTime, long releaseTime, List<ArcConstraint<Arc>> arcConstraintList, Map<Long, String> availableAntennaMap, List<Arc> optionalArcList, Arc arc) {
        this.snIndex = snIndex;
        this.subIndex = subIndex;
        this.dayIndex = dayIndex;
        this.taskType = taskType;
        this.satelliteIndex = satelliteIndex;
        this.prepareTime = prepareTime;
        this.durationTime = durationTime;
        this.releaseTime = releaseTime;
        this.arcConstraintList = arcConstraintList;
        this.availableAntennaMap = availableAntennaMap;
        this.optionalArcList = optionalArcList;
        this.arc = arc;
    }
}

