package org.nudtopt.classicproblems.functionoptimization.f10;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.functionoptimization.FunctionSolution;
import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.Random;

public class HelloWorld {


    public static void main(String[] args) {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        FunctionSolution solution = new FunctionSolution();
        solution.setXList(new X().createXList(50, -50, 50, 0));
        solution.setId(0L);

        // 2. 创建函数(收益)
        Function function = new Function();
        solution.setConstraint(function);
        solution.updateScore();                         // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(10);
        algorithm.setTabuSize(5);
        algorithm.setIteration(10000000);
        algorithm.setSolution(solution);

        // 4. 运行算法
        algorithm.run();
    }


/* class ends */
}
