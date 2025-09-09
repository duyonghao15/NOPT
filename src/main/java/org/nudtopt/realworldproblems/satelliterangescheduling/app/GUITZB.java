package org.nudtopt.realworldproblems.satelliterangescheduling.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB.DecodeByHour;
import org.nudtopt.realworldproblems.satelliterangescheduling.gui.SatelliteRangingGUI;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadAntennaData;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadRangeData;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.ReadTaskData;

import java.util.Map;
import java.util.Random;

public class GUITZB {


    public static void main(String[] args) throws Exception {

        // 1. 读取数据
        Tool.checkLicence();                                                    // 校验日志
        Tool.setRandom(new Random(30));                                   // 固定随机种子
        String path = Tool.getDesktopPath() + "/xml/科目2竞赛数据/01";          // 根目录路径
        Map<Long, Antenna> antennaMap = new ReadAntennaData().read(path);       // 1. 读取天线数据
        Scenario scenario = new ReadRangeData().read(path, antennaMap);         // 2. 读取弧段数据并创建场景
        new ReadTaskData().read(path, scenario);                                // 3. 读取任务数据, 预处理约束, 并匹配任务资源

        // 2. 创建约束
        // Decoder constraint = new Decoder();                                     // 增量式约束检查: 以站为单位
        DecodeByHour constraint = new DecodeByHour();                           // 增量式约束检查: 以站为单位, 每小时判断
        constraint.setIncremental(true);
        scenario.setConstraint(constraint);
        scenario.updateScore();

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();                        // 逾期接受元启发式
        algorithm.setSolution(scenario);
        algorithm.setIteration(50000000);                                       // 5000000
        algorithm.setHistorySize((int)algorithm.getIteration());
        algorithm.setTabuSize(4);
        algorithm.setLateSize(10);

        // 4. 启动gui
        // SolutionGUI gui = new SolutionGUI();
        SatelliteRangingGUI gui = new SatelliteRangingGUI();
        gui.setSolution(scenario);
        gui.setAlgorithm(algorithm);
        gui.openGUI();
    }


/* class ends */
}
