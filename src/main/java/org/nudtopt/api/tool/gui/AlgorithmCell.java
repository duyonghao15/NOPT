package org.nudtopt.api.tool.gui;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.evolution.DifferentialEvolution;
import org.nudtopt.api.algorithm.evolution.GeneticAlgorithm;
import org.nudtopt.api.algorithm.hybrid.MemeticAlgorithm;
import org.nudtopt.api.algorithm.localsearch.*;
import org.nudtopt.api.algorithm.rule.*;
import org.nudtopt.api.tool.function.Tool;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmCell {

    private SolutionGUI gui;

    private List<Algorithm> algorithmList;

    protected JCheckBox createPanel(Algorithm algorithm) {
        JCheckBox box = new JCheckBox(algorithm.getName() + (algorithm.getHighlight() ? " ★" : ""));
        box.addActionListener(e -> {
            box.setSelected(true);
            new ParameterListDialog(algorithm);
            if(algorithm != gui.getAlgorithm()) {
                algorithm.setSolution(gui.getSolution());   // 设置solution
                gui.getAlgorithm().setSolution(null);       // 移除solution
                gui.setAlgorithm(algorithm);                // 更换算法
                gui.progress();                             // 更新进度条
                System.out.println("已完成算法及参数配置! 当前算法: " + algorithm.getName());
            }
        });
        return box;
    }


    // Editor(编辑)
    protected class CellEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            if(row < algorithmList.size())  return createPanel(algorithmList.get(row));
            else                            return null;
        }
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }


    // Renderer(渲染)
    protected class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(row < algorithmList.size())  return createPanel(algorithmList.get(row));
            else                            return null;
        }
    }


    // 读取rule/localsearch/evolution/hybrid全部算法实例
    public List<Algorithm> readAlgorithmList(String type) {
        List<Algorithm> algorithmList = new ArrayList<>();
        switch (type) {
            case "rule":
                algorithmList.add(new FirstFit());
                algorithmList.add(new BestFit());
                algorithmList.add(new RandomFit());
                algorithmList.add(new RandomAllocate());
                algorithmList.add(new Deconflict());
                break;
            case "localsearch":
                algorithmList.add(new HillClimbing());
                algorithmList.add(new TabuSearch());
                algorithmList.add(new SimulatedAnnealing());
                algorithmList.add(new LateAcceptance());
                algorithmList.add(new GreatDeluge());
                algorithmList.add(new IteratedLocalSearch());
                break;
            case "evolution":
                algorithmList.add(new GeneticAlgorithm());
                algorithmList.add(new DifferentialEvolution());
                /*algorithmList.add(newversion MOEA());*/
                break;
            case "hybrid":
                algorithmList.add(new MemeticAlgorithm());
                break;
            default:
                break;
        }
        /*try {
            String algorithmPath = URLDecoder.decode(Algorithm.class.getResource("").getPath(),"utf-8");
            for(File file : Tool.readAllFiles(algorithmPath + type)) {
                String name = file.getName().split("\\.")[0];
                Class cls = Class.forName("org.nudtopt.api.algorithm." + type + "."  + name);
                algorithmList.add((Algorithm)cls.newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return this.algorithmList = algorithmList;
    }


    // ##################################################################
    // ####################### 私有类: 算法配置弹窗 #######################
    // ##################################################################
    public class ParameterListDialog extends JDialog {
        public ParameterListDialog(Algorithm algorithm) {
            JPanel panel = new JPanel();
            Field[] fields = algorithm.getClass().getDeclaredFields();
            panel.setLayout(new GridLayout(fields.length, 3, 20, 20));
            panel.setPreferredSize(new Dimension(600, fields.length * 50));

            for(int i = 0 ; i < fields.length ; i ++) {
                String name = fields[i].getName();
                Object value = Tool.getValue(algorithm, name);
                panel.add(new JLabel("   " + (i+1)));
                panel.add(new JLabel(name));
                // 可修改参数(int, double, string, 前两个是备注)
                if(i >= 2 && (value instanceof Integer || value instanceof Double || value instanceof String)) {
                    JTextField text = new JTextField(value.toString());
                    text.getDocument().addDocumentListener(new DocumentListener(){
                        public void changedUpdate(DocumentEvent e) {
                            change(text, algorithm, name, value);
                        }
                        public void insertUpdate(DocumentEvent e) {
                            change(text, algorithm, name, value);
                        }
                        public void removeUpdate(DocumentEvent e) {
                            change(text, algorithm, name, value);
                        }
                    });
                    panel.add(text);
                } else {
                    panel.add(new JLabel(value != null ? value.toString() : "null"));
                }
            }
            setContentPane(new JScrollPane(panel));
            pack();
            setTitle(algorithm.getName() + "参数配置");
            setLocationRelativeTo(getRootPane());
            setModal(true); // 模态的对话框: 不能点击背景, 只能在对话框上操作(需写于setVisible之前)
            setVisible(true);
        }
        // 参数更改后触发的内容
        private void change(JTextField text, Algorithm algorithm, String name, Object value) {
            String s = text.getText().trim();        // trim()方法用于去掉你可能误输入的空格号
            if(value instanceof Integer)     Tool.setValue(algorithm, name, Integer.parseInt(s));
            else if(value instanceof Double) Tool.setValue(algorithm, name, Double.parseDouble(s));
            else                             Tool.setValue(algorithm, name, s);
            System.out.println(algorithm.getName() + "参数 " + name + ": " + value + " -> " + s);
            gui.progress();
        }
    }


    // getter & setter
    public TableCellEditor getCellEditor() {
        return new CellEditor();
    }
    public TableCellRenderer getCellRender() {
        return new CellRenderer();
    }

    public List<Algorithm> getAlgorithmList() {
        return algorithmList;
    }
    public void setAlgorithmList(List<Algorithm> algorithmList) {
        this.algorithmList = algorithmList;
    }

    public SolutionGUI getGui() {
        return gui;
    }
    public void setGui(SolutionGUI gui) {
        this.gui = gui;
    }

/* class ends */
}
