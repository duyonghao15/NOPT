package org.nudtopt.classicproblems.functionoptimization.f7;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f =
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double n = xList.size();
        double y1 = 0;
        double y2 = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x = xList.get(i).getValue();
            y1 += x * x;
            y2 += Math.cos(2 * Math.PI * x);
        }
        double y = -20 * Math.exp(-0.2 * Math.sqrt(y1 / n)) - Math.exp(y2 / n) + 20 + Math.E;
        return y;
    }




/* class ends */
}
