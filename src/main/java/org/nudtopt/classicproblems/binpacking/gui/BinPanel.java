package org.nudtopt.classicproblems.binpacking.gui;

import org.nudtopt.api.tool.gui.PlotPanel;
import org.nudtopt.api.tool.gui.TangoColorFactory;
import org.nudtopt.classicproblems.binpacking.model.Bin;
import org.nudtopt.classicproblems.binpacking.model.Item;

import java.awt.*;

public class BinPanel extends PlotPanel {

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());                             // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());                             // 画黑边框
        Bin bin = (Bin) solution;                                                   // 获得当前解
        bin.getConstraint().calScore();                                             // 重新解个码 (更新货物位置)

        // 1. 确定缩放比例, 画箱子
        double xRatio = getWidth()  / bin.getWidth();
        double yRatio = getHeight() / bin.getHeight();
        double ratio = Math.floor(Math.min(xRatio, yRatio));
        int binWidth = (int) Math.round(bin.getWidth()  * ratio);
        int binHeight= (int) Math.round(bin.getHeight() * ratio);
        int delta_X = (getWidth()  - binWidth) / 2;
        int delta_Y = (getHeight() - binHeight) / 2;
        g.drawRect(delta_X, delta_Y, binWidth, binHeight);

        // 2. 依次画货物
        for(int i = 0 ; i < bin.getPlacedItemList().size() ; i ++) {
            Item item = bin.getPlacedItemList().get(i);
            int x = (int) Math.round(item.getX() * ratio);
            int y = (int) Math.round(item.getY() * ratio);
            int itemWidth  = (int) Math.round(item.getWidth()  * ratio);
            int itemHeight = (int) Math.round(item.getHeight() * ratio);
            int colorIndex = (i + 1) % TangoColorFactory.SEQUENCE_1.length;         // 颜色周期性递增变化
            g.setColor(TangoColorFactory.SEQUENCE_1[colorIndex]);                   // 逐一赋颜色
            g.fillRect(delta_X + x, delta_Y + binHeight - y - itemHeight, itemWidth, itemHeight);
            g.setColor(new Color(0, 0, 0));
            g.drawRect(delta_X + x, delta_Y + binHeight - y - itemHeight, itemWidth, itemHeight);
            g.drawString("No. " + (i+1), delta_X + x, delta_Y + binHeight - y);
        }
    }

    @Override
    public String getName() {
        return "装箱图";
    }

}
