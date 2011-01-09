/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.DSWorkbenchMerchantDistibutor.Transport;
import de.tor.tribes.util.attack.StandardAttackManager;
import java.awt.Desktop;
import java.net.URI;
import org.apache.log4j.Logger;

/**
 * http://de8.die-staemme.de/game.php?t=743256&village=269739&screen=place
 * http://de8.die-staemme.de/game.php?t=743256&village=273090&screen=place&mode=command&target=285904
 * @author Charon
 */
public class BrowserCommandSender {

    private static Logger logger = Logger.getLogger("BrowserInterface");

    public static boolean sendAttack(Attack pAttack) {
        boolean result = false;
        if (pAttack != null) {
            result = sendTroops(pAttack.getSource(), pAttack.getTarget(), pAttack.getType());
            pAttack.setTransferredToBrowser(result);
        }
        return result;
    }

    private static boolean sendTroops(Village pSource, Village pTarget, int pType) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            logger.debug("Transfer troops to browser for village '" + pSource + "' to '" + pTarget + "' with type '" + pType + "'");
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=place&mode=command&target=" + pTarget.getId();
            url += "&type=0";
            for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
                int amount = StandardAttackManager.getSingleton().getAmountForVillage(pType, unit, pSource);
                url += "&" + unit.getPlainName() + "=" + amount;
            }
            url += "&ts=" + System.currentTimeMillis();
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }

            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
            return false;
        }
        return true;
    }

    public static void sendRes(Village pSource, Village pTarget, Transport pTrans) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            logger.debug("Transfer resources to browser for village '" + pSource + "' to '" + pTarget);
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=market&mode=send&target=" + pTarget.getId();
            url += "&type=1";
            url += "&wood=" + pTrans.getSingleTransports().get(0).getAmount();
            url += "&clay=" + pTrans.getSingleTransports().get(1).getAmount();
            url += "&iron=" + pTrans.getSingleTransports().get(2).getAmount();
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }
            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
        }
    }

    public static boolean sendTroops(Village pSource, Village pTarget) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            logger.debug("Transfer troops to browser for ville '" + pSource + "' to '" + pTarget + "'");
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=place&mode=command&target=" + pTarget.getId();
            url += "&ts=" + System.currentTimeMillis();
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }

            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
            return false;
        }
        return true;
    }

    public static void openPage(String pUrl) {
        String browser = GlobalOptions.getProperty("default.browser");
        if (browser == null || browser.length() < 1) {
            try {
                Desktop.getDesktop().browse(new URI(pUrl));
            } catch (Throwable t) {
                logger.error("Failed opening URL " + pUrl);
            }
        } else {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{browser, pUrl});
                p.waitFor();
            } catch (Throwable t) {
                logger.error("Failed opening URL " + pUrl);
            }
        }
        try {
            Thread.sleep(10);
        } catch (Exception ignored) {
        }
    }

    public static boolean openTestPage(String pUrl) {
        String browser = GlobalOptions.getProperty("default.browser");
        if (browser == null || browser.length() < 1) {
            try {
                Desktop.getDesktop().browse(new URI(pUrl));
            } catch (Throwable t) {
                logger.error("Failed opening URL " + pUrl);
                return false;
            }
        } else {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{browser, pUrl});
                p.waitFor();
            } catch (Throwable t) {
                logger.error("Failed opening URL " + pUrl);
                return false;
            }
        }
        try {
            Thread.sleep(10);
        } catch (Exception ignored) {
        }
        return true;
    }

    public static void openPlaceTroopsView(Village pSource) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=place&mode=units";
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }
            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
        }
    }

    public static void centerVillage(Village pSource) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=map&x=" + pSource.getX() + "&y=" + pSource.getY();
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }
            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
        }
    }

    public static void centerCoordinate(int pX, int pY) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += "&screen=map&x=" + pX + "&y=" + pY;
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }

            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
        }
    }

    public static void sendRes(Village pSource, Village pTarget) {
        try {
            String baseURL = ServerManager.getServerURL(GlobalOptions.getSelectedServer());
            String url = baseURL + "/game.php?village=";
            int uvID = GlobalOptions.getSelectedProfile().getUVId();
            if (uvID >= 0) {
                url = baseURL + "/game.php?t=" + uvID + "&village=";
            }
            url += pSource.getId() + "&screen=market&mode=send&target=" + pTarget.getId();
            String browser = GlobalOptions.getProperty("default.browser");
            if (browser == null || browser.length() < 1) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Process p = Runtime.getRuntime().exec(new String[]{browser, url});
                p.waitFor();
            }

            try {
                Thread.sleep(10);
            } catch (Exception ignored) {
            }
        } catch (Throwable t) {
            JOptionPaneHelper.showErrorBox(null, "Fehler beim Öffnen des Browsers", "Fehler");
            logger.error("Failed to open browser window", t);
        }
    }
}
