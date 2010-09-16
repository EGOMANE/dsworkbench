/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.ui.MapPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.net.URLDecoder;
import org.jdom.Element;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Charon
 */
public class Circle extends AbstractForm {

    private double xPosEnd = -1;
    private double yPosEnd = -1;
    private boolean filled = false;
    private float strokeWidth = 1.0f;
    private boolean drawName = false;
    private Color drawColor = Color.WHITE;
    private float drawAlpha = 1.0f;

    public static AbstractForm fromXml(Element e) {
        try {
            Circle l = new Circle();
            Element elem = e.getChild("name");
            l.setFormName(URLDecoder.decode(elem.getTextTrim(), "UTF-8"));
            elem = e.getChild("pos");
            l.setXPos(Double.parseDouble(elem.getAttributeValue("x")));
            l.setYPos(Double.parseDouble(elem.getAttributeValue("y")));
            elem = e.getChild("textColor");
            l.setTextColor(new Color(Integer.parseInt(elem.getAttributeValue("r")), Integer.parseInt(elem.getAttributeValue("g")), Integer.parseInt(elem.getAttributeValue("b"))));
            l.setTextAlpha(Float.parseFloat(elem.getAttributeValue("a")));
            elem = e.getChild("drawColor");
            l.setDrawColor(new Color(Integer.parseInt(elem.getAttributeValue("r")), Integer.parseInt(elem.getAttributeValue("g")), Integer.parseInt(elem.getAttributeValue("b"))));
            l.setDrawAlpha(Float.parseFloat(elem.getAttributeValue("a")));
            elem = e.getChild("stroke");
            l.setStrokeWidth(Float.parseFloat(elem.getAttributeValue("width")));
            elem = e.getChild("end");
            l.setXPosEnd(Double.parseDouble(elem.getAttributeValue("x")));
            l.setYPosEnd(Double.parseDouble(elem.getAttributeValue("y")));
            elem = e.getChild("filled");
            l.setFilled(Boolean.parseBoolean(elem.getTextTrim()));
            elem = e.getChild("textSize");
            l.setTextSize(Integer.parseInt(elem.getTextTrim()));
            elem = e.getChild("drawName");
            l.setDrawName(Boolean.parseBoolean(elem.getTextTrim()));
            return l;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void renderForm(Graphics2D g2d) {
        if (getXPos() < 0 || getYPos() < 0 || xPosEnd < 0 || yPosEnd < 0) {
            return;
        }
        Point2D.Double s = MapPanel.getSingleton().virtualPosToSceenPosDouble(getXPos(), getYPos());
        Point.Double e = MapPanel.getSingleton().virtualPosToSceenPosDouble(getXPosEnd(), getYPosEnd());
        int x = (int) Math.rint((s.getX() < e.getX()) ? s.getX() : e.getX());
        int y = (int) Math.rint((s.getY() < e.getY()) ? s.getY() : e.getY());
        int w = (int) Math.rint(Math.abs(s.getX() - e.getX()));
        int h = (int) Math.rint(Math.abs(s.getY() - e.getY()));

        if (new Ellipse2D.Double(x, y, w, h).intersects(MapPanel.getSingleton().getBounds())) {
            setVisibleOnMap(true);
        } else {
            setVisibleOnMap(false);
            return;
        }
        //store properties
        Stroke sBefore = g2d.getStroke();
        Color cBefore = g2d.getColor();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        //draw
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));
        g2d.setStroke(getStroke());
        checkShowMode(g2d, getDrawColor());


        if (isFilled()) {
            g2d.fillOval(x, y, w, h);
        } else {
            g2d.drawOval(x, y, w, h);
        }
        g2d.setClip(new Ellipse2D.Float(x, y, w, h));
        if (isDrawName()) {
            g2d.setColor(getTextColor());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
            g2d.setFont(fBefore.deriveFont((float) getTextSize()));
            Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getFormName(), g2d);
            g2d.drawString(getFormName(), (int) Math.rint((double) x + (double) w / 2 - textBounds.getWidth() / 2), (int) Math.rint((double) y + (double) h / 2 + textBounds.getHeight() / 2));
        }

