package org.nudtopt.realworldproblems.satelliterangescheduling.gui;

import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Scenario;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ScenarioPanel extends JPanel {

    private JPanel headerPanel = new JPanel();
    private JPanel antennaListPanel = new JPanel(new GridLayout(0, 1));                     // 轨道列表窗口
    private AntennaPanel taskPool = new AntennaPanel(this, null, null);   // 任务池(未调度的任务)
    private Map<Antenna, AntennaPanel> antennaToPanelMap = new LinkedHashMap<>();                       // 一个antenna对应一个窗口

    // 构造方法
    public ScenarioPanel() {
        // a. 标题行
        headerPanel.add(new JLabel("测站列表"));
        headerPanel.setToolTipText("未开始调度");
        // b. 布局
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(headerPanel).addComponent(antennaListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(antennaListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }


    public void reset(Scenario scenario) {
        // 1. 重画表头(首次)
        if(headerPanel.getToolTipText().equals("未开始调度")) {
            headerPanel.setToolTipText("正在调度");
            headerPanel.setLayout(new GridLayout(0, scenario.getDayList().size() + 1));
            for(Day day : scenario.getDayList()) {
                headerPanel.add(new JLabel(" 第 " + day.getId() + " 天"));
            }
        }

        // 2. 重画甘特图
        antennaToPanelMap.clear();
        antennaListPanel.removeAll();
        taskPool = new AntennaPanel(this, null, scenario.getDayList());
        antennaListPanel.add(taskPool);
        antennaToPanelMap.put(null, taskPool);
        update(scenario);
        /* function ends */
    }


    public void update(Scenario scenario) {
        Set<Antenna> deadAntennaSet = new LinkedHashSet<>(antennaToPanelMap.keySet()); // 获取map全部的键值
        deadAntennaSet.remove(null);                                                // remove 空
        for(Antenna antenna : scenario.getAntennaList()) {
            deadAntennaSet.remove(antenna);                                            // remove orbit, 只剩下dead orbit(不在solution中的)
            AntennaPanel antennaPanel = antennaToPanelMap.get(antenna);
            if(antennaPanel == null) {
                antennaPanel = new AntennaPanel(this, antenna, scenario.getDayList());
                antennaListPanel.add(antennaPanel);
                antennaToPanelMap.put(antenna, antennaPanel);                          // 指定二者关联
            }
            antennaPanel.clearTasks();                                                 // taskList.clear()
        }
        taskPool.clearTasks();

        for(Task task : scenario.getTaskList()) {
            Antenna antenna = task.getRange() == null ? null : task.getRange().getAntenna();
            AntennaPanel antennaPanel = antennaToPanelMap.get(antenna);                // 从map里找到对应的成像panel
            if(antennaPanel != null) {
                antennaPanel.addTask(task);                                            // orbit对应的Panel里加上task(可能多个)
            }
        }
        for(Antenna deadAntenna : deadAntennaSet) {
            AntennaPanel antennaPanel = antennaToPanelMap.remove(deadAntenna);         // map里删除, 同时获得对应的value
            antennaListPanel.remove(antennaPanel);
        }
        for(AntennaPanel antennaPanel : antennaToPanelMap.values()) {
            antennaPanel.update();
        }
    }

}
