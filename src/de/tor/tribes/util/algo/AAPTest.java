/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.algo;

import de.tor.tribes.util.algo.types.OffVillage;
import de.tor.tribes.util.algo.types.TargetVillage;
import de.tor.tribes.util.algo.types.Destination;
import de.tor.tribes.util.algo.types.Coordinate;
import java.util.ArrayList;
import java.util.Hashtable;

import java.io.*;
import javax.swing.JFrame;

/**
 * Demonstrates how to make use of the stp package.
 *
 * @author Robert Nitsch <dev@robertnitsch.de>
 */
public class AAPTest {

    @SuppressWarnings("unchecked")
    public static void main(String args[]) {
        // Source and destination lists
        ArrayList<OffVillage> offs = new ArrayList<OffVillage>();
        ArrayList<TargetVillage> targets = new ArrayList<TargetVillage>();

        // Sources
        String input_offs[] = {"100|100", "150|200", "200|200", "250|250", "300|300", "350|350", "400|400", "377|244"};
        for (String coord : input_offs) {
            String coords[] = coord.split("\\|");
            int wares = 1;
            /*if (Integer.parseInt(coords[1]) == 200) {
                wares = 8;
            }*/
            offs.add(new OffVillage(new Coordinate(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])), wares));
        }

        // Destinations
        String input_targets[] = {"100|150", "100|150", "100|150", "200|250", "180|210", "80|190", "121|362", "234|223"};
        for (String coord : input_targets) {
            String coords[] = coord.split("\\|");
             int wares = 1;
           /* if (Integer.parseInt(coords[0]) == 100) {
                wares = 5;
            }*/
            targets.add(new TargetVillage(new Coordinate(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])), wares));
        }

        // Transport costs
        Hashtable<Destination, Double> costs[] = new Hashtable[offs.size()];
        for (int i = 0; i < offs.size(); i++) {
            costs[i] = new Hashtable<Destination, Double>();
            for (int j = 0; j < targets.size(); j++) {
                
                
                costs[i].put(targets.get(j), offs.get(i).distanceTo(targets.get(j)));
                System.out.println(targets.get(j).getC().getX() + "|" + (targets.get(j)).getC().getY());
                System.out.println(offs.get(i).distanceTo(targets.get(j)));
            }
        }

        // Create an algorithm instance
        Optex<OffVillage, TargetVillage> algo = new Optex<OffVillage, TargetVillage>(offs, targets, costs);

        // Run the algorithm
        try {
            algo.run();
            draw(offs, targets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void draw(ArrayList<OffVillage> offs, ArrayList<TargetVillage> targets) {
        JFrame drawer = new JFrame("Drawer");
        STPDrawer stpdrawer = new STPDrawer(offs, targets);
        stpdrawer.setSize(500, 500);
        drawer.add(stpdrawer);

        drawer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawer.setSize(500, 500);
        drawer.setVisible(true);
    }

    public static String readLine(String prompt) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(prompt);
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

