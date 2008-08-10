/*
 * MarkerPanel.java
 *
 * Created on 7. Oktober 2007, 14:06
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Marker;
import javax.swing.ImageIcon;

/**
 *
 * @author  Charon
 */
public class MarkerCell extends javax.swing.JPanel {

    private static ImageIcon PLAYER_ICON = null;
    private static ImageIcon ALLY_ICON = null;
    private int type = Marker.TRIBE_MARKER_TYPE;

    static {
        try {
            PLAYER_ICON = new javax.swing.ImageIcon(MarkerCell.class.getResource("/res/face.png"));
            ALLY_ICON = new javax.swing.ImageIcon(MarkerCell.class.getResource("/res/ally.png"));
        } catch (Exception e) {
        }
    }

    public static MarkerCell factoryPlayerMarker(String pPlayerName) {
        return new MarkerCell(pPlayerName, Marker.TRIBE_MARKER_TYPE);
    }

    public static MarkerCell factoryAllyMarker(String pAllyName) {
        return new MarkerCell(pAllyName, Marker.ALLY_MARKER_TYPE);
    }

    /** Creates new form MarkerPanel */
    MarkerCell(String pName, int pType) {
        initComponents();
        jMarkerLabel.setText(pName);
        if (pType == Marker.TRIBE_MARKER_TYPE) {
            jMarkerLabel.setIcon(PLAYER_ICON);
            type = pType;
        } else {
            jMarkerLabel.setIcon(ALLY_ICON);
            type = pType;
        }
    }

    public int getType(){
        return type;
    }
    
    public void setType(int pType){
        type = pType;
    }
    
    public String getMarkerName() {
        return jMarkerLabel.getText();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jMarkerLabel = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(255, 255, 255));

        jMarkerLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jMarkerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ally.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMarkerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMarkerLabel)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jMarkerLabel;
    // End of variables declaration//GEN-END:variables
}
