package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.app;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint.TasksConstraint;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Arc;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Plan;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Task;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class Main extends MainLogger {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Main.class);
        /*System.out.println(Arrays.toString(args));
        if (args.length != 3)
            logger.error("参数数量错误，请输入场景文件夹路径、规划方案路径和约束检查结果输出路径。");
        String settingPath  = args[0];  //场景数据的文件夹
        String planDataPath = args[1];  //规划方案的绝对路径
        String logDirPath   = args[2];  //结果要写入到那个文件夹里*/
        String rootPath = "C:\\Users\\caimengsi\\Desktop\\xml\\科目2竞赛数据";
        String fileName = "000_05_20240716150051_GNPLAN";
        String id = fileName.split("_")[1];
        String settingPath  = rootPath    + "\\" + id;                              //场景数据的文件夹
        String logDirPath   = settingPath + "\\" + "满分结果";                      //结果要写入到那个文件夹里
        String planDataPath = logDirPath  + "\\" + fileName + ".xml";               //规划方案的绝对路径

        String antennaDataPath = settingPath + "\\Device_Normalized_Simple.XML";
        String taskDataPath    = settingPath + "\\Requsets_Normalized_Simple.XML";
        String arcDataPath     = settingPath + "\\Forecast_all_satellite_Simple.XML";

        String[] parts = planDataPath.split(Pattern.quote(File.separator));
        String logPath = logDirPath + "\\约束检查结果-" + parts[parts.length - 1] + "-" + System.currentTimeMillis() +
                ".txt";

        // 日志输出重定向
        PrintStream tofile = new PrintStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(logPath))), true);
        // 日志输出重定向到控制台和文件
        System.setOut(new PrintStream(new TeeOutputStream(System.out, tofile)));

        logger.info(Utils.logFormatString("正在读取场景数据"));
        //实例化数据读取器
        DataReader dataReader = new DataReader();
        // 读取所有设备数据
        Map<Long, Antenna> antennaMap = dataReader.readAntennaData(antennaDataPath);
        // 读取所有弧段数据
        List<Arc> arcList = dataReader.readArcData(arcDataPath);
        // 读取所有任务数据
        List<Task> taskList = dataReader.readTaskData(taskDataPath);
        // 初始化配置所有弧度和任务
        dataReader.dataInit(antennaMap, arcList, taskList);
        Map<String, Arc>  SDAOArcMap = dataReader.getSDAOArcMap(arcList);
        Map<String, Task> SDSTaskMap = dataReader.getSDSTaskMap(taskList);
        // 读取所有计划数据
        List<Plan> planList = dataReader.readPlanData(planDataPath);
        logger.info(Utils.logFormatString("场景数据读取完成"));

        // 首先检查一元约束：每个任务安排的弧段是否在任务可用弧段中
        boolean oneELeConstraintFlag = checkOneEleConstraint(planList, antennaMap, SDSTaskMap, SDAOArcMap, logger);
        if (!oneELeConstraintFlag) return;

        // 再检查二元约束：任务与任务之间是否有冲突
        int twoEleConstraintNum = checkTwoEleConstraintParallel(planList, antennaMap, SDSTaskMap, parts, logger);

        // 对任务进行评分
        if (twoEleConstraintNum == 0) {
            calResultScore(planList, taskList, antennaMap, SDSTaskMap, logger);
        } else {
            logger.info(Utils.logFormatString("方案不可行，得分：0"));
        }

        logger.info("约束检查日志保存在: " + logPath);
    }

    public static boolean checkOneEleConstraint(List<Plan> planList,
                                                Map<Long, Antenna> antennaMap,
                                                Map<String, Task> SDSTaskMap,
                                                Map<String, Arc> SDAOArcMap, Logger logger) {
        logger.info(Utils.logFormatString("正在检查规划方案一元约束"));
        boolean oneEleConstraintFlag = true;  // 是否满足任务弧段一元约束
        for (Plan plan : planList) {
            //获取任务和任务所选弧段
            Task task = SDSTaskMap.get(Utils.generateTaskSDS(plan.getSnIndex(), plan.getDayIndex(),
                    plan.getSubIndex()));
            Arc arc = SDAOArcMap.get(Utils.generateArcSDAO(plan.getSatelliteIndex(), plan.getDayIndex(),
                    plan.getAntennaIndex(), plan.getOrbitIndex()));
            //检查任务和选用弧段是否有效（在数据中出现过）
            if (arc == null) {
                logger.info("检查到无效弧段，该弧段不存在：");
                logger.info("规划方案调试信息：" + plan);
                oneEleConstraintFlag = false;
            }
            if (task == null) {
                logger.info("检查到无效任务，该任务不存在：");
                logger.info("规划方案调试信息：" + plan);
                oneEleConstraintFlag = false;
            }
            //如果出现无效任务或弧段, 方案损坏，直接退出
            if (!oneEleConstraintFlag) {
                logger.info(Utils.logFormatString("检查到非法规划数据, 请确认场景数据和输入方案匹配, 约束检查结束！"));
                return oneEleConstraintFlag;
            }
            task.setArc(arc);
            //检查弧段是否满足任务约束，弧段是否在天线可用时间内，弧段和任务类型是否匹配
            if (!Objects.requireNonNull(task).getOptionalArcList().contains(arc)) {
                String taskSDS = Utils.generateTaskSDS(task);
                String arcSDAO = Utils.generateArcSDAO(arc);
                oneEleConstraintFlag = false;
                logger.info(" ");
                logger.info("任务: " + taskSDS + " 所选弧段: " + arcSDAO + " 不符合约束!");
                logger.info("违反约束原因：");
                // 检查任务类型和天线类型是否匹配
                if (!task.getAvailableAntennaMap().containsKey(arc.getAntennaIndex())) {
                    logger.info("任务类型: " + task.getTaskType() +
                            "与弧段天线支持任务类型: " + antennaMap.get(arc.getAntennaIndex()).getFunction() + "不匹配!");
                }
                // 检查这个弧段是否满足任务约束之一
                if (!task.isArcAvailable(arc)) {
                    logger.info("弧段: " + arcSDAO + "不满足任务 " + taskSDS + "的任一时间窗约束!");
                    logger.info("弧段数据：" + arc);
                    logger.info("任务时间窗约束列表：" + task.getArcConstraintList());
                }
                // 检查这个弧段加上任务的准备和释放时间后和天线禁用时间无冲突
                if (!antennaMap.get(arc.getAntennaIndex()).isArcAvailable(task, arc)) {
                    logger.info("任务: " + taskSDS + " 选择弧段: " + arcSDAO + "后，占用天线时间与天线" + arc.getAntennaIndex() +
                            "的禁用时段有重叠！");
                    logger.info("弧段数据：" + arc);
                    logger.info("任务准备时间：" + task.getPrepareTime() + " 任务释放时间：" + task.getReleaseTime());
                    logger.info("天线禁用时段：" + antennaMap.get(arc.getAntennaIndex()).getForbiddenWindowList());
                }
                logger.info("违反约束规划方案：" + plan);
            }
        }
        if (!oneEleConstraintFlag) {
            logger.info(Utils.logFormatString("检查到方案违反一元约束, 规划方案不合法, 约束检查结束!"));
            return oneEleConstraintFlag;
        } else {
            logger.info(Utils.logFormatString("一元约束检查完成，无约束违反情况！"));
            return oneEleConstraintFlag;
        }
    }

    public static void checkTwoEleConstraint(List<Plan> planList,
                                             Map<Long, Antenna> antennaMap,
                                             Map<String, Task> SDSTaskMap,
                                             String[] parts,
                                             Logger logger) {
        logger.info(Utils.logFormatString("正在检查任务方案冲突情况"));
        int towEleConstraintVoietNum = 0;
        for (int i = 0; i < planList.size(); i++) {
            Plan plan = planList.get(i);
            Task task = SDSTaskMap.get(
                    Utils.generateTaskSDS(plan.getSnIndex(), plan.getDayIndex(), plan.getSubIndex()));
            for (int j = i; j < planList.size(); j++) {
                Plan plan2 = planList.get(j);
                Task task2 = SDSTaskMap.get(
                        Utils.generateTaskSDS(plan2.getSnIndex(), plan2.getDayIndex(), plan2.getSubIndex()));
                //检查两个任务是否冲突
                int constraintCode = new TasksConstraint<>(antennaMap).intCheck(new Pair<>(task, task2));
                if (constraintCode != 0) {
                    towEleConstraintVoietNum += 1;
                    // logger.info(" ");
                    logger.info("任务1: " + Utils.generateTaskSDS(task) + " 与任务2: " + Utils.generateTaskSDS(task2) +
                            " 存在冲突！");
                    if (constraintCode == 1) {
                        logger.info("冲突原因：任务1: " + Utils.generateTaskSDS(task) +
                                " 与任务2: " + Utils.generateTaskSDS(task2) + " 占用同一设备，且占用时间存在重叠！");
                    }
                    if (constraintCode == 2) {
                        logger.info("原因：任务1: " + Utils.generateTaskSDS(task) +
                                " 与任务2: " + Utils.generateTaskSDS(task2) + " 是同一颗卫星的任务，且任务类型相同，不可在同一圈执行！");
                        logger.info("任务1类型：" + task.getTaskType());
                        logger.info("任务2类型：" + task2.getTaskType());
                    }
                    logger.info("规划方案调试信息：");
                    logger.info("任务1规划方案：" + plan);
                    logger.info("任务2规划方案：" + plan2);
                }
            }
        }
        if (towEleConstraintVoietNum != 0)
            logger.info(Utils.logFormatString("约束检查结束！共检查到" + towEleConstraintVoietNum + "个任务冲突！"));
        else {
            logger.info(Utils.logFormatString("约束检查结束，方案" + parts[parts.length - 1] + "无违反约束情况！"));
        }
    }

    public static int checkTwoEleConstraintParallel(List<Plan> planList,
                                                    Map<Long, Antenna> antennaMap,
                                                    Map<String, Task> SDSTaskMap,
                                                    String[] parts,
                                                    Logger logger) {
        logger.info(Utils.logFormatString("正在检查任务方案冲突情况"));
        Object lock = new Object();
        int towEleConstraintVoietNum = (int) IntStream.range(0, planList.size()).parallel().mapToObj(i -> {
            Plan plan = planList.get(i);
            Task task = SDSTaskMap.get(Utils.generateTaskSDS(plan.getSnIndex(), plan.getDayIndex(),
                    plan.getSubIndex()));

            return IntStream.range(i, planList.size()).parallel().mapToObj(j -> {
                Plan plan2 = planList.get(j);
                Task task2 = SDSTaskMap.get(Utils.generateTaskSDS(plan2.getSnIndex(), plan2.getDayIndex(),
                        plan2.getSubIndex()));

                // 检查两个任务是否冲突
                int constraintCode = new TasksConstraint<>(antennaMap).intCheck(new Pair<>(task, task2));
                // 输出代码块同步，避免调试信息交叉
                if (constraintCode != 0) {
                    synchronized (lock) {
                        logger.info(" ");
                        logger.info("任务1: " + Utils.generateTaskSDS(task) + " 与任务2: " + Utils.generateTaskSDS(task2) + " " +
                                "存在冲突！");
                        if (constraintCode == 1) {
                            logger.info("冲突原因：任务1: " + Utils.generateTaskSDS(task) +
                                    " 与任务2: " + Utils.generateTaskSDS(task2) + " 占用同一设备，且占用时间存在重叠！");
                        } else if (constraintCode == 2) {
                            logger.info("原因：任务1: " + Utils.generateTaskSDS(task) +
                                    " 与任务2: " + Utils.generateTaskSDS(task2) + " 是同一颗卫星的任务，且任务类型相同，不可在同一圈执行！");
                            logger.info("任务1类型：" + task.getTaskType());
                            logger.info("任务2类型：" + task2.getTaskType());
                        }
                        logger.info("规划方案调试信息：");
                        logger.info("任务1规划方案：" + plan);
                        logger.info("任务2规划方案：" + plan2);
                        return 1;
                    }
                } else
                    return 0;
            }).reduce(0, Integer::sum);
        }).reduce(0, Integer::sum);

        if (towEleConstraintVoietNum != 0) {
            logger.info(Utils.logFormatString("约束检查结束！共检查到" + towEleConstraintVoietNum + "个任务冲突！"));
        } else {
            logger.info(Utils.logFormatString("约束检查结束，方案" + parts[parts.length - 1] + "无违反约束情况！"));
        }
        return towEleConstraintVoietNum;
    }

    public static void calResultScore(List<Plan> planList,
                                      List<Task> taskList,
                                      Map<Long, Antenna> antennaMap,
                                      Map<String, Task> SDSTaskMap,
                                      Logger logger) {
        //将计划列表里的测控任务和数传任务分别存到两个列表里
        List<Plan> TTCPlanList = new ArrayList<>();
        List<Plan> DDTPlanList = new ArrayList<>();
        for (Plan plan : planList) {
            Task task = SDSTaskMap.get(Utils.generateTaskSDS(plan));
            if (task.getTaskType().equals("TTC")) {
                TTCPlanList.add(plan);
            } else {
                DDTPlanList.add(plan);
            }
        }
        //将任务列表里的测控任务和数传任务分别存到两个列表里
        List<Task> TTCTaskList = new ArrayList<>();
        List<Task> DDTTaskList = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getTaskType().equals("TTC")) {
                TTCTaskList.add(task);
            } else {
                DDTTaskList.add(task);
            }
        }
        //计算TTC和DDT的完成百分比
        double ttcPercent = TTCPlanList.size() * 1.0 / TTCTaskList.size();
        double ddtPercent = DDTPlanList.size() * 1.0 / DDTTaskList.size();

        //根据规则计算最终得分
        int ttcScore = CalScore.calTTCScore(ttcPercent);
        int ddtScore = CalScore.calDDTScore(ddtPercent);
        logger.info("");
        logger.info(String.format("测控任务完成率：%.6f 得分：%d", ttcPercent * 100, ttcScore));
        logger.info(String.format("数传任务完成率：%.6f 得分：%d", ddtPercent * 100, ddtScore));
        int sumScore = ttcScore == 0 || ddtScore == 0 ? 0 : ddtScore + ttcScore;
        logger.info(String.format("方案总得分：%d", sumScore));

    }
}
