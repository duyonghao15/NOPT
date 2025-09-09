package org.nudtopt.realworldproblems.networkdisintegration.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.apidata.Data;
import org.nudtopt.realworldproblems.networkdisintegration.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Reader extends MainLogger {

    public static void main(String[] args) throws Exception {
        String file = "inf-openflights.edges";
        Network network = new Reader().read(file);
        double[] results = CalConnectivity.countConnectedComponentsWithWeight(network);
    }


    /**
     * 读取复杂网络节点与边的数据
     * @param file 数据文件名
     * @return     解(节点和边)
     * @author     杜永浩
     */
    public Network read(String file) throws Exception {
        logger.info("正读取 -> 网络节点与边的数据 (" + file + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("networkdisintegration/" + file), "GBK")); // 相对路径
        List<Link> linkList = new ArrayList<>();
        Map<Long, Node> nodeMap = new HashMap<>();
        String line = br.readLine();
        br.readLine();
        long meanLinkNum = 0;
        List<Boolean> optionalDisintegrateList = Arrays.asList(false, true);
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+");
            long startId = Long.valueOf(str[0]);
            long endId = Long.valueOf(str[1]);
            double weight = (str.length > 2) ? Double.valueOf(str[2]) : 1; // 默认权重为1
            // 1. 读取/创建节点
            Node startNode = nodeMap.get(startId);
            Node endNode = nodeMap.get(endId);
            if(startNode == null) {
                startNode = new Node();
                startNode.setId(startId);
                startNode.setOptionalDisintegrateList(optionalDisintegrateList);
                nodeMap.put(startId, startNode);
            }
            if(endNode == null) {
                endNode = new Node();
                endNode.setId(endId);
                endNode.setOptionalDisintegrateList(optionalDisintegrateList);
                nodeMap.put(endId, endNode);
            }
            // 2. 创建边
            Link link = new Link();
            link.setId((long) linkList.size());
            link.setStartNode(startNode);
            link.setEndNode(endNode);
            link.setWeight(weight);
            link.setOptionalDisintegrateList(optionalDisintegrateList);
            linkList.add(link);
            startNode.getLinkList().add(link);          // 记录: 从起点向外发散的边
            meanLinkNum ++;
        }
        logger.info("已读取 -> 节点 " + nodeMap.size() + " 个, 边 " + linkList.size() + " 条 (平均每节点外连 " + meanLinkNum /nodeMap.size() + " 条边).\n");
        Network network = new Network();
        network.setNodeMap(nodeMap);
        network.setNodeList(new ArrayList<>(nodeMap.values()));
        network.getNodeList().sort(Comparator.comparing(Node::getId));
        network.setLinkList(linkList);
        network.setName(file);
        return network;
    }


/* class ends */
}