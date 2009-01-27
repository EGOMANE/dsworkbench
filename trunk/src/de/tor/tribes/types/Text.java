/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.ui.MapPanel;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.Skin;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.net.URLDecoder;
import org.jdom.Element;

/**
 *
 * @author Charon
 */
public class Text extends AbstractForm {

    private java.awt.Rectangle mBounds = null;

    public static AbstractForm fromXml(Element e) {
        try {
            Text l = new Text();
            Element elem = e.getChild("name");
            l.setFormName(URLDecoder.decode(elem.getTextTrim(), "UTF-8"));
            elem = e.getChild("pos");
            l.setXPos(Double.parseDouble(elem.getAttributeValue("x")));
            l.setYPos(Double.parseDouble(elem.getAttributeValue("y")));
            elem = e.getChild("textColor");
            l.setTextColor(new Color(Integer.parseInt(elem.getAttributeValue("r")), Integer.parseInt(elem.getAttributeValue("g")), Integer.parseInt(elem.getAttributeValue("b"))));
            l.setTextAlpha(Float.parseFloat(elem.getAttributeValue("a")));
            elem = e.getChild("textSize");
            l.setTextSize(Integer.parseInt(elem.getTextTrim()));
            return l;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void renderForm(Graphics2D g2d) {
        Font fBefore = g2d.getFont();
        g2d.setFont(fBefore.deriveFont((float) getTextSize()));
        java.awt.Rectangle mapBounds = MapPanel.getSingleton().getBounds();
        FontMetrics met = g2d.getFontMetrics();
        Rectangle2D bounds = met.getStringBounds(getFormName(), g2d);
        Point s = MapPanel.getSingleton().virtualPosToSceenPos(getXPos(), getYPos());
        mBounds = new java.awt.Rectangle((int) (bounds.getX() + s.x), (int) (bounds.getY() + s.y), (int) (bounds.getWidth()), (int) (bounds.getHeight()));
        java.awt.Rectangle virtBounds = new java.awt.Rectangle((int) (bounds.getX() + s.x), (int) (bounds.getY() + s.y), (int) (bounds.getWidth()), (int) (bounds.getHeight()));

        //calculate bounds
        double zoom = DSWorkbenchMainFrame.getSingleton().getZoomFactor();
        int w = GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, zoom).getWidth(null);
        int h = GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, zoom).getHeight(null);
        mBounds = new java.awt.Rectangle((int) (getXPos()), (int) (getYPos()), (int) Math.rint(bounds.getWidth() / (double) w), (int) Math.rint(bounds.getHeight() / (double) h));

        if (mapBounds.intersects(virtBounds)) {
            setVisibleOnMap(true);
        } else {
            setVisibleOnMap(false);
            g2d.setFont(fBefore);
            return;
        }

        //save properties
        Color cBefore = g2d.getColor();
        Composite coBefore = g2d.getComposite();

        //draw
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
        checkShowMode(g2d, getTextColor());
        g2d.drawString(getFormName(), s.x, s.y);
        //restore properties
        g2d.setColor(cBefore);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);
    }

    public void renderPreview(Graphics2D g2d, java.awt.Rectangle bounds) {
        //save properties
        Color cBefore = g2d.getColor();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        //draw
        g2d.setFont(fBefore.deriveFont((float) getTextSize()));
        Rectangle2D rect = g2d.getFontMetrics().getStringBounds(getFormName(), g2d);
        int x = (int) Math.rint((double) bounds.getWidth() / 2 - (double) rect.getWidth() / 2);
        int y = (int) Math.rint((double) bounds.getHeight() / 2 + (double) rect.getHeight() / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
        g2d.setColor(getTextColor());
        g2d.drawString(getFormName(), x, y);
        //restore properties
        g2d.setColor(cBefore);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);

    }

    @Override
    public java.awt.Rectangle getBounds() {
        return mBounds;
    }

    @Override
    protected String getFormXml() {
        return "";
    }

    @Override
    public String getFormType() {
        return "text";
    }
}
