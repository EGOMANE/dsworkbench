/*
 * MapFrame.java
 *
 * Created on 4. September 2007, 18:07
 */
package de.tor.tribes.ui;

import de.tor.tribes.dssim.ui.DSWorkbenchSimulatorFrame;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.php.ScreenUploadInterface;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.models.TroopsManagerTableModel;
import de.tor.tribes.util.BrowserCommandSender;
import de.tor.tribes.util.ClipboardWatch;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSWorkbenchFrameListener;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ToolChangeListener;
import de.tor.tribes.util.tag.TagManager;
import java.awt.AWTEvent;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import de.tor.tribes.types.Tag;
import de.tor.tribes.ui.models.ConquersTableModel;
import de.tor.tribes.ui.models.DistanceTableModel;
import de.tor.tribes.ui.models.StandardAttackTableModel;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.MainShutdownHook;
import de.tor.tribes.util.MapShotListener;
import de.tor.tribes.util.ServerSettings;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.conquer.ConquerManager;
import de.tor.tribes.util.dist.DistanceManager;
import de.tor.tribes.util.dsreal.DSRealManager;
import de.tor.tribes.util.map.FormManager;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.note.NoteManager;
import de.tor.tribes.util.roi.ROIManager;
import de.tor.tribes.util.stat.StatManager;
import java.io.File;
import javax.swing.JFileChooser;
import de.tor.tribes.util.troops.TroopsManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * @TODO (1.5?) Add min number to troop filter in attack planer????
 * @author  Charon
 */
