package org.nudtopt.realworldproblems.apiforsatellite.resource.window;

import java.util.Date;

public class Day extends Window {

    private Date beginDate;             // Date型的开始时间
    private Date endDate;               // Date型的结束时间

    // getter & setter
    public Date getBeginDate() {
        return beginDate;
    }
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
