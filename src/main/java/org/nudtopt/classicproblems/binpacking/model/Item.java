package org.nudtopt.classicproblems.binpacking.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Item extends DecisionEntity {

    private double width;                                               // 宽度
    private double height;                                              // 高度
    private double x;                                                   // 左下角坐标
    private double y;                                                   // 右下角坐标
    private Bin bin;                                                    // 货物

    @DecisionVariable(sortIn = "bin")
    private Integer priority;                                           // 决策变量1: 装箱优先级(顺序)

    @DecisionVariable
    private Boolean rotate;                                             // 决策变量2: 是否旋转

    private List<Integer> optionalPriorityList  = new ArrayList<>();    // 决策变量1的取值范围
    private List<Boolean> optionalRotateList = new ArrayList<>();       // 决策变量2的取值范围

    // getter & setter
    public double getWidth() {
        return rotate != null && rotate ? height : width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return rotate != null && rotate ? width : height;
    }
    public void setHeight(double height) {
        this.height = height;
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

    public Bin getBin() {
        return bin;
    }
    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getRotate() {
        return rotate;
    }
    public void setRotate(Boolean rotate) {
        this.rotate = rotate;
    }

    public List<Integer> getOptionalPriorityList() {
        return optionalPriorityList;
    }
    public void setOptionalPriorityList(List<Integer> optionalPriorityList) {
        this.optionalPriorityList = optionalPriorityList;
    }

    public List<Boolean> getOptionalRotateList() {
        return optionalRotateList;
    }
    public void setOptionalRotateList(List<Boolean> optionalRotateList) {
        this.optionalRotateList = optionalRotateList;
    }

}
