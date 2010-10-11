/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types;

import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.ui.MapPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.net.URLDecoder;
import org.jdom.Element;

/**
 *
 * @author Torridity
 */
public class Arrow extends AbstractForm {

    private double xPosEnd = -1;
    private double yPosEnd = -1;
    private GeneralPath path = null;
    private boolean filled = false;
    private Color drawColor = Color.WHITE;
    private float drawAlpha = 1.0f;
    private float strokeWidth = 1.0f;
    private boolean drawName = true;

    public Arrow() {
        super();
    }

    public static Arrow fromXml(Element e) {
        try {
            Arrow l = new Arrow();
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

    protected String getFormXml() {
        String xml = "<end x=\"" + getXPosEnd() + "\" y=\"" + getYPosEnd() + "\"/>\n";
        xml += "<filled>" + isFilled() + "</filled>\n";
        xml += "<drawColor r=\"" + getDrawColor().getRed() + "\" g=\"" + getDrawColor().getGreen() + "\" b=\"" + getDrawColor().getBlue() + "\" a=\"" + getDrawAlpha() + "\"/>\n";
        xml += "<stroke width=\"" + getStrokeWidth() + "\"/>\n";
        xml += "<drawName>" + isDrawName() + "</drawName>\n";
        return xml;
    }

    @Override
    public String getFormType() {
        return "arrow";
    }

    public java.awt.Rectangle getBounds() {
        Point2D.Double s = new Point2D.Double(getXPos(), getYPos());
        Point2D.Double e = new Point2D.Double(getXPosEnd(), getYPosEnd());
        int x = (int) ((s.x < e.x) ? s.x : e.x);
        int y = (int) ((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.rint(Math.abs(s.x - e.x));
        int h = (int) Math.rint(Math.abs(s.y - e.y));
        return new java.awt.Rectangle(x, y, w, h);
    }

    @Override
    public void renderForm(Graphics2D g2d) {
        Point2D.Double s = MapPanel.getSingleton().virtualPosToSceenPosDouble(getXPos(), getYPos());
        Point2D.Double e = MapPanel.getSingleton().virtualPosToSceenPosDouble(getXPosEnd(), getYPosEnd());
        if (xPosEnd == -1 && yPosEnd == -1) {
            e = MapPanel.getSingleton().virtualPosToSceenPosDouble(getXPos(), getYPos());
        }
        java.awt.Rectangle mapBounds = MapPanel.getSingleton().getBounds();

        setVisibleOnMap(mapBounds.intersectsLine(new Line2D.Double(s, e)));
        if (!isVisibleOnMap()) {
            return;
        }

        //store properties
        Stroke before = g2d.getStroke();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        Color cBefore = g2d.getColor();
        checkShowMode(g2d, getDrawColor());
        //start draw
        g2d.setFont(fBefore.deriveFont((float) getTextSize()));
        g2d.setStroke(getStroke());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));

        double h = Math.abs(s.y - e.y);
        double c = s.distance(e);
        double a = Math.asin(h / c);

        /*
         * x > 0, y > 0: quadrant I.
        x < 0, y > 0: quadrant II.
        x < 0, y < 0: quadrant III
        x > 0, y < 0: quadrant IV
         */
        path = new GeneralPath();
        path.moveTo(0, -10);
        path.lineTo(80, -10);
        path.lineTo(80, -20);
        path.lineTo(100, 0);
        path.lineTo(80, 20);
        path.lineTo(80, 10);
        path.lineTo(0, 10);
        path.closePath();

        double rot = 0;

        if (e.x > s.x && e.y >= s.y) {
            rot = Math.toDegrees(a);
        } else if (e.x <= s.x && e.y >= s.y) {
            rot = 180 - Math.toDegrees(a);
        } else if (e.x >= s.x && e.y <= s.y) {
            rot = 360 - Math.toDegrees(a);
        } else {
            rot = 180 + Math.toDegrees(a);
        }

        a = Math.toRadians(rot);
        AffineTransform trans = AffineTransform.getScaleInstance(c / 100.0, c / 210.0);
        path.transform(trans);
        trans = AffineTransform.getTranslateInstance(path.getBounds2D().getX(), 0);
        path.transform(trans);
        trans = AffineTransform.getRotateInstance(a, 0, 0);
        path.transform(trans);
        trans = AffineTransform.getTranslateInstance(s.x, s.y);
        path.transform(trans);
        
        if (isFilled()) {
            g2d.fill(path);
        } else {
            g2d.draw(path);
        }
        drawDecoration(s, e, g2d);
        //reset properties
        g2d.setStroke(before);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);
        g2d.setColor(cBefore);
    }

    public void renderPreview(Graphics2D g2d) {
        Point2D.Double s = new Point2D.Double(getXPos(), getYPos());
        //store properties
        Stroke before = g2d.getStroke();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        AffineTransform tb = g2d.getTransform();
        //start draw
        g2d.setFont(fBefore.deriveFont((float) getTextSize()));
        g2d.setStroke(getStroke());
        g2d.setColor(getDrawColor());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));
        path = new GeneralPath();
        path.moveTo(getXPos(), getYPos() - 10);
        path.lineTo(getXPos() + 80, getYPos() - 10);
        path.lineTo(getXPos() + 80, getYPos() - 20);
        path.lineTo(getXPos() + 100, getYPos());
        path.lineTo(getXPos() + 80, getYPos() + 20);
        path.lineTo(getXPos() + 80, getYPos() + 10);
        path.lineTo(getXPos() + 0, getYPos() + 10);
        path.closePath();

        if (isFilled()) {
            g2d.fill(path);
        } else {
            g2d.draw(path);
        }

        if (isDrawName()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
            g2d.setColor(getTextColor());
            g2d.drawString(getFormName(), (int) s.x, (int) s.y);
        }
        //reset properties
        g2d.setStroke(before);
        g2d.setComposite(coBefore);
        g2d.setFont(fBefore);
        g2d.setTransform(tb);
    }

    private void drawDecoration(Point2D.Double s, Point2D.Double e, Graphics2D g2d) {
        if (isDrawName()) {
            /* Point2D.Double center = new Point2D.Double((e.getX() - s.getX()) / 2, (e.getY() - s.getY()) / 2);
            AffineTransform t = AffineTransform.getRotateInstance(theta, s.x + center.getX(), s.y + center.getY());
            g2d.setTransform(t);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            g2d.drawString(getFormName(), (int) Math.rint(s.x + center.getX()), (int) Math.rint(s.y + center.getY()));
             */
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTextAlpha()));
            g2d.setColor(getTextColor());
            g2d.drawString(getFormName(), (int) s.x, (int) s.y);
        }
    }

    public BasicStroke getStroke() {
        float w = (float) (getStrokeWidth() / DSWorkbenchMainFrame.getSingleton().getZoomFactor());
        return new BasicStroke(w);
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
     * @return the drawColor
     */
    public Color getDrawColor() {
        return drawColor;
    }

    /**
     * @param drawColor the drawColor to set
     */
    public void setDrawColor(Color drawColor) {
        this.drawColor = drawColor;
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
}
