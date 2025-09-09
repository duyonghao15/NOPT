package org.nudtopt.classicproblems.knapsack.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Object extends DecisionEntity {

    @DecisionVariable(nullable = true)
    private Knapsack knapsack;                                          // 决策变量: 物品是否放在背包里 (可为空, 即不放, 也可用0-1变量表示)

    private List<Knapsack> optionalKnapsackList = new ArrayList<>();    // 决策变量取值范围 (只有一个背包)

    private double value;                                               // 价值
    private List<Double> propertyList = new ArrayList<>();              // 多维属性清单
    private Map<String, Double> propertyMap = new HashMap<>();          // 多维属性map

    // getter & setter
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }

    public Knapsack getKnapsack() {
        return knapsack;
    }
    public void setKnapsack(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    public List<Knapsack> getOptionalKnapsackList() {
        return optionalKnapsackList;
    }
    public void setOptionalKnapsackList(List<Knapsack> optionalKnapsackList) {
        this.optionalKnapsackList = optionalKnapsackList;
    }

    public List<Double> getPropertyList() {
        return propertyList;
    }
    public void setPropertyList(List<Double> propertyList) {
        this.propertyList = propertyList;
    }

    public Map<String, Double> getPropertyMap() {
        return propertyMap;
    }
    public void setPropertyMap(Map<String, Double> propertyMap) {
        this.propertyMap = propertyMap;
    }


/* class ends */
}