public class DSWorkbenchMainFrame extends javax.swing.JFrame implements
        MapPanelListener,
        ToolChangeListener,
        DSWorkbenchFrameListener,
        MapShotListener {

    private static Logger logger = Logger.getLogger("MainApp");
    private double dCenterX = 500.0;
    private double dCenterY = 500.0;
    private double dZoomFactor = 1.0;
    private TribeTribeAttackFrame mTribeTribeAttackFrame = null;
    private AboutDialog mAbout = null;
    private static DSWorkbenchMainFrame SINGLETON = null;
    private boolean initialized = false;
    private JFrame fullscreenFrame = null;
    private ImageIcon uvModeOn = null;
    private ImageIcon uvModeOff = null;
    private boolean putOnline = false;

    public static synchronized DSWorkbenchMainFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchMainFrame();
        }
        return SINGLETON;
    }

    /** Creates new form MapFrame */
    DSWorkbenchMainFrame() {
        initComponents();
        setTitle("DS Workbench " + Constants.VERSION + Constants.VERSION_ADDITION);
        setAlwaysOnTop(false);
        jExportDialog.pack();
        jAddROIDialog.pack();
        mAbout = new AboutDialog(this, true);
        mAbout.pack();

        // <editor-fold defaultstate="collapsed" desc=" Register ShutdownHook ">

        Runtime.getRuntime().addShutdownHook(new MainShutdownHook());

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" General UI setup ">
        Vector<String> v = new Vector<String>(Constants.LAYER_COUNT);
        for (int i = 0; i < Constants.LAYER_COUNT; i++) {
            v.add("");
        }

        String layerOrder = GlobalOptions.getProperty("layer.order");
        if (layerOrder == null) {
            Enumeration<String> values = Constants.LAYERS.keys();
            while (values.hasMoreElements()) {
                String layer = values.nextElement();
                v.set(Constants.LAYERS.get(layer), layer);
            }
        } else {
            //try to use stored layers
            String[] layers = layerOrder.split(";");
            if (layers.length == Constants.LAYER_COUNT) {
                //layer sizes are equal, so set layers in stored order
                int cnt = 0;
                for (String layer : layers) {
                    v.set(cnt, layer);
                    cnt++;
                }
            } else {
                //layer number has changed since value was stored, so rebuild
                Enumeration<String> values = Constants.LAYERS.keys();
                while (values.hasMoreElements()) {
                    String layer = values.nextElement();
                    v.set(Constants.LAYERS.get(layer), layer);
                }
            }
        }

        DefaultListModel model = new DefaultListModel();
        for (String s : v) {
            model.addElement(s);
        }

        jLayerList.setModel(model);

        jLayerList.setCellRenderer(new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                try {
                    JLabel label = ((JLabel) c);
                    if (value.equals("Dörfer")) {
                        //map layer is red
                        if (!isSelected) {
                            label.setBackground(Color.RED);
                        }
                    } else if (value.equals("Markierungen")) {
                        //marker layer is not influenced by map layer
                        //so it gets a special color
                        if (!isSelected) {
                            label.setBackground(Color.LIGHT_GRAY);
                        }
                    } else {
                        //layers which are "behind" map are disabled
                        int villageIndex = ((DefaultListModel) list.getModel()).indexOf("Dörfer");
                        if (index < villageIndex && !isSelected) {
                            label.setForeground(Color.LIGHT_GRAY);
                        }
                    }
                } catch (Exception e) {
                }
                return c;
            }
        });

        getContentPane().setBackground(Constants.DS_BACK);
        pack();

        // </editor-fold>        

        // <editor-fold defaultstate="collapsed" desc=" Add global KeyListener ">
        jMenuBar1.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

            @Override
            public void eventDispatched(AWTEvent event) {
                if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
                    KeyEvent e = (KeyEvent) event;
                    if (DSWorkbenchMainFrame.getSingleton().isActive() || fullscreenFrame != null) {
                        //move shortcuts
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            scroll(0.0, 2.0);
                        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                            scroll(0.0, -2.0);
                        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                            scroll(-2.0, 0.0);
                        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                            scroll(2.0, 0.0);
                        } else if ((e.getKeyCode() == KeyEvent.VK_1) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //shot minimap tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_AXE);
                        } else if ((e.getKeyCode() == KeyEvent.VK_2) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack axe tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_RAM);
                        } else if ((e.getKeyCode() == KeyEvent.VK_3) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack ram tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_SNOB);
                        } else if ((e.getKeyCode() == KeyEvent.VK_4) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack snob tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_SPY);
                        } else if ((e.getKeyCode() == KeyEvent.VK_5) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack sword tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_LIGHT);
                        } else if ((e.getKeyCode() == KeyEvent.VK_6) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack light tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_HEAVY);
                        } else if ((e.getKeyCode() == KeyEvent.VK_7) && e.isShiftDown() && !e.isControlDown() && !e.isAltDown()) {
                            //attack heavy tool shortcut
                            MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_SWORD);
                        } else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown() && !e.isAltDown()) {
                            //search frame shortcut
                            DSWorkbenchSearchFrame.getSingleton().setVisible(!DSWorkbenchSearchFrame.getSingleton().isVisible());
                        }
                    }

                    //misc shortcuts
                    if ((e.getKeyCode() == KeyEvent.VK_0) && e.isAltDown()) {
                        //no tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_DEFAULT);
                    } else if ((e.getKeyCode() == KeyEvent.VK_1) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //measure tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_MEASURE);
                    } else if ((e.getKeyCode() == KeyEvent.VK_2) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //mark tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_MARK);
                    } else if ((e.getKeyCode() == KeyEvent.VK_3) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //tag tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_TAG);
                    } else if ((e.getKeyCode() == KeyEvent.VK_4) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //attack ingame tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_SUPPORT);
                    } else if ((e.getKeyCode() == KeyEvent.VK_5) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //attack ingame tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_SELECTION);
                    } else if ((e.getKeyCode() == KeyEvent.VK_6) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //attack ingame tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_RADAR);
                    } else if ((e.getKeyCode() == KeyEvent.VK_7) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //attack ingame tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ATTACK_INGAME);
                    } else if ((e.getKeyCode() == KeyEvent.VK_8) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        //res ingame tool shortcut
                        MapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_SEND_RES_INGAME);
                    } else if ((e.getKeyCode() == KeyEvent.VK_1) && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        //move minimap tool shortcut
                        MinimapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_MOVE);
                    } else if ((e.getKeyCode() == KeyEvent.VK_2) && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        //zoom minimap tool shortcut
                        MinimapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_ZOOM);
                    } else if ((e.getKeyCode() == KeyEvent.VK_3) && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        //shot minimap tool shortcut
                        MinimapPanel.getSingleton().setCurrentCursor(ImageManager.CURSOR_SHOT);
                    } else if ((e.getKeyCode() == KeyEvent.VK_T) && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        //search time shortcut
                        ClockFrame.getSingleton().setVisible(!ClockFrame.getSingleton().isVisible());
                    } else if ((e.getKeyCode() == KeyEvent.VK_F) && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        if (fullscreenFrame == null) {
                            jPanel1.remove(MapPanel.getSingleton());
                            fullscreenFrame = new JFrame();
                            fullscreenFrame.add(MapPanel.getSingleton());
                            Dimension fullscreen = Toolkit.getDefaultToolkit().getScreenSize();
                            fullscreenFrame.setSize(fullscreen);
                            fullscreenFrame.setUndecorated(true);
                            fullscreenFrame.setVisible(true);
                        } else {
                            fullscreenFrame.remove(MapPanel.getSingleton());
                            jPanel1.add(MapPanel.getSingleton());
                            jPanel1.repaint();//.updateUI();
                            MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
                            fullscreenFrame.dispose();
                            fullscreenFrame = null;
                        }

                        if (ServerSettings.getSingleton().getCoordType() != 2) {
                            int[] hier = DSCalculator.hierarchicalToXy(Integer.parseInt(jCenterX.getText()), Integer.parseInt(jCenterY.getText()), 12);
                            if (hier != null) {
                                MapPanel.getSingleton().updateMapPosition(hier[0], hier[1]);
                            }
                        } else {
                            MapPanel.getSingleton().updateMapPosition(Integer.parseInt(jCenterX.getText()), Integer.parseInt(jCenterY.getText()));
                        }
                    } else if ((e.getKeyCode() == KeyEvent.VK_F) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        DSWorkbenchMarkerFrame.getSingleton().firePublicDrawMarkedOnlyChangedEvent();
                    } else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                        fireCreateMapShotEvent(null);
                    } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                        DSWorkbenchAttackFrame.getSingleton().setVisible(!DSWorkbenchAttackFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F3) {
                        DSWorkbenchMarkerFrame.getSingleton().setVisible(!DSWorkbenchMarkerFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F4) {
                        DSWorkbenchTroopsFrame.getSingleton().setVisible(!DSWorkbenchTroopsFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                        DSWorkbenchRankFrame.getSingleton().setVisible(!DSWorkbenchRankFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F6) {
                        DSWorkbenchFormFrame.getSingleton().setVisible(!DSWorkbenchFormFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F7) {
                        if (jShowChurchFrame.isEnabled()) {
                            DSWorkbenchChurchFrame.getSingleton().setVisible(!DSWorkbenchChurchFrame.getSingleton().isVisible());
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_F8) {
                        DSWorkbenchConquersFrame.getSingleton().setVisible(!DSWorkbenchConquersFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F9) {
                        DSWorkbenchNotepad.getSingleton().setVisible(!DSWorkbenchNotepad.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F10) {
                        DSWorkbenchTagFrame.getSingleton().setVisible(!DSWorkbenchTagFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F11) {
                        DSWorkbenchStatsFrame.getSingleton().setVisible(!DSWorkbenchStatsFrame.getSingleton().isVisible());
                    } else if (e.getKeyCode() == KeyEvent.VK_F12) {
                        DSWorkbenchSettingsDialog.getSingleton().setVisible(true);
                    } else if ((e.getKeyCode() == KeyEvent.VK_1) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 1
                        centerROI(0);
                    } else if ((e.getKeyCode() == KeyEvent.VK_2) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 2
                        centerROI(1);
                    } else if ((e.getKeyCode() == KeyEvent.VK_3) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 3
                        centerROI(2);
                    } else if ((e.getKeyCode() == KeyEvent.VK_4) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 4
                        centerROI(3);
                    } else if ((e.getKeyCode() == KeyEvent.VK_5) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 5
                        centerROI(4);
                    } else if ((e.getKeyCode() == KeyEvent.VK_6) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 6
                        centerROI(5);
                    } else if ((e.getKeyCode() == KeyEvent.VK_7) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 7
                        centerROI(6);
                    } else if ((e.getKeyCode() == KeyEvent.VK_8) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 8
                        centerROI(7);
                    } else if ((e.getKeyCode() == KeyEvent.VK_9) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 9
                        centerROI(8);
                    } else if ((e.getKeyCode() == KeyEvent.VK_0) && e.isControlDown() && e.isAltDown() && !e.isShiftDown()) {
                        //ROI 10
                        centerROI(9);
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        MapPanel.getSingleton().requestFocusInWindow();
                        MapPanel.getSingleton().setSpaceDown(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        MapPanel.getSingleton().requestFocusInWindow();
                        MapPanel.getSingleton().setShiftDown(true);
                    }
                } else if (((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED) {
                    KeyEvent e = (KeyEvent) event;
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        MapPanel.getSingleton().setSpaceDown(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        MapPanel.getSingleton().setShiftDown(false);
                    }
                }
            }
        },
                AWTEvent.KEY_EVENT_MASK);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Load UI Icons ">





        try {
            jOnlineLabel.setIcon(new ImageIcon("./graphics/icons/online.png"));
            jCenterIngameButton.setIcon(new ImageIcon("./graphics/icons/center.png"));
            jRefreshButton.setIcon(new ImageIcon("./graphics/icons/refresh.png"));
            jCenterCoordinateIngame.setIcon(new ImageIcon("./graphics/icons/center.png"));
            uvModeOn = new ImageIcon(getClass().getResource("/res/ui/uv.png"));
            uvModeOff = new ImageIcon(getClass().getResource("/res/ui/uv_off.png"));
        } catch (Exception e) {
            logger.error("Failed to load status icon(s)", e);
        }
// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Check for desktop support ">
        if (!Desktop.isDesktopSupported()) {
            jCenterIngameButton.setEnabled(false);
            jCenterCoordinateIngame.setEnabled(false);
        }
// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Restore last map position ">
        try {
            String x = GlobalOptions.getProperty("last.x");
            String y = GlobalOptions.getProperty("last.y");
            dCenterX = Double.parseDouble(x);
            dCenterY = Double.parseDouble(y);
            jCenterX.setText(x);
            jCenterY.setText(y);
        } catch (Exception e) {
            if (ServerSettings.getSingleton().getCoordType() != 2) {
                dCenterX = 250.0;
                dCenterY = 250.0;
                int[] hier = DSCalculator.xyToHierarchical(250, 250);
                jCenterX.setText(Integer.toString(hier[0]));
                jCenterY.setText(Integer.toString(hier[1]));
            } else {
                dCenterX = 500.0;
                dCenterY = 500.0;
                jCenterX.setText("500");
                jCenterY.setText("500");
            }
        }

// </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Restore other settings ">
        try {
            String val = GlobalOptions.getProperty("show.map.popup");
            if (val == null) {
                jShowMapPopup.setSelected(true);
                GlobalOptions.addProperty("show.map.popup", Boolean.toString(true));
            } else {
                jShowMapPopup.setSelected(Boolean.parseBoolean(val));
            }
        } catch (Exception e) {
            jShowMapPopup.setSelected(true);
            GlobalOptions.addProperty("show.map.popup", Boolean.toString(true));
        }
        try {
            String val = GlobalOptions.getProperty("highlight.tribes.villages");
            if (val == null) {
                jHighlightTribeVillages.setSelected(true);
                GlobalOptions.addProperty("highlight.tribes.villages", Boolean.toString(true));
            } else {
                jHighlightTribeVillages.setSelected(Boolean.parseBoolean(val));
            }
        } catch (Exception e) {
            jHighlightTribeVillages.setSelected(true);
            GlobalOptions.addProperty("highlight.tribes.villages", Boolean.toString(true));
        }
        try {
            String val = GlobalOptions.getProperty("show.ruler");
            if (val == null) {
                jShowRuler.setSelected(true);
                GlobalOptions.addProperty("show.ruler", Boolean.toString(true));
            } else {
                jShowRuler.setSelected(Boolean.parseBoolean(val));
            }
        } catch (Exception e) {
            jShowRuler.setSelected(true);
            GlobalOptions.addProperty("show.ruler", Boolean.toString(true));
        }

        try {
            jRadarSpinner.setEditor(new JSpinner.DateEditor(jRadarSpinner, "HH'h' mm'min'"));
            String val = GlobalOptions.getProperty("radar.size");
            Calendar c = Calendar.getInstance();
            int hour = 1;
            int min = 0;
            if (val != null) {
                int r = Integer.parseInt(val);
                hour = (int) r / 60;
                min = r - hour * 60;
            } else {
                throw new Exception();
            }
            c.set(0, 0, 0, hour, min);
            jRadarSpinner.setValue(c.getTime());
        } catch (Exception e) {
            Calendar c = Calendar.getInstance();
            c.set(0, 0, 0, 1, 0);
            jRadarSpinner.setValue(c.getTime());
            GlobalOptions.addProperty("radar.size", "60");
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Skin Setup">
        DefaultComboBoxModel gpModel = new DefaultComboBoxModel(GlobalOptions.getAvailableSkins());
        jGraphicPacks.setModel(gpModel);
        String skin = GlobalOptions.getProperty("default.skin");
        if (skin != null) {
            if (gpModel.getIndexOf(skin) != -1) {
                jGraphicPacks.setSelectedItem(skin);
            } else {
                jGraphicPacks.setSelectedItem("default");
            }
        } else {
            jGraphicPacks.setSelectedItem("default");
        }
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "index", GlobalOptions.getHelpBroker().getHelpSet());
        jHelpItem.addActionListener(GlobalOptions.getHelpDisplay());

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Init A*Star HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(DSWorkbenchSimulatorFrame.getSingleton().getRootPane(), "pages.astar", GlobalOptions.getHelpBroker().getHelpSet());
        // </editor-fold>
        //update online state
        onlineStateChanged();
        restoreProperties();
    }
    /* boolean map = true;

    public void switchPanel() {
    if (map) {
    jPanel1.remove(MapPanel.getSingleton());
    jPanel1.add(jCustomPanel);
    map = false;
    } else {
    jPanel1.remove(jCustomPanel);
    jPanel1.add(MapPanel.getSingleton());
    map = true;
    }
    jPanel1.repaint();
    }*/

    public void storeProperties() {
        GlobalOptions.addProperty("main.size.width", Integer.toString(getWidth()));
        GlobalOptions.addProperty("main.size.height", Integer.toString(getHeight()));
        GlobalOptions.addProperty("navigation.group.expanded", Boolean.toString(jNavigationGroup.isExpanded()));
        GlobalOptions.addProperty("information.group.expanded", Boolean.toString(jInformationGroup.isExpanded()));
        GlobalOptions.addProperty("map.group.expanded", Boolean.toString(jMapGroup.isExpanded()));
        GlobalOptions.addProperty("roi.group.expanded", Boolean.toString(jROIGroup.isExpanded()));
        GlobalOptions.addProperty("uv.group.expanded", Boolean.toString(jUVGroup.isExpanded()));
    }

    public void restoreProperties() {
        try {
            int width = Integer.parseInt(GlobalOptions.getProperty("main.size.width"));
            int height = Integer.parseInt(GlobalOptions.getProperty("main.size.height"));
            setSize(width, height);
        } catch (Exception e) {
        }
        try {
            jNavigationGroup.setExpanded(Boolean.parseBoolean(GlobalOptions.getProperty("navigation.group.expanded")));
            jInformationGroup.setExpanded(Boolean.parseBoolean(GlobalOptions.getProperty("information.group.expanded")));
            jMapGroup.setExpanded(Boolean.parseBoolean(GlobalOptions.getProperty("map.group.expanded")));
            jROIGroup.setExpanded(Boolean.parseBoolean(GlobalOptions.getProperty("roi.group.expanded")));
            jUVGroup.setExpanded(Boolean.parseBoolean(GlobalOptions.getProperty("uv.group.expanded")));
        } catch (Exception e) {
            jNavigationGroup.setExpanded(false);
            jInformationGroup.setExpanded(false);
            jMapGroup.setExpanded(true);
            jROIGroup.setExpanded(false);
            jUVGroup.setExpanded(false);
        }
    }

    public String[] getCurrentPosition() {
        return new String[]{jCenterX.getText(), jCenterY.getText()};
    }

    /**Update on server change*/
    public void serverSettingsChangedEvent() {
        try {
            logger.info("Updating server settings");
            String playerName = GlobalOptions.getProperty("player." + GlobalOptions.getSelectedServer());
            String playerID = playerName + "@" + GlobalOptions.getSelectedServer();
            logger.info(" - using playerID " + playerID);
            jCurrentPlayer.setText(playerID);
            try {
                DefaultComboBoxModel model = new DefaultComboBoxModel();
                Tribe t = DataHolder.getSingleton().getTribeByName(playerName);
                Village[] villages = t.getVillageList();
                Arrays.sort(villages, Village.CASE_INSENSITIVE_ORDER);
                for (Village v : villages) {
                    model.addElement(v);
                }
                jCurrentPlayerVillages.setModel(model);
            } catch (Exception e) {
                jCurrentPlayerVillages.setModel(new DefaultComboBoxModel(new Object[]{"-keine Dörfer-"}));
            }

            //update views
            MapPanel.getSingleton().resetServerDependendSettings();
            MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);
            MapPanel.getSingleton().getAttackAddFrame().buildUnitBox();
            DSWorkbenchMarkerFrame.getSingleton().setupMarkerPanel();
            DSWorkbenchChurchFrame.getSingleton().setupChurchPanel();
            DSWorkbenchAttackFrame.getSingleton().setupAttackPanel();
            DSWorkbenchTagFrame.getSingleton().setup();
            ConquersTableModel.getSingleton();
            DSWorkbenchConquersFrame.getSingleton().setupConquersPanel();
            //update troops table and troops view
            TroopsManagerTableModel.getSingleton().setup();
            StandardAttackTableModel.getSingleton().setup();
            DSWorkbenchTroopsFrame.getSingleton().setupTroopsPanel();
            DistanceManager.getSingleton().clear();
            StatManager.getSingleton().setup();
            DistanceTableModel.getSingleton().fireTableStructureChanged();
            DSWorkbenchDistanceFrame.getSingleton().setup();
            DSWorkbenchStatsFrame.getSingleton().setup();
            DSWorkbenchDoItYourselfAttackPlaner.getSingleton().setupAttackPlaner();
            DSWorkbenchReTimerFrame.getSingleton().setup();
            DSWorkbenchReportFrame.getSingleton().setup();
            DSWorkbenchSupportCoordinator.getSingleton().setup();
            DSWorkbenchSOSRequestAnalyzer.getSingleton().setup();
            //update attack planner
            if (mTribeTribeAttackFrame != null) {
                mTribeTribeAttackFrame.setup();
            }

            DSWorkbenchSettingsDialog.getSingleton().setupAttackColorTable();
            DSWorkbenchRankFrame.getSingleton().setup();
            DSWorkbenchRankFrame.getSingleton().updateAllyList();
            DSWorkbenchRankFrame.getSingleton().updateRankTable();

            if (ServerSettings.getSingleton().getCoordType() != 2) {
                jLabel1.setText("K");
                jLabel2.setText("S");
            } else {
                jLabel1.setText("X");
                jLabel2.setText("Y");
                DSRealManager.getSingleton().checkFilesystem();
            }

            jShowChurchFrame.setEnabled(ServerSettings.getSingleton().isChurch());
            jROIBox.setModel(new DefaultComboBoxModel(ROIManager.getSingleton().getROIs()));

            DSWorkbenchSelectionFrame.getSingleton().clear();
            DSWorkbenchNotepad.getSingleton().setup();
            if (DSWorkbenchSimulatorFrame.getSingleton().isVisible()) {
                DSWorkbenchSimulatorFrame.getSingleton().showIntegratedVersion(GlobalOptions.getSelectedServer());
            }
            ConquerManager.getSingleton().forceUpdate();
            //relevant for first start
            propagateLayerOrder();
            MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
            MinimapPanel.getSingleton().redraw(true);
            //call all frames during first execution
            DSWorkbenchAttackFrame.getSingleton();
            DSWorkbenchNotepad.getSingleton();
            DSWorkbenchTroopsFrame.getSingleton();
            DSWorkbenchRankFrame.getSingleton();
            DSWorkbenchFormFrame.getSingleton();
            DSWorkbenchMarkerFrame.getSingleton();
            DSWorkbenchChurchFrame.getSingleton();
            DSWorkbenchConquersFrame.getSingleton();
            DSWorkbenchNotepad.getSingleton();
            DSWorkbenchTagFrame.getSingleton();
            FormConfigFrame.getSingleton();
            DSWorkbenchSearchFrame.getSingleton();
            DSWorkbenchSelectionFrame.getSingleton();
            DSWorkbenchStatsFrame.getSingleton();
            DSWorkbenchReTimerFrame.getSingleton();
            DSWorkbenchDoItYourselfAttackPlaner.getSingleton();
            DSWorkbenchReportFrame.getSingleton();
            logger.info("Server settings updated");
            String path = "./servers/" + GlobalOptions.getSelectedServer() + "/serverdata.bin";
            if (!DSWorkbenchSettingsDialog.getSingleton().isVisible()) {
                long dataVersion = new File(path).lastModified();
                long oneDayAgo = System.currentTimeMillis() - 1000 * 60 * 60 * 24;
                if (dataVersion < oneDayAgo) {
                    JOptionPaneHelper.showWarningBox(this, "Deine Weltdaten sind älter als 24 Stunden.\n" +
                            "Es wird empfohlen, sie sobald wie möglich zu aktualisieren.", "Warnung");
                }
            }
        } catch (Exception e) {
            logger.error("Error while refreshing server settings", e);
        }
    }

    /**Update UI depending on online state*/
    public void onlineStateChanged() {
        jOnlineLabel.setEnabled(!GlobalOptions.isOfflineMode());
        if (GlobalOptions.isOfflineMode()) {
            jOnlineLabel.setToolTipText("Offline");
        } else {
            jOnlineLabel.setToolTipText("Online");
        }
    }

    /**Get current zoom factor*/
    public synchronized double getZoomFactor() {
        return dZoomFactor;
    }

    /**Called at startup*/
    protected void init() {
        logger.info("Starting initialization");
        //setup everything
        serverSettingsChangedEvent();

        logger.info(" * Setting up maps");
        setupMaps();

        logger.info(" * Setting up views");
        setupFrames();
        //setup toolbox

        fireToolChangedEvent(ImageManager.CURSOR_DEFAULT);
        logger.info(" * Setting up attack planner");
        //setup frames
        mTribeTribeAttackFrame = new TribeTribeAttackFrame();
        mTribeTribeAttackFrame.pack();


        logger.info("Initialization finished");
        initialized = true;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    public TribeTribeAttackFrame getAttackPlaner() {
        return mTribeTribeAttackFrame;
    }

    /**Setup of all frames*/
    private void setupFrames() {
        DSWorkbenchAttackFrame.getSingleton().addFrameListener(this);
        DSWorkbenchMarkerFrame.getSingleton().addFrameListener(this);
        DSWorkbenchChurchFrame.getSingleton().addFrameListener(this);
        DSWorkbenchConquersFrame.getSingleton().addFrameListener(this);
        DSWorkbenchNotepad.getSingleton().addFrameListener(this);
        DSWorkbenchTagFrame.getSingleton().addFrameListener(this);
        TroopsManagerTableModel.getSingleton().setup();
        DSWorkbenchTroopsFrame.getSingleton().addFrameListener(this);
        DSWorkbenchRankFrame.getSingleton().addFrameListener(this);
        DSWorkbenchFormFrame.getSingleton().addFrameListener(this);
        DSWorkbenchStatsFrame.getSingleton().addFrameListener(this);
        DSWorkbenchReportFrame.getSingleton().addFrameListener(this);

    }

    /**Setup main map and mini map*/
    private void setupMaps() {
        try {
            dZoomFactor = Double.parseDouble(GlobalOptions.getProperty("zoom.factor"));
            checkZoomRange();
        } catch (Exception e) {
            dZoomFactor = 1.0;
        }
        //build the map panel
        logger.info("Adding MapListener");
        MapPanel.getSingleton().addMapPanelListener(this);
        MapPanel.getSingleton().addToolChangeListener(this);
        MinimapPanel.getSingleton().addToolChangeListener(this);
        logger.info("Adding MapPanel");
        jPanel1.add(MapPanel.getSingleton());
        //build the minimap
        logger.info("Adding MinimapPanel");
        /*MinimapPanel.getSingleton().setMinimumSize(jMinimapPanel.getMinimumSize());
        MapPanel.getSingleton().setMinimumSize(jPanel1.getMinimumSize());*/
        jMinimapPanel.add(MinimapPanel.getSingleton());
    }

    @Override
    public void setVisible(boolean v) {
        logger.info("Setting MainWindow visible");

        super.setVisible(v);
        if (v) {
            //only if set to visible
            MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);

            double w = (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getCurrentFieldWidth();
            double h = (double) MapPanel.getSingleton().getHeight() / (double) GlobalOptions.getSkin().getCurrentFieldHeight();
            MinimapPanel.getSingleton().setSelection((int) Math.floor(dCenterX), (int) Math.floor(dCenterY), (int) Math.rint(w), (int) Math.rint(h));

            // <editor-fold defaultstate="collapsed" desc=" Check frames and toolbar visibility ">

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("attack.frame.visible"))) {
                    jShowAttackFrame.setSelected(true);
                    logger.info("Restoring attack frame");
                    DSWorkbenchAttackFrame.getSingleton().setVisible(true);
                }
            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("marker.frame.visible"))) {
                    jShowMarkerFrame.setSelected(true);
                    logger.info("Restoring marker frame");
                    DSWorkbenchMarkerFrame.getSingleton().setVisible(true);
                }

            } catch (Exception e) {
            }

            try {
                if (jShowChurchFrame.isEnabled()) {
                    if (Boolean.parseBoolean(GlobalOptions.getProperty("church.frame.visible"))) {
                        jShowChurchFrame.setSelected(true);
                        logger.info("Restoring church frame");
                        DSWorkbenchChurchFrame.getSingleton().setVisible(true);
                    }
                }
            } catch (Exception e) {
            }

            try {
                if (jShowConquersFrame.isEnabled()) {
                    if (Boolean.parseBoolean(GlobalOptions.getProperty("conquers.frame.visible"))) {
                        jShowConquersFrame.setSelected(true);
                        logger.info("Restoring conquers frame");
                        DSWorkbenchConquersFrame.getSingleton().setVisible(true);
                    }
                }
            } catch (Exception e) {
            }

            try {
                if (jShowNotepadFrame.isEnabled()) {
                    if (Boolean.parseBoolean(GlobalOptions.getProperty("notepad.frame.visible"))) {
                        jShowNotepadFrame.setSelected(true);
                        logger.info("Restoring notepad frame");
                        DSWorkbenchNotepad.getSingleton().setVisible(true);
                    }
                }
            } catch (Exception e) {
            }

            try {
                if (jShowTagFrame.isEnabled()) {
                    if (Boolean.parseBoolean(GlobalOptions.getProperty("tag.frame.visible"))) {
                        jShowTagFrame.setSelected(true);
                        logger.info("Restoring tag frame");
                        DSWorkbenchTagFrame.getSingleton().setVisible(true);
                    }
                }
            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("troops.frame.visible"))) {
                    jShowTroopsFrame.setSelected(true);
                    logger.info("Restoring troops frame");
                    DSWorkbenchTroopsFrame.getSingleton().setVisible(true);
                }
            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("rank.frame.visible"))) {
                    jShowRankFrame.setSelected(true);
                    logger.info("Restoring rank frame");
                    DSWorkbenchRankFrame.getSingleton().setVisible(true);
                }

            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("form.frame.visible"))) {
                    jShowFormsFrame.setSelected(true);
                    logger.info("Restoring form frame");
                    DSWorkbenchFormFrame.getSingleton().setVisible(true);
                }

            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("search.frame.visible"))) {
                    logger.info("Restoring search frame");
                    DSWorkbenchSearchFrame.getSingleton().setVisible(true);
                }

            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("stats.frame.visible"))) {
                    jShowStatsFrame.setSelected(true);
                    logger.info("Restoring stats frame");
                    DSWorkbenchStatsFrame.getSingleton().setVisible(true);
                }
            } catch (Exception e) {
            }

            try {
                if (Boolean.parseBoolean(GlobalOptions.getProperty("report.frame.visible"))) {
                    jShowReportFrame.setSelected(true);
                    logger.info("Restoring report frame");
                    DSWorkbenchReportFrame.getSingleton().setVisible(true);
                }
            } catch (Exception e) {
            }
            // </editor-fold>

            //start ClipboardWatch
            ClipboardWatch.getSingleton();
            //draw map the first time
            fireRefreshMapEvent(null);
            /*  MapPanel.getSingleton().addNotify();
            MapPanel.getSingleton().initBuffer();
            MapPanel.getDoubleton().addNotify();
            MapPanel.getDoubleton().initBuffer();*/
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMapShotDialog = new javax.swing.JDialog();
        jLabel3 = new javax.swing.JLabel();
        jFileTypeChooser = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jExportDialog = new javax.swing.JDialog();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttackExportTable = new javax.swing.JTable();
        jExportTags = new javax.swing.JCheckBox();
        jExportTroops = new javax.swing.JCheckBox();
        jExportForms = new javax.swing.JCheckBox();
        jExportButton = new javax.swing.JButton();
        jCancelExportButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jMarkerSetExportTable = new javax.swing.JTable();
        jExportNotes = new javax.swing.JCheckBox();
        jAddROIDialog = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jROIRegion = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jROITextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jROIPosition = new javax.swing.JComboBox();
        jAddNewROIButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jCustomPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jMinimapPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTaskPane1 = new com.l2fprod.common.swing.JTaskPane();
        jNavigationGroup = new com.l2fprod.common.swing.JTaskPaneGroup();
        jNavigationPanel = new javax.swing.JPanel();
        jMoveE = new javax.swing.JButton();
        jMoveNE = new javax.swing.JButton();
        jMoveN = new javax.swing.JButton();
        jMoveNW = new javax.swing.JButton();
        jMoveW = new javax.swing.JButton();
        jMoveSW = new javax.swing.JButton();
        jMoveS = new javax.swing.JButton();
        jMoveSE = new javax.swing.JButton();
        jCenterX = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCenterY = new javax.swing.JTextField();
        jRefreshButton = new javax.swing.JButton();
        jMoveE1 = new javax.swing.JButton();
        jZoomInButton = new javax.swing.JButton();
        jZoomOutButton = new javax.swing.JButton();
        jCenterCoordinateIngame = new javax.swing.JButton();
        jInformationGroup = new com.l2fprod.common.swing.JTaskPaneGroup();
        jInformationPanel = new javax.swing.JPanel();
        jCurrentPlayerVillages = new javax.swing.JComboBox();
        jCurrentPlayer = new javax.swing.JLabel();
        jCenterIngameButton = new javax.swing.JButton();
        jOnlineLabel = new javax.swing.JLabel();
        jCurrentToolLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jMapGroup = new com.l2fprod.common.swing.JTaskPaneGroup();
        jPanel2 = new javax.swing.JPanel();
        jShowMapPopup = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jRadarSpinner = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLayerList = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        jLayerUpButton = new javax.swing.JButton();
        jLayerDownButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jGraphicPacks = new javax.swing.JComboBox();
        jHighlightTribeVillages = new javax.swing.JCheckBox();
        jShowRuler = new javax.swing.JCheckBox();
        jROIGroup = new com.l2fprod.common.swing.JTaskPaneGroup();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jROIBox = new javax.swing.JComboBox();
        jRemoveROIButton = new javax.swing.JButton();
        jAddROIButton = new javax.swing.JButton();
        jUVGroup = new com.l2fprod.common.swing.JTaskPaneGroup();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jUVIDField = new javax.swing.JTextField();
        jUVModeButton = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jSearchItem = new javax.swing.JMenuItem();
        jClockItem = new javax.swing.JMenuItem();
        jTribeTribeAttackItem = new javax.swing.JMenuItem();
        jUnitOverviewItem = new javax.swing.JMenuItem();
        jSelectionOverviewItem = new javax.swing.JMenuItem();
        jStartAStarItem = new javax.swing.JMenuItem();
        jDistanceItem = new javax.swing.JMenuItem();
        jDoItYourselfAttackPlanerItem = new javax.swing.JMenuItem();
        jReTimeToolEvent = new javax.swing.JMenuItem();
        jSupportCoordinator = new javax.swing.JMenuItem();
        jSOSAnalyzerItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jShowAttackFrame = new javax.swing.JCheckBoxMenuItem();
        jShowMarkerFrame = new javax.swing.JCheckBoxMenuItem();
        jShowTroopsFrame = new javax.swing.JCheckBoxMenuItem();
        jShowRankFrame = new javax.swing.JCheckBoxMenuItem();
        jShowFormsFrame = new javax.swing.JCheckBoxMenuItem();
        jShowChurchFrame = new javax.swing.JCheckBoxMenuItem();
        jShowConquersFrame = new javax.swing.JCheckBoxMenuItem();
        jShowNotepadFrame = new javax.swing.JCheckBoxMenuItem();
        jShowTagFrame = new javax.swing.JCheckBoxMenuItem();
        jShowStatsFrame = new javax.swing.JCheckBoxMenuItem();
        jShowReportFrame = new javax.swing.JCheckBoxMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jHelpItem = new javax.swing.JMenuItem();
        jAboutItem = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        jLabel3.setText(bundle.getString("DSWorkbenchMainFrame.jLabel3.text")); // NOI18N

        jFileTypeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "png", "gif", "jpeg" }));
        jFileTypeChooser.setMaximumSize(new java.awt.Dimension(80, 22));
        jFileTypeChooser.setMinimumSize(new java.awt.Dimension(80, 22));
        jFileTypeChooser.setPreferredSize(new java.awt.Dimension(80, 22));

        jButton2.setText(bundle.getString("DSWorkbenchMainFrame.jButton2.text")); // NOI18N
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCreateMapShotEvent(evt);
            }
        });

        jButton3.setText(bundle.getString("DSWorkbenchMainFrame.jButton3.text")); // NOI18N
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCancelMapShotEvent(evt);
            }
        });

        javax.swing.GroupLayout jMapShotDialogLayout = new javax.swing.GroupLayout(jMapShotDialog.getContentPane());
        jMapShotDialog.getContentPane().setLayout(jMapShotDialogLayout);
        jMapShotDialogLayout.setHorizontalGroup(
            jMapShotDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMapShotDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMapShotDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jMapShotDialogLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jFileTypeChooser, 0, 123, Short.MAX_VALUE))
                    .addGroup(jMapShotDialogLayout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jMapShotDialogLayout.setVerticalGroup(
            jMapShotDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMapShotDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMapShotDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jFileTypeChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jMapShotDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jExportDialog.setTitle(bundle.getString("DSWorkbenchMainFrame.jExportDialog.title")); // NOI18N
        jExportDialog.setAlwaysOnTop(true);

        jScrollPane1.setOpaque(false);

        jAttackExportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Angriffplan", "Exportieren"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jAttackExportTable.setOpaque(false);
        jScrollPane1.setViewportView(jAttackExportTable);

        jExportTags.setText(bundle.getString("DSWorkbenchMainFrame.jExportTags.text")); // NOI18N
        jExportTags.setOpaque(false);

        jExportTroops.setText(bundle.getString("DSWorkbenchMainFrame.jExportTroops.text")); // NOI18N
        jExportTroops.setOpaque(false);

        jExportForms.setText(bundle.getString("DSWorkbenchMainFrame.jExportForms.text")); // NOI18N
        jExportForms.setOpaque(false);

        jExportButton.setText(bundle.getString("DSWorkbenchMainFrame.jExportButton.text")); // NOI18N
        jExportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireExportEvent(evt);
            }
        });

        jCancelExportButton.setText(bundle.getString("DSWorkbenchMainFrame.jCancelExportButton.text")); // NOI18N
        jCancelExportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireExportEvent(evt);
            }
        });

        jScrollPane4.setOpaque(false);

        jMarkerSetExportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Markierungsset", "Exportieren"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jMarkerSetExportTable.setOpaque(false);
        jScrollPane4.setViewportView(jMarkerSetExportTable);

        jExportNotes.setText(bundle.getString("DSWorkbenchMainFrame.jExportNotes.text")); // NOI18N
        jExportNotes.setOpaque(false);

        javax.swing.GroupLayout jExportDialogLayout = new javax.swing.GroupLayout(jExportDialog.getContentPane());
        jExportDialog.getContentPane().setLayout(jExportDialogLayout);
        jExportDialogLayout.setHorizontalGroup(
            jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jExportDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jExportDialogLayout.createSequentialGroup()
                        .addComponent(jExportTroops)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(jExportForms))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addGroup(jExportDialogLayout.createSequentialGroup()
                        .addComponent(jExportTags)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addComponent(jExportNotes))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jExportDialogLayout.createSequentialGroup()
                        .addComponent(jCancelExportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jExportButton))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
                .addContainerGap())
        );
        jExportDialogLayout.setVerticalGroup(
            jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jExportDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jExportTroops)
                    .addComponent(jExportForms))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jExportTags)
                    .addComponent(jExportNotes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jExportDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jExportButton)
                    .addComponent(jCancelExportButton))
                .addContainerGap())
        );

        jAddROIDialog.setTitle(bundle.getString("DSWorkbenchMainFrame.jAddROIDialog.title")); // NOI18N

        jLabel7.setText(bundle.getString("DSWorkbenchMainFrame.jLabel7.text")); // NOI18N

        jROIRegion.setText(bundle.getString("DSWorkbenchMainFrame.jROIRegion.text")); // NOI18N
        jROIRegion.setEnabled(false);
        jROIRegion.setMaximumSize(new java.awt.Dimension(120, 20));
        jROIRegion.setMinimumSize(new java.awt.Dimension(120, 20));
        jROIRegion.setPreferredSize(new java.awt.Dimension(120, 20));

        jLabel8.setText(bundle.getString("DSWorkbenchMainFrame.jLabel8.text")); // NOI18N

        jROITextField.setText(bundle.getString("DSWorkbenchMainFrame.jROITextField.text")); // NOI18N
        jROITextField.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jROITextField.toolTipText")); // NOI18N

        jLabel9.setText(bundle.getString("DSWorkbenchMainFrame.jLabel9.text")); // NOI18N

        jROIPosition.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Ende" }));
        jROIPosition.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jROIPosition.toolTipText")); // NOI18N

        jAddNewROIButton.setText(bundle.getString("DSWorkbenchMainFrame.jAddNewROIButton.text")); // NOI18N
        jAddNewROIButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jAddNewROIButton.toolTipText")); // NOI18N
        jAddNewROIButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddROIDoneEvent(evt);
            }
        });

        jButton5.setText(bundle.getString("DSWorkbenchMainFrame.jButton5.text")); // NOI18N
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddROIDoneEvent(evt);
            }
        });

        javax.swing.GroupLayout jAddROIDialogLayout = new javax.swing.GroupLayout(jAddROIDialog.getContentPane());
        jAddROIDialog.getContentPane().setLayout(jAddROIDialogLayout);
        jAddROIDialogLayout.setHorizontalGroup(
            jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddROIDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jAddROIDialogLayout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAddNewROIButton))
                    .addGroup(jAddROIDialogLayout.createSequentialGroup()
                        .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jROIRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jROITextField, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addComponent(jROIPosition, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jAddROIDialogLayout.setVerticalGroup(
            jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddROIDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jROIRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jROITextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jROIPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jAddROIDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jAddNewROIButton)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(jTable1);

        jButton4.setText(bundle.getString("DSWorkbenchMainFrame.jButton4.text")); // NOI18N
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                switchPanel(evt);
            }
        });

        javax.swing.GroupLayout jCustomPanelLayout = new javax.swing.GroupLayout(jCustomPanel);
        jCustomPanel.setLayout(jCustomPanelLayout);
        jCustomPanelLayout.setHorizontalGroup(
            jCustomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCustomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jCustomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                    .addComponent(jButton4))
                .addContainerGap())
        );
        jCustomPanelLayout.setVerticalGroup(
            jCustomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jCustomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("DSWorkbenchMainFrame.title")); // NOI18N
        setBackground(new java.awt.Color(225, 213, 190));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                fireDSWorkbenchClosingEvent(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                fireFrameResizedEvent(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 64, 0), 2));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jMinimapPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 64, 0), 2));
        jMinimapPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setFocusTraversalPolicyProvider(true);

        jTaskPane1.setBackground(new java.awt.Color(239, 235, 223));
        com.l2fprod.common.swing.PercentLayout percentLayout1 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout1.setOrientation(1);
        percentLayout1.setGap(5);
        jTaskPane1.setLayout(percentLayout1);

        jNavigationGroup.setExpanded(false);
        jNavigationGroup.setTitle(bundle.getString("DSWorkbenchMainFrame.jNavigationGroup.title")); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout6 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout6.setOrientation(1);
        jNavigationGroup.getContentPane().setLayout(percentLayout6);

        jNavigationPanel.setBackground(new java.awt.Color(239, 235, 223));

        jMoveE.setBackground(new java.awt.Color(239, 235, 223));
        jMoveE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_e.png"))); // NOI18N
        jMoveE.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveE.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveE.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveNE.setBackground(new java.awt.Color(239, 235, 223));
        jMoveNE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_ne.png"))); // NOI18N
        jMoveNE.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveNE.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveNE.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveNE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveN.setBackground(new java.awt.Color(239, 235, 223));
        jMoveN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_n.png"))); // NOI18N
        jMoveN.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveN.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveN.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveN.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveNW.setBackground(new java.awt.Color(239, 235, 223));
        jMoveNW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_nw.png"))); // NOI18N
        jMoveNW.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveNW.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveNW.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveNW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveW.setBackground(new java.awt.Color(239, 235, 223));
        jMoveW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_w.png"))); // NOI18N
        jMoveW.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveW.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveW.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveSW.setBackground(new java.awt.Color(239, 235, 223));
        jMoveSW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_sw.png"))); // NOI18N
        jMoveSW.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveSW.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveSW.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveSW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveS.setBackground(new java.awt.Color(239, 235, 223));
        jMoveS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_s.png"))); // NOI18N
        jMoveS.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveS.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveS.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jMoveSE.setBackground(new java.awt.Color(239, 235, 223));
        jMoveSE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/map_se.png"))); // NOI18N
        jMoveSE.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveSE.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveSE.setPreferredSize(new java.awt.Dimension(21, 21));
        jMoveSE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireMoveMapEvent(evt);
            }
        });

        jCenterX.setText(bundle.getString("DSWorkbenchMainFrame.jCenterX.text")); // NOI18N
        jCenterX.setMaximumSize(new java.awt.Dimension(40, 20));
        jCenterX.setMinimumSize(new java.awt.Dimension(40, 20));
        jCenterX.setPreferredSize(new java.awt.Dimension(40, 20));

        jLabel1.setText(bundle.getString("DSWorkbenchMainFrame.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("DSWorkbenchMainFrame.jLabel2.text")); // NOI18N

        jCenterY.setText(bundle.getString("DSWorkbenchMainFrame.jCenterY.text")); // NOI18N
        jCenterY.setMaximumSize(new java.awt.Dimension(40, 20));
        jCenterY.setMinimumSize(new java.awt.Dimension(40, 20));
        jCenterY.setPreferredSize(new java.awt.Dimension(40, 20));

        jRefreshButton.setBackground(new java.awt.Color(239, 235, 223));
        jRefreshButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jRefreshButton.toolTipText")); // NOI18N
        jRefreshButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jRefreshButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jRefreshButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jRefreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRefreshMapEvent(evt);
            }
        });

        jMoveE1.setBackground(new java.awt.Color(239, 235, 223));
        jMoveE1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMoveE1.setEnabled(false);
        jMoveE1.setMaximumSize(new java.awt.Dimension(21, 21));
        jMoveE1.setMinimumSize(new java.awt.Dimension(21, 21));
        jMoveE1.setPreferredSize(new java.awt.Dimension(21, 21));

        jZoomInButton.setBackground(new java.awt.Color(239, 235, 223));
        jZoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_out.png"))); // NOI18N
        jZoomInButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jZoomInButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jZoomInButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jZoomInButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireZoomEvent(evt);
            }
        });

        jZoomOutButton.setBackground(new java.awt.Color(239, 235, 223));
        jZoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_in.png"))); // NOI18N
        jZoomOutButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jZoomOutButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jZoomOutButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jZoomOutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireZoomEvent(evt);
            }
        });

        jCenterCoordinateIngame.setBackground(new java.awt.Color(239, 235, 223));
        jCenterCoordinateIngame.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jCenterCoordinateIngame.toolTipText")); // NOI18N
        jCenterCoordinateIngame.setMaximumSize(new java.awt.Dimension(30, 30));
        jCenterCoordinateIngame.setMinimumSize(new java.awt.Dimension(30, 30));
        jCenterCoordinateIngame.setPreferredSize(new java.awt.Dimension(30, 30));
        jCenterCoordinateIngame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterCurrentPosInGameEvent(evt);
            }
        });

        javax.swing.GroupLayout jNavigationPanelLayout = new javax.swing.GroupLayout(jNavigationPanel);
        jNavigationPanel.setLayout(jNavigationPanelLayout);
        jNavigationPanelLayout.setHorizontalGroup(
            jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNavigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNavigationPanelLayout.createSequentialGroup()
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jNavigationPanelLayout.createSequentialGroup()
                                .addComponent(jMoveNW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jMoveN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jNavigationPanelLayout.createSequentialGroup()
                                .addComponent(jMoveW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jMoveE1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jMoveNE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jNavigationPanelLayout.createSequentialGroup()
                        .addComponent(jMoveSW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMoveS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMoveSE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jZoomInButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jZoomOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCenterX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCenterY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCenterCoordinateIngame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jNavigationPanelLayout.setVerticalGroup(
            jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNavigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jNavigationPanelLayout.createSequentialGroup()
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jMoveNE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveNW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jMoveE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveE1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jMoveSW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveSE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jNavigationPanelLayout.createSequentialGroup()
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCenterX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jCenterY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jNavigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jNavigationPanelLayout.createSequentialGroup()
                            .addComponent(jRefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCenterCoordinateIngame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(5, 5, 5))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jNavigationPanelLayout.createSequentialGroup()
                            .addComponent(jZoomInButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jZoomOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jNavigationGroup.getContentPane().add(jNavigationPanel);

        jTaskPane1.add(jNavigationGroup);

        jInformationGroup.setBackground(new java.awt.Color(239, 235, 223));
        jInformationGroup.setExpanded(false);
        jInformationGroup.setTitle(bundle.getString("DSWorkbenchMainFrame.jInformationGroup.title")); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout5 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout5.setOrientation(1);
        jInformationGroup.getContentPane().setLayout(percentLayout5);

        jInformationPanel.setBackground(new java.awt.Color(239, 235, 223));

        jCurrentPlayerVillages.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jCurrentPlayerVillages.toolTipText")); // NOI18N
        jCurrentPlayerVillages.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                fireCurrentPlayerVillagePopupEvent(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jCurrentPlayer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jCurrentPlayer.setMaximumSize(new java.awt.Dimension(155, 14));
        jCurrentPlayer.setMinimumSize(new java.awt.Dimension(155, 14));
        jCurrentPlayer.setPreferredSize(new java.awt.Dimension(155, 14));

        jCenterIngameButton.setBackground(new java.awt.Color(239, 235, 223));
        jCenterIngameButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jCenterIngameButton.toolTipText")); // NOI18N
        jCenterIngameButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jCenterIngameButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jCenterIngameButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jCenterIngameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterVillageIngameEvent(evt);
            }
        });

        jOnlineLabel.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jOnlineLabel.toolTipText")); // NOI18N
        jOnlineLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jOnlineLabel.setMaximumSize(new java.awt.Dimension(30, 30));
        jOnlineLabel.setMinimumSize(new java.awt.Dimension(30, 30));
        jOnlineLabel.setPreferredSize(new java.awt.Dimension(30, 30));

        jCurrentToolLabel.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jCurrentToolLabel.toolTipText")); // NOI18N
        jCurrentToolLabel.setAlignmentY(0.0F);
        jCurrentToolLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jCurrentToolLabel.setIconTextGap(0);
        jCurrentToolLabel.setMaximumSize(new java.awt.Dimension(30, 30));
        jCurrentToolLabel.setMinimumSize(new java.awt.Dimension(30, 30));
        jCurrentToolLabel.setPreferredSize(new java.awt.Dimension(30, 30));
        jCurrentToolLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                firePanelMin(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/camera.png"))); // NOI18N
        jButton1.setText(bundle.getString("DSWorkbenchMainFrame.jButton1.text")); // NOI18N
        jButton1.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jButton1.toolTipText")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(30, 30));
        jButton1.setMinimumSize(new java.awt.Dimension(30, 30));
        jButton1.setPreferredSize(new java.awt.Dimension(30, 30));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCreateMapShotEvent(evt);
            }
        });

        javax.swing.GroupLayout jInformationPanelLayout = new javax.swing.GroupLayout(jInformationPanel);
        jInformationPanel.setLayout(jInformationPanelLayout);
        jInformationPanelLayout.setHorizontalGroup(
            jInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCurrentPlayer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .addComponent(jCurrentPlayerVillages, javax.swing.GroupLayout.Alignment.LEADING, 0, 218, Short.MAX_VALUE)
                    .addGroup(jInformationPanelLayout.createSequentialGroup()
                        .addComponent(jCurrentToolLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(jCenterIngameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jOnlineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jInformationPanelLayout.setVerticalGroup(
            jInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCurrentPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCurrentPlayerVillages, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOnlineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCenterIngameButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCurrentToolLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jInformationGroup.getContentPane().add(jInformationPanel);

        jTaskPane1.add(jInformationGroup);

        jMapGroup.setTitle(bundle.getString("DSWorkbenchMainFrame.jMapGroup.title")); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout4 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout4.setOrientation(1);
        jMapGroup.getContentPane().setLayout(percentLayout4);

        jPanel2.setBackground(new java.awt.Color(239, 235, 223));
        jPanel2.setMaximumSize(new java.awt.Dimension(100, 281));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 281));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 281));

        jShowMapPopup.setText(bundle.getString("DSWorkbenchMainFrame.jShowMapPopup.text")); // NOI18N
        jShowMapPopup.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jShowMapPopup.toolTipText")); // NOI18N
        jShowMapPopup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jShowMapPopup.setOpaque(false);
        jShowMapPopup.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireShowMapPopupChangedEvent(evt);
            }
        });

        jLabel5.setText(bundle.getString("DSWorkbenchMainFrame.jLabel5.text")); // NOI18N

        jRadarSpinner.setModel(new javax.swing.SpinnerDateModel());
        jRadarSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireRadarSpinnerChangedEvent(evt);
            }
        });

        jLayerList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jLayerList);

        jLabel10.setText(bundle.getString("DSWorkbenchMainFrame.jLabel10.text")); // NOI18N

        jLayerUpButton.setBackground(new java.awt.Color(239, 235, 223));
        jLayerUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/arrow_up.png"))); // NOI18N
        jLayerUpButton.setText(bundle.getString("DSWorkbenchMainFrame.jLayerUpButton.text")); // NOI18N
        jLayerUpButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jLayerUpButton.toolTipText")); // NOI18N
        jLayerUpButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jLayerUpButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jLayerUpButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jLayerUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeDrawOrderEvent(evt);
            }
        });

        jLayerDownButton.setBackground(new java.awt.Color(239, 235, 223));
        jLayerDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/arrow_down.png"))); // NOI18N
        jLayerDownButton.setText(bundle.getString("DSWorkbenchMainFrame.jLayerDownButton.text")); // NOI18N
        jLayerDownButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jLayerDownButton.toolTipText")); // NOI18N
        jLayerDownButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jLayerDownButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jLayerDownButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jLayerDownButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeDrawOrderEvent(evt);
            }
        });

        jLabel12.setText(bundle.getString("DSWorkbenchMainFrame.jLabel12.text")); // NOI18N

        jGraphicPacks.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireGraphicPackChangedEvent(evt);
            }
        });

        jHighlightTribeVillages.setText(bundle.getString("DSWorkbenchMainFrame.jHighlightTribeVillages.text")); // NOI18N
        jHighlightTribeVillages.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jHighlightTribeVillages.toolTipText")); // NOI18N
        jHighlightTribeVillages.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jHighlightTribeVillages.setOpaque(false);
        jHighlightTribeVillages.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireHighlightTribeVillagesChangedEvent(evt);
            }
        });

        jShowRuler.setText(bundle.getString("DSWorkbenchMainFrame.jShowRuler.text")); // NOI18N
        jShowRuler.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jShowRuler.toolTipText")); // NOI18N
        jShowRuler.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jShowRuler.setOpaque(false);
        jShowRuler.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireShowRulerChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jGraphicPacks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(10, 10, 10)
                        .addComponent(jRadarSpinner))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayerUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLayerDownButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jShowRuler, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jHighlightTribeVillages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jShowMapPopup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jGraphicPacks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jShowMapPopup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jHighlightTribeVillages)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jShowRuler)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jRadarSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLayerUpButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayerDownButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addContainerGap())
        );

        jMapGroup.getContentPane().add(jPanel2);

        jTaskPane1.add(jMapGroup);

        jROIGroup.setExpanded(false);
        jROIGroup.setTitle(bundle.getString("DSWorkbenchMainFrame.jROIGroup.title")); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout2 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout2.setOrientation(1);
        jROIGroup.getContentPane().setLayout(percentLayout2);

        jPanel3.setBackground(new java.awt.Color(239, 235, 223));
        jPanel3.setMaximumSize(new java.awt.Dimension(293, 70));
        jPanel3.setMinimumSize(new java.awt.Dimension(293, 70));
        jPanel3.setPreferredSize(new java.awt.Dimension(293, 70));

        jLabel6.setText(bundle.getString("DSWorkbenchMainFrame.jLabel6.text")); // NOI18N

        jROIBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireROISelectedEvent(evt);
            }
        });

        jRemoveROIButton.setBackground(new java.awt.Color(239, 235, 223));
        jRemoveROIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jRemoveROIButton.setText(bundle.getString("DSWorkbenchMainFrame.jRemoveROIButton.text")); // NOI18N
        jRemoveROIButton.setMaximumSize(new java.awt.Dimension(23, 23));
        jRemoveROIButton.setMinimumSize(new java.awt.Dimension(23, 23));
        jRemoveROIButton.setPreferredSize(new java.awt.Dimension(23, 23));
        jRemoveROIButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeROIEvent(evt);
            }
        });

        jAddROIButton.setBackground(new java.awt.Color(239, 235, 223));
        jAddROIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jAddROIButton.setText(bundle.getString("DSWorkbenchMainFrame.jAddROIButton.text")); // NOI18N
        jAddROIButton.setMaximumSize(new java.awt.Dimension(23, 23));
        jAddROIButton.setMinimumSize(new java.awt.Dimension(23, 23));
        jAddROIButton.setPreferredSize(new java.awt.Dimension(23, 23));
        jAddROIButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireChangeROIEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jROIBox, 0, 18, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jRemoveROIButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAddROIButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jROIBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jAddROIButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRemoveROIButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jROIGroup.getContentPane().add(jPanel3);

        jTaskPane1.add(jROIGroup);

        jUVGroup.setExpanded(false);
        jUVGroup.setTitle(bundle.getString("DSWorkbenchMainFrame.jUVGroup.title")); // NOI18N
        com.l2fprod.common.swing.PercentLayout percentLayout3 = new com.l2fprod.common.swing.PercentLayout();
        percentLayout3.setOrientation(1);
        jUVGroup.getContentPane().setLayout(percentLayout3);

        jPanel4.setBackground(new java.awt.Color(239, 235, 223));

        jLabel4.setText(bundle.getString("DSWorkbenchMainFrame.jLabel4.text")); // NOI18N

        jUVIDField.setText(bundle.getString("DSWorkbenchMainFrame.jUVIDField.text")); // NOI18N

        jUVModeButton.setBackground(new java.awt.Color(239, 235, 223));
        jUVModeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/uv_off.png"))); // NOI18N
        jUVModeButton.setText(bundle.getString("DSWorkbenchMainFrame.jUVModeButton.text")); // NOI18N
        jUVModeButton.setToolTipText(bundle.getString("DSWorkbenchMainFrame.jUVModeButton.toolTipText")); // NOI18N
        jUVModeButton.setMaximumSize(new java.awt.Dimension(35, 35));
        jUVModeButton.setMinimumSize(new java.awt.Dimension(35, 35));
        jUVModeButton.setPreferredSize(new java.awt.Dimension(35, 35));
        jUVModeButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeUVModeEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUVIDField))
                    .addComponent(jUVModeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jUVIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUVModeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jUVGroup.getContentPane().add(jPanel4);

        jTaskPane1.add(jUVGroup);

        jScrollPane2.setViewportView(jTaskPane1);

        jMenuBar1.setBackground(new java.awt.Color(225, 213, 190));

        jMenu1.setBackground(new java.awt.Color(225, 213, 190));
        jMenu1.setMnemonic('a');
        jMenu1.setText(bundle.getString("DSWorkbenchMainFrame.jMenu1.text")); // NOI18N

        jMenuItem1.setBackground(new java.awt.Color(239, 235, 223));
        jMenuItem1.setMnemonic('t');
        jMenuItem1.setText(bundle.getString("DSWorkbenchMainFrame.jMenuItem1.text")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowSettingsEvent(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator2);

        jMenuItem3.setBackground(new java.awt.Color(239, 235, 223));
        jMenuItem3.setText(bundle.getString("DSWorkbenchMainFrame.jMenuItem3.text")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowImportDialogEvent(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setBackground(new java.awt.Color(239, 235, 223));
        jMenuItem4.setText(bundle.getString("DSWorkbenchMainFrame.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireOpenExportDialogEvent(evt);
            }
        });
        jMenu1.add(jMenuItem4);
        jMenu1.add(jSeparator1);

        jMenuItem2.setBackground(new java.awt.Color(239, 235, 223));
        jMenuItem2.setMnemonic('n');
        jMenuItem2.setText(bundle.getString("DSWorkbenchMainFrame.jMenuItem2.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireExitEvent(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu3.setBackground(new java.awt.Color(225, 213, 190));
        jMenu3.setMnemonic('e');
        jMenu3.setText(bundle.getString("DSWorkbenchMainFrame.jMenu3.text")); // NOI18N

        jSearchItem.setBackground(new java.awt.Color(239, 235, 223));
        jSearchItem.setMnemonic('s');
        jSearchItem.setText(bundle.getString("DSWorkbenchMainFrame.jSearchItem.text")); // NOI18N
        jSearchItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jSearchItem);

        jClockItem.setBackground(new java.awt.Color(239, 235, 223));
        jClockItem.setText(bundle.getString("DSWorkbenchMainFrame.jClockItem.text")); // NOI18N
        jClockItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jClockItem);

        jTribeTribeAttackItem.setBackground(new java.awt.Color(239, 235, 223));
        jTribeTribeAttackItem.setText(bundle.getString("DSWorkbenchMainFrame.jTribeTribeAttackItem.text")); // NOI18N
        jTribeTribeAttackItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jTribeTribeAttackItem);

        jUnitOverviewItem.setBackground(new java.awt.Color(239, 235, 223));
        jUnitOverviewItem.setText(bundle.getString("DSWorkbenchMainFrame.jUnitOverviewItem.text")); // NOI18N
        jUnitOverviewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jUnitOverviewItem);

        jSelectionOverviewItem.setBackground(new java.awt.Color(239, 235, 223));
        jSelectionOverviewItem.setText(bundle.getString("DSWorkbenchMainFrame.jSelectionOverviewItem.text_1")); // NOI18N
        jSelectionOverviewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jSelectionOverviewItem);

        jStartAStarItem.setBackground(new java.awt.Color(239, 235, 223));
        jStartAStarItem.setText(bundle.getString("DSWorkbenchMainFrame.jStartAStarItem.text_1")); // NOI18N
        jStartAStarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jStartAStarItem);

        jDistanceItem.setBackground(new java.awt.Color(239, 235, 223));
        jDistanceItem.setText(bundle.getString("DSWorkbenchMainFrame.jDistanceItem.text_1")); // NOI18N
        jDistanceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jDistanceItem);

        jDoItYourselfAttackPlanerItem.setBackground(new java.awt.Color(239, 235, 223));
        jDoItYourselfAttackPlanerItem.setText(bundle.getString("DSWorkbenchMainFrame.jDoItYourselfAttackPlanerItem.text_1")); // NOI18N
        jDoItYourselfAttackPlanerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jDoItYourselfAttackPlanerItem);

        jReTimeToolEvent.setBackground(new java.awt.Color(239, 235, 223));
        jReTimeToolEvent.setText(bundle.getString("DSWorkbenchMainFrame.jReTimeToolEvent.text_1")); // NOI18N
        jReTimeToolEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jReTimeToolEvent);

        jSupportCoordinator.setBackground(new java.awt.Color(239, 235, 223));
        jSupportCoordinator.setText(bundle.getString("DSWorkbenchMainFrame.jSupportCoordinator.text_1")); // NOI18N
        jSupportCoordinator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jSupportCoordinator);

        jSOSAnalyzerItem.setBackground(new java.awt.Color(239, 235, 223));
        jSOSAnalyzerItem.setText(bundle.getString("DSWorkbenchMainFrame.jSOSAnalyzerItem.text_1")); // NOI18N
        jSOSAnalyzerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireToolsActionEvent(evt);
            }
        });
        jMenu3.add(jSOSAnalyzerItem);

        jMenuBar1.add(jMenu3);

        jMenu2.setBackground(new java.awt.Color(225, 213, 190));
        jMenu2.setMnemonic('n');
        jMenu2.setText(bundle.getString("DSWorkbenchMainFrame.jMenu2.text")); // NOI18N

        jShowAttackFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowAttackFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowAttackFrame.text")); // NOI18N
        jShowAttackFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowAttackFrameEvent(evt);
            }
        });
        jMenu2.add(jShowAttackFrame);

        jShowMarkerFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowMarkerFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowMarkerFrame.text")); // NOI18N
        jShowMarkerFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowMarkerFrameEvent(evt);
            }
        });
        jMenu2.add(jShowMarkerFrame);

        jShowTroopsFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowTroopsFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowTroopsFrame.text")); // NOI18N
        jShowTroopsFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowTroopsFrameEvent(evt);
            }
        });
        jMenu2.add(jShowTroopsFrame);

        jShowRankFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowRankFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowRankFrame.text")); // NOI18N
        jShowRankFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowRangFrameEvent(evt);
            }
        });
        jMenu2.add(jShowRankFrame);

        jShowFormsFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowFormsFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowFormsFrame.text")); // NOI18N
        jShowFormsFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowFormsFrameEvent(evt);
            }
        });
        jMenu2.add(jShowFormsFrame);

        jShowChurchFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowChurchFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowChurchFrame.text")); // NOI18N
        jShowChurchFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowChurchFrameEvent(evt);
            }
        });
        jMenu2.add(jShowChurchFrame);

        jShowConquersFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowConquersFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowConquersFrame.text")); // NOI18N
        jShowConquersFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowConquersFrameEvent(evt);
            }
        });
        jMenu2.add(jShowConquersFrame);

        jShowNotepadFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowNotepadFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowNotepadFrame.text")); // NOI18N
        jShowNotepadFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowNotepadEvent(evt);
            }
        });
        jMenu2.add(jShowNotepadFrame);

        jShowTagFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowTagFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowTagFrame.text")); // NOI18N
        jShowTagFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowTagFrameEvent(evt);
            }
        });
        jMenu2.add(jShowTagFrame);

        jShowStatsFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowStatsFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowStatsFrame.text")); // NOI18N
        jShowStatsFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowStatsFrameEvent(evt);
            }
        });
        jMenu2.add(jShowStatsFrame);

        jShowReportFrame.setBackground(new java.awt.Color(239, 235, 223));
        jShowReportFrame.setText(bundle.getString("DSWorkbenchMainFrame.jShowReportFrame.text")); // NOI18N
        jShowReportFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowReportFrameEvent(evt);
            }
        });
        jMenu2.add(jShowReportFrame);

        jMenuBar1.add(jMenu2);

        jMenu4.setBackground(new java.awt.Color(225, 213, 190));
        jMenu4.setText(bundle.getString("DSWorkbenchMainFrame.jMenu4.text")); // NOI18N

        jHelpItem.setBackground(new java.awt.Color(239, 235, 223));
        jHelpItem.setText(bundle.getString("DSWorkbenchMainFrame.jHelpItem.text")); // NOI18N
        jMenu4.add(jHelpItem);

        jAboutItem.setBackground(new java.awt.Color(239, 235, 223));
        jAboutItem.setText(bundle.getString("DSWorkbenchMainFrame.jAboutItem.text")); // NOI18N
        jAboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowAboutEvent(evt);
            }
        });
        jMenu4.add(jAboutItem);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addComponent(jMinimapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jMinimapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /**Update map position*/
private void fireRefreshMapEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRefreshMapEvent
    double cx = dCenterX;
    double cy = dCenterY;
    try {
        cx = Integer.parseInt(jCenterX.getText());
        cy = Integer.parseInt(jCenterY.getText());
    } catch (Exception e) {
        cx = dCenterX;
        cy = dCenterY;
        jCenterX.setText(Integer.toString((int) cx));
        jCenterY.setText(Integer.toString((int) cy));
    }

    if (ServerSettings.getSingleton().getCoordType() != 2) {
        int[] hier = DSCalculator.hierarchicalToXy((int) cx, (int) cy, 12);
        if (hier != null) {
            dCenterX = hier[0];
            dCenterY = hier[1];
        }
    } else {
        dCenterX = cx;
        dCenterY = cy;
    }
    double w = (double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
    double h = (double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    MinimapPanel.getSingleton().setSelection((int) Math.floor(dCenterX), (int) Math.floor(dCenterY), (int) Math.rint(w), (int) Math.rint(h));
    MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);
}//GEN-LAST:event_fireRefreshMapEvent

    /**Update map movement*/
private void fireMoveMapEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireMoveMapEvent
    double cx = dCenterX;
    double cy = dCenterY;
    try {
        cx = Integer.parseInt(jCenterX.getText());
        cy = Integer.parseInt(jCenterY.getText());
    } catch (Exception e) {
        cx = dCenterX;
        cy = dCenterY;
    }

    if (ServerSettings.getSingleton().getCoordType() != 2) {
        int[] hier = DSCalculator.hierarchicalToXy((int) cx, (int) cy, 12);
        if (hier != null) {
            cx = hier[0];
            cy = hier[1];
        }
    }

    if (evt.getSource() == jMoveN) {
        cy -= (double) MapPanel.getSingleton().getHeight() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveNE) {
        cx += (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        cy -= (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveE) {
        cx += (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveSE) {
        cx += (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        cy += (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveS) {
        cy += (double) MapPanel.getSingleton().getHeight() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveSW) {
        cx -= (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        cy += (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveW) {
        cx -= (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    } else if (evt.getSource() == jMoveNW) {
        cx -= (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        cy -= (double) MapPanel.getSingleton().getWidth() / (double) GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    }

    if (ServerSettings.getSingleton().getCoordType() != 2) {
        int[] hier = DSCalculator.xyToHierarchical((int) cx, (int) cy);
        if (hier != null) {
            cx = hier[0];
            cy = hier[1];
        }
    }

    jCenterX.setText(Integer.toString((int) Math.floor(cx)));
    jCenterY.setText(Integer.toString((int) Math.floor(cy)));
    dCenterX = cx;
    dCenterY = cy;
    MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);
    double w = (double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
    double h = (double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
    MinimapPanel.getSingleton().setSelection((int) Math.floor(cx), (int) Math.floor(cy), (int) Math.rint(w), (int) Math.rint(h));
}//GEN-LAST:event_fireMoveMapEvent

    /**React on resize events*/
private void fireFrameResizedEvent(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fireFrameResizedEvent
    try {
        MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);
    } catch (Exception e) {
        logger.error("Failed to resize map for (" + dCenterX + ", " + dCenterY + ")", e);
    }
}//GEN-LAST:event_fireFrameResizedEvent

    /**Zoom main map*/
private void fireZoomEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireZoomEvent
    if (evt.getSource() == jZoomInButton) {
        zoomIn();
    } else {
        zoomOut();
    }
}//GEN-LAST:event_fireZoomEvent

    protected synchronized void zoomIn() {
        dZoomFactor += 1.0 / 10.0;
        checkZoomRange();

        dZoomFactor = Double.parseDouble(NumberFormat.getInstance().format(dZoomFactor).replaceAll(",", "."));

        double w = (double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        double h = (double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
        int xPos = Integer.parseInt(jCenterX.getText());
        int yPos = Integer.parseInt(jCenterY.getText());
        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] hier = DSCalculator.hierarchicalToXy((int) xPos, (int) yPos, 12);
            if (hier != null) {
                xPos = hier[0];
                yPos = hier[1];
            }
        }
        MinimapPanel.getSingleton().setSelection(xPos, yPos, (int) Math.rint(w), (int) Math.rint(h));
        MapPanel.getSingleton().updateMapPosition(xPos, yPos);
    }

    protected synchronized void zoomOut() {
        dZoomFactor -= 1.0 / 10.0;
        checkZoomRange();

        dZoomFactor = Double.parseDouble(NumberFormat.getInstance().format(dZoomFactor).replaceAll(",", "."));

        double w = (double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        double h = (double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
        int xPos = Integer.parseInt(jCenterX.getText());
        int yPos = Integer.parseInt(jCenterY.getText());

        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] hier = DSCalculator.hierarchicalToXy((int) xPos, (int) yPos, 12);
            if (hier != null) {
                xPos = hier[0];
                yPos = hier[1];
            }
        }

        MinimapPanel.getSingleton().setSelection(xPos, yPos, (int) Math.rint(w), (int) Math.rint(h));
        MapPanel.getSingleton().updateMapPosition(xPos, yPos);
    }

    /**Change active player village*/
    /**Show settings dialog*/
private void fireShowSettingsEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowSettingsEvent
    DSWorkbenchSettingsDialog.getSingleton().setVisible(true);
}//GEN-LAST:event_fireShowSettingsEvent

    /**Exit the application*/
private void fireExitEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireExitEvent
    // GlobalOptions.saveProperties();
    fireDSWorkbenchClosingEvent(null);
}//GEN-LAST:event_fireExitEvent

    /**Show the toolbar*/
    /**Center village Ingame*/
private void fireCenterVillageIngameEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterVillageIngameEvent
    if (!jCenterIngameButton.isEnabled()) {
        return;
    }

    Village v = (Village) jCurrentPlayerVillages.getSelectedItem();
    if (v != null) {
        BrowserCommandSender.centerVillage(v);
    }
}//GEN-LAST:event_fireCenterVillageIngameEvent

    /**Center pos Ingame*/
private void fireCenterCurrentPosInGameEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterCurrentPosInGameEvent
    if (!jCenterCoordinateIngame.isEnabled()) {
        return;
    }
    BrowserCommandSender.centerCoordinate(Integer.parseInt(jCenterX.getText()), Integer.parseInt(jCenterY.getText()));
}//GEN-LAST:event_fireCenterCurrentPosInGameEvent

    /**Do tool action*/
private void fireToolsActionEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireToolsActionEvent
    if (evt.getSource() == jSearchItem) {
        DSWorkbenchSearchFrame.getSingleton().setVisible(true);
    } else if (evt.getSource() == jClockItem) {
        ClockFrame.getSingleton().setVisible(true);
    } else if (evt.getSource() == jTribeTribeAttackItem) {
        mTribeTribeAttackFrame.setup();
        mTribeTribeAttackFrame.setVisible(true);
    } else if (evt.getSource() == jUnitOverviewItem) {
        UnitOrderBuilder.showUnitOrder(null, null);
    } else if (evt.getSource() == jSelectionOverviewItem) {
        DSWorkbenchSelectionFrame.getSingleton().setVisible(true);
    } else if (evt.getSource() == jStartAStarItem) {
        DSWorkbenchSimulatorFrame.getSingleton().setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        DSWorkbenchSimulatorFrame.getSingleton().showIntegratedVersion(GlobalOptions.getSelectedServer());
    } else if (evt.getSource() == jDistanceItem) {
        DSWorkbenchDistanceFrame.getSingleton().setVisible(true);
    } else if (evt.getSource() == jDoItYourselfAttackPlanerItem) {
        DSWorkbenchDoItYourselfAttackPlaner.getSingleton().setVisible(true);
    } else if (evt.getSource() == jReTimeToolEvent) {
        DSWorkbenchReTimerFrame.getSingleton().setVisible(true);
    } else if (evt.getSource() == jSupportCoordinator) {
        DSWorkbenchSupportCoordinator.getSingleton().setVisible(true);
    } else if (evt.getSource() == jSOSAnalyzerItem) {
        DSWorkbenchSOSRequestAnalyzer.getSingleton().setVisible(true);
    }
}//GEN-LAST:event_fireToolsActionEvent

private void fireShowAboutEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowAboutEvent
    mAbout.setVisible(true);
}//GEN-LAST:event_fireShowAboutEvent

private void fireShowAttackFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowAttackFrameEvent
    DSWorkbenchAttackFrame.getSingleton().setVisible(!DSWorkbenchAttackFrame.getSingleton().isVisible());
    jShowAttackFrame.setSelected(DSWorkbenchAttackFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowAttackFrameEvent

private void fireShowMarkerFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowMarkerFrameEvent
    DSWorkbenchMarkerFrame.getSingleton().setVisible(!DSWorkbenchMarkerFrame.getSingleton().isVisible());
    jShowMarkerFrame.setSelected(DSWorkbenchMarkerFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowMarkerFrameEvent

private void fireShowTroopsFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowTroopsFrameEvent
    DSWorkbenchTroopsFrame.getSingleton().setVisible(!DSWorkbenchTroopsFrame.getSingleton().isVisible());
    jShowTroopsFrame.setSelected(DSWorkbenchTroopsFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowTroopsFrameEvent

private void fireCurrentPlayerVillagePopupEvent(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_fireCurrentPlayerVillagePopupEvent
    if (jCurrentPlayerVillages.getSelectedIndex() < 0) {
        return;
    }
    centerVillage((Village) jCurrentPlayerVillages.getSelectedItem());
    DSWorkbenchConquersFrame.getSingleton().repaint();
}//GEN-LAST:event_fireCurrentPlayerVillagePopupEvent

private void fireShowRangFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowRangFrameEvent
    DSWorkbenchRankFrame.getSingleton().setVisible(!DSWorkbenchRankFrame.getSingleton().isVisible());
    jShowRankFrame.setSelected(DSWorkbenchRankFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowRangFrameEvent

private void fireShowFormsFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowFormsFrameEvent
    DSWorkbenchFormFrame.getSingleton().setVisible(!DSWorkbenchFormFrame.getSingleton().isVisible());
    jShowFormsFrame.setSelected(DSWorkbenchFormFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowFormsFrameEvent

private void fireShowMapPopupChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireShowMapPopupChangedEvent
    GlobalOptions.addProperty("show.map.popup", Boolean.toString(jShowMapPopup.isSelected()));
}//GEN-LAST:event_fireShowMapPopupChangedEvent

private void fireCreateMapShotEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCreateMapShotEvent
    Component parent = fullscreenFrame;
    if (parent == null) {
        parent = this;
    }

    if (JOptionPaneHelper.showQuestionConfirmBox(parent, "Willst du die Karte online stellen oder auf deinem Rechner speichern?", "Speichern", "Nur speichern", "Online stellen") == JOptionPane.YES_OPTION) {
        putOnline = true;
        MapPanel.getSingleton().planMapShot("png", new File("tmp.png"), this);
    } else {
        putOnline = false;
        String dir = GlobalOptions.getProperty("screen.dir");
        if (dir == null) {
            dir = ".";
        }
        JFileChooser chooser = null;
        try {
            chooser = new JFileChooser(dir);
        } catch (Exception e) {
            JOptionPaneHelper.showErrorBox(this, "Konnte Dateiauswahldialog nicht öffnen.\nMöglicherweise verwendest du Windows Vista. Ist dies der Fall, beende DS Workbench, klicke mit der rechten Maustaste auf DSWorkbench.exe,\n" +
                    "wähle 'Eigenschaften' und deaktiviere dort unter 'Kompatibilität' den Windows XP Kompatibilitätsmodus.", "Fehler");
            return;
        }
        chooser.setDialogTitle("Speichern unter...");
        chooser.setSelectedFile(new File("map"));
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File f) {
                if ((f != null) && (f.isDirectory() || f.getName().endsWith(".png"))) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "PNG Image (*.png)";
            }
        });

        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File f) {
                if ((f != null) && (f.isDirectory() || f.getName().endsWith(".jpeg"))) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "JPEG Image (*.jpeg)";
            }
        });
        String type = null;
        int ret = chooser.showSaveDialog(jMapShotDialog);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                javax.swing.filechooser.FileFilter filter = chooser.getFileFilter();
                if (filter.getDescription().indexOf("jpeg") > 0) {
                    type = "jpeg";
                } else if (filter.getDescription().indexOf("png") > 0) {
                    type = "png";
                } else {
                    type = "png";
                }
                String file = f.getCanonicalPath();
                if (!file.endsWith(type)) {
                    file += "." + type;
                }
                File target = new File(file);
                if (target.exists()) {
                    //ask if overwrite

                    if (JOptionPaneHelper.showQuestionConfirmBox(jMapShotDialog, "Existierende Datei überschreiben?", "Überschreiben", "Nein", "Ja") != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                MapPanel.getSingleton().planMapShot(type, target, this);
                GlobalOptions.addProperty("screen.dir", target.getParent());
            } catch (Exception e) {
                logger.error("Failed to write map shot", e);
            }
        }
    }

    jMapShotDialog.setVisible(false);
}//GEN-LAST:event_fireCreateMapShotEvent

