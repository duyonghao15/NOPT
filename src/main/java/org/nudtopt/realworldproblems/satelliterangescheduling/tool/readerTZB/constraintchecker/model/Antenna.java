package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class Antenna {
    private Long         id;
    private String       function;                                  // 支持功能 NULL, TTC, DDT, TTC/DDT, TTC+DDT
    private List<Arc>    arcList;                                   // 该设备的拥有的弧段列表
    private List<Window> forbiddenWindowList = new ArrayList<>();   // 禁用窗口列表

    public Antenna(Long id) {
        this.id                  = id;
        this.arcList             = new ArrayList<>();
        this.forbiddenWindowList = new ArrayList<>();
    }

    public boolean isArcAvailable(Task task, Arc arc) {
        Window windowArc = new Window(arc.getStartTime() - task.getPrepareTime(),
                arc.getEndTime() + task.getReleaseTime());
        for (Window window : this.forbiddenWindowList) {
            if (windowArc.isOverlapping(window)) {
                return false;
            }
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<Arc> getArcList() {
        return arcList;
    }

    public void setArcList(List<Arc> arcList) {
        this.arcList = arcList;
    }

    public List<Window> getForbiddenWindowList() {
        return forbiddenWindowList;
    }

    public void setForbiddenWindowList(List<Window> forbiddenWindowList) {
        this.forbiddenWindowList = forbiddenWindowList;
    }
}
