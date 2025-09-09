package org.nudtopt.api.algorithm.evolution;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Crossover;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.MultiScore;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.ScoreComparator;
import org.nudtopt.api.tool.comparator.SolutionComparator;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class MOEA extends Algorithm {

    private String name         = "多目标进化算法(MOEA)";
    private String type         = "进化算法";
    private long iteration      = 100;
    private double selectionPro = 0.9;
    private double crossoverPro = 0.9;
    private double mutationPro  = 0.3;


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


    /*public List<Solution> run(Solution solution, List<Solution> solutionList, long iteration, double selectionPro, double crossoverPro, double mutationPro) {
        this.solution = solution;
        int populationSize = solutionList.size();
        int objNum = ((MultiScore)solution.getScore()).getScoreList().size();

        // 1. 初始化权重向量
        List<double[]> vectorList = new ArrayList<>();
        for(int i = 0 ; i < populationSize ; i ++) {
            double[] vector = new double[objNum];
            for(int j = 0 ; j < objNum ; j ++) {
                vector[j] = 1.0 * i / populationSize * (j + 1) / objNum;
            }
            vectorList.add(vector);
        }

        // 2. 初始化参考点
        MultiScore referMultiScore = new MultiScore();
        referMultiScore.setScoreList(new ArrayList<>());
        for(int i = 0 ; i < objNum ; i ++) {
            referMultiScore.getScoreList().add(new Score()); // 参考点值为最大值
        }

        // 3. 初始化非支配集
        List<Solution> EPSolutionList = new ArrayList<>();


        // 4. 遍历子问题
        for(int i = 0 ; i < populationSize ; i ++) {
            // a. 邻域内随机2个解
            int a = randomId(i, populationSize, populationSize / 5);
            int b = randomId(i, populationSize, populationSize / 5);
            Solution solutionA = solutionList.get(a);
            Solution solutionB = solutionList.get(b);

            // b. 交叉得最好的1个解
            List<Solution> offspringList = Crossover.crossover(solutionA, solutionB, solution);
            offspringList.sort(new SolutionComparator());
            Solution offspring = offspringList.get(0);

            // c. 更新参考点
            for(int j = 0 ; j < objNum ; j ++) {
                Score currentScore = ((MultiScore)offspring.getScore()).getScoreList().get(j);
                Score referScore = referMultiScore.getScoreList().get(j);
                if(currentScore.compareTo(referScore) > 0)  referMultiScore.getScoreList().set(j, currentScore.clone()); // 参考点值为最大值
            }

            // d. 更新相邻解
            Score oldG = getG(solutionList.get(i), referMultiScore, vectorList.get(i));
            Score newG = getG(offspring, referMultiScore, vectorList.get(i));
            if(newG.compareTo(oldG) < 0)  solutionList.set(i, offspring);  // 最小化g

            // e. 更新非支配解集
            boolean dominated = false;
            for(int j = EPSolutionList.size() - 1 ; j >= 0 ; j --) {
                Solution EPSolution = EPSolutionList.get(j);
                MultiScore EPMultiScore = (MultiScore) EPSolution.getScore();
                MultiScore offspringMultiScore = (MultiScore) offspring.getScore();
                if(EPMultiScore.dominate(offspringMultiScore)){
                    dominated = true;
                    break;                      // offspring被支配, 直接跳出
                }
                if(offspringMultiScore.dominate(EPMultiScore))  {
                    EPSolutionList.remove(j);   // EP解被支配, 剔除
                }
            }
            if(!dominated) {                    // offspring未被支配, 加入EP
                EPSolutionList.add(offspring);
            }
        }
        return null;
    }*/



    // 随机生成邻域id
    private int randomId(int i, int populationSize, int neighbourSize) {
        double id = i + (Tool.random() - 1) * neighbourSize;
        if(id < 0)                  return 0;
        if(id >= populationSize)    return populationSize - 1;
        return (int) id;
    }

    // 计算切比雪夫分解值g
    private Score getG(Solution solution, MultiScore referMultiScore, double[] vector) {
        List<Score> gList = new ArrayList<>();
        MultiScore multiScore = (MultiScore)solution.getScore();
        for(int j = 0 ; j < vector.length ; j ++) {
            double v = vector[j];
            Score currentScore = multiScore.getScoreList().get(j);
            Score referScore = referMultiScore.getScoreList().get(j);
            Score g = currentScore.cutScore(referScore);          // 当前值 - 参考点值, 再求均值
            g.setHardScore((int) Math.abs(v * g.getHardScore())); // 乘以权值分
            g.setMeanScore((int) Math.abs(v * g.getMeanScore()));
            g.setSoftScore((int) Math.abs(v * g.getSoftScore()));
            gList.add(g);
        }
        gList.sort(new ScoreComparator());                        // 降序排列
        return gList.get(0);                                      // 返回最大值
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

}
