package org.nudtopt.api.constraint;

import org.nudtopt.api.model.DecisionEntity;

import java.util.ArrayList;
import java.util.List;

public class Conflict {

    private List<DecisionEntity> decisionEntityList = new ArrayList<>();    // 冲突实体集

    private String reason;                                                  // 冲突原因

    // getter & setter
    public List<DecisionEntity> getDecisionEntityList() {
        return decisionEntityList;
    }
    public void setDecisionEntityList(List<DecisionEntity> decisionEntityList) {
        this.decisionEntityList = decisionEntityList;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isFeasible() {
        return decisionEntityList == null || decisionEntityList.size() == 0;    // 冲突集为空, 即可行
    }

}
