package org.nudtopt.classicproblems.jobshopscheduling.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.jobshopscheduling.model.Job;
import org.nudtopt.classicproblems.jobshopscheduling.model.JobShop;
import org.nudtopt.classicproblems.jobshopscheduling.model.Machine;
import org.nudtopt.classicproblems.jobshopscheduling.model.Operation;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reader extends MainLogger {

    public static void main(String[] args) throws Exception {
        String problemName = "SFJS1";
        new Reader().read(problemName);
    }


    /**
     * 读取数据集数据
     * @param path 数据集名
     * @return     初始solution
     */
    public JobShop read(String path) throws Exception {
        logger.info("正载入 -> 柔性作业车间问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("jobshopscheduling/" + path + ".txt"), "GBK")); // 相对路径
        String firstLine = br.readLine();
        int operationNum = Integer.valueOf(firstLine.split("\\s+")[0]);         // 工序总数
        int jobNum       = Integer.valueOf(firstLine.split("\\s+")[1]);         // 工件数
        int machineNum   = Integer.valueOf(firstLine.split("\\s+")[2]);         // 机器数
        // 1. 新建机器: machine
        List<Machine> machineList = new ArrayList<>();
        for(int i = 0 ; i < machineNum ; i ++) {
            Machine machine = new Machine();
            machine.setId((long) machineList.size());
            machineList.add(machine);
        }
        // 2. 新建工序: operation
        List<Operation> operationList = new ArrayList<>();
        for(int i = 0 ; i < operationNum ; i ++) {
            Operation operation = new Operation();
            operation.setId((long) operationList.size());
            operationList.add(operation);
        }
        // 3. 新建工件: job, 并读取工件中的工序时序关系
        List<Job> jobList = new ArrayList<>();
        for(int i = 0 ; i < jobNum ; i ++) {
            Job job = new Job();
            job.setId((long) jobList.size());
            jobList.add(job);
            String line = br.readLine();
            int preOperationIndex = Integer.valueOf(line.split("\\s+")[0]);              // 前置工序index
            int nextOperationIndex= Integer.valueOf(line.split("\\s+")[1]);              // 后续工序index
            Operation preOperation = operationList.get(preOperationIndex);                      // 前置工序
            Operation operation = operationList.get(nextOperationIndex);                        // 后续工序
            job.getOperationList().add(preOperation);
            job.getOperationList().add(operation);
            operation.setPreOperation(preOperation);
        }
        // 4. 遍历工序, 读取工序在机器上的加工时间
        for(Operation operation : operationList) {
            String line = br.readLine();
            int optionalMachineNum = Integer.valueOf(line.split("\\s+")[0]);             // 可用的机器数
            for(int i = 0 ; i < optionalMachineNum ; i ++) {
                int machineId = Integer.valueOf(line.split("\\s+")[i * 2 + 1]);          // 机器id
                long time = Integer.valueOf(line.split("\\s+")[i * 2 + 2]);              // 加工时间
                Machine machine = machineList.get(machineId);                                   // 机器
                machine.getProcessTimeMap().put(operation, time);                               // 存储本机器, 加工该工序的时间
                operation.getOptionalMachineList().add(machine);                                // 工序可选机器, 赋值
            }
        }
        // 6. 决策变量初始化
        List<Integer> optionalPriorityList = Arrays.asList(0, operationNum * 2);
        for(Operation operation : operationList) {
            operation.setOptionalPriorityList(optionalPriorityList);
            operation.setPriority(operation.getId().intValue());                                // 决策变量1赋值
            Machine randomMachine = Tool.randomFromList(operation.getOptionalMachineList());    // 随机选择一个(可用的)机器
            operation.setMachine(randomMachine);                                                // 决策变量2赋值
            // randomMachine.getOperationList().add(operation);                                    // 机器上的工序更新
        }
        /* function ends */
        JobShop jobShop = new JobShop();
        jobShop.setId(1L);
        jobShop.setJobList(jobList);
        jobShop.setMachineList(machineList);
        jobShop.setOperationList(operationList);
        logger.info("已载入 -> 柔性作业车间问题数据集 (" + path + ": 工序总数: " + operationNum + ", 工件/工序约束数: " + jobNum + ", 机器数: " + machineNum + ").\n");
        return jobShop;
    }

/* class ends */
}
