package org.nudtopt.realworldproblems.planegateassignment.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.planegateassignment.model.Plane;
import org.nudtopt.realworldproblems.planegateassignment.model.Position;
import org.nudtopt.realworldproblems.planegateassignment.model.Solution;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Check extends Constraint {


    @Override
    public Score calScore() {
        // 1. 根据停机位(决策变量)分配情况, 进行分组
        List<Plane> totalPlaneList = ((Solution) solution).getPlaneList();
        List<Plane> assignedPlaneList = totalPlaneList.stream().filter(plane -> plane.getPosition() != null).collect(Collectors.toList());  // 已分配停机位的
        Map<Position, List<Plane>> positionPlanesMap = assignedPlaneList.stream()
                .sorted(Comparator.comparing(Plane::getInTime))                     // 按进场时间排序
                .collect(Collectors.groupingBy(Plane::getPosition));                // 按停机位分组
        // 2. 遍历停机位, 记录冲突数
        boolean conflict = false;
        for(Position position : positionPlanesMap.keySet()) {
            List<Plane> planeList = positionPlanesMap.get(position);
            for(int i = 0 ; i < planeList.size() - 1 ; i ++) {
                Plane plane_1 = planeList.get(i);
                Plane plane_2 = planeList.get(i + 1);
                if(plane_1.conflict(plane_2)) {
                    conflict = true;
                    break;
                }
            }
        }
        /* function ends */
        Score score = new Score();
        score.setHardScore(conflict ? -1 : 0);                                              // 约束: 是否冲突
        score.setMeanScore(assignedPlaneList.size() - totalPlaneList.size());               // 收益: 未安排的飞机数
        return score;
    }


/* class ends */
}
