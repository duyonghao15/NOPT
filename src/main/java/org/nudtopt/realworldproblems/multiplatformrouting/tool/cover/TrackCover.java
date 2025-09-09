package org.nudtopt.realworldproblems.multiplatformrouting.tool.cover;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.tool.comparator.IdComparator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrackCover {

    // 返回跟踪率
    public static double getTrackCoverRatio(List<Node> totalNodeList, List<Visit> visitList) {
        visitList.sort(new IdComparator());                                    // id升序排序
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        int trackedNum = 0;
        // 1. 遍历nodeList, 判断其是否被visitList所覆盖
        for(Node node : totalNodeList) {
            boolean covered = false;                                           // 该点是否被覆盖
            node.getCoveringTimeSet().clear();
            node.setTracked(false);
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
                        Set<Integer> coveringTimeSet = MonitorCover.getTimeSet(platform, node, startNode, totalTravelTime, endNode);
                        node.getCoveringTimeSet().addAll(coveringTimeSet);  // 将timeSet加入
                    }
                    totalTravelTime = totalTravelTime + platform.getStayTimePerNode() + Function.getDistance(startNode, endNode) / platform.getSpeed();;
                    // ***** 飞机专属 最后一个 ******
                    if(covered && platform.getType().equals("airplane") && i == visitedNodeList.size() - 2) {
                        Set<Integer> coveringTimeSet = MonitorCover.getTimeSet(platform, node, startNode, totalTravelTime, endNode);
                        node.getCoveringTimeSet().addAll(coveringTimeSet);  // 将timeSet加入
                    }
                    /* node loop ends*/
                }
                /* platform loop ends*/
            }
            if(isTracked(node)) {
                node.setTracked(true);
                trackedNum ++;
            }
            /* total node loop ends */
        }
        /* function ends */
        return trackedNum / totalNodeList.size();
    }



    // 判断是否被跟踪
    public static boolean isTracked(Node node) {
        int targetTime = (int) Double.parseDouble((node.getEvent()));
        Set<Integer> coveringTimeSet = node.getCoveringTimeSet();
        return coveringTimeSet.contains(targetTime);
    }


/* class ends */
}
