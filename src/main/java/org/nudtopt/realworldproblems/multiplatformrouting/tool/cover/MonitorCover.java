package org.nudtopt.realworldproblems.multiplatformrouting.tool.cover;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.tool.comparator.IdComparator;

import java.util.*;

public class MonitorCover {

    // 返回监视率
    public static double getMonitorCoverRatio(List<Node> totalNodeList, List<Visit> visitList, int maxTime) {
        visitList.sort(new IdComparator());                                    // id升序排序
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        double totalMonitoringRate = 0;
        // 1. 遍历nodeList, 判断其是否被visitList所覆盖
        for(Node node : totalNodeList) {
            boolean covered = false;                                           // 该点是否被覆盖
            node.getCoveringTimeSet().clear();
            // 2. 遍历platformList
            for(Platform platform : platformNodeMap.keySet()) {
                List<Node> visitedNodeList = platformNodeMap.get(platform);    // 获得platform visit的nodeList
                double totalTravelTime = platform.getSetupTime();              // travelTime
                // 3. 遍历该platform visit的nodeList
                for(int i = 0 ; i < visitedNodeList.size() - 1 ; i ++) {
                    Node startNode = visitedNodeList.get(i);
                    Node endNode = visitedNodeList.get(i + 1);

                    switch (platform.getType()) {
                        case "airplane":
                            covered = Cover.covered(node, startNode, platform.getRadius());
                            if(i == visitedNodeList.size() - 2) covered = covered || Cover.covered(node, endNode, platform.getRadius()); // 最后endNode检查
                            break;
                        case "airship":
                            covered = Cover.covered(node, startNode, endNode, platform.getRadius(), true);
                            break;
                        case "satellite":
                            if(i % 2 == 1)  break;                                         // 卫星只检查偶数node (0->1, 2->3 ...)
                            covered = Cover.covered(node, startNode, endNode, platform.getRadius(), false);
                            break;
                        default:
                            System.out.println("error: " + platform + " 类型输入有误: " + platform.getType()  + "! ");
                            break;
                    }

                    if(covered) {
                        Set<Integer> coveringTimeSet = getTimeSet(platform, node, startNode, totalTravelTime, endNode);
                        node.getCoveringTimeSet().addAll(coveringTimeSet);  // 将timeSet加入
                    }
                    totalTravelTime = totalTravelTime + platform.getStayTimePerNode() + Function.getDistance(startNode, endNode) / platform.getSpeed();;
                    // ***** 飞机专属 最后一个 ******
                    if(covered && platform.getType().equals("airplane") && i == visitedNodeList.size() - 2) {
                        Set<Integer> coveringTimeSet = getTimeSet(platform, node, startNode, totalTravelTime, endNode);
                        node.getCoveringTimeSet().addAll(coveringTimeSet);  // 将timeSet加入
                    }
                    /* node loop ends*/
                }
                /* platform loop ends*/
            }
            int coveringTime = node.getCoveringTimeSet().size();
            double monitoringRate = 1.0 * coveringTime / maxTime;
            totalMonitoringRate += monitoringRate;
            /* total node loop ends */
        }
        /* function ends */
        return totalMonitoringRate / totalNodeList.size();
    }




    // 辅助函数
    public static double getRate(Node node, Node startNode, Node endNode) {
        double x0 = node.getX();
        double y0 = node.getY();
        double x1 = startNode.getX();
        double y1 = startNode.getY();
        double x2 = endNode.getX();
        double y2 = endNode.getY();

        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2 * y1 - x1 * y2;
        double distance_0Chui = Math.abs(A * x0 + B * y0 + C) / Math.sqrt(A * A + B * B);

        double distance_01_sqrt = Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2);
        double distance_12_sqrt = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        double distance_1Chui_sqrt = distance_01_sqrt - Math.pow(distance_0Chui, 2);
        double rate = Math.sqrt(distance_1Chui_sqrt / distance_12_sqrt);
        return rate;
    }


    // 获取时间
    public static Set<Integer> getTimeSet(Platform platform, Node node, Node startNode, double startTime, Node endNode) {
        double travelTime = Function.getDistance(startNode, endNode) / platform.getSpeed();
        double timeLeft;
        double timeRight;
        if(platform.getType().equals("airplane")) {
            timeLeft = startTime;
            timeRight = startTime + platform.getStayTimePerNode();
        } else {
            double rate = getRate(node, startNode, endNode);
            double nodeTime = startTime + platform.getStayTimePerNode() + travelTime * rate;
            timeLeft = nodeTime - platform.getRadius() / platform.getSpeed();
            timeRight = nodeTime + platform.getRadius() / platform.getSpeed();
        }
        Set<Integer> coveringTimeSet = new HashSet<>();
        for(int j = (int) timeLeft ; j <= timeRight + 1 ; j ++) {
            coveringTimeSet.add(j);
        }
        return coveringTimeSet;
    }






/* class ends */
}