private void fireCancelMapShotEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCancelMapShotEvent
    jMapShotDialog.setVisible(false);
}//GEN-LAST:event_fireCancelMapShotEvent

private void fireShowImportDialogEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowImportDialogEvent
    String dir = GlobalOptions.getProperty("screen.dir");
    if (dir == null) {
        dir = ".";
    }
    JFileChooser chooser = null;
    try {
        chooser = new JFileChooser(dir);
    } catch (Exception e) {
        JOptionPaneHelper.showErrorBox(this, "Konnte Dateiauswahldialog nicht öffnen.\nMöglicherweise verwendest du Windows Vista. Ist dies der Fall, beende DS Workbench, klicke mit der rechten Maustaste auf DSWorkbench.exe,\n" +
                "wähle 'Eigenschaften' und deaktiviere dort unter 'Kompatibilität' den Windows XP Kompatibilitätsmodus.", "Fehler");
        return;
    }
    chooser.setDialogTitle("Datei auswählen");

    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

        @Override
        public boolean accept(File f) {
            if ((f != null) && (f.isDirectory() || f.getName().endsWith(".xml"))) {
                return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return "*.xml";
        }
    });
    int ret = chooser.showOpenDialog(this);
    if (ret == JFileChooser.APPROVE_OPTION) {
        try {
            File f = chooser.getSelectedFile();
            String file = f.getCanonicalPath();
            if (!file.endsWith(".xml")) {
                file += ".xml";
            }

            File target = new File(file);

            String extension = JOptionPane.showInputDialog(this, "Welche Kennzeichnung sollen importierte Angriffspläne und Tags erhalten?\n" +
                    "Lass das Eingabefeld leer oder drücke 'Abbrechen', um sie unverändert zu importieren.", "Kennzeichnung festlegen", JOptionPane.INFORMATION_MESSAGE);
            if (extension != null && extension.length() > 0) {
                logger.debug("Using import extension '" + extension + "'");
            } else {
                logger.debug("Using no import extension");
                extension = null;
            }

            if (target.exists()) {
                //do import
                boolean attackImported = AttackManager.getSingleton().importAttacks(target, extension);
                boolean markersImported = MarkerManager.getSingleton().importMarkers(target, extension);
                boolean tagImported = TagManager.getSingleton().importTags(target, extension);
                boolean troopsImported = TroopsManager.getSingleton().importTroops(target);
                boolean formsImported = FormManager.getSingleton().importForms(target, extension);
                boolean notesImported = NoteManager.getSingleton().importNotes(target);

                String message = "Import beendet.\n";
                if (!attackImported) {
                    message += "  * Fehler beim Import der Angriffe\n";
                }

                if (!markersImported) {
                    message += "  * Fehler beim Import der Markierungen\n";
                }

                if (!tagImported) {
                    message += "  * Fehler beim Import der Tags\n";
                }

                if (!troopsImported) {
                    message += "  * Fehler beim Import der Truppen\n";
                }

                if (!formsImported) {
                    message += "  * Fehler beim Import der Formen\n";
                }
                if (!notesImported) {
                    message += "  * Fehler beim Import der Notizen\n";
                }
                JOptionPaneHelper.showInformationBox(this, message, "Import");
            }

            GlobalOptions.addProperty("screen.dir", target.getParent());
        } catch (Exception e) {
            logger.error("Failed to import data", e);
            JOptionPaneHelper.showErrorBox(this, "Import fehlgeschlagen.", "Import");
        }

    }
}//GEN-LAST:event_fireShowImportDialogEvent

