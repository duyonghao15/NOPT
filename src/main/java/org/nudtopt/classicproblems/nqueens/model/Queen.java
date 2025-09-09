package org.nudtopt.classicproblems.nqueens.model;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;

import java.util.ArrayList;
import java.util.List;

public class Queen extends DecisionEntity {

    @DecisionVariable
    private Square square;                                          // 决策变量: 棋格

    private List<Square> optionalSquareList = new ArrayList<>();    // 决策变量取值范围: 该皇后可选的棋格 (设第i个皇后只能放在第i列, x相同)


    // getter & setter
    public Square getSquare() {
        return square;
    }
    public void setSquare(Square square) {
        this.square = square;
    }

    public List<Square> getOptionalSquareList() {
        return optionalSquareList;
    }
    public void setOptionalSquareList(List<Square> optionalSquareList) {
        this.optionalSquareList = optionalSquareList;
    }


    /**
     * 判断两个皇后是否相互冲突
     * @author        杜永浩
     * @param queen_1 第一个皇后
     * @param queen_2 第二个皇后
     * @return        是否相关冲突/攻击
     */
    public static boolean attack(Queen queen_1, Queen queen_2) {
        if(queen_1 == queen_2)  return false;
        Square square_1 = queen_1.getSquare();
        Square square_2 = queen_2.getSquare();
        if(square_1.getX() == square_2.getX()) {
            return true;        // a. 同一列, x相同, 即冲突 (注: 根据本问题决策变量在同一列内取值的方式, 不可能冲突)
        }
        if(square_1.getY() == square_2.getY()) {
            return true;        // b. 同一行, y相同, 也冲突
        }
        if(Math.abs(square_1.getX() - square_2.getX()) == Math.abs(square_1.getY() - square_2.getY())) {
            return true;        // c. 斜对角, 即斜率为1
        }
        return false;
    }


}
