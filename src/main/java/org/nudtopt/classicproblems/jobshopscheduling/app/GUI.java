package org.nudtopt.classicproblems.jobshopscheduling.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.jobshopscheduling.constraint.Decoder;
import org.nudtopt.classicproblems.jobshopscheduling.gui.JobShopPanel;
import org.nudtopt.classicproblems.jobshopscheduling.model.JobShop;
import org.nudtopt.classicproblems.jobshopscheduling.tool.Reader;

import java.util.Arrays;
import java.util.Random;

public class GUI {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        JobShop jobShop = new Reader().read("MFJS10");          // SFJS1-10, MFJS1-10

        // 2. 创建解码器(收益函数)
        Decoder decoder = new Decoder();
        jobShop.setConstraint(decoder);
        jobShop.updateScore();                          // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(Math.min(5, jobShop.getOperationList().size() / 2));
        algorithm.setIteration(1000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomMultiMove(), new RandomSwap(), new RandomMultiSwap())); // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(jobShop);

        // 4. 启动gui
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(jobShop);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new JobShopPanel());                  // 新增画布
        gui.openGUI();
    }

}
