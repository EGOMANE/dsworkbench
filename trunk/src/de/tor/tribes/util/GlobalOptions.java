/*
 * GlobalOptions.java
 *
 * Created on 29.09.2007, 16:29:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.WorldDecorationHolder;
import de.tor.tribes.types.test.DummyUserProfile;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.ui.views.DSWorkbenchAttackFrame;
import de.tor.tribes.ui.views.DSWorkbenchChurchFrame;
import de.tor.tribes.ui.views.DSWorkbenchConquersFrame;
import de.tor.tribes.ui.views.DSWorkbenchDistanceFrame;
import de.tor.tribes.ui.views.DSWorkbenchDoItYourselfAttackPlaner;
import de.tor.tribes.ui.views.DSWorkbenchFormFrame;
import de.tor.tribes.ui.views.DSWorkbenchMarkerFrame;
import de.tor.tribes.ui.views.DSWorkbenchMerchantDistibutor;
import de.tor.tribes.ui.views.DSWorkbenchNotepad;
import de.tor.tribes.ui.views.DSWorkbenchRankFrame;
import de.tor.tribes.ui.views.DSWorkbenchReTimerFrame;
import de.tor.tribes.ui.views.DSWorkbenchReportFrame;
import de.tor.tribes.ui.views.DSWorkbenchSOSRequestAnalyzer;
import de.tor.tribes.ui.views.DSWorkbenchSelectionFrame;
import de.tor.tribes.ui.views.DSWorkbenchStatsFrame;
import de.tor.tribes.ui.views.DSWorkbenchTagFrame;
import de.tor.tribes.ui.views.DSWorkbenchTroopsFrame;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.attack.StandardAttackManager;
import de.tor.tribes.util.church.ChurchManager;
import de.tor.tribes.util.conquer.ConquerManager;
import de.tor.tribes.util.map.FormManager;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.note.NoteManager;
import de.tor.tribes.util.report.ReportManager;
import de.tor.tribes.util.roi.ROIManager;
import de.tor.tribes.util.stat.StatManager;
import de.tor.tribes.util.tag.TagManager;
import de.tor.tribes.util.troops.TroopsManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

/**Global settings used by almost all components. e.g. WorldData or UI specific objects
 * @TODO save data to user home
 * @author Charon
 */
public class GlobalOptions {

    private static Logger logger = Logger.getLogger("GlobalSettings");
    private static boolean INITIALIZED = false;
    /**Active skin used by the MapPanel*/
    private static Skin mSkin;
    /**DataHolder which holds and manages the WorldData*/
    private static WorldDecorationHolder mDecorationHolder = null;
    private static String SELECTED_SERVER = null;
    private static Properties GLOBAL_PROPERTIES = new Properties();
    //flag for online/offline mode
    private static boolean isOfflineMode = false;
    //used to store last attack time of AttackAddFrame
    private static Date lastArriveTime = null;
    private static HelpBroker mainHelpBroker = null;
    private static CSH.DisplayHelpFromSource csh = null;
    private static final String mainHelpSetName = "DS Workbench Dokumentation.hs";
    private static boolean internalDataDamaged = false;
    private static UserProfile mSelectedProfile = null;

    /**Init all managed objects
     * @param pDownloadData TRUE=download the WorldData from the tribes server
     * @throws Exception If an Error occurs while initializing the objects
     */
    public static void initialize() throws Exception {
        if (INITIALIZED) {
            return;
        }
        INITIALIZED = true;
        logger.debug("Loading help system");
        loadHelpSystem();
        logger.debug("Loading properties");
        loadProperties();
        logger.debug("Loading graphic pack");
        loadSkin();
        logger.debug("Loading world.dat");
        WorldDecorationHolder.initialize();
        setSelectedServer(getProperty("default.server"));
        UIManager.put("OptionPane.background", Constants.DS_BACK);
        UIManager.put("Panel.background", Constants.DS_BACK);
        UIManager.put("Button.background", Constants.DS_BACK_LIGHT);
    }

    public static void setInternatDataDamaged(boolean pValue) {
        logger.info("Internal data markes as " + ((pValue) ? "'DAMAGED'" : "'VALID'"));
        internalDataDamaged = pValue;
    }

    public static boolean isInternalDataDamaged() {
        return internalDataDamaged;
    }

    /**Tells if a network connection is established or not*/
    public static boolean isOfflineMode() {
        return isOfflineMode;
    }

