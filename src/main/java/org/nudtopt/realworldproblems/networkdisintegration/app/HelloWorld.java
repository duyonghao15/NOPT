package org.nudtopt.realworldproblems.networkdisintegration.app;

import org.nudtopt.api.algorithm.localsearch.HillClimbing;
import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.networkdisintegration.constraint.CalScore;
import org.nudtopt.realworldproblems.networkdisintegration.model.Network;
import org.nudtopt.realworldproblems.networkdisintegration.tool.CalConnectivity;
import org.nudtopt.realworldproblems.networkdisintegration.tool.Reader;

import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        String file = "inf-openflights.edges";
        Network network = new Reader().read(file);

        // 2. 创建解码器(收益函数)
        CalScore cal = new CalScore();                              // 计算网络最大连通度的函数
        network.setConstraint(cal);
        network.updateScore();                        // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(10);
        algorithm.setTabuSize(5);
        algorithm.setIteration(1000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomMultiMove(), new RandomSwap(), new RandomMultiSwap())); // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(network);

        // 4. 运行算法
        algorithm.run();
    }

}
