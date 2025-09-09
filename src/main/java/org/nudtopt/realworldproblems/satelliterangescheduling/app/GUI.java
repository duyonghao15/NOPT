package org.nudtopt.realworldproblems.satelliterangescheduling.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.satelliterangescheduling.constraint.Check;
import org.nudtopt.realworldproblems.satelliterangescheduling.constraint.CheckIncremental;
import org.nudtopt.realworldproblems.satelliterangescheduling.gui.SatelliteRangingGUI;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerAFIT.Reader;

import java.util.Arrays;
import java.util.Random;

public class GUI {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        Scenario scenario = new Reader().readData(3);

        // 2. 创建解码器(收益函数)
        // Check checker = new Check();                                 // 非增量式检查
        Check checker = new CheckIncremental();                         // 增量式检查 (速度更快)
        scenario.setConstraint(checker);
        scenario.updateScore();                            // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(10);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomMultiMove(), new RandomSwap(), new RandomMultiSwap())); // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(scenario);

        // 4. 启动gui
        // SolutionGUI gui = new SolutionGUI();
        SatelliteRangingGUI gui = new SatelliteRangingGUI();
        gui.setSolution(scenario);
        gui.setAlgorithm(algorithm);
        gui.openGUI();
    }

}
