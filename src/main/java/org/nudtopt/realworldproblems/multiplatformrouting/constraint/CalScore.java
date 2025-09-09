package org.nudtopt.realworldproblems.multiplatformrouting.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.Cover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.MonitorCover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.TrackCover;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalScore extends Constraint {


    @Override
    public Score calScore() {
        Scenario scenario = (Scenario) solution;
        List<Node> nodeList = scenario.getNodeList();
        List<Visit> visitList = scenario.getVisitList();
        Map<Platform, List<Visit>> platformVisitsMap = visitList.stream()                       // 获取每个平台访问的点的顺序
                .filter(visit -> visit.getNode() != null)
                .collect(Collectors.groupingBy(Visit::getPlatform));
        int constraint = 0;

        // 1. 约束计算
        double maxTravelTime = 0;
        for(Platform platform : platformVisitsMap.keySet()) {
            List<Visit> visits = platformVisitsMap.get(platform);
            double travelTime = platform.getTravelTime(visits);                                 // 计算该平台的时间
            maxTravelTime = Math.max(maxTravelTime, travelTime);
            if(platform.getType().equals("airship")) {
                for(int i = 0 ; i < visits.size() - 1 ; i ++) {
                    Node node_1 = visits.get(i).getNode();
                    Node node_2 = visits.get(i + 1).getNode();
                    if(platform.getTravelTime(node_1, node_2) < 10) {
                        constraint --;                                                          // 1# 约束: 舰船连续航行至少10分钟
                    }
                }
            }
            if(platform.getType().equals("satellite")) {
                for(int i = 0 ; i < visits.size() - 1 ; i += 2) {
                    Node node_1 = visits.get(i).getNode();
                    Node node_2 = visits.get(i + 1).getNode();
                    if(Math.abs(Function.getSlop(node_1, node_2) - 1) > 0.05) {
                        constraint --;                                                          // 2# 约束: 卫星推扫斜率为1
                    }
                }
            }
        }
        if(maxTravelTime > 120) {
            constraint -= (int) Math.ceil(maxTravelTime) - 120;                                 // 3# 约束: 总时间不超过2小时
        }


        // 2. 收益计算
        // a. 覆盖率
        List<Node> coveredNodeList = Cover.getCoveredNodeList(nodeList, visitList, false);
        double coverRatio = 1.0 * coveredNodeList.size() / nodeList.size();
        // b. 监视率
        // double monitorRatio = MonitorCover.getMonitorCoverRatio(nodeList, visitList, 120);
        // c. 跟踪率
        // double TrackRatio = TrackCover.getTrackCoverRatio(nodeList, visitList);

        /* function ends */
        Score score = new Score();
        score.setHardScore(constraint);
        score.setMeanScore(Math.round(coverRatio * 100000));
        return score;
    }


}
