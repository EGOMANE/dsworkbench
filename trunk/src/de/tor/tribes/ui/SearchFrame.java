/*
 * SearchFrame.java
 *
 * Created on 19. Juni 2008, 11:19
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.util.GlobalOptions;
import java.awt.event.ItemEvent;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author  Jejkal
 */
public class SearchFrame extends javax.swing.JFrame implements SearchListener {

    private String sLastPlayerValue = null;
    private SearchThread mSearchThread = null;
    private MapFrame mParent = null;

    /** Creates new form SearchFrame */
    public SearchFrame(MapFrame pParent) {
        initComponents();
        mParent = pParent;
        // frameControlPanel1.setupPanel(this, true, true);
        mSearchThread = new SearchThread("", this);
        mSearchThread.setDaemon(true);
        mSearchThread.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPlayerSearch = new javax.swing.JPanel();
        jSearchTerm = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTribesList = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jMarkAllyButton = new javax.swing.JButton();
        jAllyList = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jMarkTribeButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jVillageList = new javax.swing.JComboBox();

        setTitle("Suche");
        setAlwaysOnTop(true);

        jSearchTerm.setMaximumSize(new java.awt.Dimension(200, 20));
        jSearchTerm.setMinimumSize(new java.awt.Dimension(200, 20));
        jSearchTerm.setPreferredSize(new java.awt.Dimension(200, 20));
        jSearchTerm.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fireValueChangedEvent(evt);
            }
        });

        jLabel1.setText("Suchbegriff");

        jTribesList.setMaximumSize(new java.awt.Dimension(200, 20));
        jTribesList.setMinimumSize(new java.awt.Dimension(200, 20));
        jTribesList.setPreferredSize(new java.awt.Dimension(200, 20));
        jTribesList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireTribeSelectionChangedEvent(evt);
            }
        });

        jLabel2.setText("Spieler");

        jMarkAllyButton.setText("Markieren");
        jMarkAllyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddMarkerEvent(evt);
            }
        });

        jAllyList.setMaximumSize(new java.awt.Dimension(200, 20));
        jAllyList.setMinimumSize(new java.awt.Dimension(200, 20));
        jAllyList.setPreferredSize(new java.awt.Dimension(200, 20));
        jAllyList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireAllySelectionChangedEvent(evt);
            }
        });

        jLabel3.setText("Stämme");

        jMarkTribeButton.setText("Markieren");
        jMarkTribeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddMarkerEvent(evt);
            }
        });

        jLabel6.setText("Dörfer");

        jButton5.setText("Zentrieren");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterMapEvent(evt);
            }
        });

        javax.swing.GroupLayout jPlayerSearchLayout = new javax.swing.GroupLayout(jPlayerSearch);
        jPlayerSearch.setLayout(jPlayerSearchLayout);
        jPlayerSearchLayout.setHorizontalGroup(
            jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPlayerSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(6, 6, 6)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAllyList, 0, 228, Short.MAX_VALUE)
                    .addComponent(jTribesList, 0, 0, Short.MAX_VALUE)
                    .addComponent(jSearchTerm, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addComponent(jVillageList, 0, 228, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(jMarkTribeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jMarkAllyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPlayerSearchLayout.setVerticalGroup(
            jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlayerSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jSearchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jMarkAllyButton)
                    .addComponent(jAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jMarkTribeButton)
                    .addComponent(jLabel2)
                    .addComponent(jTribesList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jButton5)
                    .addComponent(jVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPlayerSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPlayerSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireValueChangedEvent(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fireValueChangedEvent
    String currentValue = jSearchTerm.getText();
    if (currentValue.equals(sLastPlayerValue)) {
        //no change
        return;
    }
    sLastPlayerValue = currentValue;
    mSearchThread.setSearchTerm(currentValue);
}//GEN-LAST:event_fireValueChangedEvent

private void fireTribeSelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireTribeSelectionChangedEvent
    try {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(((Tribe) evt.getItem()).getVillageList().toArray(new Village[0]));
            jVillageList.setModel(model);
        }
    } catch (Exception e) {
        //produced if 0-element in combobox is selected
    }
}//GEN-LAST:event_fireTribeSelectionChangedEvent

private void fireAllySelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireAllySelectionChangedEvent
    try {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(((Ally) evt.getItem()).getTribes().toArray(new Tribe[0]));
            jTribesList.setModel(model);
            jTribesList.setSelectedIndex(0);
            Tribe t = (Tribe) jTribesList.getItemAt(0);
            model = new DefaultComboBoxModel(t.getVillageList().toArray(new Village[0]));
            jVillageList.setModel(model);
        }
    } catch (Exception e) {
        //produced if 0-element in combobox is selected
    }
}//GEN-LAST:event_fireAllySelectionChangedEvent

