package org.nudtopt.classicproblems.binpacking.model;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.classicproblems.binpacking.tool.SkyLineComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class SkyLine extends NumberedObject {

    private Bin bin;                            // 箱子
    private double x;                           // 天际线左端点x坐标
    private double y;                           // 天际线右端点y坐标
    private double length;                      // 天际线长度

    // getter & setter
    public Bin getBin() {
        return bin;
    }
    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    public double getLength() {
        return length;
    }
    public void setLength(double length) {
        this.length = length;
    }


    /**
     * 排序规则, y越小越优先; y一样时, x越小越优先
     * @param skyLine 另一个天际线
     * @return        compare结果
     */
    @Override
    public int compareTo(NumberedObject skyLine) {
        int compare = Double.compare(this.y, ((SkyLine) skyLine).getY());
        return compare == 0 ? Double.compare(this.x, ((SkyLine) skyLine).getX()) : compare;
    }


    /**
     * 将最左最下的天际线上移,
     * 与其他天际线进行合并 (一定会与一个已有线合并)
     * @param skyLineList 当前天际线集合
     */
    public static PriorityBlockingQueue<SkyLine> moveUpAndCombine(PriorityBlockingQueue<SkyLine> skyLineList) {
        if(skyLineList.size() <= 1)     return skyLineList;
        SkyLine floorLine = skyLineList.poll();
        for(SkyLine skyLine : skyLineList) {
            if(floorLine.getY() <= skyLine.getY()) {
                // 1. skyLine -> floorLine 相连
                if(floorLine.getX() == skyLine.getX() + skyLine.getLength()) {
                    skyLineList.remove(skyLine);
                    SkyLine newLine = new SkyLine();
                    newLine.setX(skyLine.getX());
                    newLine.setY(skyLine.getY());
                    newLine.setLength(skyLine.getLength() + floorLine.getLength());
                    newLine.setBin(skyLine.getBin());
                    skyLineList.add(newLine);
                    return skyLineList;
                } else
                // 2. floorLine -> skyLine 相连
                if(floorLine.getX() + floorLine.getLength() == skyLine.getX()) {
                    skyLineList.remove(skyLine);
                    SkyLine newLine = new SkyLine();
                    newLine.setX(floorLine.getX());
                    newLine.setY(skyLine.getY());
                    newLine.setLength(skyLine.getLength() + floorLine.getLength());
                    newLine.setBin(skyLine.getBin());
                    skyLineList.add(newLine);
                    return skyLineList;
                }
            }
        }
        System.out.println("error: 天际线上移异常");
        return skyLineList;
    }


    /**
     * 基于最左最下的天际线, 计算其左右两侧的墙高度
     * @param skyLineList 当前天际线集
     * @return            左墙高, 右墙高
     */
    public static double[] getLeftAndRightHeight(PriorityBlockingQueue<SkyLine> skyLineList) {
        SkyLine skyLine = skyLineList.peek();
        Bin bin = skyLine.getBin();
        double leftHeight  = -1;
        double rightHeight = -1;
        for(SkyLine line : skyLineList) {
            if(line.getX() + line.getLength() == skyLine.getX()) {              // 1. line -> skyLine 相连, 可计算skyLine的左高
                leftHeight = line.getY() - skyLine.getY();
            } else if (line.getX() == skyLine.getX() + skyLine.getLength()) {   // 2. skyLine -> line 相连, 可计算skyLine的右高
                rightHeight = line.getY() - skyLine.getY();
            }
            if(leftHeight >= 0 && rightHeight >= 0) {
                return new double[]{leftHeight, rightHeight};
            }
        }
        return new double[]{bin.getHeight(), bin.getHeight()};
    }



    /**
     * 检查队列是否正确
     * @param skyLineList
     * @return
     */
    public static boolean check(PriorityBlockingQueue<SkyLine> skyLineList) {
        if(skyLineList.size() == 0)          return true;
        List<SkyLine> list = new ArrayList<>(skyLineList);
        list.sort(new SkyLineComparator());
        return list.get(0) == skyLineList.peek();
    }



    @Override
    public String toString() {
        return "SkyLine{" + "x=" + x + ", y=" + y + ", len=" + length + '}';
    }



/* class ends */
}
