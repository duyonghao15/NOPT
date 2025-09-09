package org.nudtopt.classicproblems.nqueens.model;

import org.nudtopt.api.constraint.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncrementalCheck extends Check {


    private Map<String, Integer> queenAttackMap = new HashMap();                // 皇后两两冲突map

    private long t = 0;

    @Override
    public Score calScore() {
        ChessBoard chessBoard = (ChessBoard) solution;
        List<Queen> queenList = chessBoard.getQueenList();
        // 1. 初始化皇后两两冲突map
        if(operator == null && queenAttackMap.size() == 0) {
            int initialScore = 0;
            for(int i = 0 ; i < queenList.size() ; i ++) {
                for(int j = i + 1 ; j < queenList.size() ; j ++) {
                    Queen queen_1 = queenList.get(i);
                    Queen queen_2 = queenList.get(j);
                    int attack = Queen.attack(queen_1, queen_2) ? -1 : 0;
                    String index = queen_1.getId() + " <-> " + queen_2.getId();                 // map索引: 前queen <-> 后queen
                    queenAttackMap.put(index, attack);
                    initialScore += attack;
                }
            }
            Score score = new Score();                                          // 新建score
            score.setHardScore(initialScore);                                   // 赋值初始冲突
            solution.setScore(score);
            return score;
        }

        // 2. 增量式计算/更新冲突map
        Score score = solution.getScore();                                      // 当前评分
        List changedEntityList = operator.getDecisionEntityList();              // 获得本次算子所影响的实体(皇后)清单
        for(Object changedEntity : changedEntityList) {                         // 遍历受影响的皇后, 与其他所有皇后两两重新计算冲突
            Queen changedQueen = (Queen) changedEntity;
            for(Queen otherQueen : queenList) {
                if(changedQueen == otherQueen)  continue;

                long t1 = System.currentTimeMillis();
                String index = changedQueen.getId() <= otherQueen.getId() ? changedQueen.getId() + " <-> " + otherQueen.getId() : otherQueen.getId() + " <-> " + changedQueen.getId();
                t += System.currentTimeMillis() - t1;

                int oldAttack = queenAttackMap.get(index);                      // 原冲突值
                int newAttack = Queen.attack(changedQueen, otherQueen) ? -1 : 0;// 新冲突值
                if(oldAttack == newAttack)      continue;                       // 新旧相同, do nothing
                queenAttackMap.put(index, newAttack);                           // 更新冲突值
                score.setHardScore(score.getHardScore() - oldAttack + newAttack);// score - old + new 更新
            }
        }

        /* function ends */
        return score;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }

}
