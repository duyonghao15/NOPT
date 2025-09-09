package org.nudtopt.api.algorithm.rule;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class RandomFit extends Algorithm {

    private String name = "随机分配(Random)";
    private String type = "启发式规则";


    public List<Operator> run(Solution solution) {
        return run(solution, solution.getDecisionEntityList());
    }


    public List<Operator> run(Solution solution, List<DecisionEntity> orderedEntityList) {
        this.solution = solution;
        int trappedIteration = 0;
        int i = -1;

        // 1. 遍历决策实体
        for(DecisionEntity entity : orderedEntityList) {
            List<String> nameList = entity.getDecisionVariableList();
            // 2. 遍历决策变量
            for(String name : nameList) {
                // 3. 进行至多30次随机决策变量赋值
                for(int j = 0 ; j < 30 ; j ++) {
                    i ++;
                    trappedIteration ++;
                    Score oldScore = solution.getScore().clone();
                    Move move = RandomMove.move(entity, name);
                    if(move == null)    continue;
                    else                solution.updateScore(move);
                    boolean accept = solution.getScore().compareTo(oldScore) >= 0;
                    if(accept) {    // 3.1 若接受, 跳出var循环, 进行下一个entity/var赋值
                        historyOperatorList.add(move);
                        logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby (1/" + trappedIteration + ") moves\t" + move);
                        trappedIteration = 0;
                        break;
                    } else {        // 3.2 若不接受, 撤回
                        move.undo();
                        solution.updateScore(move);
                        if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                        solution.getScore().clone(oldScore);    // solution赋旧score(注意, 只是值, score对象不能变)
                    }
                }
            }
            if(update().equals("stop"))     break;
        }
        return historyOperatorList;
    }


    @Override
    public void run() {
        historyOperatorList = run(solution);
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
