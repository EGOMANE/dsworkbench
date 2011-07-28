/*
 * DSWorkbenchTroopsFrame.java
 *
 * Created on 2. Oktober 2008, 13:34
 */
package de.tor.tribes.ui.views;

import com.jidesoft.swing.JideTabbedPane;
import com.smardec.mousegestures.MouseGestures;
import de.tor.tribes.control.GenericManagerListener;
import de.tor.tribes.control.ManageableType;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.AbstractDSWorkbenchFrame;
import de.tor.tribes.ui.GenericTestPanel;
import de.tor.tribes.ui.SupportTroopTableTab;
import de.tor.tribes.ui.TabInterface;
import de.tor.tribes.ui.TroopTableTab;
import de.tor.tribes.ui.models.SupportTroopsTableModel;
import de.tor.tribes.util.Constants;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ImageUtils;
import de.tor.tribes.util.MouseGestureHandler;
import de.tor.tribes.util.PropertyHelper;
import de.tor.tribes.util.tag.TagManager;
import de.tor.tribes.util.troops.SupportVillageTroopsHolder;
import java.util.List;
import javax.swing.event.ChangeEvent;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.ConsoleAppender;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * @author  Jejkal
 */
public class DSWorkbenchTroopsFrame extends AbstractDSWorkbenchFrame implements GenericManagerListener, ActionListener, DataHolderListener {

    @Override
    public void fireDataHolderEvent(String pFile) {
    }

