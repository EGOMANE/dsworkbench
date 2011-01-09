/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * SendTimePanel.java
 *
 * Created on 05.05.2009, 09:34:03
 */
package de.tor.tribes.ui.algo;

import de.tor.tribes.db.DatabaseServerEntry;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.types.TimeSpan;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.algo.TimeFrame;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

/**
 * @author Jejkal
 */
public class SettingsPanel extends javax.swing.JPanel {

    private static Logger logger = Logger.getLogger("AttackPlannerSettings");

    /** Creates new form TimePanel */
    public SettingsPanel() {
        initComponents();
        setBackground(Constants.DS_BACK_LIGHT);
        reset();
    }

    public void reset() {
        jSendTimeSettingsPanel.reset();
        jArriveTimeSettingsPanel.reset();
        timeFrameVisualizer1.setScrollPane(jScrollPane1);
        restoreProperties();
    }

    public void storeProperties() {
        /*    String server = GlobalOptions.getSelectedServer();
        GlobalOptions.addProperty(server + ".attack.frame.start.date", Long.toString(jSendTime.getSelectedDate().getTime()));
        GlobalOptions.addProperty(server + ".attack.frame.arrive.date", Long.toString(jArriveTime.getSelectedDate().getTime()));
        GlobalOptions.addProperty(server + ".attack.frame.arrive.frame.min", Double.toString(jArriveTimeFrame.getMinimumColoredValue()));
        GlobalOptions.addProperty(server + ".attack.frame.arrive.frame.max", Double.toString(jArriveTimeFrame.getMaximumColoredValue()));
        GlobalOptions.addProperty(server + ".attack.frame.var.arrive.time", Boolean.toString(jVariableArriveTimeBox.isSelected()));
        GlobalOptions.addProperty(server + ".attack.frame.algo.type", Integer.toString(jAlgoBox.getSelectedIndex()));
        GlobalOptions.addProperty(server + ".attack.frame.fake.off.targets", Boolean.toString(jFakeOffTargetsBox.isSelected()));
        DefaultListModel model = (DefaultListModel) jSendTimeFramesList.getModel();
        String spanProp = "";
        for (int i = 0; i < model.getSize(); i++) {
        spanProp += ((TimeSpan) model.getElementAt(i)).toPropertyString() + ";";
        }
        GlobalOptions.addProperty(server + ".attack.frame.time.spans", spanProp);*/
    }

    public void restoreProperties() {
        /* try {
        String server = GlobalOptions.getSelectedServer();
        long start = Long.parseLong(GlobalOptions.getProperty(server + ".attack.frame.start.date"));
        if (start < System.currentTimeMillis()) {
        //set start to 5 min in future if start is in past
        start = System.currentTimeMillis() + 1000 * 60 * 5;
        }
        jSendTime.setDate(new Date(start));


        long arrive = Long.parseLong(GlobalOptions.getProperty(server + ".attack.frame.arrive.date"));
        if (arrive < System.currentTimeMillis()) {
        //set start to 1 hour in future if arrive is in past
        arrive = System.currentTimeMillis() + 1000 * 60 * 60;
        }
        jArriveTime.setDate(new Date(arrive));

        jArriveTimeFrame.setMinimumColoredValue(Double.parseDouble(GlobalOptions.getProperty(server + ".attack.frame.arrive.frame.min")));
        jArriveTimeFrame.setMaximumColoredValue(Double.parseDouble(GlobalOptions.getProperty(server + ".attack.frame.arrive.frame.max")));
        jAlgoBox.setSelectedIndex(Integer.parseInt(GlobalOptions.getProperty(server + ".attack.frame.algo.type")));
        jVariableArriveTimeBox.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty(server + ".attack.frame.var.arrive.time")));
        jFakeOffTargetsBox.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty(server + ".attack.frame.fake.off.targets")));
        String spanProp = GlobalOptions.getProperty(server + ".attack.frame.time.spans");
        String[] spans = spanProp.split(";");
        DefaultListModel model = (DefaultListModel) jSendTimeFramesList.getModel();
        for (String span : spans) {
        try {
        TimeSpan s = TimeSpan.fromPropertyString(span);
        if (s != null) {
        model.addElement(s);
        }
        } catch (Exception invalid) {
        }
        }

        } catch (Exception e) {
        }
        if (jVariableArriveTimeBox.isSelected()) {
        jArriveTime.setTimeEnabled(false);
        } else {
        jArriveTime.setTimeEnabled(true);
        }*/
    }

