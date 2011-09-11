/*
 * DSWorkbenchAttackFrame.java
 *
 * Created on 28. September 2008, 14:58
 */
package de.tor.tribes.ui.views;

import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.TabEditingEvent;
import com.jidesoft.swing.TabEditingListener;
import com.jidesoft.swing.TabEditingValidator;
import com.smardec.mousegestures.MouseGestures;
import de.tor.tribes.control.GenericManagerListener;
import de.tor.tribes.control.ManageableType;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.test.DummyUnit;
import de.tor.tribes.types.StandardAttackElement;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.AbstractDSWorkbenchFrame;
import de.tor.tribes.ui.AttackTableTab;
import de.tor.tribes.ui.GenericTestPanel;
import de.tor.tribes.ui.NotifierFrame;
import de.tor.tribes.ui.editors.StandardAttackElementEditor;
import de.tor.tribes.ui.models.StandardAttackTableModel;
import java.util.Enumeration;
import java.util.Hashtable;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.ui.renderer.StandardAttackTypeCellRenderer;
import de.tor.tribes.ui.renderer.UnitTableHeaderRenderer;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.ImageUtils;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.MouseGestureHandler;
import de.tor.tribes.util.ProfileManager;
import de.tor.tribes.util.ProfileManagerListener;
import de.tor.tribes.util.PropertyHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.TableColumnExt;

// -Dsun.java2d.d3d=true -Dsun.java2d.translaccel=true -Dsun.java2d.ddforcevram=true
/**
 * @author  Charon
 */
public class DSWorkbenchAttackFrame extends AbstractDSWorkbenchFrame implements GenericManagerListener, ActionListener, ProfileManagerListener, Serializable {

