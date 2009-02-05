/*
 * DSWorkbenchMarkerFrame.java
 *
 * Created on 28. September 2008, 15:13
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Marker;
import de.tor.tribes.util.Constants;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.mark.MarkerManagerListener;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import de.tor.tribes.ui.renderer.ColorCellRenderer;
import de.tor.tribes.ui.renderer.MarkerPanelCellRenderer;
import de.tor.tribes.ui.editors.ColorChooserCellEditor;
import de.tor.tribes.ui.renderer.MapRenderer;
import de.tor.tribes.util.GlobalOptions;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;

/**
 *
 * @author  Charon
 */
public class DSWorkbenchMarkerFrame extends AbstractDSWorkbenchFrame implements MarkerManagerListener {

    private static Logger logger = Logger.getLogger(DSWorkbenchMarkerFrame.class);
    private static DSWorkbenchMarkerFrame SINGLETON = null;
    private List<TableCellRenderer> mHeaderRenderers = null;
    // private List<DSWorkbenchFrameListener> mFrameListeners = null;

    /** Creates new form DSWorkbenchMarkerFrame */
    DSWorkbenchMarkerFrame() {
        //mFrameListeners = new LinkedList<DSWorkbenchFrameListener>();
        initComponents();
        // getContentPane().setBackground(Constants.DS_BACK);
        try {
            jMarkerFrameAlwaysOnTop.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("marker.frame.alwaysOnTop")));
            setAlwaysOnTop(jMarkerFrameAlwaysOnTop.isSelected());
        } catch (Exception e) {
            //setting not available
        }
        mHeaderRenderers = new LinkedList<TableCellRenderer>();

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };

        for (int i = 0; i < 2; i++) {
            mHeaderRenderers.add(headerRenderer);
        }

        jMarkerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selected = jMarkerTable.getSelectedRows().length;
                if (selected == 0) {
                    setTitle("Markierungen");
                } else if (selected == 1) {
                    setTitle("Markierungen (1 Markierung ausgewählt)");
                } else if (selected > 1) {
                    setTitle("Markierungen (" + selected + " Markierungen ausgewählt)");
                }
            }
        });

        //set marked only button
        try {
            jToggleDrawFilterButton.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("draw.marked.only")));
        } catch (Exception e) {
        }

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.markers_view", GlobalOptions.getHelpBroker().getHelpSet());
// </editor-fold>

        pack();
    }

    public static DSWorkbenchMarkerFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchMarkerFrame();
        }
        return SINGLETON;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMarkerPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMarkerTable = new javax.swing.JTable();
        jRemoveMarkerButton = new javax.swing.JButton();
        jToggleDrawFilterButton = new javax.swing.JToggleButton();
        jMarkerFrameAlwaysOnTop = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        setTitle(bundle.getString("DSWorkbenchMarkerFrame.title")); // NOI18N

        jMarkerPanel.setBackground(new java.awt.Color(239, 235, 223));
        jMarkerPanel.setMaximumSize(new java.awt.Dimension(750, 305));
        jMarkerPanel.setMinimumSize(new java.awt.Dimension(750, 305));

        jScrollPane1.setBackground(new java.awt.Color(239, 235, 223));
        jScrollPane1.setOpaque(false);

        jMarkerTable.setBackground(new java.awt.Color(239, 235, 223));
        jMarkerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Markierung"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jMarkerTable.setGridColor(new java.awt.Color(239, 235, 223));
        jMarkerTable.setOpaque(false);
        jMarkerTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jMarkerTable);

        jRemoveMarkerButton.setBackground(new java.awt.Color(239, 235, 223));
        jRemoveMarkerButton.setText(bundle.getString("DSWorkbenchMarkerFrame.jRemoveMarkerButton.text")); // NOI18N
        jRemoveMarkerButton.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jRemoveMarkerButton.toolTipText")); // NOI18N
        jRemoveMarkerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveMarkerEvent(evt);
            }
        });

        jToggleDrawFilterButton.setBackground(new java.awt.Color(239, 235, 223));
        jToggleDrawFilterButton.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jToggleDrawFilterButton.toolTipText")); // NOI18N
        jToggleDrawFilterButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleDrawFilterButton.setLabel(bundle.getString("DSWorkbenchMarkerFrame.jToggleDrawFilterButton.label")); // NOI18N
        jToggleDrawFilterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireDrawFilterClickedEvent(evt);
            }
        });

        javax.swing.GroupLayout jMarkerPanelLayout = new javax.swing.GroupLayout(jMarkerPanel);
        jMarkerPanel.setLayout(jMarkerPanelLayout);
        jMarkerPanelLayout.setHorizontalGroup(
            jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMarkerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToggleDrawFilterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRemoveMarkerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jMarkerPanelLayout.setVerticalGroup(
            jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMarkerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addGroup(jMarkerPanelLayout.createSequentialGroup()
                        .addComponent(jRemoveMarkerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleDrawFilterButton)))
                .addContainerGap())
        );

        jMarkerFrameAlwaysOnTop.setText(bundle.getString("DSWorkbenchMarkerFrame.jMarkerFrameAlwaysOnTop.text")); // NOI18N
        jMarkerFrameAlwaysOnTop.setOpaque(false);
        jMarkerFrameAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireMarkerFrameOnTopEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jMarkerFrameAlwaysOnTop)
                    .addComponent(jMarkerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 563, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jMarkerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jMarkerFrameAlwaysOnTop)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireRemoveMarkerEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveMarkerEvent
    int[] rows = jMarkerTable.getSelectedRows();
    if (rows.length == 0) {
        return;
    }
    String message = ((rows.length == 1) ? "Markierung " : (rows.length + " Markierungen ")) + "wirklich löschen?";

    UIManager.put("OptionPane.noButtonText", "Nein");
    UIManager.put("OptionPane.yesButtonText", "Ja");
    int ret = JOptionPane.showConfirmDialog(this, message, "Löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    UIManager.put("OptionPane.noButtonText", "No");
    UIManager.put("OptionPane.yesButtonText", "Yes");
    if (ret == JOptionPane.YES_OPTION) {
        //get markers to remove
        List<Marker> toRemove = new LinkedList<Marker>();
        for (int i = rows.length - 1; i >= 0; i--) {
            jMarkerTable.invalidate();
            int row = jMarkerTable.convertRowIndexToModel(rows[i]);
            MarkerCell cell = ((MarkerCell) ((DefaultTableModel) jMarkerTable.getModel()).getValueAt(row, 0));
            if (cell.getType() == Marker.TRIBE_MARKER_TYPE) {
                toRemove.add(MarkerManager.getSingleton().getMarker(cell.getTribe()));
            } else {
                toRemove.add(MarkerManager.getSingleton().getMarker(cell.getAlly()));
            }

            jMarkerTable.revalidate();
        }
        //remove all selected markers and update the view once
        MarkerManager.getSingleton().removeMarkers(toRemove.toArray(new Marker[]{}));
    }
}//GEN-LAST:event_fireRemoveMarkerEvent

