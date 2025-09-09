package org.nudtopt.classicproblems.knapsack.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.knapsack.model.Knapsack;
import org.nudtopt.classicproblems.knapsack.model.Object;
import org.nudtopt.classicproblems.knapsack.model.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reader extends MainLogger{


    public static void main(String[] args) throws Exception {
        String path = Tool.getDesktopPath() + "/mknap2.txt";
        new Reader().read(path, 0);
    }


    /**
     * 读取文件下全部数据, 返回第n个问题模型
     * @auther      杜永浩
     * @param path  文件路径
     * @param index 需要的第n个问题数据
     * @return      创建后的solution
     */
    public Solution read(String path, int index) throws Exception {
        List<Solution> solutionList = new ArrayList<>();
        List<String> dataList = new Reader().readStr(path);
        if(index < 0)                       index = 0;
        if(index > dataList.size() - 1)     index = dataList.size() - 1;
        for(int i = 0 ; i < dataList.size() ; i ++) {
            Solution solution = createSolution(dataList.get(i));
            solution.setId((long) solutionList.size());
            solutionList.add(solution);
        }
        Solution solution = solutionList.get(index);
        logger.info("已选择 -> 上  述: 第 " + index + " 组数据 (" + solution.getName() + ", 背包 1, 物品 " + solution.getObjectList().size()
                  + ", 维数 " + solution.getKnapsack().getCapabilityList().size() + ", 已知最优解 " + solution.getBestKnowSolution() + ") 进行求解！\n");
        return solution;
    }



    /**
     * 基于长字符串数据, 创建一个solution
     * @auther     杜永浩
     * @param data 长字符串数据
     * @return     创建后的solution
     */
    public Solution createSolution(String data) {
        String[] segments = data.split("\\s+");
        String title = segments[2].split(".DAT")[0];
        logger.info("正读取 -> 问题集: " + title + " 数据 ... ..." );
        // 1. 创建背包和物品
        int propertyNum = Integer.valueOf(segments[3]);     // 属性维度数
        int objectNum   = Integer.valueOf(segments[4]);     // 物品数
        Knapsack knapsack = new Knapsack();
        knapsack.setId(0L);
        List<Knapsack> optionalKnapsackList = Collections.singletonList(knapsack);
        List<Object>   objectList   = new ArrayList<>();
        for(int i = 0 ; i < objectNum ; i ++) {
            Object object = new Object();
            object.setId((long) objectList.size());
            object.setOptionalKnapsackList(optionalKnapsackList);
            objectList.add(object);
        }

        // 2. 赋值物品价值
        for(int i = 0 ; i < objectNum ; i ++) {
            Object object = objectList.get(i);
            int dataIndex = 5 + i;
            double value = Double.valueOf(segments[dataIndex]);
            object.setValue(value);
        }

        // 3. 赋值背包多维容量
        for(int i = 0 ; i < propertyNum ; i ++) {
            int dataIndex = 5 + objectNum + i;
            double capability = Double.valueOf(segments[dataIndex]);
            knapsack.getCapabilityList().add(capability);
        }

        // 4. 赋值物品多维度属性
        for(int i = 0 ; i < propertyNum ; i ++) {
            for(int j = 0 ; j < objectList.size() ; j ++) {
                Object object = objectList.get(j);
                int dataIndex = 5 + objectNum + propertyNum + i * objectList.size() + j;
                double property = Double.valueOf(segments[dataIndex]);
                object.getPropertyList().add(property);
            }
        }

        /* function ends */
        Solution solution = new Solution();
        solution.setName(title);
        solution.setIndex(data);
        solution.setKnapsack(knapsack);
        solution.setObjectList(objectList);
        solution.setBestKnowSolution(Double.valueOf(segments[5 + objectNum + propertyNum + propertyNum * objectList.size()]));
        logger.info("已创建 -> 背  包: 1 个, 物  品: " + objectList.size() + " 个(" + propertyNum + "维属性), 已知最优解: " + solution.getBestKnowSolution() + "." );
        return solution;
    }




    /**
     * 读取Knapsack问题数据集
     * 把每个问题的数据拼接到一起
     * @author     杜永浩
     * @param path 路径
     * @return     一个问题, 对应一个数据string
     */
    public List<String> readStr(String path) throws Exception {
        logger.info("正载入 -> Knapsack问题数据集 (" + path + ") ... ..." );
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("knapsack/" + path), "GBK")); // 相对路径
        // BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(new File(path))), "GBK")); // 决定路径
        String line;
        List<String> dataList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] segments = line.split("\\s+");
            if(segments.length == 3 && segments[2].contains(".DAT")) {                  // 遇到数据集标题行
                dataList.add("");
            }
            if(dataList.size() > 0 && line.length() > 1 && !line.contains("++++++")) {  // 把数据都拼接到一起
                dataList.set(dataList.size() - 1, dataList.get(dataList.size() - 1) + line);
            }
        }
        logger.info("共载入 -> Knapsack问题数据集 " + dataList.size() + " 组.\n" );
        return dataList;
    }


/* class ends */
}
