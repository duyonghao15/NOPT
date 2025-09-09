package org.nudtopt.classicproblems.orienteeringproblem.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.ArrayList;
import java.util.List;

public class Tour extends NumberedObject {

    private Vertex startVertex;                             // 起点
    private List<Vertex> vertexList = new ArrayList<>();    // 访问的顺序: 起点 -> vertexList -> 起点

    // getter & setter
    public Vertex getStartVertex() {
        return startVertex;
    }
    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public List<Vertex> getVertexList() {
        return vertexList;
    }
    public void setVertexList(List<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

}
