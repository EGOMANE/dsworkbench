/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AttackSourcePanel.java
 *
 * Created on Oct 15, 2011, 9:54:36 AM
 */
package de.tor.tribes.ui.wiz.tap;

import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;
import de.tor.tribes.control.ManageableType;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.windows.TroopFilterDialog;
import de.tor.tribes.ui.components.VillageOverviewMapPanel;
import de.tor.tribes.ui.models.TAPSourceFilterTableModel;
import de.tor.tribes.ui.wiz.dep.DefenseCalculationSettingsPanel;
import de.tor.tribes.ui.wiz.tap.types.TAPAttackSourceElement;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.UIHelper;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPanel;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 *
 * @author Torridity
 */
public class AttackSourceFilterPanel extends javax.swing.JPanel implements WizardPanel {

    private static final String GENERAL_INFO = "Du befindest dich in der Filterauswahl. Hier kannst du vorher gew&auml;hlte Herkunftsd&ouml;rfer herausfiltern, "
            + "wenn sie nicht bestimmten Kriterien entsprechen. M&ouml;gliche Filterkriterien sind:"
            + "<ul> <li>D&ouml;rfer werden bereits in einem Angriffsplan verwendet</li> "
            + "<li>D&ouml;rfer geh&ouml;ren nicht dem momentan aktiven Spieler. Diese Option dient dazu, D&ouml;rfer zu entfernen, die aufgrund ihrer Gruppenzugeh&ouml;rigkeit in die Auswahl gelangt sind, zu entfernen.</li>"
            + "<li>Die Anzahl der belegten Bauernhofpl&auml;tze in einem Dorf ist kleiner als ein bestimmter Wert</li>"
            + "<li>Die im Dorf stationierten Truppen entsprechen nicht bestimmten Vorgaben.</li>"
            + "</ul> "
            + "Nach einer &Auml;nderung der Filtereinstellungen muss die Filterung aktualisiert werden. "
            + "Herausgefilterte D&ouml;rfer sind dann in der Tabelle markiert. Unter der Tabelle siehst du die genaue Anzahl der D&ouml;rfer, die herausgefiltert wurden."
            + "</html>";
    private static AttackSourceFilterPanel singleton = null;
    private WizardController controller = null;
    private TroopFilterDialog troopFilterDialog = null;
    private VillageOverviewMapPanel overviewPanel = null;

    public static synchronized AttackSourceFilterPanel getSingleton() {
        if (singleton == null) {
            singleton = new AttackSourceFilterPanel();
        }
        return singleton;
    }

