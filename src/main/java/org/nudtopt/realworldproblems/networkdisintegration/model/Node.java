package org.nudtopt.realworldproblems.networkdisintegration.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Node extends DecisionEntity {

    private List<Link> linkList = new ArrayList<>();                        // 以本node为起点, 向往连接的边

    @DecisionVariable
    private Boolean disintegrate = false;                                   // 决策变量: 是否被瓦解

    private List<Boolean> optionalDisintegrateList = new ArrayList<>();     // 决策变量取值范围

    // getter & setter
    public List<Link> getLinkList() {
        return linkList;
    }
    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public Boolean getDisintegrate() {
        return disintegrate;
    }
    public void setDisintegrate(Boolean disintegrate) {
        this.disintegrate = disintegrate;
    }

    public List<Boolean> getOptionalDisintegrateList() {
        return optionalDisintegrateList;
    }
    public void setOptionalDisintegrateList(List<Boolean> optionalDisintegrateList) {
        this.optionalDisintegrateList = optionalDisintegrateList;
    }

}