    @Override
    public void fireDataLoadedEvent(boolean pSuccess) {
        if (pSuccess) {
            resetView();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TabInterface activeTab = getActiveTab();
        if (e.getActionCommand().equals("Delete")) {
            if (activeTab != null) {
                activeTab.deleteSelection();
            }
        } else if (e.getActionCommand().equals("BBCopy")) {
            if (activeTab != null) {
                activeTab.transferSelection(TroopTableTab.TRANSFER_TYPE.CLIPBOARD_BB);
            }
        } else if (e.getActionCommand().equals("Find")) {
            BufferedImage back = ImageUtils.createCompatibleBufferedImage(3, 3, BufferedImage.TRANSLUCENT);
            Graphics g = back.getGraphics();
            g.setColor(new Color(120, 120, 120, 120));
            g.fillRect(0, 0, back.getWidth(), back.getHeight());
            g.setColor(new Color(120, 120, 120));
            g.drawLine(0, 0, 3, 3);
            g.dispose();
            TexturePaint paint = new TexturePaint(back, new Rectangle2D.Double(0, 0, back.getWidth(), back.getHeight()));
            jxSearchPane.setBackgroundPainter(new MattePainter(paint));
            updateTagList();
            jxSearchPane.setVisible(true);
        } else if (e.getActionCommand() != null && activeTab != null) {
            if (e.getActionCommand().equals("SelectionDone")) {
                activeTab.updateSelectionInfo();
            }
        }
    }
    private static Logger logger = Logger.getLogger("TroopsDialog");
    private static DSWorkbenchTroopsFrame SINGLETON = null;
    private GenericTestPanel centerPanel = null;

    public static synchronized DSWorkbenchTroopsFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchTroopsFrame();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchTroopsFrame */
    DSWorkbenchTroopsFrame() {
        initComponents();

        centerPanel = new GenericTestPanel(true);
        jTroopsPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setChildComponent(jXTroopsPanel);
        buildMenu();

        jTroopsTabPane.setTabShape(JideTabbedPane.SHAPE_OFFICE2003);
        jTroopsTabPane.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);
        jTroopsTabPane.setBoldActiveTab(true);

        jTroopsTabPane.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                TabInterface activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.updateSet();
                }
            }
        });

        DataHolder.getSingleton().addDataHolderListener(DSWorkbenchTroopsFrame.this);

        jXGroupsList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateFilter();
            }
        });
        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.troops_view", GlobalOptions.getHelpBroker().getHelpSet());
        }
        // </editor-fold>
        setGlassPane(jxSearchPane);
        pack();
    }

    public void storeCustomProperties(Configuration pConfig) {
        pConfig.setProperty(getPropertyPrefix() + ".menu.visible", centerPanel.isMenuVisible());
        pConfig.setProperty(getPropertyPrefix() + ".alwaysOnTop", jTroopsInformationAlwaysOnTop.isSelected());

        int selectedIndex = jTroopsTabPane.getModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            pConfig.setProperty(getPropertyPrefix() + ".tab.selection", selectedIndex);
        }


        TroopTableTab tab = ((TroopTableTab) jTroopsTabPane.getComponentAt(0));
        PropertyHelper.storeTableProperties(tab.getTroopTable(), pConfig, getPropertyPrefix());
    }

    public void restoreCustomProperties(Configuration pConfig) {
        centerPanel.setMenuVisible(pConfig.getBoolean(getPropertyPrefix() + ".menu.visible", true));
        try {
            jTroopsTabPane.setSelectedIndex(pConfig.getInteger(getPropertyPrefix() + ".tab.selection", 0));
        } catch (Exception e) {
        }
        try {
            jTroopsInformationAlwaysOnTop.setSelected(pConfig.getBoolean(getPropertyPrefix() + ".alwaysOnTop"));
        } catch (Exception e) {
        }

        setAlwaysOnTop(jTroopsInformationAlwaysOnTop.isSelected());

        TroopTableTab tab = ((TroopTableTab) jTroopsTabPane.getComponentAt(0));
        PropertyHelper.restoreTableProperties(tab.getTroopTable(), pConfig, getPropertyPrefix());
    }

    public String getPropertyPrefix() {
        return "troops.view";
    }

    private void buildMenu() {
        JXTaskPane transferTaskPane = new JXTaskPane();
        transferTaskPane.setTitle("Übertragen");
        JXButton transferVillageList = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/ui/center_ingame.png")));
        transferVillageList.setToolTipText("Zentriert das gewählte Dorf im Spiel");
        transferVillageList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                TabInterface tab = getActiveTab();
                if (tab != null) {
                    tab.centerVillageInGame();
                }
            }
        });
        transferTaskPane.getContentPane().add(transferVillageList);

        JXButton openPlace = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/ui/place.png")));
        openPlace.setToolTipText("Öffnet den Versammlungsplatz des gewählten Dorfes im Spiel");
        openPlace.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                TabInterface tab = getActiveTab();
                if (tab != null) {
                    tab.openPlaceInGame();
                }
            }
        });
        openPlace.setSize(transferVillageList.getSize());
        openPlace.setMinimumSize(transferVillageList.getMinimumSize());
        openPlace.setMaximumSize(transferVillageList.getMaximumSize());
        openPlace.setPreferredSize(transferVillageList.getPreferredSize());
        transferTaskPane.getContentPane().add(openPlace);
        JXButton centerVillage = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/center_24x24.png")));
        centerVillage.setToolTipText("Zentriert das gewählte Dorf auf der Hauptkarte");
        centerVillage.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                TabInterface tab = getActiveTab();
                if (tab != null) {
                    tab.centerVillage();
                }
            }
        });

        transferTaskPane.getContentPane().add(centerVillage);

        JXTaskPane miscPane = new JXTaskPane();
        miscPane.setTitle("Sonstiges");


        JXButton refillSupport = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/ui/filter_off.png")));
        refillSupport.setToolTipText("<html>Auff&uuml;llen der Unterst&uuml;tzungen für die gew&auml;hlten D&ouml;rfer<br/>"
                + "Die D&ouml;rfer werden so oft in den Angriffsplaner als Ziel eingef&uuml;gtm<br/>"
                + "bis die Truppenanzahl im Dorf der eingestellten Menge entspricht,<br/>"
                + "sofern entsprechend viele Unterst&uuml;zungen<br/>"
                + "mit der eingestellten Truppenzahl zugewiesen werden k&ouml;nnen</html>");
        refillSupport.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                TabInterface tab = getActiveTab();
                if (tab != null) {
                    tab.refillSupports();
                }
            }
        });

        miscPane.getContentPane().add(refillSupport);

        centerPanel.setupTaskPane(transferTaskPane, miscPane);
    }

    /**Get the currently selected tab*/
    private TabInterface getActiveTab() {
        try {
            if (jTroopsTabPane.getModel().getSelectedIndex() < 0) {
                return null;
            }
            return ((TabInterface) jTroopsTabPane.getComponentAt(jTroopsTabPane.getModel().getSelectedIndex()));
        } catch (ClassCastException cce) {
            return null;
        }
    }

    private void updateTagList() {
        DefaultListModel m = new DefaultListModel();
        for (ManageableType t : TagManager.getSingleton().getAllElements()) {
            Tag ta = (Tag) t;
            m.addElement(ta);
        }
        jXGroupsList.setModel(m);
    }

    /**Initialize and add one tab for each marker set to jTabbedPane1*/
    public void generateTroopTabs() {

        jTroopsTabPane.invalidate();
        while (jTroopsTabPane.getTabCount() > 0) {
            TabInterface tab = (TabInterface) jTroopsTabPane.getComponentAt(0);
            tab.deregister();
            jTroopsTabPane.removeTabAt(0);
        }

        String[] sets = TroopsManager.getSingleton().getGroups();

        //insert default tab to first place
        int cnt = 0;
        for (String set : sets) {
            System.out.println("Troop frame: add tab " + set);
            if (set.equals(TroopsManager.SUPPORT_GROUP)) {
                SupportTroopTableTab tab = new SupportTroopTableTab(this);
                tab.updateSet();
                jTroopsTabPane.addTab(set, tab);
            } else {
                jTroopsTabPane.addTab(set, new TroopTableTab(set, this));
            }

            cnt++;
        }

        for (int i = 0; i < jTroopsTabPane.getTabCount(); i++) {
            jTroopsTabPane.setTabClosableAt(i, false);
        }
        jTroopsTabPane.revalidate();
        TabInterface tab = getActiveTab();
        if (tab != null) {
            tab.updateSet();
        }
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

        jXTroopsPanel = new org.jdesktop.swingx.JXPanel();
        jTroopsTabPane = new com.jidesoft.swing.JideTabbedPane();
        jxSearchPane = new org.jdesktop.swingx.JXPanel();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        jButton12 = new javax.swing.JButton();
        jFilterRows = new javax.swing.JCheckBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jXGroupsList = new org.jdesktop.swingx.JXList();
        jLabel22 = new javax.swing.JLabel();
        jRelationType1 = new javax.swing.JCheckBox();
        jTroopsInformationAlwaysOnTop = new javax.swing.JCheckBox();
        jTroopsPanel = new javax.swing.JPanel();
        capabilityInfoPanel1 = new de.tor.tribes.ui.CapabilityInfoPanel();

        jXTroopsPanel.setMinimumSize(new java.awt.Dimension(700, 500));
        jXTroopsPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        jXTroopsPanel.setLayout(new java.awt.BorderLayout());

        jTroopsTabPane.setScrollSelectedTabOnWheel(true);
        jTroopsTabPane.setShowCloseButtonOnTab(true);
        jTroopsTabPane.setShowGripper(true);
        jTroopsTabPane.setTabEditingAllowed(true);
        jXTroopsPanel.add(jTroopsTabPane, java.awt.BorderLayout.CENTER);

        jxSearchPane.setOpaque(false);
        jxSearchPane.setLayout(new java.awt.GridBagLayout());

        jXPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jXPanel2.setInheritAlpha(false);

        jButton12.setText("Anwenden");
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireHideGlassPaneEvent(evt);
            }
        });

        jFilterRows.setText("Nur gefilterte Zeilen anzeigen");
        jFilterRows.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jFilterRowsfireUpdateFilterEvent(evt);
            }
        });

        jXGroupsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jXGroupsList);

        jLabel22.setText("Gruppen");

        jRelationType1.setSelected(true);
        jRelationType1.setText("Verknüpfung");
        jRelationType1.setToolTipText("Verknüpfung der gewählten Dorfgruppen (UND = Dorf muss in allen Gruppen sein, ODER = Dorf muss in mindestens einer Gruppe sein)");
        jRelationType1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRelationType1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/logic_or.png"))); // NOI18N
        jRelationType1.setOpaque(false);
        jRelationType1.setRolloverEnabled(false);
        jRelationType1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/logic_and.png"))); // NOI18N
        jRelationType1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireRelationChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jRelationType1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jFilterRows, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(jButton12))
                .addContainerGap())
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jRelationType1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFilterRows)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton12))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jxSearchPane.add(jXPanel2, new java.awt.GridBagConstraints());

        setTitle("Truppen");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jTroopsInformationAlwaysOnTop.setText("Immer im Vordergrund");
        jTroopsInformationAlwaysOnTop.setOpaque(false);
        jTroopsInformationAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireTroopsFrameOnTopEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jTroopsInformationAlwaysOnTop, gridBagConstraints);

        jTroopsPanel.setBackground(new java.awt.Color(239, 235, 223));
        jTroopsPanel.setMinimumSize(new java.awt.Dimension(700, 500));
        jTroopsPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        jTroopsPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jTroopsPanel, gridBagConstraints);

        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setPastable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(capabilityInfoPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireTroopsFrameOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireTroopsFrameOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireTroopsFrameOnTopEvent

private void fireHideGlassPaneEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideGlassPaneEvent
    jxSearchPane.setBackgroundPainter(null);
    jxSearchPane.setVisible(false);
}//GEN-LAST:event_fireHideGlassPaneEvent

private void jFilterRowsfireUpdateFilterEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jFilterRowsfireUpdateFilterEvent
    updateFilter();
}//GEN-LAST:event_jFilterRowsfireUpdateFilterEvent

private void fireRelationChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireRelationChangedEvent
    updateFilter();
}//GEN-LAST:event_fireRelationChangedEvent

    /**Update the attack plan filter*/
    private void updateFilter() {
        TabInterface tab = getActiveTab();
        if (tab != null) {
            final List<Tag> selection = new LinkedList<Tag>();
            for (Object o : jXGroupsList.getSelectedValues()) {
                selection.add((Tag) o);
            }
            tab.updateFilter(selection, jRelationType1.isSelected(), jFilterRows.isSelected());
        }
    }

    public List<Village> getSelectedSupportVillages() {
        List<Village> targets = new LinkedList<Village>();

        Component c = jTroopsTabPane.getSelectedComponent();
        if (c instanceof SupportTroopTableTab) {
            SupportTroopTableTab tab = (SupportTroopTableTab) c;
            JXTreeTable table = (JXTreeTable) tab.getTroopTable();
            int[] rows = table.getSelectedRows();
            SupportTroopsTableModel model = (SupportTroopsTableModel) table.getTreeTableModel();
            if (rows != null && rows.length > 0) {
                model.setTopLevelOnly(true);
                for (int row : rows) {
                    Village v = (Village) table.getValueAt(row, 0);
                    if (v != null) {
                        targets.add(v);
                    }
                }
                model.setTopLevelOnly(false);
            }
        }
        return targets;
    }

    @Override
    public void resetView() {
        System.out.println("RESET Troops frame");
        TroopsManager.getSingleton().addManagerListener(this);
        generateTroopTabs();
    }

    // <editor-fold defaultstate="collapsed" desc="Gesture handling">
    @Override
    public void fireExportAsBBGestureEvent() {
        TabInterface tab = getActiveTab();
        if (tab != null) {
            tab.transferSelection(TroopTableTab.TRANSFER_TYPE.CLIPBOARD_BB);
        }
    }

    @Override
    public void fireNextPageGestureEvent() {
        int current = jTroopsTabPane.getSelectedIndex();
        int size = jTroopsTabPane.getTabCount();
        if (current + 1 > size - 1) {
            current = 0;
        } else {
            current += 1;
        }
        jTroopsTabPane.setSelectedIndex(current);
    }

    @Override
    public void firePreviousPageGestureEvent() {
        int current = jTroopsTabPane.getSelectedIndex();
        int size = jTroopsTabPane.getTabCount();
        if (current - 1 < 0) {
            current = size - 1;
        } else {
            current -= 1;
        }
        jTroopsTabPane.setSelectedIndex(current);
    }
