package org.nudtopt.realworldproblems.multiplatformrouting.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.List;

public class Visit extends DecisionEntity {

    private Platform platform;                 // 哪个platform

    @DecisionVariable
    private Node node;                         // 决策变量: 经过哪个点

    private List<Node> optionalNodeList;       // 决策变量取值范围

    // getter & setter
    public Platform getPlatform() {
        return platform;
    }
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Node getNode() {
        return node;
    }
    public void setNode(Node node) {
        this.node = node;
    }

    public List<Node> getOptionalNodeList() {
        return optionalNodeList;
    }
    public void setOptionalNodeList(List<Node> optionalNodeList) {
        this.optionalNodeList = optionalNodeList;
    }

}