    /**Add tribe to timeframe list*/
    public void addTribe(Tribe t) {
        /* DefaultComboBoxModel model = (DefaultComboBoxModel) jTribeTimeFrameBox.getModel();
        List<Tribe> tribes = new LinkedList<Tribe>();
        for (int i = 0; i < model.getSize(); i++) {
        try {
        tribes.add((Tribe) model.getElementAt(i));
        } catch (Exception e) {
        }
        }
        if (!tribes.contains(t)) {
        tribes.add(t);
        Collections.sort(tribes);
        model = new DefaultComboBoxModel();
        model.addElement("Alle");
        for (Tribe tribe : tribes) {
        model.addElement(tribe);
        }
        jTribeTimeFrameBox.setModel(model);
        }*/
        jSendTimeSettingsPanel.addTribe(t);
    }

    /**Remove tribe from  timeframe list (not used yet)*/
    public void removeTribe(Tribe pTribe) {
        /* DefaultComboBoxModel model = (DefaultComboBoxModel) jTribeTimeFrameBox.getModel();
        List<Tribe> tribes = new LinkedList<Tribe>();
        for (int i = 0; i < model.getSize(); i++) {
        try {
        tribes.add((Tribe) model.getElementAt(i));
        } catch (Exception e) {
        }
        }
        tribes.remove(pTribe);
        Collections.sort(tribes);
        model = new DefaultComboBoxModel();
        model.addElement("Alle");
        for (Tribe tribe : tribes) {
        model.addElement(tribe);
        }
        jTribeTimeFrameBox.setModel(model);*/
        jSendTimeSettingsPanel.removeTribe(pTribe);
    }

    /**Return selected send time frames
     */
    public TimeFrame getTimeFrame() {
        /*  TimeFrame result = new TimeFrame(jSendTime.getSelectedDate(), jArriveTime.getSelectedDate());
        //add time frames
        DefaultListModel model = (DefaultListModel) jSendTimeFramesList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
        TimeSpan span = (TimeSpan) model.getElementAt(i);

        IntRange s = new IntRange(span.getSpan().getMinimumInteger(), span.getSpan().getMaximumInteger() - 1);
        System.out.println(s);
        TimeSpan tmp = new TimeSpan(span.getAtDate(), s, span.isValidFor());
        result.addTimeSpan(tmp);
        }
        if (jVariableArriveTimeBox.isSelected()) {
        result.setUseVariableArriveTime(true);
        result.setArriveSpan((int) Math.rint(jArriveTimeFrame.getMinimumColoredValue()), (int) Math.rint(jArriveTimeFrame.getMaximumColoredValue()));
        }
        System.out.println(jSendTimeSettingsPanel.getTimeSpans());
        System.out.println(jArriveTimeSettingsPanel.getTimeSpans());

        Date minTime = jSendTimeSettingsPanel.getMinMaxTime();
        Date maxTime = jArriveTimeSettingsPanel.getMinMaxTime();
         */

        Date sendMaxTime = jSendTimeSettingsPanel.getMaxTime();
        if (sendMaxTime == null) {
            sendMaxTime = jArriveTimeSettingsPanel.getMaxTime();
        }
        TimeFrame result = new TimeFrame(jSendTimeSettingsPanel.getMinTime(), sendMaxTime, jArriveTimeSettingsPanel.getMinTime(), jArriveTimeSettingsPanel.getMaxTime());
        for (TimeSpan span : jSendTimeSettingsPanel.getTimeSpans()) {
            result.addStartTimeSpan(span);
        }
        for (TimeSpan span : jArriveTimeSettingsPanel.getTimeSpans()) {
            result.addArriveTimeSpan(span);
        }

        return result;
    }

