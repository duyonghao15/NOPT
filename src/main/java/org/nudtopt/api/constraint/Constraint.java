package org.nudtopt.api.constraint;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.DecisionVariable;
import org.nudtopt.api.model.NumberedObject;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.comparator.PriorityComparator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Constraint extends NumberedObject {

    protected String path;                                              // 约束文件路径
    protected Solution solution;                                        // 解
    protected boolean incremental;                                      // 是否为增量式约束
    protected Operator lastOperator;                                    // 上一个算子
    protected Operator operator;                                        // 当前算子

    /**
     * 计算收益值, 供子类重写
     */
    public Score calScore() {
        /* calculate scores */
        return new Score();
    }
    /**
     * 更新收益值, 作为通用化接口被引擎调用
     */
    public Score updateScore() {
        // 1. 算子变为上一个算子
        lastOperator = operator;
        // 2. 若算子操作了list, 则更新list排序及关联关系
        if(operator != null && operator.isAffectSort()) {
            try {
                updateAllocationList(operator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 3. 计算收益值 (开放接口重写)
        Score score = calScore();
        solution.setScore(score);
        return score;
    }


    // getter & setter
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public boolean isIncremental() {
        return incremental;
    }
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public Operator getLastOperator() {
        return lastOperator;
    }
    public void setLastOperator(Operator lastOperator) {
        this.lastOperator = lastOperator;
    }

    public Operator getOperator() {
        return operator;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public List<DecisionEntity> getConflictEntityList(Solution solution, DecisionEntity decisionEntity) {
        return new ArrayList<>();
    }
    public Map<DecisionEntity, String> getConflictEntityMap(Solution solution, DecisionEntity decisionEntity) {
        return new HashMap<>();
    }

    /**
     * 根据一个算子(多个move)
     * 更新决策实体->决策变量(分配资源)的分配情况
     * 以及分配资源中决策实体Decisionlist的元素及顺序
     */
    public void updateAllocationList(Operator operator) throws Exception{
        // 1. 按照from/to resource, 把move分分类
        Map<Object, List<Move>> oldMap = new HashMap<>();                                                               // 记录原资源, move的起点, 如车辆vehicle_1
        Map<Object, List<Move>> newMap = new HashMap<>();                                                               // 记录新资源, move的终点, 如车辆vehicle_2
        for(Move move : operator.getMoveList()) {
            DecisionEntity entity = move.getDecisionEntity();
            String resource = entity.getClass().getDeclaredField(move.getName()).getAnnotation(DecisionVariable.class).sortIn();// 分配的资源, 如车辆vehicle
            Object oldResource = resource.length() > 0 ? entity.getVariable(resource) : move.getDoingOldValue();        // 变量为priority 或 资源(如车辆vehicle)
            Object newResource = resource.length() > 0 ? oldResource                  : move.getDoingNewValue();
            if(oldResource != null) {
                if(!oldMap.containsKey(oldResource))     oldMap.put(oldResource, new ArrayList<>());
                oldMap.get(oldResource).add(move);
            }
            if(newResource != null) {
                if(!newMap.containsKey(newResource))     newMap.put(newResource, new ArrayList<>());
                newMap.get(newResource).add(move);
            }
        }
        // 2. 针对fromResource, 执行删除move
        PriorityComparator comparator = solution.getComparator();
        String entityName = operator.getMoveList().get(0).getDecisionEntity().getClassName();
        for(Object resource : oldMap.keySet()) {
            List<Move> moveList = oldMap.get(resource);
            List<DecisionEntity> entityList = (List) resource.getClass().getDeclaredMethod("get" + entityName + "List").invoke(resource);
            comparator.removeAndSort(entityList, moveList);
        }
        // 3. 针对toResource, 执行插入move
        for(Object resource : newMap.keySet()) {
            List<Move> moveList = newMap.get(resource);
            List<DecisionEntity> entityList = (List) resource.getClass().getDeclaredMethod("get" + entityName + "List").invoke(resource);
            comparator.insertAndSort(entityList, moveList);
        }
        /* function ends */
    }


/* class ends */
}
