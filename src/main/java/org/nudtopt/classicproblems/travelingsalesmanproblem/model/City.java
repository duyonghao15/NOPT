package org.nudtopt.classicproblems.travelingsalesmanproblem.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class City extends DecisionEntity {

    @DecisionVariable(sortIn = "salesman")
    private Integer priority;                                               // 决策变量: 城市在旅行商的顺序权重
    private Salesman salesman;                                              // 商人 (单旅行商问题只有一个商人)
    private List<Integer> optionalPriorityList = new ArrayList<>();         // 决策变量值域

    private double x;                                                       // x坐标
    private double y;                                                       // y坐标
    private double totalDistance;                                           // 当前累计路径距离

    // getter & setter
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Salesman getSalesman() {
        return salesman;
    }
    public void setSalesman(Salesman salesman) {
        this.salesman = salesman;
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

    public double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }


/* class ends */
}