private void fireMarkerFrameOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireMarkerFrameOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireMarkerFrameOnTopEvent

private void fireDrawFilterClickedEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireDrawFilterClickedEvent
    GlobalOptions.addProperty("draw.marked.only", Boolean.toString(jToggleDrawFilterButton.isSelected()));
    MinimapPanel.getSingleton().redraw();
    MapPanel.getSingleton().getMapRenderer().initiateRedraw(MapRenderer.ALL_LAYERS);
}//GEN-LAST:event_fireDrawFilterClickedEvent

    public void firePublicDrawMarkedOnlyChangedEvent() {
        boolean v = jToggleDrawFilterButton.isSelected();
        jToggleDrawFilterButton.setSelected(!v);
        GlobalOptions.addProperty("draw.marked.only", Boolean.toString(v));
        MinimapPanel.getSingleton().redraw();
        MapPanel.getSingleton().getMapRenderer().initiateRedraw(MapRenderer.ALL_LAYERS);
    }

    /**Setup marker panel*/
    protected void setupMarkerPanel() {
        jMarkerTable.invalidate();
        jMarkerTable.setModel(MarkerManager.getSingleton().getTableModel());
        MarkerManager.getSingleton().addMarkerManagerListener(this);
        //setup renderer and general view
        jMarkerTable.setDefaultRenderer(Color.class, new ColorCellRenderer());
        jMarkerTable.setDefaultRenderer(MarkerCell.class, new MarkerPanelCellRenderer());
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jMarkerTable.getModel());
        jMarkerTable.setRowSorter(sorter);

        // <editor-fold defaultstate="collapsed" desc=" Add color chooser ActionListener ">

        ColorChooserCellEditor editor = new ColorChooserCellEditor(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //update markers as soon as the colorchooser cell editor has closed
                try {
                    MarkerCell cell = ((MarkerCell) jMarkerTable.getModel().getValueAt(jMarkerTable.getSelectedRow(), 0));
                    Marker m = null;
                    if (cell.getType() == Marker.TRIBE_MARKER_TYPE) {
                        m = MarkerManager.getSingleton().getMarker(cell.getTribe());
                    } else {
                        m = MarkerManager.getSingleton().getMarker(cell.getAlly());
                    }

                    Color color = (Color) jMarkerTable.getModel().getValueAt(jMarkerTable.getSelectedRow(), 1);
                    if (m != null && color != null) {

                        m.setMarkerColor(color);
                        MarkerManager.getSingleton().markerUpdatedExternally();
                    }
                } catch (NullPointerException npe) {
                    //ignored
                }
            }
        });
        // </editor-fold>

        jMarkerTable.setDefaultEditor(Color.class, editor);
        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        //update view
        MarkerManager.getSingleton().markerUpdatedExternally();
        jMarkerTable.revalidate();
        jMarkerTable.updateUI();
    }

    @Override
    public void fireMarkersChangedEvent() {
        jMarkerTable.invalidate();
        jMarkerTable.setModel(MarkerManager.getSingleton().getTableModel());

        //setup marker table view
        jMarkerTable.getColumnModel().getColumn(1).setMaxWidth(75);

        for (int i = 0; i < jMarkerTable.getColumnCount(); i++) {
            jMarkerTable.getColumn(jMarkerTable.getColumnName(i)).setHeaderRenderer(mHeaderRenderers.get(i));
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(MarkerManager.getSingleton().getTableModel());
        jMarkerTable.setRowSorter(sorter);
        jMarkerTable.revalidate();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jMarkerFrameAlwaysOnTop;
    private javax.swing.JPanel jMarkerPanel;
    private javax.swing.JTable jMarkerTable;
    private javax.swing.JButton jRemoveMarkerButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleDrawFilterButton;
    // End of variables declaration//GEN-END:variables
}
