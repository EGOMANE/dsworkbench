/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MiscSettingsPanel.java
 *
 * Created on 05.05.2009, 15:00:08
 */
package de.tor.tribes.ui.algo;

import de.tor.tribes.util.Constants;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Jejkal
 */
public class MiscSettingsPanel extends javax.swing.JPanel {

    /** Creates new form MiscSettingsPanel */
    public MiscSettingsPanel() {
        initComponents();
        setBackground(Constants.DS_BACK_LIGHT);
    }

    public void reset() {
        jAttacksPerTarget.setText("6");
        jCleanOffsPerEnoblement.setText("3");
    }

    public void setRandomizeEnabled(boolean pValue) {
        jLabel4.setEnabled(pValue);
        jRandomizeBox.setEnabled(pValue);
    }

    public void setCleanOffsEnabled(boolean pValue) {
        jLabel2.setEnabled(pValue);
        jCleanOffsPerEnoblement.setEnabled(pValue);
    }

    public int getMaxAttacksPerVillage() {
        return Integer.parseInt(jAttacksPerTarget.getText());
    }

    public int getCleanOffsPerEnoblement() {
        return Integer.parseInt(jCleanOffsPerEnoblement.getText());
    }

    public boolean isRandomize() {
        return (jRandomizeBox.getSelectedIndex() == 1);
    }

    public boolean validatePanel() {
        boolean result = true;
        UIManager.put("OptionPane.noButtonText", "Nein");
        UIManager.put("OptionPane.yesButtonText", "Ja");

        int maxAttacks = 0;
        //check max. attacks per village
        try {
            maxAttacks = Integer.parseInt(jAttacksPerTarget.getText());
            if (maxAttacks == 0) {
                //invalid value
                throw new Exception();
            }
        } catch (Exception e) {
            if (JOptionPane.showConfirmDialog(this, "Der Wert für die maximale Anzahl der Angriffe pro Ziel ist ungültig.\n" +
                    "Soll der Standardwert (6) verwendet werden?", "Fehlerhafte Eingabe", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                jAttacksPerTarget.setText("6");
                maxAttacks = 6;
            } else {
                result = false;
            }
        }

        if (!jCleanOffsPerEnoblement.isEnabled()) {
            //skip check if not enabled
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.yesButtonText", "Yes");
            return result;
        }
        //check clean offs per enoblement
        try {
            int v = Integer.parseInt(jCleanOffsPerEnoblement.getText());
            if (maxAttacks < v) {
                //more clean offs needed than max attacks allowed
                throw new Exception();
            }
        } catch (Exception e) {
            //no valid number
            int stdClean = 3;
            if (maxAttacks < 3) {
                //set standard clean offs per enoblement to max attacks
                //due to its value is smaller than the standard value
                stdClean = maxAttacks;
            }
            if (JOptionPane.showConfirmDialog(this, "Der Wert für die minimale Anzahl an Clean-Offs pro Adelung ist ungültig.\n" +
                    "Soll der Standardwert (" + stdClean + ") verwendet werden?", "Fehlerhafte Eingabe", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                jCleanOffsPerEnoblement.setText(Integer.toString(stdClean));
            } else {
                result = false;
            }
        }
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.yesButtonText", "Yes");
        return result;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jAttacksPerTarget = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jCleanOffsPerEnoblement = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jRandomizeBox = new javax.swing.JComboBox();

        jLabel1.setText("Wieviele Angriffe sollen einem einzelnen Zieldorf maximal zugeordnet werden?");
        jLabel1.setMaximumSize(new java.awt.Dimension(374, 14));
        jLabel1.setMinimumSize(new java.awt.Dimension(374, 14));
        jLabel1.setPreferredSize(new java.awt.Dimension(374, 14));

        jAttacksPerTarget.setMaximumSize(new java.awt.Dimension(120, 20));
        jAttacksPerTarget.setMinimumSize(new java.awt.Dimension(120, 20));
        jAttacksPerTarget.setPreferredSize(new java.awt.Dimension(120, 20));

        jLabel2.setText("Wieviele Clean-Offs sollen vor einer möglichen Adelung im Zieldorf eintreffen? ");
        jLabel2.setEnabled(false);

        jCleanOffsPerEnoblement.setEnabled(false);
        jCleanOffsPerEnoblement.setMaximumSize(new java.awt.Dimension(120, 20));
        jCleanOffsPerEnoblement.setMinimumSize(new java.awt.Dimension(120, 20));
        jCleanOffsPerEnoblement.setPreferredSize(new java.awt.Dimension(120, 20));

        jLabel4.setText("In welcher Reihenfolge sollen die Angriffe auf die Zieldörfer verteilt werden?");
        jLabel4.setMaximumSize(new java.awt.Dimension(374, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(374, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(374, 14));

        jRandomizeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alphabetisch, entsprechend der Dorfliste", "Zufällig" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jAttacksPerTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCleanOffsPerEnoblement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addContainerGap(63, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRandomizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(97, 97, 97))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAttacksPerTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCleanOffsPerEnoblement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRandomizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(133, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
//
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAttacksPerTarget;
    private javax.swing.JTextField jCleanOffsPerEnoblement;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox jRandomizeBox;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        Font f = new Font("SansSerif", Font.PLAIN, 11);
        UIManager.put("Label.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("ToggleButton.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("CheckBoxMenuItem.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("OptionPane.font", f);
        UIManager.put("Panel.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("PopupMenu.font", f);
        UIManager.put("ProgressBar.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("Tree.font", f);
        UIManager.put("Viewport.font", f);


        //UIManager.put("Panel.background", Constants.DS_BACK);
        UIManager.put("Label.background", Constants.DS_BACK);
        UIManager.put("MenuBar.background", Constants.DS_BACK);
        UIManager.put("ScrollPane.background", Constants.DS_BACK);
        UIManager.put("Button.background", Constants.DS_BACK);
        UIManager.put("TabbedPane.background", Constants.DS_BACK);
        UIManager.put("SplitPane.background", Constants.DS_BACK);
        UIManager.put("Separator.background", Constants.DS_BACK);
        UIManager.put("Menu.background", Constants.DS_BACK);
        UIManager.put("OptionPane.background", Constants.DS_BACK);
        UIManager.put("ToolBar.background", Constants.DS_BACK);

        JFrame fr = new JFrame();
        fr.add(new MiscSettingsPanel());
        fr.pack();
        fr.setVisible(true);
    }
}
