/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.conquer;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Conquer;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.MapPanel;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.xml.JaxenUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Charon
 */
public class ConquerManager {

    private static Logger logger = Logger.getLogger("ConquerManager");
    private static ConquerManager SINGLETON = null;
    private long lastUpdate = -1;
    private List<Conquer> conquers = null;
    private ConquerUpdateThread updateThread = null;
    private List<ConquerManagerListener> mManagerListeners = null;

    public static synchronized ConquerManager getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new ConquerManager();
        }
        return SINGLETON;
    }

    ConquerManager() {
        conquers = Collections.synchronizedList(new LinkedList<Conquer>());
        mManagerListeners = new LinkedList<ConquerManagerListener>();
        updateThread = new ConquerUpdateThread();
        updateThread.start();
    }

    public synchronized void addConquerManagerListener(ConquerManagerListener pListener) {
        if (pListener == null) {
            return;
        }
        if (!mManagerListeners.contains(pListener)) {
            mManagerListeners.add(pListener);
        }
    }

    public synchronized void removeConquerManagerListener(ConquerManagerListener pListener) {
        mManagerListeners.remove(pListener);
    }

    public int getConquerCount() {
        return conquers.size();
    }

    public Conquer getConquer(int id) {
        return conquers.get(id);
    }

    public void forceUpdate() {
        try {
            logger.debug("Force conquers update");
            updateThread.interrupt();
        } catch (Exception e) {
        }
    }

    public void loadConquersFromFile(String pFile) {
        conquers.clear();
        if (pFile == null) {
            logger.error("File argument is 'null'");
            return;
        }
        File conquerFile = new File(pFile);
        if (conquerFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reading conquers from '" + pFile + "'");
            }
            try {
                Document d = JaxenUtils.getDocument(conquerFile);
                String lastup = JaxenUtils.getNodeValue(d, "//conquers/lastUpdate");
                setLastUpdate(Long.parseLong(lastup));
                for (Element e : (List<Element>) JaxenUtils.getNodes(d, "//conquers/conquer")) {
                    try {
                        Conquer c = Conquer.fromXml(e);
                        conquers.add(c);
                    } catch (Exception inner) {
                        //ignored, conquer invalid
                    }
                }
                logger.debug("Conquers successfully loaded");
            } catch (Exception e) {
                logger.error("Failed to load conquers", e);
            }

            //merge conquers and world data
            logger.debug("Merging conquer data with world data");
            try {
                Conquer[] conquerA = conquers.toArray(new Conquer[]{});
                for (Conquer c : conquerA) {
                    Village v = DataHolder.getSingleton().getVillagesById().get(c.getVillageID());
                    Tribe loser = DataHolder.getSingleton().getTribes().get(c.getLoser());
                    Tribe winner = DataHolder.getSingleton().getTribes().get(c.getWinner());
                    if (v.getTribeID() != winner.getId()) {
                        //conquer not yet in world data
                        if (loser != null && loser.getVillageList().remove(v)) {
                            loser.setVillages((short) (loser.getVillages() - 1));
                            Ally loserAlly = loser.getAlly();
                            if (loserAlly != null) {
                                loserAlly.setVillages(loserAlly.getVillages() - 1);
                            }
                        }
                        if (winner != null && !winner.getVillageList().contains(v)) {
                            winner.getVillageList().add(v);
                            winner.setVillages((short) (winner.getVillages() + 1));
                            v.setTribe(winner);
                            v.setTribeID(winner.getId());
                            Ally winnerAlly = winner.getAlly();
                            if (winnerAlly != null) {
                                winnerAlly.setVillages(winnerAlly.getVillages() - 1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //setting last update to 0 to avoid errors
                lastUpdate = 0;
            }
            updateAcceptance();
            MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
        } else {
            lastUpdate = 0;
            if (logger.isInfoEnabled()) {
                logger.info("Conquers file not found under '" + pFile + "'");
            }
        }
    }

    public void saveConquersToFile(String pFile) {
        if (pFile == null) {
            logger.error("File argument is 'null'");
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Writing conquers to '" + pFile + "'");
        }

        try {
            FileWriter w = new FileWriter(pFile);
            w.write("<conquers>\n");
            w.write("<lastUpdate>" + getLastUpdate() + "</lastUpdate>\n");
            Conquer[] conquerA = conquers.toArray(new Conquer[]{});
            for (Conquer c : conquerA) {
                if (c != null) {
                    String xml = c.toXml();
                    if (xml != null) {
                        w.write(xml + "\n");
                        w.flush();
                    }
                }
            }
            w.write("</conquers>");
            w.flush();
            w.close();
            logger.debug("Conquers successfully saved");
        } catch (Throwable t) {
            if (!new File(pFile).getParentFile().exists()) {
                //server directory obviously does not exist yet
                //this should only happen at the first start
                logger.info("Ignoring error, server directory does not exists yet");
            } else {
                logger.error("Failed to save conquers", t);
            }
            //try to delete errornous file
            new File(pFile).delete();
        }
    }

    private void updateAcceptance() {
        logger.debug("Filtering conquers");
        double risePerHour = 1.0;
        try {
            risePerHour = ServerManager.getServerAcceptanceRiseSpeed(GlobalOptions.getSelectedServer());
        } catch (Exception e) {
        }
        logger.debug(" - using " + risePerHour + " as acceptance increment value");
        List<Conquer> toRemove = new LinkedList<Conquer>();
        Conquer[] conquersA = conquers.toArray(new Conquer[]{});
        for (Conquer c : conquersA) {
            Village v = DataHolder.getSingleton().getVillagesById().get(c.getVillageID());
            if (v == null) {
                toRemove.add(c);
            } else {
                long time = c.getTimestamp();
                //get diff in seconds
                long diff = System.currentTimeMillis() / 1000 - time;
                //compare diff with estimated time for reaching 100% acceptance
                if (diff > (75 / risePerHour) * 60 * 60) {
                    //acceptance has grown at least to 100, mark conquer for removal
                    toRemove.add(c);
                }
            }
        }
        logger.debug("Removing " + toRemove.size() + " conquers due to 100% acceptance");
        for (Conquer remove : toRemove) {
            conquers.remove(remove);
        }
        fireConquersChangedEvents();
    }

    protected void updateConquers() {
        logger.debug("Updating conquers from server");
        InputStream is = null;
        try {
            if (lastUpdate == -1) {
                //not yet loaded
                return;
            }
            String baseUrl = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            if (System.currentTimeMillis() - lastUpdate > 1000 * 60 * 60 * 24) {
                //time larger than 24 hours, take 23 hours in past
                logger.debug("Last update more than 24h ago. Setting last update to NOW - 23h");
                lastUpdate = System.currentTimeMillis() - 1000 * 60 * 60 * 23;
            }

            URL u = new URL(baseUrl + "/interface.php?func=get_conquer&since=" + (int) Math.rint(lastUpdate / 1000));
            logger.debug("Querying " + u.toString());
            URLConnection uc = u.openConnection();
            is = uc.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = r.readLine()) != null) {
                if (line.indexOf("ONLY_ONE_DAY_AGO") != -1) {
                    logger.warn("Update still more than 24h ago. Diff to server clock > 1h ?");
                } else {
                    String[] data = line.split(",");
                    //$village_id, $unix_timestamp, $new_owner, $old_owner
                    int villageID = Integer.parseInt(data[0]);
                    int timestamp = Integer.parseInt(data[1]);
                    int newOwner = Integer.parseInt(data[2]);
                    int oldOwner = Integer.parseInt(data[3]);
                    boolean exists = false;
                    Conquer[] conquerA = conquers.toArray(new Conquer[]{});
                    for (Conquer c : conquerA) {
                        if (c.getVillageID() == villageID) {
                            //already exists
                            c.setWinner(newOwner);
                            c.setLoser(oldOwner);
                            c.setTimestamp(timestamp);
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        Conquer c = new Conquer();
                        c.setVillageID(villageID);
                        c.setTimestamp(timestamp);
                        c.setWinner(newOwner);
                        c.setLoser(oldOwner);
                        conquers.add(c);
                    }
                    Tribe loser = DataHolder.getSingleton().getTribes().get(oldOwner);
                    Tribe winner = DataHolder.getSingleton().getTribes().get(newOwner);
                    Village v = DataHolder.getSingleton().getVillagesById().get(villageID);

                    if (v.getTribeID() != winner.getId()) {
                        //conquer not yet in world data
                        if (loser != null && loser.getVillageList().remove(v)) {
                            loser.setVillages((short) (loser.getVillages() - 1));
                            Ally loserAlly = loser.getAlly();
                            if (loserAlly != null) {
                                loserAlly.setVillages(loserAlly.getVillages() - 1);
                            }
                        }
                        if (winner != null && !winner.getVillageList().contains(v)) {
                            winner.getVillageList().add(v);
                            winner.setVillages((short) (winner.getVillages() + 1));
                            v.setTribe(winner);
                            v.setTribeID(winner.getId());
                            Ally winnerAlly = winner.getAlly();
                            if (winnerAlly != null) {
                                winnerAlly.setVillages(winnerAlly.getVillages() - 1);
                            }
                        }
                    }
                }
            }
            //enforce full map update
            is.close();
        } catch (Exception e) {
            logger.error("An error occured while updating conquers", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception inner) {
            }
        }
        lastUpdate = System.currentTimeMillis() + 1000;
        logger.debug("Setting lastUpdate to NOW (" + lastUpdate + ")");
        updateAcceptance();
    }

    public Conquer getConquer(Village pVillage) {
        if (pVillage == null) {
            return null;
        }
        Conquer[] conquerA = conquers.toArray(new Conquer[]{});
        for (Conquer c : conquerA) {
            if (c.getVillageID() == pVillage.getId()) {
                return c;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ConquerManager.getSingleton().updateConquers();
    }

    /**
     * @return the lastUpdate
     */
    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void conquersUpdatedExternally() {
        fireConquersChangedEvents();
    }

    public int[] getConquersStats() {

        int grey = 0;
        int friendly = 0;
        Conquer[] conquerA = conquers.toArray(new Conquer[]{});
        for (Conquer c : conquerA) {
            if (c.getLoser() == 0) {
                grey++;
            } else {
                Tribe loser = DataHolder.getSingleton().getTribes().get(c.getLoser());
                Tribe winner = DataHolder.getSingleton().getTribes().get(c.getWinner());
                if (loser != null && winner != null) {
                    if (loser.getAllyID() == winner.getAllyID()) {
                        friendly++;
                    } else {
                        Ally loserAlly = loser.getAlly();
                        Ally winnerAlly = winner.getAlly();
                        if (loserAlly != null && winnerAlly != null) {
                            String lAllyName = loserAlly.getName().toLowerCase();
                            String wAllyName = winnerAlly.getName().toLowerCase();
                            if (lAllyName.indexOf(wAllyName) > -1 || wAllyName.indexOf(lAllyName) > -1) {
                                friendly++;
                            }
                        }
                    }
                }
            }

        }
        return new int[]{grey, friendly};
    }

    /**Notify all MarkerManagerListeners that the marker data has changed*/
    private void fireConquersChangedEvents() {
        ConquerManagerListener[] listeners = mManagerListeners.toArray(new ConquerManagerListener[]{});
        for (ConquerManagerListener listener : listeners) {
            listener.fireConquersChangedEvent();
        }
        MapPanel.getSingleton().getMapRenderer().initiateRedraw(0);
    }
}

class ConquerUpdateThread extends Thread {

    private final long FIVE_MINUTES = 1000 * 60 * 5;

    public ConquerUpdateThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                ConquerManager.getSingleton().updateConquers();
                try {
                    Thread.sleep(FIVE_MINUTES);
                } catch (Exception e) {
                }
            } catch (Exception ignore) {
            }
        }
    }
}
