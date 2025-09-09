package org.nudtopt.realworldproblems.apiforsatellite.tool.interval;


public class Interval {

    //  ############################################################################
    //  #######################  A. 计算相邻时间区间的间隔时间  ######################
    //  ############################################################################

    public static double getIntervalTime(double beginTime1, double endTime1, double beginTime2, double endTime2) {
        if(beginTime2 >= endTime1)  return beginTime2 - endTime1;     // 1-2的间隔时间 (>0)
        if(beginTime1 >= endTime2)  return beginTime1 - endTime2;     // 2-1的间隔时间 (>0)
        if(beginTime2 < endTime1)   return beginTime2 - endTime1;     // 1-2的重叠时间 (<0)
        else                        return beginTime1 - endTime2;     // 2-1的重叠时间 (<0)
    }

/* class ends */
}
