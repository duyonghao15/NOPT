package org.nudtopt.api.model;

import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.rule.Deconflict;
import org.nudtopt.api.constraint.Constraint;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.api.tool.comparator.PriorityComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class Solution extends NumberedObject {

    protected Score score;
    protected Constraint constraint;
    protected double[][] decisionMatrix;                                                        // 决策矩阵(null: 实例型编码, 非null: 数字型编码)
    protected Long beginTime = (long) Infinity;                                                 // 场景开始时间
    protected Long endTime = 0L;                                                                // 场景结束时间
    protected Map<Long, DecisionEntity> entityIdMap = new HashMap<>();                          // 记录决策实体id的map, 便于快速获得指定实体
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日期格式
    protected PriorityComparator comparator = new PriorityComparator();                                 // 实体顺序排序器

    // getter & setter
    public Score getScore() {
        return score;
    }
    public void setScore(Score score) {
        this.score = score;
    }

    public Constraint getConstraint() {
        return constraint;
    }
    public void setConstraint(Constraint constraint) {
        constraint.setSolution(this);
        this.constraint = constraint;
    }

    public double[][] getDecisionMatrix() {
        return decisionMatrix;
    }
    public void setDecisionMatrix(double[][] decisionMatrix) {
        this.decisionMatrix = decisionMatrix;
    }

    public Long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Map<Long, DecisionEntity> getEntityIdMap() {
        return entityIdMap;
    }
    public void setEntityIdMap(Map<Long, DecisionEntity> entityIdMap) {
        this.entityIdMap = entityIdMap;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public PriorityComparator getComparator() {
        return comparator;
    }
    public void setComparator(PriorityComparator comparator) {
        this.comparator = comparator;
    }

    // -------------------------------- 以下是辅助计算函数 ------------------------------------
    // 0. 获取decision entities的名字
    private List<String> getDecisionEntityNameList() {
        List<String> nameList = new ArrayList<>();
        for(Field field : this.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(DecisionEntityList.class))  nameList.add(field.getName()); // 获得被声明的entity名(可能不止一个)
        }
        return nameList;
    }


    // 1.1 读取全部decision entity list
    public <D extends DecisionEntity> List<D> getDecisionEntityList() {
        List<String> nameList = getDecisionEntityNameList();
        List<D> decisionEntityList = new ArrayList<>();
        for(String name : nameList) {
            try {                                                  // 获得getter
                Method method = this.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name));
                decisionEntityList.addAll((List<D>)method.invoke(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decisionEntityList;
    }


    // 1.2 读取可更改的decision entity list
    public <D extends DecisionEntity> List<D> getChangeableEntityList() {
        List<D> changeableEntityList = new ArrayList<>();
        List<D> decisionEntityList   = getDecisionEntityList();
        for(D entity : decisionEntityList) {
            if(entity.isChangeable() && entity.isChangeable())  changeableEntityList.add((entity));
        }
        return changeableEntityList;
    }


    // 2. 读取最大的决策变量数量 (不同的decision entity的variable数量可能不同)
    public int getMaxDecisionVariableSize() {
        List<String> nameList = getDecisionEntityNameList();
        int maxSize = 0;
        for(String name : nameList) {
            try {                                                  // 获得getter
                Method method = this.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name));
                List<DecisionEntity> decisionEntityList = (List)method.invoke(this);
                maxSize = Math.max(maxSize, decisionEntityList.get(0).getDecisionVariableList().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return maxSize;
    }


    // 3.1 更新score
    public Score updateScore(Operator operator) {
        if(score == null)score = new Score();
        Score oldScore = score.clone();
        constraint.setOperator(operator);
        constraint.updateScore();
        /*if(constraint.isIncremental()) {
            if(constraint instanceof DroolsConstraint) {
                DroolsConstraint drools = (DroolsConstraint) constraint;
                if(constraint.isIncremental())  drools.calStatefulDroolsScore(this, (Move) operator);
                else                            drools.calStatelessDroolsScore(this);
            } else {
                constraint.setOperator(operator);
                constraint.updateScore();
            }
        } else {
            constraint.updateScore();
        }*/
        if(operator != null)    operator.setGapScore(score.cutScore(oldScore));
        return score;
    }
    public Score updateScore() {
        return updateScore(null);
    }

    // 3.2 重新计算Score
    public Score reScore() {
        return updateScore();
    }
    // 3.3. 检查评分
    public void checkScore() {
        Score oldScore = score.clone();
        Score newScore = reScore();
        if(oldScore.compareTo(reScore()) != 0)   System.out.println("\terror:\t增量式约束 old: " + oldScore + " != new: " + newScore);
    }


    // 4.1 解码: 由决策矩阵对solution进行赋值
    public void decode(double[][] decisionMatrix) {
        this.decisionMatrix = decisionMatrix;
        if(decisionMatrix == null)  return;
        List<DecisionEntity> decisionEntityList = getDecisionEntityList();
        for(int i = 0 ; i < decisionEntityList.size() ; i ++) {
            DecisionEntity decisionEntity = decisionEntityList.get(i);
            List<String> nameList = decisionEntity.getDecisionVariableList();
            for(int j = 0 ; j < nameList.size() ; j ++) {
                String name = nameList.get(j);
                List<Object> variableList = decisionEntity.getOptionalDecisionVariableList(name);
                if(variableList.size() == 0)    continue;
                double value = decisionMatrix[i][j];            // 第i个决策实体, 第j个决策变量的值
                int index;
                Object variable;
                if(decisionEntity.nullable(name)) {
                    double probability = decisionEntity.nullProbability(name);
                    int nullSize = (int) Math.max(1,  variableList.size() * probability / (1 - probability)); // 在原list后面加至少1个null
                    index = (int) Math.round(value * (variableList.size() + nullSize));
                    if(index < variableList.size())   variable = variableList.get(index);
                    else                              variable = null;
                } else {
                    index = (int) Math.round(value * (variableList.size()));
                    if(index < variableList.size())   variable = variableList.get(index);
                    else                              variable = variableList.get(variableList.size() - 1);
                }
                decisionEntity.setDecisionVariable(name, variable);
            }
        }
    }


    // 4.2 编码: 由solution对决策矩阵赋值
    public double[][] encode() {
        List<DecisionEntity> decisionEntityList = getDecisionEntityList();
        decisionMatrix = new double[decisionEntityList.size()][getMaxDecisionVariableSize()];
        for(int i = 0 ; i < decisionEntityList.size() ; i ++) {
            DecisionEntity decisionEntity = decisionEntityList.get(i);
            List<String> nameList = decisionEntity.getDecisionVariableList();
            for(int j = 0 ; j < nameList.size() ; j ++) {
                String name = nameList.get(j);
                List<Object> variableList = decisionEntity.getOptionalDecisionVariableList(name);
                if(variableList.size() == 0)    continue;
                Object variable = decisionEntity.getDecisionVariable(name);
                if(decisionEntity.nullable(name)) { // variable可为null时
                    if(variable == null) {
                        decisionMatrix[i][j] = 1;   // variable = null, 取1
                    } else {
                        double probability = decisionEntity.nullProbability(name);
                        int nullSize = (int) Math.max(1, variableList.size() * probability / (1 - probability)); // 在原list后面加至少1个null
                        int index = variableList.indexOf(variable);
                        decisionMatrix[i][j] = 1.0 * index / (variableList.size() + nullSize);
                    }
                } else {
                    int index = variableList.indexOf(variable);
                    decisionMatrix[i][j] = 1.0 * index / variableList.size();
                }
                if(decisionMatrix[i][j] < 0) {      // todo 存在变值域的时候, variable不属于list, 则视为null取1
                    decisionMatrix[i][j] = 1;
                }
            }
        }
        return decisionMatrix;
    }


    // 5.1 决策矩阵克隆(用于数字编码), 只包含: 决策矩阵和新评分, 不含其他属性
    public Solution matrixClone(double[][] decisionMatrix) {
        decode(decisionMatrix);                                                       // 1. 基于决策矩阵, 解码solution
        Score score = updateScore().clone();                                // 2. 计算评分
        Solution newSolution = new Solution();                                        // 3. 新建newSolution
        newSolution.setDecisionMatrix(decisionMatrix);
        newSolution.setScore(score);
        return newSolution;
    }
    // 5.2 无参的决策矩阵克隆
    public Solution matrixClone() {
        Solution newSolution = new Solution();
        newSolution.setDecisionMatrix(Tool.clone(decisionMatrix));
        newSolution.setScore(score.clone());
        return newSolution;
    }
    // 5.3 由一个半克隆的solution(variable是同一对象)更新当前solution
    public void clone(Solution newSolution) {
        if(newSolution.getDecisionMatrix() != null) {
            decode(newSolution.getDecisionMatrix());
            return;
        }
        for(int i = 0 ; i < newSolution.getDecisionEntityList().size() ; i ++) {
            DecisionEntity newEntity = newSolution.getDecisionEntityList().get(i);
            DecisionEntity entity = getDecisionEntityList().get(i);
            for(String name : entity.getDecisionVariableList()) {
                Move move = Move.move(entity, name, newEntity.getDecisionVariable(name));
                if(constraint.isIncremental())  updateScore(move);
            }
        }
        if(score.compareTo(newSolution.getScore()) != 0) {
            System.out.print("error: 评分错误! 原评分: " + score + " -> 新评分: " +  newSolution.getScore());
            score.clone(newSolution.getScore());
        }
    }


    // 5. 半克隆(只深克隆decisionEntity, variable和值域等属性均不克隆)
    public Solution semiClone() {
        // 1. 浅克隆entity
        Solution newSolution = new Solution();
        try {
            newSolution = (Solution) super.clone();
            for(Field field : newSolution.getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(DecisionEntityList.class)) {                              // 1. 寻找规划实体列表
                    Method getter = newSolution.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(field.getName()));
                    Method setter = newSolution.getClass().getDeclaredMethod("set" + Tool.firstUpperCase(field.getName()), List.class);
                    List<DecisionEntity> entityList = (List<DecisionEntity>) getter.invoke(this);  // 2. 规划实体列表
                    List<DecisionEntity> newEntityList = new ArrayList<>();
                    for(DecisionEntity entity : entityList) {
                        newEntityList.add(entity.clone());                                             // 3. 浅克隆规划实体
                    }
                    setter.invoke(newSolution, newEntityList);                                         // 4. 替换新解中的规划实体列表
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2. 深克隆Score
        newSolution.setScore(score.clone());
        // 3. 深克隆decisionMatrix
        newSolution.setDecisionMatrix(Tool.clone(decisionMatrix));
        newSolution.updateScore();
        return newSolution;
    }



    // 6.0 获取某个decision entity list
    public List<DecisionEntity> getDecisionEntityList(DecisionEntity decisionEntity) {
        String name = decisionEntity.toString().split("-")[0].toLowerCase();
        try {
            Method method = this.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name) + "List");
            return (List)method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }

    // 6.1 增decision entity
    public List<Operator> addDecisionEntity(DecisionEntity decisionEntity, boolean deconflict) {
        // a. add entity
        getDecisionEntityList(decisionEntity).add(decisionEntity);
        // b. update score
        updateScore();
        System.out.println("\n已新增:\t" + decisionEntity +"\t obtains new score: " + score + ".");
        // c. deconflict
        if(deconflict)  return new Deconflict().run(this, decisionEntity);
        else            return new ArrayList<>();
    }


    // 6.2 减decision entity
    public List<Move> removeDecisionEntity(DecisionEntity decisionEntity) {
        List<Move> moveList = new ArrayList<>();
        // a. remove entity
        getDecisionEntityList(decisionEntity).remove(decisionEntity);
        // b. update
        System.out.println("\n已删除:\t" + decisionEntity +"\t obtains new score: " + score);
        return moveList;
    }


    // 7. 创建一个滚动调度的newSolution
    public Solution createRollingSolution(long beginTime, long endTime) {
        Solution newSolution = semiClone();
        for(DecisionEntity entity : newSolution.getDecisionEntityList()) {
            if(entity.getId() < 0 ) continue;                                       // id < 0 (虚拟), 跳过
            // 1. 先禁止任务被修改
            entity.setChangeable(false);
            // 2. 遍历当前决策变量, 判断任务是否已发生
            boolean happened = false;
            for(String name : entity.getDecisionVariableList()) {
                Object variable = entity.getDecisionVariable(name);
                if(variable instanceof Window) {
                    Window window = (Window) variable;
                    if(window.getBeginTime() <= beginTime)   happened = true;       // 早于滚动窗口, 已发生
                }
            }
            if(happened)            continue;                                       // 已发生, 直接跳过
            // 3. 任务未发生, 遍历决策变量及其取值, 更新决策变量取值
            for(String name : entity.getDecisionVariableList()) {
                // a. 只更新窗口类(window, 有开始/结束时间)的变量
                if(entity.getOptionalDecisionVariableList(name).size() == 0 || !(entity.getOptionalDecisionVariableList(name).get(0) instanceof Window)) {
                    continue;
                }
                // b. 遍历原决策变量取值范围
                List<Object> optionalVariableList = new ArrayList<>();
                for(Object variable : entity.getOptionalDecisionVariableList(name)) {
                    Window window = (Window) variable;
                    if(window.getBeginTime() >= beginTime && window.getBeginTime() <= endTime ||
                       window.getEndTime()   >= beginTime && window.getEndTime()   <= endTime) {
                        optionalVariableList.add(variable);
                    }
                }
                // b. 更新决策变量取值范围
                entity.setOptionalDecisionVariableList(name, optionalVariableList);
                if(optionalVariableList.size() > 0)    entity.setChangeable(true);
            }
        }
        System.out.print("已启动滚动调度! 当前可调度窗口为: " + beginTime + " -> " + endTime + " (仅窗口内任务可变)." + "\n\n");
        return newSolution;
    }


    /**
     * 根据entity id查找指定entity
     * @param id entity的id
     * @return   指定entity
     */
    public DecisionEntity getDecisionEntity(long id) {
        if(entityIdMap == null || entityIdMap.size() == 0) {
            entityIdMap = new HashMap<>();
            List<DecisionEntity> entityList = getDecisionEntityList();
            for(DecisionEntity entity : entityList) {
                entityIdMap.put(entity.getId(), entity);
            }
        }
        return entityIdMap.get(id);
    }


    /**
     * 随机获取n个(不同的, 可移动的)决策实体
     * @param num 获取决策实体的数量
     * @return    随机决策实体list
     */
    public List<DecisionEntity> getRandomDecisionEntity(int num) {
        List<DecisionEntity> entityList = getDecisionEntityList();
        List<DecisionEntity> outputList = new ArrayList<>();
        if(num > entityList.size())              return new ArrayList<>(entityList);    // 若要求取值超过列表长度的不同值, 只能全部返回
        for(int i = 0 ; i < num ; i ++) {
            DecisionEntity entity = Tool.randomFromList(entityList);
            // 若实体重复, 或不可改, 不可用, 则重新选择
            while(outputList.contains(entity) || !entity.isChangeable() || !entity.isAvailable()) {
                entity = Tool.randomFromList(entityList);
            }
            outputList.add(entity);
        }
        return outputList;
    }
    public DecisionEntity getRandomDecisionEntity() {
        return getRandomDecisionEntity(1).get(0);
    }



/* class ends */
}
