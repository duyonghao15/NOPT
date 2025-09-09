package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.*;

public class Move extends Operator {

    private DecisionEntity decisionEntity;      // move哪个实体(实例编码)
    private Object oldValue;                    // move前的值(实例编码: 决策变量; 数字编码: 决策矩阵)
    private Object newValue;                    // move后的值

    // getter & setter
    public DecisionEntity getDecisionEntity() {
        return decisionEntity;
    }
    public void setDecisionEntity(DecisionEntity decisionEntity) {
        this.decisionEntity = decisionEntity;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public Object getDoingOldValue() {
        return undo ? newValue : oldValue;
    }   // todo 考虑undo情况, 此次move的新值与旧值
    public Object getDoingNewValue() {
        return undo ? oldValue : newValue;
    }

    // -------------------------------- 以下是辅助计算函数 ------------------------------------
    // 1. move (邻域构造的基本单位, 实例编码)
    public static Move move(DecisionEntity decisionEntity, String name, Object newVariable) {
        if(decisionEntity == null || name.equals(""))                       return null; // a. 输入任意为null
        if(!decisionEntity.isChangeable() || !decisionEntity.isAvailable()) return null; // b. entity不可变/用
        if(decisionEntity.getDecisionVariable(name) == newVariable)         return null; // c. 新旧相同

        // todo 链式无效move
        if(name.equals("priority")) {
            int minPriority = decisionEntity.getLastEntity() == null ? 0         : (int) decisionEntity.getLastEntity().getDecisionVariable(name);
            int maxPriority = decisionEntity.getNextEntity() == null ? 999999999 : (int) decisionEntity.getNextEntity().getDecisionVariable(name);
            if((int) newVariable > minPriority && (int) newVariable < maxPriority) {
                return null;
            }
        }


        Move move = new Move();
        move.setDecisionEntity(decisionEntity);
        move.setName(name);
        move.setOldValue(decisionEntity.getDecisionVariable(name));
        move.setNewValue(newVariable);
        move.setLogger(decisionEntity + " (" + move.getOldValue() + " -> " + newVariable + ")");
        decisionEntity.setDecisionVariable(name, newVariable);                           // 赋值新的variable
        move.setMoveList(Collections.singletonList(move));                               // move本身也是一个算子(moveList为自身)
        move.update();                                                                   // 更新operator属性
        return move;
    }


    @Override
    public void undo() {
        decisionEntity.setDecisionVariable(name, oldValue);                             // 把旧的variable赋值回去
        undo = true;
    }
    @Override
    public void redo() {
        decisionEntity.setDecisionVariable(name, newValue);                             // 把新的variable赋值回去
        undo = false;
    }


    /** 2.2
     * 撤销(实例编码 + 数字编码)
     * @param solution  当前解
     * @param moveList  待撤销的move操作集合
     */
    public static void undo(Solution solution, List<Move> moveList) {
        if(moveList.get(Tool.licence).getDecisionEntity() != null) {                    // 1. 实例编码
            for(int i = moveList.size() - 1 ; i >= 0 ; i --) {                          // 逆序撤回, 返回原解
                Move move = moveList.get(i);
                if(move == null)    continue;
                move.undo();                                                            // 撤回
                if(solution.getConstraint().isIncremental()) {
                    solution.updateScore(move);                                         // (增量式的), 需更新solution
                }
            }
        } else {                                                                        // 2. 数字编码
            solution.decode((double[][]) moveList.get(0).getOldValue());                // 返回最早的move的oldMatrix
        }
    }


    /** 4. 用于重写的接口
     * 执行算子并评分
     * @author                 杜永浩
     * @param solution         当前解
     * @param tabuOperatorList 被禁忌的算子列表
     * @return                 未被禁忌的一个算子
     */
    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuOperatorList) {
        return null;
    }


    // 5. 强制转换
    public Move transfer(Move move) {
        if(move == null)    return null;
        Move trueMove = new Move();
        try {
            trueMove = getClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        trueMove.setDecisionEntity(move.getDecisionEntity());
        trueMove.setName(move.getName());
        trueMove.setOldValue(move.getOldValue());
        trueMove.setNewValue(move.getNewValue());
        trueMove.setLogger(move.getLogger());
        return trueMove;
    }


    // 6. 将entity中变量置null
    public static Move moveToNull(DecisionEntity decisionEntity, String name) {
        return move(decisionEntity, name, null);
    }


    /**
     * 邻域构造并更新约束收益
     * @param solution       当前解
     * @param decisionEntity 决策实体
     * @param name           决策变量名
     * @param newVariable    新变量
     * @return               move算子
     */
    public static Move moveAndScore(Solution solution, DecisionEntity decisionEntity, String name, Object newVariable) {
        Move move = move(decisionEntity, name, newVariable);
        solution.updateScore(move);
        return move;
    }


    /**
     * 构造更优的邻域并更新约束收益, 如果非更优, 则返回
     * @param solution       当前解
     * @param decisionEntity 决策实体
     * @param name           决策变量名
     * @param newVariable    新变量
     * @return               move算子(收益更佳) 或 null(收益不佳)
     */
    public static Move betterMoveAndScore(Solution solution, DecisionEntity decisionEntity, String name, Object newVariable) {
        Score oldScore = solution.getScore().clone();                           // 注意: 记录旧score
        Move move = moveAndScore(solution, decisionEntity, name, newVariable);  // 构造邻域并更新
        // System.out.println("\tobtains score " + solution.getScore() + "\tby moves\t" + move);
        if(move == null)         return null;
        boolean better = solution.getScore().compareTo(oldScore) >= 0;          // 判断收益是否增加(含相同)
        if(better) {                                                            // 1. 收益更佳
            return move;                                                        //    返回当前move
        } else {                                                                // 2. 收益不佳
            move.undo();                                                        //    撤销
            solution.updateScore(move);                                         //    重新更新收益
            if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) {
                System.out.println("\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
            }
            return null;                                                        //    返回空
        }
    }


/* class ends */
}
