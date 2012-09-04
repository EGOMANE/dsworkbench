/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.renderer;

import de.tor.tribes.ui.components.ColoredProgressBar;
import de.tor.tribes.ui.util.ColorGradientHelper;
import de.tor.tribes.util.Constants;
import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;

/**
 *
 * @author Charon
 */
public class PercentCellRenderer extends DefaultTableRenderer {

    private DefaultTableCellRenderer renderer = null;
    private NumberFormat format = NumberFormat.getInstance();
    private boolean fromString = false;

    public PercentCellRenderer(boolean pFromString) {
        super();
        renderer = new DefaultTableCellRenderer();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        fromString = pFromString;
    }

    public PercentCellRenderer() {
        this(false);
    }

    public PercentCellRenderer(NumberFormat pCustomFormat) {
        this();
        format = pCustomFormat;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ColoredProgressBar p = new ColoredProgressBar(0, 100);
        p.setBackground(c.getBackground());
        p.setStringPainted(true);
        if (!fromString) {
            Float val = (Float) value * 100;
            p.setForeground(ColorGradientHelper.getGradientColor(val, Color.RED, c.getBackground()));
            p.setValue(Math.round(val));
        } else {
            String val = (String) value;
            String[] values = val.split("/");
            int first = Integer.parseInt(values[0]);
            int second = Integer.parseInt(values[1]);
            float perc = (float) first / (float) second * 100;
            p.setForeground(ColorGradientHelper.getGradientColor(perc, Color.RED, c.getBackground()));
            p.setValue(Math.round(perc));
            p.setString(val);
            
        }

        return p;
    }
}
