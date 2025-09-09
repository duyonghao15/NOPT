package org.nudtopt.classicproblems.vehicleroutingproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Reader extends MainLogger{

    public static void main(String[] args) throws Exception {
        String path = "c101";
        new Reader().read(path);
    }


    /**
     * 读取cvrptw问题数据集
     * @author     杜永浩
     * @param path 文件路径
     * @return     解
     */
    public Routes read(String path) throws Exception {
        logger.info("正载入 -> CVRPTW问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("vehicleroutingproblem/CVRPTW_Cordeau/" + path), "GBK")); // 相对路径
        // BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File(path))), "GBK"));          // 绝对路径
        String firstLine  = br.readLine();
        String secondLine = br.readLine();
        String thirdLine  = br.readLine();
        int vehicleNum  = Integer.valueOf(firstLine.split("\\s+")[1]);
        int customerNum = Integer.valueOf(firstLine.split("\\s+")[2]);
        int capability  = Integer.valueOf(secondLine.split("\\s+")[1]);

        // 1. 创建车场
        Depot depot = new Depot();
        depot.setId(Long.valueOf(thirdLine.split("\\s+")[1]));
        depot.setX(Double.valueOf(thirdLine.split("\\s+")[2]));
        depot.setY(Double.valueOf(thirdLine.split("\\s+")[3]));

        // 2. 创建车辆
        List<Vehicle> vehicleList = new ArrayList<>();
        for(int i = 0 ; i < vehicleNum ; i ++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId((long) vehicleList.size());
            vehicle.setCapacity(capability);
            vehicle.setDepot(depot);
            vehicleList.add(vehicle);
        }

        // 3. 创建用户
        String line;
        List<Customer> customerList = new ArrayList<>();
        List<Integer> priorityList = new ArrayList<>();
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
            double demand = Double.valueOf(segments[4 + indexGap]);             // 需求量
            long beginTime = Long.valueOf(segments[8 + indexGap]);              // 访问窗口开始时间
            long endTime = Long.valueOf(segments[9 + indexGap]);                // 访问窗口结束时间

            Customer customer = new Customer();
            customer.setId(id);
            customer.setX(x);
            customer.setY(y);
            customer.setDemand(demand);
            customer.setDuration(duration);
            TimeWindow timeWindow = new TimeWindow();
            timeWindow.setBeginTime(beginTime);
            timeWindow.setEndTime(endTime);
            customer.setTimeWindow(timeWindow);
            customer.setOptionalVehicleList(vehicleList);                       // 决策变量(可选车辆)取值范围赋值
            customer.setOptionalPriorityList(priorityList);                     // 决策变量(可选优先顺序)取值范围赋值
            customerList.add(customer);
        }
        priorityList.add(0);                                                    // priority最小=0
        priorityList.add(customerList.size());                                  // priority最小=customerNum
        // priorityList.add(0);                                                 // priority最小=0

        /* function ends */
        Routes routes = new Routes();
        routes.setId(0L);
        routes.setName(path);
        routes.setDepot(depot);
        routes.setVehicleList(vehicleList);
        routes.setCustomerList(customerList);
        logger.info("已载入 -> CVRPTW问题数据集 (" + path + ": 车场: 1, 车辆: " + vehicleList.size() + ", 每车容量: " + capability + ", 顾客: " + customerList.size() + ").\n");
        initialize(routes);
        return routes;
    }


    /**
     * 初始化, 决策变量赋初始值
     * 并更新车辆/客户分配情况
     */
    public void initialize(Routes routes) {
        // 1. 决策变量赋初值
        for(Customer customer : routes.getCustomerList()) {
            customer.setVehicle(Tool.randomFromList(customer.getOptionalVehicleList()));
            customer.setPriority(Tool.randomFromList(customer.getOptionalPriorityList()));
        }
        // 2. 根据决策变量(车辆)的选择情况, 获取车->顾客的访问清单
        Map<Vehicle, List<Customer>> vehicleRoutesMap = routes.getCustomerList().stream()
                .filter(customer -> customer.getVehicle() != null)
                .collect(Collectors.groupingBy(Customer::getVehicle));
        // 3. 更新并排序vehicle的customerList
        CustomerComparator comparator = new CustomerComparator();
        for(Vehicle vehicle : vehicleRoutesMap.keySet()) {
            List<Customer> customerList = vehicleRoutesMap.get(vehicle);
            vehicle.getCustomerList().addAll(customerList);
            comparator.sort(vehicle.getCustomerList());
            comparator.check(vehicle.getCustomerList());
        }
    }


/* class ends */
}
