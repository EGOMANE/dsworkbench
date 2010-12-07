/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.report;

import de.tor.tribes.types.FightReport;
import de.tor.tribes.types.ReportSet;
import de.tor.tribes.ui.DSWorkbenchReportFrame;
import de.tor.tribes.ui.models.ReportManagerTableModel;
import de.tor.tribes.util.FilterableManager;
import de.tor.tribes.util.xml.JaxenUtils;
import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Torridity
 */
public class ReportManager extends FilterableManager<FightReport, ReportFilterInterface> {

    private static Logger logger = Logger.getLogger("ReportManager");
    private static ReportManager SINGLETON = null;
    public static final String DEFAULT_SET = "default";
    private Hashtable<String, ReportSet> reportSets = null;
    private final List<ReportManagerListener> mManagerListeners = new LinkedList<ReportManagerListener>();

    public static synchronized ReportManager getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new ReportManager();
        }
        return SINGLETON;
    }

    ReportManager() {
        reportSets = new Hashtable<String, ReportSet>();
        reportSets.put(DEFAULT_SET, new ReportSet(DEFAULT_SET));
    }

    public synchronized void addReportManagerListener(ReportManagerListener pListener) {
        if (pListener == null) {
            return;
        }
        if (!mManagerListeners.contains(pListener)) {
            mManagerListeners.add(pListener);
        }
    }

    public synchronized void removeAttackManagerListener(ReportManagerListener pListener) {
        mManagerListeners.remove(pListener);
    }

    public void loadReportsFromFile(String pFile) {
        reportSets.clear();
        clearFilteredList();
        reportSets.put(DEFAULT_SET, new ReportSet(DEFAULT_SET));
        if (pFile == null) {
            logger.error("File argument is 'null'");
            return;
        }
        File reportFile = new File(pFile);
        if (reportFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Reading reports from '" + pFile + "'");
            }
            try {
                Document d = JaxenUtils.getDocument(reportFile);
                for (Element e : (List<Element>) JaxenUtils.getNodes(d, "//reportSets/reportSet")) {
                    try {
                        ReportSet set = ReportSet.fromXml(e);
                        if (set != null) {
                            reportSets.put(set.getName(), set);
                        }
                    } catch (Exception inner) {
                        //ignored, reportset invalid
                    }
                }
                logger.debug("Reports successfully loaded");
                forceUpdate(DEFAULT_SET);
                /* ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "database.dbo");
                try {
                Enumeration<String> sets = reportSets.keys();
                long s = System.currentTimeMillis();
                while (sets.hasMoreElements()) {
                ReportSet set = reportSets.get(sets.nextElement());
                for (FightReport rep : set.getReports()) {
                rep.setWon((Math.random() > .5));
                }

                db.store(set);
                }
                } finally {
                db.close();
                }*/
            } catch (Exception e) {
                logger.error("Failed to load Reports", e);
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Reports file not found under '" + pFile + "'");
            }
        }
    }

    public boolean importReports(File pFile, String pExtension) {
        if (pFile == null) {
            logger.error("File argument is 'null'");
            return false;
        }
        logger.debug("Importing reports");
        try {
            Document d = JaxenUtils.getDocument(pFile);
            for (Element e : (List<Element>) JaxenUtils.getNodes(d, "//reportSets/reportSet")) {
                try {
                    ReportSet s = ReportSet.fromXml(e);
                    s.setName(s.getName() + pExtension);
                    ReportSet set = reportSets.get(s.getName());
                    if (set == null) {
                        //add new report set
                        reportSets.put(s.getName(), s);
                    } else {
                        //add reports to existing set
                        for (FightReport report : s.getReports()) {
                            set.addReport(report);
                        }
                    }
                } catch (Exception inner) {
                    //ignored, reports invalid
                }
            }
            logger.debug("Reports imported successfully");
            DSWorkbenchReportFrame.getSingleton().fireReportsChangedEvent(DEFAULT_SET);
            return true;
        } catch (Exception e) {
            logger.error("Failed to import reports", e);
            DSWorkbenchReportFrame.getSingleton().fireReportsChangedEvent(DEFAULT_SET);
            return false;
        }
    }

    public String getExportData(String[] pSets) {
        logger.debug("Generating report export data");

        String result = "<reportSets>\n";
        for (String set : pSets) {
            ReportSet s = reportSets.get(set);
            try {
                String xml = s.toXml();
                if (xml != null) {
                    result += xml + "\n";
                }
            } catch (Exception e) {
            }
        }
        result += "</reportSets>\n";
        logger.debug("Export data generated successfully");
        return result;
    }

    /**Load markers to file
     */
    public void saveReportsToFile(String pFile) {
        if (pFile == null) {
            logger.error("File argument is 'null'");
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Writing reports to '" + pFile + "'");
        }
        try {
            StringBuffer b = new StringBuffer();
            b.append("<reportSets>\n");
            Enumeration<String> setKeys = reportSets.keys();
            while (setKeys.hasMoreElements()) {
                ReportSet set = reportSets.get(setKeys.nextElement());
                b.append(set.toXml());
            }
            b.append("</reportSets>");
            //writing data to file
            FileWriter w = new FileWriter(pFile);
            w.write(b.toString());
            w.flush();
            w.close();
            logger.debug("Reports successfully saved");
        } catch (Exception e) {
            if (!new File(pFile).getParentFile().exists()) {
                //server directory obviously does not exist yet
                //this should only happen at the first start
                logger.info("Ignoring error, server directory does not exists yet");
            } else {
                logger.error("Failed to save reports", e);
            }
        }
    }

    public boolean createReportSet(String pName) {
        if (reportSets.get(pName) != null) {
            return false;
        }
        ReportSet set = new ReportSet(pName);
        reportSets.put(pName, set);
        fireReportsChangedEvents(pName);
        return true;
    }

    public ReportSet getReportSet(String pName) {
        if (pName == null) {
            pName = DEFAULT_SET;
        }
        return reportSets.get(pName);
    }

    public ReportSet getCurrentReportSet() {
        return getReportSet(ReportManagerTableModel.getSingleton().getActiveReportSet());
    }

    public boolean renameReportSet(String pName, String pNewName) {
        if (pName == null) {
            return false;
        }
        ReportSet toRename = reportSets.remove(pName);
        if (toRename == null) {
            return false;
        }
        toRename.setName(pNewName);
        reportSets.put(pNewName, toRename);
        ReportManagerTableModel.getSingleton().setActiveReportSet(pNewName);
        ReportManager.getSingleton().updateFilters();
        fireReportsChangedEvents(pNewName);
        return true;
    }

    public boolean removeReportSet(String pName) {
        if (pName == null || pName.equals(DEFAULT_SET)) {
            return false;
        }
        reportSets.remove(pName);
        fireReportsChangedEvents(pName);
        return true;
    }

    public synchronized void removeReport(String pSet, int pID) {
        removeReports(pSet, new int[]{pID});
    }

    public synchronized void removeReports(String pSet, int[] pIDs) {
        String set = pSet;
        if (set == null) {
            set = DEFAULT_SET;
        }

        ReportSet reportSet = reportSets.get(set);
        FightReport[] reports = getFilteredList().toArray(new FightReport[]{});

        for (int i : pIDs) {
            if (logger.isDebugEnabled()) {
                logger.debug("Removing report " + i + " from set '" + set + "'");
            }

            reportSet.removeReport(reports[i]);
        }

        fireReportsChangedEvents(set);
    }

    public Enumeration<String> getReportSets() {
        return reportSets.keys();
    }

    public void forceUpdate(String pPlan) {
        updateFilters();
        fireReportsChangedEvents(pPlan);
    }

    private void fireReportsChangedEvents(String pPlan) {
        String plan = pPlan;
        if (plan == null) {
            plan = DEFAULT_SET;
        }

        updateFilters();
        ReportManagerListener[] listeners = mManagerListeners.toArray(new ReportManagerListener[]{});
        for (ReportManagerListener listener : listeners) {
            listener.fireReportsChangedEvent(plan);
        }
    }

    @Override
    public FightReport[] getUnfilteredElements() {
        return reportSets.get(ReportManagerTableModel.getSingleton().getActiveReportSet()).getReports();
    }
}