/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.renderer;

import de.tor.tribes.types.Tribe;
import de.tor.tribes.util.Constants;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Jejkal
 */
public class TribeCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JLabel label = (JLabel) c;
        Tribe t = (Tribe) value;
        if (!isSelected) {
           if (row % 2 == 0) {
                label.setBackground(Constants.DS_ROW_B);
            } else {
                label.setBackground(Constants.DS_ROW_A);
            }
        }
        try {
            label.setText(t.toString());
            label.setToolTipText(t.getToolTipText());
        } catch (Exception e) {
            label.setText("Ungültig");
        }
        return label;
    }
}
