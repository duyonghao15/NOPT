package org.nudtopt.realworldproblems.satelliterangescheduling.tool.exporter;

import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.comparator.BeginTimeComparator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.nudtopt.api.tool.function.Exporter.writeFile;

public class Exporter {


    public static void export(Scenario scenario, String filePath) throws IOException {
        List<Task> scheduledTaskList = new ArrayList<>();
        List<Task> unScheduledTaskList = new ArrayList<>();
        for(Task task : scenario.getTaskList()) {
            if(task.getRange() != null) {
                scheduledTaskList.add(task);      // a. 成功调度任务
            } else {
                unScheduledTaskList.add(task);    // b. 未调度任务
            }
        }

        Collections.sort(scheduledTaskList, new BeginTimeComparator());
        StringBuilder str = new StringBuilder();
        SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat minFormat  = new SimpleDateFormat("HH:mm:ss");
        str.append("设备代号,设备名,数传任务,卫星,轨道圈号,日期,窗口开始时间,窗口结束时间,窗口持续时间(s)\n");
        // a. 成功调度任务
        for(Antenna antenna : scenario.getAntennaList()) {
            for(Task task : scheduledTaskList) {
                if(task.getRange().getAntenna() != antenna) continue;
                str.append(antenna.getId()).append(",")
                   .append(antenna.getName()).append(",")
                   .append(task).append(",")
                   .append(task.getRange().getOrbit().getSatellite().getName()).append(",")
                   .append(task.getRange().getOrbit().getId()).append(",")
                   .append(dayFormat.format(task.getRange().getOrbit().getDay().getBeginDate())).append(",")
                   .append(minFormat.format(task.getRange().getBeginTime() * 1000)).append(",")
                   .append(minFormat.format(task.getRange().getEndTime() * 1000)).append(",")
                   .append(task.getRange().getCapability()).append(",");
                str.append("\n");
            }
        }
        // b. 未调度任务
        for(Task task : unScheduledTaskList) {
            str.append(" ,null,").append(task.getId()).append(",").append("\n");
        }
        /* function ends */
        writeFile(str, filePath, ".csv");
    }

/* class ends */
}
