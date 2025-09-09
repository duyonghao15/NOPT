package org.nudtopt.classicproblems.functionoptimization;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;

import java.util.List;

public class Function extends Constraint {

    @Override
    public Score calScore() {
        List<X> xList = ((FunctionSolution) solution).getXList();
        double f = function(xList);
        Score score = new Score();
        score.setMeanScore(- Math.round(f * 100));          // 最小值问题, 取负转为最大值问题
        return score;
    }


    /**
     * @auther       杜永浩
     * @param xList  x输入值
     * @return       f函数输出值
     */
    public double function(List<X> xList) {
        return 0;
    }




/* class ends */
}
