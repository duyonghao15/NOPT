package org.nudtopt.api.algorithm.operator;


import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.SolutionComparator;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crossover {

    // 0. 交叉
    public static List<Solution> crossover(List<Solution> parentList, double probability, int populationSize, Solution realSolution) {
        List<Solution> solutionList = new ArrayList<>();
        while (solutionList.size() < populationSize) {
            // a. 随机选择2个父代
            Solution parentSolution_1 = Tool.randomFromList(parentList);
            Solution parentSolution_2 = Tool.randomFromList(parentList);

            // b. 交叉, 获得父子代共4个
            List<Solution> offspringSolutionList = crossover(parentSolution_1, parentSolution_2, realSolution);

            // c. 选择最好的
            Collections.sort(offspringSolutionList, new SolutionComparator());
            solutionList.add(offspringSolutionList.get(0));
        }
        parentList.clear();
        /* function ends */
        return solutionList;
    }


    // 1. 交叉
    public static List<Solution> crossover(Solution parentSolution_1, Solution parentSolution_2, Solution realSolution) {
        // a. 2个父代的决策矩阵
        double[][] parentMatrix_1 = parentSolution_1.getDecisionMatrix();
        double[][] parentMatrix_2 = parentSolution_2.getDecisionMatrix();

        // b. 交叉父代的决策矩阵, 获得2个子代决策矩阵
        List<double[][]> offspringMatrixList = Crossover.onePointCrossover(parentMatrix_1, parentMatrix_2);
        double[][] offspringMatrix_1 = offspringMatrixList.get(0);
        double[][] offspringMatrix_2 = offspringMatrixList.get(1);

        // c. 子代决策矩阵 -> solution (影子, 只有矩阵和评分, 没有属性)
        Solution offspringSolution_1 = realSolution.matrixClone(offspringMatrix_1);
        Solution offspringSolution_2 = realSolution.matrixClone(offspringMatrix_2);

        // d. 输出父子代共4个
        List<Solution> fourSolutionList = new ArrayList<>();
        fourSolutionList.add(parentSolution_1);
        fourSolutionList.add(parentSolution_2);
        fourSolutionList.add(offspringSolution_1);
        fourSolutionList.add(offspringSolution_2);
        /* function ends */
        return fourSolutionList;
    }


    // 2.1 单点矩阵交叉(数字编码)
    public static List<double[][]> onePointCrossover(double[][] parentMatrix_1, double[][] parentMatrix_2) {
        int index = (int) (Tool.random() * parentMatrix_1.length);
        int[] indexArray = new int[index];
        for(int i = 0 ; i < indexArray.length ; i ++) {
            indexArray[i] = i;
        }
        return Swap.swap(parentMatrix_1, parentMatrix_2, indexArray);   // 注意: 传参未改, 返回新值
    }


    // 2.2 双点矩阵交叉(数字编码)
    public static List<double[][]> twoPointCrossover(double[][] parentMatrix_1, double[][] parentMatrix_2) {
        int index_1 = (int) (Tool.random() * parentMatrix_1.length);
        int index_2 = (int) (Tool.random() * parentMatrix_2.length);
        int index_small = index_1 < index_2 ? index_1 : index_2;
        int index_large = index_1 > index_2 ? index_1 : index_2;
        int[] indexArray = new int[index_large - index_small];
        for(int i = 0 ; i < indexArray.length ; i ++) {
            indexArray[i] = i + index_small;
        }
        return Swap.swap(parentMatrix_1, parentMatrix_2, indexArray);   // 注意: 传参未改, 返回新值
    }


/* class ends */
}
