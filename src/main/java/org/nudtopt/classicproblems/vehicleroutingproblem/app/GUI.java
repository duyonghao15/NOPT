package org.nudtopt.classicproblems.vehicleroutingproblem.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.algorithm.operator.*;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.vehicleroutingproblem.constraint.Decoder;
import org.nudtopt.classicproblems.vehicleroutingproblem.constraint.Decoder3;
import org.nudtopt.classicproblems.vehicleroutingproblem.gui.VRPPanel;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.tool.CustomerComparator;
import org.nudtopt.classicproblems.vehicleroutingproblem.tool.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUI {


    public static void main(String[] args) throws Exception {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(20));
        Tool.checkLicence();
        String problemName = "c101";
        Routes routes = new Reader().read(problemName);
        // new ReadSolution().readBestSolution(routes);                                    // 读取最优解

        // 2. 创建函数(收益)
        // Decoder decoder = new Decoder();
        // Decoder decoder = new Decoder2();
        Decoder decoder = new Decoder3();
        decoder.createDistanceMap(routes);                                              // 创建客户距离矩阵
        routes.setConstraint(decoder);                                                  // 赋值解码器
        routes.setComparator(new CustomerComparator());                                 // 赋值排序器
        routes.updateScore();                                              // 更新计算当前收益值

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setLateSize(50);
        algorithm.setTabuSize(5);
        algorithm.setIteration(5000000);
        algorithm.setOperatorList(createOperators());                                   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(routes);

        // 4. 启动GUI
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(routes);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new VRPPanel());                                         // 新增画布
        gui.openGUI();
    }


    /**
     * 创新一个或多个算子, 可控算法调用
     */
    public static List<Operator> createOperators() {
        List<Operator> operatorList = new ArrayList<>();
        operatorList.add(new RandomMove());                                             // 随机移动算子
        operatorList.add(new RandomSwap());                                             // 随机交换算子
        operatorList.add(new RandomMultiMove());                                        // 移动(多个)算子
        operatorList.add(new RandomMultiSwap());                                        // 随机交换(全部变量)算子
        operatorList.add(new RandomBlockMove());                                        // 随机整块move算子
        operatorList.add(new RandomBlockSwap());                                        // 随机整块swap算子
        operatorList.add(new CrossoverOnePoint());                                      // 单点交叉算子
        operatorList.add(new CrossoverTwoPoint());                                      // 多点交叉算子
        return operatorList;
    }


}
