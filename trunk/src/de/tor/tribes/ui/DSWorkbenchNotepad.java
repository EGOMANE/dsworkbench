/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DSWorkbenchNotepad.java
 *
 * Created on 28.06.2009, 12:02:37
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.types.Note;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.renderer.MapRenderer;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.ServerSettings;
import de.tor.tribes.util.note.NoteManager;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.html.HTMLDocument;
import org.apache.log4j.Logger;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.PluginManager;
import de.tor.tribes.util.VillageListFormatter;
import de.tor.tribes.util.parser.VillageParser;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import javax.swing.SwingConstants;

/**
 * @author Charon
 */
public class DSWorkbenchNotepad extends AbstractDSWorkbenchFrame {

    private static Logger logger = Logger.getLogger("Notepad");
    private static DSWorkbenchNotepad SINGLETON = null;
    /* private Action boldAction = new StyledEditorKit.BoldAction();
    private Action underlineAction = new StyledEditorKit.UnderlineAction();
    private Action italicAction = new StyledEditorKit.ItalicAction();*/
    private Note currentNote = null;

    public static synchronized DSWorkbenchNotepad getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchNotepad();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchNotepad */
    DSWorkbenchNotepad() {
        initComponents();
        /*  jBoldButton.setAction(boldAction);
        jBoldButton.setText("");
        jBoldButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_bold.png")));
        jItalicButton.setAction(italicAction);
        jItalicButton.setText("");
        jItalicButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_italics.png")));
        jUnderlineButton.setAction(underlineAction);
        jUnderlineButton.setText("");
        jUnderlineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_underlined.png")));
        jSize10Button.setAction(new StyledEditorKit.FontSizeAction("10", 10));
        jSize12Button.setAction(new StyledEditorKit.FontSizeAction("12", 12));
        jSize14Button.setAction(new StyledEditorKit.FontSizeAction("14", 14));
        jSize18Button.setAction(new StyledEditorKit.FontSizeAction("18", 18));
        jSize20Button.setAction(new StyledEditorKit.FontSizeAction("20", 20));
        jSize28Button.setAction(new StyledEditorKit.FontSizeAction("28", 28));
        jRedColorButton.setAction(new StyledEditorKit.ForegroundAction("Red", Color.RED));
        jRedColorButton.setText("");
        jGreenColorButton.setAction(new StyledEditorKit.ForegroundAction("Green", Color.GREEN));
        jGreenColorButton.setText("");
        jBlueColorButton.setAction(new StyledEditorKit.ForegroundAction("Blue", Color.BLUE));
        jBlueColorButton.setText("");
        jBlackColorButton.setAction(new StyledEditorKit.ForegroundAction("Black", Color.BLACK));
        jBlackColorButton.setText("");
        jCyanColorButton.setAction(new StyledEditorKit.ForegroundAction("Cyan", Color.CYAN));
        jCyanColorButton.setText("");
        jOrangeColorButton.setAction(new StyledEditorKit.ForegroundAction("Orange", new Color(255, 102, 0)));
        jOrangeColorButton.setText("");
        jPinkColorButton.setAction(new StyledEditorKit.ForegroundAction("Pink", new Color(255, 0, 204)));
        jPinkColorButton.setText("");
        jVioletColorButton.setAction(new StyledEditorKit.ForegroundAction("Violet", new Color(152, 153, 255)));
        jVioletColorButton.setText("");
        jLeftButton.setAction(new StyledEditorKit.AlignmentAction("<", StyleConstants.ALIGN_LEFT));
        jLeftButton.setText("");
        jLeftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_left.png")));
        jCenterButton.setAction(new StyledEditorKit.AlignmentAction("|", StyleConstants.ALIGN_CENTER));
        jCenterButton.setText("");
        jCenterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_center.png")));
        jRightButton.setAction(new StyledEditorKit.AlignmentAction(">", StyleConstants.ALIGN_RIGHT));
        jRightButton.setText("");
        jRightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_right.png")));
         */

        try {
            jAlwaysOnTopBox.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("notepad.frame.alwaysOnTop")));
            setAlwaysOnTop(jAlwaysOnTopBox.isSelected());
        } catch (Exception e) {
            //setting not available
        }

        //setup map marker box
        for (int i = 0; i <= ImageManager.ID_NOTE_ICON_13; i++) {
            jIconBox.addItem(i);
        }

        ListCellRenderer r = new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                try {
                    JLabel label = ((JLabel) c);
                    label.setText("");
                    BufferedImage symbol = ImageManager.getNoteIcon((Integer) value);
                    label.setIcon(new ImageIcon(symbol.getScaledInstance(18, 18, BufferedImage.SCALE_FAST)));
                } catch (Exception e) {
                }
                return c;
            }
        };
        jIconBox.setRenderer(r);

        //setup note symbol box
        for (int i = -1; i <= ImageManager.NOTE_SYMBOL_NO_EYE; i++) {
            jNoteSymbolBox.addItem(i);
        }

        jNoteSymbolBox.setRenderer(new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                try {
                    JLabel label = ((JLabel) c);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setText("");
                    int val = (Integer) value;
                    if (val != -1) {
                        BufferedImage symbol = ImageManager.getNoteSymbol(val);
                        label.setIcon(new ImageIcon(symbol.getScaledInstance(18, 18, BufferedImage.SCALE_FAST)));
                    } else {
                        //no symbol
                        label.setIcon(null);
                        label.setText("-");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        });


        //<editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.notes_view", GlobalOptions.getHelpBroker().getHelpSet());
        //</editor-fold>

    }

    public void setup() {
        currentNote = NoteManager.getSingleton().getFirstNote();
        showCurrentNote();
    }

    public void setCurrentNote(Note pNote) {
        currentNote = pNote;
        showCurrentNote();
    }

    public void setVillageFieldExternally(Village pVillage) {
        if (pVillage == null) {
            return;
        }
        if (ServerSettings.getSingleton().getCoordType() != 2) {
            int[] coord = DSCalculator.xyToHierarchical(pVillage.getX(), pVillage.getY());
            jAddVillageField.setText(coord[0] + ":" + coord[1] + ":" + coord[2]);
        } else {
            jAddVillageField.setText("(" + pVillage.getX() + "|" + pVillage.getY() + ")");
        }
    }

    public void setSearchTermByVillageExternally(Village pVillage) {
        if (pVillage == null) {
            return;
        }
        jSearchField.setText(pVillage.toString());
    }

    public void addNoteForVillage(Village pVillage) {
        Note n = new Note();
        n.addVillage(pVillage);
        n.setNoteText("(kein Text)");
        n.setMapMarker(0);
        NoteManager.getSingleton().addNote(n);
        currentNote = n;
        showCurrentNote();
    }

    public void addNoteForVillages(List<Village> pVillages) {
        Note n = new Note();
        for (Village v : pVillages) {
            n.addVillage(v);
        }
        n.setNoteText("(kein Text)");
        n.setMapMarker(0);
        NoteManager.getSingleton().addNote(n);
        currentNote = n;
        showCurrentNote();
    }

    public boolean addVillageToCurrentNote(Village pVillage) {
        if (currentNote != null) {
            currentNote.addVillage(pVillage);
            showCurrentNote();
            return true;
        }
        return false;
    }

    public boolean addVillagesToCurrentNote(List<Village> pVillages) {
        if (currentNote != null) {
            for (Village v : pVillages) {
                currentNote.addVillage(v);
            }
            showCurrentNote();
            return true;
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFormatPanel = new javax.swing.JPanel();
        jBoldButton = new javax.swing.JButton();
        jItalicButton = new javax.swing.JButton();
        jUnderlineButton = new javax.swing.JButton();
        jLeftButton = new javax.swing.JButton();
        jCenterButton = new javax.swing.JButton();
        jRightButton = new javax.swing.JButton();
        jColorPanel = new javax.swing.JPanel();
        jBlackColorButton = new javax.swing.JButton();
        jRedColorButton = new javax.swing.JButton();
        jGreenColorButton = new javax.swing.JButton();
        jBlueColorButton = new javax.swing.JButton();
        jOrangeColorButton = new javax.swing.JButton();
        jVioletColorButton = new javax.swing.JButton();
        jPinkColorButton = new javax.swing.JButton();
        jCyanColorButton = new javax.swing.JButton();
        jSizePanel = new javax.swing.JPanel();
        jSize10Button = new javax.swing.JButton();
        jSize12Button = new javax.swing.JButton();
        jSize14Button = new javax.swing.JButton();
        jSize18Button = new javax.swing.JButton();
        jSize20Button = new javax.swing.JButton();
        jSize28Button = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jRightPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jNotePane = new javax.swing.JTextArea();
        jButton7 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSearchField = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLastModified = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jIconBox = new javax.swing.JComboBox();
        jNoteSymbolBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jLeftPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jVillageList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jAddVillageField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jAlwaysOnTopBox = new javax.swing.JCheckBox();

        jFormatPanel.setMaximumSize(new java.awt.Dimension(121, 48));
        jFormatPanel.setLayout(new java.awt.GridLayout(2, 3, 2, 2));

        jBoldButton.setBackground(new java.awt.Color(239, 235, 223));
        jBoldButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_bold.png"))); // NOI18N
        jBoldButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jBoldButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jBoldButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jBoldButton);

        jItalicButton.setBackground(new java.awt.Color(239, 235, 223));
        jItalicButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_italics.png"))); // NOI18N
        jItalicButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jItalicButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jItalicButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jItalicButton);

        jUnderlineButton.setBackground(new java.awt.Color(239, 235, 223));
        jUnderlineButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_underlined.png"))); // NOI18N
        jUnderlineButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jUnderlineButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jUnderlineButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jUnderlineButton);

        jLeftButton.setBackground(new java.awt.Color(239, 235, 223));
        jLeftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_left.png"))); // NOI18N
        jLeftButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jLeftButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jLeftButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jLeftButton);

        jCenterButton.setBackground(new java.awt.Color(239, 235, 223));
        jCenterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_center.png"))); // NOI18N
        jCenterButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jCenterButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jCenterButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jCenterButton);

        jRightButton.setBackground(new java.awt.Color(239, 235, 223));
        jRightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_align_right.png"))); // NOI18N
        jRightButton.setMaximumSize(new java.awt.Dimension(39, 25));
        jRightButton.setMinimumSize(new java.awt.Dimension(39, 25));
        jRightButton.setPreferredSize(new java.awt.Dimension(39, 25));
        jFormatPanel.add(jRightButton);

        jColorPanel.setLayout(new java.awt.GridLayout(2, 2, 2, 2));

        jBlackColorButton.setBackground(new java.awt.Color(0, 0, 0));
        jBlackColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jBlackColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jBlackColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jBlackColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jBlackColorButton);

        jRedColorButton.setBackground(new java.awt.Color(255, 0, 0));
        jRedColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jRedColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jRedColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jRedColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jRedColorButton);

        jGreenColorButton.setBackground(new java.awt.Color(0, 255, 0));
        jGreenColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jGreenColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jGreenColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jGreenColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jGreenColorButton);

        jBlueColorButton.setBackground(new java.awt.Color(0, 0, 255));
        jBlueColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jBlueColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jBlueColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jBlueColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jBlueColorButton);

        jOrangeColorButton.setBackground(new java.awt.Color(255, 102, 0));
        jOrangeColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jOrangeColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jOrangeColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jOrangeColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jOrangeColorButton);

        jVioletColorButton.setBackground(new java.awt.Color(153, 153, 255));
        jVioletColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jVioletColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jVioletColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jVioletColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jVioletColorButton);

        jPinkColorButton.setBackground(new java.awt.Color(255, 0, 204));
        jPinkColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jPinkColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jPinkColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jPinkColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jPinkColorButton);

        jCyanColorButton.setBackground(new java.awt.Color(51, 255, 255));
        jCyanColorButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jCyanColorButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jCyanColorButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jCyanColorButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jColorPanel.add(jCyanColorButton);

        jSizePanel.setMaximumSize(new java.awt.Dimension(121, 48));
        jSizePanel.setLayout(new java.awt.GridLayout(2, 2, 2, 2));

        jSize10Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize10Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize10Button.setText("10");
        jSize10Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize10Button);

        jSize12Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize12Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize12Button.setText("12");
        jSize12Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize12Button);

        jSize14Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize14Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize14Button.setText("14");
        jSize14Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize14Button);

        jSize18Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize18Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize18Button.setText("18");
        jSize18Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize18Button);

        jSize20Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize20Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize20Button.setText("20");
        jSize20Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize20Button);

        jSize28Button.setBackground(new java.awt.Color(239, 235, 223));
        jSize28Button.setFont(new java.awt.Font("SansSerif", 0, 11));
        jSize28Button.setText("28");
        jSize28Button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        jSizePanel.add(jSize28Button);

        setTitle("Notizen");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(10);
        jSplitPane1.setToolTipText("");
        jSplitPane1.setOpaque(false);

        jRightPanel.setOpaque(false);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jNotePane.setBackground(new java.awt.Color(255, 255, 204));
        jNotePane.setColumns(20);
        jNotePane.setLineWrap(true);
        jNotePane.setRows(5);
        jNotePane.setWrapStyleWord(true);
        jNotePane.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fireNoteTextUpdateEvent(evt);
            }
        });
        jScrollPane2.setViewportView(jNotePane);

        jButton7.setBackground(new java.awt.Color(239, 235, 223));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/beginning.png"))); // NOI18N
        jButton7.setToolTipText("Erste Notiz");
        jButton7.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton7.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton7.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFirstNoteEvent(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(239, 235, 223));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/prev.png"))); // NOI18N
        jButton4.setToolTipText("Vorherige Notiz");
        jButton4.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton4.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton4.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                firePrevNoteEvent(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(239, 235, 223));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/document_delete.png"))); // NOI18N
        jButton8.setToolTipText("Notiz löschen");
        jButton8.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton8.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton8.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireDeleteNoteEvent(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(239, 235, 223));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/document_new.png"))); // NOI18N
        jButton9.setToolTipText("Neue Notiz erstellen");
        jButton9.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton9.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton9.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireNewNoteEvent(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(239, 235, 223));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/next.png"))); // NOI18N
        jButton5.setToolTipText("Nächste Notiz");
        jButton5.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton5.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton5.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireNextNoteEvent(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(239, 235, 223));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/end.png"))); // NOI18N
        jButton6.setToolTipText("Letzte Notiz");
        jButton6.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton6.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton6.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireLastNoteEvent(evt);
            }
        });

        jSearchField.setToolTipText("Suchbegriff");
        jSearchField.setMaximumSize(new java.awt.Dimension(200, 25));
        jSearchField.setMinimumSize(new java.awt.Dimension(200, 25));
        jSearchField.setPreferredSize(new java.awt.Dimension(200, 25));

        jButton3.setBackground(new java.awt.Color(239, 235, 223));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/text_find.png"))); // NOI18N
        jButton3.setToolTipText("Notiz suchen");
        jButton3.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton3.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton3.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireFindNoteEvent(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Letzte Änderung");

        jLastModified.setEditable(false);
        jLastModified.setToolTipText("Letztes Änderungsdatum");
        jLastModified.setMaximumSize(new java.awt.Dimension(150, 25));
        jLastModified.setMinimumSize(new java.awt.Dimension(150, 25));
        jLastModified.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Kartensymbol");
        jLabel2.setMaximumSize(new java.awt.Dimension(66, 20));
        jLabel2.setMinimumSize(new java.awt.Dimension(66, 20));
        jLabel2.setPreferredSize(new java.awt.Dimension(66, 20));

        jIconBox.setToolTipText("Symbol, das auf der Karte angezeigt wird");
        jIconBox.setMaximumSize(new java.awt.Dimension(70, 25));
        jIconBox.setMinimumSize(new java.awt.Dimension(70, 25));
        jIconBox.setPreferredSize(new java.awt.Dimension(70, 25));
        jIconBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireMapMarkerChangedEvent(evt);
            }
        });

        jNoteSymbolBox.setToolTipText("Symbol, das im Kartenpopup angezeigt wird");
        jNoteSymbolBox.setMaximumSize(new java.awt.Dimension(70, 25));
        jNoteSymbolBox.setMinimumSize(new java.awt.Dimension(70, 25));
        jNoteSymbolBox.setPreferredSize(new java.awt.Dimension(70, 25));
        jNoteSymbolBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireNoteSymbolChangedEvent(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 11));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Notizsymbol");

        jButton13.setBackground(new java.awt.Color(239, 235, 223));
        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/to_clipboard.png"))); // NOI18N
        jButton13.setToolTipText("Notiz in die Zwischenablage kopieren");
        jButton13.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton13.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton13.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireClickNoteToClipboardEvent(evt);
            }
        });

        javax.swing.GroupLayout jRightPanelLayout = new javax.swing.GroupLayout(jRightPanel);
        jRightPanel.setLayout(jRightPanelLayout);
        jRightPanelLayout.setHorizontalGroup(
            jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jRightPanelLayout.createSequentialGroup()
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jRightPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLastModified, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jIconBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jRightPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jNoteSymbolBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jRightPanelLayout.setVerticalGroup(
            jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jRightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLastModified, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jIconBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jNoteSymbolBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jRightPanel);

        jLeftPanel.setOpaque(false);

        jVillageList.setDragEnabled(true);
        jScrollPane1.setViewportView(jVillageList);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Zugehörige Dörfer");

        jAddVillageField.setToolTipText("Koordinate des Dorfes, das der Notizliste hinzugefügt werden soll");
        jAddVillageField.setMaximumSize(new java.awt.Dimension(6, 25));
        jAddVillageField.setMinimumSize(new java.awt.Dimension(6, 25));
        jAddVillageField.setPreferredSize(new java.awt.Dimension(6, 25));

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setToolTipText("Dorf zur Notizliste hinzufügen");
        jButton1.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddVillageEvent(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(239, 235, 223));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/clipboard_empty.png"))); // NOI18N
        jButton11.setToolTipText("Dorfliste aus der Zwischenablage lesen");
        jButton11.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton11.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton11.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireVillagesFromClipboardEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton2.setToolTipText("Ausgewähltes Dorf aus der Notizliste löschen");
        jButton2.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveVillageEvent(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(239, 235, 223));
        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/center.png"))); // NOI18N
        jButton10.setToolTipText("Ausgewähltes Dorf auf der Karte zentrieren");
        jButton10.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton10.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton10.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCenterVillageEvent(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(239, 235, 223));
        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/to_clipboard.png"))); // NOI18N
        jButton12.setToolTipText("Dörfer als BB-Code in die Zwischenablage");
        jButton12.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton12.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton12.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireVillagesToClipboardEvent(evt);
            }
        });

        javax.swing.GroupLayout jLeftPanelLayout = new javax.swing.GroupLayout(jLeftPanel);
        jLeftPanel.setLayout(jLeftPanelLayout);
        jLeftPanelLayout.setHorizontalGroup(
            jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLeftPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLeftPanelLayout.createSequentialGroup()
                        .addComponent(jAddVillageField, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLeftPanelLayout.createSequentialGroup()
                        .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jLeftPanelLayout.setVerticalGroup(
            jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addGroup(jLeftPanelLayout.createSequentialGroup()
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAddVillageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jLeftPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );

        jAlwaysOnTopBox.setText("Immer im Vordergrund");
        jAlwaysOnTopBox.setOpaque(false);
        jAlwaysOnTopBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireAlwaysOnTopChangedEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAlwaysOnTopBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAlwaysOnTopBox)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireAddVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddVillageEvent
        String text = jAddVillageField.getText();
        /*  Village v = null;
        if (ServerSettings.getSingleton().getCoordType() != 2) {

        if (!text.trim().matches("\\(*[0-9]{1,2}\\:[0-9]{1,2}\\:[0-9]{1,2}\\)*")) {
        JOptionPaneHelper.showWarningBox(this, "Koordinaten müssen im Format Con:Sec:Sub angegeben werden.", "Fehler");
        return;
        }

        String[] split = text.trim().split("[\\:]");
        if (split == null || split.length != 3) {
        logger.warn("Invalid value '" + text + "'");
        JOptionPaneHelper.showWarningBox(this, "Dorfeintrag muss im Format Con:Sec:Sub angegeben werden.", "Fehler");
        return;

        }

        int[] xy = DSCalculator.hierarchicalToXy(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        v = DataHolder.getSingleton().getVillages()[xy[0]][xy[1]];
        } else {
        //if (!text.trim().matches("\\([0-9]{1,3}\\|[0-9]{1,3}\\)")) {
        if (!text.trim().matches(".*\\(*[0-9]{1,3}\\|[0-9]{1,3}\\)*")) {
        JOptionPaneHelper.showWarningBox(this, "Dorfeintrag muss im Format 'Dorfname (X|Y)' angegeben werden.", "Fehler");
        return;
        }

        text = text.substring(text.lastIndexOf("("), text.lastIndexOf(")") + 1);
        String[] split = text.replaceAll("\\(", "").replaceAll("\\)", "").trim().split("[\\|]");
        logger.warn("Invalid value '" + text + "'");
        if (split == null || split.length != 2) {
        JOptionPaneHelper.showWarningBox(this, "Koordinaten müssen im Format 'Dorfname (X|Y)' angegeben werden.", "Fehler");
        return;
        }

        v = DataHolder.getSingleton().getVillages()[Integer.parseInt(split[0])][Integer.parseInt(split[1])];
        }

        if (v == null) {
        JOptionPaneHelper.showWarningBox(this, "An den angegebenen Koordinaten befindet sich kein Dorf.", "Fehler");
        return;

        }
         */

        List<Village> villages = PluginManager.getSingleton().executeVillageParser(text);
        if (villages == null || villages.isEmpty()) {
            JOptionPaneHelper.showInformationBox(this, "Keine Dorfkoordinaten gefunden.", "Information");
            return;

        }


        boolean added = false;
        if (currentNote != null) {
            //add village to note
            for (Village v : villages) {
                if (currentNote != null) {
                    added = (currentNote.addVillage(v)) ? true : added;
                }

            }
        } else {
            JOptionPaneHelper.showWarningBox(this, "Es ist keine Notiz ausgewählt.", "Fehler");
        }

        if (added) {
            showCurrentNote();
        } else {
            JOptionPaneHelper.showInformationBox(this, "Die Notiz ist diesem Dorf/diesen Dörfern bereits zugeordnet.", "Information");
        }
    }//GEN-LAST:event_fireAddVillageEvent

    private void fireNewNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireNewNoteEvent
        Note n = new Note();
        NoteManager.getSingleton().addNote(n);
        currentNote =
                n;
        showCurrentNote();
    }//GEN-LAST:event_fireNewNoteEvent

    private void firePrevNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_firePrevNoteEvent
        currentNote = NoteManager.getSingleton().getPreviousNote(currentNote);
        showCurrentNote();
    }//GEN-LAST:event_firePrevNoteEvent

    private void fireFirstNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireFirstNoteEvent
        currentNote = NoteManager.getSingleton().getFirstNote();
        showCurrentNote();
    }//GEN-LAST:event_fireFirstNoteEvent

    private void fireNextNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireNextNoteEvent
        currentNote = NoteManager.getSingleton().getNextNote(currentNote);
        showCurrentNote();
    }//GEN-LAST:event_fireNextNoteEvent

    private void fireLastNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireLastNoteEvent
        currentNote = NoteManager.getSingleton().getLastNote();
        showCurrentNote();
    }//GEN-LAST:event_fireLastNoteEvent

    private void fireFindNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireFindNoteEvent
        if (NoteManager.getSingleton().getNotes().size() <= 0) {
            JOptionPaneHelper.showWarningBox(this, "Keine Notizen vorhanden.", "Fehler");
            return;

        }



        String text = jSearchField.getText();
        if (text.length() <= 0) {
            return;
        }

        Note n = NoteManager.getSingleton().findNote(currentNote, text);
        if (n != null) {
            currentNote = n;
            showCurrentNote();

        } else {
            JOptionPaneHelper.showInformationBox(this, "Die Suche nach '" + text + "' lieferte keine Ergebnisse.", "Information");
        }

    }//GEN-LAST:event_fireFindNoteEvent

    private void fireDeleteNoteEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireDeleteNoteEvent
        if (currentNote != null) {
            if (JOptionPaneHelper.showQuestionConfirmBox(this, "Notiz wirklich löschen?", "Löschen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
                NoteManager.getSingleton().removeNote(currentNote);
                currentNote =
                        NoteManager.getSingleton().getFirstNote();
            }

            showCurrentNote();
        }
    }//GEN-LAST:event_fireDeleteNoteEvent

    private void fireRemoveVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveVillageEvent
        Object[] values = jVillageList.getSelectedValues();
        if (values == null || values.length == 0) {
            return;
        }

        String message = values.length + ((values.length > 1) ? " Dörfer " : " Dorf ") + "entfernen?";
        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, "Dörfer entfernen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
            for (Object o : values) {
                currentNote.removeVillage((Village) o);
            }

            showCurrentNote();
        }

    }//GEN-LAST:event_fireRemoveVillageEvent

    private void fireMapMarkerChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireMapMarkerChangedEvent
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            if (currentNote != null) {
                currentNote.setMapMarker((Integer) jIconBox.getSelectedItem());
            }

        }
    }//GEN-LAST:event_fireMapMarkerChangedEvent

    private void fireAlwaysOnTopChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireAlwaysOnTopChangedEvent
        setAlwaysOnTop(!isAlwaysOnTop());
    }//GEN-LAST:event_fireAlwaysOnTopChangedEvent

    private void fireNoteSymbolChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireNoteSymbolChangedEvent
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            if (currentNote != null) {
                currentNote.setNoteSymbol((Integer) jNoteSymbolBox.getSelectedItem());
            }
        }
    }//GEN-LAST:event_fireNoteSymbolChangedEvent

    private void fireCenterVillageEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCenterVillageEvent
        try {
            Village selection = (Village) jVillageList.getSelectedValue();
            if (selection != null) {
                DSWorkbenchMainFrame.getSingleton().centerVillage(selection);
            }

        } catch (Exception e) {
            logger.error("Failed to center village", e);
        }

    }//GEN-LAST:event_fireCenterVillageEvent

    private void fireNoteTextUpdateEvent(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fireNoteTextUpdateEvent
        if (currentNote != null) {
            currentNote.setNoteText(jNotePane.getText());
        }
    }//GEN-LAST:event_fireNoteTextUpdateEvent

    private void fireVillagesFromClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireVillagesFromClipboardEvent
        try {
            Transferable t = (Transferable) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            List<Village> villages = PluginManager.getSingleton().executeVillageParser((String) t.getTransferData(DataFlavor.stringFlavor));
            if (villages == null || villages.isEmpty()) {
                JOptionPaneHelper.showInformationBox(this, "Es konnten keine Dorfkoodinaten in der Zwischenablage gefunden werden.", "Information");
                return;

            } else {
                for (Village v : villages) {
                    currentNote.addVillage(v);
                }

                setCurrentNote(currentNote);
            }

        } catch (Exception e) {
            logger.error("Failed to parse source villages from clipboard", e);
            JOptionPaneHelper.showErrorBox(this, "Fehler beim Lesen der Zwischenablage.", "Fehler");
        }
    }//GEN-LAST:event_fireVillagesFromClipboardEvent

    private void fireVillagesToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireVillagesToClipboardEvent
        if (jVillageList.getModel().getSize() == 0) {
            return;
        }
        List<Village> selection = new LinkedList<Village>();
        for (int i = 0; i < jVillageList.getModel().getSize(); i++) {
            selection.add((Village) jVillageList.getModel().getElementAt(i));
        }
        try {
            String result = VillageListFormatter.format(selection, "%VILLAGE%", true);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(result.toString()), null);
            JOptionPaneHelper.showInformationBox(this, "Dorfdaten in die Zwischenablage kopiert.", "Daten kopiert");
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            JOptionPaneHelper.showErrorBox(this, "Fehler beim Kopieren der Daten.", "Fehler");
        }
    }//GEN-LAST:event_fireVillagesToClipboardEvent

    private void fireClickNoteToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireClickNoteToClipboardEvent
        if (currentNote == null) {
            return;
        }

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(currentNote.toBBCode()), null);
            JOptionPaneHelper.showInformationBox(this, "Notiz in die Zwischenablage kopiert.", "Daten kopiert");
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            JOptionPaneHelper.showErrorBox(this, "Fehler beim Kopieren der Daten.", "Fehler");
        }
    }//GEN-LAST:event_fireClickNoteToClipboardEvent

    private void showCurrentNote() {
        if (currentNote == null) {
            jVillageList.setModel(new DefaultListModel());
            jNotePane.setDocument(new HTMLDocument());
        } else {
            DefaultListModel model = new DefaultListModel();
            for (Integer id : currentNote.getVillageIds()) {
                Village v = DataHolder.getSingleton().getVillagesById().get(id);
                model.addElement(v);
            }

            jVillageList.setModel(model);
            jNotePane.setText(currentNote.getNoteText());
            jLastModified.setText(new SimpleDateFormat("dd.MM.yy 'um' HH:mm:ss").format(new Date(currentNote.getTimestamp())));
            jIconBox.setSelectedItem(currentNote.getMapMarker());
            jNoteSymbolBox.setSelectedItem(currentNote.getNoteSymbol());
        }

        List<Note> n = NoteManager.getSingleton().getNotes();
        setTitle("Notizblock - Notiz " + (n.indexOf(currentNote) + 1) + " von " + n.size());
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
        if (currentNote == null) {
            return;
        }
        try {
            Rectangle bounds = jVillageList.getBounds();
            Point locationOnScreen = jVillageList.getLocationOnScreen();
            bounds.setLocation(locationOnScreen);
            pDropLocation.move(locationOnScreen.x, locationOnScreen.y);
            if (bounds.contains(pDropLocation)) {
                for (Village v : pVillages) {
                    currentNote.addVillage(v);
                }
            }
            showCurrentNote();
            MapPanel.getSingleton().getMapRenderer().initiateRedraw(MapRenderer.NOTE_LAYER);
        } catch (Exception e) {
            logger.error("Failed to insert dropped villages", e);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* java.awt.EventQueue.invokeLater(new Runnable() {

        public void run() {
        new DSWorkbenchNotepad().setVisible(true);
        }
        });*/

        //String text = "|41:375:12| in memoria Frozen	177|492";
        //System.out.println(text.trim().matches(".*\\(*[0-9]{1,3}\\|[0-9]{1,3}\\)*"));

        String test = "test;test;test;";
        System.out.println(test.split(";").length);

        //System.out.println(text.matches("\\([0-9]{1,3}\\|[0-9]{1,3}\\)"));
        //System.out.println(text.matches("[0-9]{1,3}\\:[0-9]{1,3}:[0-9]{1,3}"));
       /* String[] t = text.split("[0-9]{1,3}");
        System.out.println(t.length);
        System.out.println(t[t.length - 3]);
        System.out.println(t[t.length - 2]);
        System.out.println(t[t.length - 1]);

        System.out.println(text.trim().matches(".+[0-9]{1,3}\\:[0-9]{1,3}:[0-9]{1,3}"));*/
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jAddVillageField;
    private javax.swing.JCheckBox jAlwaysOnTopBox;
    private javax.swing.JButton jBlackColorButton;
    private javax.swing.JButton jBlueColorButton;
    private javax.swing.JButton jBoldButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jCenterButton;
    private javax.swing.JPanel jColorPanel;
    private javax.swing.JButton jCyanColorButton;
    private javax.swing.JPanel jFormatPanel;
    private javax.swing.JButton jGreenColorButton;
    private javax.swing.JComboBox jIconBox;
    private javax.swing.JButton jItalicButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jLastModified;
    private javax.swing.JButton jLeftButton;
    private javax.swing.JPanel jLeftPanel;
    private javax.swing.JTextArea jNotePane;
    private javax.swing.JComboBox jNoteSymbolBox;
    private javax.swing.JButton jOrangeColorButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jPinkColorButton;
    private javax.swing.JButton jRedColorButton;
    private javax.swing.JButton jRightButton;
    private javax.swing.JPanel jRightPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jSearchField;
    private javax.swing.JButton jSize10Button;
    private javax.swing.JButton jSize12Button;
    private javax.swing.JButton jSize14Button;
    private javax.swing.JButton jSize18Button;
    private javax.swing.JButton jSize20Button;
    private javax.swing.JButton jSize28Button;
    private javax.swing.JPanel jSizePanel;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton jUnderlineButton;
    private javax.swing.JList jVillageList;
    private javax.swing.JButton jVioletColorButton;
    // End of variables declaration//GEN-END:variables
}
