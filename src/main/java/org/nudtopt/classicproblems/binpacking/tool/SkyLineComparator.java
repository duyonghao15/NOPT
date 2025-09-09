package org.nudtopt.classicproblems.binpacking.tool;

import org.nudtopt.classicproblems.binpacking.model.SkyLine;

import java.util.Comparator;

public class SkyLineComparator implements Comparator<SkyLine> {

    @Override
    public int compare(SkyLine skyLine1, SkyLine skyLine2) {
        int compare = Double.compare(skyLine1.getY(),skyLine2.getY());                      // 排序规则: y越小越优先
        return compare == 0 ? Double.compare(skyLine1.getX(), skyLine2.getX()) : compare;   // y一样时, x越小越优先
    }


}
