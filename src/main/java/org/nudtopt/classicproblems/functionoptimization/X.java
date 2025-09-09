package org.nudtopt.classicproblems.functionoptimization;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.api.tool.function.Tool;

import java.util.ArrayList;
import java.util.List;

public class X extends DecisionEntity {

    @DecisionVariable
    private Double value;                       // 决策变量
    private List<Double> optionalValueList;     // 决策变量取值范围

    private double minValue;                    // 最小取值
    private double maxValue;                    // 最大取值
    private double precision = 0.01;            // 取值精度

    // getter & setter
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    public List<Double> getOptionalValueList() {
        return optionalValueList;
    }
    public void setOptionalValueList(List<Double> optionalValueList) {
        this.optionalValueList = optionalValueList;
    }

    public double getMinValue() {
        return minValue;
    }
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getPrecision() {
        return precision;
    }
    public void setPrecision(double precision) {
        this.precision = precision;
    }


    /**
     * 创建xList
     * @author          杜永浩
     * @param n         维度 (x的数量)
     * @param minValue  x的最小取值
     * @param maxValue  x的最大取值
     * @param precision x的取值精度
     * @return
     */
    public List<X> createXList(int n, double minValue, double maxValue, double precision) {
        if(precision <= 0)     precision = this.precision;
        logger.info("正创建 -> 函数中 x 的取值范围 [" + minValue + ", " + maxValue + " ], 精度 " + precision + " ... ... ");
        List<Double> optionalValueList = new ArrayList<>();
        for(double value = minValue ; value <= maxValue ; value += precision) {     // 根据取值精度, 遍历取值范围
            optionalValueList.add(value);                                           // 将可取值记录为决策变量取值范围
        }
        List<X> xList = new ArrayList<>();
        for(int i = 0 ; i < n ; i ++) {
            X x = new X();
            x.setId((long) xList.size());
            x.setMinValue(minValue);
            x.setMaxValue(maxValue);
            x.setPrecision(precision);
            x.setOptionalValueList(optionalValueList);                              // 决策变量取值范围赋值
            x.setValue(Tool.randomFromList(optionalValueList));                     // 随机赋默认值
            xList.add(x);
        }
        logger.info("已创建 -> " + n + " 维 x 及其取值范围, 并赋随机初始值 ");
        return xList;
    }




/* class ends */
}
