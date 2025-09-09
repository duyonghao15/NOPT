package org.nudtopt.classicproblems.jobshopscheduling.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.api.tool.gui.TangoColorFactory;
import org.nudtopt.classicproblems.jobshopscheduling.constraint.Decoder;
import org.nudtopt.classicproblems.jobshopscheduling.model.JobShop;
import org.nudtopt.classicproblems.jobshopscheduling.model.Operation;

import java.awt.*;
import java.util.Comparator;

public class JobShopPanel extends PlotPanel {


    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());                                 // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());                                 // 画黑边框
        JobShop jobShop = (JobShop) solution;                                           // 获得当前解
        jobShop.getConstraint().calScore();                                             // 重新解个码 (更新工序开始结束时间)

        // 1. 记录所有工件的最晚时间
        int makespan = 0;
        for(Operation operation : jobShop.getOperationList()) {
            makespan = Math.max(makespan, (int) operation.getEndTime());
        }

        // 2. 画每个工序的位置
        int yNum = jobShop.getMachineList().size();
        int yDelta = getHeight() / yNum;
        int colorIndex = 0;
        for(Operation operation : jobShop.getOperationList()) {
            int beginTime = (int) operation.getBeginTime();                             // 开始时间
            int duration  = (int) operation.getProcessTime();                           // 持续时间
            int machineIndex = operation.getMachine().getId().intValue();
            int y = getHeight() * machineIndex / jobShop.getMachineList().size();
            int x = getWidth()  * beginTime / makespan;
            int w = getWidth()  * duration  / makespan;
            g.setColor(TangoColorFactory.SEQUENCE_1[colorIndex]);                       // 逐一赋颜色
            g.fillRect(x, y, w, yDelta);                                                // 逐一从不同的x位置开始画矩形
            colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_1.length;        // 颜色周期性递增变化
            g.setColor(new Color(0, 0, 0));
            g.drawRect(x, y, w, yDelta);                                                // 画框
            g.drawString(operation.toString(), x, y + 11);                           // 画标识 (工序名)
        }

        // 3. 画x轴刻度
        int gap = makespan / 30 + 1;                                                    // x坐标最多画30个, 否则就跳过gap个
        for(int i = 0 ; i <= makespan ; i += gap) {                                     // gap递增
            int x = getWidth() * i / makespan;
            g.drawString(String.valueOf(i), x, getHeight());
        }

        // 4. 画y轴刻度
        for(int i = 0 ; i < jobShop.getMachineList().size() ; i ++) {
            double y = getHeight() * (i + 0.5) / jobShop.getMachineList().size();
            g.drawString(jobShop.getMachineList().get(i).toString(), 0, (int) Math.round(y));
        }
        /* function ends */
    }


}
