package org.nudtopt.api.tool.comparator;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.tool.function.Tool;

import java.util.*;
import java.util.stream.Collectors;

public class PriorityComparator implements Comparator<DecisionEntity> {

    protected String variable = "priority";

    public String getVariable() {
        return variable;
    }
    public void setVariable(String variable) {
        this.variable = variable;
    }


    @Override
    public int compare(DecisionEntity entity_1, DecisionEntity entity_2) {
        Integer order_1 = (Integer) entity_1.getDecisionVariable(variable);
        Integer order_2 = (Integer) entity_2.getDecisionVariable(variable);
        double temp;
        // 1. 按index升序排列
        if(order_1 != null && order_2 != null) {
            temp = order_1 - order_2;
        } else if (order_1 == null) {
            temp = -1;
        } else {
            temp = 1;
        }
        // 2. 若index相同, 则按id排序
        if(temp == 0) {
            long id_1 = entity_1.getId();
            long id_2 = entity_2.getId();
            temp = id_1 - id_2;
        }
        return (int)temp;
    }


    /**
     * 根据实际顺序, 重设index值
     */
    public void reset(List<DecisionEntity> entityList) {
        variable += entityList.get(0).getClass().getName().replaceAll(".*\\.", "");
        for(int i = 0 ; i < entityList.size() ; i ++) {
            DecisionEntity entity = entityList.get(i);
            entity.setDecisionVariable(variable, i);
        }
    }


    /**
     * 针对一个entityList, (多次)move更改priority
     * 把涉及到的entity从list中剔除
     * 增量式更新, 快速计算
     * @author           杜永浩
     * @param entityList 实体列表
     * @param moveList   改变priority值的move集合 (如: 1->2, null->1, 1->null)
     * @return           重新排序后, 未发生变化的两端 (anchor) entity
     *                   即这两个entity, 及其前后的entity都未发生变化
     */
    public <D extends DecisionEntity> List<D> removeAndSort(List<D> entityList, List<Move> moveList) {
        List<Move> removeList = moveList.stream()
                .filter(m -> m.getDoingOldValue() != null)
                // .sorted(Comparator.comparing((Move m) -> (int) m.getDoingOldValue()).thenComparing((Move m) -> m.getDecisionEntity().getId()))  // 按原优先级升序
                .collect(Collectors.toList());
        DecisionEntity anchorEntity_1 = removeList.size() == 0 ? null : removeList.get(0).getDecisionEntity().getLastEntity();
        DecisionEntity anchorEntity_2 = removeList.size() == 0 ? null : removeList.get(removeList.size() - 1).getDecisionEntity().getNextEntity();
        for(Move move : removeList) {
            remove(entityList, (D) move.getDecisionEntity());
        }
        // check(entityList, null);
        return Arrays.asList((D) anchorEntity_1, (D) anchorEntity_2);
    }


    /**
     * 把entity根据priority值, 快速插入一个list中
     * 增量式更新, 快速计算
     * @author           杜永浩
     * @param entityList 实体列表
     * @param moveList   改变priority值的move集合
     * @return           重新排序后, 未发生变化的两端 (anchor) entity
     */
    public <D extends DecisionEntity> List<D> insertAndSort(List<D> entityList, List<Move> moveList) {
        // 1. 整理出待插入的entity
        List<DecisionEntity> insertList = new ArrayList<>();
        for(Move move : moveList) {
            DecisionEntity insertEntity = move.getDecisionEntity();
            if(!insertList.contains(insertEntity)) {
                insertList.add(move.getDecisionEntity());
            }
        }
        insertList.sort(this);
        // 2. 按顺序插入entity
        int insertIndex = 0;
        for(DecisionEntity insertEntity : insertList) {
            // a. list为空, 直接插入
            if(entityList.size() == 0) {
                add(entityList, (D) insertEntity, 0);
                continue;
            }
            // b. list非空, 比大小插入
            for(int i = insertIndex ; i < entityList.size() ; i ++) {
                DecisionEntity entity = entityList.get(i);
                if(compare(insertEntity, entity) < 0) {             // 若insertEntity优先级更小, 则插在当前entity之前
                    add(entityList, (D) insertEntity, i);
                    insertIndex = i + 1;
                    break;
                }
                if(i == entityList.size() - 1) {
                    add(entityList, (D) insertEntity, entityList.size());
                    insertIndex = i + 1;
                    break;
                }
            }
        }
        DecisionEntity anchorEntity_1 = insertList.size() == 0 ? null : insertList.get(0).getLastEntity();
        DecisionEntity anchorEntity_2 = insertList.size() == 0 ? null : insertList.get(insertList.size() - 1).getNextEntity();
        /* function ends */
        // check(entityList, null);
        return Arrays.asList((D) anchorEntity_1, (D) anchorEntity_2);
    }



    /**
     * 从先后相连的entityList中, 删除一个entity
     * 并更新entity先后相连关系
     * @param entity 待删除的entity
     */
    public static <D extends DecisionEntity> void remove(List<D> entityList, D entity) {
        entityList.remove(entity);
        DecisionEntity lastEntity = entity.getLastEntity();
        DecisionEntity nextEntity = entity.getNextEntity();
        if(lastEntity != null)      lastEntity.setNextEntity(nextEntity);
        if(nextEntity != null)      nextEntity.setLastEntity(lastEntity);
        entity.setLastEntity(null);
        entity.setNextEntity(null);
    }


    /**
     * 从先后相连的entityList中, 插入一个entity
     * 并更新entity先后相连关系
     * @param entity 待插入的entity
     * @param index  待插入的下标 (若>=list.size, 即插在最后)
     */
    public static <D extends DecisionEntity> void add(List<D> entityList, D entity, int index) {
        D lastEntity = index == 0                 ? null : entityList.get(index - 1);
        D thisEntity = index >= entityList.size() ? null : entityList.get(index);
        entityList.add(Math.min(index, entityList.size()), entity);
        entity.setLastEntity(lastEntity);
        entity.setNextEntity(thisEntity);
        if(lastEntity != null)      lastEntity.setNextEntity(entity);
        if(thisEntity != null)      thisEntity.setLastEntity(entity);
    }


    /**
     * 根据优先级对list进行排序
     * 并建立前后连接关系
     */
    public <D extends DecisionEntity> void sort(List<D> entityList) {
        entityList.sort(this);
        for(int i = 0 ; i < entityList.size() - 1 ; i ++) {
            D thisEntity = entityList.get(i);
            D nextEntity = entityList.get(i + 1);
            thisEntity.setNextEntity(nextEntity);
            nextEntity.setLastEntity(thisEntity);
        }
    }


    /**
     * 检查entityList是否按照优先级正确排序
     * 前后连接关系是否正确
     */
    public <D extends DecisionEntity> boolean check(List<D> entityList) {
        for(int i = 0 ; i < entityList.size() - 1 ; i ++) {
            D thisEntity = entityList.get(i);
            D nextEntity = entityList.get(i + 1);
            if(compare(thisEntity, nextEntity) > 0) {
                System.out.println("error: 排序错误, 请检查！");
                return false;
            }
            if(thisEntity.getNextEntity() != nextEntity) {
                System.out.println("error: 排序错误, 请检查！");
                return false;
            }
            if(nextEntity.getLastEntity() != thisEntity) {
                System.out.println("error: 排序错误, 请检查！");
                return false;
            }
        }
        return true;
    }


/* class ends */
}
