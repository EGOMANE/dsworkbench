/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.types.SOSRequest;
import de.tor.tribes.types.Village;
import de.tor.tribes.types.VillageMerchantInfo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Torridity
 */
public class PluginManager {

    private static Logger logger = Logger.getLogger("PluginManager");
    private static PluginManager SINGLETON = null;
    private Properties mPluginVersions = null;
    private final File PROPERTIES_FILE = new File("plugin.version");
    private final File PLUGIN_DIR = new File("./plugins");
    private boolean INITIALIZED = false;
    private URLClassLoader mClassloader = null;
    private Object mVariableManager = null;

    public static synchronized PluginManager getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new PluginManager();
        }
        return SINGLETON;
    }

    PluginManager() {
        mPluginVersions = new Properties();
        if (PROPERTIES_FILE.exists()) {
            try {
                mPluginVersions.load(new FileInputStream(PROPERTIES_FILE));
                INITIALIZED = true;
            } catch (Exception e) {
                logger.warn("Failed to read file 'plugin.version'. Re-creating it...");
                try {
                    if (PROPERTIES_FILE.createNewFile()) {
                        logger.debug("File 'plugin.version' created successfully");
                    } else {
                        throw new Exception("createNewFile() returned 'false'");
                    }
                } catch (Exception inner) {
                    logger.error("Failed to create file 'plugin.version'", inner);
                }
            }
        }

        PLUGIN_DIR.mkdir();
        initializeClassloader();
    }

    private void initializeClassloader() {
        List<URL> urls = new LinkedList<URL>();
        int urlCount = 0;
        int fileCount = PLUGIN_DIR.listFiles().length;
        for (File f : PLUGIN_DIR.listFiles()) {
            try {
                urls.add(f.toURI().toURL());
                urlCount++;
            } catch (Exception e) {
                logger.error("Failed to create URL for file '" + f.getPath() + "'");
            }
        }

        //create the new classloader
        mClassloader = new URLClassLoader(urls.toArray(new URL[]{}), ClassLoader.getSystemClassLoader());
        if (urlCount == fileCount) {
            logger.info("Created classloader for " + urlCount + " plugins");
        } else {
            logger.warn("Created classloader for " + urlCount + "/" + fileCount + " plugins");
        }
    }

    /**Execute the village parser plugin
     * @param pData The text that contains village coordinates
     * @return List<Village> Parsed village list
     */
    public List<Village> executeVillageParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.VillageParser");
            return ((GenericParserInterface<Village>) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute merchant parser", e);
        }
        return new LinkedList<Village>();
    }

    /**Execute the merchant parser plugin
     * @param pData The text that contains merchant infos
     * @return List<VillageMerchantInfo> Parsed list of merchant infos
     */
    public List<VillageMerchantInfo> executeMerchantParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.MerchantParser");
            return ((GenericParserInterface<VillageMerchantInfo>) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute merchant parser", e);
        }
        return new LinkedList<VillageMerchantInfo>();
    }

    public boolean executeSupportParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.SupportParser");
            return ((SilentParserInterface) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute support parser", e);
        }
        return false;
    }

    public boolean executeGroupParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.GroupParser");
            return ((SilentParserInterface) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute group parser", e);
        }
        return false;
    }

    public boolean executeNonPAPlaceParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.NonPAPlaceParser");
            return ((SilentParserInterface) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute place parser", e);
        }
        return false;
    }

    public boolean executeReportParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.ReportParser");
            return ((SilentParserInterface) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute report parser", e);
        }
        return false;
    }

    public List<SOSRequest> executeSOSParserParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.SOSParser");
            return ((GenericParserInterface<SOSRequest>) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute sos request parser", e);
        }
        return new LinkedList<SOSRequest>();
    }

    public boolean executeTroopsParser(String pData) {
        try {
            Object parser = loadParser("de.tor.tribes.util.parser.TroopsParser");
            return ((SilentParserInterface) parser).parse(pData);
        } catch (Exception e) {
            logger.error("Failed to execute troops parser", e);
        }
        return false;
    }

    public String getVariableValue(String pName) {
        if (mVariableManager == null) {
            try {
                Class managerClass = mClassloader.loadClass("de.tor.tribes.util.parser.ParserVariableManager");
                 mVariableManager = managerClass.getMethod("getSingleton").invoke(null);
            } catch (Exception e) {
                logger.error("Failed to load parser variable manager", e);
            }
        }
        try {
            return (String) mVariableManager.getClass().getMethod("getProperty", String.class).invoke(mVariableManager, pName);
        } catch (Exception e) {
            logger.error("Failed to execute method getProperty() on parser variable manager", e);
            return "";
        }
    }

    private Object loadParser(String pClazz) throws Exception {
        return mClassloader.loadClass(pClazz).newInstance();
    }

    /**Write the plugin properties to disk*/
    private void storePropertyFile() throws Exception {
        mPluginVersions.store(new FileOutputStream(PROPERTIES_FILE), "Please do not modify!");
    }

    /**Check for plugin updates
     * @throws Exception If the entire update fails
     */
    public void checkForUpdates() throws Exception {
        //try to get properties
        //URLConnection connection = new URL("http://www.dsworkbench.de/downloads/plugins/plugin.version").openConnection(DSWorkbenchSettingsDialog.getSingleton().getWebProxy());
        URLConnection connection = new URL("http://www.dsworkbench.de/downloads/plugins/plugin.version").openConnection();
        Properties props = new Properties();
        props.load(connection.getInputStream());
        if (!INITIALIZED) {
            //properties not yet loaded, set versions from server
            mPluginVersions = new Properties();
        }

        //perform updates
        downloadVersionUpdates(props);
        //initialized
        INITIALIZED = true;
        initializeClassloader();
    }

    /**Update all plugins
     * @param pProperties Plugin versions properties file from server
     */
    private boolean downloadVersionUpdates(Properties pProperties) {
        Enumeration<Object> keys = pProperties.keys();

        int updateRequired = 0;
        int updateCount = 0;
        List<String> failedUpdates = new LinkedList<String>();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (mPluginVersions.get(key) == null
                    || Double.parseDouble(mPluginVersions.getProperty(key)) != Double.parseDouble(pProperties.getProperty(key))
                    || !new File("./plugins/" + key + ".jar").exists()) {
                updateRequired++;
                if (downloadPlugin(key, pProperties.getProperty(key))) {
                    updateCount++;
                    mPluginVersions.put(key, pProperties.getProperty(key));
                } else {
                    failedUpdates.add(key);
                }
            }
        }

        if (failedUpdates.isEmpty()) {
            logger.info("Performed " + updateCount + "/" + updateRequired + " updates successfully");
        } else {
            logger.warn("Performed " + updateCount + "/" + updateRequired + " updates successfully. Failed updates: " + failedUpdates);
        }

        try {
            storePropertyFile();
            logger.debug("File 'plugin.version' successfully saved");
        } catch (Exception e) {
            logger.error("Failed to write file 'plugin.version'");
        }
        return true;
    }

    /**Download one single plugin
     * @param pPluginName Name of the plugin
     * @param pVersion Plugin version
     */
    private boolean downloadPlugin(String pPluginName, String pVersion) {
        try {
            //URLConnection con = new URL("http://www.dsworkbench.de/downloads/plugins/" + pPluginName + "/" + pPluginName + pVersion + ".jar").openConnection(DSWorkbenchSettingsDialog.getSingleton().getWebProxy());
            URLConnection con = new URL("http://www.dsworkbench.de/downloads/plugins/" + pPluginName + pVersion + ".jar").openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(20000);

            InputStream isr = con.getInputStream();
            FileOutputStream tempWriter = new FileOutputStream("./plugins/" + pPluginName + ".jar");
            int bytes = 0;
            byte[] data = new byte[1024];
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            int sum = 0;
            while (bytes != -1) {
                if (bytes != -1) {
                    result.write(data, 0, bytes);
                }

                bytes = isr.read(data);
                sum += bytes;
                if (sum % 500 == 0) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                    }
                }
            }

            tempWriter.write(result.toByteArray());
            tempWriter.flush();
            try {
                isr.close();
            } catch (Exception e) {
            }
            try {
                tempWriter.close();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            logger.error("Failed to update plugin '" + pPluginName + "' to version " + pVersion, e);
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        PluginManager.getSingleton().checkForUpdates();
    }
}
