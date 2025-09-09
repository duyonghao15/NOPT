package org.nudtopt.realworldproblems.satelliterangescheduling.gui;

import org.nudtopt.api.tool.gui.TangoColorFactory;
import org.nudtopt.apidata.Data;
import org.nudtopt.realworldproblems.apiforsatellite.resource.payload.Antenna;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Day;
import org.nudtopt.realworldproblems.satelliterangescheduling.model.Task;
import org.nudtopt.realworldproblems.satelliterangescheduling.tool.comparator.BeginTimeComparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AntennaPanel extends JPanel {

    private final ScenarioPanel scenarioPanel;      // 每个antenna窗口都对应的这个总的界面
    private List<Task> taskList = new ArrayList<>();
    private Antenna antenna;
    private List<Day> dayList;
    private final ImageIcon antennaIcon = new ImageIcon(Data.class.getResource("satelliterangescheduling/antenna.png"));  // 相对路径

    // Header: 标题行
    private JLabel antennaLabel;
    private JTextField summary = new JTextField();  // 统计
    private List<JTextField> summaryList = new ArrayList<>();

    // Bar: 甘特图行
    private List<AntennaBar> barList = new ArrayList<>();

    // 0. 构造方法
    public AntennaPanel(ScenarioPanel scenarioPanel, Antenna antenna, List<Day> dayList) {
        super(new GridLayout(0, dayList == null ? 2 : dayList.size() + 1));
        this.scenarioPanel = scenarioPanel;
        this.antenna = antenna;
        this.dayList = dayList;
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(2, 0, 2, 0),
                  BorderFactory.createLineBorder(Color.GRAY)),
                  BorderFactory.createEmptyBorder(2, 0, 2, 0)));
        createHeader();
        createBar();
    }


    // 1.1 创建标题行
    private void createHeader() {
        // a. 测站信息栏
        JPanel antennaPanel = new JPanel(new BorderLayout(2, 0));
        String antennaName;
        if(antenna == null) {
            antennaName = "    尚未调度的任务:";
        } else {
            antennaPanel.add(new JLabel(antennaIcon), BorderLayout.WEST);
            antennaName = "  测站 " + antenna.getId() + " "/* + antenna.getName()*/;
        }
        antennaLabel = new JLabel(antennaName);
        antennaPanel.add(antennaLabel, BorderLayout.CENTER);
        // b. 任务信息栏
        summary.setEditable(false);
        summary.setBorder(new EmptyBorder(0,0,0,0));
        // c. 布局
        if(dayList == null) {
            add(antennaPanel);
            add(summary);
            return;
        }
        add(antennaPanel);
        for(Day day : dayList) {
            JTextField summary = new JTextField();
            add(summary);
            summaryList.add(summary);
        }
    }


    // 1.2 创建甘特图
    private void createBar() {
        if(dayList == null)  return;
        add(new JLabel());  // 空一格
        for(Day day : dayList) {
            // a. 时间信息
            AntennaBar bar = new AntennaBar(day.getCapability() > 43200 ? 86400 : day.getCapability(), day.getCapability() > 43200 ? 86400 : day.getCapability());
            bar.addActionListener(e -> new TaskListDialog(day));      // 鼠标点击, 打开任务清单
            bar.setToolTipText("打开任务清单");
            bar.setEnabled(false);
            // b. 布局
            add(bar);
            barList.add(bar);
        }
    }


    // 2.1 更新AntennaPanel
    public void update() {
        if(dayList == null) return;
        for(int i = 0 ; i < dayList.size() ; i ++) {
            Day day = dayList.get(i);
            AntennaBar bar = barList.get(i);
            int time = 0;                             // 工作时间
            int num = 0;                              // 任务数
            bar.clearTaskBeginValues();               // 清task
            bar.clearTaskDurationValues();
            for(Task task : taskList) {               // 遍历新的成像task, 重新计算
                if(task.getOptionalRangeList().size() == 0) continue;
                if(task.getRange() == null)  {        // 未调度任务, 列于未安排任务列表(堆栈排放); 若是已安排任务, 则列于轨道列表(相应位置)
                    if(task.getOptionalRangeList().get(0).getOrbit().getDay() != day) continue; // 不是该日任务, 跳过
                    bar.addTaskBeginValue(time);
                    bar.addTaskDurationValue(task.getOptionalRangeList().get(0).getCapability());
                    time += task.getOptionalRangeList().get(0).getCapability();
                    num  ++;
                } else {                              // 已安排任务, 则列于列表(当天相应位置)
                    if(task.getRange().getOrbit().getDay() != day)   continue;   // 不是该日任务, 跳过
                    bar.addTaskBeginValue(task.getRange().getBeginTime() - task.getRange().getOrbit().getDay().getBeginTime());
                    bar.addTaskDurationValue(task.getRange().getCapability());
                    time += task.getRange().getCapability();
                    num  ++;
                }
            }
            updateHeader(i, time, num);               // 更新统计
            updateBar(bar, time > 0);           // 更新甘特图
        }
    }


    // 2.2 更新标题行
    private void updateHeader(int dayId, int time, int num) {
        antennaLabel.setEnabled(time > 0);
        if(dayList == null) return;
        JTextField summary = summaryList.get(dayId);
        summary.setText("任务 " + num + " 个,\t时间 " + time + " s");
        summary.setForeground(Color.BLACK);
        summary.setEnabled(time > 0);
    }


    // 2.3 更新甘特图
    private void updateBar(AntennaBar bar, boolean used) {
        bar.repaint();  // 重画, component中已有图形发生改变后不会立刻显示
        bar.setEnabled(used);
    }


    // 私有静态类
    private static class AntennaBar extends JButton {

        private List<Double> taskBeginValues = new ArrayList<>();
        private List<Double> taskDurationValues = new ArrayList<>();
        private double value;
        private double maxValue;
        // 构造方法
        public AntennaBar(double value, double maxValue) {
            this.value = value;
            this.maxValue = maxValue;
        }

        public void clearTaskBeginValues() {
            taskBeginValues.clear();
        }
        public void clearTaskDurationValues() {
            taskDurationValues.clear();
        }
        public void addTaskBeginValue(double taskBeginValue) {
            taskBeginValues.add(taskBeginValue);
        }
        public void addTaskDurationValue(double taskDurationValue) {
            taskDurationValues.add(taskDurationValue);
        }

        @Override                    // 绘图类
        protected void paintComponent(Graphics g) {
            Dimension size = getSize();         // 封装单个对象中组件的宽度和高度(整数)
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);

            double maxWidth = size.width - 1;
            if (maxWidth <= 1) {
                g.setColor(Color.GRAY);
                g.drawString("?", 2, 2);
                return;
            }
            // 每个值对应的pixels(像素点)
            double pixelsPerValue = maxWidth / maxValue;
            int orbitWidth = (int) (pixelsPerValue * value);
            if(this.value > 0) {
                g.setColor(isEnabled() ? Color.WHITE : getBackground());              // 激活则白色实框, 否则背景色实框
                g.fillRect(0, 0, orbitWidth, size.height);                     // computer容量(实心方框)
            }
            int colorIndex = 0;
            for(int i = 0 ; i < taskBeginValues.size() ; i ++) {
                double taskBeginValue = taskBeginValues.get(i);
                double taskDurationValue = taskDurationValues.get(i);
                int x = (int) Math.round(taskBeginValue * pixelsPerValue);
                int taskWidth = (int) Math.round(taskDurationValue * pixelsPerValue);
                taskWidth = Math.max(taskWidth, 1);
                g.setColor(TangoColorFactory.SEQUENCE_1[colorIndex]);                // 逐一赋颜色
                g.fillRect(x, 0, taskWidth, size.height);                         // 逐一从不同的x位置开始画矩形
                colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_1.length; // 颜色周期性递增变化
            }
            if(this.value > 0) {
                g.setColor(isEnabled() ? Color.BLACK : Color.GRAY);                  // 激活则空心黑框, 否则灰框
                g.drawRect(1, 1, orbitWidth, size.height - 3);          // 空心黑框
            }
        }
    }   /* inner class ends */


    // ##################################################################
    // ####################### 私有类: 任务清单弹窗 #######################
    // ##################################################################
    private class TaskListDialog extends JDialog {

        public TaskListDialog(Day day) {
            // a. 创建任务列表
            JScrollPane taskPane = new JScrollPane(createTaskTable(day));
            taskPane.setPreferredSize(new Dimension(900, 400));
            // b. 布局
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(taskPane);
            setContentPane(panel);
            pack();
            setTitle("任务清单");
            setLocationRelativeTo(getRootPane());
            setModal(true); // 模态的对话框: 不能点击背景, 只能在对话框上操作(需写于setVisible之前)
            setVisible(true);
        }

        // 创建任务列表
        private JTable createTaskTable(Day day) {
            SimpleDateFormat dayFormat  = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat minFormat  = new SimpleDateFormat("HH:mm:ss");
            String[] title = new String []{"序号", "数传任务", "卫星编号", "轨道圈号", "日期", "开始时间", "结束时间", "持续时间(s)", "转换时间(s)"};
            Object[][] results;
            List<Task> todayTaskList = new ArrayList<>();            // 今日任务列表(含已调度和未调度)
            if(taskList.get(0).getRange() == null) {                 // 未调度任务
                for(Task task : taskList) {
                    if(task.getOptionalRangeList().size() == 0)                        continue;
                    if(task.getOptionalRangeList().get(0).getOrbit().getDay() == day)  todayTaskList.add(task);
                }
                results = new Object[todayTaskList.size()][title.length];
                for(int i = 0 ; i < todayTaskList.size() ; i ++) {
                    Task task = todayTaskList.get(i);
                    results[i][0] = i + 1;
                    results[i][1] = task;
                    results[i][2] = task.getOptionalRangeList().get(0).getOrbit().getSatellite() == null ? "—" : task.getOptionalRangeList().get(0).getOrbit().getSatellite().getId();
                    results[i][3] = task.getOptionalRangeList().get(0).getOrbit();
                    results[i][4] = task.getOptionalRangeList().get(0).getOrbit().getDay();
                    results[i][5] = "—";
                    results[i][6] = "—";
                    results[i][7] = task.getOptionalRangeList().get(0).getCapability();
                    results[i][8] = "—";
                }
            } else {                                                 // 已调度任务
                for(Task task : taskList) {
                    if(task.getRange().getOrbit().getDay() == day)  todayTaskList.add(task);
                }
                todayTaskList.sort(new BeginTimeComparator());       // 按测控开始时间排序
                results = new Object[todayTaskList.size()][title.length];
                long lastTime = todayTaskList.get(0).getRange().getOrbit().getDay().getBeginTime();
                for(int i = 0 ; i < todayTaskList.size() ; i ++) {   // 写内容
                    Task task = todayTaskList.get(i);
                    results[i][0] = i + 1;
                    results[i][1] = task;
                    results[i][2] = task.getRange().getOrbit().getSatellite() == null ? "—" : task.getRange().getOrbit().getSatellite().getId();
                    results[i][3] = task.getRange().getOrbit().getId();
                    results[i][4] = task.getRange().getOrbit().getDay().getBeginDate() == null ? "—" : dayFormat.format(task.getRange().getOrbit().getDay().getBeginDate());
                    results[i][5] = minFormat.format(task.getBeginTime() * 1000);
                    results[i][6] = minFormat.format(task.getEndTime() * 1000);
                    results[i][7] = task.getDuration();
                    results[i][8] = Math.max(0, task.getBeginTime() - lastTime);
                    lastTime = task.getEndTime();
                }
            }
            return new JTable(new DefaultTableModel(results, title));
        }
    }   /* inner class ends */


    // 其他函数
    public void addTask(Task task) {
        taskList.add(task);
    }
    public void clearTasks() {
        taskList.clear();
    }


/* class ends */
}