private void fireExportEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireExportEvent
    if (evt.getSource() == jExportButton) {
        //do export
        logger.debug("Building export data");

        List<String> plansToExport = new LinkedList<String>();
        for (int i = 0; i < jAttackExportTable.getRowCount(); i++) {
            String plan = (String) jAttackExportTable.getValueAt(i, 0);
            Boolean export = (Boolean) jAttackExportTable.getValueAt(i, 1);
            if (export) {
                logger.debug("Selecting attack plan '" + plan + "' to export list");
                plansToExport.add(plan);
            }

        }

        List<String> setsToExport = new LinkedList<String>();
        for (int i = 0; i < jMarkerSetExportTable.getRowCount(); i++) {
            String set = (String) jMarkerSetExportTable.getValueAt(i, 0);
            Boolean export = (Boolean) jMarkerSetExportTable.getValueAt(i, 1);
            if (export) {
                logger.debug("Selecting marker set '" + set + "' to export list");
                setsToExport.add(set);
            }

        }

        boolean needExport = false;
        needExport = !plansToExport.isEmpty();
        needExport |= !setsToExport.isEmpty();
        needExport |= jExportTags.isSelected();
        needExport |= jExportTroops.isSelected();
        needExport |= jExportForms.isSelected();
        needExport |= jExportNotes.isSelected();
        if (!needExport) {
            JOptionPaneHelper.showWarningBox(jExportDialog, "Keine Daten für den Export gewählt", "Export");
            return;

        }
        String dir = GlobalOptions.getProperty("screen.dir");
        if (dir == null) {
            dir = ".";
        }

        JFileChooser chooser = null;
        try {
            chooser = new JFileChooser(dir);
        } catch (Exception e) {
            JOptionPaneHelper.showErrorBox(this, "Konnte Dateiauswahldialog nicht öffnen.\nMöglicherweise verwendest du Windows Vista. Ist dies der Fall, beende DS Workbench, klicke mit der rechten Maustaste auf DSWorkbench.exe,\n" +
                    "wähle 'Eigenschaften' und deaktiviere dort unter 'Kompatibilität' den Windows XP Kompatibilitätsmodus.", "Fehler");
            return;
        }
        chooser.setDialogTitle("Datei auswählen");
        chooser.setSelectedFile(new File("export.xml"));
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

            @Override
            public boolean accept(File f) {
                if ((f != null) && (f.isDirectory() || f.getName().endsWith(".xml"))) {
                    return true;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "*.xml";
            }
        });
        int ret = chooser.showSaveDialog(jExportDialog);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                File f = chooser.getSelectedFile();
                String file = f.getCanonicalPath();
                if (!file.endsWith(".xml")) {
                    file += ".xml";
                }

                File target = new File(file);
                if (target.exists()) {
                    if (JOptionPaneHelper.showQuestionConfirmBox(jExportDialog, "Bestehende Datei überschreiben?", "Export", "Nein", "Ja") == JOptionPane.NO_OPTION) {
                        return;
                    }

                }

                String exportString = "<export>\n";
                if (!plansToExport.isEmpty()) {
                    exportString += AttackManager.getSingleton().getExportData(plansToExport);
                }
                if (!setsToExport.isEmpty()) {
                    exportString += MarkerManager.getSingleton().getExportData(setsToExport.toArray(new String[]{}));
                }

                if (jExportTags.isSelected()) {
                    exportString += TagManager.getSingleton().getExportData();
                }

                if (jExportTroops.isSelected()) {
                    exportString += TroopsManager.getSingleton().getExportData();
                }

                if (jExportForms.isSelected()) {
                    exportString += FormManager.getSingleton().getExportData();
                }

                if (jExportNotes.isSelected()) {
                    exportString += NoteManager.getSingleton().getExportData();
                }

                exportString += "</export>";
                logger.debug("Writing data to disk");
                FileWriter w = new FileWriter(target);
                w.write(exportString);
                logger.debug("Finalizing writer");
                w.flush();
                w.close();
                logger.debug("Export finished successfully");
                JOptionPaneHelper.showInformationBox(jExportDialog, "Export erfolgreich beendet.", "Export");
            } catch (Exception e) {
                logger.error("Failed to export data", e);
                JOptionPaneHelper.showErrorBox(this, "Export fehlgeschlagen.", "Export");
            }

        } else {
            //cancel pressed
            return;
        }

    }
    jExportDialog.setVisible(false);
}//GEN-LAST:event_fireExportEvent

