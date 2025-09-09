package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.app;

import org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool.CalScore;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class test {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("数传100%");
        System.out.println(CalScore.calDDTScore(1));
        System.out.println("测控100%");
        System.out.println(CalScore.calTTCScore(1));
    }

}

