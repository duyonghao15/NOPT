package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class RandomInitialize {


    /**
     * 对解进行随机初始化操作
     * @param solution 解(对象化的)
     * @return 算子列表
     */
    public static List<Move> randomInitialize(Solution solution) {
        List<DecisionEntity> entityList = solution.getDecisionEntityList();                                 // 获取决策实体列表
        List<Move> moveList = new ArrayList<>();
        // 1. 遍历决策实体
        for(DecisionEntity entity : entityList) {
            List<String> variableList = entity.getDecisionVariableList();                                   // 获取决策变量列表
            // 2. 遍历决策变量
            for(String name : variableList) {
                List<Object> optionalVariableList = entity.getOptionalDecisionVariableList(name);           // 获取决策变量的值域
                Object newVariable = Tool.randomFromNullableList(optionalVariableList, 0.1);    // 随机选择一个值
                // 3. 新建move
                Move move = Move.move(entity, name, newVariable);                                           // 赋值
                solution.updateScore(move);                                                                 // 更新收益
                moveList.add(move);
            }
        }
        return moveList;
    }


    /**
     * 对解进行随机初始化操作
     * @param solution 解(数字化的决策矩阵)
     * @return 初始化后的决策矩阵
     */
    public static double[][] randomInitializeDigital(Solution solution) {
        double[][] matrix = solution.getDecisionMatrix();
        if(matrix == null) {
            matrix = solution.encode();             // 若未编码, 则编码生成决策矩阵
        }
        int i_max = matrix.length;                  // 决策矩阵的行数
        int j_max = matrix[0].length;               // 决策矩阵的列数
        for(int i = 0 ; i < i_max ; i ++) {
            for(int j = 0 ; j < j_max ; j ++) {
                double variable = Tool.random();    // 随机生成决策变量的值
                matrix[i][j] = variable;            // 赋值
            }
        }
        solution.setDecisionMatrix(matrix);
        return matrix;
    }


/* class ends */
}
