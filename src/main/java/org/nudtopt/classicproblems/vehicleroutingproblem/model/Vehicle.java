package org.nudtopt.classicproblems.vehicleroutingproblem.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.List;

public class Vehicle extends NumberedObject {

    private Depot depot;                                        // 车库
    private int capacity;                                       // 车的容量
    private List<Customer> customerList = new ArrayList<>();    // 车辆的路径: depot-> customerList -> depot

    // getter & setter
    public Depot getDepot() {
        return depot;
    }
    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

}
