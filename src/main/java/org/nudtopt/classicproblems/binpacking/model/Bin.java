package org.nudtopt.classicproblems.binpacking.model;

import org.nudtopt.api.model.DecisionEntityList;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;

public class Bin extends Solution {

    private double width;
    private double height;

    @DecisionEntityList
    private List<Item> itemList = new ArrayList<>();                                // 放置的货物清单及顺序

    private List<Item> placedItemList = new ArrayList<>();

    // getter & setter
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    public List<Item> getItemList() {
        return itemList;
    }
    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getPlacedItemList() {
        return placedItemList;
    }
    public void setPlacedItemList(List<Item> placedItemList) {
        this.placedItemList = placedItemList;
    }

}
