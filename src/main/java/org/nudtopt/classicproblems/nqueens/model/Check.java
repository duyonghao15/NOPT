package org.nudtopt.classicproblems.nqueens.model;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;

import java.util.List;

public class Check extends Constraint {


    @Override
    public Score calScore() {
        ChessBoard chessBoard = (ChessBoard) solution;
        List<Queen> queenList = chessBoard.getQueenList();
        // 1. 计算冲突数
        int conflict = 0;
        for(int i = 0 ; i < queenList.size() ; i ++) {
            for(int j = i + 1 ; j < queenList.size() ; j ++) {
                Queen queen_1 = queenList.get(i);
                Queen queen_2 = queenList.get(j);
                boolean attack = Queen.attack(queen_1, queen_2);
                if(attack) {
                    conflict ++;
                }
            }
        }
        // 2. 创建收益值
        Score score = new Score();
        score.setHardScore(-conflict);  // 硬约束 = -冲突数
        return score;
    }


}