private void fireOpenExportDialogEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireOpenExportDialogEvent
    //build attack plan table
    Enumeration<String> plans = AttackManager.getSingleton().getPlans();
    jAttackExportTable.invalidate();
    for (int i = 0; i < jAttackExportTable.getColumnCount(); i++) {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };
        jAttackExportTable.getColumn(jAttackExportTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
    }

    DefaultTableModel model = (DefaultTableModel) jAttackExportTable.getModel();
    int rows = model.getRowCount();
    for (int i = 0; i < rows; i++) {
        model.removeRow(0);
    }

    while (plans.hasMoreElements()) {
        String next = plans.nextElement();
        model.addRow(new Object[]{next, Boolean.FALSE});
    }

    jAttackExportTable.revalidate();
    jAttackExportTable.repaint();
    //build marker set table
    String[] sets = MarkerManager.getSingleton().getMarkerSets();
    jMarkerSetExportTable.invalidate();
    for (int i = 0; i < jMarkerSetExportTable.getColumnCount(); i++) {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };
        jMarkerSetExportTable.getColumn(jMarkerSetExportTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
    }

    model = (DefaultTableModel) jMarkerSetExportTable.getModel();
    rows = model.getRowCount();
    for (int i = 0; i < rows; i++) {
        model.removeRow(0);
    }

    for (String set : sets) {
        model.addRow(new Object[]{set, Boolean.FALSE});
    }

    jMarkerSetExportTable.revalidate();
    jMarkerSetExportTable.repaint();//.updateUI();
    jExportDialog.setVisible(true);
}//GEN-LAST:event_fireOpenExportDialogEvent

