/*
 * DSWorkbenchTroopsFrame.java
 *
 * Created on 2. Oktober 2008, 13:34
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.models.CurrentTribeVillagesModel;
import de.tor.tribes.ui.models.TroopsManagerTableModel;
import de.tor.tribes.util.Constants;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.Logger;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.TroopsManagerListener;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import de.tor.tribes.ui.renderer.NumberFormatCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *@TODO (DIFF) Allow to add troops for single villages
 * @author  Jejkal
 */
public class DSWorkbenchTroopsFrame extends AbstractDSWorkbenchFrame implements TroopsManagerListener {

    private static Logger logger = Logger.getLogger("TroopsDialog");
    private static DSWorkbenchTroopsFrame SINGLETON = null;
    private List<DefaultTableCellRenderer> renderers = new LinkedList<DefaultTableCellRenderer>();
    private List<ImageIcon> mPowerIcons = new LinkedList<ImageIcon>();

    public static synchronized DSWorkbenchTroopsFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchTroopsFrame();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchTroopsFrame */
    DSWorkbenchTroopsFrame() {
        initComponents();

        getContentPane().setBackground(Constants.DS_BACK);

        try {
            jTroopsInformationAlwaysOnTop.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("troops.frame.alwaysOnTop")));
            setAlwaysOnTop(jTroopsInformationAlwaysOnTop.isSelected());
        } catch (Exception e) {
            //setting not available
        }
        //color scrollpanes of selection dialog
        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jTroopsTable.setColumnSelectionAllowed(false);
        jTroopsTable.setModel(TroopsManagerTableModel.getSingleton());
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(TroopsManagerTableModel.getSingleton());
        /*sorter.setSortsOnUpdates(false);
        sorter.setMaxSortKeys(2);*/
        jTroopsTable.setRowSorter(sorter);
        jTroopsTable.setDefaultRenderer(Integer.class, new NumberFormatCellRenderer());
        jTroopsTable.setDefaultRenderer(Double.class, new NumberFormatCellRenderer());

        jTroopsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selected = jTroopsTable.getSelectedRows().length;
                if (selected == 0) {
                    setTitle("Truppen");
                } else if (selected == 1) {
                    setTitle("Truppen (1 Dorf ausgewählt)");
                } else if (selected > 1) {
                    setTitle("Truppen (" + selected + " Dörfer ausgewählt)");
                }
            }
        });

        try {
            mPowerIcons.add(new ImageIcon("graphics/icons/att.png"));
            mPowerIcons.add(new ImageIcon("graphics/icons/def.png"));
            mPowerIcons.add(new ImageIcon("graphics/icons/def_cav.png"));
            mPowerIcons.add(new ImageIcon("graphics/icons/def_archer.png"));
        } catch (Exception e) {
            logger.error("Failed to read table header icons", e);
        }
        jAddTroopsDialog.pack();

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.troops_view", GlobalOptions.getHelpBroker().getHelpSet());
// </editor-fold>
        pack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAddTroopsDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jVillageBox = new javax.swing.JComboBox();
        jAddButton = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTroopsInformationAlwaysOnTop = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTroopsTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jAddTroopsDialog.setTitle("Dorf  hinzufügen");
        jAddTroopsDialog.setAlwaysOnTop(true);
        jAddTroopsDialog.setModal(true);

        jLabel1.setText("Dorf");

        jAddButton.setText("Hinzufügen");
        jAddButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTroopAddActionEvent(evt);
            }
        });

        jButton4.setText("Schließen");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTroopAddActionEvent(evt);
            }
        });

        javax.swing.GroupLayout jAddTroopsDialogLayout = new javax.swing.GroupLayout(jAddTroopsDialog.getContentPane());
        jAddTroopsDialog.getContentPane().setLayout(jAddTroopsDialogLayout);
        jAddTroopsDialogLayout.setHorizontalGroup(
            jAddTroopsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddTroopsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jVillageBox, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAddTroopsDialogLayout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAddButton)
                .addContainerGap())
        );
        jAddTroopsDialogLayout.setVerticalGroup(
            jAddTroopsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddTroopsDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAddTroopsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jVillageBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jAddTroopsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAddButton)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setTitle("Truppen");

        jTroopsInformationAlwaysOnTop.setText("Immer im Vordergrund");
        jTroopsInformationAlwaysOnTop.setOpaque(false);
        jTroopsInformationAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireTroopsFrameOnTopEvent(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jTroopsTable.setBackground(new java.awt.Color(236, 233, 216));
        jTroopsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTroopsTable.setOpaque(false);
        jScrollPane1.setViewportView(jTroopsTable);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_remove.png"))); // NOI18N
        jButton1.setToolTipText("Gewählte Truppeninformationen entfernen");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveTroopsEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton2.setToolTipText("Neues Dorf ohne Truppeninformationen hinzufügen");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTroopsEvent(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(239, 235, 223));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/center.png"))); // NOI18N
        jButton3.setToolTipText("Gewähltes Dorf auf der Karte zentrieren");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterSelectionEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTroopsInformationAlwaysOnTop)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTroopsInformationAlwaysOnTop)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireTroopsFrameOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireTroopsFrameOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireTroopsFrameOnTopEvent

