/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.php.json.JSONObject;
import de.tor.tribes.util.xml.JaxenUtils;
import java.io.Serializable;
import java.util.Date;
import org.jdom.Element;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ServerSettings;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 *
 * @author Charon
 */
public class Attack implements Serializable, Comparable<Attack> {

    public static final int NO_TYPE = 0;
    public static final int CLEAN_TYPE = 1;
    public static final int SNOB_TYPE = 2;
    public static final int SUPPORT_TYPE = 3;
    public static final int FAKE_TYPE = 4;
    public static final int FAKE_DEFF_TYPE = 5;
    public static final int SPY_TYPE = 6;
    private static final long serialVersionUID = 10L;
    private Village source = null;
    private Village target = null;
    private UnitHolder unit = null;
    private Date arriveTime = null;
    private boolean showOnMap = false;
    private int type = 0;
    private boolean transferredToBrowser = false;

    public Attack() {
        try {
            showOnMap = Boolean.parseBoolean(GlobalOptions.getProperty("draw.attacks.by.default"));
        } catch (Exception e) {
        }
    }

    public Attack(Element pElement) {
        setSource(DataHolder.getSingleton().getVillagesById().get(Integer.parseInt(pElement.getChild("source").getText())));
        setTarget(DataHolder.getSingleton().getVillagesById().get(Integer.parseInt(pElement.getChild("target").getText())));
        setArriveTime(new Date(Long.parseLong(pElement.getChild("arrive").getText())));
        setUnit(DataHolder.getSingleton().getUnitByPlainName(pElement.getChild("unit").getText()));
        setShowOnMap(Boolean.parseBoolean(JaxenUtils.getNodeValue(pElement, "extensions/showOnMap")));
        try {
            setType(Integer.parseInt(JaxenUtils.getNodeValue(pElement, "extensions/type")));
        } catch (Exception e) {
            //no type set
            setType(NO_TYPE);
        }
        try {
            setTransferredToBrowser(Boolean.parseBoolean(JaxenUtils.getNodeValue(pElement, "extensions/transferredToBrowser")));
        } catch (Exception e) {
            //not transferred yet
            setTransferredToBrowser(false);
        }
    }

    public boolean isSourceVillage(Village pVillage) {
        return (pVillage == source);
    }

    public boolean isTargetVillage(Village pVillage) {
        return (pVillage == target);
    }

    public Village getSource() {
        return source;
    }

    public void setSource(Village source) {
        this.source = source;
    }

    public Village getTarget() {
        return target;
    }

    public void setTarget(Village target) {
        this.target = target;
    }

    public UnitHolder getUnit() {
        return unit;
    }

    public void setUnit(UnitHolder unit) {
        this.unit = unit;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        if (arriveTime == null) {
            return;
        }
        if (ServerSettings.getSingleton().isMillisArrival()) {
            this.arriveTime = arriveTime;
        } else {
            this.arriveTime = new Date((long) Math.floor((double) arriveTime.getTime() / 1000.0) * 1000l);
        }
    }

    public boolean isShowOnMap() {
        return showOnMap;
    }

    public void setShowOnMap(boolean showOnMap) {
        this.showOnMap = showOnMap;
    }

    public static Attack fromXml(Element pElement) {
        return new Attack(pElement);
    }

    public String toXml() {
        String xml = "<attack>\n";
        xml += "<source>" + getSource().getId() + "</source>\n";
        xml += "<target>" + getTarget().getId() + "</target>\n";
        xml += "<arrive>" + getArriveTime().getTime() + "</arrive>\n";
        xml += "<unit>" + getUnit().getPlainName() + "</unit>\n";
        xml += "<extensions>\n";
        xml += "\t<showOnMap>" + isShowOnMap() + "</showOnMap>\n";
        xml += "\t<type>" + getType() + "</type>\n";
        xml += "\t<transferredToBrowser>" + isTransferredToBrowser() + "</transferredToBrowser>\n";
        xml += "</extensions>\n";
        xml += "</attack>";
        return xml;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
    /*
    <attack>
    <source>VillageID</source>
    <target>VillageID</target>
    <arrive>Timestamp</arrive>
    <unit>Name</unit>
    <extensions>
    <showOnMap>true</showOnMap>
    <type>0</type>
    </extensions>
    </attack>
     */

    public JSONObject toJSON(String pOwner, String pPlanID) throws Exception {
        JSONObject a = new JSONObject();
        a.put("owner", URLEncoder.encode(pOwner, "UTF-8"));
        a.put("source", getSource().getId());
        a.put("target", getTarget().getId());
        a.put("arrive", getArriveTime().getTime());
        a.put("type", getType());
        a.put("unit", getUnit().getPlainName());
        a.put("plan", URLEncoder.encode(pPlanID, "UTF-8"));
        return a;
    }

    @Override
    public String toString() {
        /* long send = getArriveTime().getTime() - (long) (DSCalculator.calculateMoveTimeInSeconds(getSource(), getTarget(), getUnit().getSpeed()) * 1000);
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(send);
        String result = getSource() + " > " + getTarget() + ": " + f.format(c.getTime()) + " ... " + f.format(getArriveTime());*/
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        String result = getSource() + "\n";
        result += getTarget() + "\n";
        result += getUnit() + "\n";
        result += f.format(getArriveTime());
        return result;
    }

    /**
     * @return the transferredToBrowser
     */
    public boolean isTransferredToBrowser() {
        return transferredToBrowser;
    }

    /**
     * @param transferredToBrowser the transferredToBrowser to set
     */
    public void setTransferredToBrowser(boolean transferredToBrowser) {
        this.transferredToBrowser = transferredToBrowser;
    }

    @Override
    public int compareTo(Attack a) {
        if (getSource().getId() == a.getSource().getId()
                && getTarget().getId() == a.getTarget().getId()
                && getArriveTime().getTime() == a.getArriveTime().getTime()) {
            return 0;
        }
        return -1;
    }
}
