/*
 * AbstractDataReader.java
 *
 * Created on 17.07.2007, 21:49:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.tor.tribes.io;

import de.tor.tribes.db.DatabaseAdapter;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.xml.JaxenUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Charon
 */
public class DataHolder {

    private static Logger logger = Logger.getLogger(DataHolder.class);
    public final static int MAX_AGE = 24 * 60 * 60 * 1000;
    private final int ID_ATT = 0;
    private final int ID_DEF = 1;
    private Village[][] mVillages = null;
    private Hashtable<Integer, Ally> mAllies = null;
    private Hashtable<Integer, Tribe> mTribes = null;
    private List<BuildingHolder> mBuildings = null;
    private List<UnitHolder> mUnits = null;
    private List<DataHolderListener> mListeners = null;
    private boolean bAborted = false;
    private String sServerBaseDir = "./servers";

    public DataHolder() {
        mListeners = new LinkedList<DataHolderListener>();
        initialize();
    }

    public void initialize() {
        mVillages = new Village[1000][1000];
        mAllies = new Hashtable<Integer, Ally>();
        mTribes = new Hashtable<Integer, Tribe>();
        mBuildings = new LinkedList<BuildingHolder>();
        mUnits = new LinkedList<UnitHolder>();
        File serverDir = new File(sServerBaseDir);
        if (!serverDir.exists()) {
            serverDir.mkdir();
        }
    }

    public synchronized void addListener(DataHolderListener pListener) {
        mListeners.add(pListener);
    }

    public synchronized void removeListener(DataHolderListener pListener) {
        mListeners.remove(pListener);
    }

    public String[] getLocalServers() {
        List<String> servers = new LinkedList<String>();
        for (File serverDir : new File(sServerBaseDir).listFiles()) {
            if (serverDir.isDirectory()) {
                System.out.println("Dir " + serverDir.getName());
                servers.add(serverDir.getName());
            }
        }
        return servers.toArray(new String[0]);
    }

    public String getDataDirectory() {
        return sServerBaseDir + "/" + GlobalOptions.getSelectedServer();
    }

    private void abort() {
        bAborted = true;
    }

    /**Check if all needed files are located in the data directory of the selected server*/
    private boolean isDataAvailable() {
        File villages = new File(getDataDirectory() + "/" + "village.txt.gz");
        File tribes = new File(getDataDirectory() + "/" + "tribe.txt.gz");
        File allys = new File(getDataDirectory() + "/" + "ally.txt.gz");
        File killsOff = new File(getDataDirectory() + "/" + "kill_att.txt.gz");
        File killsDef = new File(getDataDirectory() + "/" + "kill_def.txt.gz");
        File units = new File(getDataDirectory() + "/" + "units.xml");
        File buildings = new File(getDataDirectory() + "/" + "buildings.xml");
        File settings = new File(getDataDirectory() + "/" + "settings.xml");

        return (villages.exists() && tribes.exists() && allys.exists() && units.exists() && buildings.exists() && settings.exists());
    }

