package org.nudtopt.realworldproblems.apiforagent.gui;

import org.nudtopt.realworldproblems.apiforagent.model.Agent;
import org.nudtopt.realworldproblems.apiforagent.model.Link;
import org.nudtopt.realworldproblems.apiforagent.model.Master;
import org.nudtopt.realworldproblems.apiforagent.model.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import static java.lang.Short.MAX_VALUE;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class MultiAgentGUI extends JFrame {

    protected List<Agent> agentList;

    protected JButton startButton = new JButton();
    protected JButton waitButton = new JButton();
    protected JButton stopButton = new JButton();
    protected JButton addButton = new JButton();

    protected JTable table = new JTable();           // 表格
    protected JTextArea textArea = new JTextArea();  // 日志
    protected JTabbedPane pane = new JTabbedPane();  // 选项版

    public static void main(String[] args) {
        new MultiAgentGUI().openGUI();
    }

    public void openGUI() {
        // 设置窗格
        setPane();

        // 加载窗体上的事件
        start();
        waiting();
        stop();
        add();

        // 布局
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(LEADING).addComponent(pane, DEFAULT_SIZE, 1000, MAX_VALUE)
                /*.addComponent(progressBar)*/
                .addGroup(layout.createSequentialGroup()
                        .addGap(50, 200, MAX_VALUE).addComponent(startButton)
                        .addGap(50, 200, MAX_VALUE).addComponent(waitButton)
                        .addGap(50, 200, MAX_VALUE).addComponent(stopButton)
                        .addGap(50, 200, MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                        .addGap(50, 200, MAX_VALUE).addComponent(addButton)
                        .addGap(50, 200, MAX_VALUE))
        );
        layout.setVerticalGroup(layout.createParallelGroup(LEADING).addGroup(TRAILING, layout.createSequentialGroup()
                /*.addGap(10, 20, MAX_VALUE).addComponent(progressBar)*/
                .addGap(10, 20, MAX_VALUE)
                .addGroup(layout.createParallelGroup(BASELINE).addComponent(startButton).addComponent(waitButton).addComponent(stopButton))
                .addGap(10, 20, MAX_VALUE).addComponent(pane, PREFERRED_SIZE, 700, PREFERRED_SIZE)
                .addGap(10, 20, MAX_VALUE)
                .addGroup(layout.createParallelGroup(BASELINE).addComponent(addButton))
                .addGap(10, 20, MAX_VALUE))
        );
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 叉关闭进程
        setTitle("SpodScheduler  V1.3.2");
        setVisible(true);
        /* function ends */
    }



    // #############################################################
    // #####################  以下为内置的方法  ######################
    // #############################################################
    // 1. 开始仿真
    protected void start() {
        startButton.setText("开始仿真");
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            waitButton.setEnabled(true);
            stopButton.setEnabled(true);
            for(Agent agent : agentList) {
                if(agent.getState().equals("wait")) {
                    agent.wait(false);
                    agent.setState("start");           // 取消暂停, 重启线程
                } else {
                    Thread thread = new Thread(agent); // 新建并启动线程
                    thread.start();
                }
            }
        });
    }

    // 2. 暂停算法
    protected void waiting() {
        waitButton.setText("暂    停");
        waitButton.setEnabled(false);
        waitButton.addActionListener(e -> {
            for(Agent agent : agentList) {
                agent.setState("wait");
            }
            startButton.setEnabled(true);
            waitButton.setEnabled(false);
            update();
        });
    }


    // 3. 结束仿真
    protected void stop() {
        stopButton.setText("结束调度");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> {
            for(Agent agent : agentList) {
                agent.setState("stop");
            }
            startButton.setEnabled(false);
            waitButton.setEnabled(false);
            addButton.setEnabled(false);
            update();
        });
    }

    // 4. 插入消息
    protected void add() {
        addButton.setText("向 Master Agent 传入新消息");
        addButton.setEnabled(true);
        addButton.addActionListener(e -> {
            update();
        });
    }


    protected void updateTextArea(JTextArea textArea){
        OutputStream textAreaStream = new OutputStream() {
            public void write(int b) {
                textArea.append(String.valueOf((char)b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            public void write(byte b[]) {
                textArea.append(new String(b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            public void write(byte b[], int off, int len) {
                textArea.append(new String(b, off, len));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        };
        System.setOut(new PrintStream(textAreaStream));
        System.setErr(new PrintStream(textAreaStream));
    }


    // #############################################################
    // ##################  以下为可重写方法(接口)  ###################
    // #############################################################
    // 0. 设置窗格
    protected void setPane() {
        pane.addTab("Multi-Agent通讯记录", new JScrollPane(table));  // 表格
        pane.addTab("控制台日志信息", new JScrollPane(textArea));     // 日志
        updateTextArea(textArea);
        update();
    }


    // 1. 更新table
    public void update() {
        updateTable(table);
    }
    protected void updateTable(JTable table) {
        Master master = (Master) agentList.get(0);
        List<Link> linkList = master.getLinkList();
        // 1. 确定表格列数/行数
        int colNum = linkList.size();
        int rowNum = 1;
        for(Link link : linkList) {
            List<Message> messageList = link.getMessageList();
            rowNum = Math.max(rowNum, messageList.size());
        }
        // 2，定义表格
        table.setModel(new DefaultTableModel(rowNum, colNum));
        // 3. 依次赋值
        for(int j = 0 ; j < linkList.size() ; j ++) {
            Link link = linkList.get(j);
            table.getColumnModel().getColumn(j).setHeaderValue(link);  // 表头
            List<Message> messageList = link.getMessageList();
            for(int i = 0 ; i < messageList.size() ; i ++) {
                Message message = messageList.get(i);
                table.getModel().setValueAt(message, i, j);
            }
        }
    }


    // getter & setter
    public List<Agent> getAgentList() {
        return agentList;
    }
    public void setAgentList(List<Agent> agentList) {
        this.agentList = agentList;
    }



    /* class ends */
}
