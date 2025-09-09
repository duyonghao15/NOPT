package org.nudtopt.realworldproblems.networkdisintegration.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.realworldproblems.networkdisintegration.model.Link;
import org.nudtopt.realworldproblems.networkdisintegration.model.Network;
import org.nudtopt.realworldproblems.networkdisintegration.model.Node;
import org.nudtopt.realworldproblems.networkdisintegration.tool.CalConnectivity;

import java.util.HashSet;
import java.util.Set;

public class CalScore extends Constraint {


    /**
     * 基于网络结构, 计算网格的连通片数量和最大连通片权值
     */
    @Override
    public Score calScore() {
        // 1. 调用函数, 计算连通片数量和权重
        Network network = (Network) solution;
        double[] components = CalConnectivity.countConnectedComponentsWithWeight(network);  // 调用函数, 计算连通片数量和权重
        long componentNum = Math.round(components[0]);                                      // 最大连通片数量, 越大说明瓦解效果越好
        long maxWeight    = Math.round(components[1]);                                      // 最大连通片权值, 越小说明瓦解效果越好

        // 2. 统计被瓦解的边和节点
        long disintegratedNodeNum = Math.round(components[2]);
        long disintegratedLinkNum = network.getLinkList().stream().filter(Link::getDisintegrate).count();
        boolean constraint = false;                                                         // 是否超出最大瓦解量约束
        if(disintegratedNodeNum > network.getNodeList().size() * network.getMaxDisintegrationPercent() ||
           disintegratedLinkNum > network.getLinkList().size() * network.getMaxDisintegrationPercent()) {
            constraint = true;
        }

        // 3. 计分
        Score score = new Score();
        score.setHardScore(constraint ? -1 : 0);                                            // 约束, 违反记为-1
        score.setMeanScore(componentNum);                                                   // 收益, 最大连通片数量
        score.setSoftScore(-maxWeight);                                                     // 次收益, 最大连通片权值
        return score;
    }


/* class ends */
}
