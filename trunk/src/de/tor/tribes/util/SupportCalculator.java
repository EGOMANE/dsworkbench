/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author Charon
 */
public class SupportCalculator {

    private static Logger logger = Logger.getLogger("SupportCalculator");

    public static List<SupportMovement> calculateSupport(Village pVillage, Date pArrive, boolean pRealDefOnly, List<Tag> pTags, int pMinNumber) {
        Hashtable<UnitHolder, Integer> unitTable = new Hashtable<UnitHolder, Integer>();
        if (logger.isDebugEnabled()) {
            logger.debug("Try to find support for village " + pVillage + " at arrival time " + new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS").format(pArrive));
            logger.debug(" - " + ((pTags != null) ? "using" : "not using") + " tag filter");
            logger.debug(" - need at least " + pMinNumber + " units");
        }

        int cnt = 0;
        if (pRealDefOnly) {
            logger.debug("Using only def units");
            //use only "real" def units
            for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
                if (unit.getPlainName().equals("spear")) {
                    unitTable.put(unit, cnt);
                } else if (unit.getPlainName().equals("sword")) {
                    unitTable.put(unit, cnt);
                } else if (unit.getPlainName().equals("archer")) {
                    unitTable.put(unit, cnt);
                } else if (unit.getPlainName().equals("heavy")) {
                    unitTable.put(unit, cnt);
                } else if (unit.getPlainName().equals("knight")) {
                    unitTable.put(unit, cnt);
                }
                cnt++;
            }
        } else {
            logger.debug("Using all units but spy, ram and snob");
            //use all units for def
            for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
                if (!unit.getPlainName().equals("spy") && !unit.getPlainName().equals("snob") && !unit.getPlainName().equals("ram")) {
                    unitTable.put(unit, cnt);
                }
                cnt++;
            }
        }

        Tribe own = DSWorkbenchMainFrame.getSingleton().getCurrentUser();
        if (own == null) {
            logger.warn("Current user is 'null'");
            return new LinkedList<SupportMovement>();
        }
        List<SupportMovement> movements = new LinkedList<SupportMovement>();
        Village[] villageList = own.getVillageList().toArray(new Village[]{});
        List<Village> villages = new LinkedList<Village>();
        //buid list of allowed villages
        for (Village v : villageList) {
            if (pTags != null && !pTags.isEmpty()) {
                for (Tag t : pTags) {
                    if (t.tagsVillage(v.getId())) {
                        if (!villages.contains(v)) {
                            //add village if not already included
                            villages.add(v);
                        }
                    }
                }
            } else {
                //add all villages
                villages.add(v);
            }
        }
        //move village itself
        villages.remove(pVillage);
        for (Village v : villages) {
            //use all villages
            UnitHolder slowestUnit = calculateAvailableUnits(pVillage, v, unitTable, pArrive, pMinNumber);
            if (slowestUnit != null) {
                //unit found
                movements.add(new SupportMovement(v, slowestUnit, new Date(pArrive.getTime() - ((long) DSCalculator.calculateMoveTimeInSeconds(pVillage, v, slowestUnit.getSpeed()) * 1000))));
            }
        }

        return movements;
    }

    private static UnitHolder calculateAvailableUnits(Village pTarget, Village pSource, Hashtable<UnitHolder, Integer> pUnitTable, Date pArrive, int pMinNumber) {
        Enumeration<UnitHolder> allowedKeys = pUnitTable.keys();
        VillageTroopsHolder troops = TroopsManager.getSingleton().getTroopsForVillage(pSource);
        if (troops == null) {
            return null;
        }
        List<Integer> availableTroops = troops.getTroops();

        UnitHolder slowestPossible = null;
        while (allowedKeys.hasMoreElements()) {
            UnitHolder unit = allowedKeys.nextElement();
            int index = pUnitTable.get(unit);
            int availCount = availableTroops.get(index);
            if (availCount > pMinNumber) {
                long ms = (long) DSCalculator.calculateMoveTimeInSeconds(pSource, pTarget, unit.getSpeed()) * 1000;
                if (pArrive.getTime() - ms > System.currentTimeMillis()) {
                    if (slowestPossible == null) {
                        slowestPossible = unit;
                    } else {
                        if (unit.getSpeed() > slowestPossible.getSpeed()) {
                            //if current unit is slower use this unit
                            slowestPossible = unit;
                        }
                    }
                }
            }
        }
        return slowestPossible;
    }

    public static class SupportMovement {

        private Village source = null;
        private UnitHolder unit = null;
        private Date sendTime = null;

        public SupportMovement(Village pSource, UnitHolder pUnit, Date pStartDate) {
            setSource(pSource);
            setUnit(pUnit);
            setSendTime(pStartDate);
        }

        /**
         * @return the source
         */
        public Village getSource() {
            return source;
        }

        /**
         * @param source the source to set
         */
        public void setSource(Village source) {
            this.source = source;
        }

        /**
         * @return the unit
         */
        public UnitHolder getUnit() {
            return unit;
        }

        /**
         * @param unit the unit to set
         */
        public void setUnit(UnitHolder unit) {
            this.unit = unit;
        }

        /**
         * @return the sendTime
         */
        public Date getSendTime() {
            return sendTime;
        }

        /**
         * @param sendTime the sendTime to set
         */
        public void setSendTime(Date sendTime) {
            this.sendTime = sendTime;
        }

        public String toString() {
            String ret = "Von " + source + " am " + new SimpleDateFormat("dd.MM.yy 'um' HH:mm:ss.SSS").format(sendTime) + " mit " + unit;
            return ret;
        }
    }
}


