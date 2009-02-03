/*
 * DSWorkbenchSplashScreen.java
 *
 * Created on 30. Juni 2008, 14:12
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.util.GlobalOptions;
import org.apache.log4j.Logger;
import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.php.DatabaseInterface;
import de.tor.tribes.util.Constants;
import java.awt.Font;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Level;
import org.apache.log4j.RollingFileAppender;
import javax.swing.*;

/**
 * @author  Jejkal
 */
public class DSWorkbenchSplashScreen extends javax.swing.JFrame implements DataHolderListener {

    private static Logger logger = Logger.getLogger("Launcher");
    private final DSWorkbenchSplashScreen self = this;
    private final SplashRepaintThread t;
    private static DSWorkbenchSplashScreen SINGLETON = null;

    public static synchronized DSWorkbenchSplashScreen getSingleton() {
        if (SINGLETON == null) {
            System.out.println("Splash");
            SINGLETON = new DSWorkbenchSplashScreen();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchSplashScreen */
    DSWorkbenchSplashScreen() {
        initComponents();
        jLabel1.setIcon(new ImageIcon("./graphics/splash.gif"));
        new Timer("StartupTimer", true).schedule(new HideSplashTask(this), 1000);
        t = new SplashRepaintThread(this);
        t.setDaemon(true);
        t.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jStatusOutput = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jStatusOutput.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
        jStatusOutput.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jStatusOutput.setText("Lade Einstellungen...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
            .addComponent(jStatusOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jStatusOutput))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected void hideSplash() {
        try {
            //load properties, cursors, skins, world decoration
            GlobalOptions.initialize();
            DataHolder.getSingleton().addDataHolderListener(this);
            DataHolder.getSingleton().addDataHolderListener(DSWorkbenchSettingsDialog.getSingleton());
        } catch (Exception e) {
            logger.error("Failed to initialize global options", e);
            JOptionPane.showMessageDialog(self, "Fehler bei der Initialisierung.\nMöglicherweise ist deine DS Workbench Installation defekt.", "Fehler", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if (!DSWorkbenchSettingsDialog.getSingleton().checkSettings()) {
            logger.info("Reading user settings returned error(s)");
            DSWorkbenchSettingsDialog.getSingleton().setVisible(true);
        }

        // <editor-fold defaultstate="collapsed" desc=" Check for data updates ">
        boolean checkForUpdates = false;
        try {
            checkForUpdates = Boolean.parseBoolean(GlobalOptions.getProperty("check.updates.on.startup"));
        } catch (Exception e) {
            checkForUpdates = false;
        }
        if (checkForUpdates && !GlobalOptions.isOfflineMode()) {
            String selectedServer = GlobalOptions.getProperty("default.server");
            String name = GlobalOptions.getProperty("account.name");
            String password = GlobalOptions.getProperty("account.password");
            if (DatabaseInterface.checkUser(name, password) != DatabaseInterface.ID_SUCCESS) {
                JOptionPane.showMessageDialog(this, "Die Accountvalidierung ist fehlgeschlagen.\n" +
                        "Bitte überprüfe deine Account- und Netzwerkeinstellungen und versuches es erneut.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
                checkForUpdates = false;
            } else {
                long serverDataVersion = DatabaseInterface.getServerDataVersion(selectedServer);
                long userDataVersion = DatabaseInterface.getUserDataVersion(name, selectedServer);
                logger.debug("User data version is " + userDataVersion);
                logger.debug("Server data version is " + serverDataVersion);
                if (userDataVersion == serverDataVersion) {
                    logger.debug("Skip downloading updates");
                    checkForUpdates = false;
                }
            }
        }

        try {
            DataHolder.getSingleton().loadData(checkForUpdates);
        } catch (Exception e) {
            logger.error("Failed to load server data", e);
            System.exit(1);
        }
        // </editor-fold>

        try {
            logger.debug("Initializing application window");
            DSWorkbenchMainFrame.getSingleton().init();
            logger.debug("Initializing search frame");
            DSWorkbenchSearchFrame.getSingleton();
        
            logger.info("Showing application window");
            DSWorkbenchMainFrame.getSingleton().setVisible(true);
            t.stopRunning();
            setVisible(false);
            boolean informOnUpdate = true;

            try {
                String val = GlobalOptions.getProperty("inform.on.updates");
                if (val != null) {
                    informOnUpdate = Boolean.parseBoolean(val);
                }
            } catch (Exception e) {
                //value not found, inform by default
            }
            if (informOnUpdate) {
                //check version           
                double version = DatabaseInterface.getCurrentVersion();

                if (version > 0 && version > Constants.VERSION) {
                    NotifierFrame.doNotification("Eine neue Version (" + version + ") von DS Workbench ist verfügbar.\n" +
                            "Klicke auf das Update Icon um \'http://www.dsworkbench.de\' im Browser zu öffnen.", NotifierFrame.NOTIFY_UPDATE);
                }
            }
        } catch (Throwable th) {
            logger.fatal("Fatal error while running DS Workbench", th);
            JOptionPane.showMessageDialog(self, "Ein schwerwiegender Fehler ist aufgetreten.\nMöglicherweise ist deine DS Workbench Installation defekt. Bitte kontaktiere den Entwickler.", "Fehler", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Locale.setDefault(Locale.US);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        Font f = new Font("SansSerif", Font.PLAIN, 11);
        UIManager.put("Label.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("EditorPane.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("ToggleButton.font", f);
        UIManager.put("CheckBox.font", f);
        UIManager.put("CheckBoxMenuItem.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("OptionPane.font", f);
        UIManager.put("Panel.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("PopupMenu.font", f);
        UIManager.put("ProgressBar.font", f);
        UIManager.put("RadioButton.font", f);
        UIManager.put("ScrollPane.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("TextPane.font", f);
        UIManager.put("ToolTip.font", f);
        UIManager.put("Tree.font", f);
        UIManager.put("Viewport.font", f);


        //UIManager.put("Panel.background", Constants.DS_BACK);
        UIManager.put("Label.background", Constants.DS_BACK);
        UIManager.put("MenuBar.background", Constants.DS_BACK);
        UIManager.put("ScrollPane.background", Constants.DS_BACK);
        UIManager.put("Button.background", Constants.DS_BACK);
        UIManager.put("TabbedPane.background", Constants.DS_BACK);
        UIManager.put("SplitPane.background", Constants.DS_BACK);
        UIManager.put("Separator.background", Constants.DS_BACK);
        UIManager.put("Menu.background", Constants.DS_BACK);
        UIManager.put("OptionPane.background", Constants.DS_BACK);
        UIManager.put("ToolBar.background", Constants.DS_BACK);

        //error mode
        int mode = -1;
        if (args != null) {
            for (String arg : args) {
                if (arg.equals("-d") || arg.equals("--debug")) {
                    //debug mode
                    mode = 1;
                } else if (arg.equals("-i") || arg.equals("--info")) {
                    //info mode
                    mode = 0;
                }
            }
        }

        RollingFileAppender a = new org.apache.log4j.RollingFileAppender();
        //a.setLayout(new org.apache.log4j.PatternLayout("%d [%t] %-5p %C{6} (%F:%L) - %m%n"));
        a.setLayout(new org.apache.log4j.PatternLayout("%d - %-5p - %-20c - %m%n"));
        try {
            a.setFile("./log/dsworkbench.log", true, true, 1024);
            switch (mode) {
                case 0: {
                    Logger.getLogger("de.tor").setLevel(Level.INFO);
                    Logger.getLogger("dswb").setLevel(Level.INFO);
                    break;
                }
                case 1: {
                    Logger.getLogger("de.tor").setLevel(Level.DEBUG);
                    Logger.getLogger("dswb").setLevel(Level.DEBUG);
                    break;
                }
                default: {
                    Logger.getLogger("de.tor").setLevel(Level.ERROR);
                    Logger.getLogger("dswb").setLevel(Level.ERROR);
                    break;
                }
            }


            Logger.getRootLogger().addAppender(a);
            Logger.getLogger("de.tor").addAppender(a);
            Logger.getLogger("dswb").addAppender(a);
        } catch (IOException ioe) {
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DSWorkbenchSplashScreen.getSingleton().setLocationRelativeTo(null);
                DSWorkbenchSplashScreen.getSingleton().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jStatusOutput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void fireDataHolderEvent(String pText) {
        jStatusOutput.setText(pText);
    }

    public void updateStatus() {
        jStatusOutput.updateUI();
    }

    @Override
    public void fireDataLoadedEvent(boolean pSuccess) {
        if (pSuccess) {
            jStatusOutput.setText("Daten geladen");
        } else {
            jStatusOutput.setText("Download fehlgeschlagen");
        }
    }
}

class HideSplashTask extends TimerTask {

    private DSWorkbenchSplashScreen mParent;

    public HideSplashTask(DSWorkbenchSplashScreen pParent) {
        mParent = pParent;
    }

    public void run() {
        mParent.hideSplash();
    }
}

class SplashRepaintThread extends Thread {

    private DSWorkbenchSplashScreen mParent;
    private boolean running = true;

    public SplashRepaintThread(DSWorkbenchSplashScreen pParent) {
        mParent = pParent;
    }

    public void run() {
        while (running) {
            mParent.updateStatus();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
