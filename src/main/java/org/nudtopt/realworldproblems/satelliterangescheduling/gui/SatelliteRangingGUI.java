package org.nudtopt.realworldproblems.satelliterangescheduling.gui;

import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.gui.SolutionGUI;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.exporter.Exporter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;

public class SatelliteRangingGUI extends SolutionGUI {

    private ScenarioPanel scenarioPanel = new ScenarioPanel();

    @Override
    protected void setMainWindow() {
        JPanel mainWindow = new JPanel(new GridLayout(2, 2, 20, 20));
        mainWindow.add(new JScrollPane(algorithmTable));
        mainWindow.add(new JScrollPane(plot));
        mainWindow.add(new JScrollPane(decisionTable));
        mainWindow.add(new JScrollPane(logger));
        ((JScrollPane)mainWindow.getComponent(0)).setBorder(new TitledBorder("算法设置"));
        ((JScrollPane)mainWindow.getComponent(1)).setBorder(new TitledBorder("收敛曲线"));
        ((JScrollPane)mainWindow.getComponent(2)).setBorder(new TitledBorder("决策矩阵"));
        ((JScrollPane)mainWindow.getComponent(3)).setBorder(new TitledBorder("算法日志"));
        // Override
        this.mainWindow = new JTabbedPane();
        this.mainWindow.add("主界面", mainWindow);
        this.mainWindow.add("甘特图", new JScrollPane(scenarioPanel));
    }


    @Override
    public void update() {
        updateDecisionTable(decisionTable, algorithm.getSolution());
        updatePlot();
        scenarioPanel.reset((Scenario) algorithm.getSolution());
    }



    @Override
    protected void writeSolution(String path, Solution solution) {
        try {
            // Exporter.writeSolution(path, solution);
            Exporter.export((Scenario)solution, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


/* class ends */
}
