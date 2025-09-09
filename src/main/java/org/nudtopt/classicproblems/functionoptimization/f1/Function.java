package org.nudtopt.classicproblems.functionoptimization.f1;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = Σ x^2
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y = 0;
        for(X x : xList) {
            y += Math.pow(x.getValue(), 2);
        }
        return y;
    }




/* class ends */
}
