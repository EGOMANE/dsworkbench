/*
 * VillageTagFrame.java
 *
 * Created on 20. Juli 2008, 16:35
 */
package de.tor.tribes.ui.windows;

import de.tor.tribes.control.GenericManagerListener;
import de.tor.tribes.control.ManageableType;
import de.tor.tribes.types.ext.Barbarians;
import de.tor.tribes.types.LinkedTag;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.tag.TagManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * @author  Charon
 */
public class VillageTagFrame extends javax.swing.JFrame implements GenericManagerListener {

    private static VillageTagFrame SINGLETON = null;

    public static synchronized VillageTagFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new VillageTagFrame();
            TagManager.getSingleton().addManagerListener(SINGLETON);
        }
        return SINGLETON;
    }

    /** Creates new form VillageTagFrame */
    VillageTagFrame() {
        initComponents();
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        jTagsList.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTags();
            }
        }, "Delete", delete, JComponent.WHEN_FOCUSED);

    }

    public boolean updateUserTags() {
        List<Tag> lTags = new LinkedList<Tag>();

        for (ManageableType e : TagManager.getSingleton().getAllElements()) {
            if (!(e instanceof LinkedTag)) {
                lTags.add((Tag) e);
            }
        }
        if (lTags.isEmpty()) {
            jTagsChooser.setModel(new DefaultComboBoxModel(new String[]{"Keine Gruppen vorhanden"}));
            return false;
        }
        jTagsChooser.setModel(new DefaultComboBoxModel(lTags.toArray(new Tag[lTags.size()])));
        jTagsChooser.repaint();
        return true;
    }

    public void showTagsFrame(Village pVillage) {
        boolean tagsAvailable = updateUserTags();
        jTagsChooser.setEnabled(tagsAvailable);
        jButton1.setEnabled(tagsAvailable);

        Tribe t = pVillage.getTribe();
        if (t == null) {
            return;
        }
        jPlayerName.setText(t.getName());
        Village[] list = t.getVillageList();
        Arrays.sort(list);

        jVillageList.setModel(new DefaultComboBoxModel(list));
        jVillageList.setSelectedItem(pVillage);

        List<Tag> tags = TagManager.getSingleton().getTags(pVillage);

        DefaultListModel lModel = new DefaultListModel();

        for (Tag tag : tags) {
            lModel.addElement(tag);
        }

        jTagsList.setModel(lModel);

        setVisible(true);
    }

    public void showTagsFrame(List<Village> pVillage) {
        boolean tagsAvailable = updateUserTags();
        jTagsChooser.setEnabled(tagsAvailable);
        jButton1.setEnabled(tagsAvailable);

        Village[] list = pVillage.toArray(new Village[]{});
        jPlayerName.setText("Mehrfachauswahl");
        Arrays.sort(list);

        jVillageList.setModel(new DefaultComboBoxModel(list));
        jVillageList.setSelectedItem(pVillage.get(0));
        List<Tag> allTags = new LinkedList<Tag>();
        for (Village v : pVillage) {
            List<Tag> tags = TagManager.getSingleton().getTags(v);
            for (Tag t : tags) {
                if (!allTags.contains(t)) {
                    allTags.add(t);
                }
            }
        }

        DefaultListModel lModel = new DefaultListModel();

        for (Tag tag : allTags) {
            lModel.addElement(tag);
        }

        jTagsList.setModel(lModel);

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

        jTribeLabel = new javax.swing.JLabel();
        jVillageLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTagsList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jPlayerName = new javax.swing.JTextField();
        jTagsChooser = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();
        jVillageList = new javax.swing.JComboBox();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();

        setTitle("In Gruppe einfügen");

        jTribeLabel.setText("Spieler");
        jTribeLabel.setMaximumSize(new java.awt.Dimension(50, 14));
        jTribeLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        jTribeLabel.setPreferredSize(new java.awt.Dimension(50, 14));

        jVillageLabel.setText("Dorf");
        jVillageLabel.setMaximumSize(new java.awt.Dimension(50, 14));
        jVillageLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        jVillageLabel.setPreferredSize(new java.awt.Dimension(50, 14));

        jTagsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jTagsList);

        jLabel3.setText("Gruppen");
        jLabel3.setMaximumSize(new java.awt.Dimension(50, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(50, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(50, 14));

        jPlayerName.setBackground(new java.awt.Color(239, 235, 223));
        jPlayerName.setEditable(false);

        jTagsChooser.setMinimumSize(new java.awt.Dimension(23, 15));
        jTagsChooser.setPreferredSize(new java.awt.Dimension(28, 25));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setToolTipText("Fügt das gewählte Dorf/die gewählten Dörfern in die gewählte Gruppe ein");
        jButton1.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTagEvent(evt);
            }
        });

        jOKButton.setText("OK");
        jOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireOkEvent(evt);
            }
        });

        jCancelButton.setText("Abbrechen");
        jCancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCancelEvent(evt);
            }
        });

        jVillageList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireVillageSelectionChangedEvent(evt);
            }
        });

        capabilityInfoPanel1.setBbSupport(false);
        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setPastable(false);
        capabilityInfoPanel1.setSearchable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTribeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jVillageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(capabilityInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPlayerName, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                    .addComponent(jVillageList, 0, 257, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
                        .addComponent(jCancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jOKButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTagsChooser, 0, 226, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTribeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPlayerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jVillageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTagsChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jOKButton)
                        .addComponent(jCancelButton))
                    .addComponent(capabilityInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireAddTagEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTagEvent
    Tag tag = null;
    try {
        tag = (Tag) jTagsChooser.getSelectedItem();
    } catch (ClassCastException cce) {
        //no tags availabler
        JOptionPaneHelper.showWarningBox(this, "Keine Gruppen vorhanden. Bitte importiere zuerst Gruppen aus dem Spiel oder lege sie in der Gruppen-Ansicht manuell an.", "Warnung");
        return;
    }
    DefaultListModel model = (DefaultListModel) jTagsList.getModel();
    if (jPlayerName.getText().equals("Mehrfachauswahl")) {
        if (tag == null) {
            return;
        }

        for (int i = 0; i < jVillageList.getItemCount(); i++) {
            Village v = (Village) jVillageList.getItemAt(i);
            if (v.getTribe() != Barbarians.getSingleton()) {
                TagManager.getSingleton().addTag(v, tag.getName());
            }
        }
        if (!model.contains(tag)) {
            model.addElement(tag);
        }
    } else {
        Village selection = (Village) jVillageList.getSelectedItem();
        if ((selection == null) || (tag == null)) {
            return;
        }
        if (!model.contains(tag)) {
            model.addElement(tag);
            TagManager.getSingleton().addTag(selection, tag.getName());
        }
    }
}//GEN-LAST:event_fireAddTagEvent

private void fireOkEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireOkEvent
    //init redraw
    TagManager.getSingleton().revalidate(true);
    setVisible(false);
}//GEN-LAST:event_fireOkEvent