    public boolean validatePanel() {
        try {
            //no time frame specified
            boolean result = true;
            // <editor-fold defaultstate="collapsed" desc="Check if there are timeframes provided">

            if (jSendTimeSettingsPanel.getTimeSpans().isEmpty()) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, "Es muss mindestens ein Abschickzeitfenster angegebene werden.\n"
                        + "Soll der Standardzeitrahmen (8 - 24 Uhr) verwendet werden?", "Fehlendes Zeitfenster", "Nein", "Ja") == JOptionPane.YES_OPTION) {
                    jSendTimeSettingsPanel.addDefaultTimeFrame();
                } else {
                    result = false;
                }
            }
            if (jArriveTimeSettingsPanel.getTimeSpans().isEmpty()) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, "Es muss mindestens ein Ankunftszeitfenster angegebene werden.\n"
                        + "Soll der Standardzeitrahmen (8 - 24 Uhr) verwendet werden?", "Fehlendes Zeitfenster", "Nein", "Ja") == JOptionPane.YES_OPTION) {
                    jArriveTimeSettingsPanel.addDefaultTimeFrame();
                } else {
                    result = false;
                }
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Check min/max times">
            //check send case
            Date minSendTime = jSendTimeSettingsPanel.getMinTime();
            Date maxSendTime = jSendTimeSettingsPanel.getMaxTime();
            if (maxSendTime == null) {
                maxSendTime = jArriveTimeSettingsPanel.getMaxTime();
            }
            if (minSendTime.getTime() > maxSendTime.getTime()) {
                logger.warn("Earliest start time bigger than latest start time (" + minSendTime.getTime() + ">" + maxSendTime.getTime() + ")");
                JOptionPaneHelper.showWarningBox(this, "Die früheste Startzeit größer als die späteste Startzeit, daher ist es unmöglich Angriffe zu bestimmen.", "Startzeiten fehlerhaft");
                result = false;
            }
            if (maxSendTime.getTime() < System.currentTimeMillis()) {
                logger.warn("Latest start time in past (" + maxSendTime.getTime() + ")");
                JOptionPaneHelper.showWarningBox(this, "Die späteste Startzeit liegt in der Vergangenheit, daher ist es unmöglich Angriffe zu bestimmen.", "Startzeit in Vergangenheit");
                result = false;
            }

            //check arrive case
            Date minArriveTime = jArriveTimeSettingsPanel.getMinTime();
            Date maxArriveTime = jArriveTimeSettingsPanel.getMaxTime();
            if (minArriveTime.getTime() > maxArriveTime.getTime()) {
                logger.warn("Earliest arrive time bigger than latest arrivetime (" + minArriveTime.getTime() + ">" + maxArriveTime.getTime() + ")");
                JOptionPaneHelper.showWarningBox(this, "Die früheste Ankunftszeit größer als die späteste Ankunftszeit, daher ist es unmöglich Angriffe zu bestimmen.", "Startzeiten fehlerhaft");
                result = false;
            }
            if (maxArriveTime.getTime() < System.currentTimeMillis()) {
                logger.warn("Latest arrive time in past (" + maxArriveTime.getTime() + ")");
                //check if start is after arrive
                JOptionPaneHelper.showWarningBox(this, "Die späteste Ankunftszeit liegt in der Vergangenheit, daher ist es unmöglich Angriffe zu bestimmen.", "Ankunftszeit in Vergangenheit");
                result = false;
            }

