/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.models;

import de.tor.tribes.types.Village;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.dist.DistanceManager;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Charon
 */
public class DistanceTableModel extends AbstractTableModel {

    private static DistanceTableModel SINGLETON = null;

    public static synchronized DistanceTableModel getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DistanceTableModel();
        }
        return SINGLETON;
    }

    DistanceTableModel() {
    }

    public void clear() {
        DistanceManager.getSingleton().clear();
    }

    @Override
    public int getRowCount() {
        return DSWorkbenchMainFrame.getSingleton().getCurrentUser().getVillages();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Village.class;
        }
        return Double.class;
    }

    @Override
    public int getColumnCount() {
        int res = DistanceManager.getSingleton().getVillages().length + 1;
        return res;
    }

    @Override
    public String getColumnName(int col) {
        if (col == 0) {
            return "Eigene";
        }
        return DistanceManager.getSingleton().getVillages()[col - 1].toString();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public Village[] getColumns() {
        return DistanceManager.getSingleton().getVillages();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Village v1 = DSWorkbenchMainFrame.getSingleton().getCurrentUser().getVillageList()[rowIndex];
        if (columnIndex == 0) {
            return v1;
        }
        Village v2 = DistanceManager.getSingleton().getVillages()[columnIndex - 1];
        return DSCalculator.calculateDistance(v1, v2);
    }
}
