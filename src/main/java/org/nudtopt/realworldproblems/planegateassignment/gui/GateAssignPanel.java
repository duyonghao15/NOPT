package org.nudtopt.realworldproblems.planegateassignment.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.api.tool.gui.TangoColorFactory;
import org.nudtopt.realworldproblems.planegateassignment.model.Plane;
import org.nudtopt.realworldproblems.planegateassignment.model.Position;
import org.nudtopt.realworldproblems.planegateassignment.model.Solution;

import java.awt.*;
import java.util.List;

public class GateAssignPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());                                 // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());                                 // 画黑边框
        Solution sol = (Solution) solution;                                             // 获得当前解
        long minTime = 99999999999L;
        long maxTime = 0;
        for(Plane plane : sol.getPlaneList()) {
            if(plane.getPosition() == null) continue;
            minTime = Math.min(minTime, plane.getInTime());
            maxTime = Math.max(maxTime, plane.getOutTime());
        }
        minTime = (int) (maxTime - (maxTime - minTime) / 1.9);                          // 存在一些异常数据, 导致场景过长
        long makespan = maxTime - minTime;
        int height = getHeight() / sol.getPositionList().size();

        // 1. 遍历飞机, 画停机位置
        int colorIndex = 0;
        for(Plane plane : sol.getPlaneList()) {
            Position position = plane.getPosition();
            if(position == null)           continue;
            long beginTime = plane.getInTime();
            long endTime   = plane.getOutTime();
            long duration  = endTime - beginTime;
            int x = Math.round(getWidth()  * (beginTime - minTime) / makespan);
            int y = Math.round(getHeight() * position.getId() / sol.getPositionList().size());
            int width = Math.round(getWidth() * duration / makespan);
            g.setColor(TangoColorFactory.SEQUENCE_1[colorIndex]);                       // 逐一赋颜色
            g.fillRect(x, y, width, height);                                            // 逐一从不同的x位置开始画矩形
            colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_1.length;        // 颜色周期性递增变化
            g.setColor(new Color(0, 0, 0));
            g.drawRect(x, y, width, height);                                            // 画框
            // g.drawString(plane.getId().toString(), x, y + 11);                       // 画标识 (工序名)
        }

        /*// 3. 画x轴刻度
        int gap = makespan / 30 + 1;                                                    // x坐标最多画30个, 否则就跳过gap个
        for(int i = 0 ; i <= makespan ; i += gap) {                                     // gap递增
            int x = getWidth() * i / makespan;
            g.drawString(String.valueOf(i), x, getHeight());
        }*/

        /*// 4. 画y轴刻度
        for(int i = 0 ; i < positionList.size() ; i ++) {
            double y = getHeight() * (i + 0.5) / positionList.size();
            g.drawString(positionList.get(i).toString(), 0, (int) Math.round(y));
        }*/
        /* function ends */
    }


}
