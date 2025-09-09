package org.nudtopt.realworldproblems.satelliterangescheduling.model;

import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

import java.text.SimpleDateFormat;

public class Range extends Window {

    private Orbit orbit;        // 被测控的卫星的轨道

    private Antenna antenna;    // 测控弧由地面哪根天线提供的

    private double elevation;   // 地面天线的仰角

    private boolean effective;  // 是否有效 (有一些根据约束无效)

    // getter & setter
    public Orbit getOrbit() {
        return orbit;
    }
    public void setOrbit(Orbit orbit) {
        this.orbit = orbit;
    }

    public Antenna getAntenna() {
        return antenna;
    }
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
    }

    public double getElevation() {
        return elevation;
    }
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public boolean isEffective() {
        return effective;
    }
    public void setEffective(boolean effective) {
        this.effective = effective;
    }


/* class ends */
}
