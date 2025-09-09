package org.nudtopt.classicproblems.functionoptimization.f3;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = max (|x|)
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y = Math.abs(xList.get(0).getValue());
        for(X x : xList) {
            y = Math.max(y, Math.abs(x.getValue()));
        }
        return y;
    }




/* class ends */
}
