package org.nudtopt.classicproblems.orienteeringproblem.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomMultiSwap;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.orienteeringproblem.constraint.Decoder;
import org.nudtopt.classicproblems.orienteeringproblem.gui.OPPanel;
import org.nudtopt.classicproblems.orienteeringproblem.model.Solution;
import org.nudtopt.classicproblems.orienteeringproblem.tool.Reader;
import org.nudtopt.classicproblems.orienteeringproblem.tool.VertexComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUI {


    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(20));
        Tool.checkLicence();
        String problemName = "c101";
        int tourNum = 4;                                                                // 路径数 1-4
        Solution solution = new Reader().read(problemName + ".txt", tourNum);     // 创建场景

        // 2. 创建函数(收益)
        Decoder decoder = new Decoder();
        decoder.createDistanceMap(solution);                                            // 创建顶点距离矩阵
        solution.setConstraint(decoder);                                                // 赋值解码器
        solution.setComparator(new VertexComparator());                                 // 新建并赋值排序器
        solution.updateScore();                                            // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(50);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(createOperators());                                   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(solution);

        // 4. 启动GUI
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(solution);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new OPPanel());                                          // 新增画布
        gui.openGUI();
    }


    /**
     * 创新一个或多个算子, 可控算法调用
     */
    public static List<Operator> createOperators() {
        List<Operator> operatorList = new ArrayList<>();
        operatorList.add(new RandomMove());                                             // 随机移动算子
        operatorList.add(new RandomSwap());                                             // 随机交换算子
        operatorList.add(new RandomMultiSwap());                                        // 随机交换(全部变量)算子
//        operatorList.add(new RandomBlockMove());                                        // 随机整块move算子
//        operatorList.add(new RandomBlockSwap());                                        // 随机整块swap算子
//        operatorList.add(new CrossoverOnePoint());                                      // 单点交叉算子
//        operatorList.add(new CrossoverTwoPoint());                                      // 多点交叉算子
        return operatorList;
    }


}
