package org.nudtopt.classicproblems.travelingsalesmanproblem.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.City;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Salesman;
import org.nudtopt.classicproblems.travelingsalesmanproblem.model.Tour;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Reader extends MainLogger {


    public static void main(String[] args) throws Exception {
        String path = Tool.getDesktopPath() + "/TSP_VLSI/xqf131.tsp";
        new Reader().read(path);
    }


    /**
     * 读取tsp问题数据集
     * @author     杜永浩
     * @param path 文件路径
     * @return     解
     */
    public Tour read(String path) throws Exception {
        List<City> cityList = new ArrayList<>();
        logger.info("正载入 -> TSP问题数据集 (" + path + ") ... ..." );
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("travelingsalesmanproblem/TSP_VLSI/" + path), "GBK")); // 相对路径
        // BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File(path))), "GBK"));          // 绝对路径
        String problemName = br.readLine().split("\\s+")[2];
        br.readLine();
        br.readLine();
        br.readLine();
        br.readLine();
        int cityNum = Integer.valueOf(br.readLine().split("\\s+")[2]);
        br.readLine();
        br.readLine();
        // 1. 预设决策变量值域(=城市数)
        Salesman salesman = new Salesman();
        salesman.setId(0L);
        List<Integer> optionalPriorityList = new ArrayList<>();
        /*for(int i = 0 ; i < cityNum ; i ++) {
            optionalOrderList.add(i);
        }*/
        optionalPriorityList.add(0);
        optionalPriorityList.add(cityNum - 1);
        // 2. 读取数据
        String line;
        while ((line = br.readLine()) != null) {
            if(line.equals("EOF"))       break;
            String[] segments = line.split("\\s+");
            long id  = Long.valueOf(segments[0]);
            double x = Double.valueOf(segments[1]);
            double y = Double.valueOf(segments[2]);
            City city = new City();
            city.setId(id);
            city.setX(x);
            city.setY(y);
            city.setSalesman(salesman);
            city.setOptionalPriorityList(optionalPriorityList);     // 决策变量取值范围
            city.setPriority(cityList.size());                      // 决策变量设置默认值
            cityList.add(city);
        }
        salesman.setCityList(cityList);
        /* function ends */

        Tour tour = new Tour();
        tour.setId(0L);
        tour.setName(problemName);
        tour.setIndex(problemName);
        tour.setCityList(cityList);
        tour.setSalesman(salesman);
        logger.info("已创建 -> TSP问题模型 (城市数: " + cityList.size() + ")！\n" );
        return tour;
    }



/* class ends */
}
