package org.nudtopt.realworldproblems.multiplatformrouting.tool.reader;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class SolutionReader {

    public static void solutionReader(Scenario scenario, String solutionName) throws Exception {
        List<Node> nodeList = scenario.getNodeList();
        List<Visit> visitList = scenario.getVisitList();
        List<Platform> platformList = scenario.getPlatformList();

        for(Visit visit : visitList) {
            visit.setNode(null);
        }

        FileReader fr = new FileReader("C:/Users/Think/Desktop/" + solutionName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Platform platform = null;
        while ((line = br.readLine()) != null) {
            if(line.length() < 1) continue;
            String name = line.split("\t")[0].split("-")[0];
            int id = Integer.parseInt(line.split("\t")[0].split("-")[1]);
            if(name.equals("Platform")) {
                platform = platformList.get(id);
                continue;
            }
            for(Visit visit : visitList) {
                if(visit.getPlatform() == platform && visit.getNode() == null) {
                    visit.setNode(nodeList.get(id));
                    System.out.println(platform + " 的 " + visit + " 赋值 " + visit.getNode());
                    break;
                }
            }
        }

    }



}
