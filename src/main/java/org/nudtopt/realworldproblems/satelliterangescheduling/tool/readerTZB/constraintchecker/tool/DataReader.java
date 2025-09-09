package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool;

import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.constraint.*;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class DataReader extends MainLogger {

    public Map<Long, Antenna> readAntennaData(String path) throws ParserConfigurationException, IOException,
            SAXException {
        logger.info("正在读取天线设备数据 ......");
        Map<Long, Antenna> antennaMap      = new HashMap<>();
        DocumentBuilder    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document           document        = documentBuilder.parse("file:///" + path);
        //获取所有的Device节点，然后遍历
        NodeList nodeList = document.getElementsByTagName("Device");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element  antennaElement = (Element) nodeList.item(i);
            long     antennaId      = Long.parseLong(Utils.getFirstItemConByTagName(antennaElement, "DeviceIndex"));   // 设备id
            Antenna  antenna        = antennaMap.computeIfAbsent(antennaId, Antenna::new);
            NodeList beginNodeList  = antennaElement.getElementsByTagName("TimeStart");                                          // 设备禁用开始时间的NodeList
            NodeList endNodeList    = antennaElement.getElementsByTagName("TimeEnd");                                            // 设备禁用结束时间的NodeList
            for (int j = 0; j < beginNodeList.getLength(); j++) {
                Window window = new Window();                                                                                   // 禁用窗口
                window.setBeginTime(Long.parseLong(beginNodeList.item(j).getTextContent()));
                window.setEndTime(Long.parseLong(endNodeList.item(j).getTextContent()));
                antenna.getForbiddenWindowList().add(window);                                                                   // 禁用窗口赋值给antenna
            }
        }
        logger.info("已读取 -> 天线设备: " + antennaMap.size() + " 套 ！");
        return antennaMap;
    }


    public List<Arc> readArcData(String path) throws ParserConfigurationException, IOException, SAXException {
        logger.info("正在读取弧段数据（预报数据） ......");
        List<Arc>       arcList         = new ArrayList<>();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document        document        = documentBuilder.parse("file:///" + path);
        //获取所有的Satellite节点，然后遍历
        NodeList satellitNnodeList = document.getElementsByTagName("Satellite");
        for (int i = 0; i < satellitNnodeList.getLength(); i++) {
            Element satelliteElement = (Element) satellitNnodeList.item(i);
            long satelliteIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(satelliteElement, "SatelliteIndex"));
            // logger.info("正在读取卫星" + satelliteIndex + "的弧段数据......");
            NodeList satOneDayNodeList = satelliteElement.getElementsByTagName("OneDayForecast");
            for (int j = 0; j < satOneDayNodeList.getLength(); j++) {
                Element oneDayForecastElement = (Element) satOneDayNodeList.item(j);
                long dayIndex = Long.parseLong(
                        Utils.getFirstItemConByTagName(oneDayForecastElement, "DayIndex"));
                NodeList arcNodeList = oneDayForecastElement.getElementsByTagName("OneRecordForecast");
                for (int k = 0; k < arcNodeList.getLength(); k++) {
                    Element arcElement = (Element) arcNodeList.item(k);
                    long antennaIndex = Long.parseLong(
                            Utils.getFirstItemConByTagName(arcElement, "DeviceIndex"));
                    long orbitIndex = Long.parseLong(
                            Utils.getFirstItemConByTagName(arcElement, "RevolutionTotalNum"));
                    long orbitInChinaIndex = Long.parseLong(
                            Utils.getFirstItemConByTagName(arcElement, "RevolutionIdentificationIndex"));
                    long orbitInChinaNum = Long.parseLong(
                            Utils.getFirstItemConByTagName(arcElement, "RevolutionConsecutiveNum"));
                    boolean riseFallFlag = Boolean.parseBoolean(
                            Utils.getFirstItemConByTagName(arcElement, "RiseFallFlag"));
                    long startTime = Long.parseLong(
                            Utils.getItemConByTagName(arcElement, "TimeSecond", 0));
                    long endTime = Long.parseLong(
                            Utils.getItemConByTagName(arcElement, "TimeSecond", 2));
                    double elevation = Double.parseDouble(
                            Utils.getItemConByTagName(arcElement, "ElevationAngle", 1));
                    Arc arc = new Arc(0, dayIndex, orbitIndex, satelliteIndex, antennaIndex, riseFallFlag,
                            orbitInChinaIndex, orbitInChinaNum, startTime, endTime, elevation, true);
                    arcList.add(arc);
                }
            }
        }
        logger.info("已读取 -> 弧段数据: " + arcList.size() + " 条 ！");
        return arcList;
    }


    public List<Task> readTaskData(String path) throws ParserConfigurationException, IOException, SAXException {
        logger.info("正在读取任务数据 ......");
        List<Task>      taskList        = new ArrayList<>();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document        document        = documentBuilder.parse("file:///" + path);
        //获取所有的Satellite节点，然后遍历
        NodeList rdataList = document.getElementsByTagName("Rdata");
        for (int i = 0; i < rdataList.getLength(); i++) {
            Element rdataElement = (Element) rdataList.item(i);
            long taskSN = Long.parseLong(
                    Utils.getFirstItemConByTagName(rdataElement, "TaskSN"));
            NodeList dayNodeList = rdataElement.getElementsByTagName("DayofWeek");
            for (int j = 0; j < dayNodeList.getLength(); j++) {
                Element dayElement = (Element) dayNodeList.item(j);
                long dayIndex = Long.parseLong(
                        Utils.getFirstItemConByTagName(dayElement, "DayIndex"));
                NodeList subTaskList = dayElement.getElementsByTagName("IntraTracking");
                for (int k = 0; k < subTaskList.getLength(); k++) {
                    Element subTaskElement = (Element) subTaskList.item(k);
                    long subTaskIndex = Long.parseLong(Utils.getFirstItemConByTagName(subTaskElement,
                            "SubIndex"));
                    String taskType = Utils.getFirstItemConByTagName(subTaskElement, "TaskType");
                    long satelliteIndex = Long.parseLong(Utils.getFirstItemConByTagName(subTaskElement,
                            "SatelliteIndex"));
                    long taskPrepareTime = Long.parseLong(Utils.getFirstItemConByTagName(subTaskElement,
                            "TaskPrepareTime"));
                    long taskEndTime = Long.parseLong(Utils.getFirstItemConByTagName(subTaskElement,
                            "TaskEndTime"));
                    NodeList constraintNodeList = subTaskElement.getElementsByTagName(
                            "SelectableWindowConstrain");
                    List<ArcConstraint<Arc>> arcConstraintList = new ArrayList<>();
                    for (int l = 0; l < constraintNodeList.getLength(); l++) {
                        Element constraintElement = (Element) constraintNodeList.item(l);
                        double minimumMaxElevation = Double.parseDouble(
                                Utils.getFirstItemConByTagName(constraintElement, "MinimumMaxElevation"));
                        int constrainType = Integer.parseInt(
                                Utils.getFirstItemConByTagName(constraintElement, "ConstrainType"));
                        long scopeDefinition0 = Long.parseLong(
                                Utils.getFirstItemConByTagName(constraintElement, "ScopeDefinition1"));
                        long scopeDefinition1 = Long.parseLong(
                                Utils.getFirstItemConByTagName(constraintElement, "ScopeDefinition2"));
                        long scopeDefinition2 = Long.parseLong(
                                Utils.getFirstItemConByTagName(constraintElement, "ScopeDefinition3"));
                        arcConstraintList.add(new ArcConstraint<>(constrainType, minimumMaxElevation,
                                scopeDefinition0,
                                scopeDefinition1, scopeDefinition2));
                    }
                    NodeList          availableDeviceNodeList = subTaskElement.getElementsByTagName("OneSustainDevice");
                    Map<Long, String> availableAntennaMap     = new HashMap<>();  // 经过对比所有的任务的候选设备都是一样
                    for (int l = 0; l < availableDeviceNodeList.getLength(); l++) {
                        Element availableDeviceElement = (Element) availableDeviceNodeList.item(l);
                        long antennaIndex = Long.parseLong(
                                Utils.getFirstItemConByTagName(availableDeviceElement, "DeviceIndex"));
                        String antennaFunction =
                                Utils.getFirstItemConByTagName(availableDeviceElement, "SustainFunction");
                        availableAntennaMap.put(antennaIndex, antennaFunction);
                    }
                    taskList.add(new Task(taskSN, subTaskIndex, dayIndex,
                            taskType, satelliteIndex, taskPrepareTime, 0, taskEndTime, arcConstraintList,
                            availableAntennaMap, new ArrayList<>(), null));
                }
            }
        }
        logger.info("已读取 -> 任务数据: " + taskList.size() + " 个 ！");
        return taskList;
    }

    public List<Plan> readPlanData(String path) throws ParserConfigurationException, IOException, SAXException {
        logger.info("正在读取规划结果数据 ......");
        List<Plan>      planList        = new ArrayList<>();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document        document        = documentBuilder.parse("file:///" + path);
        //获取所有的Satellite节点，然后遍历
        NodeList PLANodeList = document.getElementsByTagName("PLAN");
        for (int i = 0; i < PLANodeList.getLength(); i++) {
            Element planElement = (Element) PLANodeList.item(i);
            long satelliteIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "SPACECRAFTCODE"));
            long antennaIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "DEVCODE"));
            long orbitIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TTCREVNO"));
            long snIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TaskSN"));
            long dayIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "DayIndex"));
            long subIndex = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "SubIndex"));
            long taskPrepareStartTime = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TWS"));
            long taskTrackingStartTime = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TTS"));
            long taskTrackingEndTime = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TTE"));
            long taskReleaseEndTime = Long.parseLong(
                    Utils.getFirstItemConByTagName(planElement, "TWE"));
            planList.add(new Plan(satelliteIndex, antennaIndex, orbitIndex, snIndex, dayIndex, subIndex,
                    taskPrepareStartTime, taskTrackingStartTime, taskTrackingEndTime, taskReleaseEndTime));
        }
        logger.info("已读取 -> 规划方案数据: " + planList.size() + " 条 ！");
        return planList;
    }

    public void dataInit(Map<Long, Antenna> antennaMap, List<Arc> arcList, List<Task> taskList) {
        logger.info("正在配置天线能力、任务可用弧段、天线可用时间窗 ......");
        //为每个天线设置能力
        Map<Long, String> availableAntennaMap = taskList.get(0).getAvailableAntennaMap();
        for (Long key : availableAntennaMap.keySet()) {
            antennaMap.get(key).setFunction(availableAntennaMap.get(key));
        }
        //为每个天线配置所有的弧段 (对这个天线不一定可用）
        for (int i = 0; i < arcList.size(); i++) {
            long antennaIndex = arcList.get(i).getAntennaIndex();
            antennaMap.get(antennaIndex).getArcList().add(arcList.get(i));
        }

        //为每个任务筛选可用天线列表
        for (int i = 0; i < taskList.size(); i++) {
            Map<Long, String> filteredAvailableAntennaMap = new HashMap<>();
            Map<Long, String> tmpAvailableAntennaMap      = taskList.get(i).getAvailableAntennaMap();
            for (Long key : tmpAvailableAntennaMap.keySet()) {
                if (tmpAvailableAntennaMap.get(key).contains(taskList.get(i).getTaskType())) {
                    filteredAvailableAntennaMap.put(key, tmpAvailableAntennaMap.get(key));
                }
            }
            taskList.get(i).setAvailableAntennaMap(filteredAvailableAntennaMap);
        }

        Map<String, List<Arc>> daySatArcMap = arcList.stream()
                .collect(Collectors.groupingBy(arc -> "day" + arc.getDayIndex() + "sat" + arc.getSatelliteIndex()));
        //为每个任务计算可选弧段
        for (int i = 0; i < taskList.size(); i++) {
            Task      task       = taskList.get(i);
            List<Arc> tmpArcList = daySatArcMap.get("day" + task.getDayIndex() + "sat" + task.getSatelliteIndex());
            for (Arc arc : tmpArcList) {
                // 先检查弧段的地面站支持不支持要求的任务类型
                if (!task.getAvailableAntennaMap().containsKey(arc.getAntennaIndex()))
                    continue;
                // 检查这个弧段是否满足任务约束之一
                if (!task.isArcAvailable(arc))
                    continue;
                // 检查这个弧段加上任务的准备和释放时间后和天线禁用时间无冲突
                if (!antennaMap.get(arc.getAntennaIndex()).isArcAvailable(task, arc))
                    continue;
                task.getOptionalArcList().add(arc);
            }
        }
        logger.info("场景数据配置完成......");
    }

    public Map<String, Arc> getSDAOArcMap(List<Arc> arcList) {
        //获取 卫星-天-天线-轨道 SDAO和弧段的映射map
        Map<String, Arc> SDAOArcMap = new HashMap<>();
        for (Arc arc : arcList) {
            SDAOArcMap.put(Utils.generateArcSDAO(arc), arc);
        }
        return SDAOArcMap;
    }

    public Map<String, Task> getSDSTaskMap(List<Task> taskList) {
        //获取 snIndex-dayIndex-subIndex 到 Task的映射map
        Map<String, Task> snSubIndexTaskMap = new HashMap<>();
        for (Task task : taskList) {
            snSubIndexTaskMap.put(Utils.generateTaskSDS(task), task);
        }
        return snSubIndexTaskMap;
    }
}