// </editor-fold>

    public static void main(String[] args) {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new org.apache.log4j.PatternLayout("%d - %-5p - %-20c (%C [%L]) - %m%n")));

        MouseGestures mMouseGestures = new MouseGestures();
        mMouseGestures.setMouseButton(MouseEvent.BUTTON3_MASK);
        mMouseGestures.addMouseGesturesListener(new MouseGestureHandler());
        mMouseGestures.start();
        try {
            //  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }
        GlobalOptions.setSelectedServer("de43");
        DataHolder.getSingleton().loadData(false);

        TagManager.getSingleton().addTag("Test1");
        TagManager.getSingleton().addTag("Test2");
        List<Village> used = new LinkedList<Village>();
        for (int i = 0; i < 1000; i++) {
            Village v = DataHolder.getSingleton().getRandomVillage();
            if (!used.contains(v)) {
                used.add(v);
                if (Math.random() > .5) {
                    TagManager.getSingleton().getTagByName("Test1").tagVillage(v.getId());
                } else {
                    TagManager.getSingleton().getTagByName("Test2").tagVillage(v.getId());
                }
                VillageTroopsHolder h = new VillageTroopsHolder(v, new Date());
                Hashtable<UnitHolder, Integer> troops = new Hashtable<UnitHolder, Integer>();
                for (UnitHolder ho : DataHolder.getSingleton().getUnits()) {
                    troops.put(ho, 1000);
                }

                h.setTroops(troops);
                TroopsManager.getSingleton().addManagedElement(h);
            }
        }


        for (int i = 0; i < 10; i++) {
            Village v = DataHolder.getSingleton().getRandomVillage();
            SupportVillageTroopsHolder supp = new SupportVillageTroopsHolder(v, new Date());
            for (int j = 0; j < 10; j++) {
                Village vsource = DataHolder.getSingleton().getRandomVillage();
                Hashtable<UnitHolder, Integer> troops = new Hashtable<UnitHolder, Integer>();
                for (UnitHolder ho : DataHolder.getSingleton().getUnits()) {
                    troops.put(ho, 50);
                }
                supp.addIncomingSupport(vsource, troops);
            }
            TroopsManager.getSingleton().addManagedElement(TroopsManager.SUPPORT_GROUP, supp);
        }
        for (int i = 0; i < 10; i++) {
            Village v = DataHolder.getSingleton().getRandomVillage();
            SupportVillageTroopsHolder supp = new SupportVillageTroopsHolder(v, new Date());
            for (int j = 0; j < 10; j++) {
                Village vsource = DataHolder.getSingleton().getRandomVillage();
                Hashtable<UnitHolder, Integer> troops = new Hashtable<UnitHolder, Integer>();
                for (UnitHolder ho : DataHolder.getSingleton().getUnits()) {
                    troops.put(ho, 50);
                }
                supp.addOutgoingSupport(vsource, troops);
            }
            TroopsManager.getSingleton().addManagedElement(TroopsManager.SUPPORT_GROUP, supp);
        }
        DSWorkbenchTroopsFrame.getSingleton().resetView();
        DSWorkbenchTroopsFrame.getSingleton().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DSWorkbenchTroopsFrame.getSingleton().setVisible(true);

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JButton jButton12;
    private javax.swing.JCheckBox jFilterRows;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JCheckBox jRelationType1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JCheckBox jTroopsInformationAlwaysOnTop;
    private javax.swing.JPanel jTroopsPanel;
    private com.jidesoft.swing.JideTabbedPane jTroopsTabPane;
    private org.jdesktop.swingx.JXList jXGroupsList;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jXTroopsPanel;
    private org.jdesktop.swingx.JXPanel jxSearchPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dataChangedEvent() {
        generateTroopTabs();
    }

    @Override
    public void dataChangedEvent(String pGroup) {
        TabInterface tab = getActiveTab();
        if (tab != null) {
            tab.updateSet();
        }
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
    }
}
