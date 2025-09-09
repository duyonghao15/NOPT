package org.nudtopt.api.tool.gui;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.algorithm.operator.Move;
import org.nudtopt.api.algorithm.operator.Operator;
import org.nudtopt.api.algorithm.rule.Deconflict;
import org.nudtopt.api.model.DecisionEntity;
import org.nudtopt.api.model.Solution;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VariableCell {

    protected Solution solution;
    protected Algorithm algorithm;

    // getter & setter
    public Solution getSolution() {
        return solution;
    }
    public void setSolution(Solution solution) {
        this.solution = solution;
    }


    protected JPanel createPanel(Object variable) {
        JLabel label = new JLabel(variable == null ? "—" : variable.toString());
        JPanel panel = new JPanel(new BorderLayout(1, 0));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }


    // Editor(编辑)
    protected class CellEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            // a. variable list
            DecisionEntity entity = solution.getDecisionEntityList().get(row);        // entity
            String name = entity.getDecisionVariableList().get(col - 1);              // name
            Object variable = entity.getDecisionVariable(name);                       // variable
            List optionalVariableList = entity.getOptionalDecisionVariableList(name); // variable list

            // b. box
            JComboBox box = new JComboBox(optionalVariableList.toArray());
            box.setSelectedItem(variable);
            box.setEnabled(entity.isAvailable() || entity.isChangeable());
            if(entity.nullable(name))      box.addItem(null);                         // nullable ?

            // c. action & deconflict
            box.addActionListener(e -> {
                Object newVariable = box.getSelectedItem();
                Deconflict deconflict = new Deconflict();                             // 新建 deconflict 算法
                deconflict.setSolution(solution);
                List<Operator> operatorList = deconflict.run(entity, name, newVariable);      // move & deconflict
                algorithm.getHistoryOperatorList().addAll(operatorList);
            });
            /* function ends */
            return box;
        }
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }


    // Renderer(渲染)
    protected class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            DecisionEntity entity = solution.getDecisionEntityList().get(row);        // entity
            String name = entity.getDecisionVariableList().get(col - 1);              // name
            Object variable = entity.getDecisionVariable(name);                       // variable
            return createPanel(variable);
        }
    }


    // getter & setter
    public TableCellEditor getCellEditor() {
        return new CellEditor();
    }
    public TableCellRenderer getCellRender() {
        return new CellRenderer();
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }


/* class ends */
}
