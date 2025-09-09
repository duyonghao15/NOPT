package org.nudtopt.classicproblems.travelingsalesmanproblem.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.List;

public class Salesman extends NumberedObject {

    private List<City> cityList = new ArrayList<>();

    // getter & setter
    public List<City> getCityList() {
        return cityList;
    }
    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
