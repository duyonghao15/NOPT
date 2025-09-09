package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model;


/**
 * @author Xu Shilong
 * @version 1.0
 */

public class Arc {
    private long id;                // 测控弧段的id
    private long dayIndex;          // 弧段所在天的index
    private long orbitIndex;        // 被测控的卫星的轨道index
    private long satelliteIndex;    // 被测控的卫星的index
    private long antennaIndex;      // 测控弧段的天线id
    private boolean riseFallFlag;   // 升降轨标记
    private long orbitInChinaIndex;  // 在中国境内的圈号：1入境 Num出境
    private long orbitInChinaNum;    // 在中国境内的连续圈的数量
    private long startTime;         // 弧段开始时间
    private long endTime;           // 弧段结束时间
    private double elevation;       // 地面天线的最大仰角
    private boolean effective;      // 是否有效 (有一些根据约束无效)


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(long dayIndex) {
        this.dayIndex = dayIndex;
    }

    public long getOrbitIndex() {
        return orbitIndex;
    }

    public void setOrbitIndex(long orbitIndex) {
        this.orbitIndex = orbitIndex;
    }

    public long getSatelliteIndex() {
        return satelliteIndex;
    }

    public void setSatelliteIndex(long satelliteIndex) {
        this.satelliteIndex = satelliteIndex;
    }

    public long getAntennaIndex() {
        return antennaIndex;
    }

    public void setAntennaIndex(long antennaIndex) {
        this.antennaIndex = antennaIndex;
    }

    public boolean isRiseFallFlag() {
        return riseFallFlag;
    }

    public void setRiseFallFlag(boolean riseFallFlag) {
        this.riseFallFlag = riseFallFlag;
    }

    public long getOrbitInChinaIndex() {
        return orbitInChinaIndex;
    }

    public void setOrbitInChinaIndex(long orbitInChinaIndex) {
        this.orbitInChinaIndex = orbitInChinaIndex;
    }

    public long getOrbitInChinaNum() {
        return orbitInChinaNum;
    }

    public void setOrbitInChinaNum(long orbitInChinaNum) {
        this.orbitInChinaNum = orbitInChinaNum;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public boolean isEffective() {
        return effective;
    }

    public void setEffective(boolean effective) {
        this.effective = effective;
    }


    public Arc(long id, long dayIndex, long orbitIndex, long satelliteIndex, long antennaIndex, boolean riseFallFlag, long orbitInChinaIndex, long orbitInChinaNum, long startTime, long endTime, double elevation, boolean effective) {
        this.id = id;
        this.dayIndex = dayIndex;
        this.orbitIndex = orbitIndex;
        this.satelliteIndex = satelliteIndex;
        this.antennaIndex = antennaIndex;
        this.riseFallFlag = riseFallFlag;
        this.orbitInChinaIndex = orbitInChinaIndex;
        this.orbitInChinaNum = orbitInChinaNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.elevation = elevation;
        this.effective = effective;
    }
}
