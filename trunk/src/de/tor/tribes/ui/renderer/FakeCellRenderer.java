/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.renderer;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Torridity
 */
public class FakeCellRenderer extends DefaultTableCellRenderer {

    private DefaultTableCellRenderer renderer = null;
    private ImageIcon fakeIcon;
    private ImageIcon noFakeIcon;

    public FakeCellRenderer() {
        super();
        renderer = new DefaultTableCellRenderer();
        try {
            fakeIcon = new ImageIcon(this.getClass().getResource("/res/ui/fake.png"));
            noFakeIcon = new ImageIcon(this.getClass().getResource("/res/ui/no_fake.png"));
        } catch (Exception e) {
            fakeIcon = null;
            noFakeIcon = null;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ((JLabel) c).setText("");
        try {
            boolean v = (Boolean) value;
            ((JLabel) c).setIcon(((v) ? fakeIcon : noFakeIcon));
            ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
        }
        return c;
    }
}
