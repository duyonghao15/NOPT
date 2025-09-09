package org.nudtopt.realworldproblems.multiplatformrouting.tool.exportor;

import org.nudtopt.realworldproblems.multiplatformrouting.model.Node;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Platform;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Visit;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.Function;
import org.nudtopt.api.tool.function.Tool;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Exporter {

    public static void writeTxtFile(Scenario scenario, String path) throws IOException {

        List<Visit> visitList = scenario.getVisitList();
        StringBuilder str = new StringBuilder();

        Map<Platform, List<Node>> platformNodeMap = Function.getPlatformNodeMap(visitList);

        for(Platform platform : platformNodeMap.keySet()) {
            str.append(platform).append("\t" + platform.getType() + "\n");
            for(Node node : platformNodeMap.get(platform)) {
                str.append(node).append("\t").append(node.getX()).append("\t").append(node.getY()).append("\n");
            }
            str.append("\n");
        }
        /* function ends */
        String score = scenario.getScore().getHardScore() + "." + scenario.getScore().getMeanScore() + "." + scenario.getScore().getSoftScore();
        Tool.writeFile(path, score + ".txt", str);
    }



/* class ends */
}
