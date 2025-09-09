package org.nudtopt.realworldproblems.apiforsatellite.resource.payload;

import org.nudtopt.realworldproblems.apiforsatellite.resource.Resource;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Platform;

public class Payload extends Resource {

    private Platform platform;                      // 载荷属于哪个平台, 测站/卫星/中继星
    private int minSingleWorkTime = 0;              // 载荷单次最短工作时间
    private int maxSingleWorkTime = 99999;          // 载荷单次最大工作时间
    private int maxOrbitWorkNum   = 99999;          // 单轨最大工作次数
    private int maxOrbitWorkTime  = 99999;          // 单轨最大工作时长
    private int maxOrbitWorkTimeInShadow = 99999;   // 单轨地影区最大工作时长
    private int maxDailyWorkNum   = 99999;          // 单日最大工作次数
    private int maxDailyWorkTime  = 99999;          // 单日最大工作时间

    // getter & setter
    public Platform getPlatform() {
        return platform;
    }
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public int getMinSingleWorkTime() {
        return minSingleWorkTime;
    }
    public void setMinSingleWorkTime(int minSingleWorkTime) {
        this.minSingleWorkTime = minSingleWorkTime;
    }

    public int getMaxSingleWorkTime() {
        return maxSingleWorkTime;
    }
    public void setMaxSingleWorkTime(int maxSingleWorkTime) {
        this.maxSingleWorkTime = maxSingleWorkTime;
    }

    public int getMaxOrbitWorkNum() {
        return maxOrbitWorkNum;
    }
    public void setMaxOrbitWorkNum(int maxOrbitWorkNum) {
        this.maxOrbitWorkNum = maxOrbitWorkNum;
    }

    public int getMaxOrbitWorkTime() {
        return maxOrbitWorkTime;
    }
    public void setMaxOrbitWorkTime(int maxOrbitWorkTime) {
        this.maxOrbitWorkTime = maxOrbitWorkTime;
    }

    public int getMaxOrbitWorkTimeInShadow() {
        return maxOrbitWorkTimeInShadow;
    }
    public void setMaxOrbitWorkTimeInShadow(int maxOrbitWorkTimeInShadow) {
        this.maxOrbitWorkTimeInShadow = maxOrbitWorkTimeInShadow;
    }

    public int getMaxDailyWorkNum() {
        return maxDailyWorkNum;
    }
    public void setMaxDailyWorkNum(int maxDailyWorkNum) {
        this.maxDailyWorkNum = maxDailyWorkNum;
    }

    public int getMaxDailyWorkTime() {
        return maxDailyWorkTime;
    }
    public void setMaxDailyWorkTime(int maxDailyWorkTime) {
        this.maxDailyWorkTime = maxDailyWorkTime;
    }

    @Override
    public String toString() {
        return getClassName() + "-" + (name == null ? id : name);
    }
}
