/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.models;

import de.tor.tribes.types.FightReport;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.util.report.ReportManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author Torridity
 */
public class ReportManagerTableModel extends AbstractTableModel {

    private String sSet = null;
    private Class[] types = new Class[]{FightReport.class, Date.class, Tribe.class, Village.class, Tribe.class, Village.class, Integer.class, Byte.class};
    private String[] colNames = new String[]{"Status", "Gesendet", "Angreifer", "Herkunft", "Verteidiger", "Ziel", "Typ", "Sonstiges"};
    private static Logger logger = Logger.getLogger("ReportTableModel");

    public ReportManagerTableModel(String pSet) {
        sSet = pSet;
    }

    public void setReportSet(String pSet) {
        sSet = pSet;
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public int getRowCount() {
        if (sSet == null) {
            return 0;
        }
        return ReportManager.getSingleton().getAllElements(sSet).size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (sSet == null) {
            return null;
        }
        try {
            FightReport r = (FightReport) ReportManager.getSingleton().getAllElements(sSet).get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return r;
                case 1:
                    return new Date(r.getTimestamp());//new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date(r.getTimestamp()));
                case 2:
                    return r.getAttacker();
                case 3:
                    return r.getSourceVillage();
                case 4:
                    return r.getDefender();
                case 5:
                    return r.getTargetVillage();
                case 6:
                    return r.guessType();
                default:
                    return r;
            }
        } catch (Exception e) {
            return null;
        }
    }
}