/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SupportRefillDialog.java
 *
 * Created on Jun 25, 2011, 4:00:48 PM
 */
package de.tor.tribes.ui.windows;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.wiz.tap.AttackTargetPanel;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ProfileManager;
import de.tor.tribes.util.SplitSetHelper;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.event.ItemEvent;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

/**
 * @author Torridity
 */
public class SupportRefillDialog extends javax.swing.JDialog {

    private static Logger logger = Logger.getLogger("RefillDialog");
    private List<VillageTroopsHolder> mTroopHolders = null;
    private Hashtable<String, Hashtable<UnitHolder, Integer>> splitSets = new Hashtable<String, Hashtable<UnitHolder, Integer>>();

    /** Creates new form SupportRefillDialog */
    public SupportRefillDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        InputVerifier verify = new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                try {
                    Integer.parseInt(((JTextField) input).getText());
                } catch (NumberFormatException nfe) {
                    ((JTextField) input).setText("0");

                }
                return true;
            }
        };
        jSpearAmount.getTextField().setInputVerifier(verify);
        jSwordAmount.getTextField().setInputVerifier(verify);
        jArcherAmount.getTextField().setInputVerifier(verify);
        jSpyAmount.getTextField().setInputVerifier(verify);
        jHeavyAmount.getTextField().setInputVerifier(verify);

        jSpearSplit.getTextField().setInputVerifier(verify);
        jSwordSplit.getTextField().setInputVerifier(verify);
        jArcherSplit.getTextField().setInputVerifier(verify);
        jSpySplit.getTextField().setInputVerifier(verify);
        jHeavySplit.getTextField().setInputVerifier(verify);

    }

    public void setupAndShow(List<VillageTroopsHolder> pTroopHolders) {
        mTroopHolders = pTroopHolders;
        if (DataHolder.getSingleton().getUnitByPlainName("archer") == null) {
            jArcherAmount.setEnabled(false);
            jArcherSplit.setEnabled(false);
            jArcherAmount.setLabelText("0");
            jArcherSplit.setLabelText("0");
        }

        splitSets.clear();
        SplitSetHelper.loadSplitSets(splitSets);

        jSplitSetBox.setModel(new DefaultComboBoxModel(splitSets.keySet().toArray(new String[splitSets.size()])));
        restoreDefaultAmount();
        setVisible(true);
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
        jSpearAmount = new com.jidesoft.swing.LabeledTextField();
        jSwordAmount = new com.jidesoft.swing.LabeledTextField();
        jArcherAmount = new com.jidesoft.swing.LabeledTextField();
        jSpyAmount = new com.jidesoft.swing.LabeledTextField();
        jHeavyAmount = new com.jidesoft.swing.LabeledTextField();
        jConfirmButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jSpearSplit = new com.jidesoft.swing.LabeledTextField();
        jSwordSplit = new com.jidesoft.swing.LabeledTextField();
        jArcherSplit = new com.jidesoft.swing.LabeledTextField();
        jSpySplit = new com.jidesoft.swing.LabeledTextField();
        jHeavySplit = new com.jidesoft.swing.LabeledTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSplitSetBox = new javax.swing.JComboBox();

        setTitle("Unterstützungen auffüllen");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Gesamtanzahl der Unterstützungstruppen"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jSpearAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/spear.png"))); // NOI18N
        jSpearAmount.setLabelText("");
        jSpearAmount.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jSpearAmount, gridBagConstraints);

        jSwordAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/sword.png"))); // NOI18N
        jSwordAmount.setLabelText("");
        jSwordAmount.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jSwordAmount, gridBagConstraints);

        jArcherAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/archer.png"))); // NOI18N
        jArcherAmount.setLabelText("");
        jArcherAmount.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jArcherAmount, gridBagConstraints);

        jSpyAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/spy.png"))); // NOI18N
        jSpyAmount.setLabelText("");
        jSpyAmount.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jSpyAmount, gridBagConstraints);

        jHeavyAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/heavy.png"))); // NOI18N
        jHeavyAmount.setLabelText("");
        jHeavyAmount.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jHeavyAmount, gridBagConstraints);

        jConfirmButton.setText("Übernehmen");
        jConfirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireConfirmEvent(evt);
            }
        });

        jButton2.setText("Abbrechen");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireConfirmEvent(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Truppenzahl eines Unterstützungsbefehls"));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jSpearSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/spear.png"))); // NOI18N
        jSpearSplit.setLabelText("");
        jSpearSplit.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jSpearSplit, gridBagConstraints);

        jSwordSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/sword.png"))); // NOI18N
        jSwordSplit.setLabelText("");
        jSwordSplit.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jSwordSplit, gridBagConstraints);

        jArcherSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/archer.png"))); // NOI18N
        jArcherSplit.setLabelText("");
        jArcherSplit.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jArcherSplit, gridBagConstraints);

        jSpySplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/spy.png"))); // NOI18N
        jSpySplit.setLabelText("");
        jSpySplit.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jSpySplit, gridBagConstraints);

        jHeavySplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/heavy.png"))); // NOI18N
        jHeavySplit.setLabelText("");
        jHeavySplit.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jHeavySplit, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(36, 25));
        jPanel3.setPreferredSize(new java.awt.Dimension(180, 25));
        jPanel3.setLayout(new java.awt.BorderLayout(10, 0));

        jLabel1.setText("SplitSet");
        jPanel3.add(jLabel1, java.awt.BorderLayout.WEST);

        jSplitSetBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireSplitSetSelectionChangedEvent(evt);
            }
        });
        jPanel3.add(jSplitSetBox, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jPanel3, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jConfirmButton))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jConfirmButton)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireConfirmEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireConfirmEvent
        if (evt.getSource() == jConfirmButton) {
            int spearGoal = Integer.parseInt(jSpearAmount.getText());
            int swordGoal = Integer.parseInt(jSwordAmount.getText());
            int archerGoal = Integer.parseInt(jArcherAmount.getText());
            int spyGoal = Integer.parseInt(jSpyAmount.getText());
            int heavyGoal = Integer.parseInt(jHeavyAmount.getText());

            int spearSplit = Integer.parseInt(jSpearSplit.getText());
            int swordSplit = Integer.parseInt(jSwordSplit.getText());
            int archerSplit = Integer.parseInt(jArcherSplit.getText());
            int spySplit = Integer.parseInt(jSpySplit.getText());
            int heavySplit = Integer.parseInt(jHeavySplit.getText());

            for (VillageTroopsHolder holder : mTroopHolders) {
                Village village = holder.getVillage();
                Hashtable<UnitHolder, Integer> troops = holder.getTroops();
                int spearDiff = spearGoal - troops.get(DataHolder.getSingleton().getUnitByPlainName("spear"));
                int swordDiff = swordGoal - troops.get(DataHolder.getSingleton().getUnitByPlainName("sword"));
                int archerDiff = archerGoal - troops.get(DataHolder.getSingleton().getUnitByPlainName("archer"));
                int spyDiff = spyGoal - troops.get(DataHolder.getSingleton().getUnitByPlainName("spy"));
                int heavyDiff = heavyGoal - troops.get(DataHolder.getSingleton().getUnitByPlainName("heavy"));

                int spearSupports = (spearSplit == 0) ? 0 : (int) (Math.ceil((double) spearDiff / (double) spearSplit));
                int swordSupports = (swordSplit == 0) ? 0 : (int) (Math.ceil((double) swordDiff / (double) swordSplit));
                int archerSupports = (archerSplit == 0) ? 0 : (int) (Math.ceil((double) archerDiff / (double) archerSplit));
                int spySupports = (spySplit == 0) ? 0 : (int) (Math.ceil((double) spyDiff / (double) spySplit));
                int heavySupports = (heavySplit == 0) ? 0 : (int) (Math.ceil((double) heavyDiff / (double) heavySplit));

                int supports = Math.max(Math.max(Math.max(Math.max(spearSupports, swordSupports), archerSupports), spySupports), heavySupports);
                if (supports != 0) {
                    //@TODO support refill to DefensePlanner!!!
                   // AttackTargetPanel.getSingleton().addVgetAttackPlaner().fireAddTargetEvent(village, supports);
                }
            }
           /* DSWorkbenchMainFrame.getSingleton().getAttackPlaner().setVisible(true);
            DSWorkbenchMainFrame.getSingleton().getAttackPlaner().toFront();*/
        }
        storeDefaultAmount();
        setVisible(false);
    }//GEN-LAST:event_fireConfirmEvent

    private void fireSplitSetSelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireSplitSetSelectionChangedEvent
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            String selection = (String) jSplitSetBox.getSelectedItem();
            Hashtable<UnitHolder, Integer> split = splitSets.get(selection);

            Integer spear = split.get(DataHolder.getSingleton().getUnitByPlainName("spear"));
            Integer sword = split.get(DataHolder.getSingleton().getUnitByPlainName("sword"));
            Integer archer = split.get(DataHolder.getSingleton().getUnitByPlainName("archer"));
            Integer spy = split.get(DataHolder.getSingleton().getUnitByPlainName("spy"));
            Integer heavy = split.get(DataHolder.getSingleton().getUnitByPlainName("heavy"));

            jSpearSplit.setText((spear == null) ? "0" : spear.toString());
            jSwordSplit.setText((sword == null) ? "0" : sword.toString());
            jArcherSplit.setText((archer == null) ? "0" : archer.toString());
            jSpySplit.setText((spy == null) ? "0" : spy.toString());
            jHeavySplit.setText((heavy == null) ? "0" : heavy.toString());

        }
    }//GEN-LAST:event_fireSplitSetSelectionChangedEvent

    private void storeDefaultAmount() {
        try {
            int spearGoal = Integer.parseInt(jSpearAmount.getText());
            int swordGoal = Integer.parseInt(jSwordAmount.getText());
            int archerGoal = Integer.parseInt(jArcherAmount.getText());
            int spyGoal = Integer.parseInt(jSpyAmount.getText());
            int heavyGoal = Integer.parseInt(jHeavyAmount.getText());
            GlobalOptions.addProperty("support.refill.amount", spearGoal + ":" + swordGoal + ":" + archerGoal + ":" + spyGoal + ":" + heavyGoal);
        } catch (Exception e) {
        }
    }

    private void restoreDefaultAmount() {
        String amounts = GlobalOptions.getProperty("support.refill.amount");
        if (amounts != null) {
            try {
                String[] sAmounts = amounts.split(":");
                jSpearAmount.setText(sAmounts[0]);
                jSwordAmount.setText(sAmounts[1]);
                jArcherAmount.setText(sAmounts[2]);
                jSpyAmount.setText(sAmounts[3]);
                jHeavyAmount.setText(sAmounts[4]);
            } catch (NullPointerException npe) {
                logger.error("Failed to read standard refill values", npe);
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                logger.error("Failed to read standard refill values", aioobe);
            }
        }else{
            logger.info("No standard refill values found");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        Logger.getRootLogger().addAppender(new ConsoleAppender(new org.apache.log4j.PatternLayout("%d - %-5p - %-20c (%C [%L]) - %m%n")));
        GlobalOptions.setSelectedServer("de43");
        ProfileManager.getSingleton().loadProfiles();
        GlobalOptions.setSelectedProfile(ProfileManager.getSingleton().getProfiles("de43")[0]);
        DataHolder.getSingleton().loadData(false);

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                SupportRefillDialog dialog = new SupportRefillDialog(new javax.swing.JFrame(), true);

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setupAndShow(new LinkedList<VillageTroopsHolder>());
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.jidesoft.swing.LabeledTextField jArcherAmount;
    private com.jidesoft.swing.LabeledTextField jArcherSplit;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jConfirmButton;
    private com.jidesoft.swing.LabeledTextField jHeavyAmount;
    private com.jidesoft.swing.LabeledTextField jHeavySplit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private com.jidesoft.swing.LabeledTextField jSpearAmount;
    private com.jidesoft.swing.LabeledTextField jSpearSplit;
    private javax.swing.JComboBox jSplitSetBox;
    private com.jidesoft.swing.LabeledTextField jSpyAmount;
    private com.jidesoft.swing.LabeledTextField jSpySplit;
    private com.jidesoft.swing.LabeledTextField jSwordAmount;
    private com.jidesoft.swing.LabeledTextField jSwordSplit;
    // End of variables declaration//GEN-END:variables
}
