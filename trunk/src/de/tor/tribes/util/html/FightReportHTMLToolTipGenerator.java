/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.util.html;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.FightReport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Torridity
 */
public class FightReportHTMLToolTipGenerator {

    private final static String WINNER_STRING = "\\$WINNER_STRING";
    private final static String SEND_TIME = "\\$SEND_TIME";
    private final static String LUCK_STRING = "\\$LUCK_STRING";
    private final static String LUCK_BAR = "\\$LUCK_BAR";
    private final static String MORAL = "\\$MORAL";
    private final static String ATTACKER = "\\$ATTACKER";
    private final static String SOURCE = "\\$SOURCE";
    private final static String DEFENDER = "\\$DEFENDER";
    private final static String TARGET = "\\$TARGET";
    private final static String ATTACKER_TABLE = "\\$ATTACKER_TABLE";
    private final static String DEFENDER_TABLE = "\\$DEFENDER_TABLE";
    private final static String RAM_DAMAGE = "\\$RAM_DAMAGE";
    private final static String CATA_DAMAGE = "\\$CATA_DAMAGE";
    private final static String SNOB_INFLUENCE = "\\$SNOB_INFLUENCE";
    private final static String LUCK_NEG = "\\$LUCK_NEG";
    private final static String LUCK_POS = "\\$LUCK_POS";
    private final static String LUCK_ICON1 = "\\$LUCK_ICON1";
    private final static String LUCK_ICON2 = "\\$LUCK_ICON2";
    private static String pTemplateData = "";

