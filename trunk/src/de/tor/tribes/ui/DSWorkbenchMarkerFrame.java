/*
 * DSWorkbenchMarkerFrame.java
 *
 * Created on 28. September 2008, 15:13
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Marker;
import de.tor.tribes.types.MarkerSet;
import de.tor.tribes.ui.dnd.GhostDropEvent;
import de.tor.tribes.ui.dnd.GhostDropListener;
import de.tor.tribes.util.Constants;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.mark.MarkerManagerListener;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import de.tor.tribes.ui.renderer.ColorCellRenderer;
import de.tor.tribes.ui.renderer.MarkerPanelCellRenderer;
import de.tor.tribes.ui.editors.ColorChooserCellEditor;
import de.tor.tribes.ui.models.MarkerTableModel;
import de.tor.tribes.ui.renderer.MapRenderer;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;

/**
 * @TODO (DIFF) Added correct sort order
 * @author  Charon
 */
public class DSWorkbenchMarkerFrame extends AbstractDSWorkbenchFrame implements MarkerManagerListener, GhostDropListener {

    private static Logger logger = Logger.getLogger("MarkerView");
    private static DSWorkbenchMarkerFrame SINGLETON = null;
    private List<TableCellRenderer> mHeaderRenderers = null;
    // private List<DSWorkbenchFrameListener> mFrameListeners = null;

