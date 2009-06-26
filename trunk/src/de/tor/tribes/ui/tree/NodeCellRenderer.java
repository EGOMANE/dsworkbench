/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.tree;

import de.tor.tribes.types.Ally;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;

/**
 *
 * @author Jejkal
 */
public class NodeCellRenderer extends DefaultTreeCellRenderer {

    private ImageIcon ally = null;
    private ImageIcon tribe = null;
    private ImageIcon village = null;

    public NodeCellRenderer() {
        ally = new ImageIcon("./graphics/icons/def.png");
        tribe = new ImageIcon("./graphics/icons/troops.png");
        village = new ImageIcon("./graphics/icons/village.png");
    }

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        try {
            MutableTreeNode node = ((MutableTreeNode) value);
            if (node instanceof AllyNode) {
                setIcon(ally);
                Ally a = ((AllyNode) node).getUserObject();
                setText(a.toString() + " [" + node.getChildCount() + " Spieler]");
            } else if (node instanceof TribeNode) {
                setIcon(tribe);
                Tribe t = ((TribeNode) node).getUserObject();
                int cnt = node.getChildCount();
                setText(t.toString() + " [" + cnt + " " + ((cnt == 1) ? "Dorf]" : "Dörfer]"));
            } else if (node instanceof VillageNode) {
                setIcon(village);
                Village v = ((VillageNode) node).getUserObject();
                setText(v.toString());
            } else if (node instanceof SelectionTreeRootNode) {
                int cnt = node.getChildCount();
                setText(((SelectionTreeRootNode) node).getUserObject() + " [" + cnt + " " + ((cnt == 1) ? "Stamm]" : "Stämme]"));
            } else {
            }
        } catch (Exception e) {
        }
        return this;
    }
}
