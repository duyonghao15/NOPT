package org.nudtopt.realworldproblems.multiplatformrouting.app;

import org.nudtopt.api.algorithm.localsearch.LateAcceptance;
import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.realworldproblems.multiplatformrouting.constraint.CalScore;
import org.nudtopt.realworldproblems.multiplatformrouting.gui.MultiPlatformPanel;
import org.nudtopt.realworldproblems.multiplatformrouting.model.Scenario;
import org.nudtopt.realworldproblems.multiplatformrouting.tool.reader.Reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Reference:
 * 杜永浩, 向尚, 邢立宁, 等. 天临空协同对地观测任务规划模型与并行竞争模因算法[J]. 控制与决策, 2021, 36(3): 523–533.
 * 杜永浩, 邢立宁, 陈盈果. 多平台海上协同搜索与路径优化策略研究[J]. 控制与决策, 2020, 35(1): 147–154.
 */

public class GUI {


    public static void main(String[] args) {
        // 1. 创建场景
        Scenario scenario = new Reader().createScenario("semi-circle");

        // 2. 创建约束
        CalScore cal = new CalScore();
        scenario.setConstraint(cal);
        scenario.updateScore();

        // 3. 创建算分
        LateAcceptance algorithm = new LateAcceptance();
        algorithm.setSolution(scenario);
        algorithm.setIteration(5000000);
        algorithm.setTabuSize(4);
        algorithm.setLateSize(50);
        Tool.setRandom(new Random(30));

        // 4. 启动gui
        SolutionGUI gui = new SolutionGUI();
        gui.setSolution(scenario);
        gui.setAlgorithm(algorithm);
        gui.getPanelList().addAll(createPanel());   // 新增画布
        gui.openGUI();
    }


    /**
     * 新增画布, 绘制路径覆盖效果图
     */
    public static List<MultiPlatformPanel> createPanel() {
        MultiPlatformPanel panel_1 = new MultiPlatformPanel();
        MultiPlatformPanel panel_2 = new MultiPlatformPanel();
        MultiPlatformPanel panel_3 = new MultiPlatformPanel();
        MultiPlatformPanel panel_4 = new MultiPlatformPanel();
        panel_1.setName("airplane");
        panel_2.setName("airship");
        panel_3.setName("satellite");
        panel_4.setName("all");
        return Arrays.asList(panel_1, panel_2, panel_3, panel_4);
    }


}
