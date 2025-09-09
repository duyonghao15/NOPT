package org.nudtopt.classicproblems.knapsack.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.knapsack.model.Check;
import org.nudtopt.classicproblems.knapsack.model.Solution;
import org.nudtopt.classicproblems.knapsack.tool.Reader;

import java.util.*;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        Solution solution = new Reader().read("mknap2.txt", 1);

        // 2. 创建函数(收益)
        Check check = new Check();
        solution.setConstraint(check);
        solution.updateScore();     // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(5);
        algorithm.setIteration(1000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomMultiMove(), new RandomSwap(), new RandomMultiSwap())); // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(solution);

        // 4. 运行算法
        algorithm.run();
    }

}
