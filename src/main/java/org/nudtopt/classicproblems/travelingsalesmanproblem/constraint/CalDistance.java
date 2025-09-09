package org.nudtopt.classicproblems.travelingsalesmanproblem.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.City;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalDistance extends Constraint {

    protected List<City> cityList = new ArrayList<>();                          // 当前城市顺序
    protected Map<String, Double> distanceMap = new HashMap<>();                // 距离矩阵
    protected double distance = 0;                                              // 累计距离

    @Override
    public Score calScore() {
        // 计算距离
        Tour tour = (Tour) solution;
        List<City> cityList = new ArrayList<>(tour.getCityList());
        tour.getComparator().sort(cityList);
        double distance = getDistance(cityList);
        this.cityList = cityList;
        this.distance = distance;
        // 设置评分
        Score score = new Score();
        score.setMeanScore(- Math.round(distance));
        return score;
    }


    /**
     * 根据城市序列, 计算距离总长
     * @param cityList 城市序列
     * @return         距离总长
     */
    public double getDistance(List<City> cityList) {
        double totalDistance = 0;
        for(int i = 0 ; i < cityList.size() - 1 ; i ++) {
            City city_1 = cityList.get(i);
            City city_2 = cityList.get(i + 1);
            double distance = getDistance(city_1, city_2);
            totalDistance  += distance;
        }
        return totalDistance;
    }



    /**
     * 计算两个城市之间的直线距离
     * @author       杜永浩
     * @param city_1 起点城市
     * @param city_2 终点城市
     * @return       直线距离
     */
    public static double calDistance(City city_1, City city_2) {
        double distance = Math.pow(city_1.getX() - city_2.getX(), 2) + Math.pow(city_1.getY() - city_2.getY(), 2);
        distance = Math.sqrt(distance);
        return Math.round(distance);
    }


    /**
     * 基于距离矩阵, 获取两个城市之间的直线距离
     * @author       杜永浩
     * @param city_1 起点城市
     * @param city_2 终点城市
     * @return       直线距离
     */
    public double getDistance(City city_1, City city_2) {
        if(city_1 == city_2 || city_1.getId().equals(city_2.getId()))    return 0;
        String index;
        if(city_1.getId() < city_2.getId())     index = city_1.getId() + " <-> " + city_2.getId();
        else                                    index = city_2.getId() + " <-> " + city_1.getId();
        Double distance = distanceMap.get(index);
        if(distance == null)                    return 0;
        else                                    return distance;
    }



    /**
     * 创建城市距离矩阵
     * @author         杜永浩
     * @param cityList 城市列表
     * @return         距离矩阵
     */
    public Map<String, Double> createDistanceMap(List<City> cityList) {
        logger.info("正创建 -> TSP城市距离矩阵 ......");
        Map<String, Double> map = new HashMap<>();
        for(int i = 0 ; i < cityList.size() ; i ++) {
            for(int j = i + 1 ; j < cityList.size() ; j ++) {
                double distance = calDistance(cityList.get(i), cityList.get(j));
                String index = cityList.get(i).getId() + " <-> " + cityList.get(j).getId();
                map.put(index, distance);
            }
        }
        logger.info("已创建 -> TSP城市距离索引表 (C " + cityList.size() + "_2 = " + map.size() + ")！\n");
        this.distanceMap = map;
        return map;
    }



    // getter & setter
    public List<City> getCityList() {
        return cityList;
    }
    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public Map<String, Double> getDistanceMap() {
        return distanceMap;
    }
    public void setDistanceMap(Map<String, Double> distanceMap) {
        this.distanceMap = distanceMap;
    }

/* class ends */
}
