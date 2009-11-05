/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import org.jdom.Element;

/**
 * @author Charon
 */
public class StandardAttackElement {

    public static final String ALL_TROOPS = "Alle";
    private UnitHolder unit = null;
    private Integer fixedAmount = -1;
    private String dynamicAmount = ALL_TROOPS;

    public StandardAttackElement(UnitHolder pUnit, Integer pFixedAmout) {
        unit = pUnit;
        fixedAmount = pFixedAmout;
        dynamicAmount = null;
    }

    public StandardAttackElement(UnitHolder pUnit, String pDynAmount) {
        unit = pUnit;
        fixedAmount = -1;
        dynamicAmount = pDynAmount;
    }

    public static StandardAttackElement fromXml(Element e) throws Exception {
        UnitHolder unit = DataHolder.getSingleton().getUnitByPlainName(e.getAttributeValue("unit"));
        Integer fixed = e.getAttribute("fixAmount").getIntValue();
        String dyn = null;
        try {
            dyn = URLDecoder.decode(e.getAttributeValue("dynAmount"), "UTF-8");
        } catch (Exception ignored) {
        }
        if (dyn == null) {
            if (fixed == -1) {
                return new StandardAttackElement(unit, ALL_TROOPS);
            } else {
                return new StandardAttackElement(unit, fixed);
            }
        } else {
            if (fixed == -1) {
                return new StandardAttackElement(unit, dyn);
            }
        }
        throw new Exception("Invalid entry");
    }

    public String toXml() {
        String result = null;
        if (dynamicAmount == null) {
            result = "<attackElement unit=\"" + unit.getPlainName() + "\" fixAmount=\"" + fixedAmount + "\"/>\n";
        } else {
            try {
                result = "<attackElement unit=\"" + unit.getPlainName() + "\" fixAmount=\"" + fixedAmount + "\" dynAmount=\"" + URLEncoder.encode(dynamicAmount, "UTF-8") + "\"/>\n";
            } catch (Exception e) {
                result = "<attackElement unit=\"" + unit.getPlainName() + "\" fixAmount=\"" + fixedAmount + "\"/>\n";
            }
        }
        return result;

    }

    public boolean affectsUnit(UnitHolder pUnit) {
        if (pUnit == null || unit == null) {
            return false;
        }
        return unit.getPlainName().equals(pUnit.getPlainName());
    }

    public UnitHolder getUnit() {
        return unit;
    }

    public StandardAttackElement(UnitHolder pUnit) {
        unit = pUnit;
        fixedAmount = -1;
        dynamicAmount = ALL_TROOPS;
    }

    public void setFixedAmount(int pAmount) {
        fixedAmount = pAmount;
        dynamicAmount = null;
    }

    public void setDynamicBySubstraction(int pRemainTroops) {
        fixedAmount = -1;
        dynamicAmount = ALL_TROOPS + " - " + pRemainTroops;
    }

    public void setDynamicByPercent(int pPercent) {
        fixedAmount = -1;
        //catch values smaller 0 and larger 100
        int percent = (pPercent > 100) ? 100 : pPercent;
        percent = (percent < 0) ? 0 : percent;
        dynamicAmount = percent + "%";
    }

    public void setAll() {
        fixedAmount = -1;
        dynamicAmount = ALL_TROOPS;
    }

    public int getTroopsAmount(Village pVillage) {
        VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage);
        if (holder == null) {
            //no info available
            return 0;
        }

        Hashtable<UnitHolder, Integer> ownTroops = holder.getOwnTroops();
        if (ownTroops == null) {
            //no troops in village
            return 0;
        }

        Integer availableAmount = ownTroops.get(unit);
        if (availableAmount == 0) {
            //no troops in village
            return 0;
        }

        if (fixedAmount != -1) {
            //fixed amount
            if (availableAmount >= fixedAmount) {
                //enough troops available
                return fixedAmount;
            } else {
                //return max. avail count
                return availableAmount;
            }
        } else {
            //dyn amount
            if (dynamicAmount.equals(ALL_TROOPS)) {
                //return all
                return availableAmount;
            } else if (dynamicAmount.startsWith(ALL_TROOPS + " - ")) {
                //return all minus X
                String v = dynamicAmount.replaceAll(ALL_TROOPS + " - ", "").trim();
                int substract = Integer.parseInt(v);
                if (availableAmount - substract > 0) {
                    //enough troops avail
                    return availableAmount - substract;
                } else {
                    //substract larger than avail count
                    return 0;
                }
            } else if (dynamicAmount.indexOf("%") > -1) {
                String v = dynamicAmount.replaceAll("%", "").trim();
                double perc = (double) Integer.parseInt(v);
                perc /= 100;
                if (Math.rint(perc * availableAmount) > 0) {
                    return (int) Math.rint(perc * availableAmount);
                } else {
                    return 0;
                }
            }
        }
        return 0;
    }

    public boolean trySettingAmount(String pValue) {
        try {
            //check for fixed value
            int fix = Integer.parseInt(pValue);
            if (fix >= 0) {
                setFixedAmount(fix);
            }
            return true;
        } catch (Exception e) {
            //no fixed value
        }

        if (pValue.equals(ALL_TROOPS)) {
            setAll();
            return true;
        }

        try {
            //check for ALL - X
            int remain = Integer.parseInt(pValue.replaceAll(ALL_TROOPS, "").replaceAll("-", "").trim());
            if (remain >= 0) {
                setDynamicBySubstraction(remain);
            }
            return true;
        } catch (Exception e) {
            //no ALL - X
        }
        try {
            //try X%
            int percent = Integer.parseInt(pValue.replaceAll("%", "").trim());
            if (percent >= 0) {
                setDynamicByPercent(percent);
            }
            return true;
        } catch (Exception e) {
            //no percent
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        if (fixedAmount != -1) {
            result += fixedAmount;
        } else {
            result += dynamicAmount;
        }

        return result;
    }

    public static void main(String[] args) {
        String dyn = "Alle - 100";
        String v = dyn.replaceAll("Alle - ", "").trim();
        int substract = Integer.parseInt(v);
        System.out.println(substract);
    }
}
