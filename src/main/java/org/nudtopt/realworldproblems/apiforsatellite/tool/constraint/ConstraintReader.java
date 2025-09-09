package org.nudtopt.realworldproblems.apiforsatellite.tool.constraint;

import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.tool.constraint.GJ.*;
import org.nudtopt.realworldproblems.apiforsatellite.tool.constraint.TZ.TZOPT;
import org.nudtopt.realworldproblems.apiforsatellite.tool.constraint.TZ.TZSAR;


public class ConstraintReader {

    /**
     * 根据卫星对象, 对其约束constraint变量进行赋值
     */
    public static void set(Satellite satellite) {
        Constraint constraint;
        switch (satellite.getName()) {
            // GJ
            case "高景1号A星":   constraint = new GJ0101_02();           break;
            case "高景1号B星":   constraint = new GJ0101_02();           break;
            case "高景1号C星":   constraint = new GJ0103_04();           break;
            case "高景1号D星":   constraint = new GJ0103_04();           break;
            case "GJ1-1":       constraint = new GJ0101_02();           break;
            case "GJ1-2":       constraint = new GJ0101_02();           break;
            case "GJ1-3":       constraint = new GJ0103_04();           break;
            case "GJ1-4":       constraint = new GJ0103_04();           break;
            default:
                // 天智杯数据
                if(satellite.getName().contains("opt")) {
                    constraint = new TZOPT();
                } else {
                    constraint = new TZSAR();
                }
                break;
        }
        satellite.setConstraint(constraint);
    }

/* class ends */
}
