/*
 * AllyAllyAttackFrame.java
 *
 * Created on 29. Juli 2008, 11:17
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.AbstractTroopMovement;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.algo.SettingsPanel;
import de.tor.tribes.ui.editors.AttackTypeCellEditor;
import de.tor.tribes.ui.editors.FakeCellEditor;
import de.tor.tribes.ui.editors.UnitCellEditor;
import de.tor.tribes.ui.renderer.AttackTypeCellRenderer;
import de.tor.tribes.ui.renderer.FakeCellRenderer;
import de.tor.tribes.ui.renderer.TribeCellRenderer;
import de.tor.tribes.ui.renderer.UnitCellRenderer;
import de.tor.tribes.ui.renderer.UnitListCellRenderer;
import de.tor.tribes.ui.renderer.VillageCellRenderer;
import de.tor.tribes.util.AttackToBBCodeFormater;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.ServerSettings;
import de.tor.tribes.util.algo.AbstractAttackAlgorithm;
import de.tor.tribes.util.algo.AlgorithmListener;
import de.tor.tribes.util.algo.BruteForce;
import de.tor.tribes.util.algo.Iterix;
import de.tor.tribes.util.algo.TimeFrame;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.parser.VillageParser;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import de.tor.tribes.util.tag.TagManager;
import de.tor.tribes.util.troops.TroopsManager;
import java.awt.Color;
import java.util.StringTokenizer;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Collections;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

/**
 * @TODO (2.0) Fixed start time, arbitrary arrive time not in night bonus?
 * @author Jejkal
 */
public class TribeTribeAttackFrame extends javax.swing.JFrame implements AlgorithmListener {

    private static Logger logger = Logger.getLogger("AttackPlanner");
    private SettingsPanel mSettingsPanel = null;
    //private MiscSettingsPanel mMiscPanel = null;
    private JButton filterSource = null;

    /** Creates new form TribeTribeAttackFrame */
    public TribeTribeAttackFrame() {
        initComponents();
        mSettingsPanel = new SettingsPanel();
        // mMiscPanel = new MiscSettingsPanel();
        jTabbedPane1.addTab("Einstellungen", new ImageIcon(this.getClass().getResource("/res/settings.png")), mSettingsPanel);
        getContentPane().setBackground(Constants.DS_BACK);
        jTransferToAttackManagerDialog.pack();
        jAttackResultDetailsFrame.pack();
        jTargetResultDetailsFrame.pack();

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelp(jSourcePanel, "pages.attack_planer_source", GlobalOptions.getHelpBroker().getHelpSet());
        GlobalOptions.getHelpBroker().enableHelp(jTargetPanel, "pages.attack_planer_target", GlobalOptions.getHelpBroker().getHelpSet());
        GlobalOptions.getHelpBroker().enableHelp(mSettingsPanel, "pages.attack_planer_settings", GlobalOptions.getHelpBroker().getHelpSet());

        GlobalOptions.getHelpBroker().enableHelpKey(jResultFrame.getRootPane(), "pages.attack_planer_results", GlobalOptions.getHelpBroker().getHelpSet());
        GlobalOptions.getHelpBroker().enableHelpKey(jTargetResultDetailsFrame.getRootPane(), "pages.attack_planer_results_details_targets", GlobalOptions.getHelpBroker().getHelpSet());
        GlobalOptions.getHelpBroker().enableHelpKey(jAttackResultDetailsFrame.getRootPane(), "pages.attack_planer_results_details_sources", GlobalOptions.getHelpBroker().getHelpSet());
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.attack_planer", GlobalOptions.getHelpBroker().getHelpSet());
        // </editor-fold>
    }

