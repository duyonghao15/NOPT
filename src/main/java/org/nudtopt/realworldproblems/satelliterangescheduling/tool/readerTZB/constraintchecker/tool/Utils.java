package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool;

import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.model.*;
import org.w3c.dom.Element;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class Utils {

    public static String getItemConByTagName(Element element, String tagName, int itemIndex) {
        return element.getElementsByTagName(tagName).item(itemIndex).getTextContent();
    }

    public static String getFirstItemConByTagName(Element element, String tagName) {
        return getItemConByTagName(element, tagName, 0);
    }

    public static String generateTaskSDS(long sn, long day, long subIndex) {
        return "Sn" + sn + "-D" + day + "-SubIndex" + subIndex;
    }

    public static String generateTaskSDS(Task task) {
        return "Sn" + task.getSnIndex() + "-D" + task.getDayIndex() + "-SubIndex" + task.getSubIndex();
    }

    public static String generateTaskSDS(Plan plan) {
        return "Sn" + plan.getSnIndex() + "-D" + plan.getDayIndex() + "-SubIndex" + plan.getSubIndex();
    }

    public static String generateTaskName(Task task) {
        //SN3/天0/index3
        return "SN" + task.getSnIndex() + "/天" + task.getDayIndex() + "/Index" + task.getSubIndex();
    }

    public static String generateArcSDAO(long satelliteIndex, long dayIndex, long antennaIndex, long orbitIndex) {
        return "S" + satelliteIndex + "-D" + dayIndex + "-A" + antennaIndex + "-O" + orbitIndex;
    }

    public static String generateArcSDAO(Arc arc) {
        return "S" + arc.getSatelliteIndex() + "-D" + arc.getDayIndex() + "-A" + arc.getAntennaIndex() + "-O" + arc.getOrbitIndex();
    }

    public static String generateArcSDAO(Plan plan) {
        return "S" + plan.getSatelliteIndex() + "-D" + plan.getDayIndex() + "-A" + plan.getAntennaIndex() + "-O" + plan.getOrbitIndex();
    }

    public static String logFormatString(String str, int targetLength, String paddingChar) {
        int strLength = str.length();

        int totalPadding = targetLength - strLength;
        // int leftPadding  = totalPadding / 2;
        int leftPadding  = 6;
        // int rightPadding = totalPadding - leftPadding;
        int rightPadding = 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            sb.append(paddingChar);
        }
        sb.append(str);
        for (int i = 0; i < rightPadding; i++) {
            sb.append(paddingChar);
        }
        return sb.toString();
    }

    public static String logFormatString(String str) {
        return logFormatString(str, 40, "》");
    }

    public static String logFormatString(String str, int targetLength) {
        return logFormatString(str, targetLength, "》");
    }
}
