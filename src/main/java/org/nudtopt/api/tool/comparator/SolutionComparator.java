package org.nudtopt.api.tool.comparator;

import org.nudtopt.api.model.Solution;

import java.util.Comparator;

public class SolutionComparator implements Comparator<Solution> {

    @Override                      // 降序: 大的在前, 小的在后
    public int compare(Solution a, Solution b) {
        return b.getScore().compareTo(a.getScore());
    }

}
