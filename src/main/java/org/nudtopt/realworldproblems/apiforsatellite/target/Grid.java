package org.nudtopt.realworldproblems.apiforsatellite.target;

import org.nudtopt.api.model.NumberedObject;

public class Grid extends NumberedObject {

    private double width;                        // x方向宽度
    private double height;                       // y方向高度
    private double x;                            // x坐标(中心点)
    private double y;                            // y坐标(中心点)
    private boolean boundary;                    // 是否是边界网格

    // getter & setter
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
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

    public boolean isBoundary() {
        return boundary;
    }
    public void setBoundary(boolean boundary) {
        this.boundary = boundary;
    }

    @Override
    public Grid clone() {
        try {
            return (Grid) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }   return null;
    }

    @Override
    public String toString() {
        return getClassName() + " (" + x + ", " + y + ")";
    }


    /**
     * 根据经纬度信息, 创建中心网格
     * @autor             杜永浩
     * @param coordinates 经纬度
     * @return            创建后的网格
     */
    public Grid create(String coordinates) {
        String[] xyList = coordinates.split(";");                 // 网格四角坐标列表
        double x = 0;                                                   // 中心(平均)经度
        double y = 0;                                                   // 中心(平均)纬度
        for(int i = 0 ; i < xyList.length ; i ++) {
            x += Double.valueOf(xyList[i].split(",")[0]);         // 经度累加
            y += Double.valueOf(xyList[i].split(",")[1]);         // 纬度累加
        }
        x = x / 4;
        y = y / 4;
        double width  = Math.abs(Double.valueOf(xyList[0].split(",")[0]) - Double.valueOf(xyList[1].split(",")[0]));
        double height = Math.abs(Double.valueOf(xyList[1].split(",")[1]) - Double.valueOf(xyList[2].split(",")[1]));
        Grid grid = new Grid();                                         // 创建网格
        grid.setX(x);
        grid.setY(y);
        grid.setWidth(width);
        grid.setHeight(height);
        return grid;
    }


}
