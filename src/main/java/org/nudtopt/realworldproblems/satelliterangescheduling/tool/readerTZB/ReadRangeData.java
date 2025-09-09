package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Orbit;
import org.nudtopt.realworldproblems.apiforsatellite.tool.comparator.WindowBeginTimeComparator;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Range;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class ReadRangeData extends MainLogger {


    /**
     * 读取卫星和测传弧段数据
     * @author 杜永浩
     * @param path        根目录路径
     * @param antennaMap  天线数据
     * @return            构建的场景
     */
    public Scenario read(String path, Map<Long, Antenna> antennaMap) throws Exception {
        logger.info("正在导入卫星与测传弧段数据 ......");
        Map<Long, Satellite> satelliteMap = new HashMap<>();                        // 根据: 卫星id,          快速索引卫星
        Map<String, Orbit> orbitMap = new HashMap<>();                              // 根据: 卫星id/轨道圈号, 快速索引轨道
        Map<Long, Day> dayMap = new HashMap<>();                                    // 根据: 日期id,          快速索取日期(北京时)
        Map<String, Day> satelliteDayMap = new HashMap<>();                         // 根据: 卫星id/日期id    快速索取日期(卫星时)
        Map<String, List<Range>> rangeListMap = new HashMap<>();                    // 根据: 星/站/天,        快速索引出当天有哪些弧段
        List<Range> rangeList = new ArrayList<>();
        long scenarioBeginTime= (long)Infinity;
        long scenarioEndTime  = 0;
        long minRangeDuration = (long)Infinity;
        long maxRangeDuration = 0;
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file:///" + path + "\\Forecast_all_satellite_Simple.XML");
        // 1. 遍历Satellite数据
        NodeList nodeList = document.getElementsByTagName("Satellite");
        for(int i = 0 ; i < nodeList.getLength() ; i ++) {
            Element antennaElement = (Element) nodeList.item(i);
            long satelliteId = Long.valueOf(antennaElement.getElementsByTagName("SatelliteIndex").item(0).getTextContent());  // 卫星id
            // 匹配卫星
            Satellite satellite;
            if(!satelliteMap.containsKey(satelliteId)) {
                satellite = new Satellite();
                satellite.setId(satelliteId);
                satelliteMap.put(satelliteId, satellite);
            }
            satellite = satelliteMap.get(satelliteId);

            // 2. 遍历day数据
            NodeList dayNodeList = antennaElement.getElementsByTagName("OneDayForecast");
            for(int j = 0 ; j < dayNodeList.getLength() ; j ++) {
                Element dayElement = (Element) dayNodeList.item(j);
                long dayIndex = Long.valueOf(dayElement.getElementsByTagName("DayIndex").item(0).getTextContent()); // 日期index(卫星时)
                // 匹配日期(卫星时)
                Day satelliteDay;
                String satelliteDayIndex = "星" + satelliteId + "/天" + dayIndex;
                if(!satelliteDayMap.containsKey(satelliteDayIndex)) {
                    satelliteDay = new Day();
                    satelliteDay.setId(dayIndex);
                    satelliteDay.setBeginTime((long) Infinity);
                    satelliteDay.setEndTime(0);
                    satelliteDay.setName(satelliteDayIndex);
                    satelliteDayMap.put(satelliteDayIndex, satelliteDay);
                }
                satelliteDay = satelliteDayMap.get(satelliteDayIndex);

                // 3. 遍历record数据(一个record即一个range)
                NodeList recordNodeList = dayElement.getElementsByTagName("OneRecordForecast");
                for(int k = 0 ; k < recordNodeList.getLength() ; k ++) {
                    Element recordElement = (Element) recordNodeList.item(k);
                    long antennaId = Long.valueOf(recordElement.getElementsByTagName("DeviceIndex").item(0).getTextContent());                          // 天线id (DeviceIndex)
                    long orbitId   = Long.valueOf(recordElement.getElementsByTagName("RevolutionTotalNum").item(0).getTextContent());                   // 卫星圈号
                    long orbitIndexInChina = Long.valueOf(recordElement.getElementsByTagName("RevolutionIdentificationIndex").item(0).getTextContent());// 境内第几圈
                    long orbitNumInChina   = Long.valueOf(recordElement.getElementsByTagName("RevolutionConsecutiveNum").item(0).getTextContent());     // 境内总圈数
                    boolean rise = recordElement.getElementsByTagName("RiseFallFlag").item(0).getTextContent().equals("true");                          // 升降轨
                    NodeList timeNodeList      = recordElement.getElementsByTagName("TimeSecond");                                                            // 三点报时间NodeList
                    NodeList elevationNodeList = recordElement.getElementsByTagName("ElevationAngle");                                                        // 三点报仰角NodeList
                    long beginTime = Long.valueOf(timeNodeList.item(0).getTextContent());                                                               // 三点报进站点时间
                    long endTime   = Long.valueOf(timeNodeList.item(2).getTextContent());                                                               // 三点报出站时间
                    double elevation = Double.valueOf(elevationNodeList.item(1).getTextContent());                                                      // 三点报中的仰角
                    scenarioBeginTime = Math.min(scenarioBeginTime, beginTime);                                                                               // 更新场景开始时间
                    scenarioEndTime   = Math.max(scenarioEndTime,   endTime);                                                                                 // 更新场景结束时间

                    // 匹配日期(北京时间)
                    long dayId = beginTime / 86400;
                    /*long dayId = satelliteDay.getId();*/
                    Day day;
                    if(!dayMap.containsKey(dayId)) {
                        day = new Day();
                        day.setId(dayId);
                        day.setBeginTime((long) Infinity);
                        day.setEndTime(0);
                        dayMap.put(dayId, day);
                    }
                    day = dayMap.get(dayId);

                    // 匹配轨道
                    Orbit orbit;
                    String orbitIndex = "星" + satellite.getId() + "/轨" + orbitId;
                    if(!orbitMap.containsKey(orbitIndex)) {
                        orbit = new Orbit();
                        orbit.setId(orbitId);
                        orbit.setIndex(orbitIndexInChina + "");
                        orbit.setDay(day);
                        orbit.setSatellite(satellite);
                        orbit.setRise(rise);
                        orbit.setBeginTime(beginTime);
                        orbit.setEndTime(endTime);
                        orbit.setName(orbitIndex);
                        if(orbitIndexInChina == 1)                   orbit.setComment("入境圈");     // 入境为第一圈
                        else if(orbitIndexInChina == orbitNumInChina)orbit.setComment("出境圈");     // 出境为最后圈
                        else                                         orbit.setComment("中间圈");     // 其余为中间圈
                        orbit.setComment(orbit.getComment() + "/" + orbitNumInChina);                // 备注: 某圈/总境内圈数
                        orbitMap.put(orbitIndex, orbit);                                             // 用comment (satId : orbitId)去索引
                    }
                    orbit = orbitMap.get(orbitIndex);
                    if(beginTime < orbit.getBeginTime())             orbit.setBeginTime(beginTime);  // 更新轨道开始时间
                    if(endTime   > orbit.getEndTime())               orbit.setEndTime(endTime);      // 更新轨道结束时间
                    if(beginTime < day.getBeginTime())               day.setBeginTime(beginTime);    // 更新日期(北京时)开始时间
                    if(endTime   > day.getEndTime())                 day.setEndTime(endTime);        // 更新日期(北京时)结束时间
                    if(beginTime < satelliteDay.getBeginTime())      satelliteDay.setBeginTime(beginTime);  // 更新日期(卫星时)开始时间
                    if(endTime   > satelliteDay.getEndTime())        satelliteDay.setEndTime(endTime);      // 更新日期(卫星时)结束时间

                    // 新建弧段
                    Antenna antenna = antennaMap.get(antennaId);
                    String rangeListIndex = "星" + satelliteId + "/站" + antennaId + "/天" + dayIndex;                // 根据: 星/站/天, 快速索引出当天有哪些弧段
                    if(!rangeListMap.containsKey(rangeListIndex)) {
                        rangeListMap.put(rangeListIndex, new ArrayList<>());
                    }
                    Range range;
                    range = new Range();
                    range.setOrbit(orbit);
                    range.setAntenna(antenna);
                    range.setBeginTime(beginTime);
                    range.setEndTime(endTime);
                    range.setCapability(endTime - beginTime);
                    range.setElevation(elevation);
                    range.setName(rangeListIndex + "/轨" + orbitId);
                    range.setComment(range.getName());
                    rangeListMap.get(rangeListIndex).add(range);    // 索引: 星/站/天 -> rangeList
                    range.setId((long) rangeList.size());
                    rangeList.add(range);
                    minRangeDuration = Math.min(minRangeDuration, range.getCapability());
                    maxRangeDuration = Math.max(maxRangeDuration, range.getCapability());
                }   /* record loop ends */
            }   /* day loop ends */
        }   /* satellite loop ends */
        // 数据处理
        List<Day> dayList = new ArrayList<>(dayMap.values());
        List<Orbit> orbitList = new ArrayList<>(orbitMap.values());
        List<Antenna> antennaList = new ArrayList<>(antennaMap.values());
        List<Satellite> satelliteList = new ArrayList<>(satelliteMap.values());
        List<Day> satelliteDayList = new ArrayList<>(satelliteDayMap.values());
        dayList.sort(new WindowBeginTimeComparator());
        orbitList.sort(new WindowBeginTimeComparator());
        satelliteDayList.sort(new WindowBeginTimeComparator());
        // 更新前后轨道数据
        Map<Satellite, List<Orbit>> satelliteOrbitsMap = orbitList.stream().collect(Collectors.groupingBy(Orbit::getSatellite));
        for(Satellite satellite : satelliteOrbitsMap.keySet()) {
            List<Orbit> orbits = satelliteOrbitsMap.get(satellite);
            for(int i = 0 ; i < orbits.size() - 1 ; i ++) {
                Orbit lastOrbit = orbits.get(i);
                Orbit nextOrbit = orbits.get(i + 1);
                if(Math.abs(lastOrbit.getId() - nextOrbit.getId()) == 1) {  // 同颗卫星, 圈号差1, 即为相邻圈
                    lastOrbit.setNextOrbit(nextOrbit);                      // 后一圈变量赋值
                    nextOrbit.setLastOrbit(lastOrbit);                      // 前一圈变量赋值
                }
                lastOrbit.setCapability(lastOrbit.getEndTime() - lastOrbit.getBeginTime());
                nextOrbit.setCapability(nextOrbit.getEndTime() - nextOrbit.getBeginTime());
            }
        }
        // 更新日期起止日期
        for(Day day : dayList) {
            day.setBeginDate(new Date(day.getBeginTime() * 1000));
            day.setEndDate(new Date(day.getEndTime() * 1000));
            day.setCapability(day.getEndTime() - day.getBeginTime());
        }
        for(Day day : satelliteDayList) {
            day.setBeginDate(new Date(day.getBeginTime() * 1000));
            day.setEndDate(new Date(day.getEndTime() * 1000));
            day.setCapability(day.getEndTime() - day.getBeginTime());
        }
        // 创建场景
        Scenario scenario = new Scenario();
        scenario.setId(Long.valueOf(path.split("/")[path.split("/").length - 1]));
        scenario.setBeginTime(scenarioBeginTime);
        scenario.setEndTime(scenarioEndTime);
        scenario.setDayList(dayList);
        scenario.setOrbitList(orbitList);
        scenario.setAntennaList(antennaList);
        scenario.setSatelliteList(satelliteList);
        scenario.setRangeList(rangeList);
        scenario.setDayMap(dayMap);
        scenario.setSatelliteDayMap(satelliteDayMap);
        scenario.setOrbitMap(orbitMap);
        scenario.setAntennaMap(antennaMap);
        scenario.setSatelliteMap(satelliteMap);
        scenario.setRangeListMap(rangeListMap);
        logger.info("已生成 -> 调度周期: " + dayMap.size()    + " 天 (北京时, 范围 : " + Tool.getTime(scenarioBeginTime * 1000) + " -> " + Tool.getTime(scenarioEndTime * 1000) + ") ;");
        logger.info("已生成 -> 卫    星: " + satelliteMap.size()  + " 颗 ;");
        logger.info("已生成 -> 卫星轨道: " + orbitMap.size()  + " 圈 ;");
        logger.info("已生成 -> 测传弧段: " + rangeList.size() + " 条 (平均每星每站 " + Tool.round(1.0 * rangeList.size() / rangeListMap.size(), 1) + " 圈/天; 最短 " + minRangeDuration + " s, 最长 " + maxRangeDuration + " s) !\n");
        return scenario;
    }


/* class ends */
}
