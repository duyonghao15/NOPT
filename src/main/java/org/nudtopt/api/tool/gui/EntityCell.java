package org.nudtopt.api.tool.gui;

import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EntityCell {

    protected Solution solution;

    protected JPanel createPanel(DecisionEntity entity) {
        // a. label
        JLabel label = new JLabel(entity.toString());

        // b. check
        JCheckBox box1 = new JCheckBox("🔒", null, !entity.isChangeable());
        JCheckBox box2 = new JCheckBox("✖", null, !entity.isAvailable());
        box1.setToolTipText("锁定(不再更改决策变量)");
        box2.setToolTipText("禁用/忽略");
        box1.addActionListener(e -> entity.setChangeable(!entity.isChangeable()));
        box2.addActionListener(e -> {
            entity.setAvailable(!entity.isAvailable());
//            solution.reScore();  // 重新计算评分
        });

        JPanel check = new JPanel(new BorderLayout(2, 0));
        check.add(box1, BorderLayout.WEST);
        check.add(box2, BorderLayout.EAST);

        // c. panel
        JPanel panel = new JPanel(new BorderLayout(2, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(check, BorderLayout.EAST);
        return panel;
    }


    // Editor(编辑)
    protected class CellEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            DecisionEntity entity = solution.getDecisionEntityList().get(row);
            return createPanel(entity);
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
            DecisionEntity entity = solution.getDecisionEntityList().get(row);
            return createPanel(entity);
        }
    }


    // getter & setter
    public TableCellEditor getCellEditor() {
        return new CellEditor();
    }
    public TableCellRenderer getCellRender() {
        return new CellRenderer();
    }

    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

/* class ends */
}
