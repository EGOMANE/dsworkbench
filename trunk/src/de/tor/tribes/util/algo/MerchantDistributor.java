/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.algo;

import de.tor.tribes.util.parser.MerchantParser.VillageMerchantInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jejkal
 */
public class MerchantDistributor {

    public MerchantDistributor() {
    }

    public List<List<MerchantSource>> calculate(List<VillageMerchantInfo> pInfos, int[] pTargetRes, int[] pRemainRes) {
        return calculate(pInfos, new LinkedList<de.tor.tribes.types.Village>(), new LinkedList<de.tor.tribes.types.Village>(), pTargetRes, pRemainRes);
    }

    public List<List<MerchantSource>> calculate(List<VillageMerchantInfo> pInfos, List<de.tor.tribes.types.Village> pIncomingOnly, List<de.tor.tribes.types.Village> pOutgoingOnly, int[] pTargetRes, int[] pRemainRes) {
        int[] targetRes = pTargetRes;//new int[]{100000, 100000, 100000};
        int[] minRemainRes = pRemainRes;//new int[]{100000, 100000, 100000};
        /* for (VillageMerchantInfo info : pInfos) {
        System.out.println(info);
        }
        System.out.println("Target: " + targetRes[0] + "/" + targetRes[1] + "/" + targetRes[2]);
         */
        // System.out.println("INCO " + pIncomingOnly);
        //  System.out.println("OUTO " + pOutgoingOnly);
        ArrayList<MerchantSource> sources = new ArrayList<MerchantSource>();
        ArrayList<MerchantDestination> destinations = new ArrayList<MerchantDestination>();
        List<List<MerchantSource>> results = new LinkedList<List<MerchantSource>>();
        for (int i = 0; i < targetRes.length; i++) {
            sources.clear();
            destinations.clear();
            for (VillageMerchantInfo info : pInfos) {
                int res = 0;
                switch (i) {
                    case 0:
                        res = info.getWoodStock();
                        break;
                    case 1:
                        res = info.getClayStock();
                        break;
                    case 2:
                        res = info.getIronStock();
                        break;
                }
                int targetValue = targetRes[i];
                int maxAvailable = (int) (Math.round((double) Math.abs(res - minRemainRes[i]) / 1000.0 + .5));
                int maxDelivery = info.getAvailableMerchants();
                //try to add receiver
                if (maxAvailable < 0 || res < targetValue) {
                    // System.out.println("< 0 or target for " + info.getVillage());
                    //check if target value fits into the stash

                    if (targetRes[i] > info.getStashCapacity()) {
                        targetValue = info.getStashCapacity();
                    }

                    // System.out.println("Res: " + res + "/" + targetValue);
                    int need = (int) (Math.round((double) Math.abs(targetValue - res) / 1000.0 + .5));
                    if (res + need > info.getStashCapacity()) {
                        need -= (res + need - info.getStashCapacity());
                    }

                    // System.out.println("Need for " + info.getVillage() + ": " + need);
                    if (need > 0 && !pOutgoingOnly.contains(info.getVillage())) {
                        // System.out.println("Receiver " + info + " -> " + need);
                        //add to destination list

                        MerchantDestination d = new MerchantDestination(new Coordinate(info.getVillage().getX(), info.getVillage().getY()), need);
                        destinations.add(d);
                    }
                }

                //try to add sender

                if (maxAvailable > maxDelivery) {
                    // System.out.println("> maxDe for " + info.getVillage() + " ( " + maxDelivery + ")");
                    //    System.out.println("Deliverer " + info + " -> " + maxDelivery);
                    //use max capacity
                    if (!pIncomingOnly.contains(info.getVillage()) && maxAvailable > 0) {
                       // System.out.println("Deliverer " + info.getVillage() + " -> " + maxAvailable);
                        MerchantSource s = new MerchantSource(new Coordinate(info.getVillage().getX(), info.getVillage().getY()), maxDelivery);
                        sources.add(s);
                    }
                } else {
                    //use max available
                    // System.out.println("< maxDe for " + info.getVillage());
                    /*if (maxAvailable > maxDelivery) {
                        maxAvailable = maxDelivery;
                    }*/
                    // System.out.println(" - maxA " + maxAvailable);
                   // System.out.println("Deliverer " + info + " -> " + maxAvailable);
                    if (!pIncomingOnly.contains(info.getVillage()) && maxAvailable > 0) {
                        //     System.out.println("Deliverer " + info.getVillage() + " -> " + maxAvailable);
                        MerchantSource s = new MerchantSource(new Coordinate(info.getVillage().getX(), info.getVillage().getY()), maxAvailable);
                        sources.add(s);
                    }

                }
            }
            //calculate
            if (sources.isEmpty() || destinations.isEmpty()) {
                // System.out.println("Nothing todo in round " + i);
                results.add(new LinkedList<MerchantSource>());
            } else {
                new MerchantDistributor().calculate(sources, destinations);
                List<MerchantSource> sourcesCopy = new LinkedList<MerchantSource>(sources);
                results.add(sourcesCopy);
                //Collections.copy(sources, sources);

                // <editor-fold defaultstate="collapsed" desc=" Result building">
                for (MerchantSource source : sources) {
                    for (Order o : source.getOrders()) {
                        int amount = o.getAmount();

                        int needMerchants = amount;
                        MerchantDestination d = (MerchantDestination) o.getDestination();
                        //System.out.println(source + " " + o);
                        for (VillageMerchantInfo info : pInfos) {
                            if (info.getVillage().getX() == source.getC().getX() && info.getVillage().getY() == source.getC().getY()) {
                                switch (i) {
                                    case 0:
                                        info.setWoodStock(info.getWoodStock() - amount * 1000);
                                        break;
                                    case 1:
                                        info.setClayStock(info.getClayStock() - amount * 1000);
                                        break;
                                    case 2:
                                        info.setIronStock(info.getIronStock() - amount * 1000);
                                        break;
                                }
                                info.setAvailableMerchants(info.getAvailableMerchants() - needMerchants);
                            } else if (info.getVillage().getX() == d.getC().getX() && info.getVillage().getY() == d.getC().getY()) {
                                switch (i) {
                                    case 0:
                                        info.setWoodStock(info.getWoodStock() + amount * 1000);
                                        break;
                                    case 1:
                                        info.setClayStock(info.getClayStock() + amount * 1000);
                                        break;
                                    case 2:
                                        info.setIronStock(info.getIronStock() + amount * 1000);
                                        break;
                                }
                            }
                        }
                    }
                }
                // </editor-fold>
            }
            // System.out.println("Round done");
        }
        // System.out.println("=================");
        /* for (VillageMerchantInfo info : pInfos) {
        System.out.println(info);
        }*/
        return results;
    }