    /**
     * Creates new form AttackSourcePanel
     */
    AttackSourceFilterPanel() {
        initComponents();
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        jVillageTable.setModel(new TAPSourceFilterTableModel());
        jVillageTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jInfoTextPane.setText(GENERAL_INFO);

        jideSplitPane1.setOrientation(JideSplitPane.VERTICAL_SPLIT);
        jideSplitPane1.setProportionalLayout(true);
        jideSplitPane1.setDividerSize(5);
        jideSplitPane1.setShowGripper(true);
        jideSplitPane1.setOneTouchExpandable(true);
        jideSplitPane1.setDividerStepSize(10);
        jideSplitPane1.setInitiallyEven(true);
        jideSplitPane1.add(jFilterPanel, JideBoxLayout.FLEXIBLE);
        jideSplitPane1.add(jVillagePanel, JideBoxLayout.VARY);
        jideSplitPane1.getDividerAt(0).addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    jideSplitPane1.setProportions(new double[]{0.5});
                }
            }
        });
        troopFilterDialog = new TroopFilterDialog(new JFrame(), true);
        updateFilterPanel(new LinkedList<TAPAttackSourceElement>());
        overviewPanel = new VillageOverviewMapPanel();
        jPanel2.add(overviewPanel, BorderLayout.CENTER);
    }

    public void setController(WizardController pWizCtrl) {
        controller = pWizCtrl;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jInfoScrollPane = new javax.swing.JScrollPane();
        jInfoTextPane = new javax.swing.JTextPane();
        jFilterPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jAttackPlanList = new org.jdesktop.swingx.JXList();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPlayerVillagesOnly = new javax.swing.JCheckBox();
        jFarmSpace = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTroopFilterButton = new javax.swing.JButton();
        jVillagePanel = new javax.swing.JPanel();
        jTableScrollPane = new javax.swing.JScrollPane();
        jVillageTable = new org.jdesktop.swingx.JXTable();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jLabel1 = new javax.swing.JLabel();
        jideSplitPane1 = new com.jidesoft.swing.JideSplitPane();

        jInfoScrollPane.setMinimumSize(new java.awt.Dimension(19, 180));
        jInfoScrollPane.setPreferredSize(new java.awt.Dimension(19, 180));

        jInfoTextPane.setContentType("text/html");
        jInfoTextPane.setEditable(false);
        jInfoTextPane.setText("<html>Du befindest dich im <b>Angriffsmodus</b>. Hier kannst du die Herkunftsd&ouml;rfer ausw&auml;hlen, die f&uuml;r Angriffe verwendet werden d&uuml;rfen. Hierf&uuml;r hast die folgenden M&ouml;glichkeiten:\n<ul>\n<li>Einf&uuml;gen von Dorfkoordinaten aus der Zwischenablage per STRG+V</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus der Gruppen&uuml;bersicht</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus dem SOS-Analyzer</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus Berichten</li>\n<li>Einf&uuml;gen aus der Auswahlübersicht</li>\n<li>Manuelle Eingabe</li>\n</ul>\n</html>\n");
        jInfoScrollPane.setViewportView(jInfoTextPane);

        jFilterPanel.setPreferredSize(new java.awt.Dimension(389, 300));
        jFilterPanel.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Angriffspläne"));
        jPanel3.setMinimumSize(new java.awt.Dimension(160, 88));
        jPanel3.setPreferredSize(new java.awt.Dimension(160, 195));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jAttackPlanList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jAttackPlanList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jScrollPane3, gridBagConstraints);

        jButton1.setText("Keinen auswählen");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireRemoveAttackPlanSelectionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jFilterPanel.add(jPanel3, gridBagConstraints);

        jButton3.setText("Filterung aktualisieren");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireUpdateFilterEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 17, 5);
        jFilterPanel.add(jButton3, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Truppen & Sonstiges"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPlayerVillagesOnly.setSelected(true);
        jPlayerVillagesOnly.setText("Nur Spielerdörfer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPlayerVillagesOnly, gridBagConstraints);

        jFarmSpace.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jFarmSpace, gridBagConstraints);

        jLabel3.setText("Min. Bauernhofplätze");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Truppenstärke");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel4, gridBagConstraints);

        jTroopFilterButton.setBackground(new java.awt.Color(255, 51, 51));
        jTroopFilterButton.setText("Inaktiv (klicken)");
        jTroopFilterButton.setMaximumSize(new java.awt.Dimension(120, 23));
        jTroopFilterButton.setMinimumSize(new java.awt.Dimension(120, 23));
        jTroopFilterButton.setPreferredSize(new java.awt.Dimension(120, 23));
        jTroopFilterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireShowTroopFilterEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jTroopFilterButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jFilterPanel.add(jPanel1, gridBagConstraints);

        jVillagePanel.setLayout(new java.awt.GridBagLayout());

        jTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Gefilterte Dörfer"));

        jVillageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableScrollPane.setViewportView(jVillageTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillagePanel.add(jTableScrollPane, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("0 Dörfer werden ignoriert");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillagePanel.add(jLabel2, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 5, 5);
        jVillagePanel.add(jPanel2, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search.png"))); // NOI18N
        jToggleButton1.setToolTipText("Informationskarte vergrößern");
        jToggleButton1.setMaximumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(100, 23));
        jToggleButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireViewStateChangeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillagePanel.add(jToggleButton1, gridBagConstraints);

        setPreferredSize(new java.awt.Dimension(600, 600));
        setLayout(new java.awt.GridBagLayout());

        jXCollapsiblePane1.setCollapsed(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jXCollapsiblePane1, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Informationen einblenden");
        jLabel1.setToolTipText("Blendet Informationen zu dieser Ansicht und zu den Datenquellen ein/aus");
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideInfoEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jideSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void fireHideInfoEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideInfoEvent
        if (jXCollapsiblePane1.isCollapsed()) {
            jXCollapsiblePane1.setCollapsed(false);
            jLabel1.setText("Informationen ausblenden");
        } else {
            jXCollapsiblePane1.setCollapsed(true);
            jLabel1.setText("Informationen einblenden");
        }
    }//GEN-LAST:event_fireHideInfoEvent

    private void fireRemoveAttackPlanSelectionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveAttackPlanSelectionEvent
        jAttackPlanList.getSelectionModel().clearSelection();
    }//GEN-LAST:event_fireRemoveAttackPlanSelectionEvent

    private void fireUpdateFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUpdateFilterEvent
        updateFilters();
    }//GEN-LAST:event_fireUpdateFilterEvent

    private void fireShowTroopFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireShowTroopFilterEvent
        if (troopFilterDialog.showDialog()) {
            jTroopFilterButton.setBackground(Color.GREEN);
            jTroopFilterButton.setText("Aktiv");
        } else {
            jTroopFilterButton.setBackground(Color.RED);
            jTroopFilterButton.setText("Inaktiv (klicken)");
        }
    }//GEN-LAST:event_fireShowTroopFilterEvent

    private void fireViewStateChangeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireViewStateChangeEvent
        if (jToggleButton1.isSelected()) {
            overviewPanel.setOptimalSize();
            jTableScrollPane.setViewportView(overviewPanel);
            jPanel2.remove(overviewPanel);
        } else {
            jTableScrollPane.setViewportView(jVillageTable);
            jPanel2.add(overviewPanel, BorderLayout.CENTER);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    jPanel2.updateUI();
                }
            });
        }
    }//GEN-LAST:event_fireViewStateChangeEvent

    private void updateFilters() {
        List<TAPAttackSourceElement> elements = getAllElements();
        filterMisc(elements);
        filterByAttackPlans(elements);
        filterTroops(elements);
        updateFilterPanel(elements);
        updateVillageOverview();
        getModel().fireTableDataChanged();
    }

    private void filterByAttackPlans(List<TAPAttackSourceElement> pAllElements) {
        Object[] selection = jAttackPlanList.getSelectedValues();
        if (selection.length == 0) {
            for (TAPAttackSourceElement element : pAllElements) {
                element.setIgnored(false);
            }
            return;
        } else {
            List<String> groups = new ArrayList<String>();
            for (Object o : selection) {
                groups.add((String) o);
            }

            List<ManageableType> attacks = AttackManager.getSingleton().getAllElements(groups);
            for (ManageableType type : attacks) {
                Attack a = (Attack) type;
                for (TAPAttackSourceElement element : pAllElements) {
                    if (a.getSource().equals(element.getVillage())) {
                        element.setIgnored(true);
                    }
                }
            }
        }
    }

    private void filterTroops(List<TAPAttackSourceElement> pAllElements) {
        //filter by farm space
        int requiredTroopAmount = UIHelper.parseIntFromField(jFarmSpace, 0);
        if (requiredTroopAmount > 0) {
            for (TAPAttackSourceElement element : pAllElements) {
                VillageTroopsHolder troopsForVillage = TroopsManager.getSingleton().getTroopsForVillage(element.getVillage(), TroopsManager.TROOP_TYPE.OWN);
                if (troopsForVillage != null) {
                    element.setIgnored(troopsForVillage.getTroopPopCount() < requiredTroopAmount);
                } else {
                    element.setIgnored(requiredTroopAmount > 0);
                }
            }
        }
        //filter single amounts
        if (troopFilterDialog.canFilter()) {
            for (TAPAttackSourceElement elem : pAllElements) {
                if (troopFilterDialog.getIgnoredVillages(new Village[]{elem.getVillage()}).length != 0) {
                    elem.setIgnored(true);
                }
            }
        }
    }

    private void filterMisc(List<TAPAttackSourceElement> pAllElements) {
        Tribe t = GlobalOptions.getSelectedProfile().getTribe();
        for (TAPAttackSourceElement elem : pAllElements) {
            if (jPlayerVillagesOnly.isSelected()) {
                elem.setIgnored(!elem.getVillage().getTribe().equals(t));
            } else {
                elem.setIgnored(true);
            }
        }
    }

    private void updateVillageOverview() {
        overviewPanel.reset();
        List<TAPAttackSourceElement> elements = getAllElements();
        for (TAPAttackSourceElement element : elements) {
            if (!element.isIgnored()) {
                overviewPanel.addVillage(new Point(element.getVillage().getX(), element.getVillage().getY()), Color.yellow);
            }
        }
        overviewPanel.repaint();
    }

    private TAPSourceFilterTableModel getModel() {
        return (TAPSourceFilterTableModel) jVillageTable.getModel();
    }

    protected void setup() {
        TAPAttackSourceElement[] elements = AttackSourcePanel.getSingleton().getAllElements();
        getModel().clear();
        overviewPanel.reset();
        for (TAPAttackSourceElement element : elements) {
            getModel().addRow(element, false);
            if (!element.isIgnored()) {
                overviewPanel.addVillage(new Point(element.getVillage().getX(), element.getVillage().getY()), Color.yellow);
            }
        }
        getModel().fireTableDataChanged();
        overviewPanel.repaint();
    }

    protected void updateFilterPanel(List<TAPAttackSourceElement> pAllElements) {
        DefaultListModel attackModel = new DefaultListModel();
        for (String plan : AttackManager.getSingleton().getGroups()) {
            attackModel.addElement(plan);
        }
        jAttackPlanList.setModel(attackModel);

        int ignoreCount = 0;
        for (TAPAttackSourceElement elem : pAllElements) {
            if (elem.isIgnored()) {
                ignoreCount++;
            }
        }

        if (controller != null) {
            if (ignoreCount == pAllElements.size()) {
                controller.setProblem("Alle Dörfer werden ignoriert");
            } else {
                controller.setProblem(null);
            }
        }
        jLabel2.setText(ignoreCount + " Dörfer werden ignoriert");
    }

    public TAPAttackSourceElement[] getFilteredElements() {
        List<TAPAttackSourceElement> filtered = new LinkedList<TAPAttackSourceElement>();
        TAPSourceFilterTableModel model = getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            TAPAttackSourceElement elem = model.getRow(i);
            if (!elem.isIgnored()) {
                filtered.add(model.getRow(i));
            }
        }
        return filtered.toArray(new TAPAttackSourceElement[filtered.size()]);
    }

    public List<TAPAttackSourceElement> getAllElements() {
        List<TAPAttackSourceElement> elements = new LinkedList<TAPAttackSourceElement>();
        TAPSourceFilterTableModel model = getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            elements.add(model.getRow(i));
        }
        return elements;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXList jAttackPlanList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JTextField jFarmSpace;
    private javax.swing.JPanel jFilterPanel;
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JCheckBox jPlayerVillagesOnly;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jTableScrollPane;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton jTroopFilterButton;
    private javax.swing.JPanel jVillagePanel;
    private org.jdesktop.swingx.JXTable jVillageTable;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    private com.jidesoft.swing.JideSplitPane jideSplitPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String string, Map map, Wizard wizard) {
        if (getFilteredElements().length == 0) {
            controller.setProblem("Alle Dörfer werden ignoriert");
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }

        if (getModel().getRowCount() > 0) {
            AttackTargetPanel.getSingleton().updateOverview();
        }

        DefenseCalculationSettingsPanel.getSingleton().update();
        return WizardPanelNavResult.PROCEED;
    }

    @Override
    public WizardPanelNavResult allowBack(String string, Map map, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }

    @Override
    public WizardPanelNavResult allowFinish(String string, Map map, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }
}