    /**Setup attack frame (clear entries, fill lists and set initial values)*/
    protected void setup() {

        // <editor-fold defaultstate="collapsed" desc=" Attack table setup ">

        DefaultTableModel attackModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Herkunft", "Einheit", "Fake"
                }) {

            Class[] types = new Class[]{
                Village.class, UnitHolder.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        jAttacksTable.setModel(attackModel);
        TableRowSorter<TableModel> attackSorter = new TableRowSorter<TableModel>(jAttacksTable.getModel());
        jAttacksTable.setRowSorter(attackSorter);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Victim table setup ">

        DefaultTableModel victimModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Spieler", "Dorf"
                }) {

            Class[] types = new Class[]{
                Tribe.class, Village.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        jVictimTable.setModel(victimModel);
        TableRowSorter<TableModel> victimSorter = new TableRowSorter<TableModel>(jVictimTable.getModel());
        jVictimTable.setRowSorter(victimSorter);

        // </editor-fold>        

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                c.setBackground(Constants.DS_BACK);
                return c;
            }
        };

        for (int i = 0; i < jAttacksTable.getColumnCount(); i++) {
            jAttacksTable.getColumn(jAttacksTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        for (int i = 0; i < jVictimTable.getColumnCount(); i++) {
            jVictimTable.getColumn(jVictimTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        for (int i = 0; i < jResultsTable.getColumnCount(); i++) {
            jResultsTable.getColumn(jResultsTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        jAttacksTable.getColumn("Einheit").setWidth(60);
        jAttacksTable.getColumn("Einheit").setPreferredWidth(60);
        jAttacksTable.getColumn("Einheit").setMaxWidth(60);
        jAttacksTable.getColumn("Fake").setWidth(60);
        jAttacksTable.getColumn("Fake").setPreferredWidth(60);
        jAttacksTable.getColumn("Fake").setMaxWidth(60);
        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jScrollPane2.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jScrollPane3.getViewport().setBackground(Constants.DS_BACK_LIGHT);

        jAttackPlanSelectionDialog.getContentPane().setBackground(Constants.DS_BACK_LIGHT);
        jAttackPlanSelectionDialog.pack();

        // <editor-fold defaultstate="collapsed" desc="Build filter dialog">
        jFilterUnitBox.setModel(new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{})));
        jFilterUnitBox.setRenderer(new UnitListCellRenderer());
        jFilterList.setModel(new DefaultListModel());
// </editor-fold>
        try {

            // <editor-fold defaultstate="collapsed" desc=" Build target allies list ">
            fireTargetAllyFilterChangedEvent(null);

            jTargetTribeList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    fireFilterTargetByTribeEvent();
                }
            });

            jTargetContinentList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    fireFilterTargetByContinentEvent();
                }
            });
            //select first ally and initialize all lists
            jTargetAllyList.setSelectedIndex(0);

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Build user village list ">
            Tag[] tags = TagManager.getSingleton().getTags().toArray(new Tag[]{});
            DefaultListModel tagModel = new DefaultListModel();
            tagModel.addElement("Keinen Tag");
            for (Tag t : tags) {
                tagModel.addElement(t);
            }
            jVillageGroupList.setModel(tagModel);

            jVillageGroupList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    fireFilterSourceVillagesByGroupEvent();
                }
            });

            jSourceContinentList.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    fireFilterSourceContinentEvent();
                }
            });

            if (TagManager.getSingleton().getTags().isEmpty()) {
                jVillageGroupList.setEnabled(false);
            } else {
                jVillageGroupList.setEnabled(true);
            }
            //select all groups and initialize lists
            jVillageGroupList.getSelectionModel().setSelectionInterval(0, (tags.length > 0) ? tags.length : tags.length);
            // </editor-fold>

            mSettingsPanel.reset();
            /* jAttacksTable.setDefaultRenderer(Date.class, new DateCellRenderer());
            jAttacksTable.setDefaultEditor(Date.class, new DateSpinEditor());*/
            jAttacksTable.setDefaultEditor(UnitHolder.class, new UnitCellEditor());
            jAttacksTable.setDefaultRenderer(UnitHolder.class, new UnitCellRenderer());
            jAttacksTable.setDefaultEditor(Boolean.class, new FakeCellEditor());
            jAttacksTable.setDefaultRenderer(Boolean.class, new FakeCellRenderer());
            jAttacksTable.setDefaultRenderer(Village.class, new VillageCellRenderer());

            jAttacksTable.setRowHeight(20);
            jVictimTable.setRowHeight(20);
            jVictimTable.setDefaultRenderer(Tribe.class, new TribeCellRenderer());
            jVictimTable.setDefaultRenderer(Village.class, new VillageCellRenderer());
            DefaultComboBoxModel unitModel = new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{}));
            jTroopsList.setModel(unitModel);
            jTroopsList.setSelectedItem(DataHolder.getSingleton().getUnitByPlainName("ram"));
            jResultFrame.pack();
        } catch (Exception e) {
            logger.error("Failed to initialize TribeAttackFrame", e);
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

        jResultFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTargetsBar = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        jEnoblementsBar = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jFullOffsBar = new javax.swing.JProgressBar();
        jAttacksBar = new javax.swing.JProgressBar();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jResultsTable = new javax.swing.JTable();
        jCloseResultsButton = new javax.swing.JButton();
        jCopyToClipboardAsBBButton = new javax.swing.JButton();
        jAddToAttacksButton = new javax.swing.JButton();
        jCopyToClipboardButton = new javax.swing.JButton();
        jTransferToAttackManagerDialog = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jAttackPlansBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jNewPlanName = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        attackTypeGroup = new javax.swing.ButtonGroup();
        jAttackResultDetailsFrame = new javax.swing.JFrame();
        jHideAttackDetailsButton = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        jNotAssignedSourcesTable = new javax.swing.JTable();
        jTargetResultDetailsFrame = new javax.swing.JFrame();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTargetDetailsTable = new javax.swing.JTable();
        jHideTargetDetailsButton = new javax.swing.JButton();
        jAttackPlanSelectionDialog = new javax.swing.JDialog();
        jScrollPane13 = new javax.swing.JScrollPane();
        jAttackPlanTable = new javax.swing.JTable();
        jDoSyncButton = new javax.swing.JButton();
        jCancelSyncButton = new javax.swing.JButton();
        jFilterFrame = new javax.swing.JFrame();
        jScrollPane14 = new javax.swing.JScrollPane();
        jFilterList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jFilterUnitBox = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jMinValue = new javax.swing.JTextField();
        jMaxValue = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jButton18 = new javax.swing.JButton();
        jApplyFiltersButton = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jCalculateButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jSourcePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttacksTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSourceVillageLabel1 = new javax.swing.JLabel();
        jTroopsList = new javax.swing.JComboBox();
        jSourceVillageLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jVillageGroupList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jSourceContinentList = new javax.swing.JList();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jSourceVillageList = new javax.swing.JList();
        jLabel22 = new javax.swing.JLabel();
        jSourceGroupRelation = new javax.swing.JRadioButton();
        jSelectionStart = new javax.swing.JTextField();
        jSelectionEnd = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jSelectionBeginButton = new javax.swing.JButton();
        jPrevSelectionButton = new javax.swing.JButton();
        jNextSelectionButton = new javax.swing.JButton();
        jSelectionEndButton = new javax.swing.JButton();
        jSelectButton = new javax.swing.JButton();
        jMarkAsFakeBox = new javax.swing.JCheckBox();
        jPlayerSourcesOnlyBox = new javax.swing.JCheckBox();
        jButton8 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jSetFakeButton = new javax.swing.JButton();
        jSetNoFakeButton = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jFilterSourceButton = new javax.swing.JButton();
        jTargetPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jTargetAllyLabel = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTargetAllyList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTargetTribeList = new javax.swing.JList();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTargetContinentList = new javax.swing.JList();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTargetVillageList = new javax.swing.JList();
        jLabel11 = new javax.swing.JLabel();
        jTargetTribeFilter = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jVictimTable = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jFilterTargetButton = new javax.swing.JButton();
        jCalculatingProgressBar = new javax.swing.JProgressBar();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        jResultFrame.setTitle(bundle.getString("TribeTribeAttackFrame.jResultFrame.title")); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/target.png"))); // NOI18N
        jLabel6.setText(bundle.getString("TribeTribeAttackFrame.jLabel6.text")); // NOI18N
        jLabel6.setMaximumSize(new java.awt.Dimension(18, 18));
        jLabel6.setMinimumSize(new java.awt.Dimension(18, 18));
        jLabel6.setPreferredSize(new java.awt.Dimension(18, 18));

        jTargetsBar.setBackground(new java.awt.Color(255, 255, 51));
        jTargetsBar.setForeground(new java.awt.Color(51, 153, 0));
        jTargetsBar.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTargetsBar.toolTipText")); // NOI18N
        jTargetsBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTargetsBar.setStringPainted(true);
        jTargetsBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireShowResultDetailsEvent(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/snob.png"))); // NOI18N
        jLabel7.setText(bundle.getString("TribeTribeAttackFrame.jLabel7.text")); // NOI18N

        jEnoblementsBar.setBackground(new java.awt.Color(255, 0, 0));
        jEnoblementsBar.setForeground(new java.awt.Color(51, 153, 0));
        jEnoblementsBar.setMaximum(0);
        jEnoblementsBar.setToolTipText(bundle.getString("TribeTribeAttackFrame.jEnoblementsBar.toolTipText")); // NOI18N
        jEnoblementsBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jEnoblementsBar.setStringPainted(true);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/ram.png"))); // NOI18N
        jLabel9.setText(bundle.getString("TribeTribeAttackFrame.jLabel9.text")); // NOI18N

        jFullOffsBar.setBackground(new java.awt.Color(255, 255, 51));
        jFullOffsBar.setForeground(new java.awt.Color(51, 153, 0));
        jFullOffsBar.setToolTipText(bundle.getString("TribeTribeAttackFrame.jFullOffsBar.toolTipText")); // NOI18N
        jFullOffsBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jFullOffsBar.setStringPainted(true);

        jAttacksBar.setBackground(new java.awt.Color(255, 0, 0));
        jAttacksBar.setForeground(new java.awt.Color(51, 153, 0));
        jAttacksBar.setToolTipText(bundle.getString("TribeTribeAttackFrame.jAttacksBar.toolTipText")); // NOI18N
        jAttacksBar.setValue(50);
        jAttacksBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jAttacksBar.setStringPainted(true);
        jAttacksBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireShowResultDetailsEvent(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/barracks.png"))); // NOI18N
        jLabel10.setText(bundle.getString("TribeTribeAttackFrame.jLabel10.text")); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(bundle.getString("TribeTribeAttackFrame.jLabel5.text")); // NOI18N

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(bundle.getString("TribeTribeAttackFrame.jLabel8.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jTargetsBar, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(jAttacksBar, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(jEnoblementsBar, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(jFullOffsBar, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(2, 2, 2)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTargetsBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jAttacksBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEnoblementsBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFullOffsBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jResultsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jResultsTable.setOpaque(false);
        jScrollPane2.setViewportView(jResultsTable);

        jCloseResultsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_remove.png"))); // NOI18N
        jCloseResultsButton.setText(bundle.getString("TribeTribeAttackFrame.jCloseResultsButton.text")); // NOI18N
        jCloseResultsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideResultsEvent(evt);
            }
        });

        jCopyToClipboardAsBBButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_clipboardBB.png"))); // NOI18N
        jCopyToClipboardAsBBButton.setText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardAsBBButton.text")); // NOI18N
        jCopyToClipboardAsBBButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardAsBBButton.toolTipText")); // NOI18N
        jCopyToClipboardAsBBButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAttacksToClipboardEvent(evt);
            }
        });

        jAddToAttacksButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_overview.png"))); // NOI18N
        jAddToAttacksButton.setText(bundle.getString("TribeTribeAttackFrame.jAddToAttacksButton.text")); // NOI18N
        jAddToAttacksButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jAddToAttacksButton.toolTipText")); // NOI18N
        jAddToAttacksButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTransferToAttackPlanningEvent(evt);
            }
        });

        jCopyToClipboardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_clipboard.png"))); // NOI18N
        jCopyToClipboardButton.setText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardButton.text")); // NOI18N
        jCopyToClipboardButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardButton.toolTipText")); // NOI18N
        jCopyToClipboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUnformattedAttacksToClipboardEvent(evt);
            }
        });

        javax.swing.GroupLayout jResultFrameLayout = new javax.swing.GroupLayout(jResultFrame.getContentPane());
        jResultFrame.getContentPane().setLayout(jResultFrameLayout);
        jResultFrameLayout.setHorizontalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap(403, Short.MAX_VALUE)
                .addComponent(jAddToAttacksButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCopyToClipboardButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCopyToClipboardAsBBButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCloseResultsButton)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 697, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jResultFrameLayout.setVerticalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCloseResultsButton)
                    .addComponent(jCopyToClipboardAsBBButton)
                    .addComponent(jCopyToClipboardButton)
                    .addComponent(jAddToAttacksButton))
                .addContainerGap())
        );

        jTransferToAttackManagerDialog.setTitle(bundle.getString("TribeTribeAttackFrame.jTransferToAttackManagerDialog.title")); // NOI18N
        jTransferToAttackManagerDialog.setAlwaysOnTop(true);
        jTransferToAttackManagerDialog.setModal(true);

        jLabel2.setText(bundle.getString("TribeTribeAttackFrame.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("TribeTribeAttackFrame.jLabel3.text")); // NOI18N

        jNewPlanName.setText(bundle.getString("TribeTribeAttackFrame.jNewPlanName.text")); // NOI18N

        jButton5.setText(bundle.getString("TribeTribeAttackFrame.jButton5.text")); // NOI18N
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTransferAttacksToPlanEvent(evt);
            }
        });

        jButton6.setText(bundle.getString("TribeTribeAttackFrame.jButton6.text")); // NOI18N
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCancelTransferEvent(evt);
            }
        });

        javax.swing.GroupLayout jTransferToAttackManagerDialogLayout = new javax.swing.GroupLayout(jTransferToAttackManagerDialog.getContentPane());
        jTransferToAttackManagerDialog.getContentPane().setLayout(jTransferToAttackManagerDialogLayout);
        jTransferToAttackManagerDialogLayout.setHorizontalGroup(
            jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTransferToAttackManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jTransferToAttackManagerDialogLayout.createSequentialGroup()
                        .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jAttackPlansBox, 0, 267, Short.MAX_VALUE)
                            .addComponent(jNewPlanName, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTransferToAttackManagerDialogLayout.createSequentialGroup()
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)))
                .addContainerGap())
        );
        jTransferToAttackManagerDialogLayout.setVerticalGroup(
            jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTransferToAttackManagerDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jAttackPlansBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jNewPlanName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jTransferToAttackManagerDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTransferToAttackManagerDialog.getAccessibleContext().setAccessibleParent(null);

        jAttackResultDetailsFrame.setTitle(bundle.getString("TribeTribeAttackFrame.jAttackResultDetailsFrame.title")); // NOI18N

        jHideAttackDetailsButton.setBackground(new java.awt.Color(239, 235, 223));
        jHideAttackDetailsButton.setText(bundle.getString("TribeTribeAttackFrame.jHideAttackDetailsButton.text")); // NOI18N
        jHideAttackDetailsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideResultDetailsEvent(evt);
            }
        });

        jNotAssignedSourcesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane11.setViewportView(jNotAssignedSourcesTable);

        javax.swing.GroupLayout jAttackResultDetailsFrameLayout = new javax.swing.GroupLayout(jAttackResultDetailsFrame.getContentPane());
        jAttackResultDetailsFrame.getContentPane().setLayout(jAttackResultDetailsFrameLayout);
        jAttackResultDetailsFrameLayout.setHorizontalGroup(
            jAttackResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackResultDetailsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAttackResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jHideAttackDetailsButton)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
                .addContainerGap())
        );
        jAttackResultDetailsFrameLayout.setVerticalGroup(
            jAttackResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackResultDetailsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jHideAttackDetailsButton)
                .addContainerGap())
        );

        jTargetResultDetailsFrame.setTitle(bundle.getString("TribeTribeAttackFrame.jTargetResultDetailsFrame.title")); // NOI18N

        jTargetDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane12.setViewportView(jTargetDetailsTable);

        jHideTargetDetailsButton.setText(bundle.getString("TribeTribeAttackFrame.jHideTargetDetailsButton.text")); // NOI18N
        jHideTargetDetailsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideResultDetailsEvent(evt);
            }
        });

        javax.swing.GroupLayout jTargetResultDetailsFrameLayout = new javax.swing.GroupLayout(jTargetResultDetailsFrame.getContentPane());
        jTargetResultDetailsFrame.getContentPane().setLayout(jTargetResultDetailsFrameLayout);
        jTargetResultDetailsFrameLayout.setHorizontalGroup(
            jTargetResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTargetResultDetailsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTargetResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jHideTargetDetailsButton)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                .addContainerGap())
        );
        jTargetResultDetailsFrameLayout.setVerticalGroup(
            jTargetResultDetailsFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTargetResultDetailsFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jHideTargetDetailsButton)
                .addContainerGap())
        );

        jAttackPlanSelectionDialog.setTitle(bundle.getString("TribeTribeAttackFrame.jAttackPlanSelectionDialog.title")); // NOI18N
        jAttackPlanSelectionDialog.setAlwaysOnTop(true);
        jAttackPlanSelectionDialog.setModal(true);

        jAttackPlanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Angriffsplan", "Abgleichen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane13.setViewportView(jAttackPlanTable);

        jDoSyncButton.setBackground(new java.awt.Color(239, 235, 223));
        jDoSyncButton.setText(bundle.getString("TribeTribeAttackFrame.jDoSyncButton.text")); // NOI18N
        jDoSyncButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSynchWithAttackPlansEvent(evt);
            }
        });

        jCancelSyncButton.setBackground(new java.awt.Color(239, 235, 223));
        jCancelSyncButton.setText(bundle.getString("TribeTribeAttackFrame.jCancelSyncButton.text")); // NOI18N
        jCancelSyncButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSynchWithAttackPlansEvent(evt);
            }
        });

        javax.swing.GroupLayout jAttackPlanSelectionDialogLayout = new javax.swing.GroupLayout(jAttackPlanSelectionDialog.getContentPane());
        jAttackPlanSelectionDialog.getContentPane().setLayout(jAttackPlanSelectionDialogLayout);
        jAttackPlanSelectionDialogLayout.setHorizontalGroup(
            jAttackPlanSelectionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackPlanSelectionDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAttackPlanSelectionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jAttackPlanSelectionDialogLayout.createSequentialGroup()
                        .addComponent(jCancelSyncButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDoSyncButton))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                .addContainerGap())
        );
        jAttackPlanSelectionDialogLayout.setVerticalGroup(
            jAttackPlanSelectionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackPlanSelectionDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jAttackPlanSelectionDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDoSyncButton)
                    .addComponent(jCancelSyncButton))
                .addContainerGap())
        );

        jFilterFrame.setTitle(bundle.getString("TribeTribeAttackFrame.jFilterFrame.title")); // NOI18N

        jScrollPane14.setViewportView(jFilterList);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("TribeTribeAttackFrame.jPanel3.border.title"))); // NOI18N

        jFilterUnitBox.setMaximumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setMinimumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setPreferredSize(new java.awt.Dimension(51, 25));

        jLabel25.setText(bundle.getString("TribeTribeAttackFrame.jLabel25.text")); // NOI18N

        jLabel26.setText(bundle.getString("TribeTribeAttackFrame.jLabel26.text")); // NOI18N

        jLabel27.setText(bundle.getString("TribeTribeAttackFrame.jLabel27.text")); // NOI18N

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton17.setText(bundle.getString("TribeTribeAttackFrame.jButton17.text")); // NOI18N
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTroopFilterEvent(evt);
            }
        });

        jMinValue.setText(bundle.getString("TribeTribeAttackFrame.jMinValue.text")); // NOI18N
        jMinValue.setMaximumSize(new java.awt.Dimension(51, 20));
        jMinValue.setMinimumSize(new java.awt.Dimension(51, 20));
        jMinValue.setPreferredSize(new java.awt.Dimension(51, 20));

        jMaxValue.setText(bundle.getString("TribeTribeAttackFrame.jMaxValue.text")); // NOI18N
        jMaxValue.setMaximumSize(new java.awt.Dimension(51, 20));
        jMaxValue.setMinimumSize(new java.awt.Dimension(51, 20));
        jMaxValue.setPreferredSize(new java.awt.Dimension(51, 20));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jMinValue, 0, 0, Short.MAX_VALUE)
                            .addComponent(jFilterUnitBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
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
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jMinValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton17)
                .addContainerGap())
        );

        jLabel28.setText(bundle.getString("TribeTribeAttackFrame.jLabel28.text")); // NOI18N

        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton18.setText(bundle.getString("TribeTribeAttackFrame.jButton18.text")); // NOI18N
        jButton18.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton18.toolTipText")); // NOI18N
        jButton18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveTroopFilterEvent(evt);
            }
        });

        jApplyFiltersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/checkbox.png"))); // NOI18N
        jApplyFiltersButton.setText(bundle.getString("TribeTribeAttackFrame.jApplyFiltersButton.text")); // NOI18N
        jApplyFiltersButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });

        jButton20.setText(bundle.getString("TribeTribeAttackFrame.jButton20.text")); // NOI18N
        jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });

        javax.swing.GroupLayout jFilterFrameLayout = new javax.swing.GroupLayout(jFilterFrame.getContentPane());
        jFilterFrame.getContentPane().setLayout(jFilterFrameLayout);
        jFilterFrameLayout.setHorizontalGroup(
            jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFilterFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jFilterFrameLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFilterFrameLayout.createSequentialGroup()
                                .addComponent(jButton20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jApplyFiltersButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jFilterFrameLayout.setVerticalGroup(
            jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFilterFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(jScrollPane14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jFilterFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jApplyFiltersButton)
                    .addComponent(jButton20))
                .addContainerGap())
        );

        setTitle(bundle.getString("TribeTribeAttackFrame.title")); // NOI18N
        setBackground(new java.awt.Color(239, 235, 223));

        jCalculateButton.setBackground(new java.awt.Color(239, 235, 223));
        jCalculateButton.setText(bundle.getString("TribeTribeAttackFrame.jCalculateButton.text")); // NOI18N
        jCalculateButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCalculateButton.toolTipText")); // NOI18N
        jCalculateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCalculateAttackEvent(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(239, 235, 223));

        jSourcePanel.setBackground(new java.awt.Color(239, 235, 223));

        jAttacksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAttacksTable.setOpaque(false);
        jAttacksTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jAttacksTable);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton1.toolTipText")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(23, 23));
        jButton1.setMinimumSize(new java.awt.Dimension(23, 23));
        jButton1.setPreferredSize(new java.awt.Dimension(23, 23));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAttackEvent(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(239, 235, 223));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton3.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton3.toolTipText")); // NOI18N
        jButton3.setMaximumSize(new java.awt.Dimension(23, 23));
        jButton3.setMinimumSize(new java.awt.Dimension(23, 23));
        jButton3.setPreferredSize(new java.awt.Dimension(23, 23));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveAttackEvent(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setOpaque(false);

        jSourceVillageLabel1.setText(bundle.getString("TribeTribeAttackFrame.jSourceVillageLabel1.text")); // NOI18N

        jTroopsList.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTroopsList.toolTipText")); // NOI18N
        jTroopsList.setMaximumSize(new java.awt.Dimension(500, 20));
        jTroopsList.setMinimumSize(new java.awt.Dimension(20, 20));
        jTroopsList.setPreferredSize(new java.awt.Dimension(150, 20));

        jSourceVillageLabel2.setText(bundle.getString("TribeTribeAttackFrame.jSourceVillageLabel2.text")); // NOI18N

        jScrollPane4.setViewportView(jVillageGroupList);

        jScrollPane5.setMaximumSize(new java.awt.Dimension(60, 132));
        jScrollPane5.setMinimumSize(new java.awt.Dimension(60, 132));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(60, 132));

        jScrollPane5.setViewportView(jSourceContinentList);

        jLabel21.setText(bundle.getString("TribeTribeAttackFrame.jLabel21.text")); // NOI18N

        jScrollPane6.setPreferredSize(new java.awt.Dimension(100, 130));

        jScrollPane6.setViewportView(jSourceVillageList);

        jLabel22.setText(bundle.getString("TribeTribeAttackFrame.jLabel22.text")); // NOI18N

        jSourceGroupRelation.setSelected(true);
        jSourceGroupRelation.setText(bundle.getString("TribeTribeAttackFrame.jSourceGroupRelation.text")); // NOI18N
        jSourceGroupRelation.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSourceGroupRelation.toolTipText")); // NOI18N
        jSourceGroupRelation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/logic_and.png"))); // NOI18N
        jSourceGroupRelation.setOpaque(false);
        jSourceGroupRelation.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/logic_or.png"))); // NOI18N
        jSourceGroupRelation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireSourceRelationChangedEvent(evt);
            }
        });

        jSelectionStart.setText(bundle.getString("TribeTribeAttackFrame.jSelectionStart.text")); // NOI18N
        jSelectionStart.setMaximumSize(new java.awt.Dimension(40, 25));
        jSelectionStart.setMinimumSize(new java.awt.Dimension(40, 25));
        jSelectionStart.setPreferredSize(new java.awt.Dimension(40, 25));

        jSelectionEnd.setText(bundle.getString("TribeTribeAttackFrame.jSelectionEnd.text")); // NOI18N
        jSelectionEnd.setMaximumSize(new java.awt.Dimension(40, 25));
        jSelectionEnd.setMinimumSize(new java.awt.Dimension(40, 25));
        jSelectionEnd.setPreferredSize(new java.awt.Dimension(40, 25));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(bundle.getString("TribeTribeAttackFrame.jLabel4.text")); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jSelectionBeginButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectionBeginButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/beginning.png"))); // NOI18N
        jSelectionBeginButton.setText(bundle.getString("TribeTribeAttackFrame.jSelectionBeginButton.text")); // NOI18N
        jSelectionBeginButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSelectionBeginButton.toolTipText")); // NOI18N
        jSelectionBeginButton.setEnabled(false);
        jSelectionBeginButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jSelectionBeginButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jSelectionBeginButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jSelectionBeginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateSelectionEvent(evt);
            }
        });

        jPrevSelectionButton.setBackground(new java.awt.Color(239, 235, 223));
        jPrevSelectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/prev.png"))); // NOI18N
        jPrevSelectionButton.setText(bundle.getString("TribeTribeAttackFrame.jPrevSelectionButton.text")); // NOI18N
        jPrevSelectionButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jPrevSelectionButton.toolTipText")); // NOI18N
        jPrevSelectionButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jPrevSelectionButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jPrevSelectionButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jPrevSelectionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateSelectionEvent(evt);
            }
        });

        jNextSelectionButton.setBackground(new java.awt.Color(239, 235, 223));
        jNextSelectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/next.png"))); // NOI18N
        jNextSelectionButton.setText(bundle.getString("TribeTribeAttackFrame.jNextSelectionButton.text")); // NOI18N
        jNextSelectionButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jNextSelectionButton.toolTipText")); // NOI18N
        jNextSelectionButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jNextSelectionButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jNextSelectionButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jNextSelectionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateSelectionEvent(evt);
            }
        });

        jSelectionEndButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectionEndButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/end.png"))); // NOI18N
        jSelectionEndButton.setText(bundle.getString("TribeTribeAttackFrame.jSelectionEndButton.text")); // NOI18N
        jSelectionEndButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSelectionEndButton.toolTipText")); // NOI18N
        jSelectionEndButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jSelectionEndButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jSelectionEndButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jSelectionEndButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateSelectionEvent(evt);
            }
        });

        jSelectButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/select.png"))); // NOI18N
        jSelectButton.setText(bundle.getString("TribeTribeAttackFrame.jSelectButton.text")); // NOI18N
        jSelectButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSelectButton.toolTipText")); // NOI18N
        jSelectButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jSelectButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jSelectButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jSelectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateSelectionEvent(evt);
            }
        });

        jMarkAsFakeBox.setText(bundle.getString("TribeTribeAttackFrame.jMarkAsFakeBox.text")); // NOI18N
        jMarkAsFakeBox.setToolTipText(bundle.getString("TribeTribeAttackFrame.jMarkAsFakeBox.toolTipText")); // NOI18N
        jMarkAsFakeBox.setOpaque(false);

        jPlayerSourcesOnlyBox.setSelected(true);
        jPlayerSourcesOnlyBox.setText(bundle.getString("TribeTribeAttackFrame.jPlayerSourcesOnlyBox.text")); // NOI18N
        jPlayerSourcesOnlyBox.setToolTipText(bundle.getString("TribeTribeAttackFrame.jPlayerSourcesOnlyBox.toolTipText")); // NOI18N
        jPlayerSourcesOnlyBox.setOpaque(false);
        jPlayerSourcesOnlyBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireShowPlayerSourcesOnlyChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jSourceVillageLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jSourceGroupRelation, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTroopsList, javax.swing.GroupLayout.Alignment.LEADING, 0, 175, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jSourceVillageLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jMarkAsFakeBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPlayerSourcesOnlyBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jSelectionStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel4)
                            .addGap(11, 11, 11)
                            .addComponent(jSelectionEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jSelectionBeginButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPrevSelectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSelectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jNextSelectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSelectionEndButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSourceVillageLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSourceVillageLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jSourceGroupRelation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTroopsList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jMarkAsFakeBox)
                            .addComponent(jPlayerSourcesOnlyBox)))
                    .addComponent(jSelectionEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSelectionStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addComponent(jNextSelectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSelectionEndButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSelectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSelectionBeginButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPrevSelectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jButton8.setBackground(new java.awt.Color(239, 235, 223));
        jButton8.setText(bundle.getString("TribeTribeAttackFrame.jButton8.text")); // NOI18N
        jButton8.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton8.toolTipText")); // NOI18N
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAllPlayerVillages(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(239, 235, 223));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/use_snob.png"))); // NOI18N
        jButton11.setText(bundle.getString("TribeTribeAttackFrame.jButton11.text")); // NOI18N
        jButton11.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton11.toolTipText")); // NOI18N
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUseSnobEvent(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(239, 235, 223));
        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/filter_strength.png"))); // NOI18N
        jButton14.setText(bundle.getString("TribeTribeAttackFrame.jButton14.text")); // NOI18N
        jButton14.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton14.toolTipText")); // NOI18N
        jButton14.setMaximumSize(new java.awt.Dimension(59, 59));
        jButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFilterTroopStrengthEvent(evt);
            }
        });

        jLabel20.setText(bundle.getString("TribeTribeAttackFrame.jLabel20.text")); // NOI18N

        jSetFakeButton.setBackground(new java.awt.Color(239, 235, 223));
        jSetFakeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/fake.png"))); // NOI18N
        jSetFakeButton.setText(bundle.getString("TribeTribeAttackFrame.jSetFakeButton.text")); // NOI18N
        jSetFakeButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSetFakeButton.toolTipText")); // NOI18N
        jSetFakeButton.setMaximumSize(new java.awt.Dimension(59, 33));
        jSetFakeButton.setMinimumSize(new java.awt.Dimension(59, 33));
        jSetFakeButton.setPreferredSize(new java.awt.Dimension(59, 33));
        jSetFakeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeSourceFakeStateEvent(evt);
            }
        });

        jSetNoFakeButton.setBackground(new java.awt.Color(239, 235, 223));
        jSetNoFakeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/no_fake.png"))); // NOI18N
        jSetNoFakeButton.setText(bundle.getString("TribeTribeAttackFrame.jSetNoFakeButton.text")); // NOI18N
        jSetNoFakeButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSetNoFakeButton.toolTipText")); // NOI18N
        jSetNoFakeButton.setMaximumSize(new java.awt.Dimension(59, 33));
        jSetNoFakeButton.setMinimumSize(new java.awt.Dimension(59, 33));
        jSetNoFakeButton.setPreferredSize(new java.awt.Dimension(59, 33));
        jSetNoFakeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeSourceFakeStateEvent(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(239, 235, 223));
        jButton15.setText(bundle.getString("TribeTribeAttackFrame.jButton15.text")); // NOI18N
        jButton15.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton15.toolTipText")); // NOI18N
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireGetSourceVillagesFromClipboardEvent(evt);
            }
        });

        jFilterSourceButton.setBackground(new java.awt.Color(239, 235, 223));
        jFilterSourceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/filter_off.png"))); // NOI18N
        jFilterSourceButton.setText(bundle.getString("TribeTribeAttackFrame.jFilterSourceButton.text")); // NOI18N
        jFilterSourceButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jFilterSourceButton.toolTipText")); // NOI18N
        jFilterSourceButton.setMaximumSize(new java.awt.Dimension(59, 59));
        jFilterSourceButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFilterByAttackPlansEvent(evt);
            }
        });

        javax.swing.GroupLayout jSourcePanelLayout = new javax.swing.GroupLayout(jSourcePanel);
        jSourcePanel.setLayout(jSourcePanelLayout);
        jSourcePanelLayout.setHorizontalGroup(
            jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jSourcePanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jFilterSourceButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton11)
                            .addComponent(jSetNoFakeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSetFakeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jSourcePanelLayout.createSequentialGroup()
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)))
                .addContainerGap())
        );
        jSourcePanelLayout.setVerticalGroup(
            jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jSourcePanelLayout.createSequentialGroup()
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFilterSourceButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSetNoFakeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSetFakeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton8)
                    .addGroup(jSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton15))
                .addGap(20, 20, 20)
                .addComponent(jLabel20)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("TribeTribeAttackFrame.jSourcePanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/res/barracks.png")), jSourcePanel); // NOI18N

        jTargetPanel.setBackground(new java.awt.Color(239, 235, 223));

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.setOpaque(false);

        jTargetAllyLabel.setText(bundle.getString("TribeTribeAttackFrame.jTargetAllyLabel.text")); // NOI18N

        jScrollPane7.setMaximumSize(new java.awt.Dimension(260, 140));
        jScrollPane7.setMinimumSize(new java.awt.Dimension(260, 140));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(260, 140));

        jTargetAllyList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane7.setViewportView(jTargetAllyList);

        jLabel1.setText(bundle.getString("TribeTribeAttackFrame.jLabel1.text")); // NOI18N

        jTargetTribeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane8.setViewportView(jTargetTribeList);

        jLabel23.setText(bundle.getString("TribeTribeAttackFrame.jLabel23.text")); // NOI18N

        jScrollPane9.setMaximumSize(new java.awt.Dimension(60, 132));
        jScrollPane9.setMinimumSize(new java.awt.Dimension(60, 132));
        jScrollPane9.setPreferredSize(new java.awt.Dimension(60, 132));

        jScrollPane9.setViewportView(jTargetContinentList);

        jLabel24.setText(bundle.getString("TribeTribeAttackFrame.jLabel24.text")); // NOI18N

        jScrollPane10.setMaximumSize(new java.awt.Dimension(220, 132));
        jScrollPane10.setMinimumSize(new java.awt.Dimension(100, 132));
        jScrollPane10.setPreferredSize(new java.awt.Dimension(240, 132));

        jScrollPane10.setViewportView(jTargetVillageList);

        jLabel11.setText(bundle.getString("TribeTribeAttackFrame.jLabel11.text")); // NOI18N

        jTargetTribeFilter.setText(bundle.getString("TribeTribeAttackFrame.jTargetTribeFilter.text")); // NOI18N
        jTargetTribeFilter.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fireTargetAllyFilterChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTargetAllyLabel)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTargetTribeFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 167, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTargetAllyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTargetTribeFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jVictimTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jVictimTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane3.setViewportView(jVictimTable);

        jButton4.setBackground(new java.awt.Color(239, 235, 223));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton4.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton4.toolTipText")); // NOI18N
        jButton4.setMaximumSize(new java.awt.Dimension(23, 23));
        jButton4.setMinimumSize(new java.awt.Dimension(23, 23));
        jButton4.setPreferredSize(new java.awt.Dimension(23, 23));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveTargetVillageEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton2.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton2.toolTipText")); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(23, 23));
        jButton2.setMinimumSize(new java.awt.Dimension(23, 23));
        jButton2.setPreferredSize(new java.awt.Dimension(23, 23));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTargetVillageEvent(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(239, 235, 223));
        jButton9.setText(bundle.getString("TribeTribeAttackFrame.jButton9.text")); // NOI18N
        jButton9.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton9.toolTipText")); // NOI18N
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAllTargetVillagesEvent(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(239, 235, 223));
        jButton16.setText(bundle.getString("TribeTribeAttackFrame.jButton16.text")); // NOI18N
        jButton16.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton16.toolTipText")); // NOI18N
        jButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireGetTargetVillagesFromClipboardEvent(evt);
            }
        });

        jFilterTargetButton.setBackground(new java.awt.Color(239, 235, 223));
        jFilterTargetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/filter_off.png"))); // NOI18N
        jFilterTargetButton.setText(bundle.getString("TribeTribeAttackFrame.jFilterTargetButton.text")); // NOI18N
        jFilterTargetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFilterByAttackPlansEvent(evt);
            }
        });

        javax.swing.GroupLayout jTargetPanelLayout = new javax.swing.GroupLayout(jTargetPanel);
        jTargetPanel.setLayout(jTargetPanelLayout);
        jTargetPanelLayout.setHorizontalGroup(
            jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTargetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTargetPanelLayout.createSequentialGroup()
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTargetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFilterTargetButton)))
                .addContainerGap())
        );
        jTargetPanelLayout.setVerticalGroup(
            jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTargetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFilterTargetButton)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton9)
                    .addGroup(jTargetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton16))
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("TribeTribeAttackFrame.jTargetPanel.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/res/ally.png")), jTargetPanel); // NOI18N

        jCalculatingProgressBar.setString(bundle.getString("TribeTribeAttackFrame.jCalculatingProgressBar.string")); // NOI18N
        jCalculatingProgressBar.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCalculatingProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCalculateButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jCalculatingProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCalculateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireAddAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAttackEvent
    //add selected attack sources
    Object[] values = jSourceVillageList.getSelectedValues();
    if (values == null) {
        return;
    }
    UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
    for (Object value : values) {
        Village vSource = (Village) value;
        ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{vSource, uSource, jMarkAsFakeBox.isSelected()});
        mSettingsPanel.addTribe(vSource.getTribe());
    }
}//GEN-LAST:event_fireAddAttackEvent

