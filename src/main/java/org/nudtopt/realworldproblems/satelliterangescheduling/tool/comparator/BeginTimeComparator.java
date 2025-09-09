package org.nudtopt.realworldproblems.satelliterangescheduling.tool.comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.Comparator;

public class BeginTimeComparator implements Comparator<Task> {

    @Override
    public int compare(Task a, Task b) {
        return new CompareToBuilder()
                .append(a.getRange().getBeginTime(), b.getRange().getBeginTime())                 // b(1) > a(0), 则a排在前面, b排在后面
                .toComparison();
    }

}
