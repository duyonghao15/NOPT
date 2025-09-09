package org.nudtopt.realworldproblems.apiforsatellite.resource.platform;

import org.nudtopt.realworldproblems.apiforsatellite.resource.chance.DownlinkChance;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Battery;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Camera;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Memory;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.DownlinkWindow;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.ImageWindow;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.InterlinkWindow;
import org.nudtopt.realworldproblems.apiforsatellite.tool.constraint.Constraint;
import org.nudtopt.realworldproblems.apiforsatellite.tool.constraint.ConstraintReader;

import java.util.ArrayList;
import java.util.List;

public class Satellite extends Platform {

    private List<Camera> cameraList;    // 多载荷相机
    private Antenna antenna;            // 天线
    private Battery battery;            // 电池
    private Memory memory;              // 固存
    private Constraint constraint;      // 转换时间计算函数

    // getter & setter
    public List<Camera> getCameraList() {
        return cameraList;
    }
    public void setCameraList(List<Camera> cameraList) {
        this.cameraList = cameraList;
    }

    public Antenna getAntenna() {
        return antenna;
    }
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
    }

    public Battery getBattery() {
        return battery;
    }
    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    public Memory getMemory() {
        return memory;
    }
    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Constraint getConstraint() {
        return constraint;
    }
    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    /* Currently not available online, please contact duyonghao15@163.com for more details */


    /* class ends */
}
