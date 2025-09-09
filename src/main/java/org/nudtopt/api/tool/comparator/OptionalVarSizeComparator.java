package org.nudtopt.api.tool.comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.nudtopt.api.model.DecisionEntity;

import java.util.Comparator;
import java.util.List;

public class OptionalVarSizeComparator implements Comparator<DecisionEntity> {

    @Override
    public int compare(DecisionEntity a, DecisionEntity b) {
        List<String> nameListA = a.getDecisionVariableList();
        List<String> nameListB = b.getDecisionVariableList();

        int varOptionSizeA = 0;
        for(String name : nameListA) {
            try {
                varOptionSizeA += a.getOptionalDecisionVariableList(name).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int varOptionSizeB = 0;
        for(String name : nameListB) {
            try {
                varOptionSizeB += b.getOptionalDecisionVariableList(name).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new CompareToBuilder()
                .append(varOptionSizeA, varOptionSizeB)      // varOptionSize升序
                .toComparison();
    }


/* class ends */
}
