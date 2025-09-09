package org.nudtopt.api.constraint;

import java.util.List;

public class MultiScore extends Score {

    private List<Score> scoreList;


    // getter & setter
    public List<Score> getScoreList() {
        return scoreList;
    }
    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }


    public boolean dominate(MultiScore multiScore) {
        int scoreSize = scoreList.size();
        for(int i = 0 ; i < scoreSize ; i ++) {
            Score thisScore = scoreList.get(i);
            Score thatScore = multiScore.getScoreList().get(i);
            int compare = thisScore.compareTo(thatScore);   // 1(大于), 0(等于) , -1(小于)
            if(compare < 0)   return false;
        }
        return true;
    }

}
