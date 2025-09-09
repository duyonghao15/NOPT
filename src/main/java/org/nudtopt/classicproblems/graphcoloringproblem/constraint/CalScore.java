package org.nudtopt.classicproblems.graphcoloringproblem.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Graph;
import org.nudtopt.classicproblems.graphcoloringproblem.model.Node;

import java.util.ArrayList;
import java.util.List;

public class CalScore extends Constraint {

    @Override
    public Score calScore() {
        // 计算冲突
        Graph graph = (Graph) solution;
        List<Node> nodeList = graph.getNodeList();
        int conflicts = 0;
        for(Node node : nodeList) {
            conflicts += node.getConflict();
        }
        // 设置评分
        Score score = new Score();
        score.setHardScore(- conflicts);
        return score;
    }

}
