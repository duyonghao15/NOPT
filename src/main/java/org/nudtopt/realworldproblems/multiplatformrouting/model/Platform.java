package org.nudtopt.realworldproblems.multiplatformrouting.model;

import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.tool.comparator.IdComparator;

import java.util.ArrayList;
import java.util.List;

public class Platform extends NumberedObject {

    private double radius;           // 覆盖半径
    private double speed;            // 移动速度
    private double setupTime;        // 进去区域开始工作的准备时间
    private double stayTimePerNode;  // 在node上所需的停留时间
    // private String type;             // airplane, airship, satellite,

    // getter & setter
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSetupTime() {
        return setupTime;
    }
    public void setSetupTime(double setupTime) {
        this.setupTime = setupTime;
    }

    public double getStayTimePerNode() {
        return stayTimePerNode;
    }
    public void setStayTimePerNode(double stayTimePerNode) {
        this.stayTimePerNode = stayTimePerNode;
    }


    // 以下是辅助函数
    public List<Node> getVisitedNodeList(List<Visit> visitList) {
        visitList.sort(new IdComparator());             // id升序
        List<Node> nodeList = new ArrayList<>();
        for(Visit visit : visitList) {
            if(this == visit.getPlatform()) {
                nodeList.add(visit.getNode());
            }
        }
        return nodeList;
    }


    public double getTravelTime(Node startNode, Node endNode) {
        return Function.getDistance(startNode, endNode) / speed;
    }


    public double getTravelTime(List<Visit> visitList) {
        return getTravelTime(getVisitedNodeList(visitList), setupTime);
    }


    public double getTravelTime(List<Node> nodeList, double setupTime) {
        double travelTime = setupTime;
        for(int i = 0 ; i < nodeList.size() - 1 ; i ++) {
            Node startNode = nodeList.get(i);
            Node endNode = nodeList.get(i + 1);
            travelTime += Function.getDistance(startNode, endNode) / speed + stayTimePerNode;
        }
        return travelTime;
    }


/* class ends */
}
