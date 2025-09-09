package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;
import org.nudtopt.realworldproblems.apiforsatellite.tool.interval.Interval;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadTaskData extends MainLogger {


    public static void main(String[] args) throws Exception {
        String path = Tool.getDesktopPath() + "/卫星任务规划xml/测控数据";                                            // 根目录路径
        Map<Long, Antenna> antennaMap = new ReadAntennaData().read(path);                                           // 1. 读取天线数据
        Scenario scenario = new ReadRangeData().read(path, antennaMap);                                             // 2. 读取弧段数据并创建场景
        new ReadTaskData().read(path, scenario);                                                                    // 3. 读取任务数据, 预处理约束, 并匹配任务资源
    }


    /**
     * 读取任务数据
     * @param path 根目录路径
     * @author     杜永浩
     */
    public void read(String path, Scenario scenario) throws Exception {
        logger.info("正在读取任务数据, 预处理约束, 建立任务、资源 (弧段/天线/卫星及轨道) 的匹配关系 ......");
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file:///" + path + "/Requsets_Normalized_Simple.XML");
        NodeList nodeList = document.getElementsByTagName("Rdata");                                                               // 根据Rdata索引
        Map<String, Task> taskMap = new HashMap<>();
        long maxSetupTime   = 0;
        long maxReleaseTime = 0;

        // 1. 遍历taskSN数据
        for(int i = 0 ; i < nodeList.getLength() ; i ++) {
            Element SNElement = (Element) nodeList.item(i);
            long taskSN = Long.valueOf(SNElement.getElementsByTagName("TaskSN").item(0).getTextContent());                  // taskSN

            // 2. 遍历day数据
            NodeList dayNodeList = SNElement.getElementsByTagName("DayofWeek");
            for(int j = 0 ; j < dayNodeList.getLength() ; j ++) {
                Element dayElement = (Element) dayNodeList.item(j);
                long dayIndex = Long.valueOf(dayElement.getElementsByTagName("DayIndex").item(0).getTextContent());        // dayIndex(卫星时)

                // 3. 遍历tracking数据
                NodeList trackNodeList = dayElement.getElementsByTagName("IntraTracking");
                for(int k = 0 ; k < trackNodeList.getLength() ; k ++) {
                    Element trackElement = (Element) trackNodeList.item(k);
                    long subIndex = Long.valueOf(trackElement.getElementsByTagName("SubIndex").item(0).getTextContent());  // subIndex
                    String taskType = trackElement.getElementsByTagName("TaskType").item(0).getTextContent();              // 任务类型: TTC, DDT
                    String taskIndex = "SN" + taskSN + "/天" + dayIndex + "/index" + subIndex;
                    long satelliteId = Long.valueOf(((Element) trackElement.getElementsByTagName("SatelliteObject").item(0)).getElementsByTagName("SatelliteIndex").item(0).getTextContent());
                    long setup       = Long.valueOf(((Element) trackElement.getElementsByTagName("SatelliteObject").item(0)).getElementsByTagName("TaskPrepareTime").item(0).getTextContent());
                    long release     = Long.valueOf(((Element) trackElement.getElementsByTagName("SatelliteObject").item(0)).getElementsByTagName("TaskEndTime").item(0).getTextContent());
                    Satellite satellite = scenario.getSatelliteMap().get(satelliteId);
                    String satelliteDayIndex = "星" + satelliteId + "/天" + dayIndex;
                    Day satelliteDay = scenario.getSatelliteDayMap().get(satelliteDayIndex);                                     // 卫星时

                    // 匹配任务
                    Task task;                                                                                                    // 任务
                    if(!taskMap.containsKey(taskIndex)) {                                                                         // 若map中不含该任务索引id, 则新建任务
                        task = new Task();
                        task.setId((long) taskMap.size());
                        task.setType(taskType);
                        task.setName(taskIndex);
                        task.setSetup(setup);
                        task.setRelease(release);
                        task.setSatelliteDay(satelliteDay);
                        task.setSatellite(satellite);
                        taskMap.put(taskIndex, task);
                        TaskRequest taskRequest = new TaskRequest();
                        task.setTaskRequest(taskRequest);
                        maxSetupTime   = Math.max(maxSetupTime,   task.getSetup());
                        maxReleaseTime = Math.max(maxReleaseTime, task.getRelease());
                    }
                    task = taskMap.get(taskIndex);

                    // 4. 遍历WindowConstrain数据
                    NodeList windowNodeList = trackElement.getElementsByTagName("SelectableWindowConstrain");
                    for(int m = 0 ; m < windowNodeList.getLength() ; m ++) {
                        Element windowElement = (Element) windowNodeList.item(m);
                        double elevation = Double.valueOf(windowElement.getElementsByTagName("MinimumMaxElevation").item(0).getTextContent());  // 最小仰角约束
                        long constraintType = Long.valueOf(((Element) windowElement.getElementsByTagName("RevolutionPositionConstrain").item(0)).getElementsByTagName("ConstrainType").item(0).getTextContent());
                        long scope1 = Long.valueOf(((Element) windowElement.getElementsByTagName("RevolutionPositionConstrain").item(0)).getElementsByTagName("ScopeDefinition1").item(0).getTextContent());
                        long scope2 = Long.valueOf(((Element) windowElement.getElementsByTagName("RevolutionPositionConstrain").item(0)).getElementsByTagName("ScopeDefinition2").item(0).getTextContent());
                        long scope3 = Long.valueOf(((Element) windowElement.getElementsByTagName("RevolutionPositionConstrain").item(0)).getElementsByTagName("ScopeDefinition3").item(0).getTextContent());
                        Constraint constraint = new Constraint();
                        constraint.setId(constraintType);
                        constraint.setMinElevation(elevation);
                        constraint.setScope(new long[]{scope1, scope2, scope3});
                        task.getConstraintList().add(constraint);
                    }

                    // 5. 遍历device数据
                    List<Range> optionalRangeList     = new ArrayList<>();          // 可选的弧段
                    Map<Range, String> supportTypeMap = new HashMap<>();            // 选择某弧段时, 支持的类型 (NULL, TTC, DDT, TTC/DDT, TTC+DDT)
                    NodeList deviceNodeList = trackElement.getElementsByTagName("OneSustainDevice");
                    for(int m = 0 ; m < deviceNodeList.getLength() ; m ++) {
                        Element deviceElement = (Element) deviceNodeList.item(m);
                        long antennaId = Long.valueOf(deviceElement.getElementsByTagName("DeviceIndex").item(0).getTextContent());  // taskSN
                        String supportType    = deviceElement.getElementsByTagName("SustainFunction").item(0).getTextContent();     // 支持的五种类型: NULL, TTC, DDT, TTC/DDT, TTC+DDT
                        String rangeListIndex = "星" + satelliteId + "/站" + antennaId + "/天" + dayIndex;
                        List<Range> rangeList = scenario.getRangeListMap().get(rangeListIndex);                                           // todo 获得当前该星该站的全部弧段, 即该任务的可选弧段
                        Antenna antenna       = scenario.getAntennaMap().get(antennaId);                                                  // todo 获得天线(站)
                        if(rangeList == null)   continue;
                        // 遍历可选弧段
                        for(Range range : rangeList) {
                            boolean effective = false;
                            // a. 检查任务类型约束
                            if(!supportType.contains(taskType)) {
                                continue;
                            }
                            // b. 检查任务需求约束
                            for(Constraint constraint : task.getConstraintList()) {
                                if(constraint.check(range)) {
                                    effective = true;   // 任意一条约束满足, 则该弧段有效
                                    break;
                                }
                            }
                            if(!effective)      continue;
                            // c. 检查天线可用时间约束
                            for(Window window : antenna.getForbiddenWindowList()) {
                                long rangeBegin = range.getBeginTime() - task.getSetup();
                                long rangeEnd   = range.getEndTime()   + task.getRelease();
                                long forbidBegin= window.getBeginTime();
                                long forbidEnd  = window.getEndTime();
                                double gap = Interval.getIntervalTime(rangeBegin, rangeEnd, forbidBegin, forbidEnd);
                                if(gap < 0) {
                                    effective = false;  // 任意弧段处于与设备禁用时间有交集, 则该弧段无效
                                    break;
                                }
                            }
                            if(!effective)      continue;
                            // d. 有效弧段, 记为可选弧段
                            range.setEffective(true);
                            optionalRangeList.add(range);
                            supportTypeMap.put(range, supportType);
                        }
                    }
                    task.setOptionalRangeList(optionalRangeList);   // 为task决策变量值域赋值
                    task.setSupportTypeMap(supportTypeMap);         // 为supportTypeMap赋值
                }   /* track loop ends */
            }   /* day loop ends  */
        }   /* taskSN loop ends  */
        List<Task> taskList = new ArrayList<>(taskMap.values());
        taskList.sort(new IdComparator());
        scenario.setTaskList(taskList);
        scenario.setTotalTaskList(new ArrayList<>(taskList));
        // scenario.setDayTaskMap(taskList.stream().collect(Collectors.groupingBy(Task::getDay)));
        /* function ends  */
        List<Task> TTCList = scenario.getTaskList().stream().filter(t -> t.getType().equals("TTC")).collect(Collectors.toList());   // 测控任务列表
        List<Task> DDTList = scenario.getTaskList().stream().filter(t -> t.getType().equals("DDT")).collect(Collectors.toList());   // 数传任务列表
        List<Task> nullTTCList = TTCList.stream().filter(t -> t.getOptionalRangeList().size() == 0).collect(Collectors.toList());   // 无可用弧段的测控任务
        List<Task> nullDDTList = DDTList.stream().filter(t -> t.getOptionalRangeList().size() == 0).collect(Collectors.toList());   // 无可用数传的测控任务
        List<Range> effectiveRangeList = scenario.getRangeList().stream().filter(Range::isEffective).collect(Collectors.toList());
        int effectTaskNum = TTCList.size() - nullTTCList.size() + DDTList.size() - nullDDTList.size();
        logger.info("经处理 -> 满足约束的弧段共 " + effectiveRangeList.size() + " 条 " +
                    "(占全部弧段的 " + Tool.round(100.0 * effectiveRangeList.size() / scenario.getRangeList().size(), 1) + " %) ;");
        logger.info("已生成 -> 测控任务: " + TTCList.size() + " 个 (占比 " + Tool.round(100.0 * TTCList.size() / taskList.size(), 1) + " %)" +
                   (nullTTCList.size() == 0 ? " ;" : ",\t其中 " + nullTTCList.size() + " 个无可用弧段 (占测控任务 " + Tool.round(100.0 * nullTTCList.size() / TTCList.size(), 1) + " %) ;" ));
        logger.info("已生成 -> 数传任务: " + DDTList.size() + " 个 (占比 " + Tool.round(100.0 * DDTList.size() / taskList.size(), 1) + " %)" +
                   (nullDDTList.size() == 0 ? " ;" : ",\t其中 " + nullDDTList.size() + " 个无可用弧段 (占数传任务 " + Tool.round(100.0 * nullDDTList.size() / DDTList.size(), 1) + " %) ;" ));
        logger.info("总  计 -> 任务需求: " + taskMap.size() + " 个 (任务平均 " + Tool.round(1.0 * effectiveRangeList.size() / taskMap.size(), 1) + " 条测控弧段)" +
                   (taskList.size() == effectTaskNum ? " !" : ",\t其中有可用弧段的 " + effectTaskNum + " 个 (占总任务 " + Tool.round(100.0 * effectTaskNum / taskList.size(), 1) + " %) !" ));
    }




/* class ends */
}
