/*
 * SearchFrame.java
 *
 * Created on 19. Juni 2008, 11:19
 */
package de.tor.tribes.ui.views;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.types.ext.Ally;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.ui.windows.MarkerAddFrame;
import de.tor.tribes.util.BrowserCommandSender;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**
 * @author  Jejkal
 */
public class DSWorkbenchSearchFrame extends javax.swing.JFrame implements SearchListener {

    private static Logger logger = Logger.getLogger("Search");
    private String sLastPlayerValue = null;
    private SearchThread mSearchThread = null;
    private static DSWorkbenchSearchFrame SINGLETON = null;
    private MarkerAddFrame mMarkerAddFrame = null;

    public static synchronized DSWorkbenchSearchFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchSearchFrame();
        }
        return SINGLETON;
    }

    /** Creates new form SearchFrame */
    DSWorkbenchSearchFrame() {
        initComponents();
        // frameControlPanel1.setupPanel(this, true, true);
        jCenterInGameButton.setIcon(new ImageIcon("./graphics/icons/center.png"));
        jSendResButton.setIcon(new ImageIcon("./graphics/icons/booty.png"));
        jSendDefButton.setIcon(new ImageIcon("./graphics/icons/def.png"));
        try {
            jSearchFrameAlwaysOnTop.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("search.frame.alwaysOnTop")));
            setAlwaysOnTop(jSearchFrameAlwaysOnTop.isSelected());
        } catch (Exception e) {
            //setting not available
        }

        //check desktop support
        if (!Desktop.isDesktopSupported()) {
            jCenterInGameButton.setEnabled(false);
            jSendDefButton.setEnabled(false);
            jSendResButton.setEnabled(false);
        }
        mSearchThread = new SearchThread("", this);
        mSearchThread.start();
        mMarkerAddFrame = new MarkerAddFrame();
        jCenterButton.setEnabled(GlobalOptions.isMinimal());


        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.search_tool", GlobalOptions.getHelpBroker().getHelpSet());
        }
        // </editor-fold>
    }

    @Override
    public void toBack() {
        jSearchFrameAlwaysOnTop.setSelected(false);
        fireSearchFrameAlwaysOnTopEvent(null);
        super.toBack();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPlayerSearch = new javax.swing.JPanel();
        jSearchTerm = new javax.swing.JTextField();
        jSearchTermLabel = new javax.swing.JLabel();
        jTribesList = new javax.swing.JComboBox();
        jTribesLabel = new javax.swing.JLabel();
        jMarkAllyButton = new javax.swing.JButton();
        jAllyList = new javax.swing.JComboBox();
        jAlliesLabel = new javax.swing.JLabel();
        jMarkTribeButton = new javax.swing.JButton();
        jVillagesLabel = new javax.swing.JLabel();
        jCenterButton = new javax.swing.JButton();
        jVillageList = new javax.swing.JComboBox();
        jCenterInGameButton = new javax.swing.JButton();
        jSendResButton = new javax.swing.JButton();
        jSendDefButton = new javax.swing.JButton();
        jInGameOptionsLabel = new javax.swing.JLabel();
        jSearchFrameAlwaysOnTop = new javax.swing.JCheckBox();

        setTitle("Suche");

        jPlayerSearch.setBackground(new java.awt.Color(239, 235, 223));

        jSearchTerm.setMaximumSize(new java.awt.Dimension(200, 25));
        jSearchTerm.setMinimumSize(new java.awt.Dimension(200, 25));
        jSearchTerm.setPreferredSize(new java.awt.Dimension(200, 25));
        jSearchTerm.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fireValueChangedEvent(evt);
            }
        });

        jSearchTermLabel.setText("Suchbegriff");
        jSearchTermLabel.setMaximumSize(new java.awt.Dimension(70, 25));
        jSearchTermLabel.setMinimumSize(new java.awt.Dimension(70, 25));
        jSearchTermLabel.setPreferredSize(new java.awt.Dimension(70, 25));

        jTribesList.setMaximumSize(new java.awt.Dimension(200, 25));
        jTribesList.setMinimumSize(new java.awt.Dimension(200, 25));
        jTribesList.setPreferredSize(new java.awt.Dimension(200, 25));
        jTribesList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireTribeSelectionChangedEvent(evt);
            }
        });

        jTribesLabel.setText("Spieler");
        jTribesLabel.setMaximumSize(new java.awt.Dimension(70, 25));
        jTribesLabel.setMinimumSize(new java.awt.Dimension(70, 25));
        jTribesLabel.setPreferredSize(new java.awt.Dimension(70, 25));

        jMarkAllyButton.setBackground(new java.awt.Color(239, 235, 223));
        jMarkAllyButton.setText("Markieren");
        jMarkAllyButton.setToolTipText("Stamm markieren");
        jMarkAllyButton.setMaximumSize(new java.awt.Dimension(100, 25));
        jMarkAllyButton.setMinimumSize(new java.awt.Dimension(100, 25));
        jMarkAllyButton.setPreferredSize(new java.awt.Dimension(100, 25));
        jMarkAllyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddMarkerEvent(evt);
            }
        });

        jAllyList.setMaximumSize(new java.awt.Dimension(200, 25));
        jAllyList.setMinimumSize(new java.awt.Dimension(200, 25));
        jAllyList.setPreferredSize(new java.awt.Dimension(200, 25));
        jAllyList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireAllySelectionChangedEvent(evt);
            }
        });

        jAlliesLabel.setText("Stämme");
        jAlliesLabel.setMaximumSize(new java.awt.Dimension(70, 25));
        jAlliesLabel.setMinimumSize(new java.awt.Dimension(70, 25));
        jAlliesLabel.setPreferredSize(new java.awt.Dimension(70, 25));

        jMarkTribeButton.setBackground(new java.awt.Color(239, 235, 223));
        jMarkTribeButton.setText("Markieren");
        jMarkTribeButton.setToolTipText("Spieler markieren");
        jMarkTribeButton.setMaximumSize(new java.awt.Dimension(100, 25));
        jMarkTribeButton.setMinimumSize(new java.awt.Dimension(100, 25));
        jMarkTribeButton.setPreferredSize(new java.awt.Dimension(100, 25));
        jMarkTribeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddMarkerEvent(evt);
            }
        });

        jVillagesLabel.setText("Dörfer");
        jVillagesLabel.setMaximumSize(new java.awt.Dimension(70, 25));
        jVillagesLabel.setMinimumSize(new java.awt.Dimension(70, 25));
        jVillagesLabel.setPreferredSize(new java.awt.Dimension(70, 25));

        jCenterButton.setBackground(new java.awt.Color(239, 235, 223));
        jCenterButton.setText("Zentrieren");
        jCenterButton.setToolTipText("Dorf in DS Workbench zentrieren");
        jCenterButton.setMaximumSize(new java.awt.Dimension(100, 25));
        jCenterButton.setMinimumSize(new java.awt.Dimension(100, 25));
        jCenterButton.setPreferredSize(new java.awt.Dimension(100, 25));
        jCenterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterMapEvent(evt);
            }
        });

        jVillageList.setMaximumSize(new java.awt.Dimension(200, 25));
        jVillageList.setMinimumSize(new java.awt.Dimension(200, 25));
        jVillageList.setPreferredSize(new java.awt.Dimension(200, 25));

        jCenterInGameButton.setBackground(new java.awt.Color(239, 235, 223));
        jCenterInGameButton.setToolTipText("Karte zentrieren");
        jCenterInGameButton.setMaximumSize(new java.awt.Dimension(35, 35));
        jCenterInGameButton.setMinimumSize(new java.awt.Dimension(35, 35));
        jCenterInGameButton.setPreferredSize(new java.awt.Dimension(35, 35));
        jCenterInGameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterMapInGameEvent(evt);
            }
        });

        jSendResButton.setBackground(new java.awt.Color(239, 235, 223));
        jSendResButton.setToolTipText("Rohstoffe schicken");
        jSendResButton.setMaximumSize(new java.awt.Dimension(35, 35));
        jSendResButton.setMinimumSize(new java.awt.Dimension(35, 35));
        jSendResButton.setPreferredSize(new java.awt.Dimension(35, 35));
        jSendResButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSendResEvent(evt);
            }
        });

        jSendDefButton.setBackground(new java.awt.Color(239, 235, 223));
        jSendDefButton.setToolTipText("Unterstützung schicken");
        jSendDefButton.setMaximumSize(new java.awt.Dimension(35, 35));
        jSendDefButton.setMinimumSize(new java.awt.Dimension(35, 35));
        jSendDefButton.setPreferredSize(new java.awt.Dimension(35, 35));
        jSendDefButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSendDefEvent(evt);
            }
        });

        jInGameOptionsLabel.setText("InGame Optionen");

        javax.swing.GroupLayout jPlayerSearchLayout = new javax.swing.GroupLayout(jPlayerSearch);
        jPlayerSearch.setLayout(jPlayerSearchLayout);
        jPlayerSearchLayout.setHorizontalGroup(
            jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlayerSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jInGameOptionsLabel)
                    .addComponent(jSearchTermLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jVillagesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTribesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAlliesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAllyList, 0, 386, Short.MAX_VALUE)
                    .addComponent(jTribesList, 0, 0, Short.MAX_VALUE)
                    .addComponent(jSearchTerm, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                    .addComponent(jVillageList, javax.swing.GroupLayout.Alignment.TRAILING, 0, 386, Short.MAX_VALUE)
                    .addGroup(jPlayerSearchLayout.createSequentialGroup()
                        .addComponent(jCenterInGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSendDefButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSendResButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jMarkTribeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMarkAllyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCenterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPlayerSearchLayout.setVerticalGroup(
            jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlayerSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSearchTermLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSearchTerm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jMarkAllyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAlliesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jMarkTribeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTribesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTribesList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jVillagesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCenterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPlayerSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jCenterInGameButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jInGameOptionsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSendResButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSendDefButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSearchFrameAlwaysOnTop.setText("Immer im Vordergrund");
        jSearchFrameAlwaysOnTop.setOpaque(false);
        jSearchFrameAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireSearchFrameAlwaysOnTopEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSearchFrameAlwaysOnTop)
                    .addComponent(jPlayerSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPlayerSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSearchFrameAlwaysOnTop)
                .addContainerGap())
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
            Village[] v = ((Tribe) evt.getItem()).getVillageList();
            Arrays.sort(v);
            DefaultComboBoxModel model = new DefaultComboBoxModel(v);
            jVillageList.setModel(model);
        }
    } catch (Exception e) {
        //produced if 0-element in combobox is selected
    }
}//GEN-LAST:event_fireTribeSelectionChangedEvent

