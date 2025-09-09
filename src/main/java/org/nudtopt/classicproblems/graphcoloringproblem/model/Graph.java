package org.nudtopt.classicproblems.graphcoloringproblem.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class Graph extends Solution {

    @DecisionEntityList
    private List<Node> nodeList = new ArrayList<>();

    private List<Color> colorList = new ArrayList<>();

    // getter & setter
    public List<Node> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public List<Color> getColorList() {
        return colorList;
    }
    public void setColorList(List<Color> colorList) {
        this.colorList = colorList;
    }

}