    @Override
    public void fireProfilesLoadedEvent() {
        UserProfile[] profiles = ProfileManager.getSingleton().getProfiles(GlobalOptions.getSelectedServer());

        DefaultComboBoxModel model = new DefaultComboBoxModel(new Object[]{"Standard"});
        if (profiles != null && profiles.length > 0) {
            for (UserProfile profile : profiles) {
                model.addElement(profile);
            }
        }
        jProfileBox.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttackTableTab activeTab = getActiveTab();
        int idx = jAttackTabPane.getSelectedIndex();
        if (e.getActionCommand() != null && activeTab != null) {
            if (e.getActionCommand().equals("TimeChange")) {
                activeTab.fireChangeTimeEvent();
            } else if (e.getActionCommand().equals("UnitChange")) {
                activeTab.fireChangeUnitEvent();
            } else if (e.getActionCommand().equals("SendIGM")) {
                activeTab.fireSendIGMEvent();
            } else if (e.getActionCommand().equals("ExportScript")) {
                activeTab.fireExportScriptEvent();
            } else if (e.getActionCommand().equals("Copy")) {
                activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.COPY_TO_INTERNAL_CLIPBOARD);
            } else if (e.getActionCommand().equals("BBCopy")) {
                activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.CLIPBOARD_BB);
            } else if (e.getActionCommand().equals("Cut")) {
                activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.CUT_TO_INTERNAL_CLIPBOARD);
                jAttackTabPane.setSelectedIndex(idx);
            } else if (e.getActionCommand().equals("Paste")) {
                activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.FROM_INTERNAL_CLIPBOARD);
                jAttackTabPane.setSelectedIndex(idx);
            } else if (e.getActionCommand().equals("Delete")) {
                activeTab.deleteSelection(true);
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
                DefaultListModel model = new DefaultListModel();

                for (int i = 0; i < activeTab.getAttackTable().getColumnCount(); i++) {
                    TableColumnExt col = activeTab.getAttackTable().getColumnExt(i);
                    if (col.isVisible()) {
                        if (!col.getTitle().equals("Einheit") && !col.getTitle().equals("Typ") && !col.getTitle().equals("Sonstiges")
                                && !col.getTitle().equals("Abschickzeit") && !col.getTitle().equals("Ankunftzeit") && !col.getTitle().equals("Verbleibend")) {
                            model.addElement(col.getTitle());
                        }
                    }
                }
                jXColumnList.setModel(model);
                jXColumnList.setSelectedIndex(0);
                jxSearchPane.setVisible(true);
            }
        }
    }
    private static Logger logger = Logger.getLogger("AttackView");
    private static DSWorkbenchAttackFrame SINGLETON = null;
    private NotifyThread mNotifyThread = null;
    private CountdownThread mCountdownThread = null;
    private int iClickAccount = 0;
    private GenericTestPanel centerPanel = null;

    public static synchronized DSWorkbenchAttackFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchAttackFrame();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchAttackFrame */
    DSWorkbenchAttackFrame() {
        initComponents();
        centerPanel = new GenericTestPanel();
        jAttackPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setChildComponent(jXAttackPanel);
        fireProfilesLoadedEvent();
        buildMenu();

        jAttackTabPane.setCloseAction(new AbstractAction("closeAction") {

            public void actionPerformed(ActionEvent e) {
                AttackTableTab tab = (AttackTableTab) e.getSource();
                if (JOptionPaneHelper.showQuestionConfirmBox(jAttackTabPane, "Angriffsplan '" + tab.getAttackPlan() + "' und alle darin enthaltenen Angriffe wirklich löschen? ", "Löschen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
                    AttackManager.getSingleton().removeGroup(tab.getAttackPlan());
                }
            }
        });
        jAttackTabPane.addTabEditingListener(new TabEditingListener() {

            @Override
            public void editingStarted(TabEditingEvent tee) {
            }

            @Override
            public void editingStopped(TabEditingEvent tee) {
                AttackManager.getSingleton().renameGroup(tee.getOldTitle(), tee.getNewTitle());
            }

            @Override
            public void editingCanceled(TabEditingEvent tee) {
            }
        });
        jAttackTabPane.setTabShape(JideTabbedPane.SHAPE_OFFICE2003);
        jAttackTabPane.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);
        jAttackTabPane.setBoldActiveTab(true);
        jAttackTabPane.setTabEditingValidator(new TabEditingValidator() {

            @Override
            public boolean alertIfInvalid(int tabIndex, String tabText) {
                if (tabText.trim().length() == 0) {
                    JOptionPaneHelper.showWarningBox(jAttackTabPane, "'" + tabText + "' ist ein ungültiger Planname", "Fehler");
                    return false;
                }

                if (AttackManager.getSingleton().groupExists(tabText)) {
                    JOptionPaneHelper.showWarningBox(jAttackTabPane, "Es existiert bereits ein Plan mit dem Namen '" + tabText + "'", "Fehler");
                    return false;
                }
                return true;
            }

            @Override
            public boolean isValid(int tabIndex, String tabText) {
                if (tabText.trim().length() == 0) {
                    return false;
                }

                if (AttackManager.getSingleton().groupExists(tabText)) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean shouldStartEdit(int tabIndex, MouseEvent event) {
                return !(tabIndex == 0 || tabIndex == 1);
            }
        });

        mNotifyThread = new NotifyThread();
        new ColorUpdateThread().start();
        mNotifyThread.start();
        mCountdownThread = new CountdownThread();
        mCountdownThread.start();
        jXColumnList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateFilter();
            }
        });


        jAttackTabPane.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.updatePlan();
                }
            }
        });

        ProfileManager.getSingleton().addProfileManagerListener(DSWorkbenchAttackFrame.this);
        jStandardAttackDialog.pack();
        setGlassPane(jxSearchPane);
        pack();
        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.attack_view", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelpKey(jStandardAttackDialog, "pages.standard_attacks", GlobalOptions.getHelpBroker().getHelpSet());
        }       // </editor-fold>
    }

    public void storeCustomProperties(Configuration pConfig) {
        pConfig.setProperty(getPropertyPrefix() + ".menu.visible", centerPanel.isMenuVisible());
        pConfig.setProperty(getPropertyPrefix() + ".alwaysOnTop", jAttackFrameAlwaysOnTop.isSelected());

        int selectedIndex = jAttackTabPane.getModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            pConfig.setProperty(getPropertyPrefix() + ".tab.selection", selectedIndex);
        }


        AttackTableTab tab = ((AttackTableTab) jAttackTabPane.getComponentAt(0));
        PropertyHelper.storeTableProperties(tab.getAttackTable(), pConfig, getPropertyPrefix());
    }

    public void restoreCustomProperties(Configuration pConfig) {
        centerPanel.setMenuVisible(pConfig.getBoolean(getPropertyPrefix() + ".menu.visible", true));
        try {
            jAttackTabPane.setSelectedIndex(pConfig.getInteger(getPropertyPrefix() + ".tab.selection", 0));
        } catch (Exception e) {
        }
        try {
            jAttackFrameAlwaysOnTop.setSelected(pConfig.getBoolean(getPropertyPrefix() + ".alwaysOnTop"));
        } catch (Exception e) {
        }

        setAlwaysOnTop(jAttackFrameAlwaysOnTop.isSelected());

        AttackTableTab tab = ((AttackTableTab) jAttackTabPane.getComponentAt(0));
        PropertyHelper.restoreTableProperties(tab.getAttackTable(), pConfig, getPropertyPrefix());
    }

    public String getPropertyPrefix() {
        return "attack.view";
    }

    public JDialog getStandardAttackDialog() {
        return jStandardAttackDialog;
    }

    /**Get the currently selected tab*/
    private AttackTableTab getActiveTab() {
        try {
            if (jAttackTabPane.getModel().getSelectedIndex() < 0) {
                return null;
            }
            return ((AttackTableTab) jAttackTabPane.getComponentAt(jAttackTabPane.getModel().getSelectedIndex()));
        } catch (ClassCastException cce) {
            return null;
        }
    }

    /**Get the currently active attack plan
     * @return
     */
    public String getActivePlan() {
        AttackTableTab tab = getActiveTab();
        if (tab == null) {
            return AttackManager.DEFAULT_GROUP;
        }
        return tab.getAttackPlan();
    }

    /**Build the main menu for this frame*/
    private void buildMenu() {
        // <editor-fold defaultstate="collapsed" desc="Edit task pane">
        JXTaskPane editTaskPane = new JXTaskPane();
        editTaskPane.setTitle("Bearbeiten");
        editTaskPane.getContentPane().add(factoryButton("/res/ui/garbage.png", "Abgelaufene Angriffe entfernen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.cleanup();
                }
            }
        }));
        editTaskPane.getContentPane().add(factoryButton("/res/ui/att_changeTime.png", "Ankunftszeit für markierte Angriffe &auml;ndern. Die Startzeit der Angriffe wird dabei entsprechend der Laufzeit angepasst", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.changeSelectionTime();
                }
            }
        }));
        editTaskPane.getContentPane().add(factoryButton("/res/ui/standard_attacks.png", "Einheit und Angriffstyp für markierte Angriffe &auml;ndern. Bitte beachte, dass sich beim &Auml;ndern der Einheit auch die Startzeit der Angriffe &auml;ndern kann", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.changeSelectionType();
                }
            }
        }));
        editTaskPane.getContentPane().add(factoryButton("/res/ui/att_browser_unsent.png", "'&Uuml;bertragen' Feld für markierte Angriffe l&ouml;schen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.setSelectionUnsent();
                }
            }
        }));
        editTaskPane.getContentPane().add(factoryButton("/res/ui/pencil2.png", "Markierte Angriffe auf der Karte einzeichen. Ist ein gewählter Angriff bereits eingezeichnet, so wird er nach Bet&auml;tigung dieses Buttons nicht mehr eingezeichnet", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.changeSelectionDrawState();
                }
            }
        }));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Transfer task pane">
        JXTaskPane transferTaskPane = new JXTaskPane();
        transferTaskPane.setTitle("Übertragen");
        transferTaskPane.getContentPane().add(factoryButton("/res/ui/att_clipboard.png", "Markierte Angriffe im Klartext in die Zwischenablage kopieren. Der Inhalt der Zwischenablage kann dann z.B. in Excel oder OpenOffice eingef&uuml;gt werden", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.CLIPBOARD_PLAIN);
                }
            }
        }));

        transferTaskPane.getContentPane().add(factoryButton("/res/ui/att_HTML.png", "Markierte Angriffe in eine HTML Datei kopieren.<br/>Die erstellte Datei kann dann per eMail verschickt oder zum Abschicken von Angriffen ohne ge&ouml;ffnetesDS Workbench verwendet werden", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.FILE_HTML);
                }
            }
        }));
        transferTaskPane.getContentPane().add(factoryButton("/res/ui/atts_igm.png", "Markierte Angriffe als IGM verschicken. (PA notwendig) Der/die Empf&auml;nger der IGMs sind die Besitzer der Herkunftsd&ouml;rfer der geplanten Angriffe.", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.BROWSER_IGM);
                }
            }
        }));
        transferTaskPane.getContentPane().add(factoryButton("/res/ui/re-time.png", "Markierten Angriff in das Werkzeug 'Retimer' einfügen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.DSWB_RETIME);
                }
            }
        }));
        transferTaskPane.getContentPane().add(factoryButton("/res/ui/att_browser.png", "Markierte Angriffe in den Browser &uuml;bertragen. Im Normalfall werden nur einzelne Angriffe &uuml;bertragen. F&uuml;r das &Uuml;bertragen mehrerer Angriffe ist zuerst das Klickkonto entsprechend zu f&uuml;llen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.BROWSER_LINK);
                }
            }
        }));
        transferTaskPane.getContentPane().add(factoryButton("/res/ui/export_js.png", "Markierte Angriffe in ein Userscript schreiben.Das erstellte Userscript muss im Anschluss manuell im Browser installiert werden. Als Ergebnis bekommt man an verschiedenen Stellen im Spiel Informationen &uuml;ber geplante Angriffe angezeigt.", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                AttackTableTab activeTab = getActiveTab();
                if (activeTab != null) {
                    activeTab.transferSelection(AttackTableTab.TRANSFER_TYPE.FILE_GM);
                }
            }
        }));
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Misc task pane">

        JXTaskPane miscTaskPane = new JXTaskPane();
        miscTaskPane.setTitle("Sonstiges");
        miscTaskPane.getContentPane().add(factoryButton("/res/ui/standard_attacks.png", "Truppenst&auml;rke von Standardangriffen definieren. Diese Einstellungen werden verwendet, wenn man Angriffe in den Browser &uuml;bertr&auml;gt und das entsprechende Userscript 'dswb.user.js' installiert hat, um im ge&ouml;ffneten Versammlungsplatz Truppen bereits einzutragen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                //build table
                jStandardAttackTable.invalidate();
                jStandardAttackTable.setModel(new DefaultTableModel());
                jStandardAttackTable.revalidate();

                jStandardAttackTable.setModel(StandardAttackTableModel.getSingleton());
                for (int i = 0; i < StandardAttackTableModel.getSingleton().getColumnCount(); i++) {
                    jStandardAttackTable.getColumnModel().getColumn(i).setHeaderRenderer(new UnitTableHeaderRenderer());
                }
                jStandardAttackTable.setDefaultEditor(StandardAttackElement.class, new StandardAttackElementEditor());
                jStandardAttackTable.setDefaultRenderer(String.class, new StandardAttackTypeCellRenderer());
                jStandardAttackTable.setRowHeight(20);
                jStandardAttackDialog.setLocationRelativeTo(DSWorkbenchAttackFrame.getSingleton());
                jStandardAttackDialog.setVisible(true);
            }
        }));

        miscTaskPane.getContentPane().add(factoryButton("/res/ui/att_alert_off.png", "Aktiviert eine Warnung f&uuml;r Angriffe, welche in den n&auml;chsten 10 Minuten abgeschickt werden m&uuml;ssen", new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                mNotifyThread.setActive(!mNotifyThread.isActive());
                if (mNotifyThread.isActive()) {
                    ((JXButton) e.getSource()).setIcon(new ImageIcon(DSWorkbenchAttackFrame.class.getResource("/res/ui/att_alert.png")));
                } else {
                    ((JXButton) e.getSource()).setIcon(new ImageIcon(DSWorkbenchAttackFrame.class.getResource("/res/ui/att_alert_off.png")));
                }
            }
        }));
        // </editor-fold>
        centerPanel.setupTaskPane(jClickAccountLabel, jProfileQuickChange, editTaskPane, transferTaskPane, miscTaskPane);
    }

    public UserProfile getQuickProfile() {
        Object o = jProfileBox.getSelectedItem();
        if (o instanceof UserProfile) {
            return (UserProfile) o;
        }
        return null;
    }

    /**Factory a new button*/
    private JXButton factoryButton(String pIconResource, String pTooltip, MouseListener pListener) {
        JXButton button = new JXButton(new ImageIcon(DSWorkbenchAttackFrame.class.getResource(pIconResource)));
        if (pTooltip != null) {
            button.setToolTipText("<html><div width='150px'>" + pTooltip + "</div></html>");
        }
        button.addMouseListener(pListener);
        return button;
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

        jStandardAttackDialog = new javax.swing.JDialog();
        jScrollPane5 = new javax.swing.JScrollPane();
        jStandardAttackTable = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jClickAccountLabel = new javax.swing.JLabel();
        jxSearchPane = new org.jdesktop.swingx.JXPanel();
        jXPanel2 = new org.jdesktop.swingx.JXPanel();
        jButton12 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jFilterRows = new javax.swing.JCheckBox();
        jFilterCaseSensitive = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXColumnList = new org.jdesktop.swingx.JXList();
        jLabel22 = new javax.swing.JLabel();
        jXAttackPanel = new org.jdesktop.swingx.JXPanel();
        jAttackTabPane = new com.jidesoft.swing.JideTabbedPane();
        jNewPlanPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProfileQuickChange = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jProfileBox = new javax.swing.JComboBox();
        jAttackPanel = new javax.swing.JPanel();
        jAttackFrameAlwaysOnTop = new javax.swing.JCheckBox();
        capabilityInfoPanel1 = new de.tor.tribes.ui.CapabilityInfoPanel();

        jStandardAttackDialog.setTitle("Standardangriffe");
        jStandardAttackDialog.setModal(true);

        jStandardAttackTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jStandardAttackTable.setToolTipText("");
        jScrollPane5.setViewportView(jStandardAttackTable);

        jButton11.setText("Schließen");
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyStandardAttacksEvent(evt);
            }
        });

        jScrollPane6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane6.setMaximumSize(new java.awt.Dimension(472, 159));
        jScrollPane6.setMinimumSize(new java.awt.Dimension(472, 159));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(472, 159));

        jTextPane1.setContentType("text/html");
        jTextPane1.setEditable(false);
        jTextPane1.setText("<html><p style=\"margin-top: 0\"> Für die obere Tabelle gibt es vier mögliche Formatvorgaben:<UL><LI><I>Ganze Zahlen</I> (0 &lt;= X &lt;= Max.), um eine feste Anzahl einer Truppenart einzufügen (z.B: '100')<LI><I>Alle</I>, um alle Truppen einzufügen (z.B: 'Alle')\n<LI><I>Alle - X</I>, um alle Truppen abzüglich einer bestimmten Anzahl einzufügen (z.B: 'Alle - 100')\n<LI><I>X%</I>, um einen prozentualen Anteil aller Truppen einzufügen (z.B: '50%')\n</UL> </p></html>");
        jTextPane1.setOpaque(false);
        jScrollPane6.setViewportView(jTextPane1);

        javax.swing.GroupLayout jStandardAttackDialogLayout = new javax.swing.GroupLayout(jStandardAttackDialog.getContentPane());
        jStandardAttackDialog.getContentPane().setLayout(jStandardAttackDialogLayout);
        jStandardAttackDialogLayout.setHorizontalGroup(
            jStandardAttackDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jStandardAttackDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jStandardAttackDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addComponent(jButton11))
                .addContainerGap())
        );
        jStandardAttackDialogLayout.setVerticalGroup(
            jStandardAttackDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jStandardAttackDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton11)
                .addContainerGap())
        );

        jClickAccountLabel.setBackground(new java.awt.Color(255, 255, 255));
        jClickAccountLabel.setFont(new java.awt.Font("sansserif", 0, 11));
        jClickAccountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jClickAccountLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/LeftClick.png"))); // NOI18N
        jClickAccountLabel.setText("Klick-Konto [0]");
        jClickAccountLabel.setToolTipText("0 Klick(s) aufgeladen");
        jClickAccountLabel.setAlignmentY(1.0F);
        jClickAccountLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jClickAccountLabel.setMaximumSize(new java.awt.Dimension(110, 40));
        jClickAccountLabel.setMinimumSize(new java.awt.Dimension(110, 40));
        jClickAccountLabel.setOpaque(true);
        jClickAccountLabel.setPreferredSize(new java.awt.Dimension(110, 40));
        jClickAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFillClickAccountEvent(evt);
            }
        });

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

        jTextField1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fireHighlightEvent(evt);
            }
        });

        jLabel21.setText("Suchbegriff");

        jFilterRows.setText("Nur gefilterte Zeilen anzeigen");
        jFilterRows.setOpaque(false);
        jFilterRows.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireUpdateFilterEvent(evt);
            }
        });

        jFilterCaseSensitive.setText("Groß-/Kleinschreibung beachten");
        jFilterCaseSensitive.setOpaque(false);
        jFilterCaseSensitive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireUpdateFilterEvent(evt);
            }
        });

        jXColumnList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jXColumnList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jXColumnList);

        jLabel22.setText("Spalten");

        javax.swing.GroupLayout jXPanel2Layout = new javax.swing.GroupLayout(jXPanel2);
        jXPanel2.setLayout(jXPanel2Layout);
        jXPanel2Layout.setHorizontalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jFilterRows, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jFilterCaseSensitive, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton12)))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jXPanel2Layout.setVerticalGroup(
            jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jXPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jXPanel2Layout.createSequentialGroup()
                            .addComponent(jFilterCaseSensitive)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jFilterRows)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton12))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel22))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jxSearchPane.add(jXPanel2, new java.awt.GridBagConstraints());

        jXAttackPanel.setLayout(new java.awt.BorderLayout());

        jAttackTabPane.setScrollSelectedTabOnWheel(true);
        jAttackTabPane.setShowCloseButtonOnTab(true);
        jAttackTabPane.setShowGripper(true);
        jAttackTabPane.setTabEditingAllowed(true);
        jXAttackPanel.add(jAttackTabPane, java.awt.BorderLayout.CENTER);

        jNewPlanPanel.setOpaque(false);
        jNewPlanPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/document_new_24x24.png"))); // NOI18N
        jLabel1.setToolTipText("Leeren Angriffsplan erstellen");
        jLabel1.setEnabled(false);
        jLabel1.setMaximumSize(new java.awt.Dimension(40, 40));
        jLabel1.setMinimumSize(new java.awt.Dimension(40, 40));
        jLabel1.setPreferredSize(new java.awt.Dimension(40, 40));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireEnterEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCreateAttackPlanEvent(evt);
            }
        });
        jNewPlanPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        jProfileQuickChange.setBackground(new java.awt.Color(255, 255, 255));
        jProfileQuickChange.setLayout(new java.awt.GridBagLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Profil-Schnellauswahl");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jProfileQuickChange.add(jLabel2, gridBagConstraints);

        jProfileBox.setToolTipText("Erlaubt die Schnellauswahl des Benutzerprofils mit dem Angriffe in den Browser übertragen werden");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jProfileQuickChange.add(jProfileBox, gridBagConstraints);

        setTitle("Angriffe");
        setMinimumSize(new java.awt.Dimension(700, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jAttackPanel.setBackground(new java.awt.Color(239, 235, 223));
        jAttackPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        jAttackPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jAttackPanel, gridBagConstraints);

        jAttackFrameAlwaysOnTop.setText("Immer im Vordergrund");
        jAttackFrameAlwaysOnTop.setOpaque(false);
        jAttackFrameAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireAttackFrameAlwaysOnTopEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jAttackFrameAlwaysOnTop, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(capabilityInfoPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireFillClickAccountEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireFillClickAccountEvent
    iClickAccount++;
    updateClickAccount();
}//GEN-LAST:event_fireFillClickAccountEvent

private void fireHideGlassPaneEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideGlassPaneEvent
    jxSearchPane.setBackgroundPainter(null);
    jxSearchPane.setVisible(false);
}//GEN-LAST:event_fireHideGlassPaneEvent
private void fireHighlightEvent(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fireHighlightEvent
    updateFilter();
}//GEN-LAST:event_fireHighlightEvent

private void fireUpdateFilterEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireUpdateFilterEvent
    updateFilter();
}//GEN-LAST:event_fireUpdateFilterEvent

private void fireApplyStandardAttacksEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireApplyStandardAttacksEvent
    jStandardAttackDialog.setVisible(false);
}//GEN-LAST:event_fireApplyStandardAttacksEvent

private void fireAttackFrameAlwaysOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireAttackFrameAlwaysOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireAttackFrameAlwaysOnTopEvent

private void fireEnterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireEnterEvent
    jLabel1.setEnabled(true);
}//GEN-LAST:event_fireEnterEvent

private void fireMouseExitEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireMouseExitEvent
    jLabel1.setEnabled(false);
}//GEN-LAST:event_fireMouseExitEvent

private void fireCreateAttackPlanEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCreateAttackPlanEvent
    int unusedId = 1;
    while (unusedId < 1000) {
        if (AttackManager.getSingleton().addGroup("Neuer Plan " + unusedId)) {
            break;
        }
        unusedId++;
    }
    if (unusedId == 1000) {
        JOptionPaneHelper.showErrorBox(DSWorkbenchAttackFrame.this, "Du hast mehr als 1000 Angriffspläne. Bitte lösche zuerst ein paar bevor du Neue erstellst.", "Fehler");
        return;
    }
}//GEN-LAST:event_fireCreateAttackPlanEvent

    /**Update the attack plan filter*/
    private void updateFilter() {
        AttackTableTab tab = getActiveTab();
        if (tab != null) {
            final List<String> selection = new LinkedList<String>();
            for (Object o : jXColumnList.getSelectedValues()) {
                selection.add((String) o);
            }
            tab.updateFilter(jTextField1.getText(), selection, jFilterCaseSensitive.isSelected(), jFilterRows.isSelected());
        }
    }

    @Override
    public void toBack() {
        jAttackFrameAlwaysOnTop.setSelected(false);
        fireAttackFrameAlwaysOnTopEvent(null);
        super.toBack();
    }

    public void decreaseClickAccountValue() {
        iClickAccount = (iClickAccount == 0) ? 0 : iClickAccount - 1;
        updateClickAccount();
    }

    public int getClickAccountValue() {
        return iClickAccount;
    }

    private void updateClickAccount() {
        jClickAccountLabel.setToolTipText(iClickAccount + " Klick(s) aufgeladen");
        jClickAccountLabel.setText("Klick-Konto [" + iClickAccount + "]");
    }

    @Override
    public void resetView() {
        AttackManager.getSingleton().addManagerListener(this);
        generateAttackTabs();
    }

    /**Initialize and add one tab for each attack plan to jTabbedPane1*/
    public void generateAttackTabs() {
        jAttackTabPane.invalidate();
        while (jAttackTabPane.getTabCount() > 0) {
            AttackTableTab tab = (AttackTableTab) jAttackTabPane.getComponentAt(0);
            tab.deregister();
            jAttackTabPane.removeTabAt(0);
        }
        LabelUIResource lr = new LabelUIResource();
        lr.setLayout(new BorderLayout());
        lr.add(jNewPlanPanel, BorderLayout.CENTER);
        jAttackTabPane.setTabLeadingComponent(lr);
        String[] plans = AttackManager.getSingleton().getGroups();

        //insert default tab to first place
        int cnt = 0;

        for (String plan : plans) {
            AttackTableTab tab = new AttackTableTab(plan, this);
            jAttackTabPane.addTab(plan, tab);
            cnt++;
        }

        jAttackTabPane.setTabClosableAt(0, false);
        jAttackTabPane.setTabClosableAt(1, false);
        jAttackTabPane.revalidate();
        AttackTableTab tab = getActiveTab();
        if (tab != null) {
            tab.updatePlan();
        }
    }

    @Override
    public void dataChangedEvent() {
        generateAttackTabs();
    }

    @Override
    public void dataChangedEvent(String pGroup) {
        AttackTableTab tab = getActiveTab();
        if (tab != null) {
            tab.updatePlan();
        }
    }

    public CountdownThread getCountdownThread() {
        return mCountdownThread;
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
    }

    /**Redraw the countdown col*/
    protected void updateCountdown() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ((AttackTableTab) jAttackTabPane.getSelectedComponent()).updateCountdown();
                } catch (Exception e) {
                }
            }
        });

    }

    /**Redraw the time col*/
    protected void updateTime() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    ((AttackTableTab) jAttackTabPane.getSelectedComponent()).updateTime();
                } catch (Exception e) {
                }
            }
        });

    }

    public static void main(String[] args) {


        Logger.getRootLogger().addAppender(new ConsoleAppender(new org.apache.log4j.PatternLayout("%d - %-5p - %-20c (%C [%L]) - %m%n")));
        MouseGestures mMouseGestures = new MouseGestures();
        mMouseGestures.setMouseButton(MouseEvent.BUTTON3_MASK);
        mMouseGestures.addMouseGesturesListener(new MouseGestureHandler());
        mMouseGestures.start();
        GlobalOptions.setSelectedServer("de43");
        ProfileManager.getSingleton().loadProfiles();
        GlobalOptions.setSelectedProfile(ProfileManager.getSingleton().getProfiles("de43")[0]);

        DataHolder.getSingleton().loadData(false);
        try {
            //  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }

        //  DSWorkbenchAttackFrame.getSingleton().setSize(800, 600);
        DSWorkbenchAttackFrame.getSingleton().pack();
        AttackManager.getSingleton().addGroup("test1");
        AttackManager.getSingleton().addGroup("asd2");
        AttackManager.getSingleton().addGroup("awe3");
        for (int i = 0; i < 100; i++) {
            Attack a = new Attack();
            a.setSource(DataHolder.getSingleton().getRandomVillage());
            a.setTarget(DataHolder.getSingleton().getRandomVillage());
            a.setArriveTime(new Date(Math.round(Math.random() * System.currentTimeMillis())));
            a.setUnit(new DummyUnit());
            Attack a1 = new Attack();
            a1.setSource(DataHolder.getSingleton().getRandomVillage());
            a1.setTarget(DataHolder.getSingleton().getRandomVillage());
            a1.setArriveTime(new Date(Math.round(Math.random() * System.currentTimeMillis())));
            a1.setUnit(new DummyUnit());
            Attack a2 = new Attack();
            a2.setSource(DataHolder.getSingleton().getRandomVillage());
            a2.setTarget(DataHolder.getSingleton().getRandomVillage());
            a2.setArriveTime(new Date(Math.round(Math.random() * System.currentTimeMillis())));
            a2.setUnit(new DummyUnit());
            AttackManager.getSingleton().addManagedElement(a);
            AttackManager.getSingleton().addManagedElement("test1", a1);
            AttackManager.getSingleton().addManagedElement("asd2", a2);
        }
        DSWorkbenchAttackFrame.getSingleton().resetView();
        DSWorkbenchAttackFrame.getSingleton().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DSWorkbenchAttackFrame.getSingleton().setVisible(true);
    }
    // <editor-fold defaultstate="collapsed" desc="Gesture Handling">

    @Override
    public void fireExportAsBBGestureEvent() {
        AttackTableTab tab = getActiveTab();
        if (tab != null) {
            tab.transferSelection(AttackTableTab.TRANSFER_TYPE.CLIPBOARD_BB);
        }
    }

    @Override
    public void firePlainExportGestureEvent() {
        AttackTableTab tab = getActiveTab();
        if (tab != null) {
            tab.transferSelection(AttackTableTab.TRANSFER_TYPE.CLIPBOARD_PLAIN);
        }
    }

    @Override
    public void fireNextPageGestureEvent() {
        int current = jAttackTabPane.getSelectedIndex();
        int size = jAttackTabPane.getTabCount();
        if (current + 1 > size - 1) {
            current = 0;
        } else {
            current += 1;
        }
        jAttackTabPane.setSelectedIndex(current);
    }

    @Override
    public void firePreviousPageGestureEvent() {
        int current = jAttackTabPane.getSelectedIndex();
        int size = jAttackTabPane.getTabCount();
        if (current - 1 < 0) {
            current = size - 1;
        } else {
            current -= 1;
        }
        jAttackTabPane.setSelectedIndex(current);
    }

    @Override
    public void fireRenameGestureEvent() {
        int idx = jAttackTabPane.getSelectedIndex();
        if (idx != 0 && idx != 1) {
            jAttackTabPane.editTabAt(idx);
        }
    }
// </editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JCheckBox jAttackFrameAlwaysOnTop;
    private javax.swing.JPanel jAttackPanel;
    private com.jidesoft.swing.JideTabbedPane jAttackTabPane;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JLabel jClickAccountLabel;
    private javax.swing.JCheckBox jFilterCaseSensitive;
    private javax.swing.JCheckBox jFilterRows;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JPanel jNewPlanPanel;
    private javax.swing.JComboBox jProfileBox;
    private javax.swing.JPanel jProfileQuickChange;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JDialog jStandardAttackDialog;
    private javax.swing.JTable jStandardAttackTable;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    private org.jdesktop.swingx.JXPanel jXAttackPanel;
    private org.jdesktop.swingx.JXList jXColumnList;
    private org.jdesktop.swingx.JXPanel jXPanel2;
    private org.jdesktop.swingx.JXPanel jxSearchPane;
    // End of variables declaration//GEN-END:variables
}


