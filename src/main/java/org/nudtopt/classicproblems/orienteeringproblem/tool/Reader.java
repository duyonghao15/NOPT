package org.nudtopt.classicproblems.orienteeringproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.orienteeringproblem.model.Solution;
import org.nudtopt.classicproblems.orienteeringproblem.model.TimeWindow;
import org.nudtopt.classicproblems.orienteeringproblem.model.Tour;
import org.nudtopt.classicproblems.orienteeringproblem.model.Vertex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Reader extends MainLogger {

    public static void main(String[] args) throws Exception {
        String path = "c101";
        int tourNum = 1;            // 路径数1-4
        new Reader().read(path + ".txt", tourNum);
    }


    /**
     * 读取cvrptw问题数据集
     * @author        杜永浩
     * @param path    文件路径
     * @param tourNum 路径数量
     * @return        解
     */
    public Solution read(String path, int tourNum) throws Exception {
        logger.info("正载入 -> OPTW问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("orienteeringproblem/optw/" + path), "GBK")); // 相对路径
        String firstLine  = br.readLine();
        String secondLine = br.readLine();
        String thirdLine  = br.readLine();
        int vertexNum = Integer.valueOf(firstLine.split("\\s+")[2]);
        List<Vertex> vertexList = new ArrayList<>();
        List<Integer> priorityList = new ArrayList<>();

        // 1. 创建起点
        Vertex startVertex = new Vertex();
        startVertex.setId(Long.valueOf(thirdLine.split("\\s+")[1]));
        startVertex.setX(Double.valueOf(thirdLine.split("\\s+")[2]));
        startVertex.setY(Double.valueOf(thirdLine.split("\\s+")[3]));
        TimeWindow timeWindow = new TimeWindow();
        timeWindow.setBeginTime(Long.valueOf(thirdLine.split("\\s+")[8]));
        timeWindow.setEndTime(Long.valueOf(thirdLine.split("\\s+")[9]));
        startVertex.setTimeWindow(timeWindow);
        vertexList.add(startVertex);

        // 2. 创建路径
        List<Tour> tourList = new ArrayList<>();
        for(int i = 0 ; i < tourNum ; i ++) {
            Tour tour = new Tour();
            tour.setId((long) tourList.size());
            tour.setStartVertex(startVertex);
            tourList.add(tour);
        }

        // 3. 创建顶点
        String line;
        while ((line = br.readLine()) != null) {
            String[] segments = line.split("\\s+");
            int indexGap = 0;
            if(segments[0].length() == 0) {
                indexGap = 1;
            }
            long id = Long.valueOf(segments[0 + indexGap]);
            double x = Double.valueOf(segments[1 + indexGap]);
            double y = Double.valueOf(segments[2 + indexGap]);
            long duration = Math.round(Double.valueOf(segments[3 + indexGap])); // 服务时长
            double score = Double.valueOf(segments[4 + indexGap]);              // 得分
            long beginTime = Long.valueOf(segments[8 + indexGap]);              // 访问窗口开始时间
            long endTime = Long.valueOf(segments[9 + indexGap]);                // 访问窗口结束时间

            Vertex vertex = new Vertex();
            vertex.setId(id);
            vertex.setX(x);
            vertex.setY(y);
            vertex.setScore(score);
            vertex.setDuration(duration);
            timeWindow = new TimeWindow();
            timeWindow.setBeginTime(beginTime);
            timeWindow.setEndTime(endTime);
            vertex.setTimeWindow(timeWindow);
            vertex.setOptionalTourList(tourList);                               // 决策变量(可选路径)取值范围赋值
            vertex.setOptionalPriorityList(priorityList);                       // 决策变量(可选优先顺序)取值范围赋值
            vertexList.add(vertex);
        }
        priorityList.add(0);
        priorityList.add(vertexList.size());

        /* function ends */
        Solution solution = new Solution();
        solution.setId(0L);
        solution.setStartVertex(startVertex);
        solution.setTourList(tourList);
        solution.setVertexList(vertexList);
        logger.info("已载入 -> OPTW问题数据集 (" + path + ": 起点数: 1, 路线数: " + tourList.size() + ", 顶点数: " + vertexList.size() + ").\n");
        // initialize(routes);
        return solution;
    }


    /**
     * 初始化, 决策变量赋初始值
     * 并更新顶点/路线分配情况
     */
    public void initialize(Solution solution) {
        // 1. 决策变量赋初值
        for(Vertex vertex : solution.getVertexList()) {
            vertex.setTour(Tool.randomFromList(vertex.getOptionalTourList()));
            vertex.setPriority(Tool.randomFromList(vertex.getOptionalPriorityList()));
        }
        // 2. 根据决策变量(路线)的选择情况, 获取顶点的访问清单
        Map<Tour, List<Vertex>> tourMap = solution.getVertexList().stream()
                .filter(vertex -> vertex.getTour() != null)
                .collect(Collectors.groupingBy(Vertex::getTour));
        // 3. 更新并排序tour的vertexList
        VertexComparator comparator = new VertexComparator();
        for(Tour tour : tourMap.keySet()) {
            List<Vertex> vertexList = tourMap.get(tour);
            tour.getVertexList().addAll(vertexList);
            comparator.sort(tour.getVertexList());
            comparator.check(tour.getVertexList());
        }
    }


/* class ends */
}
