/*
 * AttackAddFrame.java
 *
 * Created on 17. Juni 2008, 14:13
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Village;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  Jejkal
 */
public class AttackAddFrame extends javax.swing.JFrame {

    private Village mSource;
    private Village mTarget;
    private DSWorkbenchMainFrame mParent = null;
    private final NumberFormat nf = NumberFormat.getInstance();
    private boolean skipValidation = false;

    /** Creates new form AttackAddFrame */
    public AttackAddFrame(DSWorkbenchMainFrame pParent) {
        initComponents();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        frameControlPanel1.setupPanel(this, true, false);
        mParent = pParent;
        getContentPane().setBackground(GlobalOptions.DS_BACK);

        ((DateEditor) jTimeSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        ((DateEditor) jTimeSpinner.getEditor()).getFormat().applyPattern("dd.MM.yy HH:mm:ss");
        jTimeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (skipValidation) {
                    return;
                }
                if (!validateTime()) {
                    ((DateEditor) jTimeSpinner.getEditor()).getTextField().setForeground(Color.RED);
                } else {
                    ((DateEditor) jTimeSpinner.getEditor()).getTextField().setForeground(Color.BLACK);
                }
            }
        });
        buildUnitBox();
    }

    /**Check if the currently selected unit can arrive at the target village at the selected time.
     * Returns result depending on the time mode (arrive or send) 
     */
    private boolean validateTime() {
        long sendMillis = ((Date) jTimeSpinner.getValue()).getTime();
        //check time depending selected unit
        int speed = ((UnitHolder) jUnitBox.getSelectedItem()).getSpeed();
        double minTime = DSCalculator.calculateMoveTimeInMinutes(mSource, mTarget, speed);
        long moveTime = (long) minTime * 60000;
        return (sendMillis > System.currentTimeMillis() + moveTime);
    }

    private void buildUnitBox() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (UnitHolder unit : GlobalOptions.getDataHolder().getUnits()) {
            model.addElement(unit);
        }
        jUnitBox.setModel(model);
    }

    public Date getTime() {
        return (Date) jTimeSpinner.getValue();
    }

    public UnitHolder getSelectedUnit() {
        return (UnitHolder) jUnitBox.getSelectedItem();
    }

    public void setupAttack(Village pSource, Village pTarget, int pInitialUnit, Date pInititalTime) {
        if ((pSource == null) || (pTarget == null)) {
            return;
        }
        if (pSource.equals(pTarget)) {
            return;
        }
        if (pSource.getTribe() == null) {
            //empty villages cannot attack
            return;
        }
        skipValidation = true;
        int initialUnit = (pInitialUnit >= 0) ? pInitialUnit : 0;
        if (initialUnit > jUnitBox.getItemCount() - 1) {
            initialUnit = -1;
        }
        jUnitBox.setSelectedIndex(initialUnit);

        if (pInititalTime != null) {
            jTimeSpinner.setValue(pInititalTime);
        } else {
            double dur = DSCalculator.calculateMoveTimeInMinutes(pSource, pTarget, ((UnitHolder) jUnitBox.getSelectedItem()).getSpeed());
            dur = dur * 60000;
            jTimeSpinner.setValue(new Date(System.currentTimeMillis() + (long) dur + 60000));
            ((DateEditor) jTimeSpinner.getEditor()).getTextField().setForeground(Color.BLACK);
        }
        mSource = pSource;
        mTarget = pTarget;
        jSourceVillage.setText(pSource.getTribe() + " (" + pSource + ")");
        if (pTarget.getTribe() != null) {
            jTargetVillage.setText(pTarget.getTribe() + " (" + pTarget + ")");
        } else {
            jTargetVillage.setText("Barbarendorf" + " (" + pTarget.getX() + "|" + pTarget.getY() + ")");
        }
        double d = DSCalculator.calculateDistance(mSource, mTarget);

        jDistance.setText(nf.format(d));
        setVisible(true);
        skipValidation = false;
    }

    public void setupAttack(Village pSource, Village pTarget, int pInitialUnit) {
        setupAttack(pSource, pTarget, pInitialUnit, null);
    }

    public void setupAttack(Village pSource, Village pTarget) {
        setupAttack(pSource, pTarget, -1);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jDistance = new javax.swing.JLabel();
        jSourceVillage = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jUnitBox = new javax.swing.JComboBox();
        jTimeSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jTargetVillage = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        frameControlPanel1 = new de.tor.tribes.ui.FrameControlPanel();

        setTitle("Angriff hinzufügen");
        setUndecorated(true);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setText("OK");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAttackEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setText("Abbrechen");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCancelAddAttackEvent(evt);
            }
        });

        jDistance.setBackground(new java.awt.Color(239, 235, 223));
        jDistance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jDistance.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jDistance.setOpaque(true);

        jSourceVillage.setBackground(new java.awt.Color(239, 235, 223));
        jSourceVillage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jSourceVillage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSourceVillage.setOpaque(true);

        jLabel1.setText("<html><u>Herkunft</u></html>");
        jLabel1.setBorder(javax.swing.BorderFactory.createCompoundBorder());

        jLabel2.setText("<html><u>Ziel</u></html>");

        jLabel3.setText("<html><u>Langsamste Einheit</u></html>");
        jLabel3.setMaximumSize(new java.awt.Dimension(120, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(120, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(120, 14));

        jUnitBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Speerträger (18)", "Adelsgeschlecht (35)", "Berittener Bogenschütze (11 Felder/min)" }));
        jUnitBox.setSelectedIndex(2);
        jUnitBox.setMinimumSize(new java.awt.Dimension(300, 18));
        jUnitBox.setPreferredSize(new java.awt.Dimension(300, 20));
        jUnitBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireUnitChangedEvent(evt);
            }
        });

        jTimeSpinner.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.SECOND));

        jLabel4.setText("<html><u>Entfernung</u></html>");

        jTargetVillage.setBackground(new java.awt.Color(239, 235, 223));
        jTargetVillage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jTargetVillage.setAutoscrolls(true);
        jTargetVillage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTargetVillage.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        jTargetVillage.setMinimumSize(new java.awt.Dimension(39, 20));
        jTargetVillage.setOpaque(true);
        jTargetVillage.setPreferredSize(new java.awt.Dimension(39, 20));

        jLabel5.setText("<html><u>Ankunftzeit</u></html>");
        jLabel5.setMaximumSize(new java.awt.Dimension(120, 14));
        jLabel5.setMinimumSize(new java.awt.Dimension(120, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 14));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTimeSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addComponent(jTargetVillage, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addComponent(jSourceVillage, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addComponent(jDistance, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addComponent(jUnitBox, 0, 306, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
            .addComponent(frameControlPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(frameControlPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSourceVillage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTargetVillage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDistance, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jUnitBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireUnitChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireUnitChangedEvent
    if (evt.getStateChange() == ItemEvent.SELECTED) {
        UnitHolder u = (UnitHolder) evt.getItem();
        long arriveTime = (long) (System.currentTimeMillis() + DSCalculator.calculateMoveTimeInSeconds(mSource, mTarget, u.getSpeed()) * 1000 + 1000);
        if (((Date) jTimeSpinner.getValue()).getTime() < arriveTime) {
            //only set new arrive time if unit could not arrive at the current time
            jTimeSpinner.setValue(new Date(arriveTime));
        }
    }
}//GEN-LAST:event_fireUnitChangedEvent

private void fireCancelAddAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCancelAddAttackEvent
    setVisible(false);
}//GEN-LAST:event_fireCancelAddAttackEvent

private void fireAddAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAttackEvent
    if (!validateTime()) {
        ((DateEditor) jTimeSpinner.getEditor()).getTextField().setForeground(Color.RED);
        return;
    }

    mParent.addAttack(mSource, mTarget, getSelectedUnit(), getTime());

    setVisible(false);
}//GEN-LAST:event_fireAddAttackEvent

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            GlobalOptions.initialize();
            GlobalOptions.setSelectedServer("de26");

            GlobalOptions.loadData(false);
        } catch (Exception e) {
        }
        Village source = GlobalOptions.getDataHolder().getVillages()[452][467];
        Village target = GlobalOptions.getDataHolder().getVillages()[449][466];
        /* for (int i = 0; i < 1000; i++) {
        for (int j = 0; j < 1000; j++) {
        Village v = GlobalOptions.getDataHolder().getVillages()[i][j];
        if (v != null) {
        if (source == null) {
        source = v;
        } else if (target == null) {
        target = v;
        } else {
        break;
        }
        }
        }
        
        }*/

        new AttackAddFrame(null).setupAttack(source, target);

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.FrameControlPanel frameControlPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jDistance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jSourceVillage;
    private javax.swing.JLabel jTargetVillage;
    private javax.swing.JSpinner jTimeSpinner;
    private javax.swing.JComboBox jUnitBox;
    // End of variables declaration//GEN-END:variables
}