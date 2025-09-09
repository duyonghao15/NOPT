package org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Decode extends Constraint {

    @Override
    public Score calScore() {
        List<Task> taskList = getScenario().getTaskList();
        Score score = decode(taskList);
        return score;
    }


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
        Map<Antenna, List<Task>> antennaTaskMap = updateAntennaTaskMap(movedTask, movedAntennaList);    // 更新资源分配方案
        Map<Orbit  , List<Task>> orbitTaskMap   = updateOrbitTaskMap  (movedTask, movedOrbitList);      // 更新资源分配方案

        // 3.1 增量式检查（检查同一轨道的约束）
        Orbit fromOrbit = getFromOrbit(move);
        Orbit toOrbit   = getToOrbit(move);
        for(Orbit orbit : movedOrbitList) {                                                             // 遍历被更改的资源
            List<Task> allocatedTaskList = orbitTaskMap.get(orbit);                                     // 获得该资源所分配的任务
            if(allocatedTaskList == null)                           continue;                           // null, 跳过
            if(fromOrbit != toOrbit && fromOrbit == orbit)          continue;                           // 若移出资源!=移入资源, 则移出无需判断约束, 跳过
            for(Task task : allocatedTaskList) {                                                        // 遍历该资源所分配的任务
                if(!org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.check(task, movedTask)) {       // 判断插入任务与已有任务是否冲突
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
            for(Task task : allocatedTaskList) {                                                        // 遍历该资源所分配的任务
                if(!org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.check(task, movedTask)) {       // 判断插入任务与已有任务是否冲突
                    getScenario().getScore().setHardScore(-1);
                    return updateTaskScoreMap(movedTaskSet, getScenario());                             // 增量式算分
                }
            }
        }
        /* function ends */
        getScenario().getScore().setHardScore(0);
        return updateTaskScoreMap(movedTaskSet, getScenario());                                         // 增量式算分
    }



    /** 2
     * 基于被移动的taskList, 进一步考虑其关联的任务, 进行增量式计算收益
     * @param movedTaskSet  被移动的task的list
     * @param scenario      当前场景
     * @return              计算后的得分
     */
    public Score updateTaskScoreMap(Set<Task> movedTaskSet, Scenario scenario) {
        List<Task> relatingTaskList;
        // 1. 获取改变的任务列表
        if(movedTaskSet.size() == 0) {
            relatingTaskList = scenario.getTaskList();
        } else {
            relatingTaskList = new ArrayList<>(movedTaskSet);
            relatingTaskList.sort(new IdComparator());
        }
        // 2. 对相关任务进行重算分
        Score score = scenario.getScore();
        for(Task task : relatingTaskList) {
            Score oldScore = taskScoreMap.get(task);                                // 获取旧分
            if(oldScore != null)      score = score.cutScore(oldScore);             // 减去旧分
            Score newScore = CalScore.calObjective(Collections.singletonList(task));// 计算新分
            taskScoreMap.put(task, newScore);                                       // 更新新分
            score = score.addScore(newScore);                                       // 加上新分
        }
        /* function ends */
        return score;
    }


    /** 3
     * 根据某一个move, 确定哪些资源需要重新更新解码
     * @param move 上一次的move
     * @return     需要重新更新解码的资源列表
     */
    public List<Antenna> getMovedAntennaList(Move move) {
        List<Antenna> antennaList = new ArrayList<>();
        if(move == null)   return antennaList;
        Antenna fromAntenna = getFromAntenna(move);
        Antenna toAntenna   = getToAntenna(move);
        antennaList.add(fromAntenna);
        if(toAntenna != fromAntenna) {
            antennaList.add(toAntenna);
        }
        return antennaList;
    }
    public List<Orbit> getMovedOrbitList(Move move) {
        List<Orbit> orbitList = new ArrayList<>();
        if(move == null)   return orbitList;
        Orbit fromOrbit = getFromOrbit(move);
        Orbit toOrbit   = getToOrbit(move);
        orbitList.add(fromOrbit);
        if(toOrbit != fromOrbit) {
            orbitList.add(toOrbit);
        }
        return orbitList;
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
            return antennaTaskMap;
        }
        // a. 只有一个资源, 即在同一个资源上操作, 分配方案不变
        if(antennaList.size() <= 1) {
            return antennaTaskMap;
        }
        Antenna fromAntenna = antennaList.get(0);                                                       // 移出资源
        Antenna toAntenna   = antennaList.get(1);                                                       // 移入资源
        // b. 移出
        if(fromAntenna != null) {
            antennaTaskMap.get(fromAntenna).remove(task);
        }
        // c. 移入
        if(toAntenna != null) {
            if(!antennaTaskMap.containsKey(toAntenna)) antennaTaskMap.put(toAntenna, new ArrayList<>());// 初始化
            antennaTaskMap.get(toAntenna).add(task);
        }
        return antennaTaskMap;
    }



    /** 4-2
     * 增量式更新orbitTskMap (用stream分类太慢了)
     * @param task      更改了的任务
     * @param orbitList 更改了的天线列表
     * @return          更新后的orbitTaskMap
     */
    public Map<Orbit, List<Task>> updateOrbitTaskMap(Task task, List<Orbit> orbitList) {
        if(task == null) {    // 空判断
            orbitTaskMap = getScenario().getTaskList().stream()                                         // 获取任务清单
                    .filter(t -> t.getRange() != null)                                                  // 过滤未决策的
                    .collect(Collectors.groupingBy(t -> t.getRange().getOrbit()));                      // 按天线分组
            return orbitTaskMap;
        }
        // a. 只有一个资源, 即在同一个资源上操作, 分配方案不变
        if(orbitList.size() <= 1) {
            return orbitTaskMap;
        }
        Orbit fromOrbit = orbitList.get(0);                                                             // 移出资源
        Orbit toOrbit   = orbitList.get(1);                                                             // 移入资源
        // b. 移出
        if(fromOrbit != null) {
            orbitTaskMap.get(fromOrbit).remove(task);
        }
        // c. 移入
        if(toOrbit != null) {
            if(!orbitTaskMap.containsKey(toOrbit)) orbitTaskMap.put(toOrbit, new ArrayList<>());        // 初始化
            orbitTaskMap.get(toOrbit).add(task);
        }
        return orbitTaskMap;
    }



    protected Map<Antenna, List<Task>> antennaTaskMap = new HashMap<>();                                // 记录上一次的设备-任务分配方案, 用于增量式计算
    protected Map<Orbit, List<Task>>   orbitTaskMap   = new HashMap<>();                                // 记录上一次的卫星轨道-任务分配方案, 用于增量式检查同轨任务类型不得相同的约束
    protected Map<Task, Score> taskScoreMap = new HashMap<>();                                          // 当前每个任务对应的得分

    public Scenario getScenario() {
        return (Scenario) solution;
    }

    /**
     * 基于当前move算子(考虑undo情况), 获得task从哪个资源移动到哪个资源
     * @param move move算子
     * @return     移出资源/移入资源
     */
    public Antenna getFromAntenna(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingOldValue();
        if(range == null)   return null;
        return range.getAntenna();
    }
    public Antenna getToAntenna(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingNewValue();
        if(range == null)   return null;
        return range.getAntenna();
    }

    public Orbit getFromOrbit(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingOldValue();
        if(range == null)   return null;
        return range.getOrbit();
    }
    public Orbit getToOrbit(Move move) {
        if(move == null)    return null;
        Range range = (Range) move.getDoingNewValue();
        if(range == null)   return null;
        return range.getOrbit();
    }

/* class ends */
}
