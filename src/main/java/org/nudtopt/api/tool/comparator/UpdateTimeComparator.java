package org.nudtopt.api.tool.comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.nudtopt.api.model.NumberedObject;

import java.util.Comparator;

public class UpdateTimeComparator implements Comparator<NumberedObject> {

    @Override
    public int compare(NumberedObject a, NumberedObject b) {
        return new CompareToBuilder()
                .append(b.getUpdateTime(), a.getUpdateTime())                 // 更新时间降序排列
                .toComparison();
    }


}
