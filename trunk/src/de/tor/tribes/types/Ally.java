/*
 * TribesStructure.java
 * 
 * Created on 18.07.2007, 18:58:23
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Charon
 */
public class Ally implements Serializable, Comparable {

    private static final long serialVersionUID = 10L;
    private int id = 0;
    private String name = null;
    private String tag = null;
    private short members = 0;
    private int villages = 0;
    private int points = 0;
    private int all_points = 0;
    private int rank = 0;
    private transient List<Tribe> tribes = null;
    private String stringRepresentation = null;
    //$id, $name, $tag, $members, $villages, $points, $all_points, $rank

    public static Ally parseFromPlainData(String pLine) {
        StringTokenizer tokenizer = new StringTokenizer(pLine, ",");
        Ally entry = new Ally();
        if (tokenizer.countTokens() < 8) {
            return null;
        }

        try {
            entry.setId(Integer.parseInt(tokenizer.nextToken()));
            String name = URLDecoder.decode(tokenizer.nextToken(), "UTF-8");
            /* if ((name == null) || (name.trim().length() == 0)) {
            throw new Exception();
            }*/
            entry.setName(name);
            String tag = URLDecoder.decode(tokenizer.nextToken(), "UTF-8");
            /* if ((tag == null) || (tag.trim().length() == 0)) {
            throw new Exception();
            }*/

            entry.setTag(tag);
            entry.setMembers(Short.parseShort(tokenizer.nextToken()));
            entry.setVillages(Integer.parseInt(tokenizer.nextToken()));
            entry.setPoints(Integer.parseInt(tokenizer.nextToken()));
            entry.setAll_points(Integer.parseInt(tokenizer.nextToken()));
            entry.setRank(Integer.parseInt(tokenizer.nextToken()));
            return entry;
        } catch (Exception e) {
            //ally entry invalid
        }
        return null;
    }

    public String toPlainData() {
        StringBuffer b = new StringBuffer();
        b.append(getId());
        b.append(",");
        try {
            b.append(URLEncoder.encode(getName(), "UTF-8"));
        } catch (Exception e) {
            b.append(getName());
        }
        b.append(",");
        try {
            b.append(URLEncoder.encode(getTag(), "UTF-8"));
        } catch (Exception e) {
            b.append(getTag());
        }
        b.append(",");
        b.append(getMembers());
        b.append(",");
        b.append(getVillages());
        b.append(",");
        b.append(getPoints());
        b.append(",");
        b.append(getAll_points());
        b.append(",");
        b.append(getRank());
        return b.toString();
    }

    public Ally() {
        tribes = new LinkedList<Tribe>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        stringRepresentation = null;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        stringRepresentation = null;
    }

    public short getMembers() {
        return members;
    }

    public void setMembers(short members) {
        this.members = members;
    }

    public int getVillages() {
        return villages;
    }

    public void setVillages(int villages) {
        this.villages = villages;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getAll_points() {
        return all_points;
    }

    public void setAll_points(int all_points) {
        this.all_points = all_points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void addTribe(Tribe t) {
        if (tribes == null) {
            tribes = new LinkedList<Tribe>();
        }
        if (!tribes.contains(t)) {
            tribes.add(t);
        }
    }

    public List<Tribe> getTribes() {
        if (tribes == null) {
            tribes = new LinkedList<Tribe>();
        }
        return tribes;
    }

    public String getHTMLInfo() {
        StringBuffer b = new StringBuffer();

        NumberFormat nf = NumberFormat.getInstance();
        b.append("<html><b>Stamm (Tag):</b> ");
        b.append(getName());
        b.append(" (");
        b.append(getTag());
        b.append(")");
        b.append(" <b>Punkte (Rang):</b> ");
        b.append(nf.format(getPoints()));
        b.append(" (");
        b.append(nf.format(getRank()));
        b.append(")");
        b.append(" <b>Member (Dörfer):</b> ");
        b.append(nf.format(getMembers()));
        b.append(" (");
        b.append(nf.format(getVillages()));
        b.append(")</html>");
        return b.toString();
    }

    @Override
    public String toString() {
        if (stringRepresentation == null) {
            stringRepresentation = getName() + " (" + getTag() + ")";
        }
        return stringRepresentation;
    }

    public String toBBCode() {
        return "[ally]" + getTag() + "[/ally]";
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
    public static final Comparator<Ally> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator();

    private static class CaseInsensitiveComparator
            implements Comparator<Ally>, java.io.Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability

        private static final long serialVersionUID = 8575799808933029326L;

        @Override
        public int compare(Ally s1, Ally s2) {
            int n1 = s1.toString().length(),     n2 = s2.toString().length();
            for (int i1 = 0,     i2 = 0; i1 < n1 && i2 < n2; i1++, i2++) {
                char c1 = s1.toString().charAt(i1);
                char c2 = s2.toString().charAt(i2);
                if (c1 != c2) {
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        if (c1 != c2) {
                            return c1 - c2;
                        }
                    }
                }
            }
            return n1 - n2;
        }
    }
}
