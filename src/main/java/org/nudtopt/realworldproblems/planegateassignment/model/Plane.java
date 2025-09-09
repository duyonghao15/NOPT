package org.nudtopt.realworldproblems.planegateassignment.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.realworldproblems.apiforsatellite.tool.interval.Interval;

import java.util.*;

public class Plane extends DecisionEntity {

    private String company;                                                 // 航空公司编号
    private long inTime;                                                    // 进场时间 (秒)
    private long outTime;                                                   // 离场时间 (秒)
    private Date inDate;                                                    // 进场时间 (时间格式)
    private Date outDate;                                                   // 离场时间 (时间格式)
    private String task;                                                    // 飞行任务
    private boolean international;                                          // 是否为国际航班
    private List<Plane> possibleConflictPlaneList = new ArrayList<>();      // 可能存在冲突(时间上存在重叠)的飞机列表

    @DecisionVariable(nullable = true)
    private Position position;                                              // 决策变量: 停机位

    private List<Position> optionalPositionList = new ArrayList<>();        // 决策变量取值范围: 可停的机位

    // getter & setter
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public long getInTime() {
        return inTime;
    }
    public void setInTime(long inTime) {
        this.inTime = inTime;
    }

    public long getOutTime() {
        return outTime;
    }
    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public Date getInDate() {
        return inDate;
    }
    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Date getOutDate() {
        return outDate;
    }
    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }

    public boolean isInternational() {
        return international;
    }
    public void setInternational(boolean international) {
        this.international = international;
    }

    public List<Plane> getPossibleConflictPlaneList() {
        return possibleConflictPlaneList;
    }
    public void setPossibleConflictPlaneList(List<Plane> possibleConflictPlaneList) {
        this.possibleConflictPlaneList = possibleConflictPlaneList;
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Position> getOptionalPositionList() {
        return optionalPositionList;
    }
    public void setOptionalPositionList(List<Position> optionalPositionList) {
        this.optionalPositionList = optionalPositionList;
    }


    /**
     * 判断两架次飞机是否冲突
     * @param plane 另一架次飞机
     * @return      是否冲突
     */
    public boolean conflict(Plane plane) {
        if(this == plane)                                       return false;                                           // 自己和自己不冲突
        if(position != plane.getPosition())                     return false;                                           // 不在一个机位, 不可能冲突
        if(position == null || plane.getPosition() == null)     return false;                                           // 都没停机, 也不冲突
        long transTime = position.getTransTime();                                                                       // 两架飞机停机中间所需的间隔时间
        double interval = Interval.getIntervalTime(inTime, outTime, plane.getInTime(), plane.getOutTime());             // 时间间隔
        return interval < transTime;
    }


    /**
     * 判断两架次飞机是否可能存在冲突
     * @param plane 另一架次飞机
     * @return      是否可能冲突
     */
    public boolean possibleConflict(Plane plane) {
        if(this == plane)                                                                    return false;              // 自己和自己不冲突
        if(Collections.disjoint(optionalPositionList, plane.getOptionalPositionList()))      return false;              // 若可停机位无交集, 则不可能冲突
        long transTime = 5 * 60;                                                                                        // 两架飞机停机中间所需的间隔时间
        double interval = Interval.getIntervalTime(inTime, outTime, plane.getInTime(), plane.getOutTime());             // 时间间隔
        return interval < transTime;
    }


/* class ends */
}
