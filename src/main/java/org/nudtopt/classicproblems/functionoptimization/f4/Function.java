package org.nudtopt.classicproblems.functionoptimization.f4;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = Σ 100*(xi+1 + xi^2)^2 + (xi - 1)^2
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x1 = xList.get(i).getValue();
            double x2 = xList.get(i + 1).getValue();
            y += 100 * Math.pow(x2 + x1 * x1, 2) + Math.pow(x1 - 1, 2);
        }
        return y;
    }




/* class ends */
}
