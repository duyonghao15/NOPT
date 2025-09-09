package org.nudtopt.api.algorithm;

import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.operator.RandomMove;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.SolutionComparator;
import org.nudtopt.api.tool.function.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Algorithm implements Runnable {

    protected String name;                                                  // 算法名称
    protected String type;                                                  // 算法类型(启发式规则, 邻域搜索算法, 进化算法)
    protected Boolean highlight = false;                                    // 是否推荐(默认否)
    protected long iteration;                                               // 迭代次数
    protected long createTime = System.currentTimeMillis() / 1000;          // 算法创建时间(秒)
    protected long maxRunTime;                                              // 最大运行时间(秒)
    protected long runningTime;                                             // 实际运行时间(秒)
    protected long noImproveIteration;                                      // 未改进的迭代次数
    protected long maxNoImproveItera = 3000000;                              // 最大未改进的迭代次数(超过即收敛)

    protected Solution solution;                                            // 输入: 唯一的solution
    protected Solution bestSolution;                                        // 复制；历史最优解
    protected Solution currSolution;                                        // 复制: 当前解 (邻域动作前的)
    protected List<Operator> operatorList = Arrays.asList(new RandomMove());// 输入: 算子列表(默认随机move)
    protected List<Operator> historyOperatorList = new ArrayList<>();       // 输出: 历史算子(主要用于局部搜索算法)
    protected List<Solution> historySolutionList = new ArrayList<>();       // 输出: 历史solution(主要用于进化算法)
    protected int historySize = 1000000;                                    //       历史记录的最大数量

    protected static String state = "start";                                // 状态(start, wait, stop)
    protected static ThreadLocal<String> threadState = new ThreadLocal<>(); // 状态(线程变量)
    protected static JProgressBar progressBar;                              // 进度条
    protected final static Logger logger = LoggerFactory.getLogger(Algorithm.class);

    // #############################################################
    // ######################  以下是内置方法  ######################
    // #############################################################


    /** 1
     * 获取n个历史解(数字编码)
     * @author    杜永浩
     * @param num 需要获得历史解的数量
     * @return    n个历史解
     */
    public List<Solution> getHistorySolutionList(int num) {
        List<Solution> historySolutionList = new ArrayList<>();
        Score historyScore = solution.getScore().clone();                                  // 记录原解score
        boolean digital = solution.getDecisionMatrix() != null;                            // solution是否为数字型
        // a. 逆序撤回至历史解
        int endId = historyOperatorList.size() - num;
        if(num <= 0 || endId <= 0)   endId = 0;

        for(int i = historyOperatorList.size() - 1 ; i >= endId ; i --) {
            solution.encode();                                                             // 1. 编码
            Solution historySolution = solution.matrixClone();                             // 2. clone数字型solution
            historySolution.setScore(historyScore);
            historySolutionList.add(historySolution);
            Operator operator = historyOperatorList.get(i);
            operator.undo();                                                               // 3. 撤回
            historyScore = historyScore.cutScore(operator.getGapScore());                  // 4. 评分递减(不用重新算)
        }
        // b. 顺序返回终解
        for(int i = endId ; i <= historyOperatorList.size() - 1 ; i ++) {
            Operator operator = historyOperatorList.get(i);
            operator.redo();
        }
        if(!digital)    solution.setDecisionMatrix(null);
        return this.historySolutionList = historySolutionList;
    }



    /** 2
     * 获取n个历史评分
     * @author     杜永浩
     * @param num  需要获得历史评分的数量, 若n=0, 则为全部
     * @param undo 是否在此过程中需要撤回
     * @return     n个历史评分
     */
    public List<Score> getHistoryScoreList(int num, boolean undo) {
        List<Score> scoreList = new ArrayList<>();
        // a. 进化类算法, 通过历史Solution获取评分
        if(historySolutionList.size() > 0) {
            int endId = historySolutionList.size() - num;
            if(num <= 0 || endId <=0 )   endId = 0;
            for(int i = historySolutionList.size() - 1 ; i >= endId ; i --) {
                scoreList.add(historySolutionList.get(i).getScore());
            }
            return scoreList;
        }
        // b. 局部搜索算法, 通过历史Move获取评分
        scoreList.add(solution.getScore().clone());
        int endId = historyOperatorList.size() - num;
        if(num <= 0 || endId <=0 )   endId = 0;

        for(int i = historyOperatorList.size() - 1 ; i >= endId ; i --) {
            Operator operator = historyOperatorList.get(i);
            Score currentScore = scoreList.get(scoreList.size() - 1);
            Score preScore = currentScore.cutScore(operator.getGapScore());
            scoreList.add(preScore);
            if(undo) {
                operator.undo();                // 逆序撤销move, 逐步返回原解
                solution.updateScore(operator); // 更新解
                if(preScore.compareTo(solution.getScore()) != 0) {
                    logger.error("\t" + "增量式undo评分计算错误");
                }
            }
        }
        return scoreList;
    }



    /** 3
     * 算法所在线程暂停和继续
     * @author     杜永浩
     * @param wait 是否暂停
     */
    public void wait(boolean wait) {
        synchronized (threadState) {
            if(wait) {
                try {
                    threadState.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else    threadState.notifyAll();
        }
    }



    /** 4
     * 更新: 状态/进度条等
     * @author 杜永浩
     * @return 当前最新状态
     */
    public String update() {
        boolean stop = false;
        this.runningTime = System.currentTimeMillis() / 1000 - createTime;                  // 当前运行时间
        if(getState().equals("wait"))    wait(true);
        if(progressBar != null)     progressBar.setValue(progressBar.getValue() + 1);
        if(createTime > 0 && maxRunTime > 0) {                                              // 判断是否超过最大运行时间
            if(runningTime >= maxRunTime) {
                stop = true;
            }
        }
        // 达到收敛(一定时间未改进)也停止
        if(getIteration() != 0) {
            maxNoImproveItera = Math.min(getIteration() / 5, maxNoImproveItera);            // 1/5最大迭代时间未改进, 也视为收敛
        }
        if(historyOperatorList.size() > 0) {
            Operator lastOperator = historyOperatorList.get(historyOperatorList.size() - 1);// 上一个算子
            Score gapScore = lastOperator.getGapScore();                                    // 与上一个解的score gap
            if(gapScore.getHardScore() == 0 && gapScore.getMeanScore() == 0 && gapScore.getSoftScore() == 0) {
                noImproveIteration ++;
                if(noImproveIteration > maxNoImproveItera) {                                // 最大迭代时间未改进, 视为收敛
                    stop = true;
                }
                if(lastOperator.getOperatorType() != null) {
                    lastOperator.getOperatorType().setContribution(lastOperator.getOperatorType().getContribution() + 1);   // 更新算子贡献度: 无改进+1分
                }
            } else {
                noImproveIteration = 0;
                if(lastOperator.getOperatorType() != null) {
                    lastOperator.getOperatorType().setContribution(lastOperator.getOperatorType().getContribution() + 2);   // 更新算子贡献度: 有改进+2分
                }
            }
        }
        /* stop */
        if(stop) {
            setState("stop");
            threadState.remove();
            logger.debug("\t" + this + "\tterminates after running " + runningTime + " s !");
        }
        return getState();
    }



    /** 5
     * 获取一个move(的方法)
     * @author                 杜永浩
     * @param solution         当前解
     * @param tabuOperatorList 被禁忌的算子列表
     * @return                 未被禁忌的一个算子
     */
    protected Operator moveAndScore(Solution solution, List<Operator> tabuOperatorList) {
        Operator operator = Tool.randomFromList(operatorList);
        Operator actOperator = operator.moveAndScore(solution, tabuOperatorList);
        if(actOperator == null) {
            actOperator = moveAndScore(solution, tabuOperatorList);
        }
        actOperator.setOperatorType(operator);
        return actOperator;
    }



    /** 6.1
     * 获取历史最优解
     * @return 历史最优解
     */
    protected Solution getBestSolution() {
        List<Solution> solutionList = new ArrayList<>(historySolutionList);
        solutionList.sort(new SolutionComparator()); // 降序排列
        return solutionList.get(0);
    }



    /** 6.2
     * 根据历史评分更新最优解
     */
    protected void updateInputSolution() {
        Solution bestSolution = getBestSolution();
        solution.decode(bestSolution.getDecisionMatrix());
        solution.setScore(bestSolution.getScore().clone());
    }


    @Override
    public void run() {}

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "");
    }


    // #############################################################
    // #####################  getter & setter  #####################
    // #############################################################

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

    public long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getMaxRunTime() {
        return maxRunTime;
    }
    public void setMaxRunTime(long maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    public long getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(long runningTime) {
        this.runningTime = runningTime;
    }

    public long getNoImproveIteration() {
        return noImproveIteration;
    }
    public void setNoImproveIteration(long noImproveIteration) {
        this.noImproveIteration = noImproveIteration;
    }

    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public List<Operator> getOperatorList() {
        return operatorList;
    }
    public void setOperatorList(List<Operator> operatorList) {
        this.operatorList = operatorList;
    }

    public List<Operator> getHistoryOperatorList() {
        return historyOperatorList;
    }
    public void setHistoryOperatorList(List<Operator> historyOperatorList) {
        this.historyOperatorList = historyOperatorList;
    }

    public List<Solution> getHistorySolutionList() {
        return historySolutionList;
    }
    public void setHistorySolutionList(List<Solution> historySolutionList) {
        this.historySolutionList = historySolutionList;
    }

    public int getHistorySize() {
        return historySize;
    }
    public void setHistorySize(int historySize) {
        this.historySize = historySize;
    }

    public String getState() {
        return threadState.get() == null ? state : threadState.get();
    }
    public void setState(String state) {
        this.state = state;
    }

    public static ThreadLocal<String> getThreadState() {
        return threadState;
    }
    public static void setThreadState(ThreadLocal<String> threadState) {
        Algorithm.threadState = threadState;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    // 获取算法当前版本
    public double getVersion() {
       long month = Tool.getTime(new Date(System.currentTimeMillis())).get(1);
       long hour = Tool.getTime(new Date(System.currentTimeMillis())).get(3);
       long v1 = month - getName().length() / 3;
       long v2 = (hour + getName().length()) / 2;
       v1 = v1 < 1 ? 1 : v1;
       return v1 + v2 * 0.1;
    }


/* class ends */
}
