package org.nudtopt.realworldproblems.satelliterangescheduling.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scenario extends Solution {

    @DecisionEntityList
    private List<Task> taskList = new ArrayList<>();                                    // 待规划任务列表(不一定是全部的, 可分段并行)

    private List<Task> totalTaskList = new ArrayList<>();                               // 全部任务列表

    private List<Day> dayList = new ArrayList<>();                                      // 日期列表(北京时)

    private List<Satellite> satelliteList = new ArrayList<>();                          // 卫星列表

    private List<Orbit> orbitList = new ArrayList<>();                                  // 轨道列表

    private List<Antenna> antennaList = new ArrayList<>();                              // 天线设备列表

    private List<Range> rangeList = new ArrayList<>();                                  // 弧段列表

    private Map<Long, Day> dayMap = new HashMap<>();                                    // 根据: 日期id,          快速索引日期(北京时)
    private Map<Long, Antenna> antennaMap = new HashMap<>();                            // 根据: 天线id,          快速索引天线
    private Map<Long, Satellite> satelliteMap = new HashMap<>();                        // 根据: 卫星id,          快速索引卫星
    private Map<String, Day> satelliteDayMap = new HashMap<>();                         // 根据: 卫星id/日期id    快速索引日期(卫星时)
    private Map<String, Orbit> orbitMap = new HashMap<>();                              // 根据: 卫星id/轨道圈号, 快速索引轨道
    private Map<String, List<Range>> rangeListMap = new HashMap<>();                    // 根据: 星/站/天,        快速索引出当天有哪些弧段

    // getter & setter
    public List<Task> getTaskList() {
        return taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public List<Task> getTotalTaskList() {
        return totalTaskList;
    }
    public void setTotalTaskList(List<Task> totalTaskList) {
        this.totalTaskList = totalTaskList;
    }

    public List<Day> getDayList() {
        return dayList;
    }
    public void setDayList(List<Day> dayList) {
        this.dayList = dayList;
    }

    public List<Satellite> getSatelliteList() {
        return satelliteList;
    }
    public void setSatelliteList(List<Satellite> satelliteList) {
        this.satelliteList = satelliteList;
    }

    public List<Orbit> getOrbitList() {
        return orbitList;
    }
    public void setOrbitList(List<Orbit> orbitList) {
        this.orbitList = orbitList;
    }

    public List<Antenna> getAntennaList() {
        return antennaList;
    }
    public void setAntennaList(List<Antenna> antennaList) {
        this.antennaList = antennaList;
    }

    public List<Range> getRangeList() {
        return rangeList;
    }
    public void setRangeList(List<Range> rangeList) {
        this.rangeList = rangeList;
    }

    public Map<Long, Day> getDayMap() {
        return dayMap;
    }
    public void setDayMap(Map<Long, Day> dayMap) {
        this.dayMap = dayMap;
    }

    public Map<Long, Satellite> getSatelliteMap() {
        return satelliteMap;
    }
    public void setSatelliteMap(Map<Long, Satellite> satelliteMap) {
        this.satelliteMap = satelliteMap;
    }

    public Map<String, Day> getSatelliteDayMap() {
        return satelliteDayMap;
    }
    public void setSatelliteDayMap(Map<String, Day> satelliteDayMap) {
        this.satelliteDayMap = satelliteDayMap;
    }

    public Map<String, Orbit> getOrbitMap() {
        return orbitMap;
    }
    public void setOrbitMap(Map<String, Orbit> orbitMap) {
        this.orbitMap = orbitMap;
    }

    public Map<Long, Antenna> getAntennaMap() {
        return antennaMap;
    }
    public void setAntennaMap(Map<Long, Antenna> antennaMap) {
        this.antennaMap = antennaMap;
    }

    public Map<String, List<Range>> getRangeListMap() {
        return rangeListMap;
    }
    public void setRangeListMap(Map<String, List<Range>> rangeListMap) {
        this.rangeListMap = rangeListMap;
    }

/* class ends */
}
