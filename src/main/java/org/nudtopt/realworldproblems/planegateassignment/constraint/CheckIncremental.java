package org.nudtopt.realworldproblems.planegateassignment.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.realworldproblems.planegateassignment.model.Plane;
import org.nudtopt.realworldproblems.planegateassignment.model.Position;

import java.util.*;

public class CheckIncremental extends Check {

    private Set<Plane> assignedPlaneSet = new HashSet<>();

    @Override
    public Score calScore() {
        if(operator == null)            return super.calScore();                                        // 算子为null, 调用父类非增量式计算
        List<DecisionEntity> changedEntityList = operator.getDecisionEntityList();                      // 本次算子, 改动决策变量的全部实体(飞机)集合
        boolean conflict = false;
        for(int i = 0 ; i < changedEntityList.size() ; i ++) {                                          // 遍历这些被改动的飞机 (因为收益/约束只会受他们影响)
            Plane plane = (Plane) changedEntityList.get(i);                                             // 被改动决策变量的飞机
            Position position = plane.getPosition();                                                    // 当前停机位(决策变量值)
            // 更新已分配机位的飞机集合
            if(position == null) {
                assignedPlaneSet.remove(plane);                                                         // 取消分配 (肯定不会产生新冲突)
            } else {
                assignedPlaneSet.add(plane);                                                            // 新分配 (有可能产生新冲突)
                for(Plane possibleConflictPlane : plane.getPossibleConflictPlaneList()) {               // 遍历潜在冲突飞机, 判断是否产生冲突
                    if(plane.conflict(possibleConflictPlane)) {                                         // 若冲突
                        conflict = true;                                                                // 则记录冲突
                        break;                                                                          // 跳出
                    }
                }
            }
        }
        /* function ends */
        Score score = new Score();
        score.setHardScore(conflict ? -1 : 0);                                                          // 约束: 是否冲突
        score.setMeanScore(assignedPlaneSet.size() - solution.getDecisionEntityList().size());          // 收益: 未安排的飞机数
        return score;
    }


    @Override
    public boolean isIncremental() {
        return true;
    }


/* class ends */
}
