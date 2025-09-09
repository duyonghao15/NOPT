package org.nudtopt.realworldproblems.multiplatformrouting.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.Cover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.MonitorCover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.TrackCover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.exportor.Plot;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiPlatformPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());                              // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());                              // 画黑边框
        Scenario scenario = (Scenario) solution;
        int width = Math.min(getHeight(), getWidth());

        // 1. 根据实际长宽, 确定比例系数ratio
        List<Node> totalNodeList = scenario.getNodeList();
        List<Visit> visitList = scenario.getVisitList();
        Node lastNode = totalNodeList.get(totalNodeList.size() - 1);
        double trueWidth  = lastNode.getX() + 0.5 * lastNode.getWidth();
        double trueHeight = lastNode.getY() + 0.5 * lastNode.getHeight();
        double ratio = width / trueWidth;
        int height = (int) (trueHeight * ratio);

        // 2. 为网格点着色
        Cover.getCoveredNodeList(totalNodeList, visitList, true);             // 记录node被哪些platform覆盖
        List<Node> eventNodeList = new ArrayList<>();                               // 判断事件
        String event = null;
        for(Node node : totalNodeList) {
            if(node.getEvent() != null) {
                eventNodeList.add(node);
                event = node.getEvent();
            }
        }

        String platformType = getName();        // 共有4种: airplane, airship, satellite, all
        // 监视任务: 画热力图
        MonitorCover.getMonitorCoverRatio(eventNodeList, visitList, 120);
        for(Node node : eventNodeList) {
            Color color = new Color(0, 0, 0, 4);
            for(int i = 0 ; i < node.getCoveringTimeSet().size() ; i ++) {
                Plot.drawNode(g, node, color, ratio);
            }
        }

        // 跟踪任务: 只画被跟踪的node
        TrackCover.getTrackCoverRatio(eventNodeList, visitList);
        for(Node node : eventNodeList) {
            Color color = new Color(0, 0, 0, 50);
            if(node.isTracked())
                Plot.drawNode(g, node, color, ratio);
        }

        // 3. 为网格点着色
        for(Node node : totalNodeList) {
            Color color = new Color(255, 255, 255);
            // a. 画event
            /*if(!platformType.equals("all") && node.getEvent() != null) {
                drawNode(g, node, color, ratio);
            }*/

            // b. 遍历覆盖该node的platformList
            for(Platform platform : node.getCoveringPlatformList()) {
                if(platformType.equals("all")) {                          // a. 画all platformType
                    color = new Color(0, 0, 0, 12);
                } else if(!platform.getType().equals(platformType)) {     // b. 画platformType, 但platform不符合
                    continue;
                } else {                                                  // c. 画platformType
                    switch (platform.getType()) {
                        case "airplane":
                            color = new Color(0, 0, 0, 50);
                            break;
                        case "airship":
                            color = new Color(0, 0, 0, 30);
                            break;
                        case "satellite":
                            color = new Color(0, 0, 0, 12);
                            break;
                    }
                }
                if(platformType.equals("all") && event != null && node.getEvent() == null)    continue;  // 是否只画event的node
                Plot.drawNode(g, node, color, ratio);
            }
            /* node loop ends*/
        }

        // 4. 画出visit轨迹
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        for(Platform platform : platformNodeMap.keySet()) {
            if(platform.getType().equals(platformType)) {
                Color color = new Color(0, 0, 0);
                BasicStroke stroke = new BasicStroke(2F, 2, 2, 1, new float[]{15, 20}, 0);
                g.setColor(color);
                Plot.drawNode(g, platformNodeMap.get(platform), color, ratio, platform);
                Plot.drawLine(g, platformNodeMap.get(platform), color, stroke, ratio, platform);
            }
        }
        /* function ends */
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width, height);
    }


/* function ends */
}
