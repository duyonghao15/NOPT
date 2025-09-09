package org.nudtopt.realworldproblems.multiplatformrouting.tool.exportor;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.Cover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.MonitorCover;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.cover.TrackCover;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Plot {

    // 0. 画总图
    public static void plotNodeList(Scenario scenario, String platformType, String path) throws Exception {

        // 0. // 图的宽度始终为1000
        int width = 1000;

        // 1. 根据实际长宽, 确定比例系数ratio
        List<Node> totalNodeList = scenario.getNodeList();
        List<Visit> visitList = scenario.getVisitList();
        Node lastNode = totalNodeList.get(totalNodeList.size() - 1);
        double trueWidth = lastNode.getX() + 0.5 * lastNode.getWidth();
        double trueHeight = lastNode.getY() + 0.5 * lastNode.getHeight();
        double ratio = width / trueWidth;
        int height = (int) (trueHeight * ratio);

        // 2. 绘制轮廓和底色
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(new Color(255, 255, 255));  // 着白底色
        graphics.fillRect(0, 0, width, height);

        // 3. 为网格点着色
        Cover.getCoveredNodeList(totalNodeList, visitList, true);   // 记录node被哪些platform覆盖
        List<Node> eventNodeList = new ArrayList<>();                     // 判断事件
        String event = null;
        for(Node node : totalNodeList) {
            if(node.getEvent() != null) {
                eventNodeList.add(node);
                event = node.getEvent();
            }
        }


        // 监视任务: 画热力图
        /*MonitorCover.getMonitorCoverScore(eventNodeList, visitList, 120);
        for(Node node : eventNodeList) {
            Color color = newversion Color(0, 0, 0, 4);
            for(int i = 0 ; i < node.getCoveringTimeSet().size() ; i ++) {
                drawNode(graphics, node, color, ratio);
            }
        }*/

        // 跟踪任务: 只画被跟踪的node
/*        TrackCover.getTrackCoverScore(eventNodeList, visitList);
        for(Node node : eventNodeList) {
            Color color = newversion Color(0, 0, 0, 50);
            if(node.isTracked())
            drawNode(graphics, node, color, ratio);
        }*/




        // 4. 为网格点着色
        for(Node node : totalNodeList) {
            Color color = new Color(255, 255, 255);
            // a. 画event
/*            if(!platformType.equals("all") && node.getEvent() != null) {
                drawNode(graphics, node, color, ratio);
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
                drawNode(graphics, node, color, ratio);
            }
            /* node loop ends*/
        }

        // 4. 画出visit轨迹
        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);
        for(Platform platform : platformNodeMap.keySet()) {
            if(platform.getType().equals(platformType)) {
                Color color = new Color(0, 0, 0);
                BasicStroke stroke = new BasicStroke(2F, 2, 2, 1, new float[]{15, 20}, 0);
                graphics.setColor(color);
                drawNode(graphics, platformNodeMap.get(platform), color, ratio, platform);
                drawLine(graphics, platformNodeMap.get(platform), color, stroke, ratio, platform);
            }
        }
        /* function ends */
        graphics.setStroke(new BasicStroke(10));         // 使用10粗线, 画黑边框
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, width, height);
        ImageIO.write(image, "png", new File(path + "/" + platformType + " .png"));
    }



    // 1. 为node着色
    public static void drawNode(Graphics graphics, Node node, Color color, double ratio) {
        double x = node.getX() - node.getWidth()  / 2;
        double y = node.getY() - node.getHeight() / 2;
        // graphics.setStroke(new BasicStroke());                // 默认线宽
        graphics.setColor(color);                             // a. 着色
        graphics.fillRect((int) (x*ratio), (int) (y*ratio), (int) (node.getWidth()*ratio), (int) (node.getHeight()*ratio));
        graphics.setColor(new Color(0, 0, 0));        // b. 画边框
        graphics.drawRect((int) (x*ratio), (int) (y*ratio), (int) (node.getWidth()*ratio), (int) (node.getHeight()*ratio));
    }

    // 2, 为nodeList着色
    public static void drawNode(Graphics graphics, List<Node> nodeList, Color color, double ratio, Platform platform) {
        for(Node node : nodeList) {
            drawNode(graphics, node, color, ratio);
        }
    }

    // 3. 为nodeList画线
    public static void drawLine(Graphics graphics, List<Node> nodeList, Color color, BasicStroke stroke, double ratio, Platform platform) {
        // graphics.setStroke(stroke);
        for(int i = 0 ; i < nodeList.size() - 1 ; i ++) {
            Node startNode = nodeList.get(i);
            Node endNode = nodeList.get(i + 1);
            graphics.drawLine((int) (startNode.getX()*ratio), (int) (startNode.getY()*ratio), (int) (endNode.getX()*ratio), (int) (endNode.getY()*ratio));
        }
    }



    // 4. 根据限定时间，把时间点后面的visit的node全部赋为null
    public static void cutTime(Scenario scenario) {
        List<Platform> platformList = scenario.getPlatformList();
        List<Visit> visitList = scenario.getVisitList();

        Map<Platform, List<Visit>> platformVisitMap = Function.getPlatformVisitMap(visitList);
        for(Platform platform : platformList) {
            List<Visit> allVisitList = platformVisitMap.get(platform);
            List<Node> nodeList = new ArrayList<>();
            int i;
            for(i = 0 ; i < allVisitList.size() ; i ++) {
                Visit visit = allVisitList.get(i);
                if(visit.getNode() != null)     nodeList.add(visit.getNode());
                double travelTime = platform.getTravelTime(nodeList, platform.getSetupTime());
                if(travelTime > 58) {
                    break;
                }
            }
            for(int j = i ; j < allVisitList.size() ; j ++) {
                Visit visit = allVisitList.get(j);
                visit.setNode(null);
            }
        }
        /* function ends */
    }




/* class ends */
}
