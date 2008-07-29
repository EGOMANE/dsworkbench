/*
 * AllyAllyAttackFrame.java
 *
 * Created on 29. Juli 2008, 11:17
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  Jejkal
 */
public class AllyAllyAttackFrame extends javax.swing.JFrame {

    private DSWorkbenchMainFrame mParent = null;

    /** Creates new form AllyAllyAttackFrame */
    public AllyAllyAttackFrame(DSWorkbenchMainFrame pParent) {
        initComponents();
        mParent = pParent;
        DefaultTableModel attackModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Stamm", "Spieler", "Dorf", "Einheit", "Zeitrahmen"
                }) {

            Class[] types = new Class[]{
                Ally.class, Tribe.class, Village.class, UnitHolder.class, String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        jAttacksTable.setModel(attackModel);

        try {
            Enumeration<Integer> allyKeys = GlobalOptions.getDataHolder().getAllies().keys();
            List<Ally> allies = new LinkedList();
            while (allyKeys.hasMoreElements()) {
                allies.add(GlobalOptions.getDataHolder().getAllies().get(allyKeys.nextElement()));
            }

            Ally[] aAllies = allies.toArray(new Ally[]{});
            allies = null;
            Arrays.sort(aAllies);
            DefaultComboBoxModel sourceAllyModel = new DefaultComboBoxModel(aAllies);
            DefaultComboBoxModel targetAllyModel = new DefaultComboBoxModel(aAllies);
            jSourceAllyList.setModel(sourceAllyModel);
            jTargetAllyList.setModel(targetAllyModel);
            jSourceAllyList.setSelectedIndex(0);
            jTargetAllyList.setSelectedIndex(0);
            jArriveTime.setValue(Calendar.getInstance().getTime());
            DefaultComboBoxModel unitModel = new DefaultComboBoxModel();
            for (UnitHolder u : GlobalOptions.getDataHolder().getUnits()) {
                unitModel.addElement(u);
            }
            jTroopsList.setModel(unitModel);
            jResultFrame.pack();
        } catch (Exception e) {
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jResultFrame = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        jResultsTable = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttacksTable = new javax.swing.JTable();
        jSourceAllyList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSourceTribeList = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jSourceVillageList = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTargetAllyList = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jArriveTime = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jMaxAttacksPerVillage = new javax.swing.JComboBox();
        jAttackPlayerBox = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTargetPlayerList = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTroopsList = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTimeFrame = new javax.swing.JComboBox();

        jResultFrame.setTitle("Angriffsplan");

        jResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jResultsTable);

        jButton4.setText("Schließen");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideResultsEvent(evt);
            }
        });

        jButton5.setText("BB-Codes in Zwischenablage");

        jButton6.setText("In Angriffsplanung übernehmen");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTransferToAttackPlanningEvent(evt);
            }
        });

        javax.swing.GroupLayout jResultFrameLayout = new javax.swing.GroupLayout(jResultFrame.getContentPane());
        jResultFrame.getContentPane().setLayout(jResultFrameLayout);
        jResultFrameLayout.setHorizontalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .addGroup(jResultFrameLayout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jResultFrameLayout.setVerticalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        jAttacksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAttacksTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jAttacksTable);

        jSourceAllyList.setMaximumSize(new java.awt.Dimension(150, 20));
        jSourceAllyList.setMinimumSize(new java.awt.Dimension(150, 20));
        jSourceAllyList.setPreferredSize(new java.awt.Dimension(150, 20));
        jSourceAllyList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireSourceAllyChangedEvent(evt);
            }
        });

        jLabel1.setText("Stamm");

        jLabel2.setText("Spieler");

        jSourceTribeList.setMaximumSize(new java.awt.Dimension(150, 20));
        jSourceTribeList.setMinimumSize(new java.awt.Dimension(150, 20));
        jSourceTribeList.setPreferredSize(new java.awt.Dimension(150, 20));
        jSourceTribeList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireSourceTribeChangedEvent(evt);
            }
        });

        jLabel3.setText("Dorf");

        jSourceVillageList.setMaximumSize(new java.awt.Dimension(150, 20));
        jSourceVillageList.setMinimumSize(new java.awt.Dimension(150, 20));
        jSourceVillageList.setPreferredSize(new java.awt.Dimension(150, 20));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(20, 20));
        jButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAttackEvent(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Einstellungen"));

        jTargetAllyList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTargetAllyList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTargetAllyList.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel4.setText("Ziel-Stamm");

        jArriveTime.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), new java.util.Date(), null, java.util.Calendar.SECOND));
        jArriveTime.setMaximumSize(new java.awt.Dimension(150, 20));
        jArriveTime.setMinimumSize(new java.awt.Dimension(150, 20));
        jArriveTime.setPreferredSize(new java.awt.Dimension(150, 20));

        jLabel5.setText("Ankunftzeit");

        jLabel8.setText("Max. Angriffe/Dorf");

        jMaxAttacksPerVillage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        jAttackPlayerBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireAttackTribeSelectionChanged(evt);
            }
        });

        jLabel6.setText("Nur Einzelspieler");

        jLabel10.setText("Ziel-Spieler");

        jTargetPlayerList.setEnabled(false);
        jTargetPlayerList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTargetPlayerList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTargetPlayerList.setPreferredSize(new java.awt.Dimension(150, 20));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTargetAllyList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jArriveTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jAttackPlayerBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMaxAttacksPerVillage, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jTargetPlayerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTargetPlayerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTargetAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jAttackPlayerBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jArriveTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMaxAttacksPerVillage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton2.setText("Berechnen");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCalculateAttackEvent(evt);
            }
        });

        jLabel7.setText("Truppen");

        jTroopsList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTroopsList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTroopsList.setPreferredSize(new java.awt.Dimension(150, 20));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton3.setMaximumSize(new java.awt.Dimension(20, 20));
        jButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveAttackEvent(evt);
            }
        });

        jLabel9.setText("Zeitrahmen");

        jTimeFrame.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "egal", "Früh (6-8)", "Vormittag (8-12)", "Nachmittag (12-18)", "Abend (18-0)", "Nacht (0-6)" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSourceAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSourceVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTroopsList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSourceTribeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jSourceAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jSourceTribeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jSourceVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(jTroopsList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireAddAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAttackEvent
    Ally aSource = (Ally) jSourceAllyList.getSelectedItem();
    Tribe tSource = (Tribe) jSourceTribeList.getSelectedItem();
    Village vSource = (Village) jSourceVillageList.getSelectedItem();
    UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
    String timeFrame = (String) jTimeFrame.getSelectedItem();
    ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{aSource, tSource, vSource, uSource, timeFrame});
}//GEN-LAST:event_fireAddAttackEvent

