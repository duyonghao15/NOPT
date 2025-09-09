package org.nudtopt.api.constraint;

import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.tool.comparator.ScoreComparator;


public class Score extends NumberedObject {

    private long hardScore;
    private long meanScore;
    private long softScore;

    // getter & setter
    public long getHardScore() {
        return hardScore;
    }
    public void setHardScore(long hardScore) {
        this.hardScore = hardScore;
    }

    public long getMeanScore() {
        return meanScore;
    }
    public void setMeanScore(long meanScore) {
        this.meanScore = meanScore;
    }

    public long getSoftScore() {
        return softScore;
    }
    public void setSoftScore(long softScore) {
        this.softScore = softScore;
    }

    public void addHardScore(double score) {
        hardScore += score;
    }

    public void addMeanScore(double score) {
        meanScore += score;
    }

    public void addSoftScore(double score) {
        softScore += score;
    }

    public void zero() {              // score清零
        hardScore = 0;
        meanScore = 0;
        softScore = 0;
    }

    @Override // 浅克隆, 也等于深克隆
    public Score clone() {
        try {
            return (Score) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }   return null;
    }

    public void clone(Score score) {      // 复制一个score的分值
        hardScore = score.getHardScore();
        meanScore = score.getMeanScore();
        softScore = score.getSoftScore();
    }

    public Score addScore(Score score) {  // 加: 返回两个score的和
        Score newScore = new Score();
        newScore.setHardScore(hardScore + score.getHardScore());
        newScore.setMeanScore(meanScore + score.getMeanScore());
        newScore.setSoftScore(softScore + score.getSoftScore());
        return newScore;
    }

    public Score cutScore(Score score) {  // 减: 返回两个score的差
        Score newScore = new Score();
        newScore.setHardScore(hardScore - score.getHardScore());
        newScore.setMeanScore(meanScore - score.getMeanScore());
        newScore.setSoftScore(softScore - score.getSoftScore());
        return newScore;
    }

    public double divideScore(Score score) { // 除: 返回两个Score的商
        if     (hardScore != score.getHardScore() && score.getHardScore() != 0)  return 1.0 * hardScore / score.getHardScore();
        else if(meanScore != score.getMeanScore() && score.getMeanScore() != 0)  return 1.0 * meanScore / score.getMeanScore();
        else if(softScore != score.getSoftScore() && score.getSoftScore() != 0)  return 1.0 * softScore / score.getSoftScore();
        else                                                                     return 1.0;
    }

    public int compareTo(Score score) {
        return new ScoreComparator().compare(this, score);
    }

    public String toString() {
        return "(" + hardScore + "hard/" + meanScore + "mean/" + softScore + "soft)";
    }

/* class ends */
}
