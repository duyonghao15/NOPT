package org.nudtopt.classicproblems.travelingsalesmanproblem.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.travelingsalesmanproblem.constraint.CalDistance;
import org.nudtopt.classicproblems.travelingsalesmanproblem.constraint.CalDistanceIncremental;
import org.nudtopt.classicproblems.travelingsalesmanproblem.gui.TSPPanel;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Tour;
import org.nudtopt.classicproblems.travelingsalesmanproblem.tool.Reader;

import java.util.Arrays;
import java.util.Random;

public class GUI {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        String problemName = "xqf131";
//        String problemName = "bck2217";
//        String problemName = "lrb744710";
        Tour tour = new Reader().read(problemName + ".tsp");

        // 2. 创建函数(收益)
        // CalDistance calculator = new CalDistance();
        CalDistance calculator = new CalDistanceIncremental();
        calculator.createDistanceMap(tour.getCityList());                               // 创建城市距离矩阵
        tour.setConstraint(calculator);
        tour.updateScore();                                                // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(5);
        algorithm.setIteration(500000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomSwap(), new RandomMultiSwap()));   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(tour);

        // 4. 启动GUI
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(tour);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new TSPPanel());                                         // 新增画布
        gui.openGUI();
        /* class ends */
    }



/* class ends */
}
