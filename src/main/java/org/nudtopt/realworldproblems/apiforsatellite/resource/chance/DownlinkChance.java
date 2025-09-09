package org.nudtopt.realworldproblems.apiforsatellite.resource.chance;

import org.nudtopt.realworldproblems.apiforsatellite.resource.window.DownlinkWindow;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

public class DownlinkChance extends Window {

    private double elevation;               // 地面天线仰角
    private DownlinkWindow downlinkWindow;  // 数传时刻所属成像窗口
    private DownlinkChance betterChance;    // 一般指上一个机会

    // getter & setter
    public double getElevation() {
        return elevation;
    }
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public DownlinkWindow getDownlinkWindow() {
        return downlinkWindow;
    }
    public void setDownlinkWindow(DownlinkWindow downlinkWindow) {
        this.downlinkWindow = downlinkWindow;
    }

    public DownlinkChance getBetterChance() {
        return betterChance;
    }
    public void setBetterChance(DownlinkChance betterChance) {
        this.betterChance = betterChance;
    }

}