private void fireRemoveAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveAttackEvent
    int[] rows = jAttacksTable.getSelectedRows();
    if ((rows != null) && (rows.length > 0)) {
        for (int i = rows.length - 1; i >= 0; i--) {
            int row = rows[i];
            ((DefaultTableModel) jAttacksTable.getModel()).removeRow(row);
        }
    }
}//GEN-LAST:event_fireRemoveAttackEvent

private void fireSourceAllyChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireSourceAllyChangedEvent
    Ally aSource = (Ally) jSourceAllyList.getSelectedItem();
    DefaultComboBoxModel model = new DefaultComboBoxModel(aSource.getTribes().toArray(new Tribe[]{}));
    jSourceTribeList.setModel(model);
    jSourceTribeList.setSelectedIndex(0);
}//GEN-LAST:event_fireSourceAllyChangedEvent

private void fireSourceTribeChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireSourceTribeChangedEvent
    Tribe tSource = (Tribe) jSourceTribeList.getSelectedItem();
    DefaultComboBoxModel model = new DefaultComboBoxModel(tSource.getVillageList().toArray(new Village[]{}));
    jSourceVillageList.setModel(model);
    jSourceVillageList.setSelectedIndex(0);
}//GEN-LAST:event_fireSourceTribeChangedEvent

private void fireCalculateAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateAttackEvent

    Hashtable<Village, Hashtable<Village, UnitHolder>> attacks = new Hashtable<Village, Hashtable<Village, UnitHolder>>();
    List<Village> notAssigned = new LinkedList<Village>();

    for (int i = 0; i < jAttacksTable.getRowCount(); i++) {
        Ally aSource = (Ally) jAttacksTable.getValueAt(i, 0);
        Tribe tSource = (Tribe) jAttacksTable.getValueAt(i, 1);
        Village vSource = (Village) jAttacksTable.getValueAt(i, 2);
        UnitHolder uSource = (UnitHolder) jAttacksTable.getValueAt(i, 3);
        String sTimeFrame = (String) jAttacksTable.getValueAt(i, 4);
        Ally aTarget = (Ally) jTargetAllyList.getSelectedItem();

        long arrive = ((Date) jArriveTime.getValue()).getTime();
        int maxAttacks = jMaxAttacksPerVillage.getSelectedIndex() + 1;
        int timeFrame = ((DefaultComboBoxModel) jTimeFrame.getModel()).getIndexOf(sTimeFrame);
        Village vTarget = null;

        boolean singlePlayer = jAttackPlayerBox.isSelected();

        List<Tribe> tribes = null;
        if (singlePlayer) {
            tribes = new LinkedList<Tribe>();
            tribes.add((Tribe) jTargetPlayerList.getSelectedItem());
        } else {
            tribes = aTarget.getTribes();
        }

        for (Tribe t : tribes) {
            for (Village v : t.getVillageList()) {
                double time = DSCalculator.calculateMoveTimeInSeconds(vSource, v, uSource.getSpeed());
                long sendTime = arrive - (long) time * 1000;
                if (sendTime > System.currentTimeMillis()) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(sendTime);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    boolean inTimeFrame = false;
                    switch (timeFrame) {
                        case 1: {
                            //6 to 8
                            if ((hour > 6) && (hour < 8)) {
                                inTimeFrame = true;
                            }
                            break;
                        }
                        case 2: {
                            //8 to 12
                            if ((hour > 8) && (hour < 12)) {
                                inTimeFrame = true;
                            }
                            break;
                        }
                        case 3: {
                            //12 to 18
                            if ((hour > 12) && (hour < 18)) {
                                inTimeFrame = true;
                            }
                            break;
                        }
                        case 4: {
                            //18 to 0
                            if ((hour > 18) && (hour < 24)) {
                                inTimeFrame = true;
                            }
                            break;
                        }
                        case 5: {
                            //0 to 6
                            if ((hour > 0) && (hour < 6)) {
                                inTimeFrame = true;
                            }
                            break;
                        }
                        default: {
                            //doesn't matter
                            inTimeFrame = true;
                        }
                    }
                    if (inTimeFrame) {
                        //only calculate if time is in time frame
                        Hashtable<Village, UnitHolder> attacksForVillage = attacks.get(v);
                        if (attacksForVillage == null) {
                            //create new table of attacks
                            attacksForVillage = new Hashtable<Village, UnitHolder>();
                            attacksForVillage.put(vSource, uSource);
                            attacks.put(v, attacksForVillage);
                            vTarget = v;
                        } else {
                            if (attacksForVillage.keySet().size() < maxAttacks) {
                                //max number of attacks not reached for this village
                                attacksForVillage.put(v, uSource);
                                vTarget = v;
                            } else {
                                //max number of attacks per village reached, continue search
                            }
                        }
                    }
                }
                if (vTarget != null) {
                    break;
                }
            }
            if (vTarget != null) {
                break;
            }
        }
        if (vTarget == null) {
            notAssigned.add(vSource);
        }
    }

    showResults(attacks);

}//GEN-LAST:event_fireCalculateAttackEvent

