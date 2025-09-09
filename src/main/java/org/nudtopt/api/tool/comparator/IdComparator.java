package org.nudtopt.api.tool.comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.nudtopt.api.model.NumberedObject;

import java.util.Comparator;

public class IdComparator implements Comparator<NumberedObject> {

    @Override
    public int compare(NumberedObject a, NumberedObject b) {
        return new CompareToBuilder()
                .append(a.getId(), b.getId())                 // b(1) > a(0), 则a排在前面, b排在后面
                .toComparison();
    }


}
