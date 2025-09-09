package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint;

/**
 * 一个抽象类，抽象约束的实现，一个约束包含检查约束需要的资源，和要检查约束的对象
 *
 * @author Xu Shilong
 * @version 1.0
 */
public interface Constraint<T> {

    /**
     * 检查被约束对象是否满足boolCheck所规定的约束
     *
     * @param constrainedObjects 被约束对象
     * @return ture:满足; false:不满足
     */
    default boolean boolCheck(T constrainedObjects) {
        throw new UnsupportedOperationException("boolCheck 约束检查方法未实现！");
    }

    /**
     * 检查被约束对象是否满足intCheck所规定的约束
     *
     * @param constrainedObjects 被约束对象
     * @return 0:满足; 其他值:不满足
     */
    default int intCheck(T constrainedObjects) {
        throw new UnsupportedOperationException("intCheck 约束检查方法未实现！");
    }

}
