package org.nudtopt.realworldproblems.networkdisintegration.tool;

import org.nudtopt.realworldproblems.networkdisintegration.model.Link;
import org.nudtopt.realworldproblems.networkdisintegration.model.Network;
import org.nudtopt.realworldproblems.networkdisintegration.model.Node;


public class CalConnectivity {

    /**
     * 基于网络结构, 计算网格的连通片数量和最大连通片权值
     * @param network  网格结构
     * @return 0: 连通片数量 (越大越好), 1: 最大连通片权值 (越小越好), 2: 瓦解的节点数量
     */
    public static double[] countConnectedComponentsWithWeight(Network network) {
        boolean[] visited = new boolean[network.getNodeList().size()];
        int componentNum = 0;
        double maxWeight = 0;
        int disintegratedNum = 0;
        // 评估网络结果
        for(Node node : network.getNodeList()) {
            if(!visited[node.getId().intValue() - 1]) {                 // nodeId从1开始
                double[] component = dfs(node, visited);
                maxWeight = Math.max(maxWeight, component[1]);          // 更新最大连通分量的规模 (0为节点数量, 1为边的权值总和)
                componentNum ++;                                        // 每次 DFS 完成，说明找到了一个新的连通分量
            }
            if(node.getDisintegrate()) {
                disintegratedNum ++;
            }
        }
        return new double[]{componentNum, maxWeight, disintegratedNum};
    }


    /**
     * 深度优先搜索--递归
     * @param node            待计算节点
     * @param visited         已访问节点
     * @return                0: 连通片节点数量, 1: 连通片边权重和
     */
    public static double[] dfs(Node node, boolean[] visited) {
        visited[node.getId().intValue() - 1] = true;                    // 标记当前节点为已访问
        int size = 1;                                                   // 当前连通分量的大小 (包括当前节点)
        double weight = 0;                                              // 当前连通分量的边的权值总和
        for(Link link : node.getLinkList()) {                           // 遍历从本节点向外连接的边
            double edgeWeight = link.getWeight();                       // 获取边的权重
            Node endNode = link.getEndNode();                           // 终点节点
            if(link.getDisintegrate() || node.getDisintegrate() || endNode.getDisintegrate()) {
                edgeWeight = 0;                                         // 若边、节点被瓦解, 则不能联通, weight记为0
            }
            if(edgeWeight != 0 && !visited[endNode.getId().intValue() - 1]) {
                double[] result = dfs(endNode, visited);
                weight += edgeWeight;                                   // 累加边的权重
                size   += result[0];                                    // 累加连通分量的节点数
                weight += result[1];                                    // 累加边的权重
            }
        }
        return new double[] {size, weight};                             // 返回当前连通分量的大小和权重
    }


}
