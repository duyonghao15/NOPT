package org.nudtopt.realworldproblems.multiplatformrouting.tool.reader;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Reader extends MainLogger {

    public static void main(String[] args) {
        new Reader().createScenario("cloud");
    }


    // 0. 创建场景
    public Scenario createScenario(String event) {
        Scenario scenario = new Scenario();
        List<Node> nodeList = createNodeList(50, 50, 1);
        List<Platform> platformList = createPlatformList(3, 2, 1);
        if(event != null) {
            if(event.equals("circle") || event.equals("fire") || event.equals("cloud"))                     markEvent(nodeList, event);
            if(event.equals("straight-line") || event.equals("poly-line") || event.equals("semi-circle"))   markMoving(nodeList, event);
        }
        List<Visit> visitList = createVisitList(nodeList, platformList, event != null);   // 是否考虑event, 若考虑, 则node值域受限

        scenario.setNodeList(nodeList);
        scenario.setPlatformList(platformList);
        scenario.setVisitList(visitList);
        return scenario;
    }


    // 1. 创建网格列表
    public List<Node> createNodeList(int width, int height, int min) {
        List<Node> nodeList = new ArrayList<>();
        for(int i = 0 ; i < width * height ; i ++) {
            Node node = new Node();
            node.setId((long) i);                            // 网格编号
            node.setWidth(min);                              // 网格边长
            node.setHeight(min);
            double x = node.calculateX(i, width, height);    // x坐标 (km)
            double y = node.calculateY(i, height);           // y坐标 (km)
            node.setX(x);
            node.setY(y);
            if(x - 0.5 * min == 0 || y - 0.5 * min == 0 || x + 0.5 * min == width || y + 0.5 * min == height) {
                node.setBoundary(true);
            }
            node.setCoveringPlatformList(new ArrayList<>());
            node.setCoveringTimeSet(new HashSet<>());
            nodeList.add(node);
        }
        logger.info("已网格化 " + width + " * " + height + " km^2 目标区域: 网格数 " + width*height + ", 精度 " + min + " km.");
        return nodeList;
    }


    // 2. 创建平台列表
    public List<Platform> createPlatformList(int airplaneNum, int airshipNum, int satelliteNum) {
        List<Platform> platformList = new ArrayList<>();
        // 1. airplane
        for(int i = 0 ; i < airplaneNum ; i ++) {
            Platform platform = new Platform();
            platform.setId((long) i);
            platform.setType("airplane");
            platform.setRadius(4.5);              // 4.5 km
            platform.setSpeed(200 / 60.0);        // 200 km/h
            platform.setSetupTime(5);             // 5 min
            platform.setStayTimePerNode(15);      // 15 min
            platformList.add(platform);
        }
        // 2. airship
        for(int i = 0 ; i < airshipNum ; i ++) {
            Platform platform = new Platform();
            platform.setId((long) i + airplaneNum);
            platform.setType("airship");
            platform.setRadius(3);                // 3 km
            platform.setSpeed(30 / 60.0);         // 30 km/h
            platform.setSetupTime(20);            // 20 min
            platform.setStayTimePerNode(0);
            platformList.add(platform);
        }
        // 3. satellite
        for(int i = 0 ; i < satelliteNum ; i ++) {
            Platform platform = new Platform();
            platform.setId((long) i + airplaneNum + airshipNum);
            platform.setType("satellite");
            platform.setRadius(6);                // 6 km
            platform.setSpeed(5000 / 60.0);       // 5000 km/h
            platform.setSetupTime(30);            // 30 min
            platform.setStayTimePerNode(0);
            platformList.add(platform);
        }
        logger.info("已实例化 " + airplaneNum + " 架无人机, " + airshipNum + " 艘飞艇, " + satelliteNum + " 颗卫星. ");
        return platformList;
    }


    // 3. 创建visitList
    public List<Visit> createVisitList(List<Node> nodeList, List<Platform> platformList, boolean event) {
        List<Visit> visitList = new ArrayList<>();
        for(Platform platform : platformList) {
            int visitNum = 0;
            switch (platform.getType()) {
                case "airplane":
                    visitNum = 8;
                    break;
                case "airship":
                    visitNum = 8;
                    break;
                case "satellite":
                    visitNum = 2;
                    break;
            }

            for(int i = 0 ; i < visitNum ; i ++) {
                Visit visit = new Visit();
                visit.setId((long) visitList.size());
                visit.setPlatform(platform);
                createOptionalNodeList(visit, nodeList, i == 0/* || i == visitNum - 1*/, event);  // 第0个和最后一个需进出边界
                visitList.add(visit);
            }
        }
        return visitList;
    }


    // 4. 创建visit的optionalNodeList
    public void createOptionalNodeList(Visit visit, List<Node> nodeList, boolean boundary, boolean event) {
        List<Node> boundaryNodeList = new ArrayList<>();
        List<Node> optionalNodeList = new ArrayList<>();
        List<Node> satelliteOptionalNodeList = new ArrayList<>();
        // 1. 值域筛选
        for(Node node : nodeList) {
            if(node.isBoundary()) {                                                        // a. 边界node
                boundaryNodeList.add(node);
            }
            if(!event || node.getEvent() != null) {                                        // b. 非边界node
                optionalNodeList.add(node);
                if(node.getY() >= node.getX() - 10 && node.getY() <= node.getX() + 10) {   // 卫星情况
                    satelliteOptionalNodeList.add(node);
                }
            }
        }
        // 2. 值域赋值
        if(visit.getPlatform().getType().equals("satellite")) {                            // a. 卫星情况
            visit.setOptionalNodeList(satelliteOptionalNodeList);
            if(visit.getId() % 2 == 0)  visit.setNode(nodeList.get(0));
            else                        visit.setNode(nodeList.get(nodeList.size() - 1));
        } else if (boundary) {                                                             // b. 边界node
            visit.setOptionalNodeList(boundaryNodeList);
            visit.setNode(Tool.randomFromList(boundaryNodeList));                   // 赋初值, 不能为null
        } else {                                                                           // c. 非边界node
            visit.setOptionalNodeList(optionalNodeList);
            for(int i = 0 ; i < optionalNodeList.size() * 0.3 ; i ++) {
                visit.getOptionalNodeList().add(null);                                     // 且30%概率可null
            }
        }
        /* function ends */
    }


    // 5.1 为一些node随机赋上event属性
    public double[][] randomMarkEvent(List<Node> nodeList, String event, int num, double meanRadius) {
        // a. 目标区域长款
        Node lastNode = nodeList.get(nodeList.size() - 1);
        double width = lastNode.getX() + 0.5 * lastNode.getWidth();
        double height = lastNode.getY() + 0.5 * lastNode.getHeight();
        // b. 云层坐标与半径
        double[][] eventXYR = new double[num][3];
        for(int i = 0 ; i < num ; i ++) {
            double x = Math.random() * width;                                // x
            double y = Math.random() * height;                               // y
            double radius = (Math.random() + 0.5) * meanRadius;              // radius
            for(Node node : nodeList) {
                if(node.getEvent() != null) continue;
                boolean cover = Math.pow((node.getX() - x), 2) + Math.pow((node.getY() - y), 2) <= radius * radius;
                if(cover)   node.setEvent(event);
            }
            eventXYR[i] = new double[]{x, y, radius};
            /* cloud loop ends */
        }
        /* function ends */
        return eventXYR;
    }


    // 5.2 为一些node指定赋上event属性
    public void markEvent(List<Node> nodeList, String event) {
        double[][] XYR = new double[8][3];
        switch (event) {
            case "fire":
                XYR = new double[][]{{0.5, 29.7, 8.0}, {16.0, 15.3, 4.3}, {20.8, 37.4, 6.7}, {24.3, 6.8, 6.9}, {32.0, 37.1, 5.6}, {22.7, 12.7, 7.4}, {40.8, 1.7, 7.5}, {21.2, 47.0, 6.2}};
                break;
            case "cloud":
                XYR = new double[][]{{40.4, 40.1, 10.5}, {39.1, 44.2, 9.2}, {24.9, 10.6, 9.3}, {43.1, 43.4, 7.4}, {37.6, 26.0, 9.8}, {18.8, 7.1, 10.2}, {30.6, 25.2, 9.6}, {15.9, 18.8, 11.8}};
                break;
            case "circle":
                XYR = new double[][]{{10, 16.6, 5}, {20, 16.6, 5}, {30, 16.6, 5}, {40, 16.6, 5}, {10, 33.3, 5}, {20, 33.3, 5}, {30, 33.3, 5}, {40, 33.3, 5}};
        }
        for(double[] xyr : XYR) {
            double x = xyr[0];
            double y = xyr[1];
            double radius = xyr[2];
            for (Node node : nodeList) {
                if (node.getEvent() != null) continue;
                boolean cover = Math.pow((node.getX() - x), 2) + Math.pow((node.getY() - y), 2) <= radius * radius;
                if (cover) node.setEvent(event);
            }
        }
    }


    // 5.3 为移动目标设置到达时间
    public void markMoving(List<Node> nodeList, String event) {
        Node lastNode = nodeList.get(nodeList.size() - 1);
        double width = lastNode.getX() + 0.5 * lastNode.getWidth();
        double radius = width;
        double speed = 60 / 60.0;   // 移动目标线速度60km/h

        for(Node node : nodeList) {
            double x = node.getX();
            double y = node.getY();
            switch (event) {
                case "semi-circle":        // a. 半圆轨迹
                    if(Math.pow((x - radius), 2) + Math.pow((y - radius), 2) >= Math.pow(0.9 * radius, 2) &&
                       Math.pow((x - radius), 2) + Math.pow((y - radius), 2) <= Math.pow(1.1 * radius, 2)) {
                        double angle = Math.atan((radius - y) / (radius - x));
                        double angelSpeed = speed / radius;
                        double time = angle / angelSpeed;
                        node.setEvent(String.valueOf(time));
                    }   break;
                case "poly-line":      // b. 折线轨迹
                    if(y >= -2 * x + width - 0.2 * width &&
                       y <= -2 * x + width + 0.2 * width && x <= 0.5 * width ||
                       y >=  2 * x - width - 0.2 * width &&
                       y <=  2 * x - width + 0.2 * width && x >= 0.5 * width) {
                        double time = x * Math.pow(5, 0.5) / speed;
                        node.setEvent(String.valueOf(time));
                    }   break;
                case "straight-line": // c. 直线轨迹
                    if(y >= 0.4 * width &&
                       y <= 0.6 * width) {
                        double time = x / speed;
                        node.setEvent(String.valueOf(time));
                    }   break;
            }
            /* loop ends */
        }
        /* function ends */
    }




/* class ends */
}
