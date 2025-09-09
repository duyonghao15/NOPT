package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model;

/**
 * @author Xu Shilong
 * @version 1.0
 */

public class Plan {
    long satelliteIndex;
    long antennaIndex;
    long orbitIndex;
    long snIndex;
    long dayIndex;
    long subIndex;
    long taskPrepareStartTime;
    long taskTrackingStartTime;
    long taskTrackingEndTime;
    long taskReleaseEndTime;

    public long getSatelliteIndex() {
        return satelliteIndex;
    }

    public void setSatelliteIndex(long satelliteIndex) {
        this.satelliteIndex = satelliteIndex;
    }

    public long getAntennaIndex() {
        return antennaIndex;
    }

    public void setAntennaIndex(long antennaIndex) {
        this.antennaIndex = antennaIndex;
    }

    public long getOrbitIndex() {
        return orbitIndex;
    }

    public void setOrbitIndex(long orbitIndex) {
        this.orbitIndex = orbitIndex;
    }

    public long getSnIndex() {
        return snIndex;
    }

    public void setSnIndex(long snIndex) {
        this.snIndex = snIndex;
    }

    public long getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(long dayIndex) {
        this.dayIndex = dayIndex;
    }

    public long getSubIndex() {
        return subIndex;
    }

    public void setSubIndex(long subIndex) {
        this.subIndex = subIndex;
    }

    public long getTaskPrepareStartTime() {
        return taskPrepareStartTime;
    }

    public void setTaskPrepareStartTime(long taskPrepareStartTime) {
        this.taskPrepareStartTime = taskPrepareStartTime;
    }

    public long getTaskTrackingStartTime() {
        return taskTrackingStartTime;
    }

    public void setTaskTrackingStartTime(long taskTrackingStartTime) {
        this.taskTrackingStartTime = taskTrackingStartTime;
    }

    public long getTaskTrackingEndTime() {
        return taskTrackingEndTime;
    }

    public void setTaskTrackingEndTime(long taskTrackingEndTime) {
        this.taskTrackingEndTime = taskTrackingEndTime;
    }

    public long getTaskReleaseEndTime() {
        return taskReleaseEndTime;
    }

    public void setTaskReleaseEndTime(long taskReleaseEndTime) {
        this.taskReleaseEndTime = taskReleaseEndTime;
    }

    public Plan(long satelliteIndex, long antennaIndex, long orbitIndex, long snIndex, long dayIndex, long subIndex, long taskPrepareStartTime, long taskTrackingStartTime, long taskTrackingEndTime, long taskReleaseEndTime) {
        this.satelliteIndex = satelliteIndex;
        this.antennaIndex = antennaIndex;
        this.orbitIndex = orbitIndex;
        this.snIndex = snIndex;
        this.dayIndex = dayIndex;
        this.subIndex = subIndex;
        this.taskPrepareStartTime = taskPrepareStartTime;
        this.taskTrackingStartTime = taskTrackingStartTime;
        this.taskTrackingEndTime = taskTrackingEndTime;
        this.taskReleaseEndTime = taskReleaseEndTime;
    }

    public Plan() {
    }
}
