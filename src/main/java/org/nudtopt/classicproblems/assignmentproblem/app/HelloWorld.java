package org.nudtopt.classicproblems.assignmentproblem.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.assignmentproblem.constraint.Check;
import org.nudtopt.classicproblems.assignmentproblem.constraint.CheckIncremental;
import org.nudtopt.classicproblems.assignmentproblem.model.Assignment;
import org.nudtopt.classicproblems.assignmentproblem.tool.Reader;

import java.util.Arrays;
import java.util.Random;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        String problemName = "assign800";
        Assignment assignment = new Reader().read(problemName);

        // 2. 创建函数(收益)
        // Check checker = new Check();                                                      // 非增量式约束/收益计算
        CheckIncremental checker = new CheckIncremental();                                   // 增 量 式约束/收益计算
        assignment.setConstraint(checker);
        assignment.updateScore();                                               // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(100);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(Arrays.asList(new RandomSwap(), new RandomMultiSwap()));   // 设置算子: 交换算子
        algorithm.setSolution(assignment);

        // 4. 运行算法
        algorithm.run();
    }

}