        //restore properties
        g2d.setStroke(sBefore);
        g2d.setColor(cBefore);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);
    }

    public void renderPreview(Graphics2D g2d) {
        Point2D.Double s = new Point2D.Double(getXPos(), getYPos());
        Point2D.Double e = new Point2D.Double(getXPosEnd(), getYPosEnd());
        int x = (int) ((s.x < e.x) ? s.x : e.x);
        int y = (int) ((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.rint(Math.abs(s.x - e.x));
        int h = (int) Math.rint(Math.abs(s.y - e.y));

        if (new Ellipse2D.Double(x, y, w, h).intersects(MapPanel.getSingleton().getBounds())) {
            setVisibleOnMap(true);
        } else {
            setVisibleOnMap(false);
            return;
        }
        //store properties
        Stroke sBefore = g2d.getStroke();
        Color cBefore = g2d.getColor();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        //draw
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));
        g2d.setStroke(getStroke());
        g2d.setColor(getDrawColor());

        if (isFilled()) {
            g2d.fillOval(x, y, w, h);
        } else {
            g2d.drawOval(x, y, w, h);
        }

        if (isDrawName()) {
            g2d.setColor(getTextColor());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
            g2d.setFont(fBefore.deriveFont((float) getTextSize()));
            Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(getFormName(), g2d);
            g2d.drawString(getFormName(), (int) Math.rint((double) x + (double) w / 2 - textBounds.getWidth() / 2), (int) Math.rint((double) y + (double) h / 2 + textBounds.getHeight() / 2));
        }

        //restore properties
        g2d.setStroke(sBefore);
        g2d.setColor(cBefore);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);
    }

    @Override
    public java.awt.Rectangle getBounds() {
        Point2D.Double s = new Point2D.Double(getXPos(), getYPos());
        Point.Double e = new Point2D.Double(getXPosEnd(), getYPosEnd());
        int x = (int) Math.rint((s.getX() < e.getX()) ? s.getX() : e.getX());
        int y = (int) Math.rint((s.getY() < e.getY()) ? s.getY() : e.getY());
        int w = (int) Math.rint(Math.abs(s.getX() - e.getX()));
        int h = (int) Math.rint(Math.abs(s.getY() - e.getY()));
        return new java.awt.Rectangle(x, y, w, h);
    }

    @Override
    protected String getFormXml() {
        String xml = "<end x=\"" + getXPosEnd() + "\" y=\"" + getYPosEnd() + "\"/>\n";
        xml += "<drawColor r=\"" + getDrawColor().getRed() + "\" g=\"" + getDrawColor().getGreen() + "\" b=\"" + getDrawColor().getBlue() + "\" a=\"" + getDrawAlpha() + "\"/>\n";
        xml += "<filled>" + isFilled() + "</filled>\n";
        xml += "<stroke width=\"" + getStrokeWidth() + "\"/>\n";
        xml += "<drawName>" + isDrawName() + "</drawName>\n";
        return xml;
    }

    @Override
    public String getFormType() {
        return "circle";
    }

    /**
     * @return the strokeWidth
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @param strokeWidth the strokeWidth to set
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     * @return the stroke
     */
    public BasicStroke getStroke() {
        float w = (float) (getStrokeWidth() / DSWorkbenchMainFrame.getSingleton().getZoomFactor());
        return new BasicStroke(w);
    }

    /**
     * @return the color
     */
    public Color getDrawColor() {
        return drawColor;
    }

    /**
     * @param color the color to set
     */
    public void setDrawColor(Color color) {
        this.drawColor = color;
    }

    @Override
    public void setXPos(double xPos) {
        super.setXPos(xPos);
        if (xPosEnd == -1) {
            setXPosEnd(xPos);
        }
    }

    @Override
    public void setYPos(double yPos) {
        super.setYPos(yPos);
        if (xPosEnd == -1) {
            setXPosEnd(yPos);
        }
    }

    /**
     * @return the yPosEnd
     */
    public double getYPosEnd() {
        return yPosEnd;
    }

    /**
     * @param yPosEnd the yPosEnd to set
     */
    public void setYPosEnd(double yPosEnd) {
        this.yPosEnd = yPosEnd;
    }

    /**
     * @return the xPosEnd
     */
    public double getXPosEnd() {
        return xPosEnd;
    }

    /**
     * @param xPosEnd the xPosEnd to set
     */
    public void setXPosEnd(double xPosEnd) {
        this.xPosEnd = xPosEnd;
    }

    /**
     * @return the filled
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * @param filled the filled to set
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    /**
     * @return the drawName
     */
    public boolean isDrawName() {
        return drawName;
    }

    /**
     * @param drawName the drawName to set
     */
    public void setDrawName(boolean drawName) {
        this.drawName = drawName;
    }

    /**
     * @return the drawAlpha
     */
    public float getDrawAlpha() {
        return drawAlpha;
    }

    /**
     * @param drawAlpha the drawAlpha to set
     */
    public void setDrawAlpha(float drawAlpha) {
        this.drawAlpha = drawAlpha;
    }
}