private void fireChangeUVModeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireChangeUVModeEvent
    if (jUVModeButton.isSelected()) {
        //set UV mode
        jUVModeButton.setIcon(uvModeOn);
        try {
            int id = Integer.parseInt(jUVIDField.getText());
            if (id < 0) {
                throw new Exception();
            }

            GlobalOptions.setUVMode(id);
        } catch (Exception e) {
            jUVModeButton.setSelected(false);
            GlobalOptions.unsetUVMode();
        }

    } else {
        jUVModeButton.setIcon(uvModeOff);
        GlobalOptions.unsetUVMode();
    }
}//GEN-LAST:event_fireChangeUVModeEvent

private void fireShowChurchFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowChurchFrameEvent
    if (jShowChurchFrame.isEnabled()) {
        DSWorkbenchChurchFrame.getSingleton().setVisible(!DSWorkbenchChurchFrame.getSingleton().isVisible());
        jShowChurchFrame.setSelected(DSWorkbenchChurchFrame.getSingleton().isVisible());
    }
}//GEN-LAST:event_fireShowChurchFrameEvent

private void fireChangeROIEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeROIEvent
    if (evt.getSource() == jAddROIButton) {
        try {
            int x = Integer.parseInt(jCenterX.getText());
            int y = Integer.parseInt(jCenterY.getText());
            jROIRegion.setText("(" + x + "|" + y + ")");
            jROIPosition.setSelectedIndex(jROIPosition.getItemCount() - 1);
        } catch (Exception e) {
            logger.error("Failed to initialize ROI dialog", e);
            return;

        }

        jAddROIDialog.setLocationRelativeTo(this);
        jAddROIDialog.setVisible(true);
    } else {
        try {
            String item = (String) jROIBox.getSelectedItem();
            logger.debug("Removing ROI '" + item + "'");
            ROIManager.getSingleton().removeROI(item);
            jROIBox.removeItem(item);
        } catch (Exception e) {
        }
    }
}//GEN-LAST:event_fireChangeROIEvent

