package org.nudtopt.realworldproblems.multiplatformrouting.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.List;

public class Scenario extends Solution {

    @DecisionEntityList
    private List<Visit> visitList;

    private List<Platform> platformList;

    private List<Node> nodeList;

    // getter & setter
    public List<Visit> getVisitList() {
        return visitList;
    }
    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public List<Platform> getPlatformList() {
        return platformList;
    }
    public void setPlatformList(List<Platform> platformList) {
        this.platformList = platformList;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }


}
