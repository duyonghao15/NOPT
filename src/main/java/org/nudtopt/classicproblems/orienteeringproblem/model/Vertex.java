package org.nudtopt.classicproblems.orienteeringproblem.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Vertex extends DecisionEntity {

    @DecisionVariable(nullable = true)
    private Tour tour;                                                      // 决策变量1: 选择哪条路线被访问

    @DecisionVariable(sortIn = "tour")
    private Integer priority = 0;                                           // 决策变量2: 在该路径中的访问优先级

    private List<Tour> optionalTourList = new ArrayList<>();                // 决策变量1的取值范围: 可用路线取值范围
    private List<Integer> optionalPriorityList = new ArrayList<>();         // 决策变量2的取值范围: 顺序取值范围

    private double x;                                                       // 位置坐标
    private double y;                                                       // 位置坐标
    private double score;                                                   // 得分
    private long duration;                                                  // 服务时长
    private TimeWindow timeWindow;                                          // 服务时间窗口

    // getter & setter
    public Tour getTour() {
        return tour;
    }
    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<Tour> getOptionalTourList() {
        return optionalTourList;
    }
    public void setOptionalTourList(List<Tour> optionalTourList) {
        this.optionalTourList = optionalTourList;
    }

    public List<Integer> getOptionalPriorityList() {
        return optionalPriorityList;
    }
    public void setOptionalPriorityList(List<Integer> optionalPriorityList) {
        this.optionalPriorityList = optionalPriorityList;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }
    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

}
