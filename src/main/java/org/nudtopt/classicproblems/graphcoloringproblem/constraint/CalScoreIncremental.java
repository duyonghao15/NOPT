package org.nudtopt.classicproblems.graphcoloringproblem.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Graph;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Node;

import java.util.*;
import java.util.stream.Collectors;

public class CalScoreIncremental extends CalScore {

    private int conflicts;
    private Map<Node, Integer> nodeConflictMap = new HashMap<>();

    @Override
    public Score calScore() {
        Graph graph = (Graph) solution;
        List<Node> nodeList = graph.getNodeList();
        // 1. 初始化计算
        if(operator == null) {
            for(Node node : nodeList) {
                int conflict = node.getConflict();
                this.conflicts += conflict;
                this.nodeConflictMap.put(node, conflict);
            }
            Score score = new Score();
            score.setHardScore(- this.conflicts);
            return score;
        }

        // 2. 根据本次operator, 获得受影响的决策实体node
        List<Node> affectedNodeList = new ArrayList<>();
        for(DecisionEntity changedEntity : operator.getDecisionEntityList()) {
            Node changedNode = (Node) changedEntity;
            affectedNodeList.add(changedNode);
            affectedNodeList.addAll(changedNode.getAdjacentNodeList());
        }
        affectedNodeList = affectedNodeList.stream().distinct().collect(Collectors.toList());   // 去重

        // 3. 重新计算受到影响的node的冲突值
        for(Node affectedNode : affectedNodeList) {
            int oldConflict = this.nodeConflictMap.get(affectedNode);                           // 获得旧冲突
            int newConflict = affectedNode.getConflict();                                       // 计算新冲突
            this.conflicts = this.conflicts - oldConflict + newConflict;                        // 总冲突 = 原总冲突 - 旧 + 新
            this.nodeConflictMap.put(affectedNode, newConflict);                                // 更新冲突值
        }

        // 4. 设置评分
        Score score = new Score();
        score.setHardScore(- this.conflicts);
        return score;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }

}
