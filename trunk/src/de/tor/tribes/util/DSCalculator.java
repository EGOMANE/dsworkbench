/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.types.Village;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Jejkal
 */
public class DSCalculator {

    public static double calculateDistance(Village pSource, Village pTarget) {
        if ((pSource == null) || (pTarget == null)) {
            return 0;
        }
        return Math.sqrt(Math.pow(pTarget.getX() - pSource.getX(), 2) + Math.pow(pTarget.getY() - pSource.getY(), 2));
    }

    public static double calculateMoveTimeInMinutes(Village pSource, Village pTarget, double pMinPerField) {
        return calculateDistance(pSource, pTarget) * pMinPerField;
    }

    public static double calculateMoveTimeInSeconds(Village pSource, Village pTarget, double pMinPerField) {
        return calculateDistance(pSource, pTarget) * pMinPerField * 60.0;
    }

    public static double calculateMoveTimeInHours(Village pSource, Village pTarget, double pMinPerField) {
        return calculateDistance(pSource, pTarget) * pMinPerField / 60.0;
    }

    public static Point calculateCenterOfMass(List<Village> pVillages) {
        double mass = pVillages.size();
        double xMass = 0;
        double yMass = 0;
        for (Village v : pVillages) {
            xMass += v.getX();
            yMass += v.getY();
        }
        xMass = Math.rint(xMass / mass);
        yMass = Math.rint(yMass / mass);
        return new Point((int) xMass, (int) yMass);
    }

    public static int[] xyToHierarchical(int x, int y) {
        if (Math.abs(x) > 499 || Math.abs(y) > 499) {
            return null; // out of range
        }
        x *= 2;
        y *= 2;
        int con = (int) (Math.floor(y / 100) * 10 + Math.floor(x / 100));
        int sec = (int) ((Math.floor(y / 10) % 10) * 10 + (Math.floor(x / 10) % 10));
        int sub = (int) ((y % 10) * 2.5 + (x % 10) / 2);
        return new int[]{con, sec, sub};
    }

    public static int[] hierarchicalToXy(int con, int sec, int sub) {
        if (con < 0 || con > 99 || sec < 0 || sec > 99 || sub < 0 || sub > 24) {
            return null; // invalid s3-coords
        }
        int x = (con % 10) * 50 + (sec % 10) * 5 + (sub % 5);
        int y = (int) (Math.floor(con / 10) * 50 + Math.floor(sec / 10) * 5 + Math.floor(sub / 5));
        return new int[]{x, y};
    }

    public static String formatTimeInMinutes(double pTime) {
        double dur = pTime;
        int hour = (int) Math.floor(dur / 60);
        dur -= hour * 60;
        int min = (int) Math.floor(dur);
        int sec = (int) Math.rint((dur - min) * 60);
        if (sec == 60) {
            min++;
            sec -= 60;
        }
        String result = "";
        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (sec < 10) {
            result += "0" + sec;
        } else {
            result += sec;
        }
        return result;
    }
}
