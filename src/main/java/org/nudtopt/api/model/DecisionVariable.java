package org.nudtopt.api.model;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface DecisionVariable {

    boolean nullable()   default false;      // 本变量是否可空
    double probability() default 0;          // 本变量为空的概率

    String sortIn()      default "";         // 本变量为在其他决策变量中的优先级顺序

}
