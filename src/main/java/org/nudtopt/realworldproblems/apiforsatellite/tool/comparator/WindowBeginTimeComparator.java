package org.nudtopt.realworldproblems.apiforsatellite.tool.comparator;

import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

import java.util.Comparator;

public class WindowBeginTimeComparator implements Comparator<Window> {

    @Override
    public int compare(Window w1, Window w2) {
        long w1BeginTime = w1.getBeginTime();
        long w2BeginTime = w2.getBeginTime();
        double temp = w1BeginTime - w2BeginTime;
        if(temp == 0)   temp = w1.getId() - w2.getId();     // 若时间一致则按id排
        return (int)temp;                                   // 按窗口开始时间升序排列
    }

}
