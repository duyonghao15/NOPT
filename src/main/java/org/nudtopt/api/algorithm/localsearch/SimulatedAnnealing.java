package org.nudtopt.api.algorithm.localsearch;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing extends Algorithm {

    private String name             = "模拟退火算法(SA)";
    private String type             = "局部搜索算法";
    private Boolean highlight       = true;
    private long iteration          = 10000;
    private List<Operator> tabuList = new ArrayList<>();
    private int tabuSize            = 4;
    private Score initTemp;                       // 初始退火温度
    private String annealType       = "等差";         // 退火类型


    public List<Operator> run(Solution solution, long iteration, int tabuSize, Score initTemp, String annealType) {
        this.solution = solution;
        int trappedIteration = 0;
        // 0. temperature
        if(initTemp == null)    initTemp = randomInitialScore(solution, 10, 1000);
        double hardTemp = initTemp.getHardScore();
        double meanTemp = initTemp.getMeanScore();
        double softTemp = initTemp.getSoftScore();

        for(int i = 0 ; i < iteration ; i ++) {
            // 1. move and score
            Score oldScore = solution.getScore().clone();
            Operator operator = moveAndScore(solution, tabuList);
            trappedIteration ++;

            // 2. annealing
            switch (annealType) {
                case "等差":
                    hardTemp = initTemp.getHardScore() * (iteration - i * 1.0) / iteration;
                    meanTemp = initTemp.getMeanScore() * (iteration - i * 1.0) / iteration;
                    softTemp = initTemp.getSoftScore() * (iteration - i * 1.0) / iteration;
                    break;
                case "等比":
                    hardTemp = initTemp.getHardScore() * Math.pow(0.001, i * 1.0 / iteration);
                    meanTemp = initTemp.getMeanScore() * Math.pow(0.001, i * 1.0 / iteration);
                    softTemp = initTemp.getSoftScore() * Math.pow(0.001, i * 1.0 / iteration);
                    break;
                case "对数":
                    hardTemp = initTemp.getHardScore() * (1 - Math.log(i + 1) / Math.log(iteration));
                    meanTemp = initTemp.getMeanScore() * (1 - Math.log(i + 1) / Math.log(iteration));
                    softTemp = initTemp.getSoftScore() * (1 - Math.log(i + 1) / Math.log(iteration));
                    break;
                default:
                    logger.error("\t退火类型输入错误 !");
                    break;
            }

            // 3. accept?
            boolean accept;
            if(solution.getScore().compareTo(oldScore) >= 0) {     // 获得优解, 接受
                accept = true;
            } else {                                               // 获得劣解, 判断
                long hardInterval = solution.getScore().getHardScore() - oldScore.getHardScore();
                long meanInterval = solution.getScore().getMeanScore() - oldScore.getMeanScore();
                long softInterval = solution.getScore().getSoftScore() - oldScore.getSoftScore();

                double hardP = Math.exp(hardInterval / (hardTemp + 0.000001));
                double meanP = Math.exp(meanInterval / (meanTemp + 0.000001));
                double softP = Math.exp(softInterval / (softTemp + 0.000001));
                double minP = (hardP < meanP) ? (hardP < softP ? hardP : softP) : (meanP < softP ? meanP : softP);
                accept = Tool.random() < minP;                     // <p, 接受(劣解); 否则不接受
            }

            // 4. accept or reject
            if(accept) {
                tabuList.add(operator);
                historyOperatorList.add(operator);
                Tool.listFIFO(tabuList, tabuSize);
                Tool.listFIFO(historyOperatorList, historySize);
                logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby (1/" + trappedIteration + ") operator\t" + operator.getMoveList());
                trappedIteration = 0;
            } else {
                Operator.undo(solution, operator);                // 撤回: 实例编码(逆序撤销move), 数字编码(赋值原矩阵)
                if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) {
                    logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                }
                solution.getScore().clone(oldScore);              // solution赋旧score(注意, 只是值, score对象不能变)
            }
            /* loop ends */
            if(update().equals("stop"))     break;
        }
        /* function ends */
        return historyOperatorList;
    }


    // 随机初始score (随机run n次, 取平均score)
    public Score randomInitialScore(Solution solution, int run, long iterationPerRun) {
        Score score = new Score();
        Score initialScore = solution.getScore().clone();
        for(int i = 0 ; i < run ; i ++) {
            logger.debug("\tgenerating\tthe initial solution\t(" + (i + 1) + "/" + run + ") ......");
            List<Operator> operatorList = new HillClimbing().run(solution, iterationPerRun);
            score.addHardScore(solution.getScore().getHardScore());
            score.addMeanScore(solution.getScore().getMeanScore());
            score.addSoftScore(solution.getScore().getSoftScore());
            for(int j = operatorList.size() - 1 ; j >= 0 ; j --) {
                Operator operator = operatorList.get(j);
                operator.undo();
                solution.updateScore(operator);
            }
            if(solution.getScore().compareTo(initialScore) != 0)    logger.error("\tscore error!\n\n");
        }
        score.setHardScore(Math.abs(score.getHardScore() / run));  // 退火温度应均为正
        score.setMeanScore(Math.abs(score.getMeanScore() / run));
        score.setSoftScore(Math.abs(score.getSoftScore() / run));
        return score;
    }


    @Override
    public void run() {
        historyOperatorList = run(solution, iteration, tabuSize, initTemp, annealType);
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

    public Boolean getHighlight() {
        return highlight;
    }
    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    public long getIteration() {
        return iteration;
    }
    public void setIteration(long iteration) {
        this.iteration = iteration;
    }

    public List<Operator> getTabuList() {
        return tabuList;
    }
    public void setTabuList(List<Operator> tabuList) {
        this.tabuList = tabuList;
    }

    public int getTabuSize() {
        return tabuSize;
    }
    public void setTabuSize(int tabuSize) {
        this.tabuSize = tabuSize;
    }

    public Score getInitTemp() {
        return initTemp;
    }
    public void setInitTemp(Score initTemp) {
        this.initTemp = initTemp;
    }

    public String getAnnealType() {
        return annealType;
    }
    public void setAnnealType(String annealType) {
        this.annealType = annealType;
    }

/* class ends */
}
