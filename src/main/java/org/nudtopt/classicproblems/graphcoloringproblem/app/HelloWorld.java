package org.nudtopt.classicproblems.graphcoloringproblem.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.graphcoloringproblem.constraint.CalScore;
import org.nudtopt.classicproblems.graphcoloringproblem.constraint.CalScoreIncremental;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Graph;
import org.nudtopt.classicproblems.graphcoloringproblem.tool.Reader;

import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        // String problemName = "gcol1";
        String problemName = "gcol30";
        Graph graph = new Reader().read(problemName + ".txt");

        // 2. 创建函数(收益)
        // CalScore cal = new CalScore();                                                // 非增量式计算
        CalScore cal = new CalScoreIncremental();                                        // 增量式快速计算
        graph.setConstraint(cal);
        graph.updateScore();                                                // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomSwap()));   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(graph);

        // 4. 运行算法
        algorithm.run();
        /* class ends */
    }
}
