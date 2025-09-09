package org.nudtopt.api.tool.function;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.model.DecisionEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListChangeRecord {

    private List oldList = new ArrayList();                 // 原list
    private List newList = new ArrayList();                 // 新list (改变后的)
    private int[] oldIndexChangeRange = new int[]{0, 0};    // 此次改动, 原list的索引变动范围
    private int[] newIndexChangeRange = new int[]{0, 0};    // 此次改动, 新list的索引变动范围

    // getter & setter
    public List getOldList() {
        return oldList;
    }
    public void setOldList(List oldList) {
        this.oldList = oldList;
    }

    public List getNewList() {
        return newList;
    }
    public void setNewList(List newList) {
        this.newList = newList;
    }

    public int[] getOldIndexChangeRange() {
        return oldIndexChangeRange;
    }
    public void setOldIndexChangeRange(int[] oldIndexChangeRange) {
        this.oldIndexChangeRange = oldIndexChangeRange;
    }

    public int[] getNewIndexChangeRange() {
        return newIndexChangeRange;
    }
    public void setNewIndexChangeRange(int[] newIndexChangeRange) {
        this.newIndexChangeRange = newIndexChangeRange;
    }


    /**
     * 针对一个实体list, (多次)移动其中元素的位置
     * 基于改变的实体(及其index值), 更新实体列表排序, 返回排序后的实体列表
     * 增量式更新, 快速计算
     * @author           杜永浩
     * @param entityList 原实体列表
     * @param moveList   改变index值的算子集合 (如: 1->2, null->1, 1->null)
     * @param <D>        决策实体子类
     * @return           重新排序后, 顺序发生改变的index范围
     */
    public static <D extends DecisionEntity> ListChangeRecord sort(List<D> entityList, List<Move> moveList) {
        ListChangeRecord record = new ListChangeRecord();
        record.setOldList(new ArrayList(entityList));
        String variable = moveList.get(0).getName();
        // 1. 先判断是否是撤销操作
        if(moveList.get(0).isUndo()) {
            moveList = new ArrayList<>(moveList);
            Collections.reverse(moveList);
        }

        // 2. 依次插入 (从原list中删除, 再插入到指定index)
        int changedIndexMin = 999999999;                                        // 记录变动起点index
        int changedIndexMax = 0;                                                // 记录变动终点index
        int oldIndexGap     = 0;                                                // 因list变化导原index的变化值
        for(int i = 0 ; i < moveList.size() ; i ++) {
            // a. 获取新/旧index
            Move move = moveList.get(i);
            DecisionEntity entity = moveList.get(i).getDecisionEntity();
            Integer oldIndex = (Integer) move.getDoingOldValue();               // 原顺序index
            Integer newIndex = (Integer) move.getDoingNewValue();               // 新顺序index
            if(oldIndex == null)        oldIndex = 99999999;                    // todo 若原序为null, 即视为在无穷远处
            if(newIndex == null)        newIndex = 99999999;
            if(newIndex > entityList.size()) {
                newIndex = entityList.size();
            }
            if(oldIndex == newIndex)    continue;                               // 新旧相同 do nothing
            // b. 因上一次list, 原oldIndex实质已发生变化, 故需要计算出实际的index应该是多少
            if(i >= 1) {
                Integer lastOldIndex = (Integer) moveList.get(i - 1).getDoingOldValue();
                Integer lastNewIndex = (Integer) moveList.get(i - 1).getDoingNewValue();
                if(lastOldIndex == null)    lastOldIndex = 99999999;            // 若原序为null, 即视为在无穷远处
                if(lastNewIndex == null)    lastNewIndex = 99999999;
                if(oldIndex < lastOldIndex && oldIndex >= lastNewIndex)         oldIndexGap ++;
                else if(oldIndex > lastOldIndex && oldIndex <=  lastNewIndex)   oldIndexGap --;
                else if(oldIndex ==lastOldIndex)                                oldIndexGap += lastOldIndex - lastNewIndex;
            }
            // c. 移动(删除+插入)
            int removeIndex = oldIndex + oldIndexGap;
            int insertIndex = newIndex;
            if(removeIndex >= 0 && removeIndex < entityList.size()) {
                entityList.remove(removeIndex);
            }
            if(insertIndex >= 0 && insertIndex <= entityList.size()) {
                entityList.add(insertIndex, (D) entity);
            }
            // d. 统计最大最小index (受影响的)
            changedIndexMin = Math.min(Math.min(newIndex, oldIndex), changedIndexMin);
            changedIndexMax = Math.max(Math.max(newIndex, oldIndex), changedIndexMax);
        }

        // 3. 更新index
        changedIndexMin = Math.max(changedIndexMin, 0);                                                         // 变动起点index(>0)
        int changedIndexMax_New = Math.max(Math.min(changedIndexMax, entityList.size() - 1), 0);                // 针对新list: 变动终点index(>0, <新list.size)
        int changedIndexMax_Old = Math.max(Math.min(changedIndexMax, record.getOldList().size() - 1), 0);       // 针对旧list: 变动终点index(>0, <新list.size)
        if(entityList.size() > 0) {
            for(int i = changedIndexMin ; i <= changedIndexMax_New ; i ++) {
                entityList.get(i).setDecisionVariable(variable, i);
            }
        }
        /* function ends */
        record.setNewList(entityList);
        record.setOldIndexChangeRange(new int[]{changedIndexMin, changedIndexMax_Old});
        record.setNewIndexChangeRange(new int[]{changedIndexMin, changedIndexMax_New});
        return record;
    }


    /**
     * 针对两个实体list, 将部分元素从一个list移动到另一个
     * 基于改变的实体(主要改变的是变量, 如vehicle, 及其index值), 更新实体列表排序, 返回排序后的实体列表
     * 增量式更新, 快速计算
     * @author             杜永浩
     * @param entityList_1 (从)第一个实体列表
     * @param entityList_2 (移动到)第二个实体列表
     * @param moveList     改变决策变量及index值的算子集合 (如: vehicle_1->vehicle_2, 1->2)
     * @param <D>          决策实体子类
     * @return             重新排序后, 顺序发生改变的index范围
     */
    public static <D extends DecisionEntity> ListChangeRecord sort(List<D> entityList_1, List<D> entityList_2, List<Move> moveList) {
        String variable = "indexIn" + Tool.firstUpperCase(moveList.get(0).getName());
        // 1. 先判断是否是撤销操作
        if(moveList.get(0).isUndo()) {
            moveList = new ArrayList<>(moveList);
            Collections.reverse(moveList);
        }

        // 2. 相当于把部分entity的index值置空, 再赋回原值插入另一个list中
        List<Move> moveList_1 = new ArrayList<>();
        List<Move> moveList_2 = new ArrayList<>();
        for(int i = 0 ; i < moveList.size() ; i ++) {
            Integer index = (Integer) moveList.get(i).getDecisionEntity().getDecisionVariable(variable);
            Move move_1 = Move.move(moveList.get(i).getDecisionEntity(), variable, null);
            Move move_2 = Move.move(moveList.get(i).getDecisionEntity(), variable, index);
            if(move_1 != null) {
                moveList_1.add(move_1);
            }
            if(move_2 != null) {
                moveList_2.add(move_2);
            }
        }

        // 3. 执行上述相应移位操作
        ListChangeRecord record_1 = new ListChangeRecord();
        ListChangeRecord record_2 = new ListChangeRecord();
        if(entityList_1 != null) {
            record_1 = sort(entityList_1, moveList_1);
        }
        if(entityList_2 != null) {
            record_2 = sort(entityList_2, moveList_2);
        }
        /* function ends */
        ListChangeRecord record = new ListChangeRecord();
        record.setOldList(record_1.getOldList());
        record.setNewList(record_1.getNewList());
        record.setOldIndexChangeRange(record_1.getOldIndexChangeRange());
        record.setNewIndexChangeRange(record_2.getNewIndexChangeRange());
        return record;
    }


/* class ends */
}
