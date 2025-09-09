package org.nudtopt.classicproblems.travelingsalesmanproblem.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class Tour extends Solution {

    @DecisionEntityList
    private List<City> cityList = new ArrayList<>();                // 城市列表

    private Salesman salesman;

    // getter & setter
    public List<City> getCityList() {
        return cityList;
    }
    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

    public Salesman getSalesman() {
        return salesman;
    }
    public void setSalesman(Salesman salesman) {
        this.salesman = salesman;
    }


}