    /**Set the network status*/
    public static void setOfflineMode(boolean pValue) {
        isOfflineMode = pValue;
    }

    /**Get the list of available skins*/
    public static String[] getAvailableSkins() {
        List<String> skins = new LinkedList<String>();
        skins.add(Skin.MINIMAP_SKIN_ID);
        for (String s : new File("graphics/skins").list()) {
            skins.add(s);
        }
        Collections.sort(skins);
        return skins.toArray(new String[]{});
    }

    private static void loadHelpSystem() {
        if (mainHelpBroker == null) {
            HelpSet mainHelpSet = null;
            try {
                URL hsURL = HelpSet.findHelpSet(null, mainHelpSetName);
                if (hsURL == null) {
                    logger.error("HelpSet " + mainHelpSetName + " not found.");
                } else {
                    logger.debug("HelpSet found");
                    mainHelpSet = new HelpSet(null, hsURL);
                }

            } catch (HelpSetException ee) {
                logger.error("HelpSet " + mainHelpSetName + " could not be opened.", ee);
                return;
            }
            logger.debug("HelpSet opened");

            if (mainHelpSet != null) {
                logger.debug("Creating HelpBroker");
                mainHelpBroker = mainHelpSet.createHelpBroker();
            }

            if (mainHelpBroker != null) {
                logger.debug("Creating DisplayHelpFromSource");
                csh = new CSH.DisplayHelpFromSource(mainHelpBroker);

            }
        }
        logger.debug("HelpSystem initialized");
    }

    public static HelpBroker getHelpBroker() {
        return mainHelpBroker;
    }

    public static CSH.DisplayHelpFromSource getHelpDisplay() {
        return csh;
    }

    public static UserProfile getSelectedProfile() {
        return mSelectedProfile;
    }

    public static void setSelectedProfile(UserProfile pProfile) {
        mSelectedProfile = pProfile;
    }

    /**Load the global properties*/
    private static void loadProperties() throws Exception {
        GLOBAL_PROPERTIES = new Properties();
        if (new File("global.properties").exists()) {
            logger.debug("Loading existing properties file");
            FileInputStream fin = new FileInputStream("global.properties");
            GLOBAL_PROPERTIES.load(fin);
            fin.close();
        } else {
            logger.debug("Creating empty properties file");
            saveProperties();
        }
    }

    /**Store the global properties*/
    public static void saveProperties() {
        logger.debug("Saving global properties");
        try {
            FileOutputStream fout = new FileOutputStream("global.properties");
            GLOBAL_PROPERTIES.store(fout, "Automatically generated. Please do not modify!");
            fout.flush();
            fout.close();
        } catch (Exception e) {
            logger.error("Failed to write properties", e);
        }
    }

    public static void storeViewStates() {
        logger.debug("Saving view state");
        DSWorkbenchAttackFrame.getSingleton().storeProperties();
        DSWorkbenchChurchFrame.getSingleton().storeProperties();
        DSWorkbenchDistanceFrame.getSingleton().storeProperties();
        DSWorkbenchDoItYourselfAttackPlaner.getSingleton().storeProperties();
        DSWorkbenchMarkerFrame.getSingleton().storeProperties();
        DSWorkbenchMerchantDistibutor.getSingleton().storeProperties();
        DSWorkbenchReTimerFrame.getSingleton().storeProperties();
        DSWorkbenchSOSRequestAnalyzer.getSingleton().storeProperties();
        DSWorkbenchStatsFrame.getSingleton().storeProperties();
        DSWorkbenchTagFrame.getSingleton().storeProperties();
        DSWorkbenchConquersFrame.getSingleton().storeProperties();
        DSWorkbenchFormFrame.getSingleton().storeProperties();
        DSWorkbenchRankFrame.getSingleton().storeProperties();
        DSWorkbenchNotepad.getSingleton().storeProperties();
        DSWorkbenchTroopsFrame.getSingleton().storeProperties();
        DSWorkbenchSelectionFrame.getSingleton().storeProperties();
        DSWorkbenchReportFrame.getSingleton().storeProperties();
    }

    /**Add a property*/
    public static void addProperty(String pKey, String pValue) {
        GLOBAL_PROPERTIES.put(pKey, pValue);
    }

    /**Remove a property*/
    public static void removeProperty(String pKey) {
        GLOBAL_PROPERTIES.remove(pKey);
    }

