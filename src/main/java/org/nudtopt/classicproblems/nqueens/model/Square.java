package org.nudtopt.classicproblems.nqueens.model;

import org.nudtopt.api.model.NumberedObject;

public class Square extends NumberedObject {

    private ChessBoard chessBoard;          // 棋盘

    private int x;                          // 该棋格所在的行索引 (从0开始)

    private int y;                          // 该棋格所在的列索引 (从0开始)

    // getter & setter
    public ChessBoard getChessBoard() {
        return chessBoard;
    }
    public void setChessBoard(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

}
