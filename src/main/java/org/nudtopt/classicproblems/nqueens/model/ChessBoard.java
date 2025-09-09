package org.nudtopt.classicproblems.nqueens.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class ChessBoard extends Solution {

    @DecisionEntityList
    private List<Queen> queenList = new ArrayList<>();      // 皇后列表 (决策实体列表)

    private List<Square> squareList = new ArrayList<>();    // 棋格列表

    // getter & setter
    public List<Queen> getQueenList() {
        return queenList;
    }
    public void setQueenList(List<Queen> queenList) {
        this.queenList = queenList;
    }

    public List<Square> getSquareList() {
        return squareList;
    }
    public void setSquareList(List<Square> squareList) {
        this.squareList = squareList;
    }

}