private void fireAddROIDoneEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddROIDoneEvent

    if (evt.getSource() == jAddNewROIButton) {
        try {
            int x = Integer.parseInt(jCenterX.getText());
            int y = Integer.parseInt(jCenterY.getText());
            String value = jROITextField.getText() + " (" + x + "|" + y + ")";
            int pos = Integer.MAX_VALUE;
            try {
                pos = Integer.parseInt((String) jROIPosition.getSelectedItem());
                pos -=
                        1;
            } catch (Exception ee) {
                //end pos selected
                pos = Integer.MAX_VALUE;
            }

            if (ROIManager.getSingleton().containsROI(value)) {
                JOptionPaneHelper.showWarningBox(this, "ROI '" + value + "' existiert bereits.", "ROI vorhanden");
                return;

            }

            ROIManager.getSingleton().addROI(pos, value);
            jROIBox.setModel(new DefaultComboBoxModel(ROIManager.getSingleton().getROIs()));
        } catch (Exception e) {
            logger.error("Failed to add ROI", e);
        }

    }
    jAddROIDialog.setVisible(false);

}//GEN-LAST:event_fireAddROIDoneEvent

private void fireROISelectedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireROISelectedEvent
    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        try {
            String item = (String) jROIBox.getSelectedItem();
            item =
                    item.substring(item.lastIndexOf("(") + 1, item.lastIndexOf(")"));
            String[] pos = item.trim().split("\\|");
            jCenterX.setText(pos[0]);
            jCenterY.setText(pos[1]);
            fireRefreshMapEvent(null);
        } catch (Exception e) {
        }
    }
}//GEN-LAST:event_fireROISelectedEvent

private void fireRadarSpinnerChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireRadarSpinnerChangedEvent
    Calendar c = Calendar.getInstance();
    c.setTime((Date) jRadarSpinner.getValue());
    int r = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    GlobalOptions.addProperty("radar.size", Integer.toString(r));
}//GEN-LAST:event_fireRadarSpinnerChangedEvent

private void fireDSWorkbenchClosingEvent(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_fireDSWorkbenchClosingEvent
    logger.debug("Shutting down DSWorkbench");
    GlobalOptions.addProperty("zoom.factor", Double.toString(getZoomFactor()));
    GlobalOptions.addProperty("last.x", getCurrentPosition()[0]);
    GlobalOptions.addProperty("last.y", getCurrentPosition()[1]);
    System.exit(0);
}//GEN-LAST:event_fireDSWorkbenchClosingEvent

private void firePanelMin(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_firePanelMin
    jPanel2.setSize(jPanel2.getWidth(), 10);
}//GEN-LAST:event_firePanelMin

private void fireShowConquersFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowConquersFrameEvent
    if (jShowConquersFrame.isEnabled()) {
        DSWorkbenchConquersFrame.getSingleton().setVisible(!DSWorkbenchConquersFrame.getSingleton().isVisible());
        jShowConquersFrame.setSelected(DSWorkbenchConquersFrame.getSingleton().isVisible());
    }
}//GEN-LAST:event_fireShowConquersFrameEvent

private void fireShowNotepadEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowNotepadEvent
    if (jShowNotepadFrame.isEnabled()) {
        DSWorkbenchNotepad.getSingleton().setVisible(!DSWorkbenchNotepad.getSingleton().isVisible());
        jShowNotepadFrame.setSelected(DSWorkbenchNotepad.getSingleton().isVisible());
    }
}//GEN-LAST:event_fireShowNotepadEvent

private void fireChangeDrawOrderEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeDrawOrderEvent
    try {
        int idx = jLayerList.getSelectedIndex();
        DefaultListModel model = ((DefaultListModel) jLayerList.getModel());
        if (evt.getSource() == jLayerUpButton) {
            if (idx == 0) {
                //already on first position
                return;
            }
            jLayerList.invalidate();
            String elem = (String) model.remove(idx);
            jLayerList.revalidate();
            idx -= 1;
            jLayerList.invalidate();
            model.add(idx, elem);
            jLayerList.setSelectedIndex(idx);
            try {
                //scroll element to be visible
                Rectangle g = jLayerList.getCellBounds(idx, idx);
                jLayerList.scrollRectToVisible(g);
            } catch (Exception e) {
            }
            jLayerList.revalidate();
        } else {
            if (idx == model.getSize() - 1) {
                //already on last position
                return;
            }

            jLayerList.invalidate();
            String elem = (String) model.remove(idx);
            idx += 1;
            model.add(idx, elem);
            jLayerList.setSelectedIndex(idx);
            try {
                //scroll element to be visible
                Rectangle g = jLayerList.getCellBounds(idx, idx);
                jLayerList.scrollRectToVisible(g);
            } catch (Exception e) {
            }
            jLayerList.revalidate();
        }
        jLayerList.updateUI();
        propagateLayerOrder();
    } catch (Exception outer) {
    }
}//GEN-LAST:event_fireChangeDrawOrderEvent

private void fireShowTagFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowTagFrameEvent
    if (jShowTagFrame.isEnabled()) {
        DSWorkbenchTagFrame.getSingleton().setVisible(!DSWorkbenchTagFrame.getSingleton().isVisible());
        jShowTagFrame.setSelected(DSWorkbenchTagFrame.getSingleton().isVisible());
    }
}//GEN-LAST:event_fireShowTagFrameEvent

private void fireGraphicPackChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireGraphicPackChangedEvent
    GlobalOptions.addProperty("default.skin", (String) jGraphicPacks.getSelectedItem());
    try {
        GlobalOptions.loadSkin();
    } catch (Exception e) {
        logger.error("Failed to load skin '" + jGraphicPacks.getSelectedItem() + "'", e);
        JOptionPaneHelper.showErrorBox(this, "Fehler beim laden des Grafikpaketes.", "Fehler");
        //load default
        GlobalOptions.addProperty("default.skin", "default");
        try {
            GlobalOptions.loadSkin();
        } catch (Exception ie) {
            logger.error("Failed to load default skin", ie);
        }
    }
    if (isInitialized()) {
        MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
    }
}//GEN-LAST:event_fireGraphicPackChangedEvent

private void fireShowStatsFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowStatsFrameEvent
    if (jShowStatsFrame.isEnabled()) {
        DSWorkbenchStatsFrame.getSingleton().setVisible(!DSWorkbenchStatsFrame.getSingleton().isVisible());
        jShowStatsFrame.setSelected(DSWorkbenchStatsFrame.getSingleton().isVisible());
    }
}//GEN-LAST:event_fireShowStatsFrameEvent

private void fireShowReportFrameEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowReportFrameEvent

    DSWorkbenchReportFrame.getSingleton().setVisible(!DSWorkbenchReportFrame.getSingleton().isVisible());
    jShowReportFrame.setSelected(DSWorkbenchReportFrame.getSingleton().isVisible());
}//GEN-LAST:event_fireShowReportFrameEvent

private void fireHighlightTribeVillagesChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireHighlightTribeVillagesChangedEvent
    GlobalOptions.addProperty("highlight.tribes.villages", Boolean.toString(jHighlightTribeVillages.isSelected()));
}//GEN-LAST:event_fireHighlightTribeVillagesChangedEvent

private void switchPanel(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_switchPanel
    //  switchPanel();
}//GEN-LAST:event_switchPanel

