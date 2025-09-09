package org.nudtopt.api.algorithm.localsearch;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class GreatDeluge extends Algorithm {

    private String name             = "大洪水算法(GD)";
    private String type             = "局部搜索算法";
    private long iteration          = 10000;
    private List<Operator> tabuList = new ArrayList<>();
    private int tabuSize            = 4;
    private double delugeRatio      = 0.5;            // 涨水系数


    public List<Operator> run(Solution solution, long iteration, int tabuSize, double delugeRatio) {
        this.solution = solution;
        int trappedIteration = 0;
        // 0. water level
        Score waterScore = new Score();
        waterScore.setHardScore(solution.getScore().getHardScore());
        waterScore.setMeanScore(solution.getScore().getMeanScore());
        waterScore.setSoftScore(solution.getScore().getSoftScore());


        for(int i = 0 ; i < iteration ; i ++) {
            // 1. move and score
            Score oldScore = solution.getScore().clone();
            Operator operator = moveAndScore(solution, tabuList);
            trappedIteration ++;

            // 2. accept?
            boolean accept = false;
            if(solution.getScore().compareTo(waterScore) >= 0) {             // 获得高于(或等于)当前水线的解, 接受
                accept = true;
                // 3. deluge up
                Score deltaScore = solution.getScore().cutScore(waterScore); // 水位差
                deltaScore.setHardScore((int) (deltaScore.getHardScore() * delugeRatio));
                deltaScore.setMeanScore((int) (deltaScore.getMeanScore() * delugeRatio));
                deltaScore.setSoftScore((int) (deltaScore.getSoftScore() * delugeRatio));
                waterScore = waterScore.addScore(deltaScore);
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


    @Override
    public void run() {
        historyOperatorList = run(solution, iteration, tabuSize, delugeRatio);
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

    public double getDelugeRatio() {
        return delugeRatio;
    }
    public void setDelugeRatio(double delugeRatio) {
        this.delugeRatio = delugeRatio;
    }

/* class ends */
}
