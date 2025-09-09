package org.nudtopt.api.algorithm.evolution;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Mutation;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.SolutionComparator;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EvolvingStrategy extends Algorithm {

    private String name         = "演化策略(ES)";
    private String type         = "进化算法";
    private long iteration      = 100;
    private int populationSize  = 100;
    private double mu           = 0.2;            // 每代产生lambda% 个子代, 与父代合并取最好的mu个解作为父代
    private double lambda       = 0.8;
    private double mutationPro  = 0.3;
    private List<Solution> population;


    public List<Operator> run(Solution solution, List<Solution> population, long iteration, double mu, double lambda, double mutationPro) {
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


                              // 实体(唯一)解     // 数字编码的(初始)种群
    /*public List<Solution> run(Solution solution, List<Solution> population, long iteration, double mu, double lambda, double mutationPro) {
        this.solution     = solution;
        populationSize    = population.size();
        int parentSize    = (int) Math.round(populationSize * mu);              // 每代父代数量
        int offspringSize = (int) Math.round(populationSize * lambda);          // 每代产生的子代数量
        defaultInitialize(population);                                          // 初始化
        GeneticAlgorithm.unIncremental(solution);                               // 遗传算法中只能使用非增量式约束检查

        for(int i = 0 ; i < iteration ; i ++) {
            // 1 selection
            List<Solution> parents = population.subList(0, parentSize);         // 父代 (选取评分最高的一些)
            logger.debug("\t" + this + "\t" + i + "th\tselecting\t" + parents.size() + "\toutperforming parents, the best " + parents.get(0).getScore());
            // 2 crossover

            // 3 mutation
            List<Solution> offspring = new ArrayList<>();
            for(int j = 0 ; j < offspringSize ; j ++) {
                Solution parent = Tool.randomFromList(parents).matrixClone();
                Solution son = Mutation.mutation(Arrays.asList(parent), mutationPro, 1, solution).get(0);
                offspring.add(son);
            }
            logger.debug("\t" + this + "\t\treproducing\t" + offspring.size() + "\toffspring");
            logger.debug("\t" + this + "\t==========================================  第 " + i + " 代完成 !  ==========================================");

            population = new ArrayList<>(parents);                              // 父代
            population.addAll(offspring);                                       // +子代 = 新种群
            Collections.sort(population, new SolutionComparator());             // 种群按评分降序
            historySolutionList.addAll(offspring);                              // 存入历史解集
            Tool.listFIFO(historySolutionList, historySize);
            if(update().equals("stop"))     break;
        }

        *//* function ends *//*
        updateInputSolution();
        return historySolutionList;
    }*/




    // 1. 初始化种群
    public List<Solution> defaultInitialize(List<Solution> population) {
        if(progressBar != null) {
            progressBar.setIndeterminate(true);
            progressBar.setString("Initializing ......");
        }

        int populationSize = population.size();         // 种群规模
        for(int i = 0 ; i < populationSize ; i ++) {
            Solution solution = population.get(i);      // 第i个个体
            logger.debug("\tgenerating\tthe initial solution\twith score: " + solution.getScore() + " (" + (i + 1) + "/" + populationSize + ") ......");
            double[][] matrix = solution.encode();      // 该solution的决策矩阵
        }
        logger.debug("\tinitialized\tthe " + populationSize + "-" + solution.getClass().getName() + "population !");

        if(progressBar != null) {
            progressBar.setValue(0);
            progressBar.setString(null);
            progressBar.setIndeterminate(false);
            progressBar.setMaximum((int) (iteration * populationSize * mutationPro));
        }
        return population;
    }




    @Override
    public void run() {
        run(solution, population, iteration, mu, lambda, mutationPro);
    }

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

    public double getMu() {
        return mu;
    }
    public void setMu(double mu) {
        this.mu = mu;
    }

    public double getLambda() {
        return lambda;
    }
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public double getMutationPro() {
        return mutationPro;
    }
    public void setMutationPro(double mutationPro) {
        this.mutationPro = mutationPro;
    }

    public List<Solution> getPopulation() {
        return population;
    }
    public void setPopulation(List<Solution> population) {
        this.population = population;
    }

/* class ends */
}