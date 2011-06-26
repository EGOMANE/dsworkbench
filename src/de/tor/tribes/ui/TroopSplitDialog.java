/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TroopSplitDialog.java
 *
 * Created on 14.01.2011, 10:01:07
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.renderer.TroopAmountListCellRenderer;
import de.tor.tribes.ui.renderer.TroopSplitListCellRenderer;
import de.tor.tribes.ui.renderer.UnitListCellRenderer;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *@TODO save and load split set
 * @author Jejkal
 */
public class TroopSplitDialog extends javax.swing.JDialog {

    private static Logger logger = Logger.getLogger("TroopSplitDialog");
    private boolean isInitialized = false;
    private Hashtable<UnitHolder, Integer> mSplitAmounts = new Hashtable<UnitHolder, Integer>();
    private List<TroopSplit> mSplits = new LinkedList<TroopSplit>();

    /** Creates new form TroopSplitDialog */
    public TroopSplitDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        jTroopsPerSplitList.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeSplitEnty();
            }
        }, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**Initialize all entries, renderers and reset the entire view*/
    private void initialize() {
        DefaultComboBoxModel unitSelectionModel = new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{}));
        jUnitSelectionBox.setModel(unitSelectionModel);
        jAmountField.setText("0");
        jTroopsPerSplitList.setModel(new DefaultListModel());
        jUnitSelectionBox.setRenderer(new UnitListCellRenderer());
        jTroopsPerSplitList.setCellRenderer(new TroopAmountListCellRenderer());
        jSplitsList.setCellRenderer(new TroopSplitListCellRenderer());
        mSplitAmounts.clear();
        isInitialized = true;
    }

    /**Insert the provided village list and show the split dialog*/
    public void setupAndShow(List<Village> pVillageList) {
        if (!isInitialized) {
            initialize();
        }
        mSplits.clear();
        for (Village v : pVillageList) {
            mSplits.add(new TroopSplit(v));
        }
        updateSplitsList();
        setVisible(true);
    }

    public TroopSplit[] getSplits() {
        return mSplits.toArray(new TroopSplit[]{});
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSplitsList = new javax.swing.JList();
        jUnitSelectionBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTroopsPerSplitList = new javax.swing.JList();
        jAmountField = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jToleranceSlider = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        capabilityInfoPanel3 = new de.tor.tribes.ui.CapabilityInfoPanel();
        jButton2 = new javax.swing.JButton();
        jAcceptButton = new javax.swing.JButton();

        setTitle("Truppen aufsplitten");
        setAlwaysOnTop(true);

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultierende Splits"));

        jScrollPane1.setViewportView(jSplitsList);

        jUnitSelectionBox.setMaximumSize(new java.awt.Dimension(100, 25));
        jUnitSelectionBox.setMinimumSize(new java.awt.Dimension(100, 25));
        jUnitSelectionBox.setPreferredSize(new java.awt.Dimension(100, 25));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Truppen pro Split"));

        jScrollPane2.setViewportView(jTroopsPerSplitList);

        jAmountField.setText("700");
        jAmountField.setMaximumSize(new java.awt.Dimension(50, 25));
        jAmountField.setMinimumSize(new java.awt.Dimension(50, 25));
        jAmountField.setPreferredSize(new java.awt.Dimension(50, 25));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton3.setToolTipText("Truppenanzahl hinzufügen");
        jButton3.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton3.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton3.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddSplitAmountEvent(evt);
            }
        });

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Zulässige Abweichung");
        jLabel2.setMaximumSize(new java.awt.Dimension(105, 45));
        jLabel2.setMinimumSize(new java.awt.Dimension(105, 45));
        jLabel2.setPreferredSize(new java.awt.Dimension(105, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel2, gridBagConstraints);

        jToleranceSlider.setMajorTickSpacing(10);
        jToleranceSlider.setMaximum(50);
        jToleranceSlider.setMinorTickSpacing(1);
        jToleranceSlider.setPaintLabels(true);
        jToleranceSlider.setPaintTicks(true);
        jToleranceSlider.setValue(10);
        jToleranceSlider.setMaximumSize(new java.awt.Dimension(200, 45));
        jToleranceSlider.setMinimumSize(new java.awt.Dimension(100, 45));
        jToleranceSlider.setOpaque(false);
        jToleranceSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireToleranceChangedEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel2.add(jToleranceSlider, gridBagConstraints);

        jLabel3.setText("%");
        jLabel3.setMaximumSize(new java.awt.Dimension(11, 45));
        jLabel3.setMinimumSize(new java.awt.Dimension(11, 45));
        jLabel3.setPreferredSize(new java.awt.Dimension(11, 45));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jLabel3, gridBagConstraints);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                        .addComponent(jAmountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jUnitSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jUnitSelectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jAmountField, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        capabilityInfoPanel3.setBbSupport(false);
        capabilityInfoPanel3.setCopyable(false);
        capabilityInfoPanel3.setPastable(false);
        capabilityInfoPanel3.setSearchable(false);

        jButton2.setText("Abbrechen");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSubmitEvent(evt);
            }
        });

        jAcceptButton.setText("Anwenden");
        jAcceptButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSubmitEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(capabilityInfoPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAcceptButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jAcceptButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(capabilityInfoPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireAddSplitAmountEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddSplitAmountEvent
        int amount = 0;
        UnitHolder unit = null;
        try {
            amount = Integer.parseInt(jAmountField.getText());
        } catch (Exception e) {
            JOptionPaneHelper.showWarningBox(this, "Ungültige Truppenzahl", "Fehler");
            return;
        }

        try {
            unit = (UnitHolder) jUnitSelectionBox.getSelectedItem();
            if (unit == null) {
                unit = (UnitHolder) jUnitSelectionBox.getModel().getElementAt(0);
            }
        } catch (Exception e) {
            logger.error("Failed to obtain unit", e);
            JOptionPaneHelper.showWarningBox(this, "Ungültige Einheit", "Fehler");
            return;
        }
        mSplitAmounts.put(unit, amount);
        updateAmountsList();

    }//GEN-LAST:event_fireAddSplitAmountEvent

    private void fireSubmitEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSubmitEvent
        if (evt.getSource() != jAcceptButton) {
            mSplits.clear();
        }

        setVisible(false);
    }//GEN-LAST:event_fireSubmitEvent

    private void fireToleranceChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireToleranceChangedEvent
        updateSplitsList();
    }//GEN-LAST:event_fireToleranceChangedEvent

    private void removeSplitEnty() {
        Object[] selection = jTroopsPerSplitList.getSelectedValues();
        List<UnitHolder> units = new LinkedList<UnitHolder>();
        for (Object o : selection) {
            String unit = ((String) o).split(" ")[1].trim();
            UnitHolder u = DataHolder.getSingleton().getUnitByPlainName(unit);
            if (u != null) {
                units.add(u);
            }
        }
        for (UnitHolder unit : units) {
            mSplitAmounts.remove(unit);
        }
        updateAmountsList();
    }

    /**Update the list of split amounts*/
    private void updateAmountsList() {
        DefaultListModel model = new DefaultListModel();
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            Integer amount = mSplitAmounts.get(unit);
            if (amount != null) {
                model.addElement(amount + " " + unit.getPlainName());
            }
        }
        jTroopsPerSplitList.setModel(model);
        updateSplitsList();
    }

    /**Update all splits and the split list itself*/
    private void updateSplitsList() {
        DefaultListModel model = new DefaultListModel();
        for (TroopSplit split : mSplits) {
            split.update(mSplitAmounts, jToleranceSlider.getValue());
            model.addElement(split);
        }
        jSplitsList.setModel(model);
    }

    /**Internal class for data holding*/
    public static class TroopSplit {

        private Village mVillage = null;
        private int iSplitCount = 1;

        public TroopSplit(Village pVillage) {
            mVillage = pVillage;
        }

        public void update(Hashtable<UnitHolder, Integer> pSplitValues, int pTolerance) {
            if (pSplitValues.isEmpty()) {
                iSplitCount = 1;
                return;
            }
            Enumeration<UnitHolder> unitKeys = pSplitValues.keys();
            int maxSplitCount = -1;
            while (unitKeys.hasMoreElements()) {
                UnitHolder unitKey = unitKeys.nextElement();
                Integer splitAmount = pSplitValues.get(unitKey);
                VillageTroopsHolder ownTroops = TroopsManager.getSingleton().getTroopsForVillage(mVillage, TroopsManager.TROOP_TYPE.OWN);

                if (ownTroops == null) {
                    //do nothing if there are no own troops in the village
                    iSplitCount = 0;
                    return;
                }

                int amountInVillage = ownTroops.getTroopsOfUnitInVillage(unitKey);
                int split = amountInVillage / splitAmount;
                int currentSplitCount = split;
                int rest = amountInVillage - split * splitAmount;
                if (100.0 * (double) rest / (double) splitAmount >= 100.0 - (double) pTolerance) {
                    currentSplitCount++;
                }
                if (maxSplitCount == -1 || (currentSplitCount < maxSplitCount)) {
                    maxSplitCount = currentSplitCount;
                }
            }
            iSplitCount = maxSplitCount;
        }

        public Village getVillage() {
            return mVillage;
        }

        public int getSplitCount() {
            return iSplitCount;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(mVillage.toString()).append(" (").append(iSplitCount).append("x)");
            return builder.toString();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.CapabilityInfoPanel capabilityInfoPanel3;
    private javax.swing.JButton jAcceptButton;
    private javax.swing.JTextField jAmountField;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList jSplitsList;
    private javax.swing.JSlider jToleranceSlider;
    private javax.swing.JList jTroopsPerSplitList;
    private javax.swing.JComboBox jUnitSelectionBox;
    // End of variables declaration//GEN-END:variables
}
