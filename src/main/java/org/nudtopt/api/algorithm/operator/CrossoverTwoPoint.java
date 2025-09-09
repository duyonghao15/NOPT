package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrossoverTwoPoint extends CrossoverOnePoint {


    /**
     * 针对两个决策实体list, 进行交叉操作
     * 双点交叉 (随机选择两个位置, 交叉这两个位置之间的全部实体变量)
     * @param list_1 实体list_1
     * @param list_2 实体list_2
     * @param <D>    泛型, 决策实体
     * @return       元算子集合
     */
    @Override
    public <D extends DecisionEntity> List<Move> randomCrossover(List<D> list_1, List<D> list_2) {
        // 1. 随机获取交叉的下标索引index
        int maxIndex = Math.min(list_1.size(), list_2.size()) - 1;
        if(maxIndex <= 0) {                                                             // 若最大的下标索引<=0
            return null;                                                                // do nothing
        }
        List<Integer> indexes = Tool.randomFromList(Arrays.asList(0, maxIndex), 2, true);    // 随机选择两个下标索引
        if(indexes.size() <= 1) {
            return super.randomCrossover(list_1, list_2);
        }
        int index_1 = indexes.get(0) < indexes.get(1) ? indexes.get(0) : indexes.get(1);            // 较小的index
        int index_2 = indexes.get(0) < indexes.get(1) ? indexes.get(1) : indexes.get(0);            // 较小的index
        // 2. 交换此index前所有决策实体的变量
        List<Move> moveList = new ArrayList<>();
        for(int i = index_1 ; i <= index_2 ; i ++) {
            D decisionEntity_1 = list_1.get(i);
            D decisionEntity_2 = list_2.get(i);
            List<Move> moves = Swap.swap(decisionEntity_1, decisionEntity_2);                       // 交换所有决策变量
            moveList.addAll(moves);
        }
        return moveList;
    }


}
