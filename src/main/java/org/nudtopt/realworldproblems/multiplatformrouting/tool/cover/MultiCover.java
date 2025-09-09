package org.nudtopt.realworldproblems.multiplatformrouting.tool.cover;

import org.nudtopt.realworldproblems.apiforsatellite.tool.interval.Interval;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.tool.comparator.IdComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiCover {

    // 1. node -> Map<platform, double>; platform -> double (时间)
    public static Map<Node, Map<Platform, double[]>> getMultiCoveredNodeMap(List<Node> totalNodeList, List<Visit> visitList) {
        visitList.sort(new IdComparator());                                    // id升序排序
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        Map<Node, Map<Platform, double[]>> nodePlatformTimeMap = new HashMap<>();

        // 1. 遍历nodeList, 判断其是否被visitList所覆盖
        for(Node node : totalNodeList) {
            Map<Platform, double[]> platformTimeMap = new HashMap<>();           // 覆盖该点的platform和time
            // 2. 遍历platformList
            for(Platform platform : platformNodeMap.keySet()) {
                List<Node> visitedNodeList = platformNodeMap.get(platform);    // 获得platform visit的nodeList
                double totalTravelTime = platform.getSetupTime();              // travelTime
                // 3. 遍历该platform visit的nodeList
                for(int i = 0 ; i < visitedNodeList.size() - 1 ; i ++) {
                    Node startNode = visitedNodeList.get(i);
                    Node endNode = visitedNodeList.get(i + 1);
                    double coveredTime_1 = totalTravelTime + (Function.getDistance(startNode, node) - platform.getRadius()) / platform.getSpeed() + platform.getStayTimePerNode();
                    double coveredTime_2 = totalTravelTime + (Function.getDistance(startNode, node) + platform.getRadius()) / platform.getSpeed() + platform.getStayTimePerNode();
                    double[] coveredTime = new double[] {coveredTime_1, coveredTime_2};
                    totalTravelTime += Function.getDistance(startNode, endNode) / platform.getSpeed() + platform.getStayTimePerNode();
                    switch (platform.getType()) {
                        case "airplane":
                            boolean covered = Cover.covered(node, startNode, platform.getRadius());
                            if(i == visitedNodeList.size() - 2) covered = covered || Cover.covered(node, endNode, platform.getRadius()); // 最后endNode检查
                            if(covered)     platformTimeMap.put(platform, coveredTime);     // 记录该平台最后一次覆盖的时间
                            break;
                        case "airship":
                            covered = Cover.covered(node, startNode, endNode, platform.getRadius(), true);
                            if(covered)     platformTimeMap.put(platform, coveredTime);     // 记录该平台最后一次覆盖的时间
                            break;
                        case "satellite":
                            if(i % 2 == 1)  break;                                          // 卫星只检查偶数node (0->1, 2->3 ...)
                            covered = Cover.covered(node, startNode, endNode, platform.getRadius(), false);
                            if(covered)     platformTimeMap.put(platform, coveredTime);     // 记录该平台最后一次覆盖的时间
                            break;
                        default:
                            System.out.println("error: " + platform + " 类型输入有误: " + platform.getType()  + "! ");
                            break;
                    }
                    /* node loop ends*/
                }
                /* platform loop ends*/
            }
            if(!platformTimeMap.isEmpty())  nodePlatformTimeMap.put(node, platformTimeMap);// node被platform访问过, 记录下来
            /* total node loop ends */
        }
        /* function ends */
        return nodePlatformTimeMap;
    }


    // 2. 计算多平台共视的收益
    public static double[] getMultiCoverScore(List<Node> nodeList, List<Visit> visitList) {
        Map<Node, Map<Platform, double[]>> nodePlatformTimeMap = getMultiCoveredNodeMap(nodeList, visitList);
        double totalInterval = 0;
        double singeCoverNum = 0;
        // 1. 遍历所有被覆盖的点
        for(Node node : nodePlatformTimeMap.keySet()) {
            Map<Platform, double[]> platformTimeMap = nodePlatformTimeMap.get(node);  // 覆盖该点的platform和时间
            // 2. 遍历覆盖该点的platform, 记录不同platform的访问时间
            List<double[]> timeList = new ArrayList<>();
            for(Platform platform : platformTimeMap.keySet()) {
                double[] coveredTime = platformTimeMap.get(platform);                 // platform覆盖该点的时间
                timeList.add(coveredTime);
            }
            // 3. 计算最短访问时间间隔
            if(timeList.size() == 1) {
                singeCoverNum ++;
                continue;
            }
            double interval = 100;
            for(int i = 0 ; i < timeList.size() ; i ++) {
                for(int j = i + 1 ; j < timeList.size() ; j ++) {
                    double[] coveredTime_1 = timeList.get(i);
                    double[] coveredTime_2 = timeList.get(j);
                    double t = Interval.getIntervalTime(coveredTime_1[0], coveredTime_1[1], coveredTime_2[0], coveredTime_2[1]);  // 两个区间的间隔时间
                    t = Math.max(0, t);
                    interval = Math.min(interval, t);
                }
            }
            totalInterval += interval;
            /* loop ends */
        }
        /* function ends */
        double meanInterval = 1.0 * totalInterval / nodeList.size();               // 每个node的平均重访时间
        double coverageRate = 10000 * Math.min(0.975, 1.0 * (nodePlatformTimeMap.size() - singeCoverNum) / nodeList.size());
        return new double[]{coverageRate, meanInterval};
    }

/* class ends */
}
