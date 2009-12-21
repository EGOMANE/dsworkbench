/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.types.Tribe;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.ui.DSWorkbenchSettingsDialog;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 *
 * @author Torridity
 */
public class IGMSender {

    private static Logger logger = Logger.getLogger("IGMSender");

    public static boolean sendIGM(Tribe pReceiver, String pApiKey, String pSubject, String pMessage) {
        Tribe t = DSWorkbenchMainFrame.getSingleton().getCurrentUser();
        try {
            String why = URLEncoder.encode(pSubject, "UTF-8");
            String name = URLEncoder.encode(pReceiver.getName(), "UTF-8");
            String text = URLEncoder.encode(pMessage, "UTF-8");
            String get = "http://de43.die-staemme.de/send_mail.php?from_id=" + t.getId() + "&api_key=" + pApiKey;
            get += "&to=" + name + "&subject=" + why + "&message=" + text;
            URL u = new URL(get);
            URLConnection ucon = u.openConnection(DSWorkbenchSettingsDialog.getSingleton().getWebProxy());
            String res = ucon.getHeaderField(0);
            byte[] data = new byte[1024];
            int read = 0;
            String returnString = "";
            while (read != -1) {
                read = ucon.getInputStream().read(data);
                returnString += new String(data).trim();
                data = new byte[1024];
            }

            boolean headerCorrect = res.endsWith("200 OK");
            if (!headerCorrect) {
                throw new Exception("Invalid HTTP header returned (" + res + ")");
            }
            boolean returnStringCorrect = returnString.startsWith("Nachricht erfolgreich");
            if (!returnStringCorrect) {
                throw new Exception("Invalid API response returned ('" + returnString + "')");
            }
            return (headerCorrect && returnStringCorrect);
        } catch (Exception e) {
            logger.error("Failed to send IGM", e);
            return false;
        }
    }

    public static boolean sendIGM(String pReceiver, String pApiKey, String pSubject, String pMessage) {

        try {
            String text = URLEncoder.encode(pSubject, "UTF-8");
            String name = URLEncoder.encode(pReceiver, "UTF-8");
            String why = URLEncoder.encode(pMessage, "UTF-8");
            String apiKey = pApiKey;
            String get = "http://de43.die-staemme.de/send_mail.php?from_id=3457919&api_key=" + apiKey;
            get += "&to=" + name + "&subject=" + why + "&message=" + text;
            URL u = new URL(get);
            Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.fzk.de", 8000));
            URLConnection ucon = u.openConnection(p);
            String res = ucon.getHeaderField(0);
            byte[] data = new byte[1024];
            int read = 0;
            String returnString = "";
            while (read != -1) {
                read = ucon.getInputStream().read(data);
                returnString += new String(data).trim();
                data = new byte[1024];
            }

            System.out.println(returnString);
            return (returnString.startsWith("Nachricht erfolgreich"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(IGMSender.sendIGM("Rattenfutter", "64ef1ebccdfd2b1e345a3514f2569419420b024c", "Test", "test"));
    }
}
