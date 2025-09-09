package org.nudtopt.realworldproblems.networkdisintegration.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Link extends DecisionEntity {

    private Node startNode;                                                 // 起点节点
    private Node endNode;                                                   // 终点节点
    private double weight = 1;                                              // 边的权重

    @DecisionVariable
    private Boolean disintegrate = false;                                   // 决策变量: 是否被瓦解

    private List<Boolean> optionalDisintegrateList = new ArrayList<>();     // 决策变量取值范围

    // getter & setter
    public Node getStartNode() {
        return startNode;
    }
    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }
    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Boolean getDisintegrate() {
        return disintegrate;
    }
    public void setDisintegrate(Boolean disintegrate) {
        this.disintegrate = disintegrate;
    }

    public List<Boolean> getOptionalDisintegrateList() {
        return optionalDisintegrateList;
    }
    public void setOptionalDisintegrateList(List<Boolean> optionalDisintegrateList) {
        this.optionalDisintegrateList = optionalDisintegrateList;
    }

}
