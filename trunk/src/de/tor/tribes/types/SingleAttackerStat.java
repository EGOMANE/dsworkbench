/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jejkal
 */
public class SingleAttackerStat {

    private Tribe attacker = null;
    private List<Village> usedVillages = null;
    private int fakeAttacks = 0;
    private int offAttacks = 0;
    private int snobAttacks = 0;
    private int simpleSnobAttacks = 0;
    private int enoblements = 0;
    private Hashtable<UnitHolder, Integer> sentUnits = null;
    private Hashtable<UnitHolder, Integer> killedUnits = null;
    private Hashtable<UnitHolder, Integer> lostUnits = null;
    private int unknownDamage = 0;
    private int atLeast2KDamage = 0;
    private int atLeast4KDamage = 0;
    private int atLeast6KDamage = 0;
    private int atLeast8KDamage = 0;
    private int destroyedWallLevels = 0;
    private Hashtable<String, Integer> destroyedBuildingLevels = null;
    private Hashtable<UnitHolder, Integer> silentKills = null;

    public SingleAttackerStat(Tribe pTribe) {
        attacker = pTribe;
        usedVillages = new LinkedList<Village>();
        sentUnits = new Hashtable<UnitHolder, Integer>();
        killedUnits = new Hashtable<UnitHolder, Integer>();
        lostUnits = new Hashtable<UnitHolder, Integer>();
        destroyedBuildingLevels = new Hashtable<String, Integer>();
        silentKills = new Hashtable<UnitHolder, Integer>();
    }

    public void addSourceVillage(Village pVillage) {
        if (!usedVillages.contains(pVillage)) {
            usedVillages.add(pVillage);
        }
    }

    public Tribe getAttacker() {
        return attacker;
    }

    public List<Village> getSourceVillages() {
        return usedVillages;
    }

    public void addFake() {
        fakeAttacks++;
    }

    public int getFakeCount() {
        return fakeAttacks;
    }

    public void addOff() {
        offAttacks++;
    }

    public int getOffCount() {
        return offAttacks;
    }

    public void addEnoblement() {
        enoblements++;
    }

    public int getEnoblementCount() {
        return enoblements;
    }

    public void addSnobAttack() {
        snobAttacks++;
    }

    public int getSnobAttackCount() {
        return snobAttacks;
    }

    public void addSimpleSnobAttack() {
        simpleSnobAttacks++;
    }

    public int getSimpleSnobAttackCount() {
        return simpleSnobAttacks;
    }

    public void addUnknownDamage() {
        unknownDamage++;
    }

    public int getUnknownDamageCount() {
        return unknownDamage;
    }

    public void addAtLeast2KDamage() {
        atLeast2KDamage++;
    }

    public int getAtLeast2KDamageCount() {
        return atLeast2KDamage;
    }

    public void addAtLeast4KDamage() {
        atLeast4KDamage++;
    }

    public int getAtLeast4KDamageCount() {
        return atLeast4KDamage;
    }

    public void addAtLeast6KDamage() {
        atLeast6KDamage++;
    }

    public int getAtLeast6KDamageCount() {
        return atLeast6KDamage;
    }

    public void addAtLeast8KDamage() {
        atLeast8KDamage++;
    }

    public int getAtLeast8KDamageCount() {
        return atLeast8KDamage;
    }

    public void addDestroyedWallLevels(int pLevels) {
        destroyedWallLevels += pLevels;
    }

    public int getDestroyedWallLevels() {
        return destroyedWallLevels;
    }

    public void addDestroyedBuildingLevel(String pBuilding, int pLevels) {
        Integer value = destroyedBuildingLevels.get(pBuilding);
        if (value == null) {
            destroyedBuildingLevels.put(pBuilding, pLevels);
        } else {
            destroyedBuildingLevels.put(pBuilding, value + pLevels);
        }
    }

    public Hashtable<String, Integer> getDestroyedBuildings() {
        return destroyedBuildingLevels;
    }

    public int getSummedDestroyedBuildings() {
        if (destroyedBuildingLevels == null || destroyedBuildingLevels.isEmpty()) {
            return 0;
        }
        Enumeration<String> keys = destroyedBuildingLevels.keys();
        int value = 0;
        while (keys.hasMoreElements()) {
            value += destroyedBuildingLevels.get(keys.nextElement());
        }
        return value;
    }

