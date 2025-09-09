package org.nudtopt.classicproblems.orienteeringproblem.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.classicproblems.orienteeringproblem.model.Solution;
import org.nudtopt.classicproblems.orienteeringproblem.model.Tour;
import org.nudtopt.classicproblems.orienteeringproblem.model.Vertex;

import java.util.*;

public class Decoder extends Constraint {

    protected Map<String, Double> distanceMap = new HashMap<>();                            // 距离矩阵
    protected double score = 0;                                                             // 累计得分(除去服务时间)

    @Override
    public Score calScore() {
        Solution solution = (Solution) this.solution;
        Vertex startVertex = solution.getStartVertex();
        // 解码
        this.score = 0;
        for(Tour tour : solution.getTourList()) {
            List<Vertex> allocatedVertexList = tour.getVertexList();                        // tour所分配的顶点(自动更新)
            List<Vertex> visitedVertexList = new ArrayList<>();                             // 记录已访问的顶点
            visitedVertexList.add(startVertex);                                             // 首先加上起点
            double leaveTime = 0;                                                           // 离开时间/服务结束时间
            for(Vertex vertex : allocatedVertexList) {
                Vertex start = visitedVertexList.get(visitedVertexList.size() - 1);         // 起点: 最后一个已访问点
                double distance = getDistance(start, vertex);                               // 计算距离
                double arrivalTime = leaveTime + distance;                                  // 达到时间 = 上个离开时间+距离(速度1)
                // a. 若无时间窗口约束
                if(vertex.getTimeWindow() == null) {
                    visitedVertexList.add(vertex);
                    leaveTime = arrivalTime + vertex.getDuration();
                    this.score += vertex.getScore();
                } else { // b. 若有时间窗口约束
                    double windowBegin = vertex.getTimeWindow().getBeginTime();
                    double windowEnd   = vertex.getTimeWindow().getEndTime();
                    double serveTime   = arrivalTime;                                       // 服务时间 = 到达时间
                    if(serveTime < windowBegin) {
                        serveTime = windowBegin;                                            // 若服务时间早于窗口时间, 则需等待
                    }
                    if(serveTime < windowEnd) {                                             // 服务时间在窗口结束时间内, 则可访问
                        double tempLeaveTime = serveTime + vertex.getDuration();            // 预计离开时间 = 服务开始时间 + 服务时间
                        boolean canReturn = canReturn(vertex, tempLeaveTime, startVertex);  // 是否有足够时间返回
                        if(canReturn) {
                            visitedVertexList.add(vertex);
                            leaveTime = tempLeaveTime;                                      // 更新离开时间
                            this.score += vertex.getScore();                                // 加分
                        }
                    }
                }
            }
        }

        /* function ends */
        Score score = new Score();
        score.setMeanScore(Math.round(this.score));
        return score;
    }



    /**
     * 判断: 从当前顶点出发, 能够在时限内返回终点/起点
     * @param vertex    当前顶点
     * @param leaveTime 离开时间
     * @param endVertex 终点
     * @return          是/否
     */
    public boolean canReturn(Vertex vertex, double leaveTime, Vertex endVertex) {
        double distance = getDistance(vertex, endVertex);                                   // 计算距离
        double arrivalTime = leaveTime + distance;                                          // 达到时间 = 上个离开时间+距离(速度1)
        if(endVertex.getTimeWindow() != null) {
            double windowEndTime = endVertex.getTimeWindow().getEndTime();                  // 窗口结束时间
            if(arrivalTime > windowEndTime) {
                return false;
            }
        }
        return true;
    }



    /**
     * 计算两个顶点之间的直线距离
     * @author         杜永浩
     * @param vertex_1 起点顶点
     * @param vertex_2 终点顶点
     * @return         直线距离
     */
    public static double calDistance(Vertex vertex_1, Vertex vertex_2) {
        double distance = Math.pow(vertex_1.getX() - vertex_2.getX(), 2) + Math.pow(vertex_1.getY() - vertex_2.getY(), 2);
        distance = Math.sqrt(distance);
        return Math.round(distance);
    }


    /**
     * 基于距离矩阵, 获取两个顶点之间的直线距离
     * @author         杜永浩
     * @param vertex_1 起点顶点
     * @param vertex_2 终点顶点
     * @return         直线距离
     */
    public double getDistance(Vertex vertex_1, Vertex vertex_2) {
        if(vertex_1 == vertex_2 || vertex_1.getId().equals(vertex_2.getId()))    return 0;
        String index;
        if(vertex_1.getId() < vertex_2.getId())     index = vertex_1.getId() + " <-> " + vertex_2.getId();
        else                                        index = vertex_2.getId() + " <-> " + vertex_1.getId();
        Double distance = distanceMap.get(index);
        if(distance == null)                        return 0;
        else                                        return distance;
    }


    /**
     * 创建顶点距离矩阵
     * @author         杜永浩
     * @param solution 场景
     * @return         距离矩阵
     */
    public Map<String, Double> createDistanceMap(Solution solution) {
        logger.info("正创建 -> OP问题顶点距离矩阵 ......");
        List<Vertex> vertexList = new ArrayList<>(solution.getVertexList());
        vertexList.add(solution.getStartVertex());                      // 起点也需计算
        vertexList.sort(new IdComparator());                            // 按id排序
        Map<String, Double> map = new HashMap<>();
        for(int i = 0 ; i < vertexList.size() ; i ++) {
            for(int j = i + 1 ; j < vertexList.size() ; j ++) {
                double distance = calDistance(vertexList.get(i), vertexList.get(j));
                String index = vertexList.get(i).getId() + " <-> " + vertexList.get(j).getId();
                map.put(index, distance);
            }
        }
        logger.info("已创建 -> OP问题顶点距离索引表 (C " + vertexList.size() + "_2 = " + map.size() + ")！\n");
        this.distanceMap = map;
        return map;
    }


/* class ends */
}