private void fireRemoveAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveAttackEvent
    //remove selected attack sources
    int[] rows = jAttacksTable.getSelectedRows();
    if ((rows != null) && (rows.length > 0)) {
        String message = "Angriff entfernen?";
        if (rows.length > 1) {
            message = rows.length + " Angriffe entfernen?";
        }

        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, "Angriff entfernen", "Nein", "Ja") != JOptionPane.YES_OPTION) {
            return;
        }

        for (int i = rows.length - 1; i >=
                0; i--) {
            jAttacksTable.invalidate();
            int row = jAttacksTable.convertRowIndexToModel(rows[i]);
            ((DefaultTableModel) jAttacksTable.getModel()).removeRow(row);
            jAttacksTable.revalidate();
        }

    }
}//GEN-LAST:event_fireRemoveAttackEvent

private void fireCalculateAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateAttackEvent
    if (!jCalculateButton.isEnabled()) {
        return;
    }
    //algorithm calculation
    //pre check
    DefaultTableModel victimModel = (DefaultTableModel) jVictimTable.getModel();
    DefaultTableModel attackModel = (DefaultTableModel) jAttacksTable.getModel();
    if (attackModel.getRowCount() == 0) {
        JOptionPaneHelper.showErrorBox(this, "Keine Herkunftsdörfer ausgewählt", "Fehler");
        jTabbedPane1.setSelectedIndex(0);
        return;
    }

    if (victimModel.getRowCount() == 0) {
        JOptionPaneHelper.showErrorBox(this, "Keine Ziele ausgewählt", "Fehler");
        jTabbedPane1.setSelectedIndex(1);
        return;

    }

    if (!mSettingsPanel.validatePanel()) {
        jTabbedPane1.setSelectedIndex(2);
        return;

    }

    //reading values
    List<Village> victimVillages = new LinkedList<Village>();
    for (int i = 0; i < victimModel.getRowCount(); i++) {
        victimVillages.add((Village) victimModel.getValueAt(i, 1));
    }

