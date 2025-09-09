package org.nudtopt.classicproblems.vehicleroutingproblem.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Depot;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Decoder2 extends Decoder {


    @Override
    public Score calScore() {
        Routes routes = (Routes) solution;
        Depot depot = routes.getDepot();
        Map<Vehicle, List<Customer>> vehicleRoutesMap = routes.getCustomerList().stream()   // 根据决策变量(车辆)的选择情况
                .filter(customer -> customer.getVehicle() != null)                          // 获取车->顾客的访问清单
                .sorted(solution.getComparator())                                           // 并根据index排序
                .collect(Collectors.groupingBy(Customer::getVehicle));

        int outOfTime = 0;              // 违反窗口约束的时长
        int outOfVolm = 0;
        int customerNum = 0;
        // 解码
        this.cost = 0;
        for(Vehicle vehicle : routes.getVehicleList()) {
            int demand = 0;
            List<Customer> customerList = vehicleRoutesMap.get(vehicle);
            if(customerList == null || customerList.size() == 0)    continue;
            customerList.sort(solution.getComparator());
            customerNum += customerList.size();

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
                        outOfTime ++;                                                       // 若服务时间晚于窗口时间, 则违法约束
                    }
                }
                leaveTime = serveTime + customer_2.getDuration();                           // 离开时间 = 服务开始时间 + 服务时间
                demand += customer_2.getDemand();
                this.cost += distance + (serveTime - arrivalTime);                          // 成本 = 距离 + 等待时间 (服务开始时间-到达时间)
            }
            if(demand > vehicle.getCapacity()) {                                            // 超出容量限制约束
                outOfVolm ++;
            }
        }

        /* function ends */
        int nullCustomerNum = routes.getCustomerList().size() - customerNum;
        Score score = new Score();
        score.setHardScore(- outOfTime - outOfVolm);                                        // 硬约束: 超出时间窗, 超出容量 (尽可能少)
        score.setMeanScore(- nullCustomerNum);                                              // 硬约束: 未访问的客户数 (尽可能少)
        score.setSoftScore(- Math.round(this.cost));                                        // 收  益: 成本 (尽可能少)
        return score;
    }



/* class ends */
}
