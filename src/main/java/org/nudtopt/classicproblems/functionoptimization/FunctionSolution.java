package org.nudtopt.classicproblems.functionoptimization;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class FunctionSolution extends Solution {

    @DecisionEntityList
    List<X> xList = new ArrayList<>();

    // getter & setter
    public List<X> getXList() {
        return xList;
    }
    public void setXList(List<X> xList) {
        this.xList = xList;
    }


}
