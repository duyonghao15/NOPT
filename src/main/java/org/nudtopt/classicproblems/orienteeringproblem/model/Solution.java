package org.nudtopt.classicproblems.orienteeringproblem.model;

import org.nudtopt.api.model.DecisionEntityList;

import java.util.ArrayList;
import java.util.List;

public class Solution extends org.nudtopt.api.model.Solution {

    @DecisionEntityList
    private List<Vertex> vertexList = new ArrayList<>();

    private List<Tour> tourList = new ArrayList<>();

    private Vertex startVertex;

    // getter & setter
    public List<Vertex> getVertexList() {
        return vertexList;
    }
    public void setVertexList(List<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    public List<Tour> getTourList() {
        return tourList;
    }
    public void setTourList(List<Tour> tourList) {
        this.tourList = tourList;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }
    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

}