// <editor-fold defaultstate="collapsed" desc=" NOTIFY THREAD ">

class NotifyThread extends Thread {

    private static Logger logger = Logger.getLogger("AttackNotificationHelper");
    private boolean active = false;
    private long nextCheck = 0;
    private final int TEN_MINUTES = 10 * 60 * 1000;

    public NotifyThread() {
        setDaemon(true);
        setPriority(MIN_PRIORITY);
    }

    public void setActive(boolean pValue) {
        active = pValue;
        if (active) {
            logger.debug("Starting notification cycle");
            nextCheck = System.currentTimeMillis();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void run() {

        while (true) {
            if (active) {
                long now = System.currentTimeMillis();
                if (now > nextCheck) {
                    logger.debug("Checking attacks");
                    //do next check
                    Hashtable<String, Integer> outstandingAttacks = new Hashtable<String, Integer>();
                    Iterator<String> plans = AttackManager.getSingleton().getGroupIterator();
                    while (plans.hasNext()) {
                        String plan = plans.next();
                        List<ManageableType> attacks = AttackManager.getSingleton().getAllElements(plan);
                        int attackCount = 0;
                        for (ManageableType t : attacks) {
                            Attack a = (Attack) t;
                            long sendTime = a.getArriveTime().getTime() - (long) (DSCalculator.calculateMoveTimeInSeconds(a.getSource(), a.getTarget(), a.getUnit().getSpeed()) * 1000);
                            //find send times between now and in 10 minutes
                            if ((sendTime >= now) && (sendTime <= now + TEN_MINUTES)) {
                                attackCount++;
                            }
                        }
                        if (attackCount > 0) {
                            outstandingAttacks.put(plan, attackCount);
                        }
                    }

                    if (outstandingAttacks.size() > 0) {
                        // if (attackCount > 0) {
                        String message = "In den kommenden 10 Minuten müssen Angriffe aus den folgenden Plänen abgeschickt werden:\n";
                        Enumeration<String> outstandingPlans = outstandingAttacks.keys();
                        while (outstandingPlans.hasMoreElements()) {
                            String nextPlan = outstandingPlans.nextElement();
                            Integer cnt = outstandingAttacks.get(nextPlan);
                            message += nextPlan + " (" + cnt + ")\n";
                        }
                        NotifierFrame.doNotification(message, NotifierFrame.NOTIFY_ATTACK);
                        outstandingAttacks = null;
                        logger.debug("Scheduling next check in 10 minutes");
                        nextCheck = now + TEN_MINUTES;
                    } else {
                        //no attacks in next 10 minutes
                        logger.debug("Scheduling next check in 1 minute");
                        nextCheck = now + TEN_MINUTES / 10;
                    }
                }//wait for next check

            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
            }
        }
    }
}
//</editor-fold>

class ColorUpdateThread extends Thread {

    public ColorUpdateThread() {
        setDaemon(true);
    }

    public void run() {
        while (true) {
            try {
                DSWorkbenchAttackFrame.getSingleton().updateTime();
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
            } catch (Throwable t) {
            }
        }
    }
}

class CountdownThread extends Thread {

    private boolean showCountdown = true;

    public CountdownThread() {
        setDaemon(true);
    }

    public void updateSettings() {
        showCountdown = Boolean.parseBoolean(GlobalOptions.getProperty("show.live.countdown"));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (showCountdown && DSWorkbenchAttackFrame.getSingleton().isVisible()) {
                    DSWorkbenchAttackFrame.getSingleton().updateCountdown();
                    //yield();
                    sleep(100);
                } else {
                    // yield();
                    sleep(1000);
                }
            } catch (Exception e) {
            }
        }
    }
}

class LabelUIResource extends JPanel implements UIResource {

    public LabelUIResource() {
        super();
    }
}
