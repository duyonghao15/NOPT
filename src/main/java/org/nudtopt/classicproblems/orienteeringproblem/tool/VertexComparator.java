package org.nudtopt.classicproblems.orienteeringproblem.tool;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.classicproblems.orienteeringproblem.model.TimeWindow;
import org.nudtopt.classicproblems.orienteeringproblem.model.Vertex;

public class VertexComparator extends PriorityComparator {


    @Override
    public int compare(DecisionEntity entity_1, DecisionEntity entity_2) {
        Vertex vertex_1 = (Vertex) entity_1;
        Vertex vertex_2 = (Vertex) entity_2;
        TimeWindow timeWindow_1 = vertex_1.getTimeWindow();
        TimeWindow timeWindow_2 = vertex_2.getTimeWindow();
        if(timeWindow_1 == null || timeWindow_2 == null) {
            return super.compare(entity_1, entity_2);
        } else {                                                        // 若有时间窗口
            long beginTime_1 = timeWindow_1.getBeginTime();
            long beginTime_2 = timeWindow_2.getBeginTime();
            long compare = beginTime_1 - beginTime_2;                   // 窗口开始时间升序
            if(compare == 0) {                                          // 若开始时间相同
                long endTime_1 = timeWindow_1.getEndTime();
                long endTime_2 = timeWindow_2.getEndTime();
                compare = endTime_1 - endTime_2;                        // 则按结束时间升序
            }
            if(compare == 0) {                                          // 若结束时间也相同
                return super.compare(entity_1, entity_2);               // 则用父类排序
            }
            return (int) compare;
        }
    }


}
