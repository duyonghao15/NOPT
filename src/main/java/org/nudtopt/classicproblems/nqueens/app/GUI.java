package org.nudtopt.classicproblems.nqueens.app;

import org.nudtopt.api.algorithm.localsearch.HillClimbing;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.algorithm.operator.RandomSwap;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.classicproblems.nqueens.gui.ChessBoardPanel;
import org.nudtopt.classicproblems.nqueens.model.ChessBoard;
import org.nudtopt.classicproblems.nqueens.model.IncrementalCheck;
import org.nudtopt.classicproblems.nqueens.tool.Generator;

import java.util.Arrays;
import java.util.Random;

public class GUI {


    public static void main(String[] args) {
        // 1. 读取数据, 创建场景
        Tool.setRandom(new Random(30));
        Tool.checkLicence();
        int n = 8;
        ChessBoard chessBoard = new Generator().createChessBoard(n);

        // 2. 创建函数(收益)
        // Check check = new Check();                                                   // 非增量式约束检查函数
        IncrementalCheck check = new IncrementalCheck();                                // 增量式检查函数
        chessBoard.setConstraint(check);
        chessBoard.updateScore();                                          // 更新计算初始收益值

        // 3. 创建算法
        HillClimbing algorithm = new HillClimbing();
        algorithm.setIteration(500000);
        algorithm.setOperatorList(Arrays.asList(new RandomMove(), new RandomSwap()));   // 设置算子: 移动算子 + 交换算子
        algorithm.setSolution(chessBoard);

        // 4. 启动GUI
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(chessBoard);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().add(new ChessBoardPanel());                                  // 新增画布
        gui.openGUI();
    }


}
