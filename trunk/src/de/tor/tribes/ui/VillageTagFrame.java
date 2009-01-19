/*
 * VillageTagFrame.java
 *
 * Created on 20. Juli 2008, 16:35
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.types.Village;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.util.tag.TagManager;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author  Charon
 */
public class VillageTagFrame extends javax.swing.JFrame {

    private static VillageTagFrame SINGLETON = null;

    public static synchronized VillageTagFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new VillageTagFrame();
        }
        return SINGLETON;
    }

    /** Creates new form VillageTagFrame */
    VillageTagFrame() {
        initComponents();
    }

    public void updateUserTags() {
        jTagsChooser.setModel(new DefaultComboBoxModel(TagManager.getSingleton().getTags().toArray(new Tag[]{})));
        jTagsChooser.updateUI();
    }

    public void showTagsFrame(Village pVillage) {
        Tribe t = pVillage.getTribe();
        if (t == null) {
            return;
        }
        jPlayerName.setText(t.getName());
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Village v : t.getVillageList()) {
            model.addElement(v);
        }
        jVillageList.setModel(model);
        jVillageList.setSelectedItem(pVillage);

        List<Tag> tags = TagManager.getSingleton().getTags(pVillage);

        DefaultListModel lModel = new DefaultListModel();

        for (Tag tag : tags) {
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
        jButton2 = new javax.swing.JButton();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();
        jVillageList = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("VillageTagFrame.title")); // NOI18N

        jTribeLabel.setText(bundle.getString("VillageTagFrame.jTribeLabel.text")); // NOI18N

        jVillageLabel.setText(bundle.getString("VillageTagFrame.jVillageLabel.text")); // NOI18N

        jScrollPane1.setViewportView(jTagsList);

        jLabel3.setText(bundle.getString("VillageTagFrame.jLabel3.text")); // NOI18N

        jPlayerName.setBackground(new java.awt.Color(239, 235, 223));
        jPlayerName.setEditable(false);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTagEvent(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveTagEvent(evt);
            }
        });

        jOKButton.setText(bundle.getString("VillageTagFrame.jOKButton.text")); // NOI18N
        jOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireOkEvent(evt);
            }
        });

        jCancelButton.setText(bundle.getString("VillageTagFrame.jCancelButton.text")); // NOI18N
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTribeLabel)
                    .addComponent(jVillageLabel)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPlayerName, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addComponent(jVillageList, 0, 344, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCancelButton)
                            .addComponent(jTagsChooser, 0, 282, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jOKButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTribeLabel)
                    .addComponent(jPlayerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jVillageLabel)
                    .addComponent(jVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTagsChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jOKButton)
                    .addComponent(jCancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireAddTagEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTagEvent
    Village selection = (Village) jVillageList.getSelectedItem();
    Tag tag = (Tag) jTagsChooser.getSelectedItem();
    if ((selection == null) || (tag == null)) {
        return;
    }

    DefaultListModel model = (DefaultListModel) jTagsList.getModel();
    if (!model.contains(tag)) {
        model.addElement(tag);
        TagManager.getSingleton().addTag(selection, tag.getName());
    }
}//GEN-LAST:event_fireAddTagEvent

private void fireRemoveTagEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveTagEvent
    Village selection = (Village) jVillageList.getSelectedItem();
    if (selection == null) {
        return;
    }
    Tag selectedTag = (Tag) jTagsList.getSelectedValue();
    if (selectedTag == null) {
        return;
    }
    TagManager.getSingleton().removeTag(selection, selectedTag.getName());

    DefaultListModel model = (DefaultListModel) jTagsList.getModel();
    model.removeElement(selectedTag);
}//GEN-LAST:event_fireRemoveTagEvent

private void fireOkEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireOkEvent
    TagManager.getSingleton().saveTagsToFile(DataHolder.getSingleton().getDataDirectory() + "/tags.xml");
    setVisible(false);
    //init redraw
    MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new VillageTagFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
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
}