private void fireShowRulerChangedEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireShowRulerChangedEvent
    GlobalOptions.addProperty("show.ruler", Boolean.toString(jShowRuler.isSelected()));
}//GEN-LAST:event_fireShowRulerChangedEvent

    private void propagateLayerOrder() {
        DefaultListModel model = ((DefaultListModel) jLayerList.getModel());

        List<Integer> layerOrder = new LinkedList<Integer>();
        for (int i = 0; i < model.size(); i++) {
            String value = (String) model.get(i);
            layerOrder.add(Constants.LAYERS.get(value));
        }
        MapPanel.getSingleton().getMapRenderer().setDrawOrder(layerOrder);
    }

    public String getLayerOrder() {
        DefaultListModel model = ((DefaultListModel) jLayerList.getModel());
        String res = "";
        for (int i = 0; i < model.size(); i++) {
            res += (String) model.get(i) + ";";
        }
        return res;
    }

    private void centerROI(int pId) {
        try {
            String item = (String) jROIBox.getItemAt(pId);
            item = item.substring(item.lastIndexOf("(") + 1, item.lastIndexOf(")"));
            String[] pos = item.trim().split("\\|");
            jCenterX.setText(pos[0]);
            jCenterY.setText(pos[1]);
            fireRefreshMapEvent(null);
        } catch (Exception e) {
        }
    }

    /**Check if zoom factor is valid and correct if needed*/
    private void checkZoomRange() {
        if (dZoomFactor <= 0.1) {
            dZoomFactor = 0.1;
            jZoomOutButton.setEnabled(false);
        } else if (dZoomFactor >= 2.5) {
            dZoomFactor = 2.5;
            jZoomInButton.setEnabled(false);
        } else {
            jZoomInButton.setEnabled(true);
            jZoomOutButton.setEnabled(true);
        }
    }

    /**Scroll the map*/
    public void scroll(double pXDir, double pYDir) {
        dCenterX = dCenterX + pXDir;
        dCenterY = dCenterY + pYDir;
        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] hier = DSCalculator.xyToHierarchical((int) dCenterX, (int) dCenterY);
            if (hier != null) {
                jCenterX.setText(Integer.toString(hier[0]));
                jCenterY.setText(Integer.toString(hier[1]));
            }
        } else {
            jCenterX.setText(Integer.toString((int) Math.floor(dCenterX)));
            jCenterY.setText(Integer.toString((int) Math.floor(dCenterY)));
        }

        double w = (double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getBasicFieldWidth() * dZoomFactor;
        double h = (double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getBasicFieldHeight() * dZoomFactor;
        MinimapPanel.getSingleton().setSelection((int) Math.floor(dCenterX), (int) Math.floor(dCenterY), (int) Math.rint(w), (int) Math.rint(h));
        MapPanel.getSingleton().updateMapPosition(dCenterX, dCenterY);
    }

    /**Center a village*/
    public void centerVillage(Village pVillage) {
        if (pVillage == null) {
            return;
        }

        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] hier = DSCalculator.xyToHierarchical((int) pVillage.getX(), (int) pVillage.getY());
            if (hier != null) {
                jCenterX.setText(Integer.toString(hier[0]));
                jCenterY.setText(Integer.toString(hier[1]));
            }

        } else {
            jCenterX.setText(Integer.toString(pVillage.getX()));
            jCenterY.setText(Integer.toString(pVillage.getY()));
        }

        fireRefreshMapEvent(null);
    }

    public void centerPosition(int xPos, int yPos) {
        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] hier = DSCalculator.xyToHierarchical((int) xPos, (int) yPos);
            if (hier != null) {
                jCenterX.setText(Integer.toString(hier[0]));
                jCenterY.setText(Integer.toString(hier[1]));
            }

        } else {
            jCenterX.setText(Integer.toString(xPos));
            jCenterY.setText(Integer.toString(yPos));
        }

        fireRefreshMapEvent(null);
    }

    /**Get active user village*/
    public Village getCurrentUserVillage() {
        try {
            if (jCurrentPlayerVillages.getSelectedIndex() < 0) {
                if (jCurrentPlayerVillages.getItemCount() > 0) {
                    jCurrentPlayerVillages.setSelectedIndex(0);
                } else {
                    //don't try to get village, list is still empty
                    return null;
                }

            }
            return (Village) jCurrentPlayerVillages.getSelectedItem();
        } catch (ClassCastException cce) {
            //if no player was selected yet
            return null;
        } catch (Exception e) {
            logger.warn("Could not get current user village.", e);
            return null;
        }

    }

    public void setCurrentUserVillage(Village pVillage) {
        jCurrentPlayerVillages.setSelectedItem(pVillage);
    }

    public Tribe getCurrentUser() {
        try {
            Village v = (Village) jCurrentPlayerVillages.getItemAt(0);
            return v.getTribe();
        } catch (Exception e) {
        }
        return null;
    }

// <editor-fold defaultstate="collapsed" desc=" Listener EventHandlers ">
    @Override
    public void fireToolChangedEvent(int pTool) {
        jCurrentToolLabel.setIcon(ImageManager.getCursorImage(pTool));
    }

    @Override
    public void fireScrollEvent(double pX, double pY) {
        scroll(pX, pY);
    }

    @Override
    public void fireVisibilityChangedEvent(JFrame pSource, boolean v) {
        if (pSource == DSWorkbenchAttackFrame.getSingleton()) {
            jShowAttackFrame.setSelected(DSWorkbenchAttackFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchMarkerFrame.getSingleton()) {
            jShowMarkerFrame.setSelected(DSWorkbenchMarkerFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchTroopsFrame.getSingleton()) {
            jShowTroopsFrame.setSelected(DSWorkbenchTroopsFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchRankFrame.getSingleton()) {
            jShowRankFrame.setSelected(DSWorkbenchRankFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchFormFrame.getSingleton()) {
            jShowFormsFrame.setSelected(DSWorkbenchFormFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchChurchFrame.getSingleton()) {
            jShowChurchFrame.setSelected(DSWorkbenchChurchFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchConquersFrame.getSingleton()) {
            jShowConquersFrame.setSelected(DSWorkbenchConquersFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchNotepad.getSingleton()) {
            jShowNotepadFrame.setSelected(DSWorkbenchNotepad.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchTagFrame.getSingleton()) {
            jShowTagFrame.setSelected(DSWorkbenchTagFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchStatsFrame.getSingleton()) {
            jShowStatsFrame.setSelected(DSWorkbenchStatsFrame.getSingleton().isVisible());
        } else if (pSource == DSWorkbenchReportFrame.getSingleton()) {
            jShowReportFrame.setSelected(DSWorkbenchReportFrame.getSingleton().isVisible());
        }

    }

    public void fireGroupParserEvent(Hashtable<String, List<Village>> pParserResult) {
        String[] groups = pParserResult.keySet().toArray(new String[]{});
        String message = "DS Workbench hat in deiner Zwischenablage Informationen zu den folgenden Gruppen gefunden:\n";
        for (String s : groups) {
            int size = pParserResult.get(s).size();
            if (size == 0) {
                message += "* keine Dörfer)\n";
            } else if (size == 1) {
                message += "* " + s + " (" + pParserResult.get(s).size() + " Dorf)\n";
            } else {
                message += "* " + s + " (" + pParserResult.get(s).size() + " Dörfer)\n";
            }

        }

        message += "Willst du diese Informationen in DS Workbench übernehmen oder sie verwerfen und aus der Zwischenablage entfernen?";
        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, "Gruppeninformationen gefunden", "Verwerfen", "Übernehmen") == JOptionPane.YES_OPTION) {
            //remove all tags
            for (String group : groups) {
                List<Village> villagesForGroup = pParserResult.get(group);
                if (villagesForGroup != null) {
                    for (Village v : villagesForGroup) {
                        TagManager.getSingleton().removeTags(v);
                    }

                }
            }

            for (String group : groups) {
                //add new groups
                TagManager.getSingleton().addTag(group);
                //get (added) group
                Tag t = TagManager.getSingleton().getTagByName(group);
                //add villages to group
                List<Village> villagesForGroup = pParserResult.get(group);
                if (villagesForGroup != null) {
                    //set new tags
                    for (Village v : villagesForGroup) {
                        t.tagVillage(v.getId());
                    }

                }
            }
        }
    }

    @Override
    public void fireMapShotDoneEvent() {
        Component parent = fullscreenFrame;
        if (parent == null) {
            parent = this;
        }

        if (!putOnline) {
            JOptionPaneHelper.showInformationBox(parent, "Kartengrafik erfolgreich gespeichert.", "Information");
        } else {
            String result = ScreenUploadInterface.upload("tmp.png");
            if (result != null) {
                if (result.indexOf("view.php") > 0) {
                    try {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(result), null);
                        JOptionPaneHelper.showInformationBox(parent, "Kartengrafik erfolgreich Online gestellt.\n" +
                                "Der Zugriffslink (" + result + ")\n" +
                                "wurde in die Zwischenablage kopiert.", "Information");
                        BrowserCommandSender.openPage(result);
                    } catch (Exception e) {
                        JOptionPaneHelper.showWarningBox(parent, "Fehler beim Kopieren des Links in die Zwischenablage.\n" +
                                "Der Zugriffslink lautet: " + result, "Link nicht kopiert werden");
                    }

                } else {
                    JOptionPaneHelper.showErrorBox(parent, "Kartengrafik konnte nicht Online gestellt werden.\n" +
                            "Fehler: " + result, "Fehler");
                }

            }
        }
        putOnline = false;
    }

    @Override
    public void fireMapShotFailedEvent() {
        Component parent = fullscreenFrame;
        if (parent == null) {
            parent = this;
        }

        JOptionPaneHelper.showErrorBox(parent, "Fehler beim Speichern der Kartengrafik.", "Fehler");
    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="Generated Variables">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jAboutItem;
    private javax.swing.JButton jAddNewROIButton;
    private javax.swing.JButton jAddROIButton;
    private javax.swing.JDialog jAddROIDialog;
    private javax.swing.JTable jAttackExportTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jCancelExportButton;
    private javax.swing.JButton jCenterCoordinateIngame;
    private javax.swing.JButton jCenterIngameButton;
    private javax.swing.JTextField jCenterX;
    private javax.swing.JTextField jCenterY;
    private javax.swing.JMenuItem jClockItem;
    private javax.swing.JLabel jCurrentPlayer;
    private javax.swing.JComboBox jCurrentPlayerVillages;
    private javax.swing.JLabel jCurrentToolLabel;
    private javax.swing.JPanel jCustomPanel;
    private javax.swing.JMenuItem jDistanceItem;
    private javax.swing.JMenuItem jDoItYourselfAttackPlanerItem;
    private javax.swing.JButton jExportButton;
    private javax.swing.JDialog jExportDialog;
    private javax.swing.JCheckBox jExportForms;
    private javax.swing.JCheckBox jExportNotes;
    private javax.swing.JCheckBox jExportTags;
    private javax.swing.JCheckBox jExportTroops;
    private javax.swing.JComboBox jFileTypeChooser;
    private javax.swing.JComboBox jGraphicPacks;
    private javax.swing.JMenuItem jHelpItem;
    private javax.swing.JCheckBox jHighlightTribeVillages;
    private com.l2fprod.common.swing.JTaskPaneGroup jInformationGroup;
    private javax.swing.JPanel jInformationPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLayerDownButton;
    private javax.swing.JList jLayerList;
    private javax.swing.JButton jLayerUpButton;
    private com.l2fprod.common.swing.JTaskPaneGroup jMapGroup;
    private javax.swing.JDialog jMapShotDialog;
    private javax.swing.JTable jMarkerSetExportTable;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jMinimapPanel;
    private javax.swing.JButton jMoveE;
    private javax.swing.JButton jMoveE1;
    private javax.swing.JButton jMoveN;
    private javax.swing.JButton jMoveNE;
    private javax.swing.JButton jMoveNW;
    private javax.swing.JButton jMoveS;
    private javax.swing.JButton jMoveSE;
    private javax.swing.JButton jMoveSW;
    private javax.swing.JButton jMoveW;
    private com.l2fprod.common.swing.JTaskPaneGroup jNavigationGroup;
    private javax.swing.JPanel jNavigationPanel;
    private javax.swing.JLabel jOnlineLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox jROIBox;
    private com.l2fprod.common.swing.JTaskPaneGroup jROIGroup;
    private javax.swing.JComboBox jROIPosition;
    private javax.swing.JTextField jROIRegion;
    private javax.swing.JTextField jROITextField;
    private javax.swing.JSpinner jRadarSpinner;
    private javax.swing.JMenuItem jReTimeToolEvent;
    private javax.swing.JButton jRefreshButton;
    private javax.swing.JButton jRemoveROIButton;
    private javax.swing.JMenuItem jSOSAnalyzerItem;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JMenuItem jSearchItem;
    private javax.swing.JMenuItem jSelectionOverviewItem;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBoxMenuItem jShowAttackFrame;
    private javax.swing.JCheckBoxMenuItem jShowChurchFrame;
    private javax.swing.JCheckBoxMenuItem jShowConquersFrame;
    private javax.swing.JCheckBoxMenuItem jShowFormsFrame;
    private javax.swing.JCheckBox jShowMapPopup;
    private javax.swing.JCheckBoxMenuItem jShowMarkerFrame;
    private javax.swing.JCheckBoxMenuItem jShowNotepadFrame;
    private javax.swing.JCheckBoxMenuItem jShowRankFrame;
    private javax.swing.JCheckBoxMenuItem jShowReportFrame;
    private javax.swing.JCheckBox jShowRuler;
    private javax.swing.JCheckBoxMenuItem jShowStatsFrame;
    private javax.swing.JCheckBoxMenuItem jShowTagFrame;
    private javax.swing.JCheckBoxMenuItem jShowTroopsFrame;
    private javax.swing.JMenuItem jStartAStarItem;
    private javax.swing.JMenuItem jSupportCoordinator;
    private javax.swing.JTable jTable1;
    private com.l2fprod.common.swing.JTaskPane jTaskPane1;
    private javax.swing.JMenuItem jTribeTribeAttackItem;
    private com.l2fprod.common.swing.JTaskPaneGroup jUVGroup;
    private javax.swing.JTextField jUVIDField;
    private javax.swing.JToggleButton jUVModeButton;
    private javax.swing.JMenuItem jUnitOverviewItem;
    private javax.swing.JButton jZoomInButton;
    private javax.swing.JButton jZoomOutButton;
    // End of variables declaration//GEN-END:variables
//</editor-fold>
}
