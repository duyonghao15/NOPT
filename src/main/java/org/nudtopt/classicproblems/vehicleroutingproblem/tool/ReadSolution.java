package org.nudtopt.classicproblems.vehicleroutingproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadSolution extends MainLogger {

    public void readBestSolution(Routes routes) {
        List<Vehicle> vehicleList = routes.getVehicleList();
        if(routes.getName().equals("c101")) {
            set(routes, vehicleList.get(0), Arrays.asList(20, 24, 25, 27, 29, 30, 28, 26, 23, 22, 21));
            set(routes, vehicleList.get(1), Arrays.asList(67, 65, 63, 62, 74, 72, 61, 64, 68, 66, 69));
            set(routes, vehicleList.get(2), Arrays.asList(43, 42, 41, 40, 44, 46, 45, 48, 51, 50, 52, 49, 47));
            set(routes, vehicleList.get(3), Arrays.asList(13, 17, 18, 19, 15, 16, 14, 12));
            set(routes, vehicleList.get(4), Arrays.asList(57, 55, 54, 53, 56, 58, 60, 59));
            set(routes, vehicleList.get(5), Arrays.asList(90, 87, 86, 83, 82, 84, 85, 88, 89, 91));
            set(routes, vehicleList.get(6), Arrays.asList(32, 33, 31, 35, 37, 38, 39, 36, 34));
            set(routes, vehicleList.get(7), Arrays.asList(98, 96, 95, 94, 92, 93, 97, 100, 99));
            set(routes, vehicleList.get(8), Arrays.asList(5, 3, 7, 8, 10, 11, 9, 6, 4, 2, 1, 75));
            set(routes, vehicleList.get(9), Arrays.asList(81, 78, 76, 71, 70, 73, 77, 79, 80));
        }
        if(routes.getName().equals("c102")) {
            set(routes, vehicleList.get(0), Arrays.asList(57, 55, 54, 53, 56, 58, 60, 59));
            set(routes, vehicleList.get(1), Arrays.asList(20, 24, 25, 27, 29, 30, 28, 26, 23, 22, 21));
            set(routes, vehicleList.get(2), Arrays.asList(67, 65, 63, 62, 74, 72, 61, 64, 68, 66, 69));
            set(routes, vehicleList.get(3), Arrays.asList(32, 33, 31, 35, 37, 38, 39, 36, 34));
            set(routes, vehicleList.get(4), Arrays.asList(98, 96, 95, 94, 92, 93, 97, 100, 99));
            set(routes, vehicleList.get(5), Arrays.asList(81, 78, 76, 71, 70, 73, 77, 79, 80));
            set(routes, vehicleList.get(6), Arrays.asList(13, 17, 18, 19, 15, 16, 14, 12));
            set(routes, vehicleList.get(7), Arrays.asList(5, 3, 7, 8, 10, 11, 9, 6, 4, 2, 1, 75));
            set(routes, vehicleList.get(8), Arrays.asList(43, 42, 41, 40, 44, 46, 45, 48, 51, 50, 52, 49, 47));
            set(routes, vehicleList.get(9), Arrays.asList(90, 87, 86, 83, 82, 84, 85, 88, 89, 91));
        }

        // 根据决策变量(车辆)的选择情况, 获取车->顾客的访问清单
        Map<Vehicle, List<Customer>> vehicleRoutesMap = routes.getCustomerList().stream()
                .filter(customer -> customer.getVehicle() != null)
                .collect(Collectors.groupingBy(Customer::getVehicle));
        // 更新并排序vehicle的customerList
        CustomerComparator comparator = new CustomerComparator();
        for(Vehicle vehicle : vehicleRoutesMap.keySet()) {
            vehicle.setCustomerList(vehicleRoutesMap.get(vehicle));
            comparator.sort(vehicle.getCustomerList());
            comparator.check(vehicle.getCustomerList());
        }
    }


    /**
     * 根据客户访问顺序值, 赋决策变量值
     * @param routes         场景
     * @param vehicle        车辆
     * @param customerIdList 客户访问顺序
     */
    public void set(Routes routes, Vehicle vehicle, List<Integer> customerIdList) {
        for(int i = 0 ; i < customerIdList.size() ; i ++) {
            int id = customerIdList.get(i);
            Customer customer = (Customer)routes.getDecisionEntity(id);
            customer.setVehicle(vehicle);
            int priority = i;
            int maxPriority = customer.getOptionalPriorityList().get(customer.getOptionalPriorityList().size() - 1);
            customer.setPriority(Math.min(priority, maxPriority));
        }
    }











}
