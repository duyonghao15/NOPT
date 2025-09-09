package org.nudtopt.classicproblems.knapsack.model;

import org.nudtopt.api.model.DecisionEntityList;

import java.util.ArrayList;
import java.util.List;

public class Solution extends org.nudtopt.api.model.Solution {

    @DecisionEntityList
    private List<Object> objectList = new ArrayList<>();        // 物品集合

    private Knapsack knapsack;                                  // 一个背包

    private double bestKnowSolution;                            // 已知最优解

    // getter & setter
    public List<Object> getObjectList() {
        return objectList;
    }
    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public Knapsack getKnapsack() {
        return knapsack;
    }
    public void setKnapsack(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    public double getBestKnowSolution() {
        return bestKnowSolution;
    }
    public void setBestKnowSolution(double bestKnowSolution) {
        this.bestKnowSolution = bestKnowSolution;
    }

}
