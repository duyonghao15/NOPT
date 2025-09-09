package org.nudtopt.realworldproblems.apiforsatellite.resource.payload;

import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

import java.util.ArrayList;
import java.util.List;

public class Antenna extends Payload {

    private List<Window> forbiddenWindowList = new ArrayList<>();   // 禁用窗口列表

    // getter & setter
    public List<Window> getForbiddenWindowList() {
        return forbiddenWindowList;
    }
    public void setForbiddenWindowList(List<Window> forbiddenWindowList) {
        this.forbiddenWindowList = forbiddenWindowList;
    }

}