    public void calculate(ArrayList<MerchantSource> pSources, ArrayList<MerchantDestination> pDestinations) {
        //ArrayList<MerchantSource> sources = prepareSourceList();
        // = prepareDestinationList();
        Hashtable<Destination, Double>[] costs = calulateCosts(pSources, pDestinations);
        Optex<MerchantSource, MerchantDestination> algo = new Optex<MerchantSource, MerchantDestination>(pSources, pDestinations, costs);
        try {
            algo.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*  for (MerchantSource source : pSources) {
        for (Order o : source.getOrders()) {
        System.out.println(source + " " + o);
        }
        }*/
    }

    public Hashtable<Destination, Double>[] calulateCosts(
            ArrayList<MerchantSource> pSources,
            ArrayList<MerchantDestination> pDestinations) {
        Hashtable<Destination, Double> costs[] = new Hashtable[pSources.size()];
        for (int i = 0; i < pSources.size(); i++) {
            costs[i] = new Hashtable<Destination, Double>();
            for (int j = 0; j < pDestinations.size(); j++) {
                double cost = pSources.get(i).distanceTo(pDestinations.get(j));
                if (cost == 0) {
                    cost = Double.MAX_VALUE;
                }
                costs[i].put(pDestinations.get(j), cost);
            }
        }
        return costs;
    }

    public static void main(String[] args) {

        //overall settings
        int[] targetRes = new int[]{100000, 150000, 100000};
        int[] minRemainRes = new int[]{100000, 150000, 100000};

        //source settings
        Coordinate s1Coord = new Coordinate(0, 0);
        int[] s1Res = new int[]{30000, 70000, 20000};
        int s1Merchants = 110;
        Coordinate s2Coord = new Coordinate(0, 1);
        int[] s2Res = new int[]{60000, 90000, 40000};
        int s2Merchants = 110;
        //destination settings
        Coordinate d1Coord = new Coordinate(1, 1);

        int[] d1Res = new int[]{40000, 20000, 5000};

        /*  int resSum = 0;
        for (int res : targetRes) {
        resSum += res;
        }*/


        for (int i = 0; i < targetRes.length; i++) {
            System.out.println("--------Round " + i + "--------");
            ArrayList<MerchantSource> sources = new ArrayList<MerchantSource>();
            //double d = (double) targetRes[i] / (double) resSum;
            int s1MaxMerchantsNeed = s1Res[i] - minRemainRes[i];
            if (s1MaxMerchantsNeed <= 0) {
                //source not available
            } else {
                int s1MerchantMaxCapacity = (int) Math.rint((double) s1Merchants / 3.0) * 1000;
                MerchantSource source1 = null;
                if (s1MaxMerchantsNeed > s1MerchantMaxCapacity) {
                    System.out.println("S1Cap " + s1MerchantMaxCapacity);
                    source1 = new MerchantSource(s1Coord, s1MerchantMaxCapacity);
                } else {
                    System.out.println("S1Cap " + s1MaxMerchantsNeed);
                    source1 = new MerchantSource(s1Coord, s1MaxMerchantsNeed);
                }
                sources.add(source1);
            }
            int s2MaxMerchantsNeed = s2Res[i] - minRemainRes[i];
            if (s2MaxMerchantsNeed <= 0) {
                //source not available
            } else {
                int s2MerchantMaxCapacity = (int) Math.rint((double) s1Merchants / 3.0) * 1000;
                MerchantSource source2 = null;
                if (s2MaxMerchantsNeed > s2MerchantMaxCapacity) {
                    System.out.println("S2Cap " + s2MerchantMaxCapacity);
                    source2 = new MerchantSource(s2Coord, s2MerchantMaxCapacity);
                } else {
                    System.out.println("S2Cap " + s2MaxMerchantsNeed);
                    source2 = new MerchantSource(s2Coord, s2MaxMerchantsNeed);
                }
                sources.add(source2);
            }

            //CHECK!!!! Needs MUST be larger 0!
            MerchantDestination dest = new MerchantDestination(d1Coord, targetRes[i] - d1Res[i]);
            ArrayList<MerchantDestination> destinations = new ArrayList<MerchantDestination>();
            destinations.add(dest);
            new MerchantDistributor().calculate(sources, destinations);
            System.out.println("--------Round Done--------");
        }
        /*
        Offs: Hier entlang! (434|876) K84  	7.28	10.251	92.154 36.371 154.905 	400000	235/235	5212/20476	am 21.06. um 16:21 Uhr
        Offs: Hier entlang! (436|880) K84  	10.82	10.387	171.896 195.970 175.433 400000	235/235	6312/24000	am 21.06. um 06:50 Uhr
        Rattennest (-1|33) (485|866) K84  	58.55	10.019	71.270 323.198 263.667 	400000	110/110	16981/24000
        Rattennest (-31|45) (455|878) K84  	28.28	10.019	96.649 385.743 222.033 	400000	110/110	18441/24000
        Rattennest (-32|15) (454|848) K84  	37.48	10.019	23.599 219.792 160.859 	400000	110/110	10091/24000
        Rattennest (-33|44) (453|877) K84  	26.17	10.019	134.644 400.000 161.743 400000	110/110	17641/24000
         */

    }
}
