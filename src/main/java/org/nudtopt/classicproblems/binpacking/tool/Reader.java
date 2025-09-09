package org.nudtopt.classicproblems.binpacking.tool;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.apidata.Data;
import org.nudtopt.classicproblems.binpacking.model.Bin;
import org.nudtopt.classicproblems.binpacking.model.Item;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reader extends MainLogger {


    public static void main(String[] args) throws Exception {
        String problemName = "cgcut1";
        new Reader().read(problemName);
    }


    /**
     * 创建二维装箱问题数据场景
     * @param path 数据集名
     * @return     场景对象
     */
    public Bin read(String path) throws Exception {
        logger.info("正载入 -> 二维装箱问题数据集 (" + path + ") ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("binpacking/" + path + ".txt"), "GBK")); // 相对路径
        br.readLine();                                                              // 跳过标题行
        br.readLine();
        String thirdLine = br.readLine();
        String forthLine = br.readLine();
        // 1. 新建箱子
        int binNum    = Integer.valueOf(thirdLine.split("\\s+")[0]);         // 箱子数
        int binWidth  = Integer.valueOf(thirdLine.split("\\s+")[1]);         // 箱子宽度
        int binHeight = Integer.valueOf(thirdLine.split("\\s+")[2]);         // 箱子高度
        int itemNum   = Integer.valueOf(forthLine.split("\\s+")[3]);         // 货物数量
        br.readLine();
        br.readLine();
        Bin bin = new Bin();
        bin.setId(0L);
        bin.setName(path);

        // 2. 统一确定决策变量取值范围
        List<Integer> optionalPriorityList = Arrays.asList(0, itemNum * 2);
        List<Boolean> optionalRotateList = Arrays.asList(false, true);

        // 3. 读取货物数据
        String line;
        double S = 0;
        while((line = br.readLine()) != null) {
            String[] segments = line.split("\\s+");
            long id     = Long.valueOf(segments[0]);
            long width  = Long.valueOf(segments[1]);
            long height = Long.valueOf(segments[2]);
            // 新建货物
            Item item = new Item();
            item.setId(id);
            item.setWidth(width);
            item.setHeight(height);
            item.setBin(bin);
            bin.getItemList().add(item);
            S += width * height;
            // 决策变量及取值范围赋值
            item.setOptionalPriorityList(optionalPriorityList);
            item.setOptionalRotateList(optionalRotateList);
            item.setPriority(bin.getItemList().size());                                     // 决策变量顺序赋初值
            item.setRotate(item.getOptionalRotateList().get(0));                            // 默认不旋转
        }

        /*function ends*/
        double ratio = Math.sqrt(S / binWidth / binHeight);                                 // 等比修正箱子长宽
        bin.setWidth(Math.floor(binWidth * ratio));
        bin.setHeight(Math.floor(binHeight * ratio));
        logger.info("已载入 -> 二维装箱问题数据集 (" + path + ": 货物数: " + itemNum + ", 箱宽: " + bin.getWidth() + ", 箱高: " + bin.getHeight() + ").\n");
        return bin;
    }


/* class ends */
}
