/*
 * Village.java
 *
 * Created on 18.07.2007, 18:58:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import java.io.Serializable;

/**
 *
 * @author Charon
 */
public class Village implements Serializable {

    private static final long serialVersionUID = 10L;
    private int id = 0;
    private String name = null;
    private int x = 0;
    private int y = 0;
    private int tribeID = 0;
    private transient Tribe tribe = null;
    private int points = 0;
    private int rank = 0;
    private int type = 0;

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
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTribeID() {
        return tribeID;
    }

    public void setTribeID(int tribeID) {
        this.tribeID = tribeID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public Tribe getTribe() {
        return tribe;
    }

    public void setTribe(Tribe tribe) {
        this.tribe = tribe;
    }

    public String getHTMLInfo() {
        String villageInfo = "<html><b>Name (X|Y):</b> " + getName() + " (" + getX() + "|" + getY() + "), <b>Punkte:</b> " + getPoints() + ", <b>Bonus:</b> ";
        switch (getType()) {
            case 1:
                villageInfo += "+ 10% </html>";
                break;

            case 2:
                villageInfo += "+ 10% </html>";
                break;

            case 3:
                villageInfo += "+ 10% </html>";
                break;

            case 4:
                villageInfo += "+ 10% </html>";
                break;

            case 5:
                villageInfo += "+ 10% </html>";
                break;

            case 6:
                villageInfo += "+ 10% </html>";
                break;

            case 7:
                villageInfo += "+ 10% </html>";
                break;

            case 8:
                villageInfo += "+ 3% </html>";
                break;

        }
        return villageInfo;
    }

    @Override
    public String toString() {
        return getX() + "|" + getY() + " " + getName();
    }
}