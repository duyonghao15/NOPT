package org.nudtopt.classicproblems.functionoptimization.f5;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = Σ i*x^4 + random(0, 1)
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x = xList.get(i).getValue();
            y += Math.pow(x, 4) * i;
        }
        y += Tool.random();
        return y;
    }




/* class ends */
}
