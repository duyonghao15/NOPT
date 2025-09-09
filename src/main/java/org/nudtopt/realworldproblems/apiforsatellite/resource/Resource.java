package org.nudtopt.realworldproblems.apiforsatellite.resource;

import org.nudtopt.api.model.NumberedObject;

public class Resource extends NumberedObject {

    protected String comment;                         // 备注
    protected long capability;                        // 容量/持续时间
    protected boolean available = true;               // 是否可用

    // getter & setter
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCapability() {
        return capability;
    }
    public void setCapability(long capability) {
        this.capability = capability;
    }

    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }


}
