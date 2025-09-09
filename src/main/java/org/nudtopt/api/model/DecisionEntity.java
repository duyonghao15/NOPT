package org.nudtopt.api.model;

import org.nudtopt.api.tool.function.Tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DecisionEntity extends NumberedObject implements Cloneable {

    protected boolean changeable = true;                            // 是否可变
    protected boolean available = true;                             // 是否可用
    protected boolean changed = false;                              // 在上一次move/迭代中, 是否被改变
    protected List<Object> relatingObjectList = new ArrayList<>();  // 增量式约束检查是紧密相关的其他对象
    protected DecisionEntity lastEntity;
    protected DecisionEntity nextEntity;
    protected List<String> variableNameList;                        // 决策变量list
    protected List<String> sortInVariableNameList;                  // 决策实体在其排序的决策变量(如salesman, vehicle)list

    // getter & setter
    public boolean isChangeable() {
        return changeable;
    }
    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isChanged() {
        return changed;
    }
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<Object> getRelatingObjectList() {
        return relatingObjectList;
    }
    public void setRelatingObjectList(List<Object> relatingObjectList) {
        this.relatingObjectList = relatingObjectList;
    }

    public DecisionEntity getLastEntity() {
        return lastEntity;
    }
    public void setLastEntity(DecisionEntity lastEntity) {
        this.lastEntity = lastEntity;
    }

    public DecisionEntity getNextEntity() {
        return nextEntity;
    }
    public void setNextEntity(DecisionEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // 0. 读取中决策变量列表
    public List<String> getDecisionVariableList() {
        if(variableNameList == null) {
            variableNameList = new ArrayList<>();
            for(Field field : this.getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(DecisionVariable.class)) {
                    variableNameList.add(field.getName());
                }
            }
        }
        return variableNameList;
    }
    public List<String> getSortInVariableNameList() {
        if(sortInVariableNameList == null) {
            sortInVariableNameList = new ArrayList<>();
            for(Field field : this.getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(DecisionVariable.class)) {
                    String resource = field.getAnnotation(DecisionVariable.class).sortIn();
                    if(resource.length() > 0) {
                        sortInVariableNameList.add(field.getAnnotation(DecisionVariable.class).sortIn());
                    }
                }
            }
        }
        return sortInVariableNameList;
    }



    // 1. 基于决策变量名, 获得该变量
    public Object getDecisionVariable(String name) {
        try {
            Method method = this.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name));
            return method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }


    // 2. 基于决策变量名, 设置该变量
    public void setDecisionVariable(String name, Object value) {
        try {
            Class variableClass = this.getClass().getDeclaredField(name).getType();
            Method method = this.getClass().getDeclaredMethod("set" + Tool.firstUpperCase(name), variableClass);
            method.invoke(this, value);
            update(); // todo 在决策变量更新后, 及时更新关联对象集
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 3. 基于决策变量名, 读取该变量值域
    public List getOptionalDecisionVariableList(String name) {
        try {
            Method method = this.getClass().getDeclaredMethod("getOptional" + Tool.firstUpperCase(name) + "List");
            return (List) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }


    // 4. 基于决策变量名, 设置该变量值域
    public void setOptionalDecisionVariableList(String name, List valueList) {
        try {
            Method method = this.getClass().getDeclaredMethod("setOptional" + Tool.firstUpperCase(name) + "List", List.class);
            method.invoke(this, valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 5. 判断决策变量是否可空
    public boolean nullable(String name) {
        try {
            return getClass().getDeclaredField(name).getAnnotation(DecisionVariable.class).nullable();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }   return false;
    }
    public double nullProbability(String name) {
        try {
            return getClass().getDeclaredField(name).getAnnotation(DecisionVariable.class).probability();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }   return 0;
    }


    @Override
    public DecisionEntity clone() {
        try {
            return (DecisionEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }   return null;
    }


    /**
     * 判断两个决策实体的决策变量是否一致
     */
    public boolean sameDecisionVariable(DecisionEntity decisionEntity) {
        if(getClass() != decisionEntity.getClass()) return false;       // 类型不同
        if(id != decisionEntity.getId())            return false;       // id不同
        List<String> variableList = getDecisionVariableList();
        for(String variable : variableList) {
            if(getDecisionVariable(variable) != decisionEntity.getDecisionVariable(variable)) {
                return false;                                           // 任意决策变量值不相同
            }
        }
        return true;
    }


    /**
     * 接口, 在任意决策变量修改后, 更新关联对象
     */
    public void update() {}


/* class ends */
}
