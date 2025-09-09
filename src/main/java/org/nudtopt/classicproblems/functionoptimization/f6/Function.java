package org.nudtopt.classicproblems.functionoptimization.f6;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f = Σ -x * sin( |x|^0.5 )
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        double y = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x = xList.get(i).getValue();
            y -= x * Math.sin(Math.sqrt(Math.abs(x)));
        }
        return y;
    }




/* class ends */
}
