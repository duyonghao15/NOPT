package org.nudtopt.realworldproblems.planegateassignment.model;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Position extends NumberedObject {

    private long transTime = 5 * 60;                                        // 两架次飞机停靠的最短间隔时间 (秒)
    private boolean close;                                                  // 近机位/远机位
    private Set<String> companySet = new HashSet<>();                       // 可停的航空公司
    private Set<String> typeSet    = new HashSet<>();                       // 可停的机型
    private Set<String> taskSet    = new HashSet<>();                       // 允许的飞行任务
    private Set<Boolean> internationalSet = new HashSet<>();                // 是否允许国内/国际航班
    private List<Window> occupiedTimeList = new ArrayList<>();              // 被占用的时间

    // getter & setter
    public long getTransTime() {
        return transTime;
    }
    public void setTransTime(long transTime) {
        this.transTime = transTime;
    }

    public boolean isClose() {
        return close;
    }
    public void setClose(boolean close) {
        this.close = close;
    }

    public Set<String> getCompanySet() {
        return companySet;
    }
    public void setCompanySet(Set<String> companySet) {
        this.companySet = companySet;
    }

    public Set<String> getTypeSet() {
        return typeSet;
    }
    public void setTypeSet(Set<String> typeSet) {
        this.typeSet = typeSet;
    }

    public Set<String> getTaskSet() {
        return taskSet;
    }
    public void setTaskSet(Set<String> taskSet) {
        this.taskSet = taskSet;
    }

    public Set<Boolean> getInternationalSet() {
        return internationalSet;
    }
    public void setInternationalSet(Set<Boolean> internationalSet) {
        this.internationalSet = internationalSet;
    }

    public List<Window> getOccupiedTimeList() {
        return occupiedTimeList;
    }
    public void setOccupiedTimeList(List<Window> occupiedTimeList) {
        this.occupiedTimeList = occupiedTimeList;
    }


    /**
     * 判断本机位, 是否可停指定飞机
     * @param plane 飞机
     * @return      是否可停
     */
    public boolean allow(Plane plane) {
        if(companySet.size() > 0 && !companySet.contains(plane.getCompany()))                   return false;
        if(typeSet   .size() > 0 && !typeSet.contains(plane.getType()))                         return false;
        if(taskSet   .size() > 0 && !taskSet.contains(plane.getTask()))                         return false;
        if(internationalSet.size() > 0 && !internationalSet.contains(plane.isInternational()))  return false;
        return true;
    }


    @Override
    public String toString() {
        return index != null ? "Position-" + index : super.toString();
    }

}
