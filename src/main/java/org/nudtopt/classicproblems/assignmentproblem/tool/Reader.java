package org.nudtopt.classicproblems.assignmentproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.assignmentproblem.model.Assignment;
import org.nudtopt.classicproblems.assignmentproblem.model.Resource;
import org.nudtopt.classicproblems.assignmentproblem.model.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reader extends MainLogger {


    public static void main(String[] args) throws Exception {
        String problemName = "assign100";
        new Reader().read(problemName);
    }


    /**
     * 创建二维装箱问题数据场景
     * @param path 数据集名
     * @return     场景对象
     */
    public Assignment read(String path) throws Exception {
        logger.info("正载入 -> 标准指派问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("assignmentproblem/" + path + ".txt"), "GBK")); // 相对路径
        int taskNum = Integer.valueOf(br.readLine().split("\\s+")[1]);        // 任务/资源数
        // 1. 新建资源
        List<Resource> resourceList = new ArrayList<>();
        for(int i = 0 ; i < taskNum ; i ++) {
            Resource resource = new Resource();
            resource.setId((long) resourceList.size());
            resourceList.add(resource);
        }
        // 2. 新建任务
        List<Task> taskList = new ArrayList<>();
        for(int i = 0 ; i < taskNum ; i ++) {
            Task task = new Task();
            task.setId((long) taskList.size());
            task.setOptionalResourceList(resourceList);                                 // 决策变量取值范围
            task.setResource(resourceList.get(i));                                      // 决策变量赋初始值 (依次分配资源)
            taskList.add(task);
        }
        // 3. 读取并记录任务-资源指派成本
        String line;
        int taskId = 0;
        int resourceId = 0;
        int meanCost = 0;
        while((line = br.readLine()) != null) {
            String[] segments = line.split("\\s+");
            for(String cost : segments) {
                if(cost.length() == 0)   continue;
                Task task = taskList.get(taskId);
                Resource resource = resourceList.get(resourceId);
                task.getResourceCostMap().put(resource, Long.valueOf(cost));
                meanCost += Long.valueOf(cost);
                resourceId ++;
                if(resourceId >= resourceList.size()) { // id更新
                    resourceId = 0;
                    taskId ++;
                }
            }
        }
        meanCost = meanCost / taskNum / taskNum;
        /* function ends */
        Assignment assignment = new Assignment();
        assignment.setId(0L);
        assignment.setName(path);
        assignment.setTaskList(taskList);
        assignment.setResourceList(resourceList);
        logger.info("已载入 -> 标准指派问题数据集 (" + path + ": 任务/资源数 : " + taskNum + ", 平均指派成本: " + meanCost + ").\n");
        return assignment;
    }


}
