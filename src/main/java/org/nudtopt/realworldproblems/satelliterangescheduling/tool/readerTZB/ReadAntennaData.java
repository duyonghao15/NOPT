package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

public class ReadAntennaData extends MainLogger {


    public static void main(String[] args) throws Exception {
        String path = Tool.getDesktopPath() + "/xml/测控数据";                  // 根目录路径
        Map<Long, Antenna> antennaMap = new ReadAntennaData().read(path);       // 读取天线数据
        Scenario scenario = new ReadRangeData().read(path, antennaMap);         // 读取弧段数据并创建场景
    }

    /**
     * 读取天线设备数据
     * @param path 根目录路径
     * @author     杜永浩
     * @return Map<id, antenna> 天线数据及id map索引
     */
    public Map<Long, Antenna> read(String path) throws Exception {
        logger.info("正在读取天线设备数据 ......");
        Map<Long, Antenna> antennaMap = new HashMap<>();                                                                        // 设备map
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file:///" + path + "/Device_Normalized_Simple.XML");
        NodeList nodeList = document.getElementsByTagName("Device");                                                            // 根据Device索引
        for(int i = 0 ; i < nodeList.getLength() ; i ++) {                                                                      // 遍历Device数据
            Element antennaElement = (Element) nodeList.item(i);                                                                // 转换为Element
            long antennaId = Long.valueOf(antennaElement.getElementsByTagName("DeviceIndex").item(0).getTextContent());   // 设备id
            Antenna antenna;                                                                                                    // 设备
            if(!antennaMap.containsKey(antennaId)) {                                                                            // 若map中不含该天线id, 则新建天线
                antenna = new Antenna();
                antenna.setId(antennaId);
                antennaMap.put(antennaId, antenna);
            }
            antenna = antennaMap.get(antennaId);                                                                                // 设备
            NodeList beginNodeList = antennaElement.getElementsByTagName("TimeStart");                                          // 设备禁用开始时间的NodeList
            NodeList endNodeList   = antennaElement.getElementsByTagName("TimeEnd");                                            // 设备禁用结束时间的NodeList
            for(int j = 0 ; j < beginNodeList.getLength() ; j ++) {
                long beginTime = Long.valueOf(beginNodeList.item(j).getTextContent());                                          // 设备禁用开始时间
                long endTime   = Long.valueOf(endNodeList  .item(j).getTextContent());                                          // 设备禁用开始时间
                Window window = new Window();                                                                                   // 禁用窗口
                window.setBeginTime(beginTime);
                window.setEndTime(endTime);
                antenna.getForbiddenWindowList().add(window);                                                                   // 禁用窗口赋值给antenna
            }
        }
        logger.info("已生成 -> 天线设备: " + antennaMap.size() + " 套 ！\n");
        return antennaMap;
    }




/* class ends */
}
