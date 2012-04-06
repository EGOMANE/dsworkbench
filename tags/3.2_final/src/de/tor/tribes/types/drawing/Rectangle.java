/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.types.drawing;

import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.panels.MapPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.net.URLDecoder;
import org.jdom.Element;
import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.util.bb.VillageListFormatter;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 *
 * @author Charon
 */
public class Rectangle extends AbstractForm {

    private double xPosEnd = -1;
    private double yPosEnd = -1;
    private boolean filled = false;
    private float strokeWidth = 1.0f;
    private int rounding = 0;
    private boolean drawName = true;
    private Color drawColor = Color.WHITE;
    private float drawAlpha = 1.0f;

    public void loadFromXml(Element e) {
        try {
            Element elem = e.getChild("name");
            setFormName(URLDecoder.decode(elem.getTextTrim(), "UTF-8"));
            elem = e.getChild("pos");
            setXPos(Double.parseDouble(elem.getAttributeValue("x")));
            setYPos(Double.parseDouble(elem.getAttributeValue("y")));
            elem = e.getChild("textColor");
            setTextColor(new Color(Integer.parseInt(elem.getAttributeValue("r")), Integer.parseInt(elem.getAttributeValue("g")), Integer.parseInt(elem.getAttributeValue("b"))));
            setTextAlpha(Float.parseFloat(elem.getAttributeValue("a")));
            elem = e.getChild("drawColor");
            setDrawColor(new Color(Integer.parseInt(elem.getAttributeValue("r")), Integer.parseInt(elem.getAttributeValue("g")), Integer.parseInt(elem.getAttributeValue("b"))));
            setDrawAlpha(Float.parseFloat(elem.getAttributeValue("a")));
            elem = e.getChild("stroke");
            setStrokeWidth(Float.parseFloat(elem.getAttributeValue("width")));
            elem = e.getChild("end");
            setXPosEnd(Double.parseDouble(elem.getAttributeValue("x")));
            setYPosEnd(Double.parseDouble(elem.getAttributeValue("y")));
            elem = e.getChild("filled");
            setFilled(Boolean.parseBoolean(elem.getTextTrim()));
            elem = e.getChild("textSize");
            setTextSize(Integer.parseInt(elem.getTextTrim()));
            elem = e.getChild("drawName");
            setDrawName(Boolean.parseBoolean(elem.getTextTrim()));
        } catch (Exception ex) {
        }
    }

    public boolean allowsBBExport() {
        return true;
    }

    @Override
    public String[] getReplacements(boolean pExtended) {
        String nameVal = getFormName();
        if (nameVal == null || nameVal.length() == 0) {
            nameVal = "Kein Name";
        }
        String startXVal = Integer.toString((int) Math.rint(getXPos()));
        String startYVal = Integer.toString((int) Math.rint(getYPos()));
        String endXVal = Integer.toString((int) Math.rint(getXPosEnd()));
        String endYVal = Integer.toString((int) Math.rint(getYPosEnd()));
        String widthVal = Integer.toString((int) Math.rint(getXPosEnd() - getXPos()));
        String heightVal = Integer.toString((int) Math.rint(getYPosEnd() - getYPos()));
        String colorVal = "";
        if (getDrawColor() != null) {
            colorVal = "#" + Integer.toHexString(getDrawColor().getRGB() & 0x00ffffff);
        } else {
            colorVal = "#" + Integer.toHexString(Color.BLACK.getRGB() & 0x00ffffff);
        }
        List<Village> containedVillages = getContainedVillages();
        String villageListVal = "";
        if (containedVillages != null && !containedVillages.isEmpty()) {
            villageListVal = new VillageListFormatter().formatElements(containedVillages, pExtended);
        } else {
            villageListVal = "Keine Dörfer enthalten";
        }
        return new String[]{nameVal, startXVal, startYVal, widthVal, heightVal, endXVal, endYVal, colorVal, villageListVal};
    }