private void fireAddMarkerEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddMarkerEvent
    if (evt.getSource() == jMarkAllyButton) {
        MarkerAddFrame f = new MarkerAddFrame(mParent);
        f.setVillage(((Ally) jAllyList.getSelectedItem()).getTribes().get(0).getVillageList().get(0));
        f.setAllyOnly();
        f.setVisible(true);
    } else {
        MarkerAddFrame f = new MarkerAddFrame(mParent);
        f.setVillage(((Tribe) jTribesList.getSelectedItem()).getVillageList().get(0));
        f.setTribeOnly();
        f.setVisible(true);
    }

}//GEN-LAST:event_fireAddMarkerEvent

private void fireCenterMapEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterMapEvent
    mParent.centerVillage(((Village) jVillageList.getSelectedItem()));
}//GEN-LAST:event_fireCenterMapEvent

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            GlobalOptions.setSelectedServer("de26");
            GlobalOptions.loadData(false);
            GlobalOptions.initialize(false, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        java.awt.EventQueue.invokeLater(new  

              Runnable() {

                 @Override
            public void run() {
                new SearchFrame(null).setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jAllyList;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton jMarkAllyButton;
    private javax.swing.JButton jMarkTribeButton;
    private javax.swing.JPanel jPlayerSearch;
    private javax.swing.JTextField jSearchTerm;
    private javax.swing.JComboBox jTribesList;
    private javax.swing.JComboBox jVillageList;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fireTribesFoundEvent(Tribe[] t) {
        jTribesList.setModel(new DefaultComboBoxModel(t));
        //remove villages
        jVillageList.setModel(new DefaultComboBoxModel());
        try {
            //jAllyList.setSelectedIndex(-1);
            String result = t.length + " Spieler gefunden";
            ((DefaultComboBoxModel) jTribesList.getModel()).insertElementAt(result, 0);
            jTribesList.setSelectedIndex(0);
        //  jTribesList.updateUI();
        } catch (Exception e) {
        }
    }

    @Override
    public void fireAlliesFoundEvent(Ally[] a) {
        jAllyList.setModel(new DefaultComboBoxModel(a));

        try {
            //jAllyList.setSelectedIndex(-1);
            String result = a.length + ((a.length == 1) ? " Stamm " : " Stämme ") + "gefunden";

            ((DefaultComboBoxModel) jAllyList.getModel()).insertElementAt(result, 0);
            jAllyList.setSelectedIndex(0);
        //   jAllyList.updateUI();
        } catch (Exception e) {
        }
    }
}

interface SearchListener {

    public void fireTribesFoundEvent(Tribe[] t);

    public void fireAlliesFoundEvent(Ally[] a);
}

class SearchThread extends Thread {

    private boolean running = true;
    private boolean restart = false;
    private boolean searchDone = false;
    private String sSearchTerm = null;
    private SearchListener mListener;

    public SearchThread(String pSearchTerm, SearchListener pListener) {
        sSearchTerm = pSearchTerm;
        mListener = pListener;
    }

    public void setSearchTerm(String pSearchTerm) {
        if (pSearchTerm != null) {
            if (!sSearchTerm.equals(pSearchTerm)) {
                sSearchTerm = pSearchTerm;
                restart = true;
                searchDone = false;
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            if (!searchDone) {
                if (sSearchTerm.length() >= 1) {
                    List<Tribe> tribeList = new LinkedList<Tribe>();
                    List<Ally> allyList = new LinkedList<Ally>();
                    Enumeration<Integer> tribes = GlobalOptions.getDataHolder().getTribes().keys();
                    while (tribes.hasMoreElements()) {
                        Tribe t = GlobalOptions.getDataHolder().getTribes().get(tribes.nextElement());
                        if (t.getName().toLowerCase().startsWith(sSearchTerm.toLowerCase())) {
                            if (!tribeList.contains(t)) {
                                tribeList.add(t);
                            }
                        }
                        Ally a = t.getAlly();
                        if (a != null) {
                            if (a.getName().toLowerCase().startsWith(sSearchTerm.toLowerCase())) {
                                if (!allyList.contains(a)) {
                                    allyList.add(a);
                                }
                            }
                        }
                        if (restart) {
                            break;
                        }
                    }
                    if (!restart) {
                        searchDone = true;
                        mListener.fireTribesFoundEvent(tribeList.toArray(new Tribe[0]));
                        mListener.fireAlliesFoundEvent(allyList.toArray(new Ally[0]));
                    } else {
                        restart = false;
                    }

                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    public void restartSearch() {
        restart = true;
    }

    public void stopRunning() {
        running = false;
    }
}
