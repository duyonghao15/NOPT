package org.nudtopt.classicproblems.binpacking.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.binpacking.gui.BinPanel;
import org.nudtopt.classicproblems.binpacking.model.Bin;
import org.nudtopt.classicproblems.binpacking.tool.Reader;
import org.nudtopt.classicproblems.binpacking.tool.SkyLineHeuristic;

import java.util.Arrays;
import java.util.Random;

public class GUI {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        // String problemName = "cgcut1";
        String problemName = "gcut4";
        Bin bin = new Reader().read(problemName);

        // 2. 创建函数(收益)
        SkyLineHeuristic decoder = new SkyLineHeuristic();
        bin.setConstraint(decoder);
        bin.updateScore();                                                 // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomSwap()));   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(bin);

        // 4. 启动GUI
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(bin);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new BinPanel());                                         // 新增画布
        gui.openGUI();
    }

}
