/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util;

import com.smardec.mousegestures.MouseGesturesListener;
import de.tor.tribes.ui.DSWorkbenchAttackFrame;
import de.tor.tribes.ui.DSWorkbenchConquersFrame;
import de.tor.tribes.ui.DSWorkbenchFormFrame;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.ui.DSWorkbenchMarkerFrame;
import de.tor.tribes.ui.DSWorkbenchNotepad;
import de.tor.tribes.ui.DSWorkbenchReportFrame;
import de.tor.tribes.ui.DSWorkbenchStatsFrame;
import de.tor.tribes.ui.DSWorkbenchTagFrame;
import de.tor.tribes.ui.DSWorkbenchTroopsFrame;
import de.tor.tribes.ui.TribeTribeAttackFrame;
import java.awt.MouseInfo;
import javax.swing.SwingUtilities;

/**
 *
 * @author Torridity
 */
public class MouseGestureHandler implements MouseGesturesListener {

    @Override
    public void processGesture(String string) {
        // System.out.println("Gesture: " + string);
        if (DSWorkbenchAttackFrame.getSingleton().isActive()) {
            DSWorkbenchAttackFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchNotepad.getSingleton().isActive()) {
            DSWorkbenchNotepad.getSingleton().handleGesture(string);
        } else if (DSWorkbenchMarkerFrame.getSingleton().isActive()) {
            DSWorkbenchMarkerFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchTroopsFrame.getSingleton().isActive()) {
            DSWorkbenchTroopsFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchFormFrame.getSingleton().isActive()) {
            DSWorkbenchFormFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchConquersFrame.getSingleton().isActive()) {
            DSWorkbenchConquersFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchTagFrame.getSingleton().isActive()) {
            DSWorkbenchTagFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchStatsFrame.getSingleton().isActive()) {
            DSWorkbenchStatsFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchReportFrame.getSingleton().isActive()) {
            DSWorkbenchReportFrame.getSingleton().handleGesture(string);
        } else if (DSWorkbenchMainFrame.getSingleton().getAttackPlaner().isActive()) {
            DSWorkbenchMainFrame.getSingleton().getAttackPlaner().handleGesture(string);
        }
    }

    @Override
    public void gestureMovementRecognized(String string) {
    }
}
