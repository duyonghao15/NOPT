package org.nudtopt.classicproblems.graphcoloringproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Color;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Graph;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Reader extends MainLogger {


    public static void main(String[] args) throws Exception {
        String problemName = "gcol1";
        new Reader().read(problemName + ".txt");
    }


    /**
     * 创建图着色问题数据场景
     * @param path 数据集名
     * @return     场景对象
     */
    public Graph read(String path) throws Exception {
        logger.info("正载入 -> 图着色问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("graphcoloringproblem/" + path), "GBK")); // 相对路径
        String firstLine = br.readLine();
        int nodeNum = Integer.valueOf(firstLine.split("\\s+")[2]);       // 节点数
        int edgeNum = Integer.valueOf(firstLine.split("\\s+")[3]);       // 边数

        // 1. 创建颜色
        long colorNum = nodeNum <= 100 ? 16 : 35;
        List<Color> colorList = new ArrayList<>();
        for(long i = 0 ; i < colorNum ; i ++) {
            Color color = new Color();
            color.setId(i);
            colorList.add(color);
        }

        // 2. 读取和创新节点
        String line;
        List<Node> nodeList = new ArrayList<>();                                // node清单
        Map<Long, Node> nodeIdMap = new HashMap<>();                            // 存储nodeId的map, 便于匹配查找
        while ((line = br.readLine()) != null) {
            // a. 读取边, 获得相邻两个节点的id
            String[] segments = line.split("\\s+");
            long nodeId_1 = Long.valueOf(segments[1]);
            long nodeId_2 = Long.valueOf(segments[2]);
            // b. 若暂无相关节点, 则创建
            if(!nodeIdMap.containsKey(nodeId_1)) {
                Node node = new Node();
                node.setId(nodeId_1);
                nodeIdMap.put(node.getId(), node);
                nodeList.add(node);
            }
            if(!nodeIdMap.containsKey(nodeId_2)) {
                Node node = new Node();
                node.setId(nodeId_2);
                nodeIdMap.put(node.getId(), node);
                nodeList.add(node);
            }
            // c. 获得相邻两个节点对象, 互相加入相邻节点
            Node node_1 = nodeIdMap.get(nodeId_1);
            Node node_2 = nodeIdMap.get(nodeId_2);
            node_1.getAdjacentNodeList().add(node_2);
            node_2.getAdjacentNodeList().add(node_1);
        }

        // 3. 节点按id升序排列, 并为决策变量赋值
        nodeList.sort(new IdComparator());
        for(Node node : nodeList) {
            node.getAdjacentNodeList().sort(new IdComparator());
            node.setOptionalColorList(colorList);                               // 决策变量取值范围赋值
            node.setColor(Tool.randomFromList(node.getOptionalColorList()));    // 决策变量随机赋初值
        }

        /* function ends */
        Graph graph = new Graph();
        graph.setId(0L);
        graph.setNodeList(nodeList);
        logger.info("已载入 -> 图着色问题数据集 (" + path + ": 节点数: " + nodeNum + ", 边数: " + edgeNum + ", 最大颜色数: " + colorNum + ").\n");
        return graph;
    }


/* class ends */
}
