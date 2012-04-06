/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.report;

import de.tor.tribes.types.FightReport;
import de.tor.tribes.util.Filter;

/**
 *
 * @author Torridity
 */
public interface ReportRuleInterface extends Filter<FightReport> {

    void setup(Object pFilterComponent) throws ReportRuleConfigurationException;

    String getDescription();

    String getStringRepresentation();
}