private void fireAllySelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireAllySelectionChangedEvent
    try {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Tribe[] tl = ((Ally) evt.getItem()).getTribes().toArray(new Tribe[0]);
            Arrays.sort(tl, Tribe.CASE_INSENSITIVE_ORDER);
            DefaultComboBoxModel model = new DefaultComboBoxModel(tl);
            jTribesList.setModel(model);
            jTribesList.setSelectedIndex(0);
            Tribe t = (Tribe) jTribesList.getItemAt(0);
            model = new DefaultComboBoxModel(t.getVillageList());
            jVillageList.setModel(model);
        }
    } catch (Exception e) {
        //produced if 0-element in combobox is selected
    }
}//GEN-LAST:event_fireAllySelectionChangedEvent

private void fireAddMarkerEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddMarkerEvent
    try {
        if (evt.getSource() == jMarkAllyButton) {
            Object selection = jAllyList.getSelectedItem();
            if (selection instanceof String) {
                //no ally selected
            } else {
                mMarkerAddFrame.setVillage(((Ally) selection).getTribes().get(0).getVillageList()[0]);
                mMarkerAddFrame.setAllyOnly();
                mMarkerAddFrame.setVisible(true);
            }
        } else {
            MarkerAddFrame f = new MarkerAddFrame();
            Object selection = jTribesList.getSelectedItem();
            if (selection instanceof String) {
                //no tribe selected
            } else {
                mMarkerAddFrame.setVillage(((Tribe) selection).getVillageList()[0]);
                mMarkerAddFrame.setTribeOnly();
                mMarkerAddFrame.setVisible(true);
            }
        }
    } catch (Exception e) {
        logger.warn("Failed to add marker", e);
    }
}//GEN-LAST:event_fireAddMarkerEvent

