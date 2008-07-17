/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.String;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author Jejkal
 */
public class IncServerSync {

    private static Logger logger = Logger.getLogger(IncServerSync.class);
    /**Full update every week*/
    private final static long FULL_UPDATE_INTERVAL = 1000 * 60 * 60 * 24 * 7;

    private static void createDiff(URL pRemoteFile, String pLocalFile) throws Exception {
        logger.info("Creating diff between " + pRemoteFile + " and " + pLocalFile);
        try {
            BufferedReader remoteReader = new BufferedReader(new InputStreamReader(new GZIPInputStream((pRemoteFile.openStream()))));
            BufferedReader localReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(pLocalFile))));

            StringBuffer diffBuffer = new StringBuffer();
            String remoteLine = "";
            int parsedLines = 0;
            int changedTokens = 0;
            int newLines = 0;
            int unchangedLines = 0;
            while ((remoteLine = remoteReader.readLine()) != null) {
                String localLine = localReader.readLine();
                if (localLine != null) {
                    //local and remote line available
                    if (localLine.equals(remoteLine)) {
                        //no change
                        diffBuffer.append("\n");
                        unchangedLines++;
                    } else {
                        //changes found, check tokens
                        StringTokenizer remoteTokens = new StringTokenizer(remoteLine, ",");
                        StringTokenizer localTokens = new StringTokenizer(localLine, ",");
                        if (remoteTokens.countTokens() == localTokens.countTokens()) {
                            //only parse tokens if the format of both files is equal
                            while (remoteTokens.hasMoreTokens()) {
                                String remoteToken = remoteTokens.nextToken();
                                String localToken = localTokens.nextToken();
                                if (localToken.equals(remoteToken)) {
                                    //local and remote tokens are equal, so don't change
                                    diffBuffer.append(",");
                                } else {
                                    //local and remote token are different, so insert the new value
                                    diffBuffer.append(remoteToken);
                                    diffBuffer.append(",");
                                    changedTokens++;
                                }
                            }
                            diffBuffer.append("\n");
                        } else {
                            logger.warn("Local and remote file seem to have a different format (#LocalTokens (" + localTokens.countTokens() + ") != #RemoteTokens (" + remoteTokens.countTokens() + "). Current line skipped");
                            diffBuffer.append("\n");
                        }
                    }
                } else {
                    //only remote line available -> complete new data
                    diffBuffer.append(remoteLine);
                    diffBuffer.append("\n");
                    newLines++;
                }
                parsedLines++;
            }

            logger.info("===Start of diff statistics for " + pLocalFile + "===");
            logger.info("File size: " + new File(pLocalFile).length() + " bytes");
            logger.info("Parsed lines: " + parsedLines);
            logger.info("Unchanged lines: " + unchangedLines);
            logger.info("New lines: " + newLines);
            logger.info("Changed tokens: " + changedTokens);
            logger.info("Diff size: " + diffBuffer.toString().length() + " bytes");
            logger.info("===End of diff statistics===");


            String diffFile = pLocalFile.substring(0, pLocalFile.indexOf(".")) + ".diff";
            logger.info("Writing diff to " + diffFile);
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(new File(diffFile)));
            out.write(diffBuffer.toString().getBytes());
            out.finish();
        } catch (Exception e) {
            logger.error("Failed to create diff from URL " + pRemoteFile + " for local file " + pLocalFile, e);
        }
    }

    private static void updateServerData(String pServerURL, String pServerDir, boolean pIncremental) throws Exception {
        if (!pIncremental) {
            logger.info("Removing old diffs");
            new File(pServerDir + "/village.diff").delete();
            new File(pServerDir + "/tribe.diff").delete();
            new File(pServerDir + "/ally.diff").delete();
            new File(pServerDir + "/kill_att.diff").delete();
            new File(pServerDir + "/kill_def.diff").delete();
            logger.info("Performing full update");
            downloadDataFile(new URL(pServerURL + "/map/village.txt.gz"), pServerDir + "/village.txt.gz");
            downloadDataFile(new URL(pServerURL + "/map/tribe.txt.gz"), pServerDir + "/tribe.txt.gz");
            downloadDataFile(new URL(pServerURL + "/map/ally.txt.gz"), pServerDir + "/ally.txt.gz");
            downloadDataFile(new URL(pServerURL + "/map/kill_att.txt.gz"), pServerDir + "/kill_att.txt.gz");
            downloadDataFile(new URL(pServerURL + "/map/kill_def.txt.gz"), pServerDir + "/kill_def.txt.gz");
        } else {
            if (!new File(pServerDir + "/village.txt.gz").exists()) {
                logger.info(pServerDir + "/village.txt.gz does not exist yet. Downloading file");
                downloadDataFile(new URL(pServerURL + "/map/village.txt.gz"), pServerDir + "/village.txt.gz");
            } else {
                createDiff(new URL(pServerURL + "/map/village.txt.gz"), pServerDir + "/village.txt.gz");
            }
            if (!new File(pServerDir + "/tribe.txt.gz").exists()) {
                logger.info(pServerDir + "/tribe.txt.gz does not exist yet. Downloading file");
                downloadDataFile(new URL(pServerURL + "/map/tribe.txt.gz"), pServerDir + "/tribe.txt.gz");
            } else {
                createDiff(new URL(pServerURL + "/map/tribe.txt.gz"), pServerDir + "/tribe.txt.gz");
            }
            if (!new File(pServerDir + "/ally.txt.gz").exists()) {
                logger.info(pServerDir + "/ally.txt.gz does not exist yet. Downloading file");
                downloadDataFile(new URL(pServerURL + "/map/ally.txt.gz"), pServerDir + "/ally.txt.gz");
            } else {
                createDiff(new URL(pServerURL + "/map/ally.txt.gz"), pServerDir + "/ally.txt.gz");
            }
            if (!new File(pServerDir + "/kill_att.txt.gz").exists()) {
                logger.info(pServerDir + "/kill_att.txt.gz does not exist yet. Downloading file");
                downloadDataFile(new URL(pServerURL + "/map/kill_att.txt.gz"), pServerDir + "/kill_att.txt.gz");
            } else {
                createDiff(new URL(pServerURL + "/map/kill_att.txt.gz"), pServerDir + "/kill_att.txt.gz");
            }
            if (!new File(pServerDir + "/kill_def.txt.gz").exists()) {
                logger.info(pServerDir + "/kill_def.txt.gz does not exist yet. Downloading file");
                downloadDataFile(new URL(pServerURL + "/map/kill_def.txt.gz"), pServerDir + "/kill_def.txt.gz");
            } else {
                createDiff(new URL(pServerURL + "/map/kill_def.txt.gz"), pServerDir + "/kill_def.txt.gz");
            }
        }
    }

    /* private static void testDiff() throws Exception {
    createDiff(new File("D:/GRID/src/DSWorkbench/servers/de26/village_new.txt.gz").toURI().toURL(), "D:/GRID/src/DSWorkbench/servers/de26/village.txt.gz");
    }*/
    private static void downloadDataFile(URL pSource, String pLocalName) throws Exception {
        logger.info("Downloading " + pSource);
        URLConnection ucon = pSource.openConnection();
        FileOutputStream tempWriter = new FileOutputStream(pLocalName);
        InputStream isr = ucon.getInputStream();
        int bytes = 0;
        while (bytes != -1) {
            byte[] data = new byte[1024];
            bytes = isr.read(data);
            if (bytes != -1) {
                tempWriter.write(data, 0, bytes);
            }
        }
        try {
            isr.close();
            tempWriter.close();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        DOMConfigurator.configure("log4j.xml");
        /* try {
        //testDiff();
        BufferedReader b = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("c:/village.diff"))));
        String l = "";
        int cnt = 0;
        while ((l = b.readLine()) != null) {
        cnt++;
        
        }
        System.out.println("diff " + cnt);
        } catch (Exception e) {
        e.printStackTrace();
        }
        if (true) {
        return;
        }*/


        if (args.length < 1) {
            logger.error("args length is smaller 1");
            System.out.println("Usage");
            System.out.println("IncServerSync <sync_properties>");
            System.exit(1);
        }
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(args[0]));
        } catch (Exception e) {
            logger.error("Failed to read properties file '" + args[0] + "'", e);
            System.out.println("Failed to load properties file from '" + args[0] + "'");
            System.exit(1);
        }

        //use proxy settings if available
        System.setProperty("proxyUse", (p.getProperty("proxyUse") != null) ? p.getProperty("proxyUse") : "");
        System.setProperty("proxyHost", (p.getProperty("proxyHost") != null) ? p.getProperty("proxyHost") : "");
        System.setProperty("proxyPort", (p.getProperty("proxyPort") != null) ? p.getProperty("proxyPort") : "");
        /* System.setProperty("proxyUse", "true");
        System.setProperty("proxyHost", "proxy.fzk.de");
        System.setProperty("proxyPort", "8000");
        try {
        URL u = new URL("http://www.torridity.de/servers/de14/test.diff");
        u.openStream();
        System.out.println("OPEN");
        } catch (Exception e) {
        System.out.println("ERROR");
        e.printStackTrace();
        }
        if (true) {
        return;
        }*/
        
        //get server base dir
        String baseDir = p.getProperty("server.base.dir");
        if (!new File(baseDir).exists()) {
            logger.info("Base dir '" + baseDir + "' does not exist. Creating new.");
            if (!new File(baseDir).mkdirs()) {
                logger.info("Failed to create base dir '" + baseDir + "'.");
                System.exit(1);
            }
        }

        logger.info("Using base dir " + baseDir);
        long lastFullUpdate = 0;
        try {
            lastFullUpdate = Long.parseLong(p.getProperty("last.full.update"));
        } catch (NumberFormatException nfe) {
            //ignored
        }

        String serverList = baseDir + "/server.list";
        if (!new File(serverList).exists()) {
            logger.error("'server.list' not found under " + serverList);
            System.exit(1);
        }
        Properties servers = new Properties();
        try {
            servers.load(new FileInputStream(serverList));
        } catch (Exception e) {
            logger.error("Failed to load 'server.list' from " + serverList);
            System.exit(1);
        }
        Enumeration elems = servers.propertyNames();
        boolean fullUpdate = false;

        logger.info("Starting data update");
        while (elems.hasMoreElements()) {
            String serverID = (String) elems.nextElement();
            String serverURL = (String) servers.get(serverID);
            String serverDir = baseDir + "/" + serverID;
            if (new File(serverDir).exists()) {
                //server was already updated, check for inc update
                if (lastFullUpdate + FULL_UPDATE_INTERVAL < System.currentTimeMillis()) {
                    //inc update
                    try {
                        IncServerSync.updateServerData(serverURL, serverDir, true);
                    } catch (Exception e) {
                        logger.error("Incremental update for serverID '" + serverID + "' failed", e);
                    }
                } else {
                    //full update
                    fullUpdate = true;
                    try {
                        IncServerSync.updateServerData(serverURL, serverDir, false);
                    } catch (Exception e) {
                        logger.error("Full update for serverID '" + serverID + "' failed", e);
                    }
                }
            } else {
                //server does not exist yet, create and perform full update
                if (!new File(serverDir).mkdirs()) {
                    logger.error("Failed to create server directory for serverID '" + serverID + "' under " + serverDir);
                } else {
                    try {
                        IncServerSync.updateServerData(serverURL, serverDir, false);
                    } catch (Exception e) {
                        logger.error("Initial download for serverID '" + serverID + "' failed", e);
                    }
                }
            }
        }
        if (fullUpdate) {
            p.put("last.full.update", Long.toString(System.currentTimeMillis()));
            try {
                p.store(new FileOutputStream("sync.properties"), null);
            } catch (Exception e) {
                logger.error("Failed to update sync.properties", e);
            }
        }

        logger.info("Data updated");
    }
}