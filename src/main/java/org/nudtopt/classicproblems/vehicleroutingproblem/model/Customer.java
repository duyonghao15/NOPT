package org.nudtopt.classicproblems.vehicleroutingproblem.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Customer extends DecisionEntity {

    @DecisionVariable/*(nullable = true)*/
    private Vehicle vehicle;                                                // 决策变量1: 选择哪辆车

    @DecisionVariable(sortIn = "vehicle")
    private Integer priority = 0;                                           // 决策变量2: 在该车路径中的访问优先级

    private List<Vehicle> optionalVehicleList = new ArrayList<>();          // 决策变量1的取值范围: 可用车辆
    private List<Integer> optionalPriorityList = new ArrayList<>();         // 决策变量2的取值范围: 顺序范围

    private double x;                                                       // 位置坐标
    private double y;                                                       // 位置坐标
    private double demand;                                                  // 用户需求量
    private long duration;                                                  // 服务时长
    private TimeWindow timeWindow;                                          // 服务时间窗口

    // getter & setter
    public Vehicle getVehicle() {
        return vehicle;
    }
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<Vehicle> getOptionalVehicleList() {
        return optionalVehicleList;
    }
    public void setOptionalVehicleList(List<Vehicle> optionalVehicleList) {
        this.optionalVehicleList = optionalVehicleList;
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

    public double getDemand() {
        return demand;
    }
    public void setDemand(double demand) {
        this.demand = demand;
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
