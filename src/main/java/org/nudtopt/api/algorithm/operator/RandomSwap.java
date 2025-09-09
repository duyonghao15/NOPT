package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class RandomSwap extends Swap {


    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuList) {
        if(solution.getDecisionEntityList().size() <= 1)       return super.moveAndScore(solution, tabuList);
        List<DecisionEntity> twoEntities = solution.getRandomDecisionEntity(2);
        List<Move> moveList = randomSwap(twoEntities.get(0), twoEntities.get(1));   // 随机交换一个变量
        return createAndUpdate(moveList, solution);                                 // 返回: 创建一个综合算子, 并更新解的约束收益
    }


/* class ends */
}
