package org.nudtopt.classicproblems.functionoptimization.f10;

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
            y1 += Math.pow(x - 1, 2) * (1 + Math.sin(3 * Math.PI * x + 1));
            y2 += u(x, 5, 100, 4);
        }
        double y = 0.1 * (Math.pow(Math.sin(3 * Math.PI * xList.get(0).getValue()), 2) + y1 + Math.pow(xList.get(xList.size() - 1).getValue() - 1, 2) * (1 + Math.pow(Math.sin(2 * Math.PI * xList.get(xList.size() - 1).getValue()), 2)) + y2 );
        return y;
    }


    private double u(double x, double a, double k, double m) {
        double u;
        if(x > a) {
            u = k * (x - a);
        } else if (x > -a && x < a) {
            u = 0;
        } else {
            u = k * (-x - a);
        }
        return u;
    }


/* class ends */
}
