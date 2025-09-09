package org.nudtopt.classicproblems.graphcoloringproblem.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node extends DecisionEntity {

    @DecisionVariable
    private Color color;                                        // 决策变量: 颜色
    private List<Node> adjacentNodeList = new ArrayList<>();    // 相邻的节点集合(需判断颜色是否相同)
    private List<Color> optionalColorList = new ArrayList<>();  // 决策变量取值范围


    // getter & setter
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public List<Node> getAdjacentNodeList() {
        return adjacentNodeList;
    }
    public void setAdjacentNodeList(List<Node> adjacentNodeList) {
        this.adjacentNodeList = adjacentNodeList;
    }

    public List<Color> getOptionalColorList() {
        return optionalColorList;
    }
    public void setOptionalColorList(List<Color> optionalColorList) {
        this.optionalColorList = optionalColorList;
    }


    /**
     * 获取当前节点与相邻节点的颜色冲突数
     * @return 颜色冲突数量
     */
    public int getConflict() {
        int conflict = 0;
        if(color == null)       return conflict;
        for(Node adjacentNode : adjacentNodeList) {
            Color adjacentColor = adjacentNode.getColor();
            if(adjacentColor != null && adjacentColor != color) {
                conflict ++;
            }
        }
        return conflict;
    }


/* class ends */
}
