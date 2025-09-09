package org.nudtopt.realworldproblems.planegateassignment.model;

import org.nudtopt.api.model.DecisionEntityList;

import java.util.ArrayList;
import java.util.List;

public class Solution extends org.nudtopt.api.model.Solution {

    @DecisionEntityList
    private List<Plane> planeList = new ArrayList<>();

    private List<Position> positionList = new ArrayList<>();

    // getter & setter
    public List<Plane> getPlaneList() {
        return planeList;
    }
    public void setPlaneList(List<Plane> planeList) {
        this.planeList = planeList;
    }

    public List<Position> getPositionList() {
        return positionList;
    }
    public void setPositionList(List<Position> positionList) {
        this.positionList = positionList;
    }
}