private void fireHideResultsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideResultsEvent
    jResultFrame.setVisible(false);
}//GEN-LAST:event_fireHideResultsEvent

private void fireTransferToAttackPlanningEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireTransferToAttackPlanningEvent

    DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
    for (int i = 0; i < resultModel.getColumnCount(); i++) {
        Village source = (Village) resultModel.getValueAt(i, 1);
        UnitHolder unit = (UnitHolder) resultModel.getValueAt(i, 2);
        Village target = (Village) resultModel.getValueAt(i, 3);

        mParent.addAttack(source, target, unit, (Date) jArriveTime.getValue());
    }

}//GEN-LAST:event_fireTransferToAttackPlanningEvent

private void fireAttackTribeSelectionChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireAttackTribeSelectionChanged
    if (jAttackPlayerBox.isSelected()) {
        Ally targetAlly = (Ally) jTargetAllyList.getSelectedItem();
        jTargetPlayerList.setModel(new DefaultComboBoxModel(targetAlly.getTribes().toArray(new Tribe[]{})));
        jTargetPlayerList.setSelectedIndex(0);
        jTargetPlayerList.setEnabled(true);
    } else {
        jTargetPlayerList.setModel(new DefaultComboBoxModel());
        jTargetPlayerList.setEnabled(true);
    }
}//GEN-LAST:event_fireAttackTribeSelectionChanged

    private void showResults(Hashtable<Village, Hashtable<Village, UnitHolder>> pAttacks) {
        DefaultTableModel resultModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Spieler", "Dorf", "Truppen", "Ziel", "Startzeit"
                }) {

            Class[] types = new Class[]{
                Tribe.class, Village.class, UnitHolder.class, Village.class, String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        Enumeration<Village> targets = pAttacks.keys();
        while (targets.hasMoreElements()) {
            Village target = targets.nextElement();
            Hashtable<Village, UnitHolder> sources = pAttacks.get(target);
            Enumeration<Village> sourceEnum = sources.keys();
            while (sourceEnum.hasMoreElements()) {
                Village source = sourceEnum.nextElement();
                UnitHolder unit = sources.get(source);
                long targetTime = ((Date) jArriveTime.getValue()).getTime();
                long startTime = targetTime - (long) DSCalculator.calculateMoveTimeInSeconds(source, target, unit.getSpeed()) * 1000;
                String tStart = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date(startTime));
                resultModel.addRow(new Object[]{source.getTribe(), source, unit, target, tStart});
            }

        }
        jResultsTable.setModel(resultModel);
        jResultFrame.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new AllyAllyAttackFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner jArriveTime;
    private javax.swing.JCheckBox jAttackPlayerBox;
    private javax.swing.JTable jAttacksTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox jMaxAttacksPerVillage;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JFrame jResultFrame;
    private javax.swing.JTable jResultsTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JComboBox jSourceAllyList;
    private javax.swing.JComboBox jSourceTribeList;
    private javax.swing.JComboBox jSourceVillageList;
    private javax.swing.JComboBox jTargetAllyList;
    private javax.swing.JComboBox jTargetPlayerList;
    private javax.swing.JComboBox jTimeFrame;
    private javax.swing.JComboBox jTroopsList;
    // End of variables declaration//GEN-END:variables
}