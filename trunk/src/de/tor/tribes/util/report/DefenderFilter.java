/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.report;

import de.tor.tribes.types.FightReport;
import de.tor.tribes.types.Tribe;
import java.util.List;

/**
 *
 * @author Torridity
 */
public class DefenderFilter implements ReportFilterInterface {

    private List<Tribe> tribes = null;

    @Override
    public void setup(Object pFilterComponent) {
        tribes = (List<Tribe>) pFilterComponent;
    }

    @Override
    public boolean isValid(FightReport c) {
        try {
            for (Tribe t : tribes) {
                if (t.equals(c.getDefender())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
