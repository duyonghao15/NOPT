package org.nudtopt.classicproblems.assignmentproblem.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task extends DecisionEntity {

    @DecisionVariable
    private Resource resource;                                          // 指派给哪个资源

    private List<Resource> optionalResourceList = new ArrayList<>();    // 决策变量取值范围

    private Map<Resource, Long> resourceCostMap = new HashMap<>();      // 指派给不同资源所需的成本

    // getter & setter
    public Resource getResource() {
        return resource;
    }
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public List<Resource> getOptionalResourceList() {
        return optionalResourceList;
    }
    public void setOptionalResourceList(List<Resource> optionalResourceList) {
        this.optionalResourceList = optionalResourceList;
    }

    public Map<Resource, Long> getResourceCostMap() {
        return resourceCostMap;
    }
    public void setResourceCostMap(Map<Resource, Long> resourceCostMap) {
        this.resourceCostMap = resourceCostMap;
    }

    /**
     * 根据不同的指派资源, 获取成本
     * @return 成本
     */
    public long getCost() {
        Long cost = resourceCostMap.get(resource);
        return cost != null ? cost : 0;
    }


}
