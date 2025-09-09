package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrossoverOnePoint extends RandomMove {


    /**
     * 交叉运算
     * @param solution 原解
     * @param tabuList 禁忌表
     * @return         元算子集合
     */
    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuList) {
        if(solution.getDecisionEntityList().size() <= 1)       return super.moveAndScore(solution, tabuList);
        // 1. 选择对象
        List<DecisionEntity> twoEntities = Tool.randomFromList(solution.getDecisionEntityList(), 2, true);
        Customer customer_1 = (Customer) twoEntities.get(0);
        Customer customer_2 = (Customer) twoEntities.get(1);
        Vehicle vehicle_1 = customer_1.getVehicle();
        Vehicle vehicle_2 = customer_2.getVehicle();
        if(vehicle_1 == vehicle_2) {                                                    // 若资源(如车辆相同)
            return super.moveAndScore(solution, tabuList);                              // 直接随机move
        }
        List<Customer> customerList_1 = vehicle_1.getCustomerList();
        List<Customer> customerList_2 = vehicle_2.getCustomerList();
        // 2. 执行交叉运算
        List<Move> moveList = randomCrossover(customerList_1, customerList_2);
        return createAndUpdate(moveList, solution);                                     // 返回: 创建一个综合算子, 并更新解的约束收益
    }


    /**
     * 针对两个决策实体list, 进行交叉操作
     * 单点交叉 (随机选择一个位置, 交叉该位置之前的全部实体变量)
     * @param list_1 实体list_1
     * @param list_2 实体list_2
     * @param <D>    泛型, 决策实体
     * @return       元算子集合
     */
    public <D extends DecisionEntity> List<Move> randomCrossover(List<D> list_1, List<D> list_2) {
        // 1. 随机获取交叉的下标索引index
        int maxIndex = Math.min(list_1.size(), list_2.size()) - 1;
        if(maxIndex <= 0) {                                                             // 若最大的下标索引<=0
            return null;                                                                // do nothing
        }
        int randomIndex = Tool.randomFromList(Arrays.asList(0, maxIndex));              // 随机选择一个下标索引
        // 2. 交换此index前所有决策实体的变量
        List<Move> moveList = new ArrayList<>();
        for(int i = 0 ; i <= randomIndex ; i ++) {
            D decisionEntity_1 = list_1.get(i);
            D decisionEntity_2 = list_2.get(i);
            List<Move> moves = Swap.swap(decisionEntity_1, decisionEntity_2);                       // 交换所有决策变量
            moveList.addAll(moves);
        }
        return moveList;
    }

}
