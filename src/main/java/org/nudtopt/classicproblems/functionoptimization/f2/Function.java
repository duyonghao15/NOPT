package org.nudtopt.classicproblems.functionoptimization.f2;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = Σ |x| + |x|
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y1 = 0;
        double y2 = 0;
        for(X x : xList) {
            y1 += Math.abs(x.getValue());
            y2 *= Math.abs(x.getValue());
        }
        return y1 + y2;
    }




/* class ends */
}
