/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.renderer;

import de.tor.tribes.types.Conquer;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.ImageManager;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ImageUtils;
import de.tor.tribes.util.conquer.ConquerManager;
import de.tor.tribes.util.tag.TagManager;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Torridity
 */
public class TagMarkerLayerRenderer extends AbstractBufferedLayerRenderer {

    private BufferedImage mLayer = null;
    private HashMap<Tag, Rectangle> renderedSpriteBounds = null;
    private Point mapPos = null;
    private BufferedImage mConquerWarning = null;
    private Rectangle conquerCopyRegion = null;
    private boolean shouldReset = true;

    public TagMarkerLayerRenderer() {
        super();
        try {
            mConquerWarning = ImageIO.read(new File("./graphics/icons/warning.png"));
        } catch (Exception e) {
        }
    }

    @Override
    public void performRendering(RenderSettings pSettings, Graphics2D pG2d) {
        if (!pSettings.isLayerVisible()) {
            return;
        }
        if (shouldReset) {
            //  setRenderedBounds(null);
            setFullRenderRequired(true);
            shouldReset = false;
        }
        Graphics2D g2d = null;
        if (isFullRenderRequired()) {
            if (mLayer == null) {
                mLayer = ImageUtils.createCompatibleBufferedImage(pSettings.getVisibleVillages().length * pSettings.getFieldWidth(), pSettings.getVisibleVillages()[0].length * pSettings.getFieldHeight(), BufferedImage.BITMASK);
                g2d = mLayer.createGraphics();
            } else {
                g2d = (Graphics2D) mLayer.getGraphics();
                Composite c = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
                g2d.fillRect(0, 0, mLayer.getWidth(), mLayer.getHeight());
                g2d.setComposite(c);
            }
            pSettings.setRowsToRender(pSettings.getVisibleVillages()[0].length);
        } else {
            //copy existing data to new location
            g2d = (Graphics2D) mLayer.getGraphics();
            performCopy(pSettings, g2d);
        }
        ImageUtils.setupGraphics(g2d);
        renderedSpriteBounds = new HashMap<Tag, Rectangle>();

        //Set new bounds
        // setRenderedBounds((Rectangle2D.Double) pVirtualBounds.clone());

        BufferedImage img = renderMarkerRows(pSettings);
        AffineTransform trans = AffineTransform.getTranslateInstance(0, 0);
        if (pSettings.getRowsToRender() < 0) {
            trans.setToTranslation(0, (pSettings.getVisibleVillages()[0].length + pSettings.getRowsToRender()) * pSettings.getFieldHeight());
        }
        g2d.drawRenderedImage(img, trans);
        if (isFullRenderRequired()) {
            //everything was rendered, skip col rendering
            setFullRenderRequired(false);
        } else {
            renderedSpriteBounds = new HashMap<Tag, Rectangle>();

            img = renderMarkerColumns(pSettings);
            trans = AffineTransform.getTranslateInstance(0, 0);
            if (pSettings.getColumnsToRender() < 0) {
                trans.setToTranslation((pSettings.getVisibleVillages().length + pSettings.getColumnsToRender()) * pSettings.getFieldWidth(), 0);
            }
            g2d.drawRenderedImage(img, trans);
        }
        g2d.dispose();
        trans.setToTranslation((int) Math.floor(pSettings.getDeltaX()), (int) Math.floor(pSettings.getDeltaY()));
        pG2d.drawRenderedImage(mLayer, trans);
    }

    private void performCopy(RenderSettings pSettings, Graphics2D pG2D) {
        if (mapPos == null) {
            mapPos = new Point((int) Math.floor(pSettings.getMapBounds().getX()), (int) Math.floor(pSettings.getMapBounds().getY()));
        }

        Point newMapPos = new Point((int) Math.floor(pSettings.getMapBounds().getX()), (int) Math.floor(pSettings.getMapBounds().getY()));

        int fieldsX = newMapPos.x - mapPos.x;
        int fieldsY = newMapPos.y - mapPos.y;

        //set new map position
        if (newMapPos.distance(mapPos) != 0.0) {
            mapPos.x = newMapPos.x;
            mapPos.y = newMapPos.y;
        }
        //  Composite comp = pG2D.getComposite();
        pG2D.setComposite(AlphaComposite.Src);
        pG2D.copyArea(0, 0, mLayer.getWidth(), mLayer.getHeight(), -fieldsX * pSettings.getFieldWidth(), -fieldsY * pSettings.getFieldHeight());
        //  pG2D.setComposite(comp);
    }

    private BufferedImage renderMarkerRows(RenderSettings pSettings) {
        //create new buffer for rendering
        BufferedImage newRows = ImageUtils.createCompatibleBufferedImage(pSettings.getVisibleVillages().length * pSettings.getFieldWidth(), Math.abs(pSettings.getRowsToRender()) * pSettings.getFieldHeight(), BufferedImage.BITMASK);
        //calculate first row that will be rendered
        int firstRow = (pSettings.getRowsToRender() > 0) ? 0 : pSettings.getVisibleVillages()[0].length - Math.abs(pSettings.getRowsToRender());
        Graphics2D g2d = newRows.createGraphics();
        ImageUtils.setupGraphics(g2d);

        //iterate through entire row
        int cnt = 0;
        conquerCopyRegion = null;
        for (int x = 0; x < pSettings.getVisibleVillages().length; x++) {
            //iterate from first row for 'pRows' times
            for (int y = firstRow; y < firstRow + Math.abs(pSettings.getRowsToRender()); y++) {
                cnt++;
                Village v = pSettings.getVisibleVillages()[x][y];
                int row = y - firstRow;
                int col = x;
                renderMarkerField(v, row, col, pSettings.getFieldWidth(), pSettings.getFieldHeight(), pSettings.getZoom(), g2d);
            }
        }
        g2d.dispose();
        return newRows;
    }

