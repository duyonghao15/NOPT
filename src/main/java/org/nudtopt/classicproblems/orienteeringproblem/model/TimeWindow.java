package org.nudtopt.classicproblems.orienteeringproblem.model;

import org.nudtopt.api.model.NumberedObject;

public class TimeWindow extends NumberedObject {

    private long beginTime;
    private long endTime;
    private long duration;

    // getter & setter
    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

}
