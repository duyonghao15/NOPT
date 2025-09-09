package org.nudtopt.classicproblems.vehicleroutingproblem.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class Routes extends Solution {

    @DecisionEntityList
    private List<Customer> customerList = new ArrayList<>();

    private List<Vehicle>  vehicleList  = new ArrayList<>();

    private Depot depot;

    // getter & setter
    public List<Customer> getCustomerList() {
        return customerList;
    }
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public Depot getDepot() {
        return depot;
    }
    public void setDepot(Depot depot) {
        this.depot = depot;
    }
}
