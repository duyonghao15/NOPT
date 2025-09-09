package org.nudtopt.classicproblems.vehicleroutingproblem.constraint;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Depot;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class Decoder extends Constraint {

    protected Map<String, Double> distanceMap = new HashMap<>();                // 距离矩阵
    protected double cost = 0;                                                  // 累计时间(除去服务时间)

    @Override
    public Score calScore() {
        Routes routes = (Routes) solution;
        Depot depot = routes.getDepot();
        Map<Vehicle, List<Customer>> vehicleRoutesMap = routes.getCustomerList().stream()   // 根据决策变量(车辆)的选择情况
                .filter(customer -> customer.getVehicle() != null)                          // 获取车->顾客的访问清单
                .sorted(solution.getComparator())                                           // 并根据priority排序
                .collect(Collectors.groupingBy(Customer::getVehicle));

        int outOfTime = 0;              // 违反窗口约束的时长
        int outOfNum = 0;               // 违反窗口约束的次数
        int outOfVolm = 0;
        // 解码
        this.cost = 0;
        for(Vehicle vehicle : routes.getVehicleList()) {
            List<Customer> customerList = vehicleRoutesMap.get(vehicle);
            List<Customer> customerList2 = vehicle.getCustomerList();
            if(customerList == null)    continue;
            customerList.sort(solution.getComparator());                                    // 排个序

            double arrivalTime = 0;                                                         // 到达(第二个)时间
            double serveTime = 0;                                                           // 服务开始时间
            double leaveTime = 0;                                                           // 离开时间/服务结束时间
            for(int i = 0 ; i <= customerList.size() ; i ++) {
                Customer customer_1 = i == 0 ? depot : customerList.get(i - 1);
                Customer customer_2 = i == customerList.size() ? depot : customerList.get(i);
                double distance = getDistance(customer_1, customer_2);
                arrivalTime = leaveTime + distance;                                         // 达到时间 = 上个离开时间+距离(速度1)
                serveTime = arrivalTime;                                                    // 服务时间 = 到达时间
                if(customer_2.getTimeWindow() != null) {
                    double windowBegin = customer_2.getTimeWindow().getBeginTime();
                    double windowEnd   = customer_2.getTimeWindow().getEndTime();
                    if(serveTime < windowBegin) {
                        serveTime = windowBegin;                                            // 若服务时间早于窗口时间, 则需等待
                    }
                    if(serveTime > windowEnd) {
                        outOfNum ++;
                        outOfTime += (serveTime - windowEnd);                               // 若服务时间晚于窗口时间, 则违法约束
                    }
                }
                leaveTime = serveTime + customer_2.getDuration();                           // 离开时间 = 服务开始时间 + 服务时间
                this.cost += distance + (serveTime - arrivalTime);                          // 成本 = 距离 + 等待时间 (服务开始时间-到达时间)
            }

            if(customerList != null && customerList.size() != customerList2.size()) {
                long a = 0;
            }
        }

        /* function ends */
        Score score = new Score();
        score.setHardScore(-outOfNum);                                                      // 硬约束: 超出时间窗数 (尽可能少)
        score.setMeanScore(-outOfTime);                                                     // 硬约束: 超过时间窗时长 (尽可能少)
        score.setSoftScore(-Math.round(this.cost));                                         // 收  益: 成本 (尽可能小)
        return score;
    }




    /**
     * 计算两个城市之间的直线距离
     * @author           杜永浩
     * @param customer_1 起点客户
     * @param customer_2 终点客户
     * @return           直线距离
     */
    public static double calDistance(Customer customer_1, Customer customer_2) {
        double distance = Math.pow(customer_1.getX() - customer_2.getX(), 2) + Math.pow(customer_1.getY() - customer_2.getY(), 2);
        distance = Math.sqrt(distance);
        return Math.round(distance);
    }


    /**
     * 基于距离矩阵, 获取两个城市之间的直线距离
     * @author           杜永浩
     * @param customer_1 起点客户
     * @param customer_2 终点客户
     * @return           直线距离
     */
    public double getDistance(Customer customer_1, Customer customer_2) {
        if(customer_1 == customer_2 || customer_1.getId().equals(customer_2.getId()))    return 0;
        String index;
        if(customer_1.getId() < customer_2.getId())     index = customer_1.getId() + " <-> " + customer_2.getId();
        else                                            index = customer_2.getId() + " <-> " + customer_1.getId();
        Double distance = distanceMap.get(index);
        if(distance == null)                    return 0;
        else                                    return Tool.round(distance, 2);
    }


    /**
     * 创建城市距离矩阵
     * @author       杜永浩
     * @param routes 场景
     * @return       距离矩阵
     */
    public Map<String, Double> createDistanceMap(Routes routes) {
        logger.info("正创建 -> VRP客户距离矩阵 ......");
        List<Customer> customerList = new ArrayList<>(routes.getCustomerList());
        customerList.add(routes.getDepot());                            // 车场也需计算
        customerList.sort(new IdComparator());                          // 按id排序
        Map<String, Double> map = new HashMap<>();
        for(int i = 0 ; i < customerList.size() ; i ++) {
            for(int j = i + 1 ; j < customerList.size() ; j ++) {
                double distance = calDistance(customerList.get(i), customerList.get(j));
                String index = customerList.get(i).getId() + " <-> " + customerList.get(j).getId();
                map.put(index, distance);
            }
        }
        logger.info("已创建 -> VRP城市距离索引表 (C " + customerList.size() + "_2 = " + map.size() + ")！\n");
        this.distanceMap = map;
        return map;
    }


/* class ends */
}