    /**Check if server is supported or not. Currently only 1000x1000 servers are allowed*/
    public boolean serverSupported() {
        fireDataHolderEvents("Prüfe Server Einstellungen");
        try {
            File settings = new File(getDataDirectory() + "/settings.xml");
            if (settings.exists()) {
                Document d = JaxenUtils.getDocument(settings);
                Integer mapType = Integer.parseInt(JaxenUtils.getNodeValue(d, "//coord/sector"));
                if (mapType != 2) {
                    logger.error("Map type '" + mapType + "' is not supported yet");
                    fireDataHolderEvents("Der gewählte Sever wird leider (noch) nicht unterstützt");
                    return false;
                }
            } else {
                if (GlobalOptions.isOfflineMode()) {
                    fireDataHolderEvents("Servereinstellungen nicht gefunden. Download im Offline-Modus nicht möglich.");
                    return false;
                } else {
                    //download settings.xml
                    URL sURL = ServerList.getServerURL(GlobalOptions.getSelectedServer());
                    new File(GlobalOptions.getDataHolder().getDataDirectory()).mkdirs();
                    fireDataHolderEvents("Lese Server Einstellungen");
                    URL file = new URL(sURL.toString() + "/interface.php?func=get_config");
                    downloadDataFile(file, "settings_tmp.xml");
                    new File("settings_tmp.xml").renameTo(settings);
                    Document d = JaxenUtils.getDocument(settings);
                    Integer mapType = Integer.parseInt(JaxenUtils.getNodeValue(d, "//coord/sector"));
                    if (mapType != 2) {
                        logger.error("Map type '" + mapType + "' is not supported yet");
                        fireDataHolderEvents("Der gewählte Sever wird leider (noch) nicht unterstützt");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check server settings", e);
            return false;
        }

        return true;
    }

    /**Update the data, optionally by downloading*/
    public boolean loadData(boolean pReload) {
        bAborted = false;
        try {
            if (pReload) {
                //completely reload data
                fireDataHolderEvents("Daten werden heruntergeladen...");
                //try to download
                if (!downloadData()) {
                    fireDataHolderEvents("Download abgebrochen/fehlgeschlagen!");
                    return false;
                }
            } else {
                //check if local loading could work
                if (!isDataAvailable()) {
                    logger.error("Local data brocken. Try to download data");
                    fireDataHolderEvents("Lokal gespeicherte Daten sind fehlerhaft. Versuche erneuten Download");
                    if (!downloadData()) {
                        logger.fatal("Download failed. No data available at the moment");
                        fireDataHolderEvents("Download abgebrochen/fehlgeschlagen");
                        return false;
                    }
                } else if (!serverSupported()) {
                    logger.error("Local data available but server not supported");
                    return false;
                }
            }

            String line = "";
            int bytes = 0;
            fireDataHolderEvents("Lese Dörferliste...");
            //read villages
            BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getDataDirectory() + "/village.txt.gz"))));

            while ((line = reader.readLine()) != null) {
                bytes += line.length();
                try {
                    Village v = parseVillage(line);
                } catch (Exception e) {
                    //ignored (should only occur on single villages)
                }
            }

            fireDataHolderEvents("Lese Stämmeliste...");
            //read allies

            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getDataDirectory() + "/ally.txt.gz"))));

            while ((line = reader.readLine()) != null) {
                bytes += line.length();
                try {
                    Ally a = parseAlly(line);
                } catch (Exception e) {
                    //ignored (should only occur on single allies)
                }
            }

            fireDataHolderEvents("Lese Spielerliste...");
            //read tribes
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getDataDirectory() + "/tribe.txt.gz"))));
            while ((line = reader.readLine()) != null) {
                bytes += line.length();
                try {
                    Tribe t = parseTribe(line);
                } catch (Exception e) {
                    //ignored (should only occur on single tribes)
                }
            }

            fireDataHolderEvents("Lese besiegte Gegner (Angriff)...");
            //read tribes
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getDataDirectory() + "/kill_att.txt.gz"))));
            while ((line = reader.readLine()) != null) {
                bytes += line.length();
                try {
                    parseConqueredLine(line, ID_ATT);
                } catch (Exception e) {
                    //ignored (should only occur on single tribes)
                }
            }

            fireDataHolderEvents("Lese besiegte Gegner (Verteidigung)...");
            //read tribes
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(getDataDirectory() + "/kill_def.txt.gz"))));
            while ((line = reader.readLine()) != null) {
                bytes += line.length();
                try {
                    // Tribe t = parseTribe(line);
                    parseConqueredLine(line, ID_DEF);
                } catch (Exception e) {
                    //ignored (should only occur on single tribes)
                }
            }


            fireDataHolderEvents("Kombiniere Daten...");
            mergeData();
            fireDataHolderEvents("Lese Servereinstellungen...");
            parseUnits();
            parseBuildings();
            fireDataHolderEvents("Daten erfolgreich gelesen");
        } catch (Exception e) {
            fireDataHolderEvents("Fehler beim Lesen der Daten.");
            logger.error("Failed to read server data", e);
            if (bAborted) {
                fireDataLoadedEvents();
                return false;
            }
        }

        fireDataLoadedEvents();

        return true;
    }

    /**Download all needed data files (villages, tribes, allies, kills, settings)*/
    private boolean downloadData() {
        URL file = null;
        String serverDir = sServerBaseDir + "/" + GlobalOptions.getSelectedServer();
        new File(serverDir).mkdirs();
        try {
            //check account
            String accountName = GlobalOptions.getProperty("account.name");
            String accountPassword = GlobalOptions.getProperty("account.password");
            if ((accountName == null) || (accountPassword == null)) {
                logger.error("No account name or password set");
                return false;
            }
            if (DatabaseAdapter.checkUser(accountName, accountPassword) != DatabaseAdapter.ID_SUCCESS) {
                logger.error("Failed to validate account (Wrong username or password?)");
                return false;
            }
            if (DatabaseAdapter.isUpdatePossible(accountName, GlobalOptions.getSelectedServer())) {
                logger.info("Update possible, try starting download");
            } else {
                logger.error("Download not yet possible");
                return false;
            }

            //download settings.xml
            URL sURL = ServerList.getServerURL(GlobalOptions.getSelectedServer());

            fireDataHolderEvents("Lese Server Einstellungen");
            File target = new File(serverDir + "/settings.xml");
            if (!target.exists()) {
                file = new URL(sURL.toString() + "/interface.php?func=get_config");
                downloadDataFile(file, "settings_tmp.xml");
                new File("settings_tmp.xml").renameTo(target);
            }

            if (!serverSupported()) {
                return false;
            }

            fireDataHolderEvents("Lade village.txt.gz");
            file = new URL(sURL.toString() + "/map/village.txt.gz");
            downloadDataFile(file, "village_tmp.txt.gz");
            target = new File(serverDir + "/village.txt.gz");
            if (target.exists()) {
                target.delete();
            }
            new File("village_tmp.txt.gz").renameTo(target);

            //download tribe.txt
            fireDataHolderEvents("Lade tribe.txt.gz");
            file = new URL(sURL.toString() + "/map/tribe.txt.gz");
            downloadDataFile(file, "tribe_tmp.txt.gz");
            target = new File(serverDir + "/tribe.txt.gz");
            if (target.exists()) {
                target.delete();
            }
            new File("tribe_tmp.txt.gz").renameTo(target);

            //download ally.txt
            fireDataHolderEvents("Lade ally.txt.gz");
            file = new URL(sURL.toString() + "/map/ally.txt.gz");
            downloadDataFile(file, "ally_tmp.txt.gz");
            target = new File(serverDir + "/ally.txt.gz");
            if (target.exists()) {
                target.delete();
            }
            new File("ally_tmp.txt.gz").renameTo(target);

            //download kill_att.txt
            fireDataHolderEvents("Lade kill_att.txt.gz");
            file = new URL(sURL.toString() + "/map/kill_att.txt.gz");
            downloadDataFile(file, "kill_att_tmp.txt.gz");
            target = new File(serverDir + "/kill_att.txt.gz");
            if (target.exists()) {
                target.delete();
            }
            new File("kill_att_tmp.txt.gz").renameTo(target);

            //download kill_def.txt
            fireDataHolderEvents("Lade kill_def.txt.gz");
            file = new URL(sURL.toString() + "/map/kill_def.txt.gz");
            downloadDataFile(file, "kill_def_tmp.txt.gz");
            target = new File(serverDir + "/kill_def.txt.gz");
            if (target.exists()) {
                target.delete();
            }
            new File("kill_def_tmp.txt.gz").renameTo(target);

            //download unit information, but only once
            target = new File(serverDir + "/units.xml");
            if (!target.exists()) {
                fireDataHolderEvents("Lade Information über Einheiten");
                file = new URL(sURL.toString() + "/interface.php?func=get_unit_info");
                downloadDataFile(file, "units_tmp.xml");

                new File("units_tmp.xml").renameTo(target);
            }

            //download building information, but only once
            target = new File(serverDir + "/buildings.xml");
            if (!target.exists()) {
                fireDataHolderEvents("Lade Information über Gebäude");
                file = new URL(sURL.toString() + "/interface.php?func=get_building_info");
                downloadDataFile(file, "buildings_tmp.xml");
                new File("buildings_tmp.xml").renameTo(target);
            }

            fireDataHolderEvents("Download erfolgreich beendet.");
            DatabaseAdapter.storeLastUpdate(accountName, GlobalOptions.getSelectedServer());
        } catch (Exception e) {
            fireDataHolderEvents("Download fehlgeschlagen.");
            logger.error("Failed to download data", e);
            return false;
        }
        return true;
    }

    /**Merge all data into the village data structure to ease searching*/
    private void mergeData() {
        Enumeration<Integer> tribes = mTribes.keys();
        while (tribes.hasMoreElements()) {
            Tribe current = mTribes.get(tribes.nextElement());
            Ally currentAlly = mAllies.get(current.getAllyID());
            if (currentAlly != null) {
                currentAlly.addTribe(current);
            }
            current.setAlly(currentAlly);
        }

        for (int i = 0; i <
                1000; i++) {
            for (int j = 0; j <
                    1000; j++) {
                Village current = mVillages[i][j];
                if (current != null) {
                    Tribe t = mTribes.get(current.getTribeID());
                    current.setTribe(t);
                    if (t != null) {
                        t.addVillage(current);
                    }
                }
            }
        }
    }

    /**Download one single file from a URL*/
    private void downloadDataFile(URL pSource, String pLocalName) throws Exception {
        URLConnection ucon = pSource.openConnection();
        FileOutputStream tempWriter = new FileOutputStream(pLocalName);
        //BufferedReader reader = new BufferedReader(new InputStreamReader(ucon.getInputStream()));
        InputStream isr = ucon.getInputStream();
        int bytes = 0;
        while (bytes != -1) {
            byte[] data = new byte[1024];
            bytes =
                    isr.read(data);
            if (bytes != -1) {
                tempWriter.write(data, 0, bytes);
            }

        }
        tempWriter.close();
    }

    /**Parse a village*/
    private Village parseVillage(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        Village entry = new Village();
        List<String> entries = new LinkedList();
        if (tokenizer.countTokens() < 7) {
            return null;
        }

        while (tokenizer.hasMoreTokens()) {
            entries.add(tokenizer.nextToken());
        }

        entry.setId(Integer.parseInt(entries.get(0)));
        try {
            String name = URLDecoder.decode(entries.get(1), "UTF-8");
            name =
                    name.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
            entry.setName(name);
        } catch (Exception e) {
            return null;
        }

        entry.setX(Integer.parseInt(entries.get(2)));
        entry.setY(Integer.parseInt(entries.get(3)));
        entry.setTribeID(Integer.parseInt(entries.get(4)));
        entry.setPoints(Integer.parseInt(entries.get(5)));

        //set village type on new servers
        try {
            entry.setType(Integer.parseInt(entries.get(6)));
        } catch (Exception e) {
        }
        mVillages[entry.getX()][entry.getY()] = entry;
        return entry;
    }

    /**Parse an ally*/
    private Ally parseAlly(String line) {
        //$id, $name, $tag, $members, $villages, $points, $all_points, $rank
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        Ally entry = new Ally();
        List<String> entries = new LinkedList();
        if (tokenizer.countTokens() < 8) {
            return null;
        }

        while (tokenizer.hasMoreTokens()) {
            entries.add(tokenizer.nextToken());
        }

        entry.setId(Integer.parseInt(entries.get(0)));
        try {
            entry.setName(URLDecoder.decode(entries.get(1), "UTF-8"));
            entry.setTag(URLDecoder.decode(entries.get(2), "UTF-8"));
        } catch (Exception e) {
            return null;
        }

        entry.setMembers(Integer.parseInt(entries.get(3)));
        entry.setVillages(Integer.parseInt(entries.get(4)));
        entry.setPoints(Integer.parseInt(entries.get(5)));
        entry.setAll_points(Integer.parseInt(entries.get(6)));
        entry.setRank(Integer.parseInt(entries.get(7)));
        mAllies.put(entry.getId(), entry);
        return entry;
    }

    /**Parse a tribe*/
    private Tribe parseTribe(String line) {
        //$id, $name, $ally, $villages, $points, $rank
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        Tribe entry = new Tribe();
        List<String> entries = new LinkedList();
        if (tokenizer.countTokens() < 6) {
            return null;
        }

        while (tokenizer.hasMoreTokens()) {
            entries.add(tokenizer.nextToken());
        }

        entry.setId(Integer.parseInt(entries.get(0)));
        try {
            entry.setName(URLDecoder.decode(entries.get(1), "UTF-8"));
        } catch (Exception e) {
            return null;
        }

        entry.setAllyID(Integer.parseInt(entries.get(2)));
        entry.setVillages(Integer.parseInt(entries.get(3)));
        entry.setPoints(Integer.parseInt(entries.get(4)));
        entry.setRank(Integer.parseInt(entries.get(5)));
        mTribes.put(entry.getId(), entry);
        return entry;
    }

    private void parseConqueredLine(String pLine, int pType) {
        StringTokenizer tokenizer = new StringTokenizer(pLine, ",");
        try {
            String rank = tokenizer.nextToken();
            String tribeID = tokenizer.nextToken();
            String kills = tokenizer.nextToken();
            Tribe t = getTribes().get(Integer.parseInt(tribeID));
            if (pType == ID_ATT) {
                t.setKillsAtt(Integer.parseInt(kills));
                t.setRankAtt(Integer.parseInt(rank));
            }else{
                t.setKillsDef(Integer.parseInt(kills));
                t.setRankDeff(Integer.parseInt(rank));
            }
        } catch (Exception e) {
            //sth went wrong with the current kill entry, ignore it
        }
    }

    /**Parse the list of units*/
    private void parseUnits() {
        String buildingsFile = sServerBaseDir + "/" + GlobalOptions.getSelectedServer();
        buildingsFile +=
                "/units.xml";
        try {
            Document d = JaxenUtils.getDocument(new File(buildingsFile));
            d =
                    JaxenUtils.getDocument(new File(buildingsFile));
            List<Element> l = JaxenUtils.getNodes(d, "/config/*");
            for (Element e : l) {
                try {
                    mUnits.add(new UnitHolder(e));
                } catch (Exception inner) {
                }
            }
        } catch (Exception outer) {
            logger.error("Failed to load units", outer);
            fireDataHolderEvents("Laden der Einheiten fehlgeschlagen");
        }

    }

    /**Parse the list of buildings*/
    public void parseBuildings() {
        String buildingsFile = sServerBaseDir + "/" + GlobalOptions.getSelectedServer();
        buildingsFile +=
                "/buildings.xml";
        try {
            Document d = JaxenUtils.getDocument(new File(buildingsFile));
            d =
                    JaxenUtils.getDocument(new File(buildingsFile));
            List<Element> l = JaxenUtils.getNodes(d, "/config/*");
            for (Element e : l) {
                try {
                    mBuildings.add(new BuildingHolder(e));
                } catch (Exception inner) {
                }
            }
        } catch (Exception outer) {
            logger.error("Failed to load buildings", outer);
            fireDataHolderEvents("Laden der Gebäude fehlgeschlagen");
        }

    }

    public Village[][] getVillages() {
        return mVillages;
    }

    public Hashtable<Integer, Ally> getAllies() {
        return mAllies;
    }

    public Ally getAllyByName(
            String pName) {
        Enumeration<Integer> ids = getAllies().keys();
        while (ids.hasMoreElements()) {
            Ally a = getAllies().get(ids.nextElement());
            if (a != null) {
                if (a.getName() != null) {
                    if (a.getName().equals(pName)) {
                        return a;
                    }

                }
            }
        }
        return null;
    }

    public Hashtable<Integer, Tribe> getTribes() {
        return mTribes;
    }

    public Tribe getTribeByName(
            String pName) {
        Enumeration<Integer> ids = getTribes().keys();
        while (ids.hasMoreElements()) {
            Tribe t = getTribes().get(ids.nextElement());
            if (t != null) {
                if (t.getName() != null) {
                    if (t.getName().equals(pName)) {
                        return t;
                    }

                }
            }
        }
        return null;
    }

    public List<UnitHolder> getUnits() {
        return mUnits;
    }

    public int getUnitID(String pUnitName) {
        int result = -1;
        int cnt = 0;
        for (UnitHolder unit : mUnits) {
            if (unit.getName().equals(pUnitName)) {
                result = cnt;
                break;

            } else {
                cnt++;
            }

        }
        return result;
    }

    public List<BuildingHolder> getBuildings() {
        return mBuildings;
    }

    public synchronized void fireDataHolderEvents(String pMessage) {
        for (DataHolderListener listener : mListeners) {
            listener.fireDataHolderEvent(pMessage);
        }

    }

    public synchronized void fireDataLoadedEvents() {
        for (DataHolderListener listener : mListeners) {
            listener.fireDataLoadedEvent();
        }
    }
}