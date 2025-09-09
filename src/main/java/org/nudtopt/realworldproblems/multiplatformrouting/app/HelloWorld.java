package org.nudtopt.realworldproblems.multiplatformrouting.app;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.multiplatformrouting.constraint.CalScore;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.exportor.Exporter;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.exportor.Plot;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.reader.Reader;
import org.nudtopt.api.algorithm.localsearch.LateAcceptance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Reference:
 * 杜永浩, 向尚, 邢立宁, 等. 天临空协同对地观测任务规划模型与并行竞争模因算法[J]. 控制与决策, 2021, 36(3): 523–533.
 * 杜永浩, 邢立宁, 陈盈果. 多平台海上协同搜索与路径优化策略研究[J]. 控制与决策, 2020, 35(1): 147–154.
 */

public class HelloWorld {


    public static void main(String[] args) throws Exception {
        // 1. 创建场景
        Scenario scenario = new Reader().createScenario(null);

        // 2. 创建约束
        CalScore cal = new CalScore();
        scenario.setConstraint(cal);
        scenario.updateScore();

        // 3. 创建算法
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setSolution(scenario);
        algorithm.setIteration(5000000);
        algorithm.setTabuSize(4);
        algorithm.setLateSize(50);
        Tool.setRandom(new Random(30));

        // 4. 运行算分
        algorithm.run();

        /* function ends */
        String time = new SimpleDateFormat("MM-dd HH.mm").format(new Date());
        String path = "C:/Users/Think/Desktop/multiplatformrouting/" + time;
        new File(path).mkdir();
        Exporter.writeTxtFile(scenario, path);
        Plot.plotNodeList(scenario, "airplane", path);
        Plot.plotNodeList(scenario, "airship", path);
        Plot.plotNodeList(scenario, "satellite", path);
        Plot.plotNodeList(scenario, "all", path);
    }


}
