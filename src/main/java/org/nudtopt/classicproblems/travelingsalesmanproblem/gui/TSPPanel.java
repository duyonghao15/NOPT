package org.nudtopt.classicproblems.travelingsalesmanproblem.gui;

import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.City;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Tour;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TSPPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());  // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());  // 画黑边框
        Tour tour = (Tour) solution;

        // 1. 统计最大最小经纬度
        Double xMin = null;
        Double xMax = null;
        Double yMin = null;
        Double yMax = null;
        for(City city : tour.getCityList()) {
            double x = city.getX();
            double y = city.getY();
            xMin = xMin == null ? x : Math.min(xMin, x);
            xMax = xMax == null ? x : Math.max(xMax, x);
            yMin = yMin == null ? y : Math.min(yMin, y);
            yMax = yMax == null ? y : Math.max(yMax, y);
        }

        // 2. 画点
        for(City city : tour.getCityList()) {
            double x = city.getX();
            double y = city.getY();
            int xPlot = scaleX(x, xMin, xMax);
            int yPlot = scaleY(y, yMin, yMax);
            int width = 6;
            g.setColor(new Color(0, 0, 0));        // a. 着色
            g.fillRect(xPlot - width/2, getHeight() - yPlot - width/2, width, width);
            g.setColor(new Color(0, 0, 0));        // b. 画边框
            g.drawRect(xPlot - width/2, getHeight() - yPlot - width/2, width, width);
        }

        // 3. 画线
        List<City> visitedCityList = tour.getSalesman().getCityList();
        for(int i = 0 ; i < visitedCityList.size() - 1 ; i ++) {
            City city_1 = visitedCityList.get(i);
            City city_2 = visitedCityList.get(i + 1);
            double x_1 = city_1.getX();
            double y_1 = city_1.getY();
            double x_2 = city_2.getX();
            double y_2 = city_2.getY();
            int xPlot_1 = scaleX(x_1, xMin, xMax);
            int yPlot_1 = scaleY(y_1, yMin, yMax);
            int xPlot_2 = scaleX(x_2, xMin, xMax);
            int yPlot_2 = scaleY(y_2, yMin, yMax);
            g.drawLine(xPlot_1, getHeight() - yPlot_1, xPlot_2, getHeight() - yPlot_2);
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
        return "旅行图";
    }


/* class ends */
}
