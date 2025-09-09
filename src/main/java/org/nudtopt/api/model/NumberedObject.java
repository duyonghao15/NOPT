package org.nudtopt.api.model;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nudtopt.api.tool.function.Tool;

public abstract class NumberedObject implements Serializable, Cloneable, Comparable<NumberedObject> {

    protected Long id;

    protected String index;

    protected String name;

    protected String type;

    protected String className;

    protected long updateTime;

    protected static final long serialVersionUID = 1L;

    // getter & setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }

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

    public long getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getClassName() {
        if(className == null)   className = getClass().getName().replaceAll(".*\\.", "");
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 基于决策变量名, 获得该变量
     * @param name 变量名
     * @return     当前变量值
     */
    public Object getVariable(String name) {
        try {
            Method method = this.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name));
            return method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }


    /**
     * 基于决策变量名, 设置该变量
     * @param name  变量名
     * @param value 新设变量值
     */
    public void setVariable(String name, Object value) {
        try {
            Class variableClass = this.getClass().getDeclaredField(name).getType();
            Method method = this.getClass().getDeclaredMethod("set" + Tool.firstUpperCase(name), variableClass);
            method.invoke(this, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int compareTo(NumberedObject other) {
        return new CompareToBuilder()
                .append(getClass().getName(), other.getClass().getName())
                .append(id, other.id)
                .toComparison();
    }

    @Override
    public String toString() {
        return getClassName() + "-" + (id != null ? id : name);
    }


}
