package org.nudtopt.realworldproblems.multiplatformrouting.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.List;
import java.util.Set;

public class Node extends NumberedObject {

    private double width;                        // x方向宽度
    private double height;                       // y方向高度
    private double x;                            // x坐标(中心点)
    private double y;                            // y坐标(中心点)
    private boolean boundary;                    // 是否是边界节点
    private String event;                        // fire, cloud,
    private List<Platform> coveringPlatformList; // 被哪些平台覆盖
    private Set<Integer> coveringTimeSet;        // 被覆盖的时间(min)
    private boolean tracked;                     // 是否被跟踪

    // getter & setter
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
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

    public boolean isBoundary() {
        return boundary;
    }
    public void setBoundary(boolean boundary) {
        this.boundary = boundary;
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    public List<Platform> getCoveringPlatformList() {
        return coveringPlatformList;
    }
    public void setCoveringPlatformList(List<Platform> coveringPlatformList) {
        this.coveringPlatformList = coveringPlatformList;
    }

    public Set<Integer> getCoveringTimeSet() {
        return coveringTimeSet;
    }
    public void setCoveringTimeSet(Set<Integer> coveringTimeSet) {
        this.coveringTimeSet = coveringTimeSet;
    }

    public boolean isTracked() {
        return tracked;
    }
    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    // 函数: 根据id, 长方形长, 宽, 计算网格x, y坐标
    public double calculateX(int id, double totalWidth, double totalHeight) {
        return width * (id - (int)(id/totalHeight) * (totalWidth/width)) + width / 2;
    }

    public double calculateY(int id, double totalHeight) {
        return height * (int)(id/totalHeight + 1) - height / 2;
    }


/* class ends*/
}
