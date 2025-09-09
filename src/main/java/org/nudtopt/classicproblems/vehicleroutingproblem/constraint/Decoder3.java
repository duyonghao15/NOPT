package org.nudtopt.classicproblems.vehicleroutingproblem.constraint;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Depot;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Decoder3 extends Decoder {


    @Override
    public Score calScore() {
        Routes routes = (Routes) solution;
        Depot depot = routes.getDepot();
        int outOfTime = 0;              // 违反窗口约束的时长
        int outOfVolm = 0;
        int customerNum = 0;
        int waitTime  = 0;
        // 解码
        this.cost = 0;
        for(Vehicle vehicle : routes.getVehicleList()) {
            int demand = 0;
            List<Customer> allocatedCustomerList = vehicle.getCustomerList();               // vehicle所分配的客户(自动更新)
            List<Customer> visitedCustomerList = new ArrayList<>();                         // 记录已访问的客户
            visitedCustomerList.add(depot);                                                 // 首先加上车场(起点)
            double leaveTime = 0;                                                           // 离开时间/服务结束时间
            for(Customer customer : allocatedCustomerList) {
                Customer start = visitedCustomerList.get(visitedCustomerList.size() - 1);   // 起点: 最后一个已访问点
                double distance = getDistance(start, customer);                             // 计算距离
                double arrivalTime = leaveTime + distance;                                  // 达到时间 = 上个离开时间+距离(速度1)
                // a. 若无时间窗口约束
                if(customer.getTimeWindow() == null) {
                    visitedCustomerList.add(customer);
                    leaveTime = arrivalTime + customer.getDuration();
                    demand += customer.getDemand();
                    this.cost += distance;                                                  // 更新成本 = 距离
                } else { // b. 若有时间窗口约束
                    double windowBegin = customer.getTimeWindow().getBeginTime();
                    double windowEnd   = customer.getTimeWindow().getEndTime();
                    double serveTime   = arrivalTime;                                       // 服务时间 = 到达时间
                    if(serveTime < windowBegin) {
                        serveTime = windowBegin;                                            // 若服务时间早于窗口时间, 则需等待
                        waitTime += serveTime - arrivalTime;
                    }
                    if(serveTime <= windowEnd) {                                            // 服务时间在窗口结束时间内, 则可访问
                        double tempLeaveTime = serveTime + customer.getDuration();          // 预计离开时间 = 服务开始时间 + 服务时间
                        boolean canReturn = canReturn(customer, tempLeaveTime, depot);      // 是否有足够时间返回
                        if(canReturn) {
                            visitedCustomerList.add(customer);
                            leaveTime = tempLeaveTime;                                      // 更新离开时间
                            demand += customer.getDemand();
                            this.cost += distance + (serveTime - arrivalTime);              // 更新成本 = 距离 + 等待时间 (服务开始时间-到达时间)
                        }
                    } else {
                        outOfTime += serveTime - windowEnd;
                    }
                }
            }
            customerNum += visitedCustomerList.size() - 1;                                  // 累加已访问的客户数
            Customer last = visitedCustomerList.get(visitedCustomerList.size() - 1);        // 最后一个已访问点
            double returnDistance = getDistance(last, depot);
            this.cost += returnDistance;
        }

        /* function ends */
        int nullCustomerNum = routes.getCustomerList().size() - customerNum;
        Score score = new Score();
        score.setHardScore(- nullCustomerNum);                                              // 硬约束: 未访问的客户数 (尽可能少)
        score.setMeanScore(- Math.round(this.cost));                                        // 收  益: 成本 (尽可能小)
        // score.setSoftScore(- Math.round(waitTime));                                         // 收 益2: 等待时间 (尽可能小)
        return score;
    }



    /**
     * 判断: 从当前顶点出发, 能够在时限内返回终点/起点
     * @param customer  当前客户
     * @param leaveTime 离开时间
     * @param depot     车场/起点
     * @return          是/否
     */
    public boolean canReturn(Customer customer, double leaveTime, Depot depot) {
        double distance = getDistance(customer, depot);                                     // 计算距离
        double arrivalTime = leaveTime + distance;                                          // 达到时间 = 上个离开时间+距离(速度1)
        if(depot.getTimeWindow() != null) {
            double windowEndTime = depot.getTimeWindow().getEndTime();                      // 窗口结束时间
            if(arrivalTime > windowEndTime) {
                return false;
            }
        }
        return true;
    }

/* class ends */
}
