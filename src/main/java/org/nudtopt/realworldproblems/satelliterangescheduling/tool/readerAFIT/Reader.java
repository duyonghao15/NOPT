package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerAFIT;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.apidata.Data;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Reader extends MainLogger {


    public static void main(String[] args) throws Exception {
        new Reader().readData(3);
    }

    public Scenario readData(int day) throws Exception {
        Scenario lowScenario = readData(day, "reqlf1", null);
        Scenario allScenario = readData(day, "requp1", lowScenario);
        return allScenario;
    }


    public Scenario readData(int dayId, String lowHigh, Scenario scenario) throws Exception {
        logger.info("正读取 -> " + (lowHigh.equals("reqlf1") ? "低轨" : "高轨") + "卫星测控标准数据 ... ...");
        InputStream in = Data.class.getResourceAsStream("satelliterangescheduling/Benchmarks-for-AFIT/" + lowHigh + (dayId+1) + ".dat");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        String[] segments;
        long minTime = scenario != null ? scenario.getBeginTime() : 99999999999L;
        long maxTime = scenario != null ? scenario.getEndTime()   : 0;

        // 判断低轨还是高轨
        List<Task> taskList = new ArrayList<>();
        List<Antenna> antennaList = new ArrayList<>();
        long lowLastTaskId = 0;
        if(scenario == null) {
            scenario = new Scenario();          // 新建场景
            scenario.setId((long) dayId);
            Day day = new Day();                // 新建天
            Orbit orbit = new Orbit();          // 新建轨道(默认一条轨道)
            day.setId(scenario.getId());
            orbit.setId(day.getId());
            orbit.setDay(day);
            scenario.getDayList().add(day);
            scenario.getOrbitList().add(orbit);
        } else {
            taskList = scenario.getTaskList();
            antennaList = scenario.getAntennaList();
            lowLastTaskId = taskList.get(taskList.size() - 1).getId();
        }

        // 开始遍历
        while ((line = br.readLine()) != null) {
            segments = line.split("\\s+");
            long taskId = Long.valueOf(segments[1]) + lowLastTaskId;
            String antennaName = segments[2];
            long windowBeginTime = Long.valueOf(segments[3]);
            long windowEndTime = Long.valueOf(segments[4]);
            long taskDuration = Long.valueOf(segments[5]);
            long taskSetUp = Long.valueOf(segments[6]);

            // a. 新建task, 初始化
            Task task = new Task();
            if(taskList.size() == 0 || taskId != taskList.get(taskList.size() - 1).getId()) {
                task.setId(taskId);
                task.setSetup(taskSetUp);
                task.setDuration(taskDuration);
                task.setOptionalRangeList(new ArrayList<>());
                taskList.add(task);
            }
            task = taskList.get(taskList.size() - 1);                 // 当前task

            // b. 新建antenna
            Antenna antenna = new Antenna();
            for(Antenna a : antennaList) {
                if(a.getName().equals(antennaName)) {
                    antenna = a;
                    break;
                }
            }
            if(antenna.getId() == null) {
                antenna.setId((long) antennaList.size());
                antenna.setName(antennaName);
                antennaList.add(antenna);
            }

            // c. 新建range
            long earlistBeginTime = windowBeginTime;                // 最早开始时间
            long latestBeginTime = windowEndTime - taskDuration;    // 最晚开始时间
            for(long beginTime = earlistBeginTime; beginTime <= latestBeginTime ; beginTime += 1.0) {
                Range range = new Range();
                range.setId(beginTime);
                range.setAntenna(antenna);
                range.setBeginTime(beginTime);
                range.setEndTime(beginTime + task.getDuration());
                range.setCapability(range.getEndTime() - range.getBeginTime());
                range.setOrbit(scenario.getOrbitList().get(0));     // 默认就一条轨道
                task.getOptionalRangeList().add(range);
                minTime = Math.min(minTime, range.getBeginTime());
                maxTime = Math.max(maxTime, range.getEndTime());
            }
            /*loop ends*/
        }

        // 3. 新建/更新scenario
        scenario.setTaskList(taskList);
        scenario.setAntennaList(antennaList);
        scenario.setBeginTime(minTime);
        scenario.setEndTime(maxTime);
        scenario.getDayList().get(0).setBeginTime(scenario.getBeginTime());
        scenario.getDayList().get(0).setEndTime(scenario.getEndTime());
        scenario.getDayList().get(0).setCapability(scenario.getEndTime() - scenario.getBeginTime());
        scenario.getOrbitList().get(0).setBeginTime(scenario.getBeginTime());
        scenario.getOrbitList().get(0).setEndTime(scenario.getEndTime());
        scenario.getOrbitList().get(0).setCapability(scenario.getEndTime() - scenario.getBeginTime());
        int meanRangeNum = 0;
        for(Task task : taskList) {
            meanRangeNum += task.getOptionalRangeList().size();
        }
        logger.info("已读取 -> 卫星测控标准任务 " + taskList.size() + " 个,\t平均每个任务可选弧段 " + meanRangeNum / taskList.size() + " 条,\t场景周期: " + scenario.getBeginTime() + " ~ " + scenario.getEndTime() + ".\n");
        return scenario;
    }


/* class ends */
}
