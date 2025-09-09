package org.nudtopt.classicproblems.binpacking.tool;

import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.classicproblems.binpacking.model.Bin;
import org.nudtopt.classicproblems.binpacking.model.Item;
import org.nudtopt.classicproblems.binpacking.model.SkyLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class SkyLineHeuristic extends Constraint {

    /**
     * 基于天际线启发式方法, 依次放置货物
     * 并计算货物的放置比例
     * @return 评分(放置比例)
     */
    @Override
    public Score calScore() {
        Bin bin = (Bin) solution;                                                                                       // 箱子
        List<Item> itemList = bin.getItemList();                                                                        // 货物清单(及装箱顺序)
        List<Item> placedItemList = new ArrayList<>();                                                                  // 已放置的货物集合
        PriorityBlockingQueue<SkyLine> skyLineList = new PriorityBlockingQueue<>();                                     // 天际线集(最左最下的在首, 自动)
        // 1. 新建初始天际线
        SkyLine initialLine = new SkyLine();
        initialLine.setBin(bin);
        initialLine.setX(0);
        initialLine.setY(0);
        initialLine.setLength(bin.getWidth());
        skyLineList.add(initialLine);
        double s = 0;
        // 2. 遍历货物, 采用天际线启发式依次放置
        for(Item item : itemList) {
            // a. 获取(最下最左)天际线, 并计算左右墙高
            SkyLine floorLine = skyLineList.peek();                                                                     // 最下最左天际线
            double[] leftAndRightHeight = SkyLine.getLeftAndRightHeight(skyLineList);                                   // 获得该天际线左右墙高
            PriorityBlockingQueue<SkyLine> temp = new PriorityBlockingQueue<>(skyLineList);                             // 存下当前天际线, 放置根本无法放置情况下还原天际线
            // b. 判断是否可以放置, 放左 or 右
            String place = place(item, floorLine, leftAndRightHeight[0], leftAndRightHeight[1]);
            while (place == null && item.getHeight() + floorLine.getY() <= bin.getHeight()) {                           // 若无法放置 (在箱高范围内)
                SkyLine.moveUpAndCombine(skyLineList);                                                                  // 则上移(最左最下的)天际线
                floorLine = skyLineList.peek();                                                                         // 更新(最左最下的)天际线
                leftAndRightHeight = SkyLine.getLeftAndRightHeight(skyLineList);                                        // 更新其左右墙高
                place = place(item, floorLine, leftAndRightHeight[0], leftAndRightHeight[1]);                           // 再次判断是否可以放置
            }
            // c. 放置 or 还原
            if(place == null) {
                skyLineList = temp;                                                                                     // 还原天际线
            } else {
                placedItemList.add(item);                                                                               // 放置货物
                s += item.getWidth() * item.getHeight();                                                                // 累加面积
                item.setY(floorLine.getY());
                skyLineList.remove(floorLine);
                SkyLine newLine_1 = new SkyLine();
                newLine_1.setY(floorLine.getY());
                newLine_1.setLength(floorLine.getLength() - item.getWidth());                                           // 原天际线长度裁剪
                newLine_1.setBin(bin);
                if(place.equals("左")) {                                                                                // 若靠左放置
                    item.setX(floorLine.getX());
                    newLine_1.setX(item.getX() + item.getWidth());                                                      // 原天际线被裁剪(起点右移)
                } else {
                    item.setX(floorLine.getX() + floorLine.getLength() - item.getWidth());                              // 若靠右放置
                    newLine_1.setX(floorLine.getX());
                }
                skyLineList.add(newLine_1);                                                                             // 更新原天际线
                SkyLine newLine_2 = new SkyLine();                                                                      // 新增第一条天际线(货物上方)
                newLine_2.setBin(floorLine.getBin());
                newLine_2.setX(item.getX());
                newLine_2.setY(item.getY() + item.getHeight());
                newLine_2.setLength(item.getWidth());
                skyLineList.add(newLine_2);                                                                             // 新加入
            }
        }
        /* function ends */
        double ratio = s / bin.getHeight() / bin.getWidth();
        Score score = new Score();
        score.setMeanScore(placedItemList.size());
        score.setSoftScore(Math.round(ratio * 10000));
        bin.setPlacedItemList(placedItemList);
        return score;
    }



    /**
     * 针对一个货物, 已知一条天际线及左右墙高
     * 判断是否可放置, 以及靠左还是靠右?
     * 原则: 1. 若货高=某墙高, 则贴该墙放
     *       2. 否则, 哪边墙高, 则靠哪边放
     * @param item        货物
     * @param skyLine     天际线
     * @param leftHeight  左墙高
     * @param rightHeight 右墙高
     * @return            是否可放置, 靠左 or 右
     */
    public String place(Item item, SkyLine skyLine, double leftHeight, double rightHeight) {
        // 货宽超过天际线宽, 放不下
        if(item.getWidth() > skyLine.getLength()) {
            return null;
        }
        // 高度超出箱子, 放不下
        if(item.getHeight() + skyLine.getY() > skyLine.getBin().getHeight()) {
            return null;
        }
        if(item.getHeight() == rightHeight)         return "左";             // 1. 若货高==左墙高, 则贴左墙放
        else if (item.getHeight() == leftHeight)    return "右";             // 2. 若货高==右墙高, 则贴右墙放
        else if (leftHeight >= rightHeight)         return "左";             // 3. 若左墙高, 则贴左墙放
        else                                        return "右";             // 4. 若右墙高, 则贴左墙放
    }



}
