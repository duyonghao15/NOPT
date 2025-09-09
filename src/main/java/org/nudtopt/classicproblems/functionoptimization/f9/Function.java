package org.nudtopt.classicproblems.functionoptimization.f9;

import org.nudtopt.classicproblems.functionoptimization.X;

import java.util.List;

public class Function extends org.nudtopt.classicproblems.functionoptimization.Function {

    /**
     * 基准函数    f =
     * 理论最优值  f = 0
     */
    @Override
    public double function(List<X> xList) {
        int n = xList.size();
        double f1 = 0;
        double f2 = 0;
        for(int i = 0 ; i < xList.size() - 1 ; i ++) {
            double x = xList.get(i).getValue();
            double y = y(x);
            double u = u(x, 10, 100, 4);
            f1 += Math.pow(y - 1, 2) * (1 + 10 * Math.pow(Math.sin(Math.PI * y), 2));
            f2 += u;
        }
        double f = Math.PI / n * ( 10 * Math.sin(Math.PI * y(xList.get(0).getValue()))  +  f1  +  Math.pow(y(xList.get(xList.size() - 1).getValue()), 2)  +  f2);
        return f;
    }



    private double y(double x) {
        return 1 + (x + 1) / 4;
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
