/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DSWorkbenchConquersFrame.java
 *
 * Created on 23.05.2009, 12:30:59
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.models.ConquersTableModel;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.conquer.ConquerManager;
import de.tor.tribes.util.conquer.ConquerManagerListener;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author Charon
 */
public class DSWorkbenchConquersFrame extends AbstractDSWorkbenchFrame implements ConquerManagerListener {

    private static Logger logger = Logger.getLogger("ConquerView");
    private static DSWorkbenchConquersFrame SINGLETON = null;
    private List<TableCellRenderer> mHeaderRenderers = null;

    DSWorkbenchConquersFrame() {
        initComponents();
        try {
            jConquersFrameAlwaysOnTop.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("conquers.frame.alwaysOnTop")));
            setAlwaysOnTop(jConquersFrameAlwaysOnTop.isSelected());
        } catch (Exception e) {
            //setting not available
        }
        mHeaderRenderers = new LinkedList<TableCellRenderer>();

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };

        jConquersTable.setColumnSelectionAllowed(false);
        jConquersTable.setModel(ConquersTableModel.getSingleton());
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
        jConquersTable.setRowSorter(sorter);

        for (int i = 0; i < 5; i++) {
            mHeaderRenderers.add(headerRenderer);
        }

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.conquers_view", GlobalOptions.getHelpBroker().getHelpSet());
        // </editor-fold>

        pack();
    }

    public static synchronized DSWorkbenchConquersFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchConquersFrame();
        }
        return SINGLETON;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jConquersTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLastUpdateLabel = new javax.swing.JLabel();
        jGreyConquersLabel = new javax.swing.JLabel();
        jFriendlyConquersLabel = new javax.swing.JLabel();
        jConquersFrameAlwaysOnTop = new javax.swing.JCheckBox();

        setTitle("Eroberungen");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jConquersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jConquersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jConquersTable);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/center.png"))); // NOI18N
        jButton1.setToolTipText("Gewähltes Dorf auf der Karte zentrieren");
        jButton1.setMaximumSize(new java.awt.Dimension(59, 35));
        jButton1.setMinimumSize(new java.awt.Dimension(59, 35));
        jButton1.setPreferredSize(new java.awt.Dimension(59, 35));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterConqueredVillageEvent(evt);
            }
        });

        jLastUpdateLabel.setText("Letzte Aktualisierung:");

        jGreyConquersLabel.setBackground(new java.awt.Color(255, 204, 204));
        jGreyConquersLabel.setText("Grau-Adelungen:");
        jGreyConquersLabel.setOpaque(true);

        jFriendlyConquersLabel.setBackground(new java.awt.Color(0, 255, 255));
        jFriendlyConquersLabel.setText("Aufadelungen:");
        jFriendlyConquersLabel.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jFriendlyConquersLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                            .addComponent(jGreyConquersLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                            .addComponent(jLastUpdateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                        .addGap(79, 79, 79))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLastUpdateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jGreyConquersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFriendlyConquersLabel)
                .addContainerGap())
        );

        jConquersFrameAlwaysOnTop.setText("Immer im Vordergrund");
        jConquersFrameAlwaysOnTop.setOpaque(false);
        jConquersFrameAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireConquersFrameAlwaysOnTopEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jConquersFrameAlwaysOnTop)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jConquersFrameAlwaysOnTop)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireConquersFrameAlwaysOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireConquersFrameAlwaysOnTopEvent
        setAlwaysOnTop(!isAlwaysOnTop());
    }//GEN-LAST:event_fireConquersFrameAlwaysOnTopEvent

    private void fireCenterConqueredVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterConqueredVillageEvent
        int[] rows = jConquersTable.getSelectedRows();
        if (rows.length != 1) {
            return;
        }
        int row = jConquersTable.convertRowIndexToModel(rows[0]);
        Village v = ((Village) ConquersTableModel.getSingleton().getValueAt(row, 0));
        DSWorkbenchMainFrame.getSingleton().centerVillage(v);
    }//GEN-LAST:event_fireCenterConqueredVillageEvent

    protected void setupConquersPanel() {
        jConquersTable.invalidate();
        jConquersTable.setModel(new DefaultTableModel());
        jConquersTable.revalidate();

        jConquersTable.setModel(ConquersTableModel.getSingleton());
        ConquerManager.getSingleton().addConquerManagerListener(this);
        jConquersTable.getTableHeader().setReorderingAllowed(false);

        //setup renderer and general view
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jConquersTable.getModel());
        jConquersTable.setRowSorter(sorter);
        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        //update view
        for (int i = 0; i < jConquersTable.getColumnCount(); i++) {
            TableColumn column = jConquersTable.getColumnModel().getColumn(i);
            column.setHeaderRenderer(mHeaderRenderers.get(i));
        }
        jConquersTable.revalidate();
        ConquerManager.getSingleton().conquersUpdatedExternally();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DSWorkbenchConquersFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jConquersFrameAlwaysOnTop;
    private javax.swing.JTable jConquersTable;
    private javax.swing.JLabel jFriendlyConquersLabel;
    private javax.swing.JLabel jGreyConquersLabel;
    private javax.swing.JLabel jLastUpdateLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fireConquersChangedEvent() {
        jConquersTable.invalidate();
        jConquersTable.setModel(ConquersTableModel.getSingleton());
        jConquersTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                try {
                    Tribe loser = (Tribe) table.getValueAt(row, 2);
                    if (loser.getId() == 0) {
                        c.setBackground(Color.PINK);
                    } else {
                        Tribe winner = (Tribe) table.getValueAt(row, 3);
                        if (loser != null && winner != null) {
                            if (loser.getId() == winner.getId()) {
                                c.setBackground(Color.GREEN);
                            } else if (loser.getAllyID() == winner.getAllyID()) {
                                c.setBackground(Color.CYAN);
                            } else {
                                Ally loserAlly = loser.getAlly();
                                Ally winnerAlly = winner.getAlly();
                                if (loserAlly != null && winnerAlly != null) {
                                    String lAllyName = loserAlly.getName().toLowerCase();
                                    String wAllyName = winnerAlly.getName().toLowerCase();
                                    if (lAllyName.indexOf(wAllyName) > -1 || wAllyName.indexOf(lAllyName) > -1) {
                                        c.setBackground(Color.CYAN);
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                }
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                // r.setText(r.getText());
                return c;
            }
        };

        jConquersTable.setDefaultRenderer(Object.class, renderer);
        jConquersTable.setDefaultRenderer(Integer.class, renderer);
        for (int i = 0; i < jConquersTable.getColumnCount(); i++) {
            TableColumn c = jConquersTable.getColumnModel().getColumn(i);
            c.setHeaderRenderer(mHeaderRenderers.get(i));
        }
        //setup row sorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
        jConquersTable.setRowSorter(sorter);
        sorter.setModel(ConquersTableModel.getSingleton());
        sorter.setComparator(0, Village.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(2, Tribe.CASE_INSENSITIVE_ORDER);
        sorter.setComparator(3, Tribe.CASE_INSENSITIVE_ORDER);
        
        jConquersTable.revalidate();
        jConquersTable.repaint();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ConquerManager.getSingleton().getLastUpdate());
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        jLastUpdateLabel.setText("<html><b>Letzte Aktualisierung:</b> " + f.format(c.getTime()) + "</html>");

        int[] conquerStats = ConquerManager.getSingleton().getConquersStats();
        int conquers = ConquerManager.getSingleton().getConquerCount();
        int percGrey = (int) Math.rint(100.0 * (double) conquerStats[0] / (double) conquers);
        int percFriendly = (int) Math.rint(100.0 * (double) conquerStats[1] / (double) conquers);
        jGreyConquersLabel.setText("<html><b>Grau-Adelungen:</b> " + conquerStats[0] + " von " + conquers + " (" + percGrey + "%)" + "</html>");
        jFriendlyConquersLabel.setText("<html><b>Aufadelungen:</b> " + conquerStats[1] + " von " + conquers + " (" + percFriendly + "%)" + "</html>");

    }
}
