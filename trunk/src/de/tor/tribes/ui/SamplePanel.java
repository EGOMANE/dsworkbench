/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SamplePanel.java
 *
 * Created on 06.01.2009, 13:17:26
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.Circle;
import de.tor.tribes.types.Line;
import de.tor.tribes.types.Rectangle;
import de.tor.tribes.types.Text;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Charon
 */
public class SamplePanel extends javax.swing.JPanel {

    private Color mDrawColor = Color.WHITE;
    private Color mTextColor = Color.BLACK;
    private float fDrawTransparency = 0.0f;
    private float fTextTransparency = 0.0f;
    private float fStrokeWidth = 1.0f;
    private int fTextSize = 14;
    //type 0-4: 0=none, 1=end, 2=start, 3=both
    private boolean drawStartArrow = false;
    private boolean drawEndArrow = false;
    private boolean bFill = false;
    private boolean drawText = false;
    private String sText = "";
    private BufferedImage sampleTexture = null;
    //type 0-3: 0=line, 1=rect, 2=circle, 3=text
    private int type = 0;
    private int roundBorders = 0;

    /** Creates new form SamplePanel */
    public SamplePanel() {
        initComponents();
        try {
            sampleTexture = ImageIO.read(new File("graphics/skins/default/v6.png"));
        } catch (Exception e) {
        }
    }

    public void setDrawColor(Color c) {
        mDrawColor = c;
    }

    public void setDrawTransparency(float t) {
        fDrawTransparency = t;
    }

    public void setDrawText(boolean c) {
        drawText = c;
    }

    public void setRoundBorders(int c) {
        roundBorders = c;
    }

    public void setTextColor(Color c) {
        mTextColor = c;
    }

    public void setTextTransparency(float t) {
        fTextTransparency = t;
    }

    public void setTextSize(int t) {
        fTextSize = t;
    }

    public void setStrokeWidth(float s) {
        fStrokeWidth = s;
    }

    public void setFill(boolean f) {
        bFill = f;
    }

    public void setText(String c) {
        sText = c;
    }

    public void drawStartArrow(boolean v) {
        drawStartArrow = v;
    }

    public void drawEndArrow(boolean v) {
        drawEndArrow = v;
    }

    public void setType(int v) {
        type = v;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(mDrawColor);
        Paint p = g2d.getPaint();
        g2d.setPaint(new TexturePaint(sampleTexture, new Rectangle2D.Double(0, 0, sampleTexture.getWidth(), sampleTexture.getHeight())));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setPaint(p);
        switch (type) {
            case 0: {
                //preview line
                Line l = new Line();
                l.setDrawColor(mDrawColor);
                l.setDrawAlpha(fDrawTransparency);
                l.setTextColor(mTextColor);
                l.setTextAlpha(fTextTransparency);
                l.setStartArrow(drawStartArrow);
                l.setEndArrow(drawEndArrow);
                l.setTextSize(fTextSize);
                l.setDrawName(drawText);
                l.setFormName(sText);
                l.setStrokeWidth(fStrokeWidth);
                l.setXPos(30);
                l.setYPos(getHeight() / 2);
                l.setXPosEnd(getWidth() - 30);
                l.setYPosEnd(getHeight() / 2);
                l.renderPreview(g2d);
                break;
            }
            case 1: {
                //preview rect
                Rectangle l = new Rectangle();
                l.setDrawColor(mDrawColor);
                l.setDrawAlpha(fDrawTransparency);
                l.setTextColor(mTextColor);
                l.setTextAlpha(fTextTransparency);
                l.setFilled(bFill);
                l.setTextSize(fTextSize);
                l.setDrawName(drawText);
                l.setFormName(sText);
                l.setStrokeWidth(fStrokeWidth);
                l.setXPos(10);
                l.setYPos(10);
                l.setXPosEnd(getWidth() - 10);
                l.setYPosEnd(getHeight() - 10);
                l.setRounding(roundBorders);
                l.renderPreview(g2d);
                break;
            }
            case 2: {
                //preview circle
                Circle l = new Circle();
                l.setDrawColor(mDrawColor);
                l.setDrawAlpha(fDrawTransparency);
                l.setTextColor(mTextColor);
                l.setTextAlpha(fTextTransparency);
                l.setFilled(bFill);
                l.setTextSize(fTextSize);
                l.setDrawName(drawText);
                l.setFormName(sText);
                l.setStrokeWidth(fStrokeWidth);
                l.setXPos(10);
                l.setYPos(10);
                l.setXPosEnd(getWidth() - 10);
                l.setYPosEnd(getHeight() - 10);
                l.renderPreview(g2d);
                break;
            }
            case 3: {
                //preview text
                Text l = new Text();
                l.setTextColor(mTextColor);
                l.setTextAlpha(fTextTransparency);
                l.setFormName(sText);
                l.setXPos(10);
                l.setYPos(getHeight() - 10);
                l.renderPreview(g2d);
                break;
            }
        }

    /*
    g2d.fillRect(0, 0, getWidth(), getHeight());
    g2d.setColor(mColor);
    Composite before = g2d.getComposite();
    Paint p = g2d.getPaint();
    g2d.setPaint(new TexturePaint(sampleTexture, new Rectangle2D.Double(0, 0, sampleTexture.getWidth(), sampleTexture.getHeight())));
    g2d.fillRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    g2d.setPaint(p);
    Stroke stro = g2d.getStroke();
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fTransparency));
    if (bFill) {
    g2d.fillRect(getBounds().x + 10, getBounds().y + 10, getBounds().width - 20, getBounds().height - 20);
    } else {
    g2d.setStroke(new BasicStroke(fStrokeWidth));
    g2d.drawRect(getBounds().x + 10, getBounds().y + 10, getBounds().width - 20, getBounds().height - 20);
    g2d.setStroke(stro);
    }
    if (sText.length() > 0) {
    Font fb = g2d.getFont();
    g2d.setFont(fb.deriveFont(fStrokeWidth));
    g2d.drawString(sText, 20, getHeight() - 20);
    g2d.setFont(fb);
    }
    g2d.setComposite(before);*/
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
