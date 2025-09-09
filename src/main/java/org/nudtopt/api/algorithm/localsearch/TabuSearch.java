package org.nudtopt.api.algorithm.localsearch;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class TabuSearch extends Algorithm {

    private String name             = "禁忌搜索算法(TS)";
    private String type             = "局部搜索算法";
    private long iteration          = 100;          // 外循环次数
    private List<Operator> tabuList = new ArrayList<>();
    private int tabuSize            = 4;
    private long innerIteration     = 100;          // 内循环次数


    public List<Operator> run(Solution solution, long iteration, int tabuSize, long innerIteration) {
        this.solution = solution;
        for(int i = 0 ; i < iteration ; i ++) {
            List<Operator> innerObjectList = new ArrayList<>();
            // A. inner loop
            for(int j = 0 ; j < innerIteration ; j ++) {
                // 1. move and score
                Score oldScore = solution.getScore().clone();         // 注意: 记录旧score
                Operator operator = moveAndScore(solution, tabuList);

                // 2. accept?
                boolean accept = solution.getScore().compareTo(oldScore) >= 0;

                // 3. accept or reject
                if(accept) {
                    innerObjectList.add(operator);
                    tabuList.add(operator);
                    historyOperatorList.add(operator);
                    Tool.listFIFO(tabuList, tabuSize);
                    Tool.listFIFO(historyOperatorList, historySize);
                } else {
                    Operator.undo(solution, operator);                // 撤回: 实例编码(逆序撤销move), 数字编码(赋值原矩阵)
                    if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) {
                        logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                    }
                    solution.getScore().clone(oldScore);              // solution赋旧score(注意, 只是值, score对象不能变)
                }
            }

            // B. logger
            logger.debug("\t" + this + "\t" + i + "th\tobtains score " + solution.getScore() + "\tby (" + innerObjectList.size() + "/" + innerIteration + ") moves\t" + innerObjectList);
            // solution.getScore().zero();                            // 还要在斟酌一下, 只能用于无会话的情况(下次一定接受劣解)
            /* loop ends */
            if(update().equals("stop"))     break;
        }
        /* function ends */
        return historyOperatorList;
    }


    @Override
    public void run() {
        historyOperatorList = run(solution, iteration, tabuSize, innerIteration);
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

    public long getInnerIteration() {
        return innerIteration;
    }
    public void setInnerIteration(long innerIteration) {
        this.innerIteration = innerIteration;
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

/* class ends */
}
