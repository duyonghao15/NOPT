package org.nudtopt.api.algorithm.rule;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.List;
import java.util.stream.Collectors;

public class RandomAllocate extends Algorithm {

    private String name = "随机无约束分配(Random)";
    private String type = "启发式规则";


    public List<Operator> run(Solution solution) {
        List<DecisionEntity> changeableEntityList = solution.getDecisionEntityList().stream().filter(DecisionEntity::isChangeable).collect(Collectors.toList());
        return run(solution, changeableEntityList);
    }


    /**
     * 先赋值, 最后评分一次式
     */
    public <D extends DecisionEntity> List<Operator> run(Solution solution, List<D> orderedEntityList) {
        this.solution = solution;
        int i = -1;

        // 1. 遍历决策实体
        for(D entity : orderedEntityList) {
            List<String> nameList = entity.getDecisionVariableList();
            // 2. 遍历决策变量
            for(String name : nameList) {
                List<Object> variableList = entity.getOptionalDecisionVariableList(name);
                if(variableList.size() == 0)    continue;
                // 3. 从值域中随机取值并赋值
                i ++;
                Object variable = Tool.randomFromList(variableList);
                Move move = Move.move(entity, name, variable);
                if(move == null)    continue;
                solution.updateScore(move);
                historyOperatorList.add(move);
                logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby move\t" + move);
            }
        }
        return historyOperatorList;
    }


    /**
     * 一步一评分式
     */
    /*public List<Move> run(Solution solution, List<DecisionEntity> orderedEntityList) {
        this.solution = solution;
        int i = -1;

        // 1. 遍历决策实体
        for(DecisionEntity entity : orderedEntityList) {
            List<String> nameList = entity.getDecisionVariableList();
            // 2. 遍历决策变量
            for(String name : nameList) {
                List<Object> variableList = entity.getOptionalDecisionVariableList(name);
                if(variableList.size() == 0)    continue;
                // 3. 从值域中随机取值并赋值
                i ++;
                Object variable = Tool.randomFromList(variableList);
                Move move = Move.move(entity, name, variable);
                if(move == null)    continue;
                else                solution.updateScore(move); // 每move一次, 就评分一次
                historyMoveList.add(move);
                logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby move\t" + move);
            }
            if(update().equals("stop"))     break;
        }
        return historyMoveList;
    }*/


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