    private BufferedImage renderMarkerColumns(RenderSettings pSettings) {
        //create new buffer for rendering
        BufferedImage newColumns = ImageUtils.createCompatibleBufferedImage(Math.abs(pSettings.getColumnsToRender()) * pSettings.getFieldWidth(), pSettings.getVisibleVillages()[0].length * pSettings.getFieldHeight(), BufferedImage.BITMASK);
        //calculate first row that will be rendered
        int firstCol = (pSettings.getColumnsToRender() > 0) ? 0 : pSettings.getVisibleVillages().length - Math.abs(pSettings.getColumnsToRender());
        Graphics2D g2d = newColumns.createGraphics();
        ImageUtils.setupGraphics(g2d);

        //iterate through entire row
        int cnt = 0;
        conquerCopyRegion = null;
        for (int x = firstCol; x < firstCol + Math.abs(pSettings.getColumnsToRender()); x++) {
            for (int y = 0; y < pSettings.getVisibleVillages()[0].length; y++) {
                cnt++;
                //iterate from first row for 'pRows' times
                Village v = pSettings.getVisibleVillages()[x][y];
                int row = y;
                int col = x - firstCol;

                renderMarkerField(v, row, col, pSettings.getFieldWidth(), pSettings.getFieldHeight(), pSettings.getZoom(), g2d);
            }
        }
        g2d.dispose();
        return newColumns;
    }

    private void renderMarkerField(Village v, int row, int col, int pFieldWidth, int pFieldHeight, double zoom, Graphics2D g2d) {
        if (v != null) {
            int tagsize = (int) Math.rint((double) 18 / zoom);
            if (tagsize > pFieldHeight || tagsize > pFieldWidth) {
                return;
            }

            //render village tags
            List<Tag> villageTags = TagManager.getSingleton().getTags(v);
            if (villageTags != null && !villageTags.isEmpty()) {
                int xcnt = 1;
                int ycnt = 2;
                int cnt = 0;
                for (Tag tag : TagManager.getSingleton().getTags(v)) {
                    // <editor-fold defaultstate="collapsed" desc="Draw tag if active">
                    if (tag.isShowOnMap()) {
                        boolean isDrawable = false;
                        Rectangle copyRect = renderedSpriteBounds.get(tag);
                        int tagX = col * pFieldWidth + pFieldWidth - xcnt * tagsize;
                        int tagY = row * pFieldHeight + pFieldHeight - ycnt * tagsize;
                        int iconType = tag.getTagIcon();
                        Color color = tag.getTagColor();
                        if (color != null || iconType != -1) {
                            isDrawable = true;
                        }
                        if (isDrawable) {
                            if (copyRect == null) {
                                if (color != null) {
                                    Color before = g2d.getColor();
                                    g2d.setColor(color);
                                    g2d.fillRect(tagX, tagY, tagsize, tagsize);
                                    g2d.setColor(before);
                                }

                                if (iconType != -1) {
                                    //drawing
                                    BufferedImage tagImage = ImageManager.getUnitImage(iconType);//mage(iconType, false).getScaledInstance(tagsize, tagsize, Image.SCALE_FAST);
                                    AffineTransform trans = AffineTransform.getTranslateInstance(tagX, tagY);
                                    trans.scale(1.0 / zoom, 1.0 / zoom);
                                    g2d.drawRenderedImage(tagImage, trans);
                                }
                                renderedSpriteBounds.put(tag, new Rectangle(tagX, tagY, tagsize, tagsize));
                            } else {
                                g2d.copyArea(copyRect.x, copyRect.y, copyRect.width, copyRect.height, tagX - copyRect.x, tagY - copyRect.y);
                            }
                            //calculate positioning
                            cnt++;
                            xcnt++;
                            if (cnt == 2) {
                                //show only 2 icons in the first line to avoid marker overlay
                                xcnt = 1;
                                ycnt--;

                            }
                        }
                    }
                    // </editor-fold>
                }
            }

            //render conquers
            int conquerSize = (int) Math.rint((double) 16 / zoom);
            if (conquerSize > pFieldHeight || conquerSize > pFieldWidth) {
                return;
            }

            Conquer c = ConquerManager.getSingleton().getConquer(v);
            if (c != null) {
                if (conquerCopyRegion != null) {
                    g2d.copyArea(conquerCopyRegion.x, conquerCopyRegion.y, conquerCopyRegion.width, conquerCopyRegion.height, col * pFieldWidth + pFieldWidth - conquerSize - conquerCopyRegion.x, row * pFieldHeight + pFieldHeight - conquerSize - conquerCopyRegion.y);
                } else {
                    g2d.drawImage(mConquerWarning, col * pFieldWidth + pFieldWidth - conquerSize, row * pFieldHeight + pFieldHeight - conquerSize, conquerSize, conquerSize, null);
                    conquerCopyRegion = new Rectangle(col * pFieldWidth + pFieldWidth - conquerSize, row * pFieldHeight + pFieldHeight - conquerSize, conquerSize, conquerSize);
                }
            }
        }
    }

    public void reset() {
        shouldReset = true;
    }
}
