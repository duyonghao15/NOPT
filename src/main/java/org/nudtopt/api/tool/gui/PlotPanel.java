package org.nudtopt.api.tool.gui;

import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.model.Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlotPanel extends JPanel {

    protected Solution solution;            // 当前解
    protected List<Score> scoreList;        // 历史评分值
    protected long maxIteration;            // 最大迭代次数
    protected long nowIteration;            // 当前迭代次数

    @Override
    public void paintComponent(Graphics g){
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getWidth(), getHeight());  // 着白底色
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth(), getHeight());  // 画黑边框

        if(scoreList == null || scoreList.size() <= 1)   return;
        List<Score> scoreList = new ArrayList<>(this.scoreList);
        Collections.reverse(scoreList);
        int firstScore = (int)scoreList.get(0).getMeanScore();
        int lastScore = (int)scoreList.get(scoreList.size() - 1).getMeanScore();
        int yMin = Math.min(Math.abs(firstScore), Math.abs(lastScore));
        int yMax = Math.max(Math.abs(firstScore), Math.abs(lastScore)) + 1;
        if(lastScore > 0)   yMax = (int)(yMax * 1.01);   // 为了不画到边缘上
        else                yMin = (int)(yMin / 1.01);
        int gap = 5;

        // 画线
        for(int i = 0 ; i < scoreList.size() - gap ; i += gap) {
            int score1 = (int)scoreList.get(i).getMeanScore();
            int score2 = (int)scoreList.get(i + gap).getMeanScore();
            score1 = Math.abs(score1);
            score2 = Math.abs(score2);
            // a. 横向等比(当前迭代/最大迭代)
            /*int x = nowIteration - scoreList.size() + i;
            int xMax = maxIteration;*/
            // b. 横向占满
            int x = i;
            int xMax = scoreList.size();

            if(xMax == 0 || yMax == 0)       return;
            int x1 = getWidth()  * x / xMax;
            int y1 = getHeight() * (score1 - yMin) / (yMax - yMin);
            int x2 = getWidth()  * (x + gap) / xMax;
            int y2 = getHeight() * (score2 - yMin) / (yMax - yMin);
            g.drawLine(x1, getHeight() - y1, x2, getHeight() - y2);
        }

        // 画刻度(10个)
        int num = 10;
        for(int i = 0 ; i <= num ; i ++) {
            int x =     i * getWidth() / num;
            int iter =  i * scoreList.size() / num;
            int y =     i * getHeight() / num;
            int score = i * (yMax - yMin) / num + yMin;
            g.drawString(String.valueOf(iter), x, getHeight());             // x轴刻度
            g.drawString(String.valueOf(score), 0, getHeight() - y); // y轴刻度
        }
        /* function ends */
    }


    // getter & setter
    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }
    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public long getMaxIteration() {
        return maxIteration;
    }
    public void setMaxIteration(long maxIteration) {
        this.maxIteration = maxIteration;
    }

    public long getNowIteration() {
        return nowIteration;
    }
    public void setNowIteration(long nowIteration) {
        this.nowIteration = nowIteration;
    }

/* class ends */
}
