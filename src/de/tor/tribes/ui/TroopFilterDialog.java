/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TroopFilterDialog.java
 *
 * Created on Jun 17, 2011, 6:21:19 PM
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.TroopFilterElement;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.components.CollapseExpandTrigger;
import de.tor.tribes.ui.renderer.UnitListCellRenderer;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.ProfileManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

/**
 * @author Torridity
 */
public class TroopFilterDialog extends javax.swing.JDialog {

    private static Logger logger = Logger.getLogger("TroopFilter");
    private boolean doFilter = false;
    private Hashtable<String, List<TroopFilterElement>> filterSets = new Hashtable<String, List<TroopFilterElement>>();

    /** Creates new form TroopFilterDialog */
    public TroopFilterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();


        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        jFilterList.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedFilters();
            }
        }, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        jList1.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeFilterSet();
            }
        }, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        CollapseExpandTrigger trigger = new CollapseExpandTrigger();
        trigger.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                sourceInfoPanel.setCollapsed(!sourceInfoPanel.isCollapsed());
            }
        });
        jPanel7.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        jPanel7.add(trigger, BorderLayout.CENTER);
        reset();
    }

    private void removeSelectedFilters() {
        Object[] selection = jFilterList.getSelectedValues();
        if (selection == null || selection.length == 0) {
            return;
        }
        List<TroopFilterElement> toRemove = new LinkedList<TroopFilterElement>();
        for (Object elem : selection) {
            toRemove.add((TroopFilterElement) elem);
        }
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
        for (TroopFilterElement elem : toRemove) {
            filterModel.removeElement(elem);
        }
        jFilterList.repaint();
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

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jFilterUnitBox = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jMinValue = new javax.swing.JTextField();
        jMaxValue = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        jFilterList = new javax.swing.JList();
        jStrictFilter = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        sourceInfoPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        capabilityInfoPanel3 = new de.tor.tribes.ui.CapabilityInfoPanel();
        jApplyFiltersButton = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();

        setTitle("Truppenfilter");
        setMinimumSize(new java.awt.Dimension(219, 400));
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));
        jPanel1.setMinimumSize(new java.awt.Dimension(200, 200));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Neuer Filter"));
        jPanel3.setOpaque(false);

        jFilterUnitBox.setMaximumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setMinimumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setPreferredSize(new java.awt.Dimension(51, 25));

        jLabel25.setText("Einheit");

        jLabel26.setText("Min");
        jLabel26.setMaximumSize(new java.awt.Dimension(20, 25));
        jLabel26.setMinimumSize(new java.awt.Dimension(20, 25));
        jLabel26.setPreferredSize(new java.awt.Dimension(20, 25));

        jLabel27.setText("Max");
        jLabel27.setMaximumSize(new java.awt.Dimension(20, 25));
        jLabel27.setMinimumSize(new java.awt.Dimension(20, 25));
        jLabel27.setPreferredSize(new java.awt.Dimension(20, 25));

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton17.setText("Hinzufügen");
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTroopFilterEvent(evt);
            }
        });

        jMinValue.setMaximumSize(new java.awt.Dimension(51, 25));
        jMinValue.setMinimumSize(new java.awt.Dimension(51, 25));
        jMinValue.setPreferredSize(new java.awt.Dimension(51, 25));

        jMaxValue.setMaximumSize(new java.awt.Dimension(51, 25));
        jMaxValue.setMinimumSize(new java.awt.Dimension(51, 25));
        jMaxValue.setPreferredSize(new java.awt.Dimension(51, 25));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jMinValue, 0, 0, Short.MAX_VALUE)
                            .addComponent(jFilterUnitBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton17, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jFilterUnitBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMinValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton17)
                .addContainerGap())
        );

        jScrollPane14.setBorder(javax.swing.BorderFactory.createTitledBorder("Verwendete Filter"));

        jScrollPane14.setViewportView(jFilterList);

        jStrictFilter.setSelected(true);
        jStrictFilter.setText("Strenge Filterung");
        jStrictFilter.setToolTipText("<html>Alle Filterbedingungen müssen erf&uuml;llt sein, damit ein Dorf zugelassen wird.<br/>\nIst dieses Feld deaktiviert reicht mindestens eine Bedingung.</html>");
        jStrictFilter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jStrictFilter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jStrictFilter.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jStrictFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jStrictFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel7.setMaximumSize(new java.awt.Dimension(32767, 20));
        jPanel7.setMinimumSize(new java.awt.Dimension(20, 20));
        jPanel7.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel7.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel7, java.awt.BorderLayout.EAST);

        jPanel4.add(jPanel2, java.awt.BorderLayout.CENTER);

        sourceInfoPanel.setAnimated(false);
        sourceInfoPanel.setCollapsed(true);
        sourceInfoPanel.setDirection(org.jdesktop.swingx.JXCollapsiblePane.Direction.LEFT);
        sourceInfoPanel.setInheritAlpha(false);

        jPanel5.setMinimumSize(new java.awt.Dimension(190, 360));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/checkbox.png"))); // NOI18N
        jButton1.setToolTipText("Filtersatz speichern");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireSaveFilterSetEvent(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jButton2.setToolTipText("Filtersatz laden");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireLoadFilterSetEvent(evt);
            }
        });

        jTextField1.setMinimumSize(new java.awt.Dimension(59, 25));
        jTextField1.setPreferredSize(new java.awt.Dimension(59, 25));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Gespeicherte Filtersätze"));

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        sourceInfoPanel.add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel4.add(sourceInfoPanel, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel4, gridBagConstraints);

        capabilityInfoPanel3.setBbSupport(false);
        capabilityInfoPanel3.setCopyable(false);
        capabilityInfoPanel3.setPastable(false);
        capabilityInfoPanel3.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        getContentPane().add(capabilityInfoPanel3, gridBagConstraints);

        jApplyFiltersButton.setText("Anwenden");
        jApplyFiltersButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        getContentPane().add(jApplyFiltersButton, gridBagConstraints);

        jButton20.setText("Abbrechen");
        jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        getContentPane().add(jButton20, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireAddTroopFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTroopFilterEvent
        UnitHolder unit = (UnitHolder) jFilterUnitBox.getSelectedItem();
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        try {
            min = Integer.parseInt(jMinValue.getText());
            jMinValue.setBackground(Color.WHITE);
        } catch (Exception e) {
            jMinValue.setBackground(Color.RED);
            return;
        }
        try {
            max = Integer.parseInt(jMaxValue.getText());
            jMaxValue.setBackground(Color.WHITE);
        } catch (Exception e) {
            jMaxValue.setBackground(Color.RED);
            return;
        }
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
            jMinValue.setText("" + min);
            jMaxValue.setText("" + max);
        }
        for (int i = 0; i < filterModel.size(); i++) {
            TroopFilterElement listElem = (TroopFilterElement) filterModel.get(i);
            if (listElem.getUnit().equals(unit)) {
                //update min and max and return
                listElem.setMin(min);
                listElem.setMax(max);
                jFilterList.repaint();
                return;
            }
        }
        //no elem update --> add new elem
        filterModel.addElement(new TroopFilterElement(unit, min, max));

}//GEN-LAST:event_fireAddTroopFilterEvent

    private void fireApplyTroopFiltersEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireApplyTroopFiltersEvent
        if (evt.getSource() == jApplyFiltersButton) {
            doFilter = true;
        }
        setVisible(false);
}//GEN-LAST:event_fireApplyTroopFiltersEvent

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        jTextField1.setText((String) jList1.getSelectedValue());
    }//GEN-LAST:event_jList1ValueChanged

    private void fireSaveFilterSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSaveFilterSetEvent
        String setName = jTextField1.getText();
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();

        if (setName == null || setName.length() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Bitte einen Namen für das neue Filterset angeben", "Information");
            return;
        }

        if (filterModel.getSize() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Ein Filterset muss mindestens einen Eintrag enthalten", "Information");
            return;
        }

        if (filterSets.get(setName) != null) {
            if (JOptionPaneHelper.showQuestionConfirmBox(this, "Das Filterset '" + setName + "' existiert bereits.\nMöchtest du es überschreiben?", "Bestätigung", "Nein", "Ja") != JOptionPane.OK_OPTION) {
                return;
            }
        }

        StringBuilder b = new StringBuilder();
        b.append(setName).append(",");
        List<TroopFilterElement> elements = new LinkedList<TroopFilterElement>();
        for (int j = 0; j < filterModel.size(); j++) {
            TroopFilterElement elem = (TroopFilterElement) filterModel.get(j);
            elements.add(new TroopFilterElement(elem.getUnit(), elem.getMin(), elem.getMax()));
        }

        filterSets.put(setName, elements);
        updateFilterSetList();
    }//GEN-LAST:event_fireSaveFilterSetEvent

    private void fireLoadFilterSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireLoadFilterSetEvent
        String selection = (String) jList1.getSelectedValue();
        if (selection != null) {
            List<TroopFilterElement> elems = filterSets.get(selection);
            DefaultListModel model = new DefaultListModel();
            for (TroopFilterElement elem : elems) {
                model.addElement(new TroopFilterElement(elem.getUnit(), elem.getMin(), elem.getMax()));
            }
            jFilterList.setModel(model);
        }
    }//GEN-LAST:event_fireLoadFilterSetEvent

    private void removeFilterSet() {
        String setName = (String) jList1.getSelectedValue();
        if (setName == null || filterSets.get(setName) == null) {
            return;
        }

        if (JOptionPaneHelper.showQuestionConfirmBox(this, "Möchtest du das Filterset '" + setName + "' wirklich löschen?", "Bestätigung", "Nein", "Ja") != JOptionPane.OK_OPTION) {
            return;
        }

        filterSets.remove(setName);
        updateFilterSetList();
    }

    private void saveFilterSets() {
        String profileDir = GlobalOptions.getSelectedProfile().getProfileDirectory();
        File filterFile = new File(profileDir + "/filters.sav");

        StringBuilder b = new StringBuilder();
        Enumeration<String> setKeys = filterSets.keys();

        while (setKeys.hasMoreElements()) {
            String key = setKeys.nextElement();
            b.append(key).append(",");
            List<TroopFilterElement> elements = filterSets.get(key);
            for (int i = 0; i < elements.size(); i++) {
                TroopFilterElement elem = elements.get(i);
                b.append(elem.getUnit().getPlainName()).append("/").append(elem.getMin()).append("/").append(elem.getMax());
                if (i < elements.size() - 1) {
                    b.append(",");
                }
            }
            b.append("\n");
        }

        FileWriter w = null;
        try {
            w = new FileWriter(filterFile);
            w.write(b.toString());
            w.flush();
        } catch (Exception e) {
            logger.error("Failed to write troop filters", e);
        } finally {
            try {
                w.close();
            } catch (Exception e) {
            }
        }
    }

    private void loadFilterSets() {
        filterSets.clear();
        String profileDir = GlobalOptions.getSelectedProfile().getProfileDirectory();
        File filterFile = new File(profileDir + "/filters.sav");
        if (!filterFile.exists()) {
            return;
        }

        BufferedReader r = null;

        try {
            r = new BufferedReader(new FileReader(filterFile));
            String line = "";
            while ((line = r.readLine()) != null) {
                String[] split = line.split(",");
                String name = split[0];
                List<TroopFilterElement> elements = new LinkedList<TroopFilterElement>();
                for (int i = 1; i < split.length; i++) {
                    String[] elemSplit = split[i].split("/");
                    TroopFilterElement elem = new TroopFilterElement(DataHolder.getSingleton().getUnitByPlainName(elemSplit[0]), Integer.parseInt(elemSplit[1]), Integer.parseInt(elemSplit[2]));
                    elements.add(elem);
                }
                filterSets.put(name, elements);
            }
        } catch (Exception e) {
            logger.error("Failed to read troop filters", e);
        } finally {
            try {
                r.close();
            } catch (Exception ignored) {
            }
        }

        updateFilterSetList();
    }

    private void updateFilterSetList() {
        DefaultListModel model = new DefaultListModel();

        Enumeration<String> keys = filterSets.keys();
        while (keys.hasMoreElements()) {
            model.addElement(keys.nextElement());
        }

        jList1.setModel(model);
    }

    public void reset() {
        jFilterList.setModel(new DefaultListModel());
    }

    public void show(List<Village> pVillageToFilter) {
        jFilterUnitBox.setModel(new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{})));
        jFilterUnitBox.setRenderer(new UnitListCellRenderer());
        //load filter sets
        loadFilterSets();

        doFilter = false;
        pack();
        setVisible(true);
        if (doFilter) {
            //update list if filter is enabled
            DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();

            for (Village v : pVillageToFilter.toArray(new Village[pVillageToFilter.size()])) {
                boolean villageAllowed = false;
                //go through all rows in attack table and get source village
                for (int j = 0; j < filterModel.size(); j++) {
                    //check for all filters if villag is allowed
                    if (!((TroopFilterElement) filterModel.get(j)).allowsVillage(v)) {
                        if (jStrictFilter.isSelected()) {
                            //village is not allowed, add to remove list if strict filtering is enabled
                            pVillageToFilter.remove(v);
                        }
                    } else {
                        villageAllowed = true;
                        if (!jStrictFilter.isSelected()) {
                            break;
                        }
                    }
                }
                if (!jStrictFilter.isSelected()) {
                    //if strict filtering is disabled remove village only if it is not allowed
                    if (!villageAllowed) {
                        pVillageToFilter.remove(v);
                    }
                }
            }
        }
        saveFilterSets();
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

        final TroopFilterDialog dialog = new TroopFilterDialog(null, false);
        dialog.reset();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                dialog.show(new LinkedList<Village>());
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.CapabilityInfoPanel capabilityInfoPanel3;
    private javax.swing.JButton jApplyFiltersButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JList jFilterList;
    private javax.swing.JComboBox jFilterUnitBox;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JList jList1;
    private javax.swing.JTextField jMaxValue;
    private javax.swing.JTextField jMinValue;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JCheckBox jStrictFilter;
    private javax.swing.JTextField jTextField1;
    private org.jdesktop.swingx.JXCollapsiblePane sourceInfoPanel;
    // End of variables declaration//GEN-END:variables
}
