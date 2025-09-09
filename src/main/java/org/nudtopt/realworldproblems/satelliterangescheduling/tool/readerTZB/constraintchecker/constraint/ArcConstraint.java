package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint;

import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.Arc;

/**
 * 记录TZB的任务约束
 *
 * @author Xu Shilong
 * @version 1.0
 */


public class ArcConstraint<T extends Arc> implements Constraint<T> {
    private int    constrainType;
    private double MinimumMaxElevation;
    private long   scopeDefinition0;
    private long   scopeDefinition1;
    private long   scopeDefinition2;

    /**
     * 检查一个弧段是否满足此约束记录所规定的约束
     *
     * @param arc
     * @return
     */
    public boolean boolCheck(T arc) {
        if (arc.getElevation() < this.MinimumMaxElevation)
            return false;
        switch (this.constrainType) {
            default:
            case 0:
                /* do nothing */
                return true;
            case 1:
                /* do nothing */
                return true;
            case 2:
                long time = Math.abs(arc.getStartTime() - (this.scopeDefinition1 + 43200)) % 86400 - 43200;
                return time >= -this.scopeDefinition0 && time <= this.scopeDefinition2;
            case 3:
                /* do nothing */
                return true;
            case 4:
                if (this.scopeDefinition1 < 50) {
                    return arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 + this.scopeDefinition2;
                } else {
                    return arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() + this.scopeDefinition2;
                }
            case 5:
                if (this.scopeDefinition1 < 50) {
                    return arc.isRiseFallFlag() &&
                            arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 + this.scopeDefinition2;
                } else {
                    return arc.isRiseFallFlag() &&
                            arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() + this.scopeDefinition2;
                }
            case 6:
                if (this.scopeDefinition1 < 50) {
                    return !arc.isRiseFallFlag() &&
                            arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 + this.scopeDefinition2;
                } else {
                    return !arc.isRiseFallFlag() &&
                            arc.getOrbitInChinaIndex() >= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() - this.scopeDefinition0 &&
                            arc.getOrbitInChinaIndex() <= this.scopeDefinition1 - 90 + arc.getOrbitInChinaNum() + this.scopeDefinition2;
                }
        }
    }


    public ArcConstraint(int constrainType, double minimumMaxElevation, long scopeDefinition0, long scopeDefinition1, long scopeDefinition2) {
        this.constrainType = constrainType;
        MinimumMaxElevation = minimumMaxElevation;
        this.scopeDefinition0 = scopeDefinition0;
        this.scopeDefinition1 = scopeDefinition1;
        this.scopeDefinition2 = scopeDefinition2;
    }


    public ArcConstraint() {
    }
}
