package org.nudtopt.api.tool.gui;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;
import org.nudtopt.api.tool.function.Exporter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Short.MAX_VALUE;
import static javax.swing.GroupLayout.Alignment.*;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class SolutionGUI extends JFrame {

    protected Solution solution;
    protected Algorithm algorithm;
    protected JComponent mainWindow;

    protected JButton readButton = new JButton();
    protected JButton writeButton = new JButton();
    protected JButton startButton = new JButton();
    protected JButton waitButton = new JButton();
    protected JButton stopButton = new JButton();
    protected JProgressBar progressBar = new JProgressBar();// 进度条

    protected JTable decisionTable;                         // 决策表格
    protected JTable algorithmTable;                        // 算法表格
    protected JTextArea logger;                             // 系统日志
    protected PlotPanel plot;                               // 收敛曲线
    protected Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); // 屏幕
    protected List<JPanel> panelList = new ArrayList<>();   // 自定义绘图集

    public static void main(String[] args) {
        new SolutionGUI().openGUI();
    }

    public void setTitle() {setTitle("NOPT  Beta");}


    public void openGUI() {
        // 1. 主窗体
        algorithmTable  = createAlgorithmTable();           // 算法表格
        decisionTable   = createDecisionTable();            // 决策表格
        logger          = createLogger();                   // 系统日志
        plot            = createPlot();                     // 收敛曲线
        setMainWindow();                                    // 主窗口布局

        // 2. 加载事件
        read();                                             // 读数据
        write();                                            // 写数据
        start();                                            // 开始调度
        waiting();                                          // 暂停
        stop();                                             // 停止调度
        progress();                                         // 进度条

        // 3. 布局
        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(LEADING).addComponent(mainWindow)
                .addComponent(progressBar)
                .addGroup(layout.createSequentialGroup()
                        .addGap(50, 200, MAX_VALUE).addComponent(startButton)
                        .addGap(50, 200, MAX_VALUE).addComponent(waitButton)
                        .addGap(50, 200, MAX_VALUE).addComponent(stopButton)
                        .addGap(50, 200, MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                        .addGap(50, 200, MAX_VALUE).addComponent(readButton)
                        .addGap(50, 200, MAX_VALUE).addComponent(writeButton)
                        .addGap(50, 200, MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(LEADING).addGroup(TRAILING, layout.createSequentialGroup()
                .addGap(10, 20, MAX_VALUE).addComponent(progressBar)
                .addGap(10, 20, MAX_VALUE)
                .addGroup(layout.createParallelGroup(BASELINE).addComponent(startButton).addComponent(waitButton).addComponent(stopButton))
                .addGap(10, 20, MAX_VALUE).addComponent(mainWindow, PREFERRED_SIZE, screen.height * 8/10, PREFERRED_SIZE)
                .addGap(10, 20, MAX_VALUE)
                .addGroup(layout.createParallelGroup(BASELINE).addComponent(readButton).addComponent(writeButton))
                .addGap(10, 20, MAX_VALUE))
        );
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 叉关闭进程
        setExtendedState(JFrame.MAXIMIZED_BOTH);        // 最大化
        setTitle();
        setVisible(true);
    }


    // #############################################################
    // #####################  以下为主界面方法  #####################
    // #############################################################
    // 1. 创建算法表格
    protected JTable createAlgorithmTable() {
        List<AlgorithmCell> algorithmCellList = new ArrayList<>();
        int rouNum = 0;
        int columnNum = 4;
        for(int i = 0 ; i < columnNum ; i ++) {
            AlgorithmCell algorithmCell = new AlgorithmCell();
            if(i == 0)    algorithmCell.readAlgorithmList("rule");
            if(i == 1)    algorithmCell.readAlgorithmList("localsearch");
            if(i == 2)    algorithmCell.readAlgorithmList("evolution");
            if(i == 3)    algorithmCell.readAlgorithmList("hybrid");
            algorithmCell.setGui(this);
            algorithmCellList.add(algorithmCell);
            rouNum = Math.max(rouNum, algorithmCell.getAlgorithmList().size());
        }
        JTable algorithmTable = new JTable(new DefaultTableModel(rouNum, columnNum));
        for(int i = 0 ; i < columnNum ; i ++) {
            TableColumn column = algorithmTable.getColumnModel().getColumn(i);
            AlgorithmCell algorithmCell = algorithmCellList.get(i);
            column.setHeaderValue(algorithmCell.getAlgorithmList().get(0).getType());
            column.setCellEditor(algorithmCell.getCellEditor());    // 编辑界面
            column.setCellRenderer(algorithmCell.getCellRender());  // 渲染界面
        }
        return algorithmTable;
    }


    // 2. 创建决策表格
    protected JTable createDecisionTable() {
        JTable table = new JTable();
        if(algorithm != null)   updateDecisionTable(table, algorithm.getSolution());
        else                    table.setModel(new DefaultTableModel(new Object [][] {{null, null, null}}, new String [] {"决策实体", "决策变量1", "决策变量2"}));
        return table;
    }


    // 3. 创建logger(捕获控制台输出到GUI界面上)
    protected JTextArea createLogger(){
        JTextArea logger = new JTextArea();
        OutputStream textAreaStream = new OutputStream() {
            public void write(int b) {
                logger.append(String.valueOf((char)b));
                logger.setCaretPosition(logger.getDocument().getLength());
            }
            public void write(byte b[]) {
                logger.append(new String(b));
                logger.setCaretPosition(logger.getDocument().getLength());
            }
            public void write(byte b[], int off, int len) {
                logger.append(new String(b, off, len));
                logger.setCaretPosition(logger.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(textAreaStream));
        System.setErr(new PrintStream(textAreaStream));
        return logger;
    }


    // 4. 创建收敛曲线
    protected PlotPanel createPlot(){
        PlotPanel plot = new PlotPanel();
        plot.setMaxIteration(algorithm.getIteration());
        plot.setScoreList(algorithm.getHistoryScoreList(0, false));
        return plot;
    }


    // #############################################################
    // #####################  以下为五个按钮方法  ####################
    // #############################################################
    // 1. 读
    protected void read() {
        readButton.setText("载入结果");
        readButton.addActionListener(e -> {
            FileDialog read = new FileDialog(this, "载入结果", FileDialog.LOAD);
            read.setVisible(true);
            String path = read.getDirectory();
            String name = read.getFile();
            if(path == null || name == null)    return;
            algorithm.setSolution(readSolution(path + name));
            algorithm.setState("start");
            update();
            startButton.setEnabled(true);
            writeButton.setEnabled(true);
        });
    }


    // 2. 写
    protected void write() {
        writeButton.setText("导出结果");
        writeButton.setEnabled(false);
        writeButton.addActionListener(e -> {
            FileDialog write = new FileDialog(this, "导出结果", FileDialog.SAVE);
            write.setVisible(true);
            String path = write.getDirectory();
            String name = write.getFile();
            if(path == null || name == null)    return;
            algorithm.wait(false);      // 唤醒线程, 让其自动终止
            algorithm.setState("stop"); // 终止
            writeSolution(path + name, algorithm.getSolution());
            startButton.setEnabled(false);
        });
    }


    // 3. 运行算法
    protected void start() {
        startButton.setText("开始调度");
        startButton.addActionListener(e -> {
            if(algorithm != null) {
                startButton.setEnabled(false);
                waitButton.setEnabled(true);
                stopButton.setEnabled(true);
                algorithmTable.setEnabled(false);
                if (algorithm.getState().equals("wait")) {
                    algorithm.wait(false);
                    algorithm.setState("start");           // 取消暂停, 重启线程
                } else {
                    Thread thread = new Thread(algorithm); // 新建并启动线程
                    thread.start();
                    writeButton.setEnabled(true);
                    startButton.setEnabled(false);
                }
            } else  JOptionPane.showMessageDialog(this, "请配置算法和参数!");
        });
    }


    // 4. 暂停算法
    protected void waiting() {
        waitButton.setText("暂    停");
        waitButton.setEnabled(false);
        waitButton.addActionListener(e -> {
            algorithm.setState("wait");
            startButton.setEnabled(true);
            waitButton.setEnabled(false);
            update();
        });
    }


    // 5. 终止算法
    protected void stop() {
        stopButton.setText("结束调度");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> {
            algorithm.setState("stop");
            startButton.setEnabled(false);
            waitButton.setEnabled(false);
            update();
        });
    }


    // 6. 进度条
    protected void progress() {
        algorithm.setProgressBar(progressBar);
        progressBar.setMinimum(0);
        progressBar.setMaximum((int)algorithm.getIteration());
        progressBar.setStringPainted(true);
    }


    // #############################################################
    // ##################  以下为可重写方法(接口)  ###################
    // #############################################################
    // 1. 设置主窗格
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
        if(panelList.size() == 0) {
            this.mainWindow = mainWindow;
        } else {
            this.mainWindow = new JTabbedPane();
            this.mainWindow.add("主界面", mainWindow);
            for(JPanel panel : panelList){
                this.mainWindow.add(panel.getName() == null || panel.getName().length() == 0 ? "绘图板" : panel.getName(), new JScrollPane(panel));
                try {
                    Method method = panel.getClass().getMethod("setSolution", Solution.class);
                    method.invoke(panel, solution);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                panel.repaint();
            }
        }
    }


    // 2. 更新窗格
    public void update() {
        updateDecisionTable(decisionTable, algorithm.getSolution());
        updatePlot();
        for(JPanel panel : panelList) {
            panel.repaint();
        }
    }


    // 3. 更新决策表格
    protected void updateDecisionTable(JTable table, Solution solution) {
        // a. 表格
        List<DecisionEntity> entityList = solution.getDecisionEntityList();
        int entitySize = entityList.size();
        int variableSize = solution.getMaxDecisionVariableSize();
        table.setModel(new DefaultTableModel(entitySize, variableSize + 1));
        // b. 单元格(可操作)
        EntityCell entityCell = new EntityCell();
        VariableCell variableCell = new VariableCell();
        entityCell.setSolution(solution);
        variableCell.setSolution(solution);
        variableCell.setAlgorithm(algorithm);
        for(int i = 0 ; i < variableSize + 1 ; i ++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            if(i == 0) {
                column.setHeaderValue("决策实体");
                column.setCellEditor(entityCell.getCellEditor());    // 编辑界面
                column.setCellRenderer(entityCell.getCellRender());  // 渲染界面
            } else {
                column.setHeaderValue("决策变量" + i);
                column.setCellEditor(variableCell.getCellEditor());  // 编辑界面
                column.setCellRenderer(variableCell.getCellRender());// 渲染界面
            }
        }
    }


    // 4. 更新收敛曲线
    protected void updatePlot() {
        plot.setNowIteration(progressBar.getValue());
        plot.setScoreList(algorithm.getHistoryScoreList(0, false));
        plot.repaint();
    }


    // 5. 读&写接口
    protected Solution readSolution(String path) {
        try {
            return Exporter.readSolution(path);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }
    protected void writeSolution(String path, Solution solution) {
        try {
            Exporter.writeSolution(path, solution);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // #############################################################
    // ##################  以下为 getter &setter  ###################
    // #############################################################
    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public List<JPanel> getPanelList() {
        return panelList;
    }
    public void setPanelList(List<JPanel> panelList) {
        this.panelList = panelList;
    }

/* class ends */
}


