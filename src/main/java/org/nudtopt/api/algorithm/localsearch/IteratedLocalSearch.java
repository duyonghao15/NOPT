package org.nudtopt.api.algorithm.localsearch;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.rule.Deconflict;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class IteratedLocalSearch extends Algorithm {

    private String name             = "迭代局部搜索算法(ISL)";
    private String type             = "局部搜索算法";
    private Boolean highlight       = true;
    private long iteration          = 100;
    private List<Operator> tabuList = new ArrayList<>();
    private int tabuSize            = 4;
    private long innerIteration     = 100;          // 内循环次数


    public List<Operator> run(Solution solution, long iteration, int tabuSize, long innerIteration) {
        this.solution = solution;
        int trappedIteration = 0;
        for(int i = 0 ; i < iteration * innerIteration ; i ++) {
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



/*    public List<Move> run(Solution solution, long iteration, int tabuSize, long innerIteration) {
        this.solution = solution;

        for(int i = 0 ; i < iteration ; i ++) {
            // 1. local Search
            Score oldScore = solution.getScore().clone();
            List<Move> localSearchMoveList = new HillClimbing().run(solution, innerIteration);
            tabuList.addAll(localSearchMoveList);
            historyMoveList.addAll(localSearchMoveList);
            Tool.listFIFO(tabuList, tabuSize);
            Tool.listFIFO(historyMoveList, historySize);

            // 2. perturb
            List<Move> perturbMoveList = moveAndScore(solution, tabuList);

            // 3. repair
            List<Move> moveList = new ArrayList<>(perturbMoveList);
            for(Move move : perturbMoveList) {
                DecisionEntity entity = move.getDecisionEntity();
                moveList.addAll(new Deconflict().run(solution, entity));
            }

            // 4. accept?
            boolean accept;
            if(solution.getScore().compareTo(oldScore) >= 0) {     // 获得优解, 接受
                accept = true;
            } else {                                               // 获得劣解, 判断
                accept = Tool.random() < 1 - Math.log(i + 1) / Math.log(iteration);
            }                       // < p, 接受(劣解); 否则不接受 (此处也可采用退火策略)

            // 5. accept or reject
            if(accept) {
                tabuList.addAll(moveList);
                historyMoveList.addAll(moveList);
                Tool.listFIFO(tabuList, tabuSize);
                Tool.listFIFO(historyMoveList, historySize);
                logger.debug("\t" + this + "\t" + i + "th\tperturb obtains score " + solution.getScore() + "\tby " + moveList.size() + " moves\t" + moveList);
            } else {
                Move.undo(solution, moveList);             // 撤回: 实例编码(逆序撤销move), 数字编码(赋值原矩阵)
                if(solution.getConstraint().isIncremental() && solution.getScore().compareTo(oldScore) != 0) logger.error("\t" + this + "\t" + i + "th\t增量式约束: " + solution.getScore() + " != 上一步: " + oldScore);
                solution.getScore().clone(oldScore);              // solution赋旧score(注意, 只是值, score对象不能变)
            }
            *//* loop ends *//*
            if(update().equals("stop"))     break;
            logger.debug("\t" + this + "\t==========================================  第 " + i + " 代完成 !  ==========================================");
        }
        *//* function ends *//*
        return historyMoveList;
    }*/


    @Override
    public void run() {
        if(progressBar != null) progressBar.setMaximum((int)(iteration * innerIteration));
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

    public long getInnerIteration() {
        return innerIteration;
    }
    public void setInnerIteration(long innerIteration) {
        this.innerIteration = innerIteration;
    }

/* class ends */
}
