/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import de.tor.tribes.types.Attack;
import de.tor.tribes.types.test.DummyUnit;
import de.tor.tribes.types.test.DummyVillage;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Torridity
 */
public class BBFormater {

    public static String getTemplate(BBSupport pElement) {
        String template = GlobalOptions.getProperty(pElement.getTemplateProperty());
        if (template == null) {
            template = pElement.getStandardTemplate();
        }
        return template;
    }

    public static String formatElement(BBSupport pElement, boolean pExtended) {
        String template = getTemplate(pElement);
        return StringUtils.replaceEach(template, pElement.getBBVariables(), pElement.getReplacements(pExtended));
    }

    public static void main(String[] args) {
        Attack sampleAttack = new Attack();
        sampleAttack.setSource(new DummyVillage());
        sampleAttack.setTarget(new DummyVillage());
        sampleAttack.setArriveTime(new Date());
        sampleAttack.setType(Attack.CLEAN_TYPE);
        sampleAttack.setUnit(new DummyUnit());
        System.out.println(formatElement(sampleAttack, true));
    }
}