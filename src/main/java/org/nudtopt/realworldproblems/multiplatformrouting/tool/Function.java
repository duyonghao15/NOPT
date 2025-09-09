package org.nudtopt.realworldproblems.multiplatformrouting.tool;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class Function {


    // 获取node之间的距离
    public static double getDistance(Node nodeA, Node nodeB) {
        double x_A = nodeA.getX();
        double y_A = nodeA.getY();
        double x_B = nodeB.getX();
        double y_B = nodeB.getY();
        double distance = Math.pow((x_A - x_B), 2) + Math.pow((y_A - y_B), 2);
        return Math.pow(distance, 0.5);
    }


    // 获取node之间的斜率
    public static double getSlop(Node nodeA, Node nodeB) {
        double x_A = nodeA.getX();
        double y_A = nodeA.getY();
        double x_B = nodeB.getX();
        double y_B = nodeB.getY();
        return (y_B - y_A) / (x_B - x_A);
    }


    // 获取每一个平台依次访问的node列表
    public static Map<Platform, List<Node>> getPlatformNodeMap(List<Visit> visitList) {
        Map<Platform, List<Node>> map = new HashMap<>();
        for(Visit visit : visitList) {
            if(visit.getNode() == null) {                 // node为null, 跳过
                continue;
            }
            Platform platform = visit.getPlatform();
            if(map.get(platform) == null) {               // map中没有platform, 存入
                map.put(platform, new ArrayList<>());
            }
            map.get(platform).add(visit.getNode());       // platform对应的node累加
        }
        return map;
    }


    // 获取每一个平台对应的visit列表
    public static Map<Platform, List<Visit>> getPlatformVisitMap(List<Visit> visitList) {
        Map<Platform, List<Visit>> map = new HashMap<>();
        for(Visit visit : visitList) {
            Platform platform = visit.getPlatform();
            if(map.get(platform) == null) {               // map中没有platform, 存入
                map.put(platform, new ArrayList<>());
            }
            map.get(platform).add(visit);                 // platform对应的visit累加
        }
        return map;
    }


    // 计算在startNode -> endNode所覆盖的两条线
    public static List<Double> getTwoLines(Node startNode, Node endNode, double radius) {
        double x1 = startNode.getX();
        double y1 = startNode.getY();
        double x2 = endNode.getX();
        double y2 = endNode.getY();
        // 1. 左右边界: y = a * x + b ± c
        double a = (y2 - y1) / (x2 - x1) ;
        if(a == Infinity) {                          // 乘大数法
            a = 100000.0;
        }
        if(a == -Infinity) {
            a = -100000.0;
        }
        double b = y1 - a * x1;
        double c = Math.pow((a*a + 1), 0.5) * radius;
        // 2. 首尾边界: y = d * x + e / f
        double d = -1 / a;
        if(d == Infinity) {                          // 乘大数法
            d = 100000.0;
        }
        if(d == -Infinity) {
            d = -100000.0;
        }
        double e = y1 - x1 * d;
        double f = y2 - x2 * d;
        List<Double> twoLines = new ArrayList<Double>();
        twoLines.add(a);    // 乘大数法
        twoLines.add(b);
        twoLines.add(c);
        twoLines.add(d);
        twoLines.add(Math.min(e, f));                // e: 下面那根线
        twoLines.add(Math.max(e, f));                // f: 上面那根线
        /* function ends */
        return twoLines;
    }


/* class ends */
}
