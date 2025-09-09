package org.nudtopt.api.tool.comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.nudtopt.api.constraint.Score;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Score> {

    @Override
    public int compare(Score a, Score b) {                      // 1(a大), 0(相等), -1(a小)
        return new CompareToBuilder()
                .append(a.getHardScore(), b.getHardScore())     // 1. 优先比较hard
                .append(a.getMeanScore(), b.getMeanScore())     // 2. 其次比较mean
                .append(a.getSoftScore(), b.getSoftScore())     // 3. 最后比较soft
                .toComparison();
    }

}
