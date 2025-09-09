package org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class DecodeByHour extends Decode {


    private long timePeriod = 3600L;      // 按多少时间(s)划分时段, 作增量式检查

    /** 1
     * 计算收益
     * @param taskList 任务列表
     * @return         收益函数
     */
    public Score decode(List<Task> taskList) {
        // 1. 计算分配方案改变的资源
        Move move =  operator == null ? null : operator.getMoveList().get(0);
        Task movedTask = move == null ? null : (Task) move.getDecisionEntity();                         // 被改变的move
        Set<Task> movedTaskSet = new HashSet<>();                                                       // move前卫星所分配的任务集合
        List<Antenna> movedAntennaList = getMovedAntennaList(move);
        List<Orbit>   movedOrbitList   = getMovedOrbitList(move);
        if(movedAntennaList.size() == 0) {                                                              // 如果没有改变的卫星, 则全部解码
            movedAntennaList = getScenario().getAntennaList();
            movedOrbitList   = getScenario().getOrbitList();
        }
        if(movedTask != null)               movedTaskSet.add(movedTask);                                // lastMove涉及的task肯定被移动了

        // 2. 更新任务分配方案
        Map<Antenna, List<Task>> antennaTaskMap = updateAntennaTaskMap(movedTask, movedAntennaList);    // 更新卫星分配方案
        Map<Orbit  , List<Task>> orbitTaskMap   = updateOrbitTaskMap  (movedTask, movedOrbitList);      // 更新资源分配方案

        // 3.1 增量式检查（检查同一轨道的约束）
        Orbit fromOrbit = getFromOrbit(move);
        Orbit toOrbit   = getToOrbit(move);
        for(Orbit orbit : movedOrbitList) {                                                             // 遍历被更改的资源
            List<Task> allocatedTaskList = orbitTaskMap.get(orbit);                                     // 获得该资源所分配的任务
            if(allocatedTaskList == null)                           continue;                           // null, 跳过
            if(fromOrbit != toOrbit && fromOrbit == orbit)          continue;                           // 若移出资源!=移入资源, 则移出无需判断约束, 跳过
            for(Task task : allocatedTaskList) {                                                        // 遍历该资源所分配的任务
                if(!org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.checkOrbit(task, movedTask)) {  // 判断插入任务与已有任务是否冲突
                    getScenario().getScore().setHardScore(-1);
                    return updateTaskScoreMap(movedTaskSet, getScenario());                             // 增量式算分
                }
            }
        }
        // 3.2 增量式检查（检查同一设备的约束）
        Antenna fromAntenna = getFromAntenna(move);
        Antenna toAntenna   = getToAntenna(move);
        for(Antenna antenna : movedAntennaList) {                                                       // 遍历被更改的资源
            List<Task> allocatedTaskList = antennaTaskMap.get(antenna);                                 // 获得该资源所分配的任务
            if(allocatedTaskList == null)                           continue;                           // null, 跳过
            if(fromAntenna != toAntenna && fromAntenna == antenna)  continue;                           // 若移出资源!=移入资源, 则移出无需判断约束, 跳过
            List<Task> recentTaskList = new ArrayList<>();                                              // 该资源在本时段(及前后两时段)所分配的任务
            long time= movedTask.getRange().getBeginTime() / timePeriod;                                // 划分时段
            long gap = movedTask.getRange().getBeginTime() % timePeriod;                                // 在时段中, 弧段开始时间距离时段起点的间隔
            String timeIndex = "站" + antenna.getId() + "/时" + time;                                   // 当前时段
            String nearIndex = null;
            if(gap              < 30 * 60)                 nearIndex = "站" + antenna.getId() + "/时" + (time - 1);     // 距起点30分钟以内, 可能与[前一个时段]有冲突, 注: 转换时间360s, 最长弧段20分, 故定30分
            if(timePeriod - gap < 30 * 60)                 nearIndex = "站" + antenna.getId() + "/时" + (time + 1);     // 距终点30分钟以内, 可能与[后一个时段]有冲突
            if(antennaTimeTaskMap.get(timeIndex) != null)  recentTaskList.addAll(antennaTimeTaskMap.get(timeIndex));    // 当前时段内资源分得的任务
            if(antennaTimeTaskMap.get(nearIndex) != null)  recentTaskList.addAll(antennaTimeTaskMap.get(nearIndex));    // 相近时间内资源分得的任务
            for(Task task : recentTaskList) {                                                           // 遍历该资源所分配的任务
                if(!org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.checkAntenna(task, movedTask)) {// 判断插入任务与已有任务是否冲突
                    getScenario().getScore().setHardScore(-1);
                    return updateTaskScoreMap(movedTaskSet, getScenario());                             // 增量式算分
                }
            }
        }
        /* function ends */
        getScenario().getScore().setHardScore(0);
        return updateTaskScoreMap(movedTaskSet, getScenario());                                         // 增量式算分
    }


    /** 4
     * 增量式更新antennaTaskMap (用stream分类太慢了)
     * @param task        更改了的任务
     * @param antennaList 更改了的天线列表
     * @return            更新后的antennaTaskMap
     */
    public Map<Antenna, List<Task>> updateAntennaTaskMap(Task task, List<Antenna> antennaList) {
        if(task == null) {    // 空判断
            antennaTaskMap = getScenario().getTaskList().stream()                                       // 获取任务清单
                    .filter(t -> t.getRange() != null)                                                  // 过滤未决策的
                    .collect(Collectors.groupingBy(t -> t.getRange().getAntenna()));                    // 按天线分组
            antennaTimeTaskMap = getScenario().getTaskList().stream()                                   // 按站x/时x分组
                    .filter(t -> t.getRange() != null)
                    .collect(Collectors.groupingBy(t -> "站" + t.getRange().getAntenna().getId() + "/时" + t.getRange().getBeginTime() / timePeriod));
            return antennaTaskMap;
        }
        Antenna fromAntenna = antennaList.get(0);                                                       // 移出资源
        Antenna toAntenna   = antennaList.size() > 1 ? antennaList.get(1) : antennaList.get(0);         // 移入资源 (若只有1个)
        // a. 移出
        Move move = operator.getMoveList().get(0);
        if(fromAntenna != null) {
            antennaTaskMap.get(fromAntenna).remove(task);
            removeAntennaTimeTaskMap(move, fromAntenna, task);
        }
        // b. 移入
        if(toAntenna != null) {
            if(!antennaTaskMap.containsKey(toAntenna)) antennaTaskMap.put(toAntenna, new ArrayList<>());// 初始化
            antennaTaskMap.get(toAntenna).add(task);
            addAntennaTimeTaskMap(move, toAntenna, task);
        }
        return antennaTaskMap;
    }


    protected Map<String, List<Task>> antennaTimeTaskMap = new HashMap<>();                                     // 记录上一次的天线(/小时段)-任务分配方案, 用于增量式计算


    /**
     * 基于当前move算子(考虑undo情况), 获得task从哪个时段移动到哪个时段
     * @param move move算子
     * @return     移出时间段/移入时间段
     */
    public Long getFromTime(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingOldValue();
        if(range == null)   return null;
        return range.getBeginTime() / timePeriod;    // 获取时段id: 时间 / 时间周期 (取整)
    }
    public Long getToTime(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingNewValue();
        return range.getBeginTime() / timePeriod;
    }


    /**
     * 更新 antennaTimeTaskMap
     * @param move    当前move
     * @param antenna 待更新的资源
     * @param task    待更新的任务(移入/移出)
     */
    public void addAntennaTimeTaskMap(Move move, Antenna antenna, Task task) {
        long time = getToTime(move);
        String index = "站" + antenna.getId() + "/时" + time;
        if(!antennaTimeTaskMap.containsKey(index))      antennaTimeTaskMap.put(index, new ArrayList<>());
        antennaTimeTaskMap.get(index).add(task);
    }
    public void removeAntennaTimeTaskMap(Move move, Antenna antenna, Task task) {
        Long time = getFromTime(move);
        if(time == null)    return;
        String index = "站" + antenna.getId() + "/时" + time;
        if(antennaTimeTaskMap.get(index) == null)       return;
        antennaTimeTaskMap.get(index).remove(task);
    }




    /* class ends */
}
