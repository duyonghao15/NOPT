package org.nudtopt.classicproblems.functionoptimization.f8;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f =
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y1 = 0;
        double y2 = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x = xList.get(i).getValue();
            y1 += x * x;
            y2 *= Math.cos(x / Math.sqrt(i + 1));
        }
        double y = y1 / 4000 - y2 + 1;
        return y;
    }




/* class ends */
}
