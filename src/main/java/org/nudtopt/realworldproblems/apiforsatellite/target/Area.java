package org.nudtopt.realworldproblems.apiforsatellite.target;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class Area extends NumberedObject {

    private List<Grid> gridList = new ArrayList<>();        // 区域内所有节点列表

    private List<Stripe> stripeList = new ArrayList<>();    // 条带列表

    private double coverRatio;                              // 覆盖率
    private double overlapRatio;                            // 重叠率
    private int times = 1;                                  // 区域目标与点目标等效的倍率(如1个区域目标 = 5个点目标)

    // getter & setter
    public List<Grid> getGridList() {
        return gridList;
    }
    public void setGridList(List<Grid> gridList) {
        this.gridList = gridList;
    }

    public List<Stripe> getStripeList() {
        return stripeList;
    }
    public void setStripeList(List<Stripe> stripeList) {
        this.stripeList = stripeList;
    }

    public double getCoverRatio() {
        return coverRatio;
    }
    public void setCoverRatio(double coverRatio) {
        this.coverRatio = coverRatio;
    }

    public double getOverlapRatio() {
        return overlapRatio;
    }
    public void setOverlapRatio(double overlapRatio) {
        this.overlapRatio = overlapRatio;
    }

    public int getTimes() {
        return times;
    }
    public void setTimes(int times) {
        this.times = times;
    }


    /** 1
     * 计算覆盖率/重叠率的函数
     * @param stripeList 参与覆盖的条带列表
     */
    public double calCoverRatio(List<Stripe> stripeList) {
        if(stripeList.equals(this.stripeList))      return coverRatio;   // todo 入参条带与区域内已存条带完全一致(已计算过覆盖率), 则不计算
        int[] count = new int[gridList.size()];
        int cover = 0;                                  // 被覆盖的网格数
        int overlap = 0;                                // 重复覆盖的网格数
        for(int i = 0 ; i < gridList.size() ; i ++) {   // 遍历所有网格
            Grid grid = gridList.get(i);
            for(Stripe stripe : stripeList) {           // 遍历所有条带
                if(stripe.cover(grid)) {                // 判断是否覆盖
                    count[i] ++;
                    cover ++;
                    break;                              // todo 不计算重叠率, 此处可直接跳过, 提高效率
                }
            }
        }
        /*for(int c : count) {
            if(c > 0)   cover++;                        // 覆盖数+1
            if(c > 1)   overlap += (c-1);               // 重复覆盖数+
        }*/
        double coverRatio = 1.0 * cover / gridList.size();
        double overlapRatio = 1.0 * overlap / gridList.size();
        this.coverRatio = coverRatio;
        this.overlapRatio = overlapRatio;
        this.stripeList = stripeList;
        return coverRatio;
    }


    /** 2
     * 创建区域
     * @param x    区域顶点坐标经度(顺时针或逆时针)
     * @param y    区域顶点坐标纬度(顺时针或逆时针)
     * @param xNum 经度方向网格化数量
     * @param yNum 纬度方向网格化数量
     */
    public Area createArea(double[] x, double[] y, int xNum, int yNum) {
        // a. 获取矩形边界全部网格
        List<Grid> gridList = createGridList(x, y, xNum, yNum);
        // b. 剔除不在区域顶点范围内的网格
        for(int i = gridList.size() - 1; i >= 0 ; i --) {
            Grid grid = gridList.get(i);
            boolean cover = Tool.cover(x, y, grid.getX(), grid.getY());
            if(!cover) {
                gridList.remove(i);
            }
        }
        // c. 节点重新编号
        for(int i = 0; i < gridList.size() ; i ++) {
            gridList.get(i).setId((long)i);
        }
        // d. 创建区域
        Area area = new Area();
        area.setGridList(gridList);
        return area;
    }


    /** 3
     * 创建多边形区域边框内的全部网格点
     * @param x    区域顶点坐标经度(顺时针或逆时针)
     * @param y    区域顶点坐标纬度(顺时针或逆时针)
     * @param xNum 经度方向网格化数量
     * @param yNum 纬度方向网格化数量
     */
    public List<Grid> createGridList(double[] x, double[] y, int xNum, int yNum) {
        // a. 计算边框坐标
        double xmax = x[0];
        double ymax = y[0];
        double xmin = x[0];
        double ymin = y[0];
        for(int i = 1 ; i < x.length ; i ++) {
            xmax = Math.max(xmax, x[i]);
            ymax = Math.max(ymax, y[i]);
            xmin = Math.min(xmin, x[i]);
            ymin = Math.min(ymin, y[i]);
        }
        // b. 计算节点x/y向宽度
        double totalWidth = xmax - xmin;
        double totalHeight = ymax - ymin;
        double width = totalWidth / xNum;
        double height = totalHeight / yNum;
        // c. 创建节点
        List<Grid> gridList = new ArrayList<>();
        for(int i = 0 ; i < xNum * yNum ; i ++) {
            Grid grid = new Grid();
            grid.setId((long) i);
            grid.setWidth(width);
            grid.setHeight(height);
            double grid_x = xmin + width  * (i % xNum) + width / 2;
            double grid_y = ymin + height * (i / xNum + 1) - height / 2;
            grid.setX(grid_x);
            grid.setY(grid_y);
            gridList.add(grid);
        }
        return gridList;
    }


/* class ends */
}
