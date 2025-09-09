package org.nudtopt.api.algorithm.operator;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class Swap extends Move {

    /**
     * 交换2个决策实体的某个变量 (实例编码)
     * (注意: swap时, decisionEntity应是不同的, 且值域应相同)
     * @param decisionEntity_1  决策实体1
     * @param decisionEntity_2  决策实体2
     * @param name              决策变量名
     * @return                  元算子集合
     */
    public static List<Move> swap(DecisionEntity decisionEntity_1, DecisionEntity decisionEntity_2, String name) {
        List optionalList_1 = decisionEntity_1.getOptionalDecisionVariableList(name);
        List optionalList_2 = decisionEntity_2.getOptionalDecisionVariableList(name);
        if(optionalList_1.size() == 0 || optionalList_2.size() == 0) {
            return new ArrayList<>();
        }
        Object variable_1 = decisionEntity_1.getDecisionVariable(name);
        Object variable_2 = decisionEntity_2.getDecisionVariable(name);
        boolean canSwap = Swap.canSwap(decisionEntity_1, decisionEntity_2, name);                       // 是否可交换                                                             // 判断是否可使用交换算子
        if(!canSwap) {                                                                                  // 3. 不可交换, 则改为交换index
            int index_1 = optionalList_1.indexOf(variable_1);                                           //    变量1在其值域中的index1
            int index_2 = optionalList_2.indexOf(variable_2);                                           //    变量2在其值域中的index2
            variable_1  = optionalList_2.get(Math.min(Math.max(index_1, 0), optionalList_2.size() - 1));//    变量1 -> 值域2中的index1
            variable_2  = optionalList_1.get(Math.min(Math.max(index_2, 0), optionalList_1.size() - 1));//    变量2 -> 值域1中的index2
            // return new ArrayList<>();
        }

        List<Move> moveList = new ArrayList<>();
        if(variable_1 != variable_2) {
            Move move_1 = Move.move(decisionEntity_1, name, variable_2);
            Move move_2 = Move.move(decisionEntity_2, name, variable_1);
            if(move_1 != null) moveList.add(move_1);
            if(move_2 != null) moveList.add(move_2);
        }
        return moveList;
    }


    /**
     * 交换2个决策实体中的所有变量 (实例编码)
     * @param decisionEntity_1 决策实体1
     * @param decisionEntity_2 决策实体2
     * @return                 元算子集合
     */
    public static List<Move> swap(DecisionEntity decisionEntity_1, DecisionEntity decisionEntity_2) {
        List<Move> moveList = new ArrayList<>();
        List<String> nameList = decisionEntity_1.getDecisionVariableList();
        for(String name : nameList) {
            moveList.addAll(swap(decisionEntity_1, decisionEntity_2, name));
        }
        return moveList;
    }


    /**
     * 交换2个决策实体的随机一个变量 (实例编码)
     * @param decisionEntity_1 决策实体1
     * @param decisionEntity_2 决策实体2
     * @return                 元算子集合
     */
    public static List<Move> randomSwap(DecisionEntity decisionEntity_1, DecisionEntity decisionEntity_2) {
        List<String> nameList = decisionEntity_1.getDecisionVariableList();
        String name = Tool.randomFromList(nameList);
        return swap(decisionEntity_1, decisionEntity_2, name);
    }


    /**
     * 交换 (数字编码, 基于2个决策矩阵, 和要swap的哪些index)
     * @param decisionMatrix_1 决策矩阵1
     * @param decisionMatrix_2 决策矩阵2
     * @param indexArray       交换的位置索引
     * @return                 交换后的决策居中
     */
    public static List<double[][]> swap(double[][] decisionMatrix_1, double[][] decisionMatrix_2, int[] indexArray) {
        double[][] newMatrix_1 = Tool.clone(decisionMatrix_1);      // 注意: 传参未修改
        double[][] newMatrix_2 = Tool.clone(decisionMatrix_2);
        for(int index : indexArray) {
            double[] vector_1 = newMatrix_1[index].clone();
            double[] vector_2 = newMatrix_2[index].clone();
            newMatrix_1[index] = vector_1;
            newMatrix_2[index] = vector_2;
        }
        List<double[][]> swappedMatrixList = new ArrayList<>();
        swappedMatrixList.add(newMatrix_1);
        swappedMatrixList.add(newMatrix_2);
        return swappedMatrixList;
    }


    /**
     * 根据决策变量的值域, 判断两个实体是否可使用交换算子
     * @param decisionEntity_1 实体1
     * @param decisionEntity_2 实体2
     * @param name             决策变量
     * @return 是否可使用交换算子
     */
    public static boolean canSwap(DecisionEntity decisionEntity_1, DecisionEntity decisionEntity_2, String name) {
        List optionalList_1 = decisionEntity_1.getOptionalDecisionVariableList(name);
        List optionalList_2 = decisionEntity_2.getOptionalDecisionVariableList(name);
        Object variable_1 = decisionEntity_1.getDecisionVariable(name);
        Object variable_2 = decisionEntity_2.getDecisionVariable(name);
        // 0. 变量相同, 视为可交换 (虽然交换无效, 单从本函数角度看是可交换的)
        if(variable_1 == variable_2) {
            return true;
        }
        // 1. 若决策变量值域完全相同, 必然可以
        if(optionalList_1 == optionalList_2){
            return true;
        }
        // 2. 若其中一个值域为空, 不可交换
        if(optionalList_1 == null || optionalList_1.size() == 0 || optionalList_2 == null || optionalList_2.size() == 0) {
            return false;
        }
        // 3. 若值域为数字, 视情况可交换
        if(optionalList_1.get(0) instanceof Number) {
            if(variable_1 == null && ((Number)variable_2).doubleValue() >= ((Number)optionalList_1.get(0)).doubleValue() && ((Number)variable_2).doubleValue() <= ((Number)optionalList_1.get(optionalList_1.size() - 1)).doubleValue()) {
                return true;
            } else
            if(variable_2 == null && ((Number)variable_1).doubleValue() >= ((Number)optionalList_2.get(0)).doubleValue() && ((Number)variable_1).doubleValue() <= ((Number)optionalList_2.get(optionalList_2.size() - 1)).doubleValue()) {
                return true;
            } else
            if(variable_1 != null && variable_2 != null &&
               ((Number)variable_2).doubleValue() >= ((Number)optionalList_1.get(0)).doubleValue() && ((Number)variable_2).doubleValue() <= ((Number)optionalList_1.get(optionalList_1.size() - 1)).doubleValue() &&
               ((Number)variable_1).doubleValue() >= ((Number)optionalList_2.get(0)).doubleValue() && ((Number)variable_1).doubleValue() <= ((Number)optionalList_2.get(optionalList_2.size() - 1)).doubleValue()) {
                return true;
            }
        }
        // 4. 当前值属于对方值域, 可交换 (计算量大)
        /*if(optionalList_1.contains(variable_2) && optionalList_2.contains(variable_1)) {
            return true;
        }*/
        return false;
    }



/* class ends */
}
