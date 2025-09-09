package org.nudtopt.realworldproblems.multiplatformrouting.tool.cover;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.tool.comparator.IdComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cover {


    // 1. 判断某点是否被*一点*所覆盖
    public static boolean covered(Node node, Node startNode, double radius) {
        if(Math.pow(node.getX() - startNode.getX(), 2) + Math.pow(node.getY() - startNode.getY(), 2) < radius*radius) {
            return true;
        }
        /* function ends */
        return false;
    }


    // 2. 判断某点是否被*两点连线*所覆盖
    public static boolean covered(Node node, Node startNode, Node endNode, double radius, boolean circle) {
        List<Double> twoLines = Function.getTwoLines(startNode, endNode, radius);
        double a = twoLines.get(0);
        double b = twoLines.get(1);
        double c = twoLines.get(2);
        double d = twoLines.get(3);
        double e = twoLines.get(4);
        double f = twoLines.get(5);
        double x = node.getX();
        double y = node.getY();
        if(y <= a * x + b + c) {
            if(y >= a * x + b - c) {
                if(y >= d * x + e) {
                    if(y <= d * x + f) {
                        return true;   // 矩形区域
                    }
                }
            }
        }
        if(circle) {                   // 考虑端部的圆形区域
            return Math.pow(x - endNode.getX(), 2) + Math.pow(y - endNode.getY(), 2) <= radius*radius;
        }
        /* function ends */
        return false;
    }


    // 3. 获取所有被cover的nodeList
    public static List<Node> getCoveredNodeList(List<Node> totalNodeList, List<Visit> visitList, boolean mark) {
        visitList.sort(new IdComparator());                                    // id升序排序
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        List<Node> coveredNodeList = new ArrayList<>();

        // 1. 遍历nodeList, 判断其是否被visitList所覆盖
        for(Node node : totalNodeList) {
            boolean covered = false;                                                // 该点是否被覆盖
            // 2. 遍历platformList
            c: {for(Platform platform : platformNodeMap.keySet()) {
                List<Node> visitedNodeList = platformNodeMap.get(platform);         // 获得platform visit的nodeList
                // 3. 遍历该platform visit的nodeList
                for(int i = 0 ; i < visitedNodeList.size() - 1 ; i ++) {
                    Node startNode = visitedNodeList.get(i);
                    Node endNode = visitedNodeList.get(i + 1);
                    switch (platform.getType()) {
                        case "airplane":
                            covered = covered(node, startNode, platform.getRadius());
                            if(i == visitedNodeList.size() - 2) covered = covered || covered(node, endNode, platform.getRadius()); // 最后endNode检查
                            break;
                        case "airship":
                            covered = covered(node, startNode, endNode, platform.getRadius(), true);
                            break;
                        case "satellite":
                            if(i % 2 == 1)  break;                                  // 卫星只检查偶数node (0->1, 2->3 ...)
                            covered = covered(node, startNode, endNode, platform.getRadius(), false);
                            break;
                        default:
                            System.out.println("error: " + platform + " 类型输入有误: " + platform.getType()  + "! ");
                            break;
                    }
                    if(covered) {
                        coveredNodeList.add(node);
                        if(mark)    node.getCoveringPlatformList().add(platform);   // mark: 将platform记入node中(最后画图用)
                        else        break c;                                        // !mark: 若被覆盖一次, 即跳出c循环
                    }
                    /* node loop ends*/
                }
                /* platform loop ends*/
            }}
            /* total node loop ends */
        }
        /* function ends */
        return coveredNodeList;
    }



/* class ends */
}