    /**Get the value of a property*/
    public static String getProperty(String pKey) {
        if (pKey == null) {
            return null;
        }
        return GLOBAL_PROPERTIES.getProperty(pKey);
    }

    /**Load the default skin
     * @throws Exception If there was an error while loading the default skin
     */
    public static void loadSkin() throws Exception {
        mSkin = new Skin(GLOBAL_PROPERTIES.getProperty("default.skin"));
    }

    /**Load user data (attacks, markers...)*/
    public static void loadUserData() {
        if (getSelectedServer() != null
                && getSelectedProfile() != null
                && !getSelectedProfile().equals(DummyUserProfile.getSingleton())) {
            logger.debug("Loading markers");
            MarkerManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/markers.xml");
            logger.debug("Loading attacks");
            AttackManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/attacks.xml");
            logger.debug("Loading tags");
            TagManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/tags.xml");
            logger.debug("Loading troops");
            TroopsManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/troops.xml");
            logger.debug("Loading forms");
            FormManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/forms.xml");
            logger.debug("Loading churches");
            ChurchManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/churches.xml");
            logger.debug("Loading rois");
            ROIManager.getSingleton().loadROIsFromFile(getSelectedProfile().getProfileDirectory() + "/rois.xml");
            logger.debug("Loading conquers");
            ConquerManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/conquers.xml");
            logger.debug("Loading notes");
            NoteManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/notes.xml");
            logger.debug("Loading standard attacks");
            StandardAttackManager.getSingleton().loadStandardAttacksFromDisk(getSelectedProfile().getProfileDirectory() + "/stdAttacks.xml");
            logger.debug("Loading reports");
            ReportManager.getSingleton().loadElements(getSelectedProfile().getProfileDirectory() + "/reports.xml");
            logger.debug("Removing temporary data");
            DataHolder.getSingleton().removeTempData();
        }
    }

    /**Load user data (attacks, markers...)*/
    public static void saveUserData() {
        if (getSelectedServer() != null
                && getSelectedProfile() != null
                && !getSelectedProfile().equals(DummyUserProfile.getSingleton())
                && !isInternalDataDamaged()) {
            logger.debug("Saving markers");
            MarkerManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/markers.xml");
            logger.debug("Saving attacks");
            AttackManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/attacks.xml");
            logger.debug("Saving tags");
            TagManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/tags.xml");
            logger.debug("Saving troops");
            TroopsManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/troops.xml");
            logger.debug("Saving forms");
            FormManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/forms.xml");
            logger.debug("Saving churches");
            ChurchManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/churches.xml");
            logger.debug("Saving rois");
            ROIManager.getSingleton().saveROIsToFile(getSelectedProfile().getProfileDirectory() + "/rois.xml");
            logger.debug("Saving conquers");
            ConquerManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/conquers.xml");
            logger.debug("Saving notes");
            NoteManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/notes.xml");
            logger.debug("Saving standard attacks");
            StandardAttackManager.getSingleton().saveStandardAttacksToDisk(getSelectedProfile().getProfileDirectory() + "/stdAttacks.xml");
            logger.debug("Saving stats");
            StatManager.getSingleton().storeStats();
            logger.debug("Saving resports");
            ReportManager.getSingleton().saveElements(getSelectedProfile().getProfileDirectory() + "/reports.xml");
            logger.debug("User data saved");
        } else {
            if (isInternalDataDamaged()) {
                logger.warn("Internal data marked as 'damaged'. Skipped saving user data");
            }
        }
    }

    public static Skin getSkin() {
        return mSkin;
    }

    /**Get the DecorationHolder
     * @return WorldDecorationHolder Object which contains the WorldData
     */
    public static WorldDecorationHolder getWorldDecorationHolder() {
        return mDecorationHolder;
    }

    public static String getSelectedServer() {
        return SELECTED_SERVER;
    }

    public static void setSelectedServer(String pServer) {
        if (pServer == null) {
            return;
        }
        if (SELECTED_SERVER != null) {
            if (SELECTED_SERVER.equals(pServer)) {
                return;
            } else {
                logger.info("Setting selected server to " + pServer);
                SELECTED_SERVER = pServer;
            }
        } else {
            logger.info("Setting selected server to " + pServer);
            SELECTED_SERVER = pServer;
        }
    }

    public static void setLastArriveTime(Date pTime) {
        lastArriveTime = pTime;
    }

    public static Date getLastArriveTime() {
        return lastArriveTime;
    }
}
