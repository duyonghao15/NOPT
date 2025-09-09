package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class NullReplace extends Move {


    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuList) {
        List<Move> moveList = new ArrayList<>();
        List<DecisionEntity> decisionEntityList = solution.getDecisionEntityList();

        // 1. 随机变量
        String name = Tool.randomFromList(decisionEntityList.get(0).getDecisionVariableList());

        // 2.1 随机1个variable != null的entity
        DecisionEntity entity_A = getVariableEntity(decisionEntityList, name);
        // 2.2 随机1个variable == null的entity
        DecisionEntity entity_B = getNonVariableEntity(decisionEntityList, name);
        if(entity_A == null)        return null;
        if(entity_B == null)        return null;
        if(entity_A == entity_B)    return null;

        // 3.1 将entity_A置空
        Move move_1 = new Move().move(entity_A, name, null);
        solution.updateScore(move_1);
        moveList.add(move_1);
        // 3.2 对entity_B赋值
        Move move_2 = RandomMove.move(entity_B, name);
        if(move_2 != null) {
            solution.updateScore(move_2);
            moveList.add(move_2);
        }

        if(moveList.size() == 0)    return moveAndScore(solution, tabuList);
        Operator operator = new Operator();
        operator.setMoveList(moveList);
        operator.update();
        return operator;
    }



    // 获得1个随机的 variable != null 的 entity
    private static DecisionEntity getVariableEntity(List<DecisionEntity> entityList, String name) {
        for(int i = 0 ; i < 50 ; i ++) {
            DecisionEntity entity = Tool.randomFromList(entityList);
            if(entity.getDecisionVariable(name) != null)    return entity;
        }   return null;
    }


    // 获得1个随机的 variable == null 的 entity
    private static DecisionEntity getNonVariableEntity(List<DecisionEntity> entityList, String name) {
        for(int i = 0 ; i < 50 ; i ++) {
            DecisionEntity entity = Tool.randomFromList(entityList);
            if(entity.getDecisionVariable(name) == null)    return entity;
        }   return null;
    }





/* class ends */
}
