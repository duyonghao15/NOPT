package org.nudtopt.realworldproblems.satelliterangescheduling.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB.DecodeByHour;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.Exporter;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadAntennaData;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadRangeData;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadTaskData;

import java.util.Map;
import java.util.Random;

public class HelloWorldTZB {


    public static void main(String[] args) throws Exception {
        new HelloWorldTZB().run(Tool.getDesktopPath() + "/xml/科目2竞赛数据/10", 30L);

        /*new HelloWorldTZB().run(Tool.getDesktopPath() + "/xml/科目2竞赛数据/01", 30L);
        new HelloWorldTZB().run(Tool.getDesktopPath() + "/xml/科目2竞赛数据/02", 30L);
        new HelloWorldTZB().run(Tool.getDesktopPath() + "/xml/科目2竞赛数据/03", 30L);
        new HelloWorldTZB().run(Tool.getDesktopPath() + "/xml/科目2竞赛数据/04", 30L);*/
        // 场景1满分：种子13, 迭代130000000
        // 场景2满分：种子30, 迭代60000000
        // 场景4满分：种子30, 迭代60000000
    }


    public void run(String path, Long seed) throws Exception {
        // 1. 读取数据
        Tool.checkLicence();                                                    // 校验日志
        Tool.setRandom(new Random(seed));                                       // 固定随机种子
        Map<Long, Antenna> antennaMap = new ReadAntennaData().read(path);       // 1. 读取天线数据
        Scenario scenario = new ReadRangeData().read(path, antennaMap);         // 2. 读取弧段数据并创建场景
        new ReadTaskData().read(path, scenario);                                // 3. 读取任务数据, 预处理约束, 并匹配任务资源

        // 2. 创建约束
        DecodeByHour constraint = new DecodeByHour();                           // 增量式约束检查: 以站为单位, 每小时判断
        constraint.setIncremental(true);
        scenario.setConstraint(constraint);
        scenario.updateScore();

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();                        // 逾期接受元启发式
        algorithm.setSolution(scenario);
        algorithm.setIteration(1000000000);                                       // 50000000, 5000000
        algorithm.setHistorySize((int)algorithm.getIteration());
        algorithm.setTabuSize(4);
        algorithm.setLateSize(10);

        for(Task task: scenario.getTaskList()) {                                // 先只做数传任务
            if(task.getType().contains("TTC")) {
                task.setChangeable(false);
            }
        }
        algorithm.run();
        algorithm.setNoImproveIteration(0);
        algorithm.setState("start");
        algorithm.setIteration(60000000);                                       // 50000000, 5000000
        for(Task task: scenario.getTaskList()) {                                // 再做测控任务
            if(task.getType().contains("TTC")) {
                task.setChangeable(true);
            }
            if(task.getType().contains("DDT")) {                                // 数传任务不动
                task.setChangeable(false);
            }
        }

        // 4. 运算
        algorithm.run();

        // 5. 输出结果
        String fileName = algorithm.getName() + "+" + algorithm.getIteration() + "+seed" + seed;  // 文件名: 场景名->算法名->迭代次数->任务成功数
        new Exporter().write(scenario, path, fileName);
    }



/* class ends */
}
