package org.nudtopt.realworldproblems.networkdisintegration.model;

import org.nudtopt.api.model.DecisionEntityList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network extends org.nudtopt.api.model.Solution {

    @DecisionEntityList
    private List<Node> nodeList = new ArrayList<>();                // 节点列表

    @DecisionEntityList
    private List<Link> linkList = new ArrayList<>();                // 边列表

    private Map<Long, Node> nodeMap = new HashMap<>();              // 节点索引表

    private double maxDisintegrationPercent = 0.1;                  // 最大节点/边的瓦解比例

    // getter & setter
    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public List<Link> getLinkList() {
        return linkList;
    }
    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public Map<Long, Node> getNodeMap() {
        return nodeMap;
    }
    public void setNodeMap(Map<Long, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public double getMaxDisintegrationPercent() {
        return maxDisintegrationPercent;
    }
    public void setMaxDisintegrationPercent(double maxDisintegrationPercent) {
        this.maxDisintegrationPercent = maxDisintegrationPercent;
    }

}