//build source-unit map
    int snobSources = 0;

    // <editor-fold defaultstate="collapsed" desc=" Build attacks and fakes">
    Hashtable<UnitHolder, List<Village>> sources = new Hashtable<UnitHolder, List<Village>>();
    Hashtable<UnitHolder, List<Village>> fakes = new Hashtable<UnitHolder, List<Village>>();
    for (int i = 0; i <
            attackModel.getRowCount(); i++) {
        Village vSource = (Village) attackModel.getValueAt(i, 0);
        UnitHolder uSource = (UnitHolder) attackModel.getValueAt(i, 1);
        boolean fake = (Boolean) attackModel.getValueAt(i, 2);
        if (!fake) {
            List<Village> sourcesForUnit = sources.get(uSource);
            if (uSource.getPlainName().equals("snob")) {
                if (sourcesForUnit == null) {
                    snobSources = 0;
                } else {
                    snobSources = sourcesForUnit.size();
                }

            }
            if (sourcesForUnit == null) {
                sourcesForUnit = new LinkedList<Village>();
                sourcesForUnit.add(vSource);
                sources.put(uSource, sourcesForUnit);
            } else {
                sourcesForUnit.add(vSource);
            }

        } else {
            List<Village> fakesForUnit = fakes.get(uSource);
            if (fakesForUnit == null) {
                fakesForUnit = new LinkedList<Village>();
                fakesForUnit.add(vSource);
                fakes.put(uSource, fakesForUnit);
            } else {
                fakesForUnit.add(vSource);
            }

        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Check for units not supported by the algorithm">
    boolean useMiscUnits = false;
    Enumeration<UnitHolder> involvedUnits = sources.keys();
    while (involvedUnits.hasMoreElements()) {
        UnitHolder u = involvedUnits.nextElement();
        //check for misc unit
        if (!u.getPlainName().equals("ram") && !u.getPlainName().equals("catapult")) {
            useMiscUnits = true;
            break;

        }
    }
    if (!useMiscUnits) {
        involvedUnits = fakes.keys();
        while (involvedUnits.hasMoreElements()) {
            UnitHolder u = involvedUnits.nextElement();
            //check for misc unit
            if (!u.getPlainName().equals("ram") && !u.getPlainName().equals("catapult")) {
                useMiscUnits = true;
                break;
            }
        }
    }

    // </editor-fold>

    boolean fakeOffTargets = mSettingsPanel.fakeOffTargets();


    int maxAttacksPerVillage = mSettingsPanel.getAttacksPerVillage();
    TimeFrame timeFrame = mSettingsPanel.getTimeFrame();

    //start processing
    AbstractAttackAlgorithm algo = null;
    boolean supportMiscUnits = false;

    if (mSettingsPanel.useBruteForce()) {
        logger.info("Using 'BruteForce' calculation");
        algo = new BruteForce();
        supportMiscUnits = true;
    } else {
        logger.info("Using 'systematic' calculation");
        algo = new Iterix();
        supportMiscUnits = false;
    }

    //check misc-units criteria
    if (useMiscUnits && !supportMiscUnits) {
        if (JOptionPaneHelper.showQuestionConfirmBox(this, "Der gewählte Algorithmus unterstützt nur Rammen, Katapulte und AGs als angreifende Einheiten.\n" +
                "Dörfer für die eine andere Einheit gewählt wurde werden ignoriert.\n" +
                "Trotzdem fortfahren?", "Warnung", "Nein", "Ja") == JOptionPane.NO_OPTION) {
            return;
        }
    }

    algo.initialize(sources,
            fakes,
            victimVillages,
            maxAttacksPerVillage,
            timeFrame,
            fakeOffTargets);

    algo.execute(this);
    jCalculateButton.setEnabled(false);
    jCalculatingProgressBar.setString("Berechnung läuft...");
    jCalculatingProgressBar.setIndeterminate(true);

}//GEN-LAST:event_fireCalculateAttackEvent

private void fireHideResultsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideResultsEvent
    jResultFrame.setVisible(false);
}//GEN-LAST:event_fireHideResultsEvent

private void fireTransferToAttackPlanningEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireTransferToAttackPlanningEvent
    //initialize transfer of results to attack view
    jNewPlanName.setText("");
    Enumeration<String> plans = AttackManager.getSingleton().getPlans();
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    while (plans.hasMoreElements()) {
        model.addElement(plans.nextElement());
    }

    jAttackPlansBox.setModel(model);
    jTransferToAttackManagerDialog.setLocationRelativeTo(jResultFrame);
    jTransferToAttackManagerDialog.setVisible(true);
}//GEN-LAST:event_fireTransferToAttackPlanningEvent

private void fireAttacksToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAttacksToClipboardEvent
    //copy results formatted to clipboard
    try {
        boolean extended = (JOptionPaneHelper.showQuestionConfirmBox(jResultFrame, "Erweiterte BB-Codes verwenden (nur für Forum und Notizen geeignet)?", "Erweiterter BB-Code", "Nein", "Ja") == JOptionPane.YES_OPTION);

        String sUrl = ServerManager.getServerURL(GlobalOptions.getSelectedServer());

        DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
        StringBuffer buffer = new StringBuffer();
        if (extended) {
            buffer.append("[u][size=12]Angriffsplan[/size][/u]\n\n");
        } else {
            buffer.append("[u]Angriffsplan[/u]\n\n");
        }

        for (int i = 0; i < resultModel.getRowCount(); i++) {

            Village sVillage = (Village) resultModel.getValueAt(i, 0);
            UnitHolder sUnit = (UnitHolder) resultModel.getValueAt(i, 1);
            Village tVillage = (Village) resultModel.getValueAt(i, 2);
            Date dTime = (Date) resultModel.getValueAt(i, 3);
            int type = (Integer) resultModel.getValueAt(i, 4);
            buffer.append(AttackToBBCodeFormater.formatAttack(sVillage, tVillage, sUnit, dTime, type, sUrl, extended));
        }

        if (extended) {
            buffer.append("\n[size=8]Erstellt am ");
            buffer.append(new SimpleDateFormat("dd.MM.yy 'um' HH:mm:ss").format(Calendar.getInstance().getTime()));
            buffer.append(" mit [url=\"http://www.dsworkbench.de/index.php?id=23\"]DS Workbench ");
            buffer.append(Constants.VERSION + Constants.VERSION_ADDITION + "[/url][/size]\n");
        } else {
            buffer.append("\nErstellt am ");
            buffer.append(new SimpleDateFormat("dd.MM.yy 'um' HH:mm:ss").format(Calendar.getInstance().getTime()));
            buffer.append(" mit [url=\"http://www.dsworkbench.de/index.php?id=23\"]DS Workbench ");
            buffer.append(Constants.VERSION + Constants.VERSION_ADDITION + "[/url]\n");
        }

        String b = buffer.toString();
        StringTokenizer t = new StringTokenizer(b, "[");
        int cnt = t.countTokens();
        if (cnt > 500) {

            if (JOptionPaneHelper.showQuestionConfirmBox(jResultFrame, "Die ausgewählten Angriffe benötigen mehr als 500 BB-Codes\n" +
                    "und können daher im Spiel (Forum/IGM/Notizen) nicht auf einmal dargestellt werden.\nTrotzdem exportieren?", "Zu viele BB-Codes", "Nein", "Ja") == JOptionPane.NO_OPTION) {
                return;
            }

        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b), null);
        String result = "Daten in Zwischenablage kopiert.";
        JOptionPaneHelper.showInformationBox(jResultFrame, result, "Information");
    } catch (Exception e) {
        logger.error("Failed to copy data to clipboard", e);
        String result = "Fehler beim Kopieren in die Zwischenablage.";
        JOptionPaneHelper.showErrorBox(jResultFrame, result, "Fehler");
    }
}//GEN-LAST:event_fireAttacksToClipboardEvent

