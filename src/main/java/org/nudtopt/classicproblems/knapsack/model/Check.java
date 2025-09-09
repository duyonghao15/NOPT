package org.nudtopt.classicproblems.knapsack.model;

import org.nudtopt.api.constraint.Score;

import java.util.List;
import java.util.stream.Collectors;

public class Check extends org.nudtopt.api.constraint.Constraint {


    @Override
    public Score calScore() {
        Knapsack knapsack = ((Solution) solution).getKnapsack();
        List<Object> objectList = ((Solution) solution).getObjectList();
        int constraint = 0;
        int valueTotal = 0;
        double leftTotal = 0;
        // 1. 更新装包结果
        List<Object> inObjectList = objectList.stream().filter(object -> object.getKnapsack() != null).collect(Collectors.toList());
        // 2. 遍历i维属性, 逐一检查约束
        for(int i = 0 ; i < knapsack.getCapabilityList().size() ; i ++) {
            double usedCapability = 0;
            for(Object object : inObjectList) {                             // 遍历物品
                usedCapability += object.getPropertyList().get(i);          // 累加第i维属性
            }
            if(usedCapability > knapsack.getCapabilityList().get(i)) {      // 判断约束: 第i维属性占用量已超过背包容量
                constraint --;
            } else {
                leftTotal += knapsack.getCapabilityList().get(i) - usedCapability;
            }
        }
        // 3. 统计物品价值
        for(Object object : inObjectList) {
            valueTotal += object.getValue();
        }
        /* function ends */
        Score score = new Score();
        score.setHardScore(constraint);
        score.setMeanScore(valueTotal);
        score.setSoftScore(Math.round(leftTotal));
        return score;
    }


/* class ends */
}
