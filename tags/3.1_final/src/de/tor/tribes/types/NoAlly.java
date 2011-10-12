/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import java.text.NumberFormat;

/**
 *
 * @author Jejkal
 */
public class NoAlly extends Ally {

    private static NoAlly SINGLETON = null;

    public static synchronized NoAlly getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new NoAlly();
        }
        return SINGLETON;
    }

    public int getId() {
        return -1;
    }

    public String getName() {
        return "Kein Stamm";
    }

    public String getTag() {
        return "-";
    }

    public short getMembers() {
        return 0;
    }

    @Override
    public double getPoints() {
        return 0;
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public String toString() {
        return "Kein Stamm";
    }

    public String getToolTipText() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        String res = "<html><table style='border: solid 1px black; cellspacing:0px;cellpadding: 0px;background-color:#EFEBDF;'>";
        res += "<tr><td><b>Stamm:</b> </td><td>" + toString() + "</td></tr>";
        res += "</table></html>";
        return res;
    }
}