private void fireUnformattedAttacksToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUnformattedAttacksToClipboardEvent
    //copy results unformatted to clipboard
    try {
        DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <
                resultModel.getRowCount(); i++) {
            Village sVillage = (Village) resultModel.getValueAt(i, 0);
            UnitHolder sUnit = (UnitHolder) resultModel.getValueAt(i, 1);
            Village tVillage = (Village) resultModel.getValueAt(i, 2);
            Date dTime = (Date) resultModel.getValueAt(i, 3);
            int type = (Integer) resultModel.getValueAt(i, 4);
            String time = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").format(dTime);

            switch (type) {
                case Attack.CLEAN_TYPE: {
                    buffer.append("(Clean-Off)");
                    buffer.append("\t");
                    break;

                }


                case Attack.FAKE_TYPE: {
                    buffer.append("(Fake)");
                    buffer.append("\t");
                    break;

                }


                case Attack.SNOB_TYPE: {
                    buffer.append("(AG)");
                    buffer.append("\t");
                    break;

                }


                case Attack.SUPPORT_TYPE: {
                    buffer.append("(Unterstützung)");
                    buffer.append("\t");
                    break;

                }


            }

            buffer.append(sVillage);
            buffer.append("\t");
            buffer.append(sUnit);
            buffer.append("\t");
            buffer.append(tVillage.getTribe());
            buffer.append("\t");
            buffer.append(tVillage);
            buffer.append("\t");
            buffer.append(time);
            buffer.append("\n");
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(buffer.toString()), null);
        String result = "Daten in Zwischenablage kopiert.";
        JOptionPaneHelper.showInformationBox(jResultFrame, result, "Information");
    } catch (Exception e) {
        logger.error("Failed to copy data to clipboard", e);
        String result = "Fehler beim Kopieren in die Zwischenablage.";
        JOptionPaneHelper.showErrorBox(jResultFrame, result, "Fehler");
    }
}//GEN-LAST:event_fireUnformattedAttacksToClipboardEvent

private void fireAddAllPlayerVillages(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAllPlayerVillages
    //add all source villages to source list
    UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
    jAttacksTable.invalidate();
    try {
        int size = jSourceVillageList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{jSourceVillageList.getModel().getElementAt(i), uSource, jMarkAsFakeBox.isSelected()});
            mSettingsPanel.addTribe(((Village) jSourceVillageList.getModel().getElementAt(i)).getTribe());
        }

    } catch (Exception e) {
        logger.error("Failed to add current group as source", e);
    }

    jAttacksTable.revalidate();
}//GEN-LAST:event_fireAddAllPlayerVillages

private void fireRemoveTargetVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveTargetVillageEvent
    //remove selected targets
    int[] rows = jVictimTable.getSelectedRows();
    if ((rows != null) && (rows.length > 0)) {
        String message = "Ziel entfernen?";
        if (rows.length > 1) {
            message = rows.length + " Ziele entfernen?";
        }

        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, "Ziel entfernen", "Nein", "Ja") != JOptionPane.YES_OPTION) {
            return;
        }

        for (int i = rows.length - 1; i >=
                0; i--) {
            jVictimTable.invalidate();
            int row = jVictimTable.convertRowIndexToModel(rows[i]);
            ((DefaultTableModel) jVictimTable.getModel()).removeRow(row);
            jVictimTable.revalidate();
        }

    }
}//GEN-LAST:event_fireRemoveTargetVillageEvent

private void fireAddTargetVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTargetVillageEvent
    //add selected target villages
    Tribe target = (Tribe) jTargetTribeList.getSelectedValue();
    Object[] villages = jTargetVillageList.getSelectedValues();
    if (villages == null) {
        return;
    }

    DefaultTableModel victimModel = (DefaultTableModel) jVictimTable.getModel();
    jVictimTable.invalidate();
    for (Object o : villages) {
        Village village = (Village) o;
        victimModel.addRow(new Object[]{target, village});
    }

    jVictimTable.revalidate();
    jVictimTable.repaint();//.updateUI();
}//GEN-LAST:event_fireAddTargetVillageEvent

private void fireAddAllTargetVillagesEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAllTargetVillagesEvent
    //add all current target villages
    Tribe target = (Tribe) jTargetTribeList.getSelectedValue();
    if (target == null) {
        return;
    }

    jVictimTable.invalidate();
    int size = jTargetVillageList.getModel().getSize();
    for (int i = 0; i <
            size; i++) {
        ((DefaultTableModel) jVictimTable.getModel()).addRow(new Object[]{target, jTargetVillageList.getModel().getElementAt(i)});
    }

    jVictimTable.revalidate();
    jVictimTable.repaint();//.updateUI();
}//GEN-LAST:event_fireAddAllTargetVillagesEvent

private void fireTransferAttacksToPlanEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireTransferAttacksToPlanEvent
    //transfer results to attack view
    String planName = jNewPlanName.getText();
    if (planName.length() < 1) {
        int idx = jAttackPlansBox.getSelectedIndex();
        if (idx < 0) {
            planName = null;
        } else {
            planName = (String) jAttackPlansBox.getSelectedItem();
        }

    }

    if (AttackManager.getSingleton().getAttackPlan(planName) == null) {
        AttackManager.getSingleton().addEmptyPlan(planName);
        DSWorkbenchAttackFrame.getSingleton().buildAttackPlanList();
    }

    if (logger.isDebugEnabled()) {
        logger.debug("Adding attacks to plan '" + planName + "'");
    }

    DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
    boolean showOnMap = false;
    try {
        showOnMap = Boolean.parseBoolean(GlobalOptions.getProperty("draw.attacks.by.default"));
    } catch (Exception e) {
    }

    for (int i = 0; i < resultModel.getRowCount(); i++) {
        Village source = (Village) resultModel.getValueAt(i, 0);
        UnitHolder unit = (UnitHolder) resultModel.getValueAt(i, 1);
        Village target = (Village) resultModel.getValueAt(i, 2);
        Date sendTime = (Date) resultModel.getValueAt(i, 3);
        Integer type = (Integer) resultModel.getValueAt(i, 4);
        long arriveTime = sendTime.getTime() + (long) (DSCalculator.calculateMoveTimeInSeconds(source, target, unit.getSpeed()) * 1000);
        AttackManager.getSingleton().addAttackFast(source, target, unit, new Date(arriveTime), showOnMap, planName, type);
    }

    AttackManager.getSingleton().forceUpdate(planName);
    jTransferToAttackManagerDialog.setVisible(false);
}//GEN-LAST:event_fireTransferAttacksToPlanEvent

private void fireCancelTransferEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCancelTransferEvent
    jTransferToAttackManagerDialog.setVisible(false);
}//GEN-LAST:event_fireCancelTransferEvent

private void fireUseSnobEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUseSnobEvent
    //use snobs in villages where snobs exist
    DefaultTableModel model = (DefaultTableModel) jAttacksTable.getModel();
    int rows = model.getRowCount();
    UnitHolder snob = DataHolder.getSingleton().getUnitByPlainName("snob");
    jAttacksTable.invalidate();
    Hashtable<Village, Integer> assignedTroops = new Hashtable<Village, Integer>();
    for (int row = 0; row < rows; row++) {
        Village v = (Village) model.getValueAt(row, 0);
        VillageTroopsHolder troops = TroopsManager.getSingleton().getTroopsForVillage(v);
        if (troops != null) {
            int availSnobs = troops.getTroopsInVillage().get(snob);
            Integer assignedSnobs = assignedTroops.get(v);
            if (assignedSnobs == null) {
                assignedSnobs = 0;
            } else {
                assignedSnobs += 1;
            }

            availSnobs -= assignedSnobs;
            assignedTroops.put(v, assignedSnobs);
            //snob avail
            if (availSnobs > 0) {
                model.setValueAt(snob, row, 1);
            }

        }
    }
    jAttacksTable.revalidate();
}//GEN-LAST:event_fireUseSnobEvent

private void fireFilterTroopStrengthEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireFilterTroopStrengthEvent
    /* fireToleranceChangedEvent(null);
    jOffStrengthFrame.setVisible(true);*/
    jFilterFrame.pack();
    jFilterFrame.setLocationRelativeTo(this);
    jFilterFrame.setVisible(true);
}//GEN-LAST:event_fireFilterTroopStrengthEvent

private void fireChangeSourceFakeStateEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeSourceFakeStateEvent
    //change marked attacks to fake/no fake
    boolean toFake = (evt.getSource() == jSetFakeButton);

    int[] rows = jAttacksTable.getSelectedRows();
    if (rows == null) {
        return;
    }

    jAttacksTable.invalidate();
    for (int row : rows) {
        jAttacksTable.setValueAt(toFake, row, 2);
    }

    jAttacksTable.revalidate();
}//GEN-LAST:event_fireChangeSourceFakeStateEvent

private void fireShowResultDetailsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireShowResultDetailsEvent
    if (evt.getSource() == jAttacksBar) {
        jAttackResultDetailsFrame.setVisible(true);
    } else if (evt.getSource() == jTargetsBar) {
        jTargetResultDetailsFrame.setVisible(true);
    }
}//GEN-LAST:event_fireShowResultDetailsEvent

private void fireHideResultDetailsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideResultDetailsEvent
    if (evt.getSource() == jHideAttackDetailsButton) {
        jAttackResultDetailsFrame.setVisible(false);
    } else if (evt.getSource() == jHideTargetDetailsButton) {
        jTargetResultDetailsFrame.setVisible(false);
    }
}//GEN-LAST:event_fireHideResultDetailsEvent

private void fireUpdateSelectionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUpdateSelectionEvent
    int start = 1;
    int end = 10;
    try {
        start = Integer.parseInt(jSelectionStart.getText());
        end = Integer.parseInt(jSelectionEnd.getText());
    } catch (Exception e) {
    }
    try {
        //switch numbers if start larger than end
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        int diff = end - start + 1;
        if (evt == null || evt.getSource() == jSelectButton) {
            //do nothing
        } else if (evt.getSource() == jSelectionBeginButton && jSelectionBeginButton.isEnabled()) {
            start = 1;
            end = diff;
        } else if (evt.getSource() == jPrevSelectionButton) {
            start = start - diff;
            if (start <= 0) {
                start = 1;
            }

            end = start + diff - 1;
        } else if (evt.getSource() == jNextSelectionButton) {
            start = end + 1;
            end = (start + diff - 1 > jSourceVillageList.getModel().getSize()) ? jSourceVillageList.getModel().getSize() : (start + diff - 1);
        } else if (evt.getSource() == jSelectionEndButton && jSelectionEnd.isEnabled()) {
            end = jSourceVillageList.getModel().getSize();
            start = end - diff + 1;
        }

        jSelectionEndButton.setEnabled(!(end == jSourceVillageList.getModel().getSize()));
        jSelectionBeginButton.setEnabled(!(start == 1));
        jSelectionStart.setText(Integer.toString(start));
        jSelectionEnd.setText(Integer.toString(end));

        jSourceVillageList.getSelectionModel().setSelectionInterval(start - 1, end - 1);
        jSourceVillageList.scrollRectToVisible(jSourceVillageList.getCellBounds(start - 1, end - 1));
    } catch (Exception e) {
        logger.warn("Error while calculating source selection step", e);
    }
}//GEN-LAST:event_fireUpdateSelectionEvent

private void fireGetSourceVillagesFromClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireGetSourceVillagesFromClipboardEvent
    try {
        Transferable t = (Transferable) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        List<Village> villages = VillageParser.parse((String) t.getTransferData(DataFlavor.stringFlavor));
        if (villages == null || villages.isEmpty()) {
            JOptionPaneHelper.showInformationBox(this, "Es konnten keine Dorfkoodinaten in der Zwischenablage gefunden werden.", "Information");
            return;

        } else {
            UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
            for (Village v : villages) {
                ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{v, uSource, jMarkAsFakeBox.isSelected()});
                mSettingsPanel.addTribe(v.getTribe());
            }

            String message = (villages.size() == 1) ? "1 Dorf " : villages.size() + " Dörfer ";
            JOptionPaneHelper.showInformationBox(this, message + " übertragen.", "Information");
        }

    } catch (Exception e) {
        logger.error("Failed to parse source villages from clipboard", e);
    }
}//GEN-LAST:event_fireGetSourceVillagesFromClipboardEvent

private void fireGetTargetVillagesFromClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireGetTargetVillagesFromClipboardEvent
    try {
        Transferable t = (Transferable) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        List<Village> villages = VillageParser.parse((String) t.getTransferData(DataFlavor.stringFlavor));
        if (villages == null || villages.isEmpty()) {
            JOptionPaneHelper.showInformationBox(this, "Es konnten keine Dorfkoodinaten in der Zwischenablage gefunden werden.", "Information");
            return;

        } else {
            for (Village v : villages) {
                Tribe victim = v.getTribe();
                if (victim != null) {
                    ((DefaultTableModel) jVictimTable.getModel()).addRow(new Object[]{victim, v});
                }

            }
            String message = (villages.size() == 1) ? "1 Dorf " : villages.size() + " Dörfer ";
            JOptionPaneHelper.showInformationBox(this, message + " übertragen.", "Information");
        }

    } catch (Exception e) {
        logger.error("Failed to parse victim villages from clipboard", e);
    }
}//GEN-LAST:event_fireGetTargetVillagesFromClipboardEvent

private void fireShowPlayerSourcesOnlyChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireShowPlayerSourcesOnlyChangedEvent
    fireFilterSourceVillagesByGroupEvent();
}//GEN-LAST:event_fireShowPlayerSourcesOnlyChangedEvent

private void fireSourceRelationChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireSourceRelationChangedEvent
    if (jSourceGroupRelation.isSelected()) {
        jSourceGroupRelation.setText("Verknüpfung (ODER)");
    } else {
        jSourceGroupRelation.setText("Verknüpfung (UND)");
    }

    fireFilterSourceVillagesByGroupEvent();
}//GEN-LAST:event_fireSourceRelationChangedEvent

private void fireSynchWithAttackPlansEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSynchWithAttackPlansEvent
    jAttackPlanSelectionDialog.setVisible(false);
    if (evt.getSource() == jCancelSyncButton) {
        return;
    }

    if (filterSource == jFilterSourceButton) {
        DefaultTableModel model = (DefaultTableModel) jAttackPlanTable.getModel();

        List<String> selectedPlans = new LinkedList<String>();
        for (int i = 0; i <
                jAttackPlanTable.getRowCount(); i++) {
            int row = jAttackPlanTable.convertRowIndexToModel(i);
            if ((Boolean) model.getValueAt(row, 1)) {
                selectedPlans.add((String) model.getValueAt(row, 0));
            }

        }

        // Enumeration<String> plans = AttackManager.getSingleton().getPlans();
        List<Integer> toRemove = new LinkedList<Integer>();
        //process all plans
        //  while (plans.hasMoreElements()) {
        for (String plan : selectedPlans) {
            //String plan = plans.nextElement();
            logger.debug("Checking plan '" + plan + "'");
            List<Attack> attacks = AttackManager.getSingleton().getAttackPlan(plan);

            //process all attacks
            for (Attack a : attacks) {
                //search attack source village in all table rows
                for (int i = 0; i <
                        jAttacksTable.getRowCount(); i++) {
                    Village v = (Village) jAttacksTable.getValueAt(i, 0);
                    if (a.getSource().equals(v)) {
                        if (!toRemove.contains(i)) {
                            toRemove.add(i);
                        }

                    }
                }
            }
        }

        String message = "";
        if (toRemove.size() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Keine Herkunftsdörfer zu entfernen.", "Information");
            return;

        } else {
            message = (toRemove.size() == 1) ? "Ein Herkunftsdorf " : toRemove.size() + " Herkunftsdörfer ";
        }

        if (JOptionPaneHelper.showQuestionConfirmBox(this, message + "entfernen?", "Entfernen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
            try {
                logger.debug("Removing " + toRemove.size() + " source villages");
                jAttacksTable.invalidate();
                Collections.sort(toRemove);

                while (toRemove.size() > 0) {
                    Integer row = toRemove.remove(toRemove.size() - 1);
                    row = jAttacksTable.convertRowIndexToModel(row);
                    ((DefaultTableModel) jAttacksTable.getModel()).removeRow(row);
                }

                jAttacksTable.revalidate();
            } catch (Exception e) {
                logger.error("Removal failed", e);
            }

        }
    } else {
        DefaultTableModel model = (DefaultTableModel) jAttackPlanTable.getModel();

        List<String> selectedPlans = new LinkedList<String>();
        for (int i = 0; i <
                jAttackPlanTable.getRowCount(); i++) {
            int row = jAttackPlanTable.convertRowIndexToModel(i);
            if ((Boolean) model.getValueAt(row, 1)) {
                selectedPlans.add((String) model.getValueAt(row, 0));
            }

        }

        // Enumeration<String> plans = AttackManager.getSingleton().getPlans();
        List<Integer> toRemove = new LinkedList<Integer>();
        //process all plans
        //  while (plans.hasMoreElements()) {
        for (String plan : selectedPlans) {
            //String plan = plans.nextElement();
            logger.debug("Checking plan '" + plan + "'");
            List<Attack> attacks = AttackManager.getSingleton().getAttackPlan(plan);

            //process all attacks
            for (Attack a : attacks) {
                //search attack target village in all table rows
                for (int i = 0; i <
                        jVictimTable.getRowCount(); i++) {
                    Village v = (Village) jVictimTable.getValueAt(i, 1);
                    if (a.getTarget().equals(v)) {
                        if (!toRemove.contains(i)) {
                            toRemove.add(i);
                        }

                    }
                }
            }
        }

        String message = "";
        if (toRemove.size() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Keine Zieldörfer zu entfernen.", "Information");
            return;

        } else {
            message = (toRemove.size() == 1) ? "Ein Zieldorf " : toRemove.size() + " Zieldörfer ";
        }

        if (JOptionPaneHelper.showQuestionConfirmBox(this, message + "entfernen?", "Entfernen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
            try {
                logger.debug("Removing " + toRemove.size() + " target villages");
                jVictimTable.invalidate();
                Collections.sort(toRemove);

                while (toRemove.size() > 0) {
                    Integer row = toRemove.remove(toRemove.size() - 1);
                    ((DefaultTableModel) jVictimTable.getModel()).removeRow(jVictimTable.convertRowIndexToModel(row));
                }

                jVictimTable.revalidate();
            } catch (Exception e) {
                logger.error("Removal failed", e);
            }

        }
    }

}//GEN-LAST:event_fireSynchWithAttackPlansEvent

private void fireFilterByAttackPlansEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireFilterByAttackPlansEvent

    filterSource = (JButton) evt.getSource();

    DefaultTableModel model = new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Angriffsplan", "Abgleichen"}) {

        Class[] types = new Class[]{
            String.class, Boolean.class
        };

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return false;
            }
            return true;
        }
    };

    jAttackPlanTable.invalidate();
    Enumeration<String> plans = AttackManager.getSingleton().getPlans();
    List<String> planList = new LinkedList<String>();
    while (plans.hasMoreElements()) {
        planList.add(plans.nextElement());
    }

    Collections.sort(planList);
    for (String plan : planList) {
        model.addRow(new Object[]{plan, false});
    }

    jAttackPlanTable.setModel(model);
    jAttackPlanTable.revalidate();
    DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
            String t = ((DefaultTableCellRenderer) c).getText();
            ((DefaultTableCellRenderer) c).setText(t);
            c.setBackground(Constants.DS_BACK);
            DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
            r.setText("<html><b>" + r.getText() + "</b></html>");
            return c;
        }
    };
    for (int i = 0; i < jAttackPlanTable.getColumnCount(); i++) {
        jAttackPlanTable.getColumn(jAttackPlanTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
    }

    jAttackPlanSelectionDialog.setLocationRelativeTo(this);
    jAttackPlanSelectionDialog.setVisible(true);

}//GEN-LAST:event_fireFilterByAttackPlansEvent

private void fireTargetAllyFilterChangedEvent(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fireTargetAllyFilterChangedEvent
    String text = jTargetTribeFilter.getText();
    if (text.length() > 0) {
        text = text.toLowerCase();
        Enumeration<Integer> allyKeys = DataHolder.getSingleton().getAllies().keys();
        List<Ally> allies = new LinkedList();
        while (allyKeys.hasMoreElements()) {
            Ally a = DataHolder.getSingleton().getAllies().get(allyKeys.nextElement());
            if (a.getName() != null && a.getTag() != null) {
                if (a.getName().toLowerCase().indexOf(text) >= 0 || a.getTag().toLowerCase().indexOf(text) >= 0) {
                    allies.add(a);
                }

            }
        }
        Ally[] aAllies = allies.toArray(new Ally[]{});
        allies =
                null;
        Arrays.sort(aAllies, Ally.CASE_INSENSITIVE_ORDER);
        DefaultListModel targetAllyModel = new DefaultListModel();
        for (Ally a : aAllies) {
            targetAllyModel.addElement(a);
        }

        jTargetAllyList.setModel(targetAllyModel);
        jTargetAllyList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fireFilterTargetByAllyEvent();
            }
        });
    } else {
        Enumeration<Integer> allyKeys = DataHolder.getSingleton().getAllies().keys();
        List<Ally> allies = new LinkedList();
        while (allyKeys.hasMoreElements()) {
            allies.add(DataHolder.getSingleton().getAllies().get(allyKeys.nextElement()));
        }

        Ally[] aAllies = allies.toArray(new Ally[]{});
        allies =
                null;
        Arrays.sort(aAllies, Ally.CASE_INSENSITIVE_ORDER);
        DefaultListModel targetAllyModel = new DefaultListModel();
        targetAllyModel.addElement("<Kein Stamm>");
        for (Ally a : aAllies) {
            targetAllyModel.addElement(a);
        }

        jTargetAllyList.setModel(targetAllyModel);
        jTargetAllyList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fireFilterTargetByAllyEvent();
            }
        });
    }
}//GEN-LAST:event_fireTargetAllyFilterChangedEvent

private void fireAddTroopFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTroopFilterEvent
    UnitHolder unit = (UnitHolder) jFilterUnitBox.getSelectedItem();
    DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
    TroopFilterElement elem = null;
    int min = Integer.MIN_VALUE;
    int max = Integer.MAX_VALUE;
    try {
        min = Integer.parseInt(jMinValue.getText());
    } catch (Exception e) {
        min = Integer.MIN_VALUE;
    }
    try {
        max = Integer.parseInt(jMaxValue.getText());
    } catch (Exception e) {
        max = Integer.MAX_VALUE;
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
    if (elem == null) {
        elem = new TroopFilterElement(unit, min, max);
        filterModel.addElement(elem);
    }
}//GEN-LAST:event_fireAddTroopFilterEvent

private void fireRemoveTroopFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveTroopFilterEvent
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
}//GEN-LAST:event_fireRemoveTroopFilterEvent

private void fireApplyTroopFiltersEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireApplyTroopFiltersEvent
    if (evt.getSource() == jApplyFiltersButton) {
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
        List<Integer> rowsToRemove = new LinkedList<Integer>();
        int removeCount = 0;
        for (int i = 0; i < jAttacksTable.getRowCount(); i++) {
            //go through all rows in attack table and get source village
            Village v = (Village) jAttacksTable.getValueAt(i, 0);
            for (int j = 0; j < filterModel.size(); j++) {
                //check for all filters if villag is allowed
                if (!((TroopFilterElement) filterModel.get(j)).allowsVillage(v)) {
                    //village is not allowed, add to remove list
                    int row = jAttacksTable.convertRowIndexToModel(i);
                    rowsToRemove.add(row);
                    removeCount++;
                }
            }
        }


        jAttacksTable.invalidate();
        for (int i = rowsToRemove.size() - 1; i >= 0; i--) {
            int row = rowsToRemove.get(i);
            ((DefaultTableModel) jAttacksTable.getModel()).removeRow(row);
        }
        jAttacksTable.revalidate();
        String message = "Es wurden keine Angriffe entfernt.";
        if (removeCount == 1) {
            message = "Es wurde ein Angriff entfernt.";
        } else if (removeCount > 1) {
            message = "Es wurden " + removeCount + " Angriffe entfernt.";
        }

        JOptionPaneHelper.showInformationBox(jFilterFrame, message, "Information");
    }
    jFilterFrame.setVisible(false);
}//GEN-LAST:event_fireApplyTroopFiltersEvent

    /**Add source villages externally (see DSWorkbenchMainFrame)*/
    public void fireAddSourcesEvent(List<Village> pSources) {
        UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
        for (Village v : pSources) {
            if (v.getTribe() != null) {
                ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{v, uSource, jMarkAsFakeBox.isSelected()});
                mSettingsPanel.addTribe(v.getTribe());
            }
        }
    }

    /**Add target villages externally (see DSWorkbenchMainFrame)*/
    public void fireAddTargetsEvent(List<Village> pVillages) {
        DefaultTableModel victimModel = (DefaultTableModel) jVictimTable.getModel();
        jVictimTable.invalidate();
        for (Village v : pVillages) {
            if (v.getTribe() != null) {
                victimModel.addRow(new Object[]{v.getTribe(), v});
            }

        }

        jVictimTable.revalidate();
        jVictimTable.repaint();
    }

    /**Show result frame for calculated attacks*/
    private void showResults(List<Attack> pAttacks) {
        jResultsTable.invalidate();
        DefaultTableModel resultModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Herkunft", "Einheit", "Ziel", "Startzeit", "Typ", ""}) {

            Class[] types = new Class[]{
                Village.class, UnitHolder.class, Village.class, Date.class, Integer.class, Boolean.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        //renderer, which hides the boolean table column
        DefaultTableCellRenderer invis = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(Color.WHITE);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("");
                r.setVisible(false);
                // r.setText(r.getText());
                return c;
            }
        };

        //renderer, which marks send times red if attack is impossible to send
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS");
                Boolean impossible = (Boolean) table.getModel().getValueAt(row, 5);
                if (impossible.booleanValue()) {
                    c.setBackground(Color.RED);
                }

                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText((value == null) ? "" : f.format(value));
                return c;
            }
        };

        jResultsTable.setDefaultRenderer(Date.class, renderer);
        jResultsTable.setDefaultRenderer(Boolean.class, invis);
        jResultsTable.setDefaultRenderer(Integer.class, new AttackTypeCellRenderer());
        jResultsTable.setDefaultEditor(Integer.class, new AttackTypeCellEditor());
        jResultsTable.setDefaultRenderer(UnitHolder.class, new UnitCellRenderer());
        jResultsTable.setDefaultEditor(UnitHolder.class, new UnitCellEditor());
        jResultsTable.setDefaultRenderer(Village.class, new VillageCellRenderer());
        jResultsTable.setRowHeight(20);

        //jResultsTable.setDefaultRenderer(Date.class, new DateCellRenderer());

        List<Long> startTimes = new LinkedList<Long>();
        int impossibleAttacks = 0;
        for (Attack a : pAttacks) {
            long targetTime = a.getArriveTime().getTime();
            long startTime = targetTime - (long) (DSCalculator.calculateMoveTimeInSeconds(a.getSource(), a.getTarget(), a.getUnit().getSpeed()) * 1000);
            boolean impossible = false;
            if (!startTimes.contains(startTime)) {
                startTimes.add(startTime);
            } else {
                impossibleAttacks++;
                impossible = true;
            }
            resultModel.addRow(new Object[]{a.getSource(), a.getUnit(), a.getTarget(), new Date(startTime), a.getType(), impossible});
        }

        jResultsTable.setModel(resultModel);
        TableColumn tc = jResultsTable.getColumnModel().getColumn(5);

        tc.setPreferredWidth(0);

        tc.setMaxWidth(0);
        tc.setWidth(0);

        tc.setResizable(false);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jResultsTable.getModel());

        jResultsTable.setRowSorter(sorter);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                String t = ((DefaultTableCellRenderer) c).getText();
                ((DefaultTableCellRenderer) c).setText(t);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };
        for (int i = 0; i < jResultsTable.getColumnCount(); i++) {
            jResultsTable.getColumn(jResultsTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }
        jResultsTable.revalidate();
        jResultFrame.setVisible(true);

        if (impossibleAttacks > 0) {
            String message = "";
            if (impossibleAttacks == 1) {
                message = "Ein berechneter Angriff kann vermutlich nicht abgeschickt werden.\nDer entsprechende Angriff ist in der Tabelle rot markiert.";
            } else {
                message = impossibleAttacks + " berechnete Angriffe können vermutlich nicht abgeschickt werden.\nDie entsprechenden Angriffe sind in der Tabelle rot markiert.";
            }
            JOptionPaneHelper.showWarningBox(jResultFrame, message, "Warnung");
        }
    }

    /**Create detail frames*/
    private void buildDetailedStatistics(Hashtable<Village, Integer> attackMappings, List<Village> pNotAssignedVillages) {
        // <editor-fold defaultstate="collapsed" desc="Build not assigned source table">

        Collections.sort(pNotAssignedVillages);
        DefaultTableModel sourcesModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Spieler", "Dorf"}) {

            Class[] types = new Class[]{
                Tribe.class, Village.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        for (Village notAssigned : pNotAssignedVillages) {
            Tribe t = notAssigned.getTribe();
            if (t == null) {
                sourcesModel.addRow(new Object[]{"Barbaren", notAssigned});
            } else {
                sourcesModel.addRow(new Object[]{t, notAssigned});
            }
        }
        jNotAssignedSourcesTable.setModel(sourcesModel);
        TableRowSorter<TableModel> sourcesSorter = new TableRowSorter<TableModel>(sourcesModel);
        jNotAssignedSourcesTable.setRowSorter(sourcesSorter);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                String t = ((DefaultTableCellRenderer) c).getText();
                ((DefaultTableCellRenderer) c).setText(t);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };
        for (int i = 0; i < jNotAssignedSourcesTable.getColumnCount(); i++) {
            jNotAssignedSourcesTable.getColumn(jNotAssignedSourcesTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        jNotAssignedSourcesTable.revalidate();
//</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Build attacks per target table">


        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Spieler", "Dorf", "Angriffe"}) {

            Class[] types = new Class[]{
                Tribe.class, Village.class, Integer.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        List<Village> notFullTargets = new LinkedList<Village>();
        Enumeration<Village> keys = attackMappings.keys();
        int max = mSettingsPanel.getAttacksPerVillage();
        while (keys.hasMoreElements()) {
            Village key = keys.nextElement();
            Tribe t = key.getTribe();
            int cnt = attackMappings.get(key);
            if (t != null) {
                tableModel.addRow(new Object[]{t, key, cnt});
            } else {
                tableModel.addRow(new Object[]{"Barbaren", key, cnt});
            }
            if (cnt < max) {
                notFullTargets.add(key);
            }
        }

        jTargetDetailsTable.setModel(tableModel);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);

        jTargetDetailsTable.setRowSorter(sorter);
        DefaultTableCellRenderer coloredRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, row);
                String t = ((DefaultTableCellRenderer) c).getText();
                ((DefaultTableCellRenderer) c).setText(t);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int r = table.convertRowIndexToModel(row);
                Integer v = (Integer) model.getValueAt(r, 2);
                int max = mSettingsPanel.getAttacksPerVillage();
                long diff = max - v;
                Color back = Color.RED;
                if (v == 0) {
                    //color stays red
                } else if (v == max) {
                    back = Color.GREEN;
                } else {
                    float posv = 100.0f * (float) diff / (float) max;
                    posv = (int) ((int) posv / 10) * 10;
                    posv /= 100;
                    Color LAST_SEGMENT = new Color(255, 100, 0);
                    int red = (int) Math.rint((float) LAST_SEGMENT.getRed() * (1.0f - posv) + (float) Color.YELLOW.getRed() * posv);
                    int green = (int) Math.rint((float) LAST_SEGMENT.getGreen() * (1.0f - posv) + (float) Color.YELLOW.getGreen() * posv);
                    int blue = (int) Math.rint((float) LAST_SEGMENT.getBlue() * (1.0f - posv) + (float) Color.YELLOW.getBlue() * posv);
                    back = new Color(red, green, blue);
                }
                DefaultTableCellRenderer renderer = ((DefaultTableCellRenderer) c);
                if (!isSelected) {
                    renderer.setBackground(back);
                }
                return c;
            }
        };
        jTargetDetailsTable.setDefaultRenderer(Village.class, coloredRenderer);
        jTargetDetailsTable.setDefaultRenderer(Integer.class, coloredRenderer);
        jTargetDetailsTable.setDefaultRenderer(String.class, coloredRenderer);
        jTargetDetailsTable.setDefaultRenderer(Tribe.class, coloredRenderer);


        for (int i = 0; i < jTargetDetailsTable.getColumnCount(); i++) {
            jTargetDetailsTable.getColumn(jTargetDetailsTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }
        jTargetDetailsTable.revalidate();
        //</editor-fold>
    }

