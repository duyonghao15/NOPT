package org.nudtopt.realworldproblems.satelliterangescheduling.constraint.decodeTZB;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import java.util.List;

public class CalScore {


    /**
     * 计算任务收益值
     * @param taskList 任务清单
     * @return         收益值
     */
    public static Score calObjective(List<Task> taskList) {
        long mean = 0;
        long soft = 0;
        for(Task task : taskList) {
            Range range = task.getRange();
            if(range != null) {
                mean += 1;
                soft -= range.getCapability();
            }
        }
        /* function ends */
        Score score = new Score();
        score.addMeanScore(mean);
        // score.addSoftScore(soft);
        return score;
    }


    /**
     * 计算测控任务得分
     */
    public static long calTTCScore(int scheduledNum, int totalNum) {
        double percent = scheduledNum * 1.0 / totalNum * 100;
        double score = 0;
        if(percent < 94) {
            return 0;
        } else if (percent < 99) {
            score = 1;
            score += (percent - 94) * 1;
        } else if (percent < 99.9) {
            score = 1;
            score += (99 - 94) * 1;
            score += (percent - 99) / 0.1 * 2;
        } else {
            score = 1;
            score += (99 - 94) * 1;
            score += (99.9 - 99) / 0.1 * 2;
            score += (percent - 99.9) / 0.01 * 4;
        }
        return Math.round(score);
    }


    /**
     * 计算数传任务得分
     */
    public static long calDDTScore(int scheduledNum, int totalNum) {
        double percent = scheduledNum * 1.0 / totalNum * 100;
        double score = 0;
        if(percent < 90) {
            return 0;
        } else if (percent < 94) {
            score = 1;
            score += (percent - 90) * 1;
        } else if (percent < 99) {
            score = 1;
            score += (94 - 90) * 1;
            score += (percent - 94) / 0.1 * 4;
        } else {
            score = 1;
            score += (94 - 90) * 1;
            score += (99 - 94) / 0.1 * 4;
            score += (percent - 99) / 0.01 * 8;
        }
        return Math.round(score);
    }



/* class ends */
}