    public void addSentUnit(UnitHolder pUnit, Integer pAmount) {
        Integer value = sentUnits.get(pUnit);
        if (value == null) {
            sentUnits.put(pUnit, pAmount);
        } else {
            sentUnits.put(pUnit, value + pAmount);
        }
    }

    public void addLostUnit(UnitHolder pUnit, Integer pAmount) {
        Integer value = lostUnits.get(pUnit);
        if (value == null) {
            lostUnits.put(pUnit, pAmount);
        } else {
            lostUnits.put(pUnit, value + pAmount);
        }
    }

    public void addKilledUnit(UnitHolder pUnit, Integer pAmount) {
        Integer value = killedUnits.get(pUnit);
        if (value == null) {
            killedUnits.put(pUnit, pAmount);
        } else {
            killedUnits.put(pUnit, value + pAmount);
        }
    }

    public void addSilentlyKilledUnit(UnitHolder pUnit, Integer pAmount) {
        Integer value = silentKills.get(pUnit);
        if (value == null) {
            silentKills.put(pUnit, pAmount);
        } else {
            silentKills.put(pUnit, value + pAmount);
        }
    }

    public int getSummedLosses() {
        int value = 0;
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (lostUnits != null && !lostUnits.isEmpty()) {
                value += lostUnits.get(unit);
            }
        }
        return value;
    }

    public int getSummedLossesAsFarmSpace() {
        int value = 0;
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (lostUnits != null && !lostUnits.isEmpty()) {
                value += lostUnits.get(unit) * (int) Math.rint(unit.getPop());
            }
        }
        return value;
    }

    public int getSummedKills() {
        int value = 0;

        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (killedUnits != null && !killedUnits.isEmpty()) {
                value += killedUnits.get(unit);
            }
            if (silentKills != null && !silentKills.isEmpty()) {
                value += silentKills.get(unit);
            }
        }
        return value;
    }

    public int getSummedKillsAsFarmSpace() {
        int value = 0;

        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (killedUnits != null && !killedUnits.isEmpty()) {
                value += killedUnits.get(unit) * (int) Math.rint(unit.getPop());
            }
            if (silentKills != null && !silentKills.isEmpty()) {
                value += silentKills.get(unit) * (int) Math.rint(unit.getPop());
            }
        }
        return value;
    }

    public int getSummedSilentKills() {
        int value = 0;

        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (silentKills != null && !silentKills.isEmpty()) {
                value += silentKills.get(unit);
            }
        }
        return value;
    }

    public int getSummedSilentKillsAsFarmSpace() {
        int value = 0;

        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (silentKills != null && !silentKills.isEmpty()) {
                value += silentKills.get(unit) * (int) Math.rint(unit.getPop());
            }
        }
        return value;
    }

    @Override
    public String toString() {
        String res = "";
        res += " Attacker: " + attacker + "\n";
        res += " UsedVillages: " + usedVillages.size() + "\n";
        res += " Fakes: " + fakeAttacks + "\n";
        res += " Offs: " + offAttacks + "\n";
        res += " Snob: " + snobAttacks + "\n";
        res += " SimpleSnob: " + simpleSnobAttacks + "\n";
        res += " Enoblements: " + enoblements + "\n";
        res += " UnitStats\n";
        res += " .........\n";
        res += "  Sent: " + sentUnits + "\n";
        res += "  Lost: " + lostUnits + "\n";
        res += "  Killed: " + killedUnits + "\n";
        res += "  SilentKills: " + silentKills + "\n";
        res += "  Killed: " + getSummedKills() + "\n";
        res += "  Lost: " + getSummedLosses() + "\n";
        res += "  UnknownDamage: " + getUnknownDamageCount() + "\n";
        res += "  AtLeast2KDamage: " + getAtLeast2KDamageCount() + "\n";
        res += "  AtLeast4KDamage: " + getAtLeast4KDamageCount() + "\n";
        res += "  AtLeast6KDamage: " + getAtLeast6KDamageCount() + "\n";
        res += "  AtLeast8KDamage: " + getAtLeast8KDamageCount() + "\n";
        res += " DestructionStats\n";
        res += " ................\n";
        res += "  Wall: " + destroyedWallLevels + "\n";
        res += "  Buildings: " + destroyedBuildingLevels + "\n";
        return res;
    }
}
