package org.nudtopt.api.algorithm.evolution;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.*;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.SolutionComparator;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DifferentialEvolution extends Algorithm {

    private String name         = "差分进化算法(DE)";
    private String type         = "进化算法";
    private long iteration      = 100;
    private int populationSize  = 100;
    private double selectionPro = 0.9;
    private double crossoverPro = 0.9;
    private double mutationPro  = 0.3;
    private int mutationNum     = 0;


    public List<Operator> run(Solution solution, List<Solution> population, long iteration, double selectionPro, double crossoverPro, double mutationPro, int mutationNum) {
        this.solution = solution;
        int trappedIteration = 0;
        for(int i = 0 ; i < iteration * 100 ; i ++) {
            // 1. move and score
            Score oldScore = solution.getScore().clone();          // 注意: 记录旧score
            Operator operator = moveAndScore(solution, null);
            trappedIteration ++;
            // 2. accept?
            boolean accept = solution.getScore().compareTo(oldScore) >= 0;
            // 3. accept or reject
            if(accept) {
                historyOperatorList.add(operator);
                Tool.listFIFO(historyOperatorList, historySize);
                logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby (1/" + trappedIteration + ") operator\t" + operator.getMoveList());
                trappedIteration = 0;
            } else {
                Operator.undo(solution, operator);                // 撤回: 实例编码(逆序撤销move), 数字编码(赋值原矩阵)
                if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0)    logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                solution.getScore().clone(oldScore);              // solution赋旧score(注意, 只是值, score对象不能变)
            }
            /* loop ends */
            if(update().equals("stop"))     break;
        }
        /* function ends */
        return historyOperatorList;
    }


    /*public List<Solution> run(Solution solution, List<Solution> population, long iteration, double selectionPro, double crossoverPro, double mutationPro, int mutationNum) {
        this.solution = solution;
        if(population == null)  population = new GeneticAlgorithm().defaultInitialize(solution, populationSize);
        else                    populationSize = population.size();
        GeneticAlgorithm.unIncremental(solution);               // 遗传算法中只能使用非增量式约束检查

        for(int i = 0 ; i < iteration ; i ++) {
            List<Solution> newPopulation = new ArrayList<>();   // 新种群
            for(Solution parent_1 : population) {
                // 1. mutation
                double[][] matrix = Mutation.differentialMutation(population, 0.1, 0.2);
                Solution parent_2 = solution.matrixClone(matrix);
                logger.debug("\t" + this + "\t" + i + "th\tmutating\t" + parent_2.getScore());

                // 2. crossover
                List<Solution> offspringList = Crossover.crossover(parent_1, parent_2, solution);

                // 3. selection
                offspringList.sort(new SolutionComparator());
                Solution offspring = offspringList.get(0);
                // offspring = solution.matrixClone(Mutation.climbingMutation(offspring.getDecisionMatrix(), mutationNum, solution));
                newPopulation.add(offspring);
                logger.debug("\t" + this + "\t\treproducing\t" + offspring.getScore());
            }
            population = newPopulation;                         // 更新种群
            logger.debug("\t" + this + "\t==========================================  第 " + i + " 代完成 !  ==========================================");

            *//* loop ends *//*
            population.sort(new SolutionComparator());          // 种群按评分升序
            historySolutionList.addAll(population);             // 存入历史解集
            Tool.listFIFO(historySolutionList, historySize);
            if(update().equals("stop"))     break;
        }
        *//* function ends *//*
        updateInputSolution();
        return historySolutionList;
    }*/


    @Override
    public void run() {
        run(solution, null, iteration, selectionPro, crossoverPro, mutationPro, mutationNum);
    }


    // getter & setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public long getIteration() {
        return iteration;
    }
    public void setIteration(long iteration) {
        this.iteration = iteration;
    }

    public int getPopulationSize() {
        return populationSize;
    }
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getSelectionPro() {
        return selectionPro;
    }
    public void setSelectionPro(double selectionPro) {
        this.selectionPro = selectionPro;
    }

    public double getCrossoverPro() {
        return crossoverPro;
    }
    public void setCrossoverPro(double crossoverPro) {
        this.crossoverPro = crossoverPro;
    }

    public double getMutationPro() {
        return mutationPro;
    }
    public void setMutationPro(double mutationPro) {
        this.mutationPro = mutationPro;
    }

    public int getMutationNum() {
        return mutationNum;
    }
    public void setMutationNum(int mutationNum) {
        this.mutationNum = mutationNum;
    }

/* class ends */
}
