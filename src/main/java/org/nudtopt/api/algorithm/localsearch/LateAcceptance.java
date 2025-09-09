package org.nudtopt.api.algorithm.localsearch;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class LateAcceptance extends Algorithm {

    private String name             = "逾期接受算法(LA)";
    private String type             = "局部搜索算法";
    private Boolean highlight       = true;
    private long iteration          = 5000;
    private List<Operator> tabuList = new ArrayList<>();
    private List<Score> lateList    = new ArrayList<>();    // 逾期表, 存储最近历史评分的list
    private int tabuSize            = 5;
    private int lateSize            = 5;                    // 逾期表长度


    public List<Operator> run(Solution solution, long iteration, int tabuSize, int lateSize) {
        this.solution = solution;
        int trappedIteration = 0;
        if(lateList.size() == 0) {
            for(int i = 0 ; i < lateSize ; i ++) {                // 初始化late list
                lateList.add(solution.getScore().clone());
            }
        }

        for(int i = 0 ; i < iteration ; i ++) {
            // 1. move and score
            Score oldScore = solution.getScore().clone();          // 注意: 记录旧score
            Operator operator = moveAndScore(solution, tabuList);
            trappedIteration ++;
            // 2. accept?
            // int lateIndex = getLateIndex(lateList.size(), i, iteration, trappedIteration);
            boolean accept = solution.getScore().compareTo(oldScore) >= 0 ||
                             solution.getScore().compareTo(lateList.get(0)) >= 0;

            // 3. accept or reject
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

            // 4. update late score list
            lateList.add(solution.getScore().clone());            // 当前解插入FIFO的lateList
            Tool.listFIFO(lateList, lateSize);
            /* loop ends */
            if(update().equals("stop"))     break;
        }
        /* function ends */
        return historyOperatorList;
    }


    @Override
    public void run() {
        historyOperatorList = run(solution, iteration, tabuSize, lateSize);
    }


    public int getLateIndex(int lateSize, int iteration, long maxIteration, int trappedIteration) {
        // 迭代进程越多, 逾期表应该缩短, index->size, (减少回退, 快速收敛)
        // trap越多, 逾期表应该拉长, index->0, 帮助回退
        double iterCoefficient = iteration * 1.0 / maxIteration;
        double trapCoefficient = Math.max((10 - trappedIteration) * 1.0 / 10, 0); // 不小于0
        double coefficient = (iterCoefficient + trapCoefficient) / 2;
        int lateIndex = (int) Math.round(lateSize * coefficient);
        return Math.min(Math.max(0, lateIndex), lateSize - 1);
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

    public List<Score> getLateList() {
        return lateList;
    }
    public void setLateList(List<Score> lateList) {
        this.lateList = lateList;
    }

    public int getLateSize() {
        return lateSize;
    }
    public void setLateSize(int lateSize) {
        this.lateSize = lateSize;
    }

/* class ends */
}
