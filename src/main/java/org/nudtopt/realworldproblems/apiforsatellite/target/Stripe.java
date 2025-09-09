package org.nudtopt.realworldproblems.apiforsatellite.target;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class Stripe extends NumberedObject {

    private List<Grid> gridList;                                // 条带四个边界点列表(顺时针或逆时针)
    private Area area;                                          // 属于哪个区域
    private double extendRatio;                                 // 条带在卫星飞行方向延长比例
    private double movingRatio;                                 // 条带在卫星垂直方向平移比例
    private List<Stripe> subStripeList = new ArrayList<>();     // 子条带列表(当前条带下, 每一秒的条带集合)
    private Map<Long, Stripe> subStripeMap = new HashMap<>();   // 条带前后约30秒的条带数据(每秒), 可供条带延长时精准计算

    // getter & setter
    public List<Grid> getGridList() {
        return gridList;
    }
    public void setGridList(List<Grid> gridList) {
        this.gridList = gridList;
    }

    public Area getArea() {
        return area;
    }
    public void setArea(Area area) {
        this.area = area;
    }

    public double getExtendRatio() {
        return extendRatio;
    }
    public void setExtendRatio(double extendRatio) {
        this.extendRatio = extendRatio;
    }

    public double getMovingRatio() {
        return movingRatio;
    }
    public void setMovingRatio(double movingRatio) {
        this.movingRatio = movingRatio;
    }

    public List<Stripe> getSubStripeList() {
        return subStripeList;
    }
    public void setSubStripeList(List<Stripe> subStripeList) {
        this.subStripeList = subStripeList;
    }

    public Map<Long, Stripe> getSubStripeMap() {
        return subStripeMap;
    }
    public void setSubStripeMap(Map<Long, Stripe> subStripeMap) {
        this.subStripeMap = subStripeMap;
    }

    /** 1
     * 引射线法
     * 判断本条带是否覆盖某坐标
     * @param x0 经度
     * @param y0 纬度
     */
    public boolean cover(double x0, double y0) {
        int crossings = 0;
        for(int i = 0 ; i < gridList.size() ; i ++) {
            double x1 = gridList.get(i).getX();                         // 边的顶点坐标
            double y1 = gridList.get(i).getY();
            double x2 = i != gridList.size() - 1 ? gridList.get(i + 1).getX() : gridList.get(0).getX();
            double y2 = i != gridList.size() - 1 ? gridList.get(i + 1).getY() : gridList.get(0).getY();
            if(x1 == x0 && y1 == y0)    return true;                    // 点在顶点上, 视为包含
            double slope = x2 == x1 ? 999999999 : (y2 - y1) / (x2 - x1);// i和i+1两点的斜率
            double y_cross = slope * (x0 - x1) + y1;
            if(x0 >= Math.min(x1, x2) && x0 <= Math.max(x1, x2)) {      // x0位于x1和x2之间
                if(y0 == y_cross)       return true;                    // 点在边上, 视为包含
                if(y0 <  y_cross)       crossings ++;                   // 向上引射线: 交叉, 顶点数+1
            }
        }
        if(crossings % 2 == 0)          return false;                   // 偶数, 不包含
        else                            return true;                    // 奇数, 包含
    }

    /**
     * 判断本条带, 是否覆盖某网格
     * @param grid 四边形网格
     * @return     是否完全覆盖
     */
    public boolean cover(Grid grid) {
        // 1 覆盖中心点
        // return subCover(grid.getX(),  grid.getY());
        // 2覆盖4点
        boolean cover_1 = subCover(grid.getX() - grid.getWidth()  / 2,  grid.getY() + grid.getHeight() / 2);
        boolean cover_2 = subCover(grid.getX() + grid.getWidth()  / 2,  grid.getY() - grid.getHeight() / 2);
        boolean cover_3 = subCover(grid.getX() - grid.getWidth()  / 2,  grid.getY() - grid.getHeight() / 2);
        boolean cover_4 = subCover(grid.getX() + grid.getWidth()  / 2,  grid.getY() + grid.getHeight() / 2);
        return cover_1 & cover_2 & cover_3 & cover_4;
    }


    /** 2
     * 计算条带面积
     */
    public double getAreas() {
        Grid grid_1 = gridList.get(0);
        Grid grid_2 = gridList.get(1);
        Grid grid_3 = gridList.get(2);
        double length_1 = Math.sqrt(Math.pow(grid_2.getY() - grid_1.getY(), 2) + Math.pow(grid_2.getX() - grid_1.getX(), 2));
        double length_2 = Math.sqrt(Math.pow(grid_3.getY() - grid_2.getY(), 2) + Math.pow(grid_3.getX() - grid_2.getX(), 2));
        double areas    = length_1 * length_2;
        return areas;
    }


    /** 3
     * 条带延长(方位向)函数
     * @param ratio 延长的比例 (延长0.5指变为原来1.5倍)
     */
    public Stripe extend(double ratio) {
        if(ratio == 0)        return this;  // 不延长, 返回自己
        ratio = Math.abs(ratio);            // 比例均为正
        // a. 根据访问计算约定, 1->4, 2->3连线为条带方向, 延长
        double x1 = gridList.get(0).getX(); // 左上, 开始时刻 - 半视场角
        double y1 = gridList.get(0).getY();
        double x2 = gridList.get(1).getX(); // 右上, 开始时刻 + 半视场角
        double y2 = gridList.get(1).getY();
        double x3 = gridList.get(2).getX(); // 右下, 结束时刻 + 半视场角
        double y3 = gridList.get(2).getY();
        double x4 = gridList.get(3).getX(); // 左下, 结束时刻 - 半视场角
        double y4 = gridList.get(3).getY();

        // b. 计算延长后的点
        double new_x4 = x1 + (x4 - x1) * (1 + ratio);      // 1->4
        double new_y4 = y1 + (y4 - y1) * (1 + ratio);
        double new_x3 = x2 + (x3 - x2) * (1 + ratio);      // 2->3
        double new_y3 = y2 + (y3 - y2) * (1 + ratio);
        Grid grid_3_e = new Grid();     grid_3_e.setX(new_x3);      grid_3_e.setY(new_y3);
        Grid grid_4_e = new Grid();     grid_4_e.setX(new_x4);      grid_4_e.setY(new_y4);

        // c. 新建条带
        Stripe stripe = new Stripe();
        stripe.setId(id);
        stripe.setGridList(new ArrayList<>());
        stripe.getGridList().add(gridList.get(0));
        stripe.getGridList().add(gridList.get(1));
        stripe.getGridList().add(grid_3_e);
        stripe.getGridList().add(grid_4_e);
        return stripe;
    }


    /** 3.-------------
     * 原天智算法版: 条带延长(方位向)函数
     * @param ratio 延长的比例 (延长0.5指变为原来1.5倍)
     */
    public Stripe extend(double ratio, boolean rise) {
        if(ratio == 0)        return this;  // 不延长, 返回自己
        ratio = Math.abs(ratio);            // 比例均为正
        // a. 根据接口文档, 1-2, 3-4连线为条带方向, 延长
        Grid grid_1 = gridList.get(0);
        Grid grid_2 = gridList.get(1);
        Grid grid_3 = gridList.get(2);
        Grid grid_4 = gridList.get(3);
        // b. 约定AB, CD为长边, 且AC低, BD高
        Grid gridA;           Grid gridB;
        Grid gridC;           Grid gridD;
        if(grid_1.getY() < grid_2.getY()) { // 13在下, 24在上
            gridA = grid_1;
            gridB = grid_2;
            gridC = grid_4;
            gridD = grid_3;
        } else {                            // 13在上, 24在下
            gridA = grid_2;
            gridB = grid_1;
            gridC = grid_3;
            gridD = grid_4;
        }
        // c. 计算延长顶点(从线的中点开始延长)
        Stripe stripe = new Stripe();
        stripe.setId(id);
        stripe.setGridList(new ArrayList<>());
        Grid gridE = new Grid();
        Grid gridF = new Grid();
        if(rise) {  // A->B->E / C->D->F
            gridE.setX(gridB.getX() + (gridB.getX() - gridA.getX()) * ratio);
            gridE.setY(gridB.getY() + (gridB.getY() - gridA.getY()) * ratio);
            gridF.setX(gridD.getX() + (gridD.getX() - gridC.getX()) * ratio);
            gridF.setY(gridD.getY() + (gridD.getY() - gridC.getY()) * ratio);
            stripe.getGridList().add(gridA);
            stripe.getGridList().add(gridE);
            stripe.getGridList().add(gridF);
            stripe.getGridList().add(gridC);
        } else {    // B->A->E / D->C->F
            gridE.setX(gridA.getX() + (gridA.getX() - gridB.getX()) * ratio);
            gridE.setY(gridA.getY() + (gridA.getY() - gridB.getY()) * ratio);
            gridF.setX(gridC.getX() + (gridC.getX() - gridD.getX()) * ratio);
            gridF.setY(gridC.getY() + (gridC.getY() - gridD.getY()) * ratio);
            stripe.getGridList().add(gridB);
            stripe.getGridList().add(gridE);
            stripe.getGridList().add(gridF);
            stripe.getGridList().add(gridD);
        }
        return stripe;
    }


    /** 4
     * 条带平移(距离向)函数
     * @param ratio 平移比例 (>0)
     */
    public Stripe move(double ratio) {
        if(ratio == 0)        return this;  // 不延长, 返回自己
        // a. 根据接口文档, 1-2, 3-4连线为条带方向, 延长
        Grid grid_1 = gridList.get(0);
        Grid grid_2 = gridList.get(1);
        Grid grid_3 = gridList.get(2);
        Grid grid_4 = gridList.get(3);

        // b. 约定1->A, 2->B, 3->C, 4->D, 计算平移后顶点
        Grid gridA = new Grid();    Grid gridD = new Grid();
        Grid gridB = new Grid();    Grid gridC = new Grid();
        gridA.setX(grid_1.getX() + ratio * Math.abs(grid_1.getX() - grid_4.getX()));
        gridA.setY(grid_1.getY() + ratio * Math.abs(grid_1.getY() - grid_4.getY()));
        gridB.setX(grid_2.getX() + ratio * Math.abs(grid_2.getX() - grid_3.getX()));
        gridB.setY(grid_2.getY() + ratio * Math.abs(grid_2.getY() - grid_3.getY()));
        gridC.setX(grid_3.getX() + ratio * Math.abs(grid_2.getX() - grid_3.getX()));
        gridC.setY(grid_3.getY() + ratio * Math.abs(grid_2.getY() - grid_3.getY()));
        gridD.setX(grid_4.getX() + ratio * Math.abs(grid_1.getX() - grid_4.getX()));
        gridD.setY(grid_4.getY() + ratio * Math.abs(grid_1.getY() - grid_4.getY()));

        // c. 生成新的条带
        Stripe stripe = new Stripe();
        stripe.setId(id);
        stripe.setGridList(new ArrayList<>());
        stripe.getGridList().add(gridA);
        stripe.getGridList().add(gridB);
        stripe.getGridList().add(gridC);
        stripe.getGridList().add(gridD);
        return stripe;
    }


    /** 5
     * 条带缩放函数
     * @param ratio 缩放比例
     */
    public Stripe scale(double ratio) {
        double x_1 = gridList.get(0).getX();
        double y_1 = gridList.get(0).getY();
        double x_2 = gridList.get(1).getX();
        double y_2 = gridList.get(1).getY();
        double x_3 = gridList.get(2).getX();
        double y_3 = gridList.get(2).getY();
        double x_4 = gridList.get(3).getX();
        double y_4 = gridList.get(3).getY();
        ratio = (1 - ratio) / 2; // 单边调整比例
        // 1-3 x缩放
        double x_1_new = x_1 + (x_3 - x_1) * ratio;
        double x_3_new = x_3 - (x_3 - x_1) * ratio;
        // 1-3 y缩放
        double y_1_new = y_1 + (y_3 - y_1) * ratio;
        double y_3_new = y_3 - (y_3 - y_1) * ratio;
        // 4-2 x缩放
        double x_2_new = x_2 + (x_4 - x_2) * ratio;
        double x_4_new = x_4 - (x_4 - x_2) * ratio;
        // 4-2 y缩放
        double y_2_new = y_2 + (y_4 - y_2) * ratio;
        double y_4_new = y_4 - (y_4 - y_2) * ratio;
        // 更新坐标
        Grid grid_1 = new Grid();
        Grid grid_2 = new Grid();
        Grid grid_3 = new Grid();
        Grid grid_4 = new Grid();
        grid_1.setX(x_1_new);
        grid_1.setY(y_1_new);
        grid_2.setX(x_2_new);
        grid_2.setY(y_2_new);
        grid_3.setX(x_3_new);
        grid_3.setY(y_3_new);
        grid_4.setX(x_4_new);
        grid_4.setY(y_4_new);
        // 新建条带
        Stripe stripe = new Stripe();
        stripe.setId(id);
        stripe.setGridList(new ArrayList<>());
        stripe.getGridList().add(grid_1);
        stripe.getGridList().add(grid_2);
        stripe.getGridList().add(grid_3);
        stripe.getGridList().add(grid_4);
        return stripe;
    }

    /**
     * 方位向缩放
     * @param longRatio 方位向缩放比例
     * @param latRatio  距离向缩放方向
     */
    public Stripe scale(double longRatio, double latRatio) {
        double x1 = gridList.get(0).getX();
        double y1 = gridList.get(0).getY();
        double x2 = gridList.get(1).getX();
        double y2 = gridList.get(1).getY();
        double x3 = gridList.get(2).getX();
        double y3 = gridList.get(2).getY();
        double x4 = gridList.get(3).getX();
        double y4 = gridList.get(3).getY();
        double[] points_1 = Tool.scale(x1, y1, x2, y2, x3, y3, x4, y4, longRatio, "1->4");  // 方位向缩放
        double[] points_2 = Tool.scale(points_1[0], points_1[1], points_1[2], points_1[3], points_1[4], points_1[5], points_1[6], points_1[7], latRatio, "1->2");
        // 更新坐标
        Grid grid_1 = new Grid();
        Grid grid_2 = new Grid();
        Grid grid_3 = new Grid();
        Grid grid_4 = new Grid();
        grid_1.setX(points_2[0]);
        grid_1.setY(points_2[1]);
        grid_2.setX(points_2[2]);
        grid_2.setY(points_2[3]);
        grid_3.setX(points_2[4]);
        grid_3.setY(points_2[5]);
        grid_4.setX(points_2[6]);
        grid_4.setY(points_2[7]);
        // 新建条带
        Stripe stripe = new Stripe();
        stripe.setId(id);
        stripe.setGridList(new ArrayList<>());
        stripe.getGridList().add(grid_1);
        stripe.getGridList().add(grid_2);
        stripe.getGridList().add(grid_3);
        stripe.getGridList().add(grid_4);
        return stripe;
    }


    /** 6
     * 获取条带四点坐标
     * @return 字符串(小数点后4位)
     */
    public String getCoordinates() {
        int n = 4;
        return Tool.round(gridList.get(0).getX(), n) + ", " + Tool.round(gridList.get(0).getY(), n) + ", " +
               Tool.round(gridList.get(1).getX(), n) + ", " + Tool.round(gridList.get(1).getY(), n) + ", " +
               Tool.round(gridList.get(2).getX(), n) + ", " + Tool.round(gridList.get(2).getY(), n) + ", " +
               Tool.round(gridList.get(3).getX(), n) + ", " + Tool.round(gridList.get(3).getY(), n);
    }



    /**
     * 创建天智杯蜈蚣数据(前后约30s条带数据)
     * @param dataList input中的蜈蚣数据
     * @param beginTime 创建子条带的开始时间(不是全部子条带都创建)
     * @param endTime   创建子条带的结束时间
     */
    public Map<Long, Stripe> createSubStripeMap(List<List<String>> dataList, Long beginTime, Long endTime) {
        Map<Long, Stripe> subStripeMap = new HashMap<>();
        if(dataList  == null || dataList.size() == 0)    return subStripeMap;
        if(beginTime == null)   beginTime = 0L;
        if(endTime   == null)   endTime   = (long) Infinity;
        for(List<String> data : dataList) {     // 遍历每一秒时间
            long time = Long.valueOf(data.get(0));
            if(time < beginTime)    continue;
            if(time > endTime)      break;
            Grid grid_1 = new Grid();
            Grid grid_2 = new Grid();
            Grid grid_3 = new Grid();
            Grid grid_4 = new Grid();
            grid_1.setX(Double.valueOf(data.get(1).split(",")[0]));
            grid_1.setY(Double.valueOf(data.get(1).split(",")[1]));
            grid_2.setX(Double.valueOf(data.get(2).split(",")[0]));
            grid_2.setY(Double.valueOf(data.get(2).split(",")[1]));
            grid_3.setX(Double.valueOf(data.get(3).split(",")[0]));
            grid_3.setY(Double.valueOf(data.get(3).split(",")[1]));
            grid_4.setX(Double.valueOf(data.get(4).split(",")[0]));
            grid_4.setY(Double.valueOf(data.get(4).split(",")[1]));
            Stripe subStripe = new Stripe();
            subStripe.setId((long)subStripeMap.size());
            subStripe.setGridList(new ArrayList<>());
            subStripe.getGridList().add(grid_1);
            subStripe.getGridList().add(grid_2);
            subStripe.getGridList().add(grid_3);
            subStripe.getGridList().add(grid_4);
            subStripeMap.put(time, subStripe);
        }
        return this.subStripeMap = subStripeMap;
    }


    /**
     * 基于延长率和天智杯蜈蚣数据(前后约30s条带数据), 获得延长后的条带
     * @param extendRatio 条带延长率
     * @return            延长后的条带
     */
    public Stripe getExtendStripe(long beginTime, long duration, double extendRatio) {
        long newDuration = (long)(duration * (1 + extendRatio));
        long endTime = beginTime + newDuration;
        Stripe endSubStripe = subStripeMap.get(endTime);                // 获得结束时间的子条带
        if(endSubStripe == null) {
            return null;
        }
        Stripe stripe = new Stripe();
        stripe.setGridList(new ArrayList<>());
        stripe.getGridList().add(gridList.get(0));                      // 原条带的前两个
        stripe.getGridList().add(gridList.get(1));
        stripe.getGridList().add(endSubStripe.getGridList().get(2));    // 蜈蚣条带的后两个
        stripe.getGridList().add(endSubStripe.getGridList().get(3));    // 拼成一个新的延长后的条带
        stripe.setSubStripeMap(subStripeMap);                           // 蜈蚣数据同时也赋值
        stripe.createSubStripeList(beginTime, endTime);                 // 创建每秒条带数据
        return stripe;
    }


    /**
     * 根据天智杯蜈蚣条带数据, 获取本条带的子条带(每1秒的小条带), 用户精确化判断覆盖情况
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return          每一秒的子条带
     */
    public List<Stripe> createSubStripeList(long beginTime, long endTime) {
        List<Stripe> subStripeList = new ArrayList<>();
        if(subStripeMap == null || subStripeMap.size() == 0)        return subStripeList;
        for(long time = beginTime ; time <= endTime ; time ++) {    // 从开始时间遍历到结束时间
            Stripe subStripe = subStripeMap.get(time);              // 取出相应的1秒的子条带
            if(subStripe != null) {                                 // 不能为null
                subStripeList.add(subStripe);
            }
        }
        return this.subStripeList = subStripeList;
    }



    /** 1
     * 逐秒判断本条带是否覆盖某坐标
     * @param x0 经度
     * @param y0 纬度
     */
    public boolean subCover(double x0, double y0) {
        if(subStripeList == null || subStripeList.size() == 0) {
            return cover(x0, y0);                   // 若无子条带(每秒)数据, 则用原cover方法
        }
        boolean cover = false;
        for(Stripe subStripe : subStripeList) {     // 逐秒遍历子条带
            if(subStripe.cover(x0, y0)) {           // 若任意1秒子条带覆盖目标
                cover = true;                       // 则视为覆盖
                break;
            }
        }
        return cover;
    }


/* class ends */
}
