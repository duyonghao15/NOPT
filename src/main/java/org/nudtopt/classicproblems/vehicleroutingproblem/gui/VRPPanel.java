package org.nudtopt.classicproblems.vehicleroutingproblem.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.api.tool.gui.TangoColorFactory;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Customer;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Depot;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Routes;
import org.nudtopt.classicproblems.vehicleroutingproblem.model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VRPPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());  // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());  // 画黑边框
        Routes routes = (Routes) solution;

        // 1. 统计最大最小经纬度
        Double xMin = null;
        Double xMax = null;
        Double yMin = null;
        Double yMax = null;
        List<Customer> customerList = new ArrayList<>(routes.getCustomerList());
        customerList.add(routes.getDepot());
        for(Customer customer : customerList) {
            double x = customer.getX();
            double y = customer.getY();
            xMin = xMin == null ? x : Math.min(xMin, x);
            xMax = xMax == null ? x : Math.max(xMax, x);
            yMin = yMin == null ? y : Math.min(yMin, y);
            yMax = yMax == null ? y : Math.max(yMax, y);
        }

        // 2. 画点
        for(Customer customer : customerList) {
            double x = customer.getX();
            double y = customer.getY();
            int xPlot = scaleX(x, xMin, xMax);
            int yPlot = scaleY(y, yMin, yMax);
            int width = 6;
            if(customer.getId() == 0) {                      // 起点
                g.setColor(new Color(255, 0, 0));   // 着红色, 画方块
                width = width * 4;
                g.fillRect(xPlot - width/2, getHeight() - yPlot - width/2, width, width);
            } else {
                g.setColor(new Color(0, 0, 0));     // 着黑色, 画圆
                g.fillOval(xPlot - width/2, getHeight() - yPlot - width/2, width, width);
            }
        }

        // 3. 画线
        Depot depot = routes.getDepot();
        for(int i = 0 ; i < routes.getVehicleList().size() ; i ++) {                            // 遍历车辆
            Vehicle vehicle = routes.getVehicleList().get(i);
            List<Customer> visitedCustomerList = new ArrayList<>(vehicle.getCustomerList());    // 按顺序访问的客户
            visitedCustomerList.add(0, depot);                                            // 把起点加上
            int colorIndex = (i + 1) % TangoColorFactory.SEQUENCE_1.length;                     // 颜色周期性递增变化
            g.setColor(TangoColorFactory.SEQUENCE_1[colorIndex]);                               // 逐一赋颜色
            // 遍历顶点
            for(int j = 0 ; j < visitedCustomerList.size() ; j ++) {
                Customer customer_1 = visitedCustomerList.get(j);
                Customer customer_2 = j < visitedCustomerList.size() - 1 ? visitedCustomerList.get(j + 1) : visitedCustomerList.get(0);
                double x_1 = customer_1.getX();
                double y_1 = customer_1.getY();
                double x_2 = customer_2.getX();
                double y_2 = customer_2.getY();
                int xPlot_1 = scaleX(x_1, xMin, xMax);
                int yPlot_1 = scaleY(y_1, yMin, yMax);
                int xPlot_2 = scaleX(x_2, xMin, xMax);
                int yPlot_2 = scaleY(y_2, yMin, yMax);
                g.drawLine(xPlot_1, getHeight() - yPlot_1, xPlot_2, getHeight() - yPlot_2);
            }
        }
        /* function ends */
    }



    /**
     * 将实际坐标转换为图像坐标
     * @param x    实际坐标值
     * @param xMin 实际最小值
     * @param xMax 实际最大值
     * @return     图像中坐标值
     */
    public int scaleX(double x, double xMin, double xMax) {
        double xPlot = getWidth() * (x - xMin) / (xMax - xMin);
        return (int) xPlot;
    }
    public int scaleY(double y, double yMin, double yMax) {
        double yPlot = getHeight() * (y - yMin) / (yMax - yMin);
        return (int) yPlot;
    }


    @Override
    public String getName() {
        return "路径图";
    }


/* class ends */
}