    static {
        try {
            BufferedReader r = new BufferedReader(new FileReader(new File("templates/report.tmpl")));
            String line = "";
            while ((line = r.readLine()) != null) {
                pTemplateData += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String buildToolTip(FightReport pReport) {
        String res = pTemplateData;
        String[] tables = buildUnitTables(pReport);
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy HH:mm");
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        res = res.replaceAll(WINNER_STRING, ((pReport.isWon() ? "Der Angreifer hat gewonnen" : "Der Verteidiger hat gewonnen")));
        res = res.replaceAll(SEND_TIME, f.format(new Date(pReport.getTimestamp())));
        res = res.replaceAll(ATTACKER_TABLE, tables[0]);
        res = res.replaceAll(DEFENDER_TABLE, tables[1]);
        res = res.replaceAll(LUCK_STRING, "Gl&uuml;ck (aus Sicht des Angreifers)");
        res = res.replaceAll(LUCK_BAR, buildLuckBar(pReport.getLuck()));
        res = res.replaceAll(MORAL, nf.format(pReport.getMoral()));
        nf.setMinimumFractionDigits(1);
        nf.setMaximumFractionDigits(1);
        res = res.replaceAll(LUCK_NEG, ((pReport.getLuck() < 0) ? "<b>" + nf.format(pReport.getLuck()) + "%</b>" : ""));
        res = res.replaceAll(LUCK_POS, ((pReport.getLuck() >= 0) ? "<b>" + nf.format(pReport.getLuck()) + "%</b>" : ""));
        res = res.replaceAll(LUCK_ICON1, "<img src=\"" + ((pReport.getLuck() <= 0) ? FightReportHTMLToolTipGenerator.class.getResource("/res/rabe.png") : FightReportHTMLToolTipGenerator.class.getResource("/res/rabe_grau.png")) + "\"/>");
        res = res.replaceAll(LUCK_ICON2, "<img src=\"" + ((pReport.getLuck() >= 0) ? FightReportHTMLToolTipGenerator.class.getResource("/res/klee.png") : FightReportHTMLToolTipGenerator.class.getResource("/res/klee_grau.png")) + "\"/>");
        res = res.replaceAll(ATTACKER, pReport.getAttacker().getName());
        res = res.replaceAll(SOURCE, pReport.getSourceVillage().getFullName());
        res = res.replaceAll(DEFENDER, pReport.getDefender().getName());
        res = res.replaceAll(TARGET, pReport.getTargetVillage().getFullName());
        res = res.replaceAll(RAM_DAMAGE, ((pReport.wasWallDamaged()) ? "Wall besch&auml;digt von Level <b>" + pReport.getWallBefore() + "</b> auf Level <b>" + pReport.getWallAfter() + "</b>" : ""));
        res = res.replaceAll(CATA_DAMAGE, ((pReport.wasBuildingDamaged()) ? pReport.getAimedBuilding() + " besch&auml;digt von Level <b>" + pReport.getBuildingBefore() + "</b> auf Level <b>" + pReport.getBuildingAfter() + "</b>" : ""));
        res = res.replaceAll(SNOB_INFLUENCE, ((pReport.wasSnobAttack()) ? "Zustimmung gesunken von <b>" + pReport.getAcceptanceBefore() + "</b> auf <b>" + pReport.getAcceptanceAfter() + "</b>" : ""));

        return res;
    }

    private static String[] buildUnitTables(FightReport pReport) {
        String attackerTable = "<table width=\"100%\" style=\"border: solid 1px black; padding: 4px;background-color:#EFEBDF;\">";
        String defenderTable = "<table width=\"100%\" style=\"border: solid 1px black; padding: 4px;background-color:#EFEBDF;\">";
        attackerTable += "<tr>";
        defenderTable += "<tr>";

        String headerRow = "<td width=\"100\">&nbsp;</td>";
        String attackerAmountRow = "<tr><td width=\"100\"><div align=\"center\">Anzahl:</div></td>";
        String defenderAmountRow = "<tr><td width=\"100\"><div align=\"center\">Anzahl:</div></td>";
        String attackerLossRow = "<tr><td width=\"100\"><div align=\"center\">Verluste:</div></td>";
        String defenderLossRow = "<tr><td width=\"100\"><div align=\"center\">Verluste:</div></td>";
        String attackerSurviveRow = "<tr><td width=\"100\"><div align=\"center\">&Uuml;berlebende:</div></td>";
        String defenderSurviveRow = "<tr><td width=\"100\"><div align=\"center\">&Uuml;berlebende:</div></td>";
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            headerRow += "<td><img src=\"" + FightReportHTMLToolTipGenerator.class.getResource("/res/ui/" + unit.getPlainName() + ".png") + "\"</td>";
            int amount = pReport.getAttackers().get(unit);
            int died = pReport.getDiedAttackers().get(unit);
            if (amount == 0) {
                attackerAmountRow += "<td style=\"color:#DED3B9;\">" + amount + "</td>";
            } else {
                attackerAmountRow += "<td>" + amount + "</td>";
            }
            if (died == 0) {
                attackerLossRow += "<td style=\"color:#DED3B9;\">" + died + "</td>";
            } else {
                attackerLossRow += "<td>" + died + "</td>";
            }
            if (amount - died == 0) {
                attackerSurviveRow += "<td style=\"color:#DED3B9;\">" + (amount - died) + "</td>";
            } else {
                attackerSurviveRow += "<td>" + (amount - died) + "</td>";
            }
            amount = pReport.getDefenders().get(unit);
            died = pReport.getDiedDefenders().get(unit);

            if (amount == 0) {
                defenderAmountRow += "<td style=\"color:#DED3B9;\">" + amount + "</td>";
            } else {
                defenderAmountRow += "<td>" + amount + "</td>";
            }
            if (died == 0) {
                defenderLossRow += "<td style=\"color:#DED3B9;\">" + died + "</td>";
            } else {
                defenderLossRow += "<td>" + died + "</td>";
            }
            if (amount - died == 0) {
                defenderSurviveRow += "<td style=\"color:#DED3B9;\">" + (amount - died) + "</td>";
            } else {
                defenderSurviveRow += "<td>" + (amount - died) + "</td>";
            }
        }

        headerRow += "</tr>";
        attackerAmountRow += "</tr>";
        attackerLossRow += "</tr>";
        attackerSurviveRow += "</tr>";
        defenderAmountRow += "</tr>";
        defenderLossRow += "</tr>";
        defenderSurviveRow += "</tr>";

        attackerTable += headerRow;
        if (pReport.areAttackersHidden()) {
            attackerTable += "<tr><td width=\"100\"><div align=\"center\">Anzahl:</div></td>";
            attackerTable += "<td colspan=\"12\" rowspan=\"3\" ><div align=\"center\" valign=\"center\">Durch den Besitzer des Berichts verborgen</div></td></tr>";
            attackerTable += "<tr><td width=\"100\"><div align=\"center\">Verluste:</div></td></tr>";
            attackerTable += "<tr><td width=\"100\"><div align=\"center\">&Uuml;berlebende:</div></td></tr>";
        } else {
            attackerTable += attackerAmountRow;
            attackerTable += attackerLossRow;
            attackerTable += attackerSurviveRow;
        }
        attackerTable += "</table>";

        if (pReport.wasLostEverything()) {
            defenderTable += "<tr><td width=\"100\"><div align=\"center\">Anzahl:</div></td>";
            defenderTable += "<td colspan=\"12\" rowspan=\"3\" ><div align=\"center\" valign=\"center\">Keiner deiner Kämpfer ist lebend zurückgekehrt.<BR/>Es konnten keine Informationen über die Truppenstärke des Gegners erlangt werden.</div></td></tr>";
            defenderTable += "<tr><td width=\"100\"><div align=\"center\">Verluste:</div></td></tr>";
            defenderTable += "<tr><td width=\"100\"><div align=\"center\">&Uuml;berlebende:</div></td></tr>";

        } else {
            defenderTable += headerRow;
            defenderTable += defenderAmountRow;
            defenderTable += defenderLossRow;
            defenderTable += defenderSurviveRow;
        }
        defenderTable += "</table>";

        return new String[]{attackerTable, defenderTable};
    }

    public static String buildLuckBar(double pLuck) {
        String res = "<table cellspacing=\"0\" cellpadding=\"0\" style=\"border: solid 1px black; padding: 0px;\">";
        res += "<tr>";
        if (pLuck == 0) {
            res += "<td width=\"" + 50 + "\" height=\"12\"></td>";
            res += "<td width=\"" + 0 + "\" style=\"background-color:#FF0000;\"></td>";
            res += "<td width=\"2\" style=\"background-color:rgb(0, 0, 0)\"></td>";
            res += "<td width=\"0\" style=\"background-color:#009300\"></td>";
            res += "<td width=\"50\"></td>";
        } else if (pLuck < 0) {
            double luck = Math.abs(pLuck);
            double filled = luck / 25 * 50;
            double notFilled = 50 - filled;
            res += "<td width=\"" + notFilled + "\" height=\"12\"></td>";
            res += "<td width=\"" + filled + "\" style=\"background-color:#FF0000;\"></td>";
            res += "<td width=\"2\" style=\"background-color:rgb(0, 0, 0)\"></td>";
            res += "<td width=\"0\" style=\"background-color:#009300\"></td>";
            res += "<td width=\"50\"></td>";
        } else {
            double filled = pLuck / 25 * 50;
            double notFilled = 50 - filled;
            res += "<td width=\"50\" height=\"12\"></td>";
            res += "<td width=\"0\" style=\"background-color:#F00;\"></td>";
            res += "<td width=\"2\" style=\"background-color:rgb(0, 0, 0)\"></td>";
            res += "<td width=\"" + filled + "\" style=\"background-color:#009300\"></td>";
            res += "<td width=\"" + notFilled + "\"></td>";
        }

        res += "</tr>";
        res += "</table>";
        return res;
    }
}