    @Override
    public void renderForm(Graphics2D g2d) {
        if (getXPos() < 0 || getYPos() < 0 || xPosEnd < 0 || yPosEnd < 0) {
            return;
        }
        Point s = MapPanel.getSingleton().virtualPosToSceenPos(getXPos(), getYPos());
        Point e = MapPanel.getSingleton().virtualPosToSceenPos(getXPosEnd(), getYPosEnd());
        int x = ((s.x < e.x) ? s.x : e.x);
        int y = ((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.rint(Math.abs(s.x - e.x));
        int h = (int) Math.rint(Math.abs(s.y - e.y));
        java.awt.Rectangle mapBounds = MapPanel.getSingleton().getBounds();
        if (mapBounds.intersects(new Rectangle2D.Double(x, y, w, h))) {
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
        g2d.setStroke(getStroke());
        checkShowMode(g2d, getDrawColor());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));

        if (isFilled()) {

            g2d.fill(new RoundRectangle2D.Double(x, y, w, h, rounding, rounding));
        } else {
            g2d.draw(new RoundRectangle2D.Double(x, y, w, h, rounding, rounding));
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
    public List<Village> getContainedVillages() {
        Point s = MapPanel.getSingleton().virtualPosToSceenPos(getXPos(), getYPos());
        Point e = MapPanel.getSingleton().virtualPosToSceenPos(getXPosEnd(), getYPosEnd());
        int x = ((s.x < e.x) ? s.x : e.x);
        int y = ((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.rint(Math.abs(s.x - e.x));
        int h = (int) Math.rint(Math.abs(s.y - e.y));

        List<Village> result = MapPanel.getSingleton().getVillagesInShape(new Rectangle2D.Double(x, y, w, h));
        if (result == null) {
            return super.getContainedVillages();
        }
        return result;
    }

    public void renderPreview(Graphics2D g2d) {
        Point2D.Double s = new Point2D.Double(getXPos(), getYPos());
        Point2D.Double e = new Point2D.Double(getXPosEnd(), getYPosEnd());
        int x = (int) ((s.x < e.x) ? s.x : e.x);
        int y = (int) ((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.rint(Math.abs(s.x - e.x));
        int h = (int) Math.rint(Math.abs(s.y - e.y));
        //store properties
        Stroke sBefore = g2d.getStroke();
        Color cBefore = g2d.getColor();
        Composite coBefore = g2d.getComposite();
        Font fBefore = g2d.getFont();
        //draw
        g2d.setStroke(getStroke());
        g2d.setColor(getDrawColor());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getDrawAlpha()));

        if (isFilled()) {
            g2d.fill(new RoundRectangle2D.Double(x, y, w, h, rounding, rounding));
        } else {
            g2d.draw(new RoundRectangle2D.Double(x, y, w, h, rounding, rounding));
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
        Point2D.Double e = new Point2D.Double(getXPosEnd(), getYPosEnd());
        int x = (int) Math.round((s.x < e.x) ? s.x : e.x);
        int y = (int) Math.round((s.y < e.y) ? s.y : e.y);
        int w = (int) Math.round(Math.abs(s.x - e.x));
        int h = (int) Math.round(Math.abs(s.y - e.y));
        return new java.awt.Rectangle(x, y, w, h);
    }

    @Override
    protected String getFormXml() {
        String xml = "<end x=\"" + getXPosEnd() + "\" y=\"" + getYPosEnd() + "\"/>\n";
        xml += "<drawColor r=\"" + getDrawColor().getRed() + "\" g=\"" + getDrawColor().getGreen() + "\" b=\"" + getDrawColor().getBlue() + "\" a=\"" + getDrawAlpha() + "\"/>\n";
        xml += "<rounding>" + getRounding() + "</rounding>\n";
        xml += "<filled>" + isFilled() + "</filled>\n";
        xml += "<stroke width=\"" + getStrokeWidth() + "\"/>\n";
        xml += "<drawName>" + isDrawName() + "</drawName>\n";
        return xml;
    }

    @Override
    public FORM_TYPE getFormType() {
        return FORM_TYPE.RECTANGLE;
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
     * @return the rounding
     */
    public int getRounding() {
        return rounding;
    }

    /**
     * @param rounding the rounding to set
     */
    public void setRounding(int rounding) {
        this.rounding = rounding;
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