private void fireCenterMapEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterMapEvent
    if (jCenterButton.isEnabled()) {
        DSWorkbenchMainFrame.getSingleton().centerVillage(((Village) jVillageList.getSelectedItem()));
    }
}//GEN-LAST:event_fireCenterMapEvent

private void fireCenterMapInGameEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterMapInGameEvent
    if (!jCenterInGameButton.isEnabled()) {
        return;
    }
    Village v = (Village) jVillageList.getSelectedItem();
    if (v != null) {
        BrowserCommandSender.centerVillage(v);
    }
}//GEN-LAST:event_fireCenterMapInGameEvent

private void fireSendDefEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSendDefEvent
    if (!jSendDefButton.isEnabled()) {
        return;
    }
    Village target = (Village) jVillageList.getSelectedItem();
    Village source = DSWorkbenchMainFrame.getSingleton().getCurrentUserVillage();
    if ((source != null) && (target != null)) {
        BrowserCommandSender.sendTroops(source, target);
    }
}//GEN-LAST:event_fireSendDefEvent

private void fireSendResEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSendResEvent
    if (!jSendResButton.isEnabled()) {
        return;
    }
    Village target = (Village) jVillageList.getSelectedItem();
    Village source = DSWorkbenchMainFrame.getSingleton().getCurrentUserVillage();
    if ((source != null) && (target != null)) {
        BrowserCommandSender.sendRes(source, target);
    }
}//GEN-LAST:event_fireSendResEvent

private void fireSearchFrameAlwaysOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireSearchFrameAlwaysOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireSearchFrameAlwaysOnTopEvent

    public static void main(String[] args) {
        try {
            //  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }
        DSWorkbenchSearchFrame.getSingleton().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DSWorkbenchSearchFrame.getSingleton().setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jAlliesLabel;
    private javax.swing.JComboBox jAllyList;
    private javax.swing.JButton jCenterButton;
    private javax.swing.JButton jCenterInGameButton;
    private javax.swing.JLabel jInGameOptionsLabel;
    private javax.swing.JButton jMarkAllyButton;
    private javax.swing.JButton jMarkTribeButton;
    private javax.swing.JPanel jPlayerSearch;
    private javax.swing.JCheckBox jSearchFrameAlwaysOnTop;
    private javax.swing.JTextField jSearchTerm;
    private javax.swing.JLabel jSearchTermLabel;
    private javax.swing.JButton jSendDefButton;
    private javax.swing.JButton jSendResButton;
    private javax.swing.JLabel jTribesLabel;
    private javax.swing.JComboBox jTribesList;
    private javax.swing.JComboBox jVillageList;
    private javax.swing.JLabel jVillagesLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fireTribesFoundEvent(Tribe[] t) {
        Arrays.sort(t, Tribe.CASE_INSENSITIVE_ORDER);
        jTribesList.setModel(new DefaultComboBoxModel(t));
        //remove villages
        jVillageList.setModel(new DefaultComboBoxModel());
        try {
            String result = t.length + " Spieler gefunden";
            ((DefaultComboBoxModel) jTribesList.getModel()).insertElementAt(result, 0);
            jTribesList.setSelectedIndex(0);
        } catch (Exception e) {
        }
    }

    @Override
    public void fireAlliesFoundEvent(Ally[] a) {
        Arrays.sort(a, Ally.CASE_INSENSITIVE_ORDER);
        jAllyList.setModel(new DefaultComboBoxModel(a));

        try {
            String result = a.length + ((a.length == 1) ? " Stamm " : " Stämme ") + "gefunden";

            ((DefaultComboBoxModel) jAllyList.getModel()).insertElementAt(result, 0);
            jAllyList.setSelectedIndex(0);
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
        setName("SearchThread");
        setDaemon(true);
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
                    Enumeration<Integer> tribes = DataHolder.getSingleton().getTribes().keys();
                    while (tribes.hasMoreElements()) {
                        Tribe t = DataHolder.getSingleton().getTribes().get(tribes.nextElement());
                        if (t.getName().toLowerCase().indexOf(sSearchTerm.toLowerCase()) > -1) {
                            if (!tribeList.contains(t)) {
                                tribeList.add(t);
                            }
                        }
                        Ally a = t.getAlly();
                        if (a != null) {
                            if ((a.getName().toLowerCase().indexOf(sSearchTerm.toLowerCase()) > -1) || (a.getTag().toLowerCase().indexOf(sSearchTerm.toLowerCase())) > -1) {
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