private void fireCancelEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCancelEvent
    setVisible(false);
}//GEN-LAST:event_fireCancelEvent

private void fireVillageSelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireVillageSelectionChangedEvent
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        DefaultListModel model = new DefaultListModel();
        Village selection = (Village) jVillageList.getSelectedItem();
        List<Tag> tags = TagManager.getSingleton().getTags(selection);
        for (Tag tag : tags) {
            model.addElement(tag);
        }
        jTagsList.setModel(model);
    }
}//GEN-LAST:event_fireVillageSelectionChangedEvent

    private void deleteTags() {
        Tag tag = null;
        try {
            tag = (Tag) jTagsList.getSelectedValue();
        } catch (ClassCastException cce) {
            //no tags availabler
            JOptionPaneHelper.showWarningBox(this, "Keine Gruppen vorhanden. Bitte importiere zuerst Gruppen aus dem Spiel oder lege sie in der Gruppen-Ansicht manuell an.", "Warnung");
            return;
        }
        if (tag == null) {
            return;
        }
        if (jPlayerName.getText().equals("Mehrfachauswahl")) {
            for (int i = 0; i < jVillageList.getItemCount(); i++) {
                Village v = (Village) jVillageList.getItemAt(i);
                if (v.getTribe() != Barbarians.getSingleton()) {
                    TagManager.getSingleton().removeTag(v, tag.getName());
                }
            }
        } else {
            Village selection = (Village) jVillageList.getSelectedItem();
            if (selection == null) {
                return;
            }
            TagManager.getSingleton().removeTag(selection, tag.getName());
        }
        DefaultListModel model = (DefaultListModel) jTagsList.getModel();
        model.removeElement(tag);
    }
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jCancelButton;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton jOKButton;
    private javax.swing.JTextField jPlayerName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jTagsChooser;
    private javax.swing.JList jTagsList;
    private javax.swing.JLabel jTribeLabel;
    private javax.swing.JLabel jVillageLabel;
    private javax.swing.JComboBox jVillageList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dataChangedEvent() {
        dataChangedEvent(null);
    }

    @Override
    public void dataChangedEvent(String pGroup) {
        updateUserTags();
    }
}
