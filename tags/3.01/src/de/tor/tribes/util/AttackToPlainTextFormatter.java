/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.Barbarians;
import de.tor.tribes.types.Village;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jejkal
 */
public class AttackToPlainTextFormatter {

    public static String formatAttack(Attack pAttack) {
        StringBuilder buffer = new StringBuilder();
        Village sVillage = pAttack.getSource();
        Village tVillage = pAttack.getTarget();
        UnitHolder sUnit = pAttack.getUnit();
        Date aTime = pAttack.getArriveTime();
        Date sTime = new Date(aTime.getTime() - (long) (DSCalculator.calculateMoveTimeInSeconds(sVillage, tVillage, sUnit.getSpeed()) * 1000));
        int type = pAttack.getType();
        String sendtime = null;
        String arrivetime = null;
        if (ServerSettings.getSingleton().isMillisArrival()) {
            sendtime = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").format(sTime);
            arrivetime = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").format(aTime);
        } else {
            sendtime = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(sTime);
            arrivetime = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(aTime);
        }
        switch (type) {
            case Attack.CLEAN_TYPE: {
                buffer.append("(Clean-Off)");
                buffer.append("\t");
                break;
            }
            case Attack.FAKE_TYPE: {
                buffer.append("(Fake)");
                buffer.append("\t");
                break;
            }
            case Attack.SNOB_TYPE: {
                buffer.append("(AG)");
                buffer.append("\t");
                break;
            }
            case Attack.SUPPORT_TYPE: {
                buffer.append("(Unterstützung)");
                buffer.append("\t");
                break;
            }
        }

        buffer.append(sVillage.getTribe());
        buffer.append("\t");
        buffer.append(sVillage);
        buffer.append("\t");
        buffer.append(sUnit);
        buffer.append("\t");
        if (tVillage.getTribe() == Barbarians.getSingleton()) {
            buffer.append("Barbaren");
        } else {
            buffer.append(tVillage.getTribe());
        }
        buffer.append("\t");
        buffer.append(tVillage);
        buffer.append("\t");
        buffer.append(sendtime);
        buffer.append("\t");
        buffer.append(arrivetime);
        return buffer.toString();
    }
}
