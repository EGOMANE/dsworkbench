/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GroupListCellRenderer.java
 *
 * Created on 21.12.2011, 08:01:06
 */
package de.tor.tribes.ui.renderer;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.ui.components.GroupSelectionList;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ProfileManager;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

/**
 *
 * @author jejkal
 */
public class GroupListCellRenderer extends javax.swing.JPanel implements ListCellRenderer {

    /** Creates new form GroupListCellRenderer */
    public GroupListCellRenderer() {
        initComponents();
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object pValue, int pIndex, boolean pSelected, boolean pHasFocus) {
        if (pSelected) {
            setForeground(list.getSelectionForeground());
            super.setBackground(list.getSelectionBackground());
            jLabel2.setForeground(list.getSelectionForeground());
        } else {
            setForeground(list.getBackground());
            super.setBackground(list.getBackground());
            jLabel2.setForeground(new java.awt.Color(0, 153, 255));
        }
        GroupSelectionList.ListItem item = (GroupSelectionList.ListItem) pValue;
        jLabel1.setText(item.getTag().toString());
        if (item.isSpecial()) {
            if (item.getState() == 0) {
                jLabel2.setText("NICHT");
            } else {
                jLabel2.setText("->");
            }
        } else {
            switch (item.getState()) {
                case 1:
                    jLabel2.setText("NICHT");
                    break;
                case 2:
                    jLabel2.setText("UND");
                    break;
                case 3:
                    jLabel2.setText("ODER");
                    break;
                default:
                    jLabel2.setText("OHNE");
            }
        }
        return this;
    }
    
    public void view() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane p = new JScrollPane();
        p.setViewportView(new GroupSelectionList());
        f.getContentPane().add(p);
        f.pack();
        f.addWindowListener(new WindowAdapter() {
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        
        f.setVisible(true);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("oder");
        jLabel2.setMaximumSize(new java.awt.Dimension(30, 16));
        jLabel2.setMinimumSize(new java.awt.Dimension(30, 16));
        jLabel2.setPreferredSize(new java.awt.Dimension(30, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(jLabel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }
        
        new GroupListCellRenderer().view();
        Logger.getRootLogger().addAppender(new ConsoleAppender(new org.apache.log4j.PatternLayout("%d - %-5p - %-20c (%C [%L]) - %m%n")));
        GlobalOptions.setSelectedServer("de43");
        DataHolder.getSingleton().loadData(false);
        ProfileManager.getSingleton().loadProfiles();
        GlobalOptions.setSelectedProfile(ProfileManager.getSingleton().getProfiles("de43")[0]);
        GlobalOptions.loadUserData();
    }
}
