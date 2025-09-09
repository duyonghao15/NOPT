package org.nudtopt.realworldproblems.apiforsatellite.resource.window;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.Resource;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Camera;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.task.Request;
import org.nudtopt.realworldproblems.apiforsatellite.task.Task;

import java.util.ArrayList;
import java.util.List;

public class Window extends Resource {

    protected long beginTime;                                       // 开始时间
    protected long centerTime;                                      // 中心时间
    protected long endTime;                                         // 结束时间
    protected long priority = 1;                                    // 窗口优先级
    protected long precision = 1;                                   // 窗口时间离散精度
    protected List<Task> taskList = new ArrayList<>();              // 该窗口内(可)包含的任务
    protected List<Request> requestList = new ArrayList<>();        // 该窗口内(可)包含的需求(1个需求可能对应多个任务)
    protected List<Window> conflictWindowList = new ArrayList<>();  // 时间接近的, 可能产生冲突的窗口
    protected double speed;                                         // 窗口数据写入/下传速率
    protected long workNum;                                         // 窗口内累计工作次数
    protected long workTime;                                        // 窗口内累计工作时长

    // getter & setter
    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getCenterTime() {
        return centerTime;
    }
    public void setCenterTime(long centerTime) {
        this.centerTime = centerTime;
    }

    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getPriority() {
        return priority;
    }
    public void setPriority(long priority) {
        this.priority = priority;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Request> getRequestList() {
        return requestList;
    }
    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public long getPrecision() {
        return precision;
    }
    public void setPrecision(long precision) {
        this.precision = precision;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public List<Window> getConflictWindowList() {
        return conflictWindowList;
    }
    public void setConflictWindowList(List<Window> conflictWindowList) {
        this.conflictWindowList = conflictWindowList;
    }


    public long getWorkNum() {
        return workNum;
    }
    public void setWorkNum(long workNum) {
        this.workNum = workNum;
    }

    public long getWorkTime() {
        return workTime;
    }
    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }


    @Override // 浅克隆
    public Window clone() {
        try {
            return (Window) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }   return null;
    }


    @Override
    public String toString() {
        String str = getClassName() + "-" + id;
        if(id > 10e8) {
            str = getClassName() + "-" + Tool.getTime(beginTime * 1000);
        }
        return str;
    }


    /**
     * 判断两个窗口是否相等
     * @author 杜永浩
     */
    public boolean equals(Window window) {
        return this.beginTime == window.getBeginTime() && this.endTime == window.getEndTime();
    }


    /**
     * 基于新任务的工作时长, 判断是否满足工作时间/次数约束
     * @author          杜永浩
     * @param satellite 卫星
     * @param workTime  工作时长
     */
    public boolean checkWorkTime(Satellite satellite, long workTime) {
        Camera camera = satellite.getCameraList().get(0);
        int maxWorkTime = this instanceof Day ? camera.getMaxDailyWorkTime() : camera.getMaxOrbitWorkTime();
        int maxWorkNum  = this instanceof Day ? camera.getMaxDailyWorkNum()  : camera.getMaxOrbitWorkNum();
        if(this.workTime + workTime > maxWorkTime) {
            return false;
        }
        if(this.workNum + 1 > maxWorkNum) {
            return false;
        }
        return true;
    }


/* class ends */
}