// </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Night bonus check">
            boolean mightBeInNightBonus = false;
            for (TimeSpan span : jArriveTimeSettingsPanel.getTimeSpans()) {
                if (span.intersectsWithNightBonus()) {
                    mightBeInNightBonus = true;
                    break;
                }
            }
            if (mightBeInNightBonus) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, "Mindestens eine der angegebenen Ankunftszeitfenster kann unter Umständen im Nachbonus liegen.\n"
                        + "Willst du die Zeitfenster entsprechend korrigieren?", "Nachtbonus", "Nein", "Ja") == JOptionPane.YES_OPTION) {
                    //correction requested
                    result = false;
                }
            }
            // </editor-fold>
            return result;
        } catch (Exception e) {
            logger.error("Failed to validate settings panel", e);
            return false;
        }
    }

    /**Return whether to use BruteForce or Iterix as algorithm*/
    public boolean useBruteForce() {
        return (jAlgoBox.getSelectedIndex() == 0);
    }

    public boolean fakeOffTargets() {
        return jFakeOffTargetsBox.isSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameValidityGroup = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jAlgoBox = new javax.swing.JComboBox();
        jFakeOffTargetsBox = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jSendTimeSettingsPanel = new de.tor.tribes.ui.algo.TimeSettingsPanel();
        jArriveTimeSettingsPanel = new de.tor.tribes.ui.algo.TimeSettingsPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        timeFrameVisualizer1 = new de.tor.tribes.ui.algo.TimeFrameVisualizer();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(0, 0, 0), null), "Sonstige Einstellungen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jPanel3.setOpaque(false);

        jLabel6.setText("Zielsuche");

        jAlgoBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Zufällig", "Systematisch" }));
        jAlgoBox.setToolTipText("Komplexität der Berechnung");
        jAlgoBox.setMaximumSize(new java.awt.Dimension(100, 20));
        jAlgoBox.setMinimumSize(new java.awt.Dimension(100, 20));
        jAlgoBox.setPreferredSize(new java.awt.Dimension(100, 20));

        jFakeOffTargetsBox.setText("Off-Ziele faken");
        jFakeOffTargetsBox.setToolTipText("Ziele die nicht als Fake-Ziel markiert sind für Fakes sperren");
        jFakeOffTargetsBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jFakeOffTargetsBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jFakeOffTargetsBox.setIconTextGap(60);
        jFakeOffTargetsBox.setMargin(new java.awt.Insets(2, 0, 2, 2));
        jFakeOffTargetsBox.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(jAlgoBox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jFakeOffTargetsBox))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jAlgoBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jFakeOffTargetsBox)
                .addContainerGap(264, Short.MAX_VALUE))
        );

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Zeiteinstellungen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jSendTimeSettingsPanel.setAllowExactDayArrival(false);
        jSendTimeSettingsPanel.setAllowSetMaxTimeToMinTime(false);
        jSendTimeSettingsPanel.setAllowSetMaxTimeToMinTimePlus1Hour(false);
        jSendTimeSettingsPanel.setMinMaxTimeLabel("Nicht vor dem");
        jTabbedPane1.addTab("Start", new javax.swing.ImageIcon(getClass().getResource("/res/ui/move_out.png")), jSendTimeSettingsPanel); // NOI18N

        jArriveTimeSettingsPanel.setAllowDisableMaxTime(false);
        jArriveTimeSettingsPanel.setAllowTribeSpecificFrames(false);
        jArriveTimeSettingsPanel.setMinMaxTimeLabel("Nicht vor dem");
        jArriveTimeSettingsPanel.setTimeFrameLabel("Ankunftzeitfenster");
        jTabbedPane1.addTab("Ankunft", new javax.swing.ImageIcon(getClass().getResource("/res/ui/move_in.png")), jArriveTimeSettingsPanel); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Zeitrahmendarstellung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jButton1.setToolTipText("Zeitrahmendarstellung aktualisieren");
        jButton1.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRefreshTimeFrameVisualizerEvent(evt);
            }
        });

        javax.swing.GroupLayout timeFrameVisualizer1Layout = new javax.swing.GroupLayout(timeFrameVisualizer1);
        timeFrameVisualizer1.setLayout(timeFrameVisualizer1Layout);
        timeFrameVisualizer1Layout.setHorizontalGroup(
            timeFrameVisualizer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 636, Short.MAX_VALUE)
        );
        timeFrameVisualizer1Layout.setVerticalGroup(
            timeFrameVisualizer1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 107, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(timeFrameVisualizer1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fireRefreshTimeFrameVisualizerEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRefreshTimeFrameVisualizerEvent
        timeFrameVisualizer1.refresh(getTimeFrame());
    }//GEN-LAST:event_fireRefreshTimeFrameVisualizerEvent
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup frameValidityGroup;
    private javax.swing.JComboBox jAlgoBox;
    private de.tor.tribes.ui.algo.TimeSettingsPanel jArriveTimeSettingsPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jFakeOffTargetsBox;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private de.tor.tribes.ui.algo.TimeSettingsPanel jSendTimeSettingsPanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private de.tor.tribes.ui.algo.TimeFrameVisualizer timeFrameVisualizer1;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new SettingsPanel());
        f.pack();
        f.setVisible(true);
    }
}
