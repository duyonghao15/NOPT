package org.nudtopt.classicproblems.nqueens.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.nqueens.model.ChessBoard;
import org.nudtopt.classicproblems.nqueens.model.Queen;
import org.nudtopt.classicproblems.nqueens.model.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Generator extends MainLogger {


    /**
     * 创建一个n皇后问题
     * 即n*n的棋盘、棋格, 以及n个皇后
     * @author  杜永浩
     * @param n 棋盘宽度/皇后数
     * @return  棋盘、棋格、n个皇后
     */
    public ChessBoard createChessBoard(int n) {
        logger.info("正创建 -> n皇后问题数据 (棋盘宽度/皇后数: " + n + ") ... ..." );
        // 1. 创建棋盘
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.setId(0L);
        // 2. 创建棋格
        List<Square> squareList = new ArrayList<>();
        for(int x = 0 ; x < n ; x ++) {
            for(int y = 0 ; y < n ; y ++) {
                Square square = new Square();
                square.setX(x);
                square.setY(y);
                square.setChessBoard(chessBoard);
                square.setId((long) squareList.size());
                squareList.add(square);
            }
        }
        chessBoard.setSquareList(squareList);
        // 3. 创建皇后
        List<Queen> queenList = new ArrayList<>();
        for(int i = 0 ; i < n ; i ++) {
            Queen queen = new Queen();
            queen.setId((long) queenList.size());
            queenList.add(queen);
            // 设第i个皇后只能放在第i列中(x=i, y可变)
            int x = i;
            List<Square> optionalSquareList = squareList.stream().filter(square -> square.getX() == x).collect(Collectors.toList());
            queen.setOptionalSquareList(optionalSquareList);                // 决策变量(棋格)的取值范围
            queen.setSquare(Tool.randomFromList(optionalSquareList));       // 决策变量初始值赋值： 随机放置于一个棋格内
        }
        chessBoard.setQueenList(queenList);
        logger.info("已创建 -> n皇后问题数据 (棋盘: 1, 棋格: " + squareList.size() + ", 皇后: " + queenList.size() + ") ... ...\n" );
        return chessBoard;
    }


}