    /** Creates new form DSWorkbenchMarkerFrame */
    DSWorkbenchMarkerFrame() {
        initComponents();
        try {
            jMarkerFrameAlwaysOnTop.setSelected(Boolean.parseBoolean(GlobalOptions.getProperty("marker.frame.alwaysOnTop")));
            setAlwaysOnTop(jMarkerFrameAlwaysOnTop.isSelected());
        } catch (Exception e) {
            //setting not available
        }

      //  GhostGlassPane pane = new GhostGlassPane();
        // DragSource dragSource = new DragSource();
        //  DropTarget dropTarget2 = new DropTarget(jMarkerPanel, DnDConstants.ACTION_COPY, new DDropSource(pane));
        // DragGestureRecognizer dragRecognizer1 = dragSource.createDefaultDragGestureRecognizer(jLabel1, DnDConstants.ACTION_COPY, new DDragSource(pane));
     /*   setGlassPane(pane);
        GhostComponentAdapter adapter = new GhostComponentAdapter(pane, "button_pushed");
        jLabel1.addMouseMotionListener(adapter);
        jLabel1.addMouseListener(adapter);
        adapter.addGhostDropListener(this);*/

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
        jAddRenameDialog.pack();
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

        jAddRenameDialog = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jSetName = new javax.swing.JTextField();
        jOKButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jMarkerPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMarkerTable = new javax.swing.JTable();
        jRemoveMarkerButton = new javax.swing.JButton();
        jToggleDrawFilterButton = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jMarkerSetList = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMarkerFrameAlwaysOnTop = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        jLabel2.setText(bundle.getString("DSWorkbenchMarkerFrame.jLabel2.text")); // NOI18N

        jSetName.setText(bundle.getString("DSWorkbenchMarkerFrame.jSetName.text")); // NOI18N

        jOKButton.setText(bundle.getString("DSWorkbenchMarkerFrame.jOKButton.text")); // NOI18N
        jOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCloseAddRenameDialogEvent(evt);
            }
        });

        jButton5.setText(bundle.getString("DSWorkbenchMarkerFrame.jButton5.text")); // NOI18N
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCloseAddRenameDialogEvent(evt);
            }
        });

        javax.swing.GroupLayout jAddRenameDialogLayout = new javax.swing.GroupLayout(jAddRenameDialog.getContentPane());
        jAddRenameDialog.getContentPane().setLayout(jAddRenameDialogLayout);
        jAddRenameDialogLayout.setHorizontalGroup(
            jAddRenameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRenameDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSetName, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jAddRenameDialogLayout.createSequentialGroup()
                .addContainerGap(118, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jOKButton)
                .addContainerGap())
        );
        jAddRenameDialogLayout.setVerticalGroup(
            jAddRenameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddRenameDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAddRenameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSetName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jAddRenameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jOKButton)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        jRemoveMarkerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/att_remove.png"))); // NOI18N
        jRemoveMarkerButton.setText(bundle.getString("DSWorkbenchMarkerFrame.jRemoveMarkerButton.text")); // NOI18N
        jRemoveMarkerButton.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jRemoveMarkerButton.toolTipText")); // NOI18N
        jRemoveMarkerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveMarkerEvent(evt);
            }
        });

        jToggleDrawFilterButton.setBackground(new java.awt.Color(239, 235, 223));
        jToggleDrawFilterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/draw_filter.gif"))); // NOI18N
        jToggleDrawFilterButton.setText(bundle.getString("DSWorkbenchMarkerFrame.jToggleDrawFilterButton.text")); // NOI18N
        jToggleDrawFilterButton.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jToggleDrawFilterButton.toolTipText")); // NOI18N
        jToggleDrawFilterButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleDrawFilterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireDrawFilterClickedEvent(evt);
            }
        });

        jLabel1.setText(bundle.getString("DSWorkbenchMarkerFrame.jLabel1.text")); // NOI18N

        jMarkerSetList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireActiveMarkerSetChangedEvent(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setText(bundle.getString("DSWorkbenchMarkerFrame.jButton1.text")); // NOI18N
        jButton1.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jButton1.toolTipText")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(27, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(27, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(27, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddSetEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton2.setText(bundle.getString("DSWorkbenchMarkerFrame.jButton2.text")); // NOI18N
        jButton2.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jButton2.toolTipText")); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(27, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(27, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(27, 25));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveSetEvent(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(239, 235, 223));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jButton3.setText(bundle.getString("DSWorkbenchMarkerFrame.jButton3.text")); // NOI18N
        jButton3.setToolTipText(bundle.getString("DSWorkbenchMarkerFrame.jButton3.toolTipText")); // NOI18N
        jButton3.setMaximumSize(new java.awt.Dimension(27, 25));
        jButton3.setMinimumSize(new java.awt.Dimension(27, 25));
        jButton3.setPreferredSize(new java.awt.Dimension(27, 25));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRenameSetEvent(evt);
            }
        });

        javax.swing.GroupLayout jMarkerPanelLayout = new javax.swing.GroupLayout(jMarkerPanel);
        jMarkerPanel.setLayout(jMarkerPanelLayout);
        jMarkerPanelLayout.setHorizontalGroup(
            jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jMarkerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jMarkerPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMarkerSetList, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToggleDrawFilterButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRemoveMarkerButton))
                .addContainerGap())
        );
        jMarkerPanelLayout.setVerticalGroup(
            jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMarkerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jMarkerSetList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jMarkerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jMarkerPanelLayout.createSequentialGroup()
                        .addComponent(jRemoveMarkerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleDrawFilterButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                .addContainerGap())
        );

        jRemoveMarkerButton.getAccessibleContext().setAccessibleDescription(bundle.getString("DSWorkbenchMarkerFrame.jRemoveMarkerButton.AccessibleContext.accessibleDescription")); // NOI18N
        jToggleDrawFilterButton.getAccessibleContext().setAccessibleDescription(bundle.getString("DSWorkbenchMarkerFrame.jToggleDrawFilterButton.AccessibleContext.accessibleDescription")); // NOI18N

        jMarkerFrameAlwaysOnTop.setText(bundle.getString("DSWorkbenchMarkerFrame.jMarkerFrameAlwaysOnTop.text")); // NOI18N
        jMarkerFrameAlwaysOnTop.setOpaque(false);
        jMarkerFrameAlwaysOnTop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
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

    if (JOptionPaneHelper.showQuestionConfirmBox(this, message, "Löschen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
        //get markers to remove
        List<Marker> toRemove = new LinkedList<Marker>();
        for (int i = rows.length - 1; i >= 0; i--) {
            jMarkerTable.invalidate();
            int row = jMarkerTable.convertRowIndexToModel(rows[i]);
            MarkerCell cell = (MarkerCell) MarkerTableModel.getSingleton().getValueAt(row, 0);
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

private void fireDrawFilterClickedEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireDrawFilterClickedEvent
    GlobalOptions.addProperty("draw.marked.only", Boolean.toString(jToggleDrawFilterButton.isSelected()));
    MinimapPanel.getSingleton().redraw();
    MapPanel.getSingleton().getMapRenderer().initiateRedraw(MapRenderer.ALL_LAYERS);
}//GEN-LAST:event_fireDrawFilterClickedEvent

private void fireMarkerFrameOnTopEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireMarkerFrameOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireMarkerFrameOnTopEvent

private void fireActiveMarkerSetChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireActiveMarkerSetChangedEvent
    if (evt.getStateChange() == ItemEvent.SELECTED) {
        jMarkerTable.invalidate();

        try {
            jMarkerTable.getCellEditor().cancelCellEditing();
        } catch (Exception e) {
        }
        jMarkerTable.getSelectionModel().clearSelection();
        jMarkerTable.setRowSorter(new TableRowSorter(MarkerTableModel.getSingleton()));
        MarkerTableModel.getSingleton().setActiveSet((String) jMarkerSetList.getSelectedItem());

        jMarkerTable.repaint();//.updateUI();
        jMarkerTable.revalidate();
        MinimapPanel.getSingleton().redraw();
    }
}//GEN-LAST:event_fireActiveMarkerSetChangedEvent

private void fireAddSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddSetEvent
    jSetName.setText("");
    jAddRenameDialog.setTitle("Set hinzufügen");
    jAddRenameDialog.setLocationRelativeTo(this);
    jAddRenameDialog.setVisible(true);
}//GEN-LAST:event_fireAddSetEvent

