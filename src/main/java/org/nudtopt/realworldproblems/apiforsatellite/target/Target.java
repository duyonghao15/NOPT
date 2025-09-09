package org.nudtopt.realworldproblems.apiforsatellite.target;

import org.nudtopt.api.model.NumberedObject;

public class Target extends NumberedObject {

    private double longitude;                       // 中心经度
    private double latitude;                        // 中心纬度
    private Grid grid;                              // 中心网格(可为空)
    private Area area;                              // 区域(区域目标)
    private long priority;                          // 优先级
    private String domain = "陆";                   // 域: 陆/海/空

    // getter & setter
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Grid getGrid() {
        return grid;
    }
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Area getArea() {
        return area;
    }
    public void setArea(Area area) {
        this.area = area;
    }

    public long getPriority() {
        return priority;
    }
    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
