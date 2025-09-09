package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Exporter extends MainLogger {


    public void write(Scenario scenario, String path, String name) throws Exception {
        logger.info("正在导出测传任务调度结果 ......");
        List<Task> taskList = scenario.getTaskList();
        List<Task> TTCList = taskList.stream().filter(t -> t.getType().equals("TTC")).collect(Collectors.toList());         // 测控任务列表
        List<Task> DDTList = taskList.stream().filter(t -> t.getType().equals("DDT")).collect(Collectors.toList());         // 数传任务列表
        List<Task> scheduledTTCList = TTCList.stream().filter(t -> t.getRange() != null).collect(Collectors.toList());      // 成功的测控任务列表
        List<Task> scheduledDDTList = DDTList.stream().filter(t -> t.getRange() != null).collect(Collectors.toList());      // 成功的数传任务列表
        double ttcRatio = Tool.round(100.0 * scheduledTTCList.size() / TTCList.size(), 2);
        double ddtRatio = Tool.round(100.0 * scheduledDDTList.size() / DDTList.size(), 2);
        double meanRatio = Tool.round(0.5 * (ttcRatio + ddtRatio), 2);
        logger.info("已调度 -> 测控任务: " + scheduledTTCList.size()  + "个, 成功率: " + ttcRatio + " % ;");
        logger.info("已调度 -> 数传任务: " + scheduledDDTList.size()  + "个, 成功率: " + ddtRatio + " % ;");
        logger.info("总  计 -> 成功任务: " + (scheduledTTCList.size() + scheduledDDTList.size()) + "个, 平均成功率: " + meanRatio + " % !");

        // 创建根元素
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element GNPLAN = document.createElement("GNPLAN");
        document.appendChild(GNPLAN);

        // 创建子元素
        for(Task task : scenario.getTaskList()) {
            if(task.getRange() == null)     continue;
            long satelliteId   = task.getRange().getOrbit().getSatellite().getId();
            long antennaId     = task.getRange().getAntenna().getId();
            long orbitId       = task.getRange().getOrbit().getId();
            long taskBeginTime = task.getBeginTime();
            long rangeBeginTime= task.getRange().getBeginTime();
            long rangeEndTime  = task.getRange().getEndTime();
            long taskEndTime   = task.getEndTime();
            String taskSN      = task.getName().split("/")[0].split("SN")[1];
            long dayId         = task.getSatelliteDay().getId();
            String subIndex    = task.getName().split("/index")[1];

            Element PLAN = document.createElement("PLAN");
            GNPLAN.appendChild(PLAN);

            Element e1 = document.createElement("SPACECRAFTCODE");
            e1.appendChild(document.createTextNode(String.valueOf(satelliteId)));
            PLAN.appendChild(e1);

            Element e2 = document.createElement("DEVCODE");
            e2.appendChild(document.createTextNode(String.valueOf(antennaId)));
            PLAN.appendChild(e2);

            Element e3 = document.createElement("TTCREVNO");
            e3.appendChild(document.createTextNode(String.valueOf(orbitId)));
            PLAN.appendChild(e3);

            Element e4 = document.createElement("TWS");
            e4.appendChild(document.createTextNode(String.valueOf(taskBeginTime)));
            PLAN.appendChild(e4);

            Element e5 = document.createElement("TTS");
            e5.appendChild(document.createTextNode(String.valueOf(rangeBeginTime)));
            PLAN.appendChild(e5);

            Element e6 = document.createElement("TTE");
            e6.appendChild(document.createTextNode(String.valueOf(rangeEndTime)));
            PLAN.appendChild(e6);

            Element e7 = document.createElement("TWE");
            e7.appendChild(document.createTextNode(String.valueOf(taskEndTime)));
            PLAN.appendChild(e7);

            Element e8 = document.createElement("TaskSN");
            e8.appendChild(document.createTextNode(taskSN));
            PLAN.appendChild(e8);

            Element e9 = document.createElement("DayIndex");
            e9.appendChild(document.createTextNode(String.valueOf(dayId)));
            PLAN.appendChild(e9);

            Element e10 = document.createElement("SubIndex");
            e10.appendChild(document.createTextNode(String.valueOf(subIndex)));
            PLAN.appendChild(e10);
        }

        // 输出
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        path += "/" + name + "+TTC" + ttcRatio + "+DDT" + ddtRatio;
        File file = new File(path);
        if(!file.exists())      file.mkdirs();  // 创新文件夹
        path += "/000_0" + scenario.getId() + "_" + time + "_GNPLAN" + ".xml";

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(path));
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(source, result);
        logger.info("已输出 -> 结果文件: " + path + "\n");
        /* function ends */
    }



/* class ends */
}