// <editor-fold defaultstate="collapsed" desc="Source selection handlers">
    private void fireFilterSourceVillagesByGroupEvent() {
        List<Village> villageList = getGroupFilteredSourceVillages();
        //build continents list
        List<String> continentList = new LinkedList<String>();
        for (Village v : villageList) {
            String cont = "K" + v.getContinent();
            if (!continentList.contains(cont)) {
                continentList.add(cont);
            }

        }

        Collections.sort(continentList, String.CASE_INSENSITIVE_ORDER);
        DefaultListModel contModel = new DefaultListModel();
        for (String cont : continentList) {
            contModel.addElement(cont);
        }

//set continents list -> village list updates automatically via continent list listener
        jSourceContinentList.setModel(contModel);
        jSourceContinentList.getSelectionModel().setSelectionInterval(0, continentList.size() - 1);
    }

    private void fireFilterSourceContinentEvent() {
        int[] conts = jSourceContinentList.getSelectedIndices();
        if (conts == null) {
            return;
        }
//build list of allowed continents

        List<Integer> allowedContinents = new LinkedList<Integer>();
        for (Integer cont : conts) {
            int contId = Integer.parseInt(((String) jSourceContinentList.getModel().getElementAt(cont)).replaceAll("K", ""));
            allowedContinents.add(contId);
        }

        List<Village> villageList = getGroupFilteredSourceVillages();

        try {
            List<Village> toRemove = new LinkedList<Village>();
            for (Village v : villageList) {
                int vCont = 0;
                if (ServerSettings.getSingleton().getCoordType() != 2) {
                    vCont = DSCalculator.xyToHierarchical(v.getX(), v.getY())[0];
                } else {
                    vCont = DSCalculator.getContinent(v.getX(), v.getY());
                }

                if (!allowedContinents.contains(vCont)) {
                    toRemove.add(v);
                }

            }

            //remove villages with wrong continent
            for (Village v : toRemove) {
                villageList.remove(v);
            }

//build village list
            DefaultListModel villageModel = new DefaultListModel();
            for (Village v : villageList) {
                villageModel.addElement(v);
            }

            jSourceVillageList.setModel(villageModel);
            jSourceVillageList.repaint();//.updateUI();
        } catch (Exception e) {
        }
    }

    private List<Village> getGroupFilteredSourceVillages() {
        Object[] values = (Object[]) jVillageGroupList.getSelectedValues();
        List<Village> villageList = new LinkedList<Village>();

        if (jVillageGroupList.isEnabled()) {
            List<Tag> tags = new LinkedList<Tag>();
            boolean useNoTag = false;
            for (Object o : values) {
                try {
                    tags.add((Tag) o);
                } catch (Exception e) {
                    //no-tag villages contained
                    useNoTag = true;
                }

            }
            boolean onlyPlayerVillages = jPlayerSourcesOnlyBox.isSelected();
            Tribe current = DSWorkbenchMainFrame.getSingleton().getCurrentUser();
            if (jSourceGroupRelation.isSelected()) {

                //default OR relation
                //tags available, use them
                for (Tag t : tags) {
                    for (Integer vId : t.getVillageIDs()) {
                        Village v = DataHolder.getSingleton().getVillagesById().get(vId);
                        if (v != null && v.getTribe() != null && !villageList.contains(v)) {
                            //add only if a players villages is tagged
                            if (!onlyPlayerVillages || (onlyPlayerVillages && v.getTribe().equals(current))) {
                                //use village if all villages are allowed or if owner is current player
                                villageList.add(v);
                            }

                        }
                    }
                }
                if (useNoTag) {
                    //use villages of current user which are not tagged
                    Village[] villages = DSWorkbenchMainFrame.getSingleton().getCurrentUser().getVillageList();
                    for (Village v : villages) {
                        List<Tag> vtags = TagManager.getSingleton().getTags(v);
                        if (vtags == null || vtags.isEmpty() && !villageList.contains(v)) {
                            if (!onlyPlayerVillages || (onlyPlayerVillages && v.getTribe().equals(current))) {
                                //use village if all villages are allowed or if owner is current player
                                villageList.add(v);
                            }

                        }

                    }
                }
            } else {
                //AND relation
                if (tags.isEmpty() && useNoTag) {
                    //only use non tagges villages
                    //-> if one tag is selected for AND relation, ignore non-tagged due to logical error
                    if (useNoTag) {
                        //use villages of current user which are not tagged
                        Village[] villages = DSWorkbenchMainFrame.getSingleton().getCurrentUser().getVillageList();
                        for (Village v : villages) {
                            List<Tag> vtags = TagManager.getSingleton().getTags(v);
                            if (vtags == null || vtags.isEmpty() && !villageList.contains(v)) {
                                if (!onlyPlayerVillages || (onlyPlayerVillages && v.getTribe().equals(current))) {
                                    //use village if all villages are allowed or if owner is current player
                                    villageList.add(v);
                                }

                            }

                        }
                    }
                } else {
                    //perform AND relation
                    List<Integer> villageIds = new LinkedList<Integer>();
                    //get all villages tagged by any tag
                    for (Tag t : TagManager.getSingleton().getTags()) {
                        for (Integer i : t.getVillageIDs()) {
                            if (!villageIds.contains(i)) {
                                villageIds.add(i);
                            }

                        }
                    }
                    //check for all villages, if they are tagged by all selected tags
                    for (Integer i : villageIds) {
                        boolean use = true;
                        for (Tag t : tags) {
                            if (!t.tagsVillage(i)) {
                                //at least one tag does not tag current village
                                use = false;
                                break;

                            }




                        }
                        if (use) {
                            //all tags tag current village, so use them
                            Village v = DataHolder.getSingleton().getVillagesById().get(i);
                            if (!villageList.contains(v)) {
                                if (!onlyPlayerVillages || (onlyPlayerVillages && v.getTribe().equals(current))) {
                                    //use village if all villages are allowed or if owner is current player
                                    villageList.add(v);
                                }

                            }
                        }
                    }
                }
            }
        } else {
            //no tags available, take current users villages
            Village[] villages = DSWorkbenchMainFrame.getSingleton().getCurrentUser().getVillageList();
            for (Village v : villages) {
                //use village if all villages are allowed or if owner is current player
                villageList.add(v);
            }

        }
        return villageList;
    }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Target selection handlers">
    private void fireFilterTargetByAllyEvent() {
        Ally a = null;
        try {
            a = (Ally) jTargetAllyList.getSelectedValue();
        } catch (Exception e) {
        }

        if (a != null) {
            //ally selected
            Tribe[] tribes = a.getTribes().toArray(new Tribe[]{});
            if ((tribes != null) && (tribes.length != 0)) {
                Arrays.sort(tribes, Tribe.CASE_INSENSITIVE_ORDER);
                jTargetTribeList.setModel(new DefaultComboBoxModel(tribes));
                jTargetTribeList.setSelectedIndex(0);
            } else {
                //not tribes in ally -> should never happen
                jTargetTribeList.setModel(new DefaultListModel());
                jTargetContinentList.setModel(new DefaultListModel());
                jTargetVillageList.setModel(new DefaultListModel());
            }

        } else {
            //no ally selected, show no-ally tribes
            Enumeration<Integer> tribeIDs = DataHolder.getSingleton().getTribes().keys();
            List<Tribe> noAlly = new LinkedList<Tribe>();
            while (tribeIDs.hasMoreElements()) {
                Tribe t = DataHolder.getSingleton().getTribes().get(tribeIDs.nextElement());
                if (t.getAlly() == null && t.getVillageList() != null && t.getVillageList().length > 0) {
                    //only add tribes which are attackable
                    noAlly.add(t);
                }

            }
            Tribe[] noAllyTribes = noAlly.toArray(new Tribe[]{});
            Arrays.sort(noAllyTribes, Tribe.CASE_INSENSITIVE_ORDER);
            jTargetTribeList.setModel(new DefaultComboBoxModel(noAllyTribes));
            jTargetTribeList.setSelectedIndex(0);
        }

    }

    private void fireFilterTargetByTribeEvent() {
        try {
            Tribe t = (Tribe) jTargetTribeList.getSelectedValue();
            if (t != null && t.getVillageList() != null && t.getVillageList().length > 0) {
                Village[] villages = t.getVillageList();
                List<String> continents = new LinkedList<String>();

                for (Village v : villages) {
                    int cont = 0;
                    if (ServerSettings.getSingleton().getCoordType() != 2) {
                        cont = DSCalculator.xyToHierarchical(v.getX(), v.getY())[0];
                    } else {
                        cont = DSCalculator.getContinent(v.getX(), v.getY());
                    }

                    String contString = "K" + cont;
                    if (!continents.contains(contString)) {
                        continents.add(contString);
                    }

                }
                Collections.sort(continents, String.CASE_INSENSITIVE_ORDER);

                DefaultListModel contModel = new DefaultListModel();
                for (String cont : continents) {
                    contModel.addElement(cont);
                }

                jTargetContinentList.setModel(contModel);
                jTargetContinentList.repaint();//.updateUI();
                jTargetContinentList.getSelectionModel().setSelectionInterval(0, continents.size() - 1);
            } else {
                //no tribe selected -> should never happen!
                jTargetContinentList.setModel(new DefaultListModel());
                jTargetVillageList.setModel(new DefaultListModel());
            }

        } catch (Exception e) {
            jTargetContinentList.setModel(new DefaultListModel());
            jTargetVillageList.setModel(new DefaultListModel());
        }

    }

    private void fireFilterTargetByContinentEvent() {
        //build list of selected/valid continents
        Object[] conts = jTargetContinentList.getSelectedValues();
        List<Integer> validConts = new LinkedList<Integer>();
        for (Object cont : conts) {
            String c = (String) cont;
            c =
                    c.replaceAll("K", "").trim();
            validConts.add(Integer.parseInt(c));
        }

        Tribe t = (Tribe) jTargetTribeList.getSelectedValue();
        Village[] villages = t.getVillageList();
        Arrays.sort(villages, Village.CASE_INSENSITIVE_ORDER);
        DefaultListModel villageModel = new DefaultListModel();
        for (Village v : villages) {
            int cont = 0;
            if (ServerSettings.getSingleton().getCoordType() != 2) {
                cont = DSCalculator.xyToHierarchical(v.getX(), v.getY())[0];
            } else {
                cont = DSCalculator.getContinent(v.getX(), v.getY());
            }

            if (validConts.contains(cont)) {
                villageModel.addElement(v);
            }

        }
        jTargetVillageList.setModel(villageModel);
    }
// </editor-fold>

    public void fireCalculationFinishedEvent(AbstractAttackAlgorithm pParent) {
        jCalculatingProgressBar.setIndeterminate(false);
        jCalculatingProgressBar.setString("Berechnung abgeschlossen");
        jCalculateButton.setEnabled(true);
        List<Attack> attackList = new LinkedList<Attack>();
        List<Village> targets = new LinkedList<Village>();

        // <editor-fold defaultstate="collapsed" desc=" Post processing ">
        int validEnoblements = 0;

        // <editor-fold defaultstate="collapsed" desc="Additional assignment">

        logger.debug("Algorithm post-processing skipped");
        for (AbstractTroopMovement movement : pParent.getResults()) {
            List<Attack> atts = null;
            atts = movement.getAttacks(pParent.getTimeFrame());
            for (Attack attack : atts) {
                attackList.add(attack);
                if (!targets.contains(attack.getTarget())) {
                    targets.add(attack.getTarget());
                }

            }
        }

        Hashtable<Village, Integer> attackMappings = new Hashtable<Village, Integer>();
        //get targets and attack count
        for (int i = 0; i < jVictimTable.getRowCount(); i++) {
            attackMappings.put((Village) jVictimTable.getValueAt(i, 1), 0);
        }

        for (Attack a : attackList) {
            Village v = a.getTarget();
            Integer val = attackMappings.get(v);
            attackMappings.put(v, val + 1);
        }


// </editor-fold>
        int numOutputTargets = targets.size();
        int fullOffs = pParent.getFullOffs();
        int calculatedAttacks = attackList.size();

        jTargetsBar.setMaximum(jVictimTable.getRowCount());
        jTargetsBar.setValue(numOutputTargets);
        jTargetsBar.setString(numOutputTargets + " / " + jVictimTable.getRowCount());


        jFullOffsBar.setMaximum(pParent.getResults().size());
        jFullOffsBar.setValue(fullOffs);
        jFullOffsBar.setString(fullOffs + " / " + pParent.getResults().size());
        jAttacksBar.setMaximum(jAttacksTable.getRowCount());
        jAttacksBar.setValue(calculatedAttacks);
        jAttacksBar.setString(calculatedAttacks + " / " + jAttacksTable.getRowCount());

        // <editor-fold defaultstate="collapsed" desc="Building details tables">
        buildDetailedStatistics(attackMappings, pParent.getNotAssignedSources());
        //</editor-fold>

        logger.debug("Sorting attacks by runtime");
        //sort result by start time
        Collections.sort(attackList, AbstractTroopMovement.RUNTIME_SORT);
        logger.debug("Building results...");

        showResults(attackList);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new TribeTribeAttackFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup attackTypeGroup;
    private javax.swing.JButton jAddToAttacksButton;
    private javax.swing.JButton jApplyFiltersButton;
    private javax.swing.JDialog jAttackPlanSelectionDialog;
    private javax.swing.JTable jAttackPlanTable;
    private javax.swing.JComboBox jAttackPlansBox;
    private javax.swing.JFrame jAttackResultDetailsFrame;
    private javax.swing.JProgressBar jAttacksBar;
    private javax.swing.JTable jAttacksTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jCalculateButton;
    private javax.swing.JProgressBar jCalculatingProgressBar;
    private javax.swing.JButton jCancelSyncButton;
    private javax.swing.JButton jCloseResultsButton;
    private javax.swing.JButton jCopyToClipboardAsBBButton;
    private javax.swing.JButton jCopyToClipboardButton;
    private javax.swing.JButton jDoSyncButton;
    private javax.swing.JProgressBar jEnoblementsBar;
    private javax.swing.JFrame jFilterFrame;
    private javax.swing.JList jFilterList;
    private javax.swing.JButton jFilterSourceButton;
    private javax.swing.JButton jFilterTargetButton;
    private javax.swing.JComboBox jFilterUnitBox;
    private javax.swing.JProgressBar jFullOffsBar;
    private javax.swing.JButton jHideAttackDetailsButton;
    private javax.swing.JButton jHideTargetDetailsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox jMarkAsFakeBox;
    private javax.swing.JTextField jMaxValue;
    private javax.swing.JTextField jMinValue;
    private javax.swing.JTextField jNewPlanName;
    private javax.swing.JButton jNextSelectionButton;
    private javax.swing.JTable jNotAssignedSourcesTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JCheckBox jPlayerSourcesOnlyBox;
    private javax.swing.JButton jPrevSelectionButton;
    private javax.swing.JFrame jResultFrame;
    private javax.swing.JTable jResultsTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JButton jSelectButton;
    private javax.swing.JButton jSelectionBeginButton;
    private javax.swing.JTextField jSelectionEnd;
    private javax.swing.JButton jSelectionEndButton;
    private javax.swing.JTextField jSelectionStart;
    private javax.swing.JButton jSetFakeButton;
    private javax.swing.JButton jSetNoFakeButton;
    private javax.swing.JList jSourceContinentList;
    private javax.swing.JRadioButton jSourceGroupRelation;
    private javax.swing.JPanel jSourcePanel;
    private javax.swing.JLabel jSourceVillageLabel1;
    private javax.swing.JLabel jSourceVillageLabel2;
    private javax.swing.JList jSourceVillageList;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jTargetAllyLabel;
    private javax.swing.JList jTargetAllyList;
    private javax.swing.JList jTargetContinentList;
    private javax.swing.JTable jTargetDetailsTable;
    private javax.swing.JPanel jTargetPanel;
    private javax.swing.JFrame jTargetResultDetailsFrame;
    private javax.swing.JTextField jTargetTribeFilter;
    private javax.swing.JList jTargetTribeList;
    private javax.swing.JList jTargetVillageList;
    private javax.swing.JProgressBar jTargetsBar;
    private javax.swing.JDialog jTransferToAttackManagerDialog;
    private javax.swing.JComboBox jTroopsList;
    private javax.swing.JTable jVictimTable;
    private javax.swing.JList jVillageGroupList;
    // End of variables declaration//GEN-END:variables
    }

class TroopFilterElement {

    private UnitHolder unit = null;
    private int minAmount = 0;
    private int maxAmount = 0;

    public TroopFilterElement(UnitHolder pUnit, int pMin, int pMax) {
        unit = pUnit;
        if (pMin < 0) {
            minAmount = Integer.MIN_VALUE;
        } else {
            minAmount = pMin;
        }

        if (pMax < 0) {
            //no max
            pMax = Integer.MAX_VALUE;
        } else {
            maxAmount = pMax;
        }
    }

    public UnitHolder getUnit() {
        return unit;
    }

    public int getMin() {
        return minAmount;
    }

    public int getMax() {
        return maxAmount;
    }

    public void setMin(int pMin) {
        minAmount = pMin;
    }

    public void setMax(int pMax) {
        maxAmount = pMax;
    }

    public boolean allowsVillage(Village pVillage) {
        VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage);
        if (holder == null) {
            return false;
        }

        int amount = holder.getOwnTroops().get(getUnit());
        return (amount >= minAmount && amount <= maxAmount);
    }

    @Override
    public String toString() {
        String res = "";
        res += unit.toString();
        if (minAmount == Integer.MIN_VALUE && maxAmount == Integer.MAX_VALUE) {
            res += " (keine Einschränkung)";
        } else if (minAmount == Integer.MIN_VALUE && maxAmount != Integer.MAX_VALUE) {
            res += " (max. " + maxAmount + ")";
        } else if (minAmount != Integer.MIN_VALUE && maxAmount == Integer.MAX_VALUE) {
            res += " (min. " + minAmount + ")";
        } else {
            res += " (" + minAmount + " bis " + maxAmount + ")";
        }
        return res;
    }
}


