package org.nudtopt.api.tool.comparator;

import org.nudtopt.api.algorithm.operator.Move;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {

    @Override
    public int compare(Move a, Move b) {                      // 1(a大), 0(相等), -1(a小)
        return new ScoreComparator().compare(a.getGapScore(), b.getGapScore());
    }

}
