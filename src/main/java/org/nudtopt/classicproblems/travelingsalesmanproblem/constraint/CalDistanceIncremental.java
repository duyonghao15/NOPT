package org.nudtopt.classicproblems.travelingsalesmanproblem.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.City;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Tour;

import java.util.ArrayList;
import java.util.List;

public class CalDistanceIncremental extends CalDistance {


    @Override
    public Score calScore() {
        // 0. 初始化计算
        if(operator == null) {
            return super.calScore();
        }

        // 1. 获取新旧顺序
        List<City> oldCityList = this.cityList;
        List<City> newCityList = ((Tour) solution).getSalesman().getCityList();

        // 2. 增量式计算收益
        for(int i = 0 ; i < oldCityList.size() - 1 ; i ++) {
            City oldCity_1 = oldCityList.get(i);
            City oldCity_2 = oldCityList.get(i + 1);
            City newCity_1 = newCityList.get(i);
            City newCity_2 = newCityList.get(i + 1);
            if(oldCity_1 != newCity_1 || oldCity_2 != newCity_2) {          // 重新计算不同的路径
                double oldDistance = getDistance(oldCity_1, oldCity_2);
                double newDistance = getDistance(newCity_1, newCity_2);
                this.distance  = this.distance - oldDistance + newDistance;
            }
        }
        this.cityList = new ArrayList<>(newCityList);                       // 更新旧顺序
        /* function ends */
        Score score = new Score();
        score.setMeanScore(- Math.round(this.distance));
        return score;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }


/* class ends */
}
