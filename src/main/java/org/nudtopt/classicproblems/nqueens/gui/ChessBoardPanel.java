package org.nudtopt.classicproblems.nqueens.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.classicproblems.nqueens.model.ChessBoard;
import org.nudtopt.classicproblems.nqueens.model.Queen;
import org.nudtopt.classicproblems.nqueens.model.Square;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessBoardPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());  // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());  // 画黑边框
        ChessBoard chessBoard = (ChessBoard) solution;
        List<Queen> queenList = chessBoard.getQueenList();
        List<Square> squareList = chessBoard.getSquareList();

        // 0. 确定棋格长宽, 和向中心移动的偏移量
        int width  = Math.min(getWidth(), getHeight()) / queenList.size();
        int height = width;
        int delta_X = (getWidth()  - width  * queenList.size()) / 2;
        int delta_Y = (getHeight() - height * queenList.size()) / 2;


        // 1. 画棋格
        for(Square square : squareList) {
            int x = square.getX();
            int y = square.getY();
            g.drawRect(delta_X + x * width, getHeight() - delta_Y - (y + 1) * height, width, height);
        }

        // 2. 画皇后
        for(Queen queen : queenList) {
            Square square = queen.getSquare();
            if(square == null)  continue;
            int x = square.getX();
            int y = square.getY();
            int d = Math.min(width, height) / 2;
            d = Math.max(d, 1);
            // 着色
            g.setColor(new Color(0, 0, 0));                 // a. 黑色
            for(Queen otherQueen : queenList) {
                boolean attack = Queen.attack(queen, otherQueen);
                if(attack) {
                    g.setColor(new Color(255, 0, 0));       // b. 红色
                    break;
                }
            }
            // 画圆
            g.fillOval(delta_X + x * width + width / 2 - d/2, getHeight() - delta_Y - (y + 1) * height + height / 2 - d/2, d, d);
        }
        /* function ends */
    }

    @Override
    public String getName() {
        return "棋盘图";
    }


/* class ends */
}
