package org.nudtopt.classicproblems.knapsack.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Knapsack extends NumberedObject {

    private List<Double> capabilityList = new ArrayList<>();            // 多维容量清单
    private Map<String, Double> capabilityMap = new HashMap<>();        // 多维容量map

    private List<Object> objectList = new ArrayList<>();                // 装入该背包中的物品清单 (优化过程中实时变化)
    private List<Double> usedCapabilityList = new ArrayList<>();        // 已占用的多维容量清单   (优化过程中实时变化)
    private List<Double> usedCapabilityMap  = new ArrayList<>();        // 已占用的多维容量清单   (优化过程中实时变化)

    // getter & setter
    public List<Double> getCapabilityList() {
        return capabilityList;
    }
    public void setCapabilityList(List<Double> capabilityList) {
        this.capabilityList = capabilityList;
    }

    public Map<String, Double> getCapabilityMap() {
        return capabilityMap;
    }
    public void setCapabilityMap(Map<String, Double> capabilityMap) {
        this.capabilityMap = capabilityMap;
    }

    public List<Object> getObjectList() {
        return objectList;
    }
    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    public List<Double> getUsedCapabilityList() {
        return usedCapabilityList;
    }
    public void setUsedCapabilityList(List<Double> usedCapabilityList) {
        this.usedCapabilityList = usedCapabilityList;
    }

    public List<Double> getUsedCapabilityMap() {
        return usedCapabilityMap;
    }
    public void setUsedCapabilityMap(List<Double> usedCapabilityMap) {
        this.usedCapabilityMap = usedCapabilityMap;
    }


/* class ends */
}
