package org.nudtopt.api.algorithm.rule;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Deconflict extends Algorithm {

    private String name = "冲突消解(Deconflicting)";
    private String type = "启发式规则";


    public List<Operator> run(Solution solution, DecisionEntity decisionEntity) {
        this.solution = solution;

        // 1. obtain conflicting entities
        List<DecisionEntity> conflictEntityList = solution.getConstraint().getConflictEntityList(solution, decisionEntity);
        // 后续用这个: Map<DecisionEntity, String> conflictEntityMap = solution.getConstraint().getConflictEntityMap(solution, decisionEntity);
        if(conflictEntityList.size() > 0)   logger.info("出现冲突:\t" + conflictEntityList);
        else                                return historyOperatorList;

        // 2. deconflict
        for(DecisionEntity conflictEntity : conflictEntityList) {
            for(String name : conflictEntity.getDecisionVariableList()) {
                Move move = Move.moveToNull(conflictEntity, name);
                if(move == null)    continue;
                solution.updateScore(move);
                historyOperatorList.add(move);
            }
            logger.debug("\t" + this + "\t已取消:\t" + conflictEntity +"\t obtains score: " + solution.getScore());
            if(solution.getScore().getHardScore() == 0)   break;    // 无冲突即可跳出
        }

        // 3. rearrange                                             // 因一些任务原本也许可不取消(误伤), 故重新安排试一试
        for(int i = historyOperatorList.size() - 1 ; i >= 0 ; i --) {
            Operator operator = historyOperatorList.get(i);
            operator.undo();                                        // 撤销取消操作, 重新安排
            solution.updateScore(operator);
            if(solution.getScore().getHardScore() == 0) {           // a. 若无冲突
                historyOperatorList.remove(i);                      // 删除该move
                logger.debug("\t" + this + "\t重新安排:\t" + operator.getDecisionEntityList() + "\tby move: " + operator +"\t obtains score: " + solution.getScore());
            } else {                                                // b. 若有冲突
                operator.redo();                                    //    还是保留取消操作
                solution.updateScore(operator);
            }
        }

        // 4. check
        /*solution.checkScore();*/
        if(solution.getScore().getHardScore() == 0) {
            logger.info("冲突已消解:\t" + solution.getScore() + "\tby moves" + historyOperatorList);
        } else {
            logger.info("已取消全部相关任务, 但冲突仍无法全部消解:\t" + solution.getScore() + "\tby moves" + historyOperatorList);
        }
        return historyOperatorList;
    }


    // move & deconflict
    public List<Operator> run(DecisionEntity decisionEntity, String name, Object newVariable) {
        Move move = Move.move(decisionEntity, name, newVariable);
        historyOperatorList.add(move);
        if(move != null){
            solution.updateScore(move);
            List<Operator> operatorList = new Deconflict().run(solution, decisionEntity);    // deconflict
            historyOperatorList.addAll(operatorList);                                        // add moves
            logger.debug("\t" + this + "\t已修改:\t" + decisionEntity +"\t obtains score: " + solution.getScore() + "\tby move\t" + move);
        }
        return historyOperatorList;
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

/* class ends */
}
