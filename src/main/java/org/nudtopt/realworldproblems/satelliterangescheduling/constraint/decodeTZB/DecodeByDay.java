package org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class DecodeByDay extends Constraint {

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
        // 1. 计算分配方案改变的卫星 (需重新解码)
        Move lastMove = lastOperator == null ? null : lastOperator.getMoveList().get(0);
        Task movedTask = lastMove == null ? null : (Task)lastMove.getDecisionEntity();                  // 被改变的move
        Set<Task> movedTaskSet = new HashSet<>();                                                       // move前卫星所分配的任务集合
        List<Antenna> movedAntennaList = getMovedAntennaList(lastMove);
        if(movedAntennaList.size() == 0)    movedAntennaList = getScenario().getAntennaList();          // 如果没有改变的卫星, 则全部解码
        if(movedTask != null)               movedTaskSet.add(movedTask);                                // lastMove涉及的task肯定被移动了

        // 2. 更新任务分配方案(每天)
        Map<String, List<Task>> antennaTaskMap = updateAntennaTaskMap(movedTask, movedAntennaList);    // 更新卫星分配方案
        Map<Antenna, List<Task>> antennaTaskMapOld = updateAntennaTaskMapOld(movedTask, movedAntennaList);    // 更新卫星分配方案


        // 3. 增量式检查
        boolean check = true;
        for(Antenna antenna : movedAntennaList) {
            if(movedTask == null)                           continue;                                   // 有null, 跳过
            // todo 按天为单位增量式计算
            String todayKey     = movedTask.getDay().getId()     + "/" + antenna.getId();               // 当天的key: dayId/antennaId
            String yesterdayKey = movedTask.getDay().getId() - 1 + "/" + antenna.getId();               // 昨天的key
            String tomorrowKey  = movedTask.getDay().getId() + 1 + "/" + antenna.getId();               // 明天的key
            List<Task> allocatedTaskList = new ArrayList<>();
            if(antennaTaskMap.get(todayKey)    != null)     allocatedTaskList.addAll(antennaTaskMap.get(todayKey));         // 当天该天线分配的任务
            if(antennaTaskMap.get(yesterdayKey)!= null)     allocatedTaskList.addAll(antennaTaskMap.get(yesterdayKey));     // 前一天的天线分配的任务
            if(antennaTaskMap.get(tomorrowKey) != null)     allocatedTaskList.addAll(antennaTaskMap.get(tomorrowKey));      // 后一天的天线分配的任务
            if(allocatedTaskList.size() == 0)               continue;                                   // 无任务, 跳过
            // todo end
            if(!allocatedTaskList.contains(movedTask))      continue;                                   // 没有movedTask, 即从这个资源中移走, 肯定不会产生冲突
            Task conflictTask = null;
            for(Task task : allocatedTaskList) {
                check = org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.check(task, movedTask);     // 判断插入任务与已有任务是否冲突
                if(!check) {
                    conflictTask = task;
                    break;
                }
            }

            Task conflictTaskOld = null;
            boolean checkOld = true;
            List<Task> allocatedTaskListOld = antennaTaskMapOld.get(antenna);                                 // 该天线分配的任务
            if(allocatedTaskListOld == null || movedTask == null)  continue;                               // 有null, 跳过
            if(!allocatedTaskListOld.contains(movedTask))          continue;                               // 没有movedTask, 即从这个资源中移走, 肯定不会产生冲突
            for(Task task : allocatedTaskListOld) {
                checkOld = org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.check(task, movedTask);     // 判断插入任务与已有任务是否冲突
                if(!checkOld) {
                    conflictTaskOld = task;
                    break;
                }
            }
            if(check != checkOld) {
                org.nudtopt.realworldproblems.satelliterangescheduling.model.Constraint.check(conflictTaskOld, movedTask);
                long a = 0;
            }


            if(!check)  break;
        }
        if(!check)  getScenario().getScore().setHardScore(-1);
        else        getScenario().getScore().setHardScore(0);
        /* function ends */
        Score score = updateTaskScoreMap(movedTaskSet, getScenario());                                  // 增量式算分
        return score;
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
     * (更快的方式)
     * @param move 上一次的move
     * @return 需要重新更新解码的资源列表
     */
    public List<Antenna> getMovedAntennaList(Move move) {
        List<Antenna> antennaList = new ArrayList<>();
        if(move == null)   return antennaList;
        Object oldValue  = move.getOldValue();
        Object newValue  = move.getNewValue();
        Task task = (Task) move.getDecisionEntity();
        if(move.getName().equals("range")) {
            // 1. 决策变量为窗口时
            if(oldValue != null) {
                Range oldRange = (Range) oldValue;
                Antenna oldAntenna = oldRange.getAntenna();
                antennaList.add(oldAntenna);
            }
            if(newValue != null) {
                Range newRange = (Range) newValue;
                Antenna newAntenna = newRange.getAntenna();
                if(!antennaList.contains(newAntenna)) {
                    antennaList.add(newAntenna);
                }
            }
        } else if (task.getRange() != null) {
            // 2. 其他决策变量时
            Antenna antenna = task.getRange().getAntenna();
            if(!antennaList.contains(antenna)) {
                antennaList.add(antenna);
            }
        }
        return antennaList;
    }



    /** 4
     * 增量式更新antennaTaskMap (用stream分类太慢了)
     * @param movedTask        更改了的任务
     * @param movedAntennaList 更改了的天线列表
     * @return                 更新后的antennaTaskMap(按每天分: String=dayId+antennaId)
     */
    public Map<String, List<Task>> updateAntennaTaskMap(Task movedTask, List<Antenna> movedAntennaList) {
        if(movedTask == null) {    // 空判断
            antennaTaskMap = getScenario().getTaskList().stream()                                                   // 获取任务清单
                    .filter(task -> task.getRange() != null)                                                        // 过滤未决策的
                    .collect(Collectors.groupingBy(task -> task.getDay().getId() + "/" + task.getRange().getAntenna().getId())); // 按天id/天线id分组
            return antennaTaskMap;
        }
        // 1. 当只涉及1个资源时
        if(movedAntennaList.size() == 1) {
            Antenna movedAntenna = movedAntennaList.get(0);
            String key = movedTask.getDay().getId() + "/" + movedAntenna.getId();                                   // key: 天id/天线id
            if(!antennaTaskMap.containsKey(key))            antennaTaskMap.put(key, new ArrayList<>());             // 若map不含该antenna, 则在map中初始化
            List<Task> movedTaskList = antennaTaskMap.get(key);                                                     // 原分配给该资源的方案
            if(movedTaskList.contains(movedTask)) {                                                                 // 若分配方案含task
                if(movedTask.getRange() == null) {                                                                  // 且该task的变量已置空
                    movedTaskList.remove(movedTask);                                                                // 则将该task移除
                }
            } else {                                                                                                // 反之, 若分配方案不含改task
                if(movedTask.getRange() != null) {                                                                  // 且该task赋了变量
                    movedTaskList.add(movedTask);                                                                   // 则将该task加入资源的分配方案中
                }
            }
        }
        // 2. 当涉及2个资源时
        if(movedAntennaList.size() == 2) {
            Antenna movedAntenna_0 = movedAntennaList.get(0);                                                       // 涉及的第0资源
            Antenna movedAntenna_1 = movedAntennaList.get(1);                                                       // 涉及的第1资源
            String key0 = movedTask.getDay().getId() + "/" + movedAntenna_0.getId();                                // key: 天id/天线id
            String key1 = movedTask.getDay().getId() + "/" + movedAntenna_1.getId();
            if(!antennaTaskMap.containsKey(key0))    antennaTaskMap.put(key0, new ArrayList<>());                   // 若map不含该antenna, 则在map中初始化
            if(!antennaTaskMap.containsKey(key1))    antennaTaskMap.put(key1, new ArrayList<>());
            List<Task> movedTaskList_0 = antennaTaskMap.get(key0);                                                  // 原分配给该资源的方案
            List<Task> movedTaskList_1 = antennaTaskMap.get(key1);
            if(movedTaskList_0.contains(movedTask)) {                                                               // 若卫星0的分配方案包含task
                movedTaskList_0.remove(movedTask);                                                                  // 则将该task移除(一定是从资源0移动到资源1)
                if(movedTask.getRange() != null) {                                                                  // 若task赋了变量
                    movedTaskList_1.add(movedTask);                                                                 // 则将该task加入资源1的分配方案中
                }
            } else if (movedTaskList_1.contains(movedTask)) {                                                       // 反之, 若资源0分分配方案不含task (则资源1的分配方案一定包含)
                movedTaskList_1.remove(movedTask);                                                                  // 则将该task从资源1方案中移除(一定是从资源1移动到卫星2)
                if(movedTask.getRange() != null) {                                                                  // 若task赋了变量
                    movedTaskList_0.add(movedTask);                                                                 // 则将该task加入卫星0的分配方案
                }
            }
        }
        return antennaTaskMap;
    }



    private Map<String, List<Task>> antennaTaskMap = new HashMap<>();                                               // 记录上一次的某一天天线-任务分配/成像方案, 用于增量式计算(String=dayId+antennaId)
    private Map<Antenna, List<Task>> antennaTaskMapOld = new HashMap<>();                                           // 记录上一次的天线-任务分配/成像方案, 用于增量式计算

    private Map<Task, Score> taskScoreMap = new HashMap<>();                                                        // 当前每个任务对应的得分

    public Scenario getScenario() {
        return (Scenario) solution;
    }



    /** 4
     * 增量式更新antennaTaskMap (用stream分类太慢了)
     * @param movedTask        更改了的任务
     * @param movedAntennaList 更改了的天线列表
     * @return                 更新后的antennaTaskMap
     */
    public Map<Antenna, List<Task>> updateAntennaTaskMapOld(Task movedTask, List<Antenna> movedAntennaList) {
        if(movedTask == null) {    // 空判断
            antennaTaskMapOld = getScenario().getTaskList().stream()                                                   // 获取任务清单
                    .filter(task -> task.getRange() != null)                                                        // 过滤未决策的
                    .collect(Collectors.groupingBy(task -> task.getRange().getAntenna()));                          // 按天线分组
            return antennaTaskMapOld;
        }
        // 1. 当只涉及1个资源时
        if(movedAntennaList.size() == 1) {
            Antenna movedAntenna = movedAntennaList.get(0);
            if(!antennaTaskMapOld.containsKey(movedAntenna))   antennaTaskMapOld.put(movedAntenna, new ArrayList<>());    // 若map不含该antenna, 则在map中初始化
            List<Task> movedTaskList = antennaTaskMapOld.get(movedAntenna);                                            // 原分配给该资源的方案
            if(movedTaskList.contains(movedTask)) {                                                                 // 若分配方案含task
                if(movedTask.getRange() == null) {                                                                  // 且该task的变量已置空
                    movedTaskList.remove(movedTask);                                                                // 则将该task移除
                }
            } else {                                                                                                // 反之, 若分配方案不含改task
                if(movedTask.getRange() != null) {                                                                  // 且该task赋了变量
                    movedTaskList.add(movedTask);                                                                   // 则将该task加入资源的分配方案中
                }
            }
        }
        // 2. 当涉及2个资源时
        if(movedAntennaList.size() == 2) {
            Antenna movedAntenna_0 = movedAntennaList.get(0);                                                       // 涉及的第0资源
            Antenna movedAntenna_1 = movedAntennaList.get(1);                                                       // 涉及的第1资源
            if(!antennaTaskMapOld.containsKey(movedAntenna_0)) antennaTaskMapOld.put(movedAntenna_0, new ArrayList<>());  // 若map不含该antenna, 则在map中初始化
            if(!antennaTaskMapOld.containsKey(movedAntenna_1)) antennaTaskMapOld.put(movedAntenna_1, new ArrayList<>());
            List<Task> movedTaskList_0 = antennaTaskMapOld.get(movedAntenna_0);                                        // 原分配给该资源的方案
            List<Task> movedTaskList_1 = antennaTaskMapOld.get(movedAntenna_1);
            if(movedTaskList_0.contains(movedTask)) {                                                               // 若卫星0的分配方案包含task
                movedTaskList_0.remove(movedTask);                                                                  // 则将该task移除(一定是从资源0移动到资源1)
                if(movedTask.getRange() != null) {                                                                  // 若task赋了变量
                    movedTaskList_1.add(movedTask);                                                                 // 则将该task加入资源1的分配方案中
                }
            } else if (movedTaskList_1.contains(movedTask)) {                                                       // 反之, 若资源0分分配方案不含task (则资源1的分配方案一定包含)
                movedTaskList_1.remove(movedTask);                                                                  // 则将该task从资源1方案中移除(一定是从资源1移动到卫星2)
                if(movedTask.getRange() != null) {                                                                  // 若task赋了变量
                    movedTaskList_0.add(movedTask);                                                                 // 则将该task加入卫星0的分配方案
                }
            }
        }
        return antennaTaskMapOld;
    }



/* class ends */
}
