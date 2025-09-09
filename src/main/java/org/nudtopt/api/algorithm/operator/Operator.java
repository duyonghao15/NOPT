package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operator extends NumberedObject {

    protected List<Move> moveList = new ArrayList<>();                           // 原子算子 move顺序集合
    protected List<DecisionEntity> decisionEntityList = new ArrayList<>();       // 改变的实体清单
    protected Map<String, Object> oldValueMap = new HashMap<>();                 // 改变前的实体值map
    protected Map<String, Object> newValueMap = new HashMap<>();                 // 改变后的实体值map
    protected String logger;                                                     // 日志信息
    protected Score gapScore;                                                    // 改变了多少的score
    protected boolean undo;                                                      // 是否为逆袭的撤销操作
    protected boolean affectSort;                                                // 是否影响list及索引操作

    protected Operator operatorType;                                             // 算子类型(生成本算子的父类算子)
    protected int contribution;                                                  // 算子贡献度
    protected double probability;                                                // 算子调用概率

    // getter & setter
    public List<Move> getMoveList() {
        return moveList;
    }
    public void setMoveList(List<Move> moveList) {
        this.moveList = moveList;
    }

    public List<DecisionEntity> getDecisionEntityList() {
        return decisionEntityList;
    }
    public void setDecisionEntityList(List<DecisionEntity> decisionEntityList) {
        this.decisionEntityList = decisionEntityList;
    }

    public Map<String, Object> getOldValueMap() {
        return oldValueMap;
    }
    public void setOldValueMap(Map<String, Object> oldValueMap) {
        this.oldValueMap = oldValueMap;
    }

    public Map<String, Object> getNewValueMap() {
        return newValueMap;
    }
    public void setNewValueMap(Map<String, Object> newValueMap) {
        this.newValueMap = newValueMap;
    }

    public String getLogger() {
        return logger;
    }
    public void setLogger(String logger) {
        this.logger = logger;
    }

    public Score getGapScore() {
        return gapScore;
    }
    public void setGapScore(Score gapScore) {
        this.gapScore = gapScore;
    }

    public boolean isUndo() {
        return undo;
    }
    public void setUndo(boolean undo) {
        this.undo = undo;
    }

    public boolean isAffectSort() {
        return affectSort;
    }
    public void setAffectSort(boolean affectSort) {
        this.affectSort = affectSort;
    }

    public Operator getOperatorType() {
        return operatorType;
    }
    public void setOperatorType(Operator operatorType) {
        this.operatorType = operatorType;
    }

    public int getContribution() {
        return contribution;
    }
    public void setContribution(int contribution) {
        this.contribution = contribution;
    }

    public double getProbability() {
        return probability;
    }
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /** 2.1
     * 撤销一个move (实例编码)
     */
    public void undo() {
        for(int i = moveList.size() - 1 ; i >= 0 ; i --) {
            moveList.get(i).undo();
        }
        undo = true;
    }
    public void redo() {
        for(Move move : moveList) {
            move.redo();
        }
        undo = false;
    }


    /** 2.2
     * 撤销(实例编码 + 数字编码)
     * @param solution 当前解
     * @param operator 算子
     */
    public static void undo(Solution solution, Operator operator) {
        operator.undo();
        if(solution.getConstraint().isIncremental() || operator.isAffectSort()) {   // 增量式的, 或影响链表, 需更新solution
            solution.updateScore(operator);
        }
    }


    /** 3.1
     * 判断本算子是否被禁忌 (实数编码)
     * @author             杜永浩
     * @param operatorList 被禁忌的算子集合
     * @return             是否被禁忌
     */
    public <O extends Operator> boolean isTabu(List<O> operatorList) {
        if(operatorList == null)    return false;
        for(O operator : operatorList) {
            if(operator.getDecisionEntityList().containsAll(decisionEntityList) &&  // 被改变的对象均相同, 即是为禁忌
               decisionEntityList.containsAll(operator.getDecisionEntityList())) {
                return true;
            }
        }
        return false;
    }


    /** 3.2
     * 判断本算子是否被禁忌 (数字编码)
     * @author             杜永浩
     * @param matrix       编码矩阵
     * @param operatorList 被禁忌的算子集合
     * @return             是否被禁忌
     */
    public static boolean isTabu(double[][] matrix, List<Operator> operatorList) {
        if(operatorList == null || operatorList.size() == 0)    return false;
        for(Operator operator : operatorList) {
            for(DecisionEntity decisionEntity : operator.getDecisionEntityList()) {
                double[][] tabuMatrix = (double[][]) operator.getNewValueMap().get(decisionEntity);
                for(int i = 0 ; i < matrix.length ; i ++) {
                    for(int j = 0 ; j < matrix[0].length ; j ++) {
                        if(Math.abs(matrix[i][j] - tabuMatrix[i][j]) >= 0.01) {  // 不过于接近, 视为不相同
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }



    /**
     * 执行算子并评分
     * @author                 杜永浩
     * @param solution         当前解
     * @param tabuOperatorList 被禁忌的算子列表
     * @return                 未被禁忌的一个算子
     */
    public Operator moveAndScore(Solution solution, List<Operator> tabuOperatorList) {
        return null;
    }


    /**
     * 更新算子所改变的实体 (及其新旧值)
     */
    public void update() {
        decisionEntityList.clear();
        oldValueMap.clear();
        newValueMap.clear();
        for(Move move : moveList) {
            DecisionEntity entity = move.getDecisionEntity();
            String variable = move.getName();
            if(!decisionEntityList.contains(entity)) {
                decisionEntityList.add(entity);
            }
            String key = entity + " : " + variable;
            if(!oldValueMap.containsKey(key)) {
                oldValueMap.put(key, move.getOldValue());
            }
            newValueMap.put(key, move.getNewValue());
            // todo 判断是否为影响list的操作
            try {
                String resourceName = entity.getClass().getDeclaredField(variable).getAnnotation(DecisionVariable.class).sortIn();
                if(resourceName.length() > 0)                             affectSort = true;
                if(entity.getSortInVariableNameList().contains(variable)) affectSort = true;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 基于原子算子集合, 创建一个综合算子, 并更新解的收益值
     * @param moveList  原子算子集合
     * @param solution  解
     * @return          综合算子
     */
    public static Operator createAndUpdate(List<Move> moveList, Solution solution) {
        if(moveList == null || moveList.size() == 0)  return null;
        Operator operator = new Operator();                                         // 新建一个算子
        operator.setMoveList(moveList);                                             // 赋其原子算子
        operator.update();                                                          // 更新算子属性
        solution.updateScore(operator);                                             // 更新评分
        return operator;
    }


    @Override
    public String toString() {
        if(this.logger == null) {
            StringBuilder str = new StringBuilder();
            for(int i = 0 ; i < moveList.size() ; i ++) {
                Move move = moveList.get(i);
                str.append(move.getLogger()).append(i == moveList.size() - 1 ? "" : ", ");
            }
            this.logger = str.toString();
        }
        if(this.logger.length() == 0)   return super.toString();
        return this.logger;
    }

/* class ends */
}
