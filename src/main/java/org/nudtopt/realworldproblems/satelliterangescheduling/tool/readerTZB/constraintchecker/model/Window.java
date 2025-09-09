package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model;


/**
 * @author Xu Shilong
 * @version 1.0
 */

public class Window {
    protected long beginTime;                           // 开始时间
    protected long endTime;                             // 结束时间

    public boolean isOverlapping(Window w1) {
        Window w2 = this;
        // 两个占用窗口之间可以有边界的重叠，例如：10-20;20-40;
        // w1在w2开始前就结束了，w2在w1开始前就结束了，以上两种都不满足，说明至少有一部分重叠，则True,
        return !(w1.endTime <= w2.beginTime || w2.endTime <= w1.beginTime);
    }

    public Window(long beginTime, long endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public Window() {
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