private void fireRemoveTroopsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveTroopsEvent
    int[] rows = jTroopsTable.getSelectedRows();
    if (rows.length == 0) {
        return;
    }

    String message = ((rows.length == 1) ? "Eintrag " : (rows.length + " Einträge ")) + "wirklich löschen?";
    UIManager.put("OptionPane.noButtonText", "Nein");
    UIManager.put("OptionPane.yesButtonText", "Ja");
    int res = JOptionPane.showConfirmDialog(this, message, "Truppeninformationen entfernen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    UIManager.put("OptionPane.noButtonText", "No");
    UIManager.put("OptionPane.yesButtonText", "Yes");
    if (res != JOptionPane.YES_OPTION) {
        return;
    }

    jTroopsTable.editingCanceled(new ChangeEvent(this));

    for (int r = rows.length - 1; r >= 0; r--) {
        jTroopsTable.invalidate();
        int row = jTroopsTable.convertRowIndexToModel(rows[r]);
        TroopsManagerTableModel.getSingleton().removeRow(row);
        jTroopsTable.revalidate();
    }
    jTroopsTable.repaint();//.updateUI();
}//GEN-LAST:event_fireRemoveTroopsEvent

private void fireAddTroopsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTroopsEvent
    jVillageBox.setModel(CurrentTribeVillagesModel.getModel());
    jAddTroopsDialog.setLocationRelativeTo(this);
    jAddTroopsDialog.setVisible(true);
}//GEN-LAST:event_fireAddTroopsEvent

private void fireTroopAddActionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireTroopAddActionEvent
    if (evt.getSource() == jAddButton) {
        try {
            Village v = (Village) jVillageBox.getSelectedItem();
            if (v != null) {
                int units = DataHolder.getSingleton().getUnits().size();
                List<Integer> emptyUnitList = new LinkedList<Integer>();
                for (int i = 0; i < units; i++) {
                    emptyUnitList.add(0);
                }
                if (TroopsManager.getSingleton().getTroopsForVillage(v) == null) {
                    TroopsManager.getSingleton().addTroopsForVillage(v, emptyUnitList);
                    JOptionPane.showMessageDialog(jAddTroopsDialog, "Truppen hinzugefügt", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(jAddTroopsDialog, "Für das gewählte Dorf sind bereits Truppeninformationen vorhanden.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to add empty troop list", e);
            JOptionPane.showMessageDialog(jAddTroopsDialog, "Fehler beim hinzufügen der Truppen", "Warnung", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        jAddTroopsDialog.setVisible(false);
    }
}//GEN-LAST:event_fireTroopAddActionEvent

private void fireCenterSelectionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterSelectionEvent
    try {
        int row = jTroopsTable.convertRowIndexToModel(jTroopsTable.getSelectedRow());
        Village v = (Village) jTroopsTable.getValueAt(row, 1);
        DSWorkbenchMainFrame.getSingleton().centerVillage(v);
    } catch (Exception e) {
        logger.error("Failed to center village", e);
    }
}//GEN-LAST:event_fireCenterSelectionEvent

    protected void setupTroopsPanel() {
        jTroopsTable.invalidate();
        jTroopsTable.setModel(new DefaultTableModel());
        jTroopsTable.revalidate();

        jTroopsTable.setModel(TroopsManagerTableModel.getSingleton());
        TroopsManager.getSingleton().addTroopsManagerListener(this);
        //setup renderer and general view
        jTroopsTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                int unitCount = DataHolder.getSingleton().getUnits().size();
                r.setHorizontalAlignment(JLabel.CENTER);
                if (column < 3) {
                    r.setText("<html><b>" + r.getText() + "</b></html>");
                } else if (column < 3 + unitCount) {
                    try {
                        r.setIcon(ImageManager.getUnitIcon(column - 3));
                    } catch (Exception e) {
                        r.setText(DataHolder.getSingleton().getUnits().get(column - 3).getName());
                    }
                } else {
                    if (column == unitCount + 3) {
                        //off col
                        r.setIcon(mPowerIcons.get(0));
                    } else if (column == unitCount + 4) {
                        //def col
                        r.setIcon(mPowerIcons.get(1));
                    } else if (column == unitCount + 5) {
                        //def cav col
                        r.setIcon(mPowerIcons.get(2));
                    } else {
                        //def archer col
                        r.setIcon(mPowerIcons.get(3));
                    }
                }
                return r;
            }
        };

        for (int i = 0; i < jTroopsTable.getColumnCount(); i++) {
            TableColumn column = jTroopsTable.getColumnModel().getColumn(i);
            column.setHeaderRenderer(headerRenderer);
            if ((i > 2 && i < DataHolder.getSingleton().getUnits().size() + 3)) {
                column.setWidth(60);
                column.setPreferredWidth(60);
            //column.setResizable(false);
            }
            renderers.add(headerRenderer);
        }
        jTroopsTable.revalidate();
        TroopsManager.getSingleton().forceUpdate();
    }

    @Override
    public void fireTroopsChangedEvent() {
        try {
            jTroopsTable.invalidate();
            jTroopsTable.getTableHeader().setReorderingAllowed(false);

            for (int i = 0; i < jTroopsTable.getColumnCount(); i++) {
                jTroopsTable.getColumnModel().getColumn(i).setHeaderRenderer(renderers.get(i));
            }

            jTroopsTable.revalidate();
            jTroopsTable.repaint();//.updateUI();
        } catch (Exception e) {
            logger.error("Failed to update troops table", e);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAddButton;
    private javax.swing.JDialog jAddTroopsDialog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JCheckBox jTroopsInformationAlwaysOnTop;
    private javax.swing.JTable jTroopsTable;
    private javax.swing.JComboBox jVillageBox;
    // End of variables declaration//GEN-END:variables
}