private void fireRenameSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRenameSetEvent
    String set = (String) jMarkerSetList.getSelectedItem();
    if (set == null) {
        return;
    }
    if (set.equals("default")) {
        JOptionPaneHelper.showInformationBox(this, "Das Standard Markierungsset kann nicht umgenannt werden.", "Information");
        return;
    }
    jSetName.setText(set);
    jAddRenameDialog.setTitle("Umbenennen");
    jAddRenameDialog.setLocationRelativeTo(this);
    jAddRenameDialog.setVisible(true);
}//GEN-LAST:event_fireRenameSetEvent

private void fireRemoveSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveSetEvent
    String set = (String) jMarkerSetList.getSelectedItem();
    if (set == null) {
        return;
    }
    if (set.equals("default")) {
        JOptionPaneHelper.showInformationBox(this, "Das Standard Markierungsset kann nicht entfernt werden.", "Information");
    } else {
        if (JOptionPaneHelper.showQuestionConfirmBox(this, "Markierungsset '" + set + "' entfernen?", "Set entfernen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
            MarkerManager.getSingleton().removeSet(set);
        }
    }
    rebuildSetList();
}//GEN-LAST:event_fireRemoveSetEvent

private void fireCloseAddRenameDialogEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCloseAddRenameDialogEvent
    if (evt.getSource() == jOKButton) {
        String newName = jSetName.getText();
        if (newName.length() < 0) {
            JOptionPaneHelper.showWarningBox(jAddRenameDialog, "Ungültiger Set Name", "Fehler");
            return;
        }
        if (jAddRenameDialog.getTitle().equals("Umbenennen")) {
            String oldName = (String) jMarkerSetList.getSelectedItem();
            if (MarkerManager.getSingleton().getMarkerSet(newName) != null) {
                JOptionPaneHelper.showWarningBox(jAddRenameDialog, "Ein Set mit dem angegebenen Namen existiert bereits", "Fehler");
                return;
            }
            MarkerSet set = MarkerManager.getSingleton().removeSet(oldName);
            set.setSetName(newName);
            MarkerManager.getSingleton().addMarkerSet(set);
        } else {
            if (MarkerManager.getSingleton().getMarkerSet(newName) != null) {
                JOptionPaneHelper.showWarningBox(jAddRenameDialog, "Ein Set mit dem angegebenen Namen existiert bereits", "Fehler");
                return;
            }
            MarkerManager.getSingleton().addMarkerSet(newName);
        }
    }
    jAddRenameDialog.setVisible(false);
    rebuildSetList();
}//GEN-LAST:event_fireCloseAddRenameDialogEvent

    private void rebuildSetList() {
        String item = (String) jMarkerSetList.getSelectedItem();
        if (item == null || MarkerManager.getSingleton().getMarkerSet(item) == null) {
            //item will be not in list any longer
            item = "default";
            //set item immediately and redraw minimap
            MarkerTableModel.getSingleton().setActiveSet(item);
            MinimapPanel.getSingleton().redraw();
        } else {
            MarkerTableModel.getSingleton().setActiveSet(item);
        }
        jMarkerSetList.setModel(new DefaultComboBoxModel(MarkerManager.getSingleton().getMarkerSets()));
        jMarkerSetList.setSelectedItem(item);
        MarkerTableModel.getSingleton().fireTableDataChanged();

        //jMarkerTable.repaint();
    }

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
        jMarkerTable.setModel(MarkerTableModel.getSingleton());
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
                    Color color = (Color) MarkerTableModel.getSingleton().getValueAt(jMarkerTable.getSelectedRow(), 1);
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
        jMarkerSetList.setModel(new DefaultComboBoxModel(MarkerManager.getSingleton().getMarkerSets()));
        //update view
        MarkerManager.getSingleton().markerUpdatedExternally();
        jMarkerTable.revalidate();
        jMarkerTable.repaint();//.updateUI();
    }

    @Override
    public void fireMarkersChangedEvent() {
        jMarkerTable.invalidate();
        jMarkerTable.setModel(MarkerTableModel.getSingleton());

        //setup marker table view
        jMarkerTable.getColumnModel().getColumn(1).setMaxWidth(75);

        for (int i = 0; i < jMarkerTable.getColumnCount(); i++) {
            jMarkerTable.getColumn(jMarkerTable.getColumnName(i)).setHeaderRenderer(mHeaderRenderers.get(i));
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(MarkerTableModel.getSingleton());
        jMarkerTable.setRowSorter(sorter);
        jMarkerTable.revalidate();
        rebuildSetList();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog jAddRenameDialog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JCheckBox jMarkerFrameAlwaysOnTop;
    private javax.swing.JPanel jMarkerPanel;
    private javax.swing.JComboBox jMarkerSetList;
    private javax.swing.JTable jMarkerTable;
    private javax.swing.JButton jOKButton;
    private javax.swing.JButton jRemoveMarkerButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jSetName;
    private javax.swing.JToggleButton jToggleDrawFilterButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void ghostDropped(GhostDropEvent e) {
        System.out.println("DROPPED " + e.getAction());

    }
}
