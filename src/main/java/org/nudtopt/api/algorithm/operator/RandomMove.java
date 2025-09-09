package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomMove extends Move {

    @Override
    public Operator moveAndScore(Solution solution, List<Operator> tabuList) {
        Move move;
        if(solution.getDecisionMatrix() == null) {
            move = move(solution, tabuList);         // 1. 实例型编码, 返回: 非null(相同), 非禁忌的move
        } else {
            move = digitalMove(solution, tabuList);  // 2. 数字型编码, 返回: 决策矩阵(传参不变)
        }
        solution.updateScore(move);
        return move;
    }


    // ##########################################################################################################
    // ##################################### 以下为内置move (均不更新score) #######################################
    // ##########################################################################################################


    // 1.1 实例编码: 值随机
    public static Move move(DecisionEntity decisionEntity, String name) {
        List optionalVariableList = decisionEntity.getOptionalDecisionVariableList(name);    // 1. 获取其值域
        if(optionalVariableList.size() == 0) {
            return null; // 值域为空
        }
        Object newVariable = new Object();
        try {
            if(decisionEntity.getClass().getDeclaredField(name).getAnnotation(DecisionVariable.class).nullable()) {
                double probability = decisionEntity.getClass().getDeclaredField(name).getAnnotation(DecisionVariable.class).probability();
                newVariable = Tool.randomFromNullableList(optionalVariableList, probability);// 2. 如果变量可空
            } else {
                newVariable = Tool.randomFromList(optionalVariableList);                     // 3. 如果变量不可空
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Move move = move(decisionEntity, name, newVariable);
        return move;
        // return new RandomMove().transfer(move);                                              // 4. 转为RandomMove类, 便于后续统计
    }


    // 1.2 实例编码: 值和变量都随机
    public static Move move(DecisionEntity decisionEntity) {
        List<String> nameList = decisionEntity.getDecisionVariableList();
        String name = Tool.randomFromList(nameList);
        return move(decisionEntity, name);
    }


    // 1.3 实例编码: entity也随机, 完全随机
    public static Move move(Solution solution, List<Operator> tabuMoveList) {
        DecisionEntity decisionEntity = solution.getRandomDecisionEntity();                     // 选择一个可变/可用的entity
        Move move = move(decisionEntity);
        if(move == null) {                                // 完全随机情况下, 不能返回空move
            return move(solution, tabuMoveList);
        }
        if(move.isTabu(tabuMoveList)) {                   // 若被禁忌, 撤销此次move, 重新move
            move.undo();
            return move(solution, tabuMoveList);
        }
        return move;
    }


    // 2.1 数字编码
    public static double[][] move(double[][] matrix) {
        double[][] newMatrix = Tool.clone(matrix);           // 克隆, 不改传参
        int i = (int) (Tool.random() * newMatrix.length);
        int j = (int) (Tool.random() * newMatrix[0].length);
        double oldValue = newMatrix[i][j];
        double newValue = Tool.random();
        if(Math.abs(newValue - oldValue) <= 0.01) {          // 新旧值过于接近, 重新move
            return move(matrix);
        }
        newMatrix[i][j] = newValue;
        return newMatrix;
    }


    // 2.2 数字编码
    public static Move digitalMove(Solution solution, List<Operator> moveList) {
        double[][] oldMatrix = solution.getDecisionMatrix();
        double[][] newMatrix = move(oldMatrix);        // 新的matrix, 不改传参
        if(isTabu(newMatrix, moveList)) {
            return digitalMove(solution, moveList);
        }
        solution.decode(newMatrix);                    // 编码, 修改solution中的实例
        Move move = new Move();
        move.setOldValue(oldMatrix);
        move.setNewValue(newMatrix);
        move.setLogger("matrix " + oldMatrix + " -> " + newMatrix);
        return move;
    }



/* class ends */
}
