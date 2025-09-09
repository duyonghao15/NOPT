package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomBlockSwap extends RandomBlockMove {


    /**
     * 针对同一分配资源(决策变量相同)的实体，整块移动到另一个资源(决策变量统一修改)
     * @param solution 原解
     * @param tabuList 禁忌表
     * @return         元算子集合
     */
    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuList) {
        if(solution.getDecisionEntityList().size() <= 1)       return super.moveAndScore(solution, tabuList);
        // 1. 选择对象
        List<DecisionEntity> twoEntities = solution.getRandomDecisionEntity(2);
        Customer customer_1 = (Customer) twoEntities.get(0);
        Customer customer_2 = (Customer) twoEntities.get(1);
        Vehicle vehicle_1 = customer_1.getVehicle();
        Vehicle vehicle_2 = customer_2.getVehicle();
        if(vehicle_1 == vehicle_2) {                                                    // 若资源(如车辆相同)
            return super.moveAndScore(solution, tabuList);                              // 直接随机move
        }
        List<Customer> customerList_1 = vehicle_1.getCustomerList();
        List<Customer> customerList_2 = vehicle_2.getCustomerList();
        // 2. 挑选出n个待移动的实体
        int moveNum = Tool.randomFromList(Arrays.asList(1, Math.min(customerList_1.size(), customerList_2.size())));
        List<Customer> changingCustomerList_1 = Tool.randomFromList(customerList_1, moveNum, true);
        List<Customer> changingCustomerList_2 = Tool.randomFromList(customerList_2, moveNum, true);
        // 3. 执行swap运算
        List<Move> moveList = new ArrayList<>();
        for(int i = 0 ; i < moveNum ; i ++) {
            Customer swapCustomer_1 = changingCustomerList_1.get(i);
            Customer swapCustomer_2 = changingCustomerList_2.get(i);
            List<Move> swapMoveList = Swap.swap(swapCustomer_1, swapCustomer_2, "vehicle");
            moveList.addAll(swapMoveList);
        }
        return createAndUpdate(moveList, solution);                                     // 返回: 创建一个综合算子, 并更新解的约束收益
    }



}
