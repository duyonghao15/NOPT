package org.nudtopt.realworldproblems.apiforsatellite.tool.constraint;

import org.apache.commons.lang3.StringUtils;
import org.nudtopt.api.constraint.Conflict;
import org.nudtopt.api.model.MainLogger;
import org.nudtopt.realworldproblems.apiforsatellite.resource.chance.Angle;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Action;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;
import org.nudtopt.realworldproblems.apiforsatellite.task.Task;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Constraint extends MainLogger implements Serializable, Cloneable {


    //  ============================================================================================
    //  =========================  接口1: 计算两个时间区间的间隔 (不分先后)  =========================
    //  ============================================================================================
    public static double getIntervalTime(double beginTime1, double endTime1, double beginTime2, double endTime2) {
        if(beginTime2 >= endTime1)  return beginTime2 - endTime1;       // 1-2的间隔时间 (>0)
        if(beginTime1 >= endTime2)  return beginTime1 - endTime2;       // 2-1的间隔时间 (>0)
        if(beginTime2 <  endTime1)  return beginTime2 - endTime1;       // 1-2的重叠时间 (<0)
        else                        return beginTime1 - endTime2;       // 2-1的重叠时间 (<0)
    }
    public static double getIntervalTime(Window window1, Window window2) {
        return getIntervalTime(window1.getBeginTime(), window1.getEndTime(), window2.getBeginTime(), window2.getEndTime());
    }



    //  ============================================================================================
    //  ===========================  接口4: todo 根据任务集合检查单星约束  ============================
    //  ============================================================================================
    public boolean check(List<Task> taskList) {
        /* Currently not available online, please contact duyonghao15@163.com for more details */
        return true;
    }


    //  ============================================================================================
    //  ===========================  接口4: todo 检查动作(前/后)转换时间约束  ============================
    //  ============================================================================================
    public boolean check(Action action) {
        return true;
    }





    //  ============================================================================================
    //  =======================  接口4: 检查两个Task转换时间约束 (不分先后)  =========================
    //  ============================================================================================
    public boolean check(Task task1, Task task2) {
        /* Currently not available online, please contact duyonghao15@163.com for more details */

        return true;
    }
    // 4.2: (仅考虑成像时)检查Task成像->成像转换时间约束
    public boolean checkImage(Task task1, Task task2) {
        /* Currently not available online, please contact duyonghao15@163.com for more details */
        return true;
    }
    //  4.3: 检查Task成像->数传转换时间约束
    public boolean check(Task task) {
        /* Currently not available online, please contact duyonghao15@163.com for more details */
        return true;
    }

    //  =======================================================================================
    //  =========================  接口5: 用于计算各卫星专门约束的接口  =========================
    //  =======================================================================================
    public boolean check(Orbit orbit, List<Task> taskList) {
        return true;
    }
    public boolean check(List<Orbit> orbitList, List<Task> taskList) {
        return true;
    }


    //  =======================================================================================
    //  ========================  接口6: 用于计算各类转换时间接口  ==============================
    //  =======================================================================================
    public long getTransitionTime(Task taskFrom, Task taskTo, String transitionType) {
        return 999L;
    }
    // 6-1 同一模式的【成像】转换时间(可不关机, 直接机动, 但超过一定时间需关机)
    public long getImageTransTime(Task task_1, Task task_2, String type) {
        /* Currently not available online, please contact duyonghao15@163.com for more details */
        return 999L;
    }


    /**
     * 获取左右视
     * @param angle
     * @return
     */
    public String getSideByAngle(double angle) {
        return StringUtils.EMPTY;
    }



/* class ends */
}
