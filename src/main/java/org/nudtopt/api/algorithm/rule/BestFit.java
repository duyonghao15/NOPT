package org.nudtopt.api.algorithm.rule;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.MoveComparator;

import java.util.ArrayList;
import java.util.List;

public class BestFit extends FirstFit {

    private String name = "优选安排(BIFS)";
    private String type = "启发式规则";

    @Override
    public List<Operator> run(Solution solution, List<DecisionEntity> orderedEntityList) {
        this.solution = solution;
        int trappedIteration = 0;
        int i = -1;

        // 1. 遍历决策实体
        for(DecisionEntity entity : orderedEntityList) {
            List<String> nameList = entity.getDecisionVariableList();
            // 2. 遍历决策变量
            for(String name : nameList) {
                List<Object> variableList = entity.getOptionalDecisionVariableList(name);
                // 3. 遍历决策变量值域
                List<Move> acceptableMoveList = new ArrayList<>();   // 可接受的MoveList
                for(Object variable : variableList) {
                    i ++;
                    trappedIteration ++;
                    // a. move
                    Score oldScore = solution.getScore().clone();
                    Move move = Move.move(entity, name, variable);
                    if(move == null)    continue;
                    else                solution.updateScore(move);
                    boolean accept = solution.getScore().compareTo(oldScore) >= 0;
                    if(accept)          acceptableMoveList.add(move);
                    // b. 撤回
                    move.undo();
                    solution.updateScore(move);
                    if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                    solution.getScore().clone(oldScore);    // solution赋旧score(注意, 只是值, score对象不能变)
                }
                // c. move
                acceptableMoveList.sort(new MoveComparator());
                if(acceptableMoveList.size() > 0) {
                    Move move = acceptableMoveList.get(0);
                    Move.move(entity, name, move.getNewValue());
                    solution.updateScore(move);
                    historyOperatorList.add(move);
                    logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby (1/" + trappedIteration + ") moves\t" + move);
                    trappedIteration = 0;
                }
            }
            if(update().equals("stop"))     break;
        }
        return historyOperatorList;
    }


    // getter & setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

/* class ends */
}
