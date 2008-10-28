/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.ui.renderer;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.io.WorldDecorationHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.Marker;
import de.tor.tribes.types.Tag;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.DSWorkbenchMainFrame;
import de.tor.tribes.ui.ImageManager;
import de.tor.tribes.ui.MapPanel;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.Skin;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.tag.TagManager;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**Map Renderer which supports "dirty layers" defining which layer has to be redrawn.<BR/>
 * Layer order with z-ID:<BR/>
 * 0: Marker Layer -> redraw after marker changes (triggered by MarkerManager)
 * 1: Village Layer: Redraw on global events (e.g. init or resize) or after changes on scaling or skin
 * 2: Misc. Basic Map Decoration: e.g. Sector or continent drawing on demand (triggered by SettingsDialog)
 * 3: Attack Vector Layer: Redraw after attack changes (triggered by AttackManager)
 * 4: Misc. Extended Map Decoration: e.g. troop qualification or active village marker
 * 5: Live Layer: Redraw in every drawing cycle e.g. Drag line, tool popup(?), (troop movement?)
 * 6-16: Free assignable
 * @author Charon
 */
/**Thread for updating after scroll operations*/
class MapRenderer extends Thread {

    private static Logger logger = Logger.getLogger(MapRenderer.class);
    public static final int ID_FULL_REDRAW = 0;
    public static final int ID_MAP_REDRAW = 1;
    public static final int ID_MARKER_REDRAW = 2;
    public static final int ID_BASIC_DECORATION_REDRAW = 3;
    public static final int ID_ATTACK_REDRAW = 4;
    public static final int ID_EXTENDED_DECORATION_REDRAW = 5;
    private Village[][] mVisibleVillages = null;
    private BufferedImage mBuffer = null;
    private BufferedImage[] mLayers = null;
    //  private BufferedImage mBackground = null;
    private int iVillagesX = 0;
    private int iVillagesY = 0;
    private int iCenterX = 500;
    private int iCenterY = 500;
    private int xe = 0;
    private int ye = 0;
    private Village mSourceVillage = null;
    private BufferedImage mDistBorder = null;
    private Image mMarkerImage = null;
    private final NumberFormat nf = NumberFormat.getInstance();
    private int iDirtLevel = -1;
    //  private boolean mustDrawBackground = true;

    public MapRenderer(int pX, int pY) {
        mVisibleVillages = new Village[iVillagesX][iVillagesY];
        setCoordinates(pX, pY);
        setDaemon(true);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        try {
            mDistBorder = ImageIO.read(new File("./graphics/dist_border.png"));
            mMarkerImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/res/marker.png"));
        } catch (Exception e) {
            logger.error("Failed to load border images", e);
        }
    }

    protected void setCoordinates(int pX, int pY) {
        iCenterX = pX;
        iCenterY = pY;
    }

    /*public void setMustDrawBackground() {
    mustDrawBackground = true;
    }*/
    @Override
    public void run() {
        while (true) {
            try {
                //  long s = System.currentTimeMillis();
                Point startPoint = calculateVisibleVillages(iCenterX, iCenterY);
                if (startPoint != null) {
                    render(startPoint.x, startPoint.y);

                    //  mustDrawBackground = false;
                    //  System.out.println("Dur " + (System.currentTimeMillis() - s));
                    if (mBuffer != null) {
                        Image iBuffer = MapPanel.getSingleton().createImage(mBuffer.getWidth(), mBuffer.getHeight());
                        Graphics2D g2d = (Graphics2D) iBuffer.getGraphics();
                        // g2d.drawImage(mBackground, null, 0, 0);
                        g2d.drawImage(mBuffer, null, 0, 0);
                        g2d.dispose();
                        //mBuffer = null;
                        MapPanel.getSingleton().updateComplete(mVisibleVillages, iBuffer);
                        MapPanel.getSingleton().repaint();
                    }
                }
            } catch (Throwable t) {
                logger.error("Redrawing map failed", t);
            //  mustDrawBackground = true;
            }
            try {
                Thread.sleep(80);
            } catch (InterruptedException ie) {
            }
        }
    }

    public void setDragLine(int pXS, int pYS, int pXE, int pYE) {
        mSourceVillage = DataHolder.getSingleton().getVillages()[pXS][pYS];
        xe = pXE;
        ye = pYE;
    }

    /**Extract the visible villages (only needed on full repaint)*/
    private Point calculateVisibleVillages(int pX, int pY) {
        iCenterX = pX;
        iCenterY = pY;

        if (DataHolder.getSingleton().getVillages() == null) {
            //probably reloading data
            return null;
        }
        //get number of drawn villages
        iVillagesX = (int) Math.rint((double) MapPanel.getSingleton().getWidth() / GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor()).getWidth(null));
        iVillagesY = (int) Math.rint((double) MapPanel.getSingleton().getHeight() / GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor()).getHeight(null));

        //make number odd to avoid spaces at the borders
        if (iVillagesX % 2 == 0) {
            iVillagesX++;
        }
        if (iVillagesY % 2 == 0) {
            iVillagesY++;
        }
        //calculate village coordinates of the upper left corner
        int xStart = (int) Math.rint((double) pX - (double) iVillagesX / 2.0);
        int yStart = (int) Math.rint((double) pY - (double) iVillagesY / 2.0);
        xStart = (xStart < 0) ? 0 : xStart;
        yStart = (yStart < 0) ? 0 : yStart;

        //calculate village coordinates of the lower right corner
        int xEnd = (int) Math.rint((double) iCenterX + (double) iVillagesX / 2);
        int yEnd = (int) Math.rint((double) iCenterY + (double) iVillagesY / 2);

        xEnd = (xEnd > 999) ? 999 : xEnd;
        yEnd = (yEnd > 999) ? 999 : yEnd;

        //add some villages to have a small drawing buffer in all directions
        iVillagesX += 1;
        iVillagesY += 1;
        mVisibleVillages = new Village[iVillagesX][iVillagesY];

        int x = 0;
        int y = 0;

        for (int i = xStart; i < xEnd; i++) {
            for (int j = yStart; j < yEnd; j++) {
                mVisibleVillages[x][y] = DataHolder.getSingleton().getVillages()[i][j];
                y++;
            }
            x++;
            y = 0;
        }
        return new Point(xStart, yStart);
    }

    /**Redraw the buffer*/
    private void render(int pXStart, int pYStart) {
        int x = 0;
        int y = 0;
        //get attack colors
        Hashtable<String, Color> attackColors = new Hashtable<String, Color>();
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            Color unitColor = Color.RED;
            try {
                unitColor = Color.decode(GlobalOptions.getProperty(unit.getName() + ".color"));
            } catch (Exception e) {
                unitColor = Color.RED;
            }
            attackColors.put(unit.getName(), unitColor);
        }

        Graphics2D g2d = null;
        //Graphics2D g2df = null;
        int width = GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor()).getWidth(null);
        int height = GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor()).getHeight(null);

        //if ((mBuffer == null) || ((mBuffer.getWidth(null) * mBuffer.getHeight(null)) != (MapPanel.getSingleton().getWidth() * MapPanel.getSingleton().getHeight()))) {
        int wb = MapPanel.getSingleton().getWidth();
        int hb = MapPanel.getSingleton().getHeight();
        if (wb == 0 || hb == 0) {
            //both are 0 if map was not drawn yet
            return;
        }
        mBuffer = new BufferedImage(wb, hb, BufferedImage.TYPE_INT_ARGB);
        /*if (mBackground == null) {
        mBackground = new BufferedImage(wb, hb, BufferedImage.TYPE_INT_RGB);
        }*/
        g2d = (Graphics2D) mBuffer.getGraphics();
        //g2d = (Graphics2D) mBackground.getGraphics();
        //g2d.fillRect(0, 0, mBackground.getWidth(null), mBackground.getHeight(null));

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Speed
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // <editor-fold defaultstate="collapsed" desc=" Variables check and init">
        int xPos = pXStart;
        int yPos = pYStart;
        // g2d.fillRect(0, 0, width, height);
        //disable decoration if field size is not equal the decoration texture size
        boolean useDecoration = true;

        if ((WorldDecorationHolder.getTexture(0, 0, 1).getWidth(null) != GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, 1).getWidth(null)) || (WorldDecorationHolder.getTexture(0, 0, 1).getHeight(null) != GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, 1).getHeight(null))) {
            useDecoration = false;
        }

        Line2D.Double dragLine = new Line2D.Double(-1, -1, xe, ye);
        int markX = -1;
        int markY = -1;
        boolean markActiveVillage = false;

        Village mouseVillage = MapPanel.getSingleton().getVillageAtMousePos();
        try {
            markActiveVillage = Boolean.parseBoolean(GlobalOptions.getProperty("mark.active.village"));
        } catch (Exception e) {
            markActiveVillage = false;
        }
        boolean markedOnly = false;
        try {
            markedOnly = Boolean.parseBoolean(GlobalOptions.getProperty("draw.marked.only"));
        } catch (Exception e) {
            markedOnly = false;
        }

        boolean markTroopTypes = false;
        try {
            markTroopTypes = Boolean.parseBoolean(GlobalOptions.getProperty("paint.troops.type"));
        } catch (Exception e) {
            markTroopTypes = false;
        }

        boolean showSectors = false;
        try {
            showSectors = Boolean.parseBoolean(GlobalOptions.getProperty("show.sectors"));
        } catch (Exception e) {
            showSectors = false;
        }

        boolean showContinents = false;
        try {
            showContinents = Boolean.parseBoolean(GlobalOptions.getProperty("map.showcontinents"));
        } catch (Exception e) {
            showContinents = false;
        }

        List<Integer> xSectors = new LinkedList<Integer>();
        List<Integer> ySectors = new LinkedList<Integer>();
        List<Integer> xContinents = new LinkedList<Integer>();
        List<Integer> yContinents = new LinkedList<Integer>();
        //       Hashtable<Village, Point> tagIconPoints = new Hashtable<Village, Point>();
        Hashtable<Point, Image> troopMarkPoints = new Hashtable<Point, Image>();

        //</editor-fold>

        for (int i = 0; i < iVillagesX; i++) {
            for (int j = 0; j < iVillagesY; j++) {
                Village v = mVisibleVillages[i][j];
                boolean drawVillage = true;

                // <editor-fold defaultstate="collapsed" desc="Marker settings">

                Color DEFAULT = Color.WHITE;
                try {
                    if (Integer.parseInt(GlobalOptions.getProperty("default.mark")) == 1) {
                        DEFAULT = Color.RED;
                    }
                } catch (Exception e) {
                    DEFAULT = Color.WHITE;
                }
                Color marker = DEFAULT;
                g2d.setColor(marker);
                if (v != null) {
                    if (v.getTribe() != null) {
                        if (v.getTribe().getName().equals(GlobalOptions.getProperty("player." + GlobalOptions.getProperty("default.server")))) {
                            marker = Color.YELLOW;

                            // <editor-fold defaultstate="collapsed" desc=" Tag filtering ">
                            //user villages are not drawn by default but with accepted tags
                            drawVillage = false;

                            List<Tag> villageTags = TagManager.getSingleton().getTags(v);
                            if ((villageTags == null) || (villageTags.size() == 0)) {
                                //if no tag found draw village
                                drawVillage = true;
                            } else {
                                for (Tag tag : TagManager.getSingleton().getTags(v)) {
                                    if (tag.isShowOnMap()) {
                                        //at least one of the tags for the village are visible
                                        drawVillage = true;
                                        break;
                                    }
                                }
                            }
                        //</editor-fold>
                        } else {
                            try {
                                Marker m = MarkerManager.getSingleton().getMarker(v.getTribe());
                                if (m == null) {
                                    m = MarkerManager.getSingleton().getMarker(v.getTribe().getAlly());
                                    if (m != null) {
                                        marker = m.getMarkerColor();
                                    } else {
                                        //abort this mark
                                        throw new NullPointerException("");
                                    }
                                } else {
                                    marker = m.getMarkerColor();
                                }
                            } catch (Throwable t) {
                                marker = DEFAULT;
                                if (markedOnly) {
                                    marker = null;
                                }
                            }
                        }
                    }
                }
                //</editor-fold>

                if ((v != null) && (mSourceVillage != null)) {
                    if ((mSourceVillage.getX() == v.getX()) && (mSourceVillage.getY() == v.getY())) {
                        dragLine.setLine(x + width / 2, y + height / 2, dragLine.getX2(), dragLine.getY2());
                    }
                }

                // <editor-fold defaultstate="collapsed" desc="Village drawing">

                //  if (mustDrawBackground) {
                if (v == null) {
                    Image underground = null;
                    if (useDecoration) {
                        underground = WorldDecorationHolder.getTexture(xPos, yPos, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                    }
                    if (underground == null) {
                        //either no decoration used or map part is outside map bounds
                        underground = GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                    }
                    g2d.drawImage(underground, x, y, null);

                } else {
                    if ((marker != null) && (drawVillage)) {
                        boolean isLeft = false;
                        if (v.getTribe() == null) {
                            isLeft = true;
                        }

                        if (v.getPoints() < 300) {
                            if (!isLeft) {
                                //changed
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V1, DSWorkbenchMainFrame.getSingleton().getZoomFactor());

                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B1, DSWorkbenchMainFrame.getSingleton().getZoomFactor());

                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V1_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B1_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        } else if (v.getPoints() < 1000) {
                            if (!isLeft) {
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V2, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B2, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V2_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B2_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        } else if (v.getPoints() < 3000) {
                            if (!isLeft) {
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V3, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B3, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V3_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B3_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        } else if (v.getPoints() < 9000) {
                            if (!isLeft) {
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V4, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B4, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V4_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B4_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        } else if (v.getPoints() < 11000) {
                            if (!isLeft) {
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V5, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B5, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V5_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B5_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        } else {
                            if (!isLeft) {
                                Image img = GlobalOptions.getSkin().getImage(Skin.ID_V6, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                if (v.getType() != 0) {
                                    img = GlobalOptions.getSkin().getImage(Skin.ID_B6, DSWorkbenchMainFrame.getSingleton().getZoomFactor());
                                }
                                g2d.setColor(marker);
                                g2d.fillRect(x, y, img.getWidth(null), img.getHeight(null));
                                g2d.drawImage(img, x, y, null);
                            } else {
                                if (v.getType() == 0) {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_V6_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                } else {
                                    g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_B6_LEFT, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                                }
                            }
                        }

                        /*                        if ((TagManager.getSingleton().getTags(v) != null) &&
                        (!TagManager.getSingleton().getTags(v).isEmpty())) {
                        int xc = x + (int) Math.round(width / 2);
                        int yc = y + (int) Math.round(height / 2);
                        tagIconPoints.put(v, new Point(xc - 10, yc - 10));
                        }
                         */

                        if (markTroopTypes) {
                            Image troopMark = TroopsManager.getSingleton().getTroopsMarkerForVillage(v);
                            if (troopMark != null) {
                                Point center = new Point(x + (int) Math.round(width / 2), y + (int) Math.round(height / 2));
                                troopMarkPoints.put(center, troopMark);
                            }
                        }

                        if (markActiveVillage) {
                            Village current = DSWorkbenchMainFrame.getSingleton().getCurrentUserVillage();
                            if (current != null) {
                                if (v.compareTo(current) == 0) {
                                    markX = x + (int) Math.round(width / 2);
                                    markY = y + (int) Math.round(height / 2);
                                }
                            }
                        }
                    } else {
                        g2d.drawImage(GlobalOptions.getSkin().getImage(Skin.ID_DEFAULT_UNDERGROUND, DSWorkbenchMainFrame.getSingleton().getZoomFactor()), x, y, null);
                        mVisibleVillages[i][j] = null;
                    }
                }
                //   }
                //</editor-fold>

                y += height;
                yPos++;

                if ((showSectors) && (yPos % 5 == 0)) {
                    int pos = (yPos - pYStart) * height;
                    ySectors.add(pos);
                }
                if ((showContinents) && (yPos % 100 == 0)) {
                    int pos = (yPos - pYStart) * height;
                    yContinents.add(pos);
                }
            }
            y = 0;
            x += width;
            yPos = pYStart;
            xPos++;
            if ((showSectors) && (xPos % 5 == 0)) {
                int pos = (xPos - pXStart) * width;
                xSectors.add(pos);
            }
            if ((showContinents) && (xPos % 100 == 0)) {
                int pos = (xPos - pXStart) * width;
                xContinents.add(pos);
            }
        }

        // showVillageInfo(g2d);

        // <editor-fold defaultstate="collapsed" desc=" Tag Icon drawing (NOT USED) ">

        /*        Composite old = g2d.getComposite();

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        if (!tagIconPoints.isEmpty()) {
        Enumeration<Village> villages = tagIconPoints.keys();

        while (villages.hasMoreElements()) {
        Village current = villages.nextElement();
        if (!mAnimators.containsKey(current)) {
        //don't draw icon if animation is running
        List<String> tags = TagManager.getSingleton().getTags(current);
        //for (String tag : tags) {
        //show only one tag
        Image tagImage = null;
        for (String t : TagManager.getSingleton().getTags(current)) {
        tagImage = TagManager.getSingleton().getUserTagIcon(t);
        if (tagImage != null) {
        break;
        }
        }
        if (tagImage != null) {
        g2d.drawImage(tagImage, tagIconPoints.get(current).x, tagIconPoints.get(current).y, null);

        }
        }
        }
        }

        try {
        Village v = MapPanel.getSingleton().getVillageAtMousePos();
        if ((v != null) && (tagIconPoints.get(v) != null)) {
        if (!mAnimators.containsKey(v)) {
        mAnimators.put(v, new TagAnimator(v, tagIconPoints.get(v).x, tagIconPoints.get(v).y));
        }
        }
        } catch (Exception e) {
        }

        updateTagMovement(g2d);

        g2d.setComposite(old);

         */
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Draw Drag line (Foreground)">

        if (mSourceVillage != null) {
            //draw dragging line for distance and attack
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(5.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            if (dragLine.getX1() == -1) {
                int xx = pXStart + xe / width;
                int yy = pYStart + ye / height;
                int tx = xx - mSourceVillage.getX();
                int ty = yy - mSourceVillage.getY();

                tx = xe - width * tx;
                ty = ye - height * ty;
                dragLine.setLine(tx, ty, xe, ye);
            }

            if ((dragLine.getX2() != 0) && (dragLine.getY2() != 0)) {
                int x1 = (int) dragLine.getX1();
                int y1 = (int) dragLine.getY1();
                int x2 = (int) dragLine.getX2();
                int y2 = (int) dragLine.getY2();
                g2d.drawLine(x1, y1, x2, y2);
                boolean drawDistance = false;
                try {
                    drawDistance = Boolean.parseBoolean(GlobalOptions.getProperty("draw.distance"));
                } catch (Exception e) {
                }
                if (drawDistance) {

                    if (mouseVillage != null) {
                        double d = DSCalculator.calculateDistance(mSourceVillage, mouseVillage);
                        String dist = nf.format(d);

                        Rectangle2D b = g2d.getFontMetrics().getStringBounds(dist, g2d);
                        g2d.drawImage(mDistBorder, null, x2 - 6, y2 - (int) Math.rint(b.getHeight()));
                        g2d.drawString(dist, x2, y2);
                    }
                }
            }
        }

        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Attack-line drawing (Foreground)">

        g2d.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        Enumeration<String> keys = AttackManager.getSingleton().getPlans();

        while (keys.hasMoreElements()) {
            String plan = keys.nextElement();
            Attack[] attacks = AttackManager.getSingleton().getAttackPlan(plan).toArray(new Attack[]{});
            for (Attack attack : attacks) {
                //go through all attacks
                if (attack.isShowOnMap()) {
                    //only enter if attack should be visible
                    //get line for this attack
                    Line2D.Double attackLine = new Line2D.Double(attack.getSource().getX(), attack.getSource().getY(), attack.getTarget().getX(), attack.getTarget().getY());
                    Rectangle2D.Double bounds = new Rectangle2D.Double(pXStart, pYStart, iVillagesX, iVillagesY);
                    String value = GlobalOptions.getProperty("attack.movement");
                    boolean showAttackMovement = (value == null) ? false : Boolean.parseBoolean(value);
                    int xStart = ((int) attackLine.getX1() - pXStart) * width + width / 2;
                    int yStart = ((int) attackLine.getY1() - pYStart) * height + height / 2;
                    int xEnd = (int) (attackLine.getX2() - pXStart) * width + width / 2;
                    int yEnd = (int) (attackLine.getY2() - pYStart) * height + height / 2;
                    ImageIcon unitIcon = null;
                    int unitXPos = 0;
                    int unitYPos = 0;
                    if (showAttackMovement) {
                        unitIcon = ImageManager.getUnitIcon(attack.getUnit());
                        if (unitIcon != null) {
                            long dur = (long) (DSCalculator.calculateMoveTimeInSeconds(attack.getSource(), attack.getTarget(), attack.getUnit().getSpeed()) * 1000);
                            long arrive = attack.getArriveTime().getTime();
                            long start = arrive - dur;
                            long current = System.currentTimeMillis();

                            if ((start < current) && (arrive > current)) {
                                //attack running
                                long runTime = System.currentTimeMillis() - start;
                                double perc = 100 * runTime / dur;
                                perc /= 100;
                                double xTar = xStart + (xEnd - xStart) * perc;
                                double yTar = yStart + (yEnd - yStart) * perc;
                                unitXPos = (int) xTar - unitIcon.getIconWidth() / 2;
                                unitYPos = (int) yTar - unitIcon.getIconHeight() / 2;
                            } else if ((start > System.currentTimeMillis()) && (arrive > current)) {
                                //attack not running, draw unit in source village
                                unitXPos = (int) xStart - unitIcon.getIconWidth() / 2;
                                unitYPos = (int) yStart - unitIcon.getIconHeight() / 2;
                            } else {
                                //attack arrived
                                unitXPos = (int) xEnd - unitIcon.getIconWidth() / 2;
                                unitYPos = (int) yEnd - unitIcon.getIconHeight() / 2;
                            }
                        }
                    }

                    g2d.setColor(attackColors.get(attack.getUnit().getName()));
                    g2d.drawLine(xStart, yStart, xEnd, yEnd);
                    g2d.setColor(Color.YELLOW);
                    if (bounds.contains(attackLine.getP1())) {
                        g2d.fillRect((int) xStart - 3, yStart - 1, 6, 6);
                    }
                    if (bounds.contains(attackLine.getP2())) {
                        g2d.fillOval((int) xEnd - 3, (int) yEnd - 3, 6, 6);
                    }

                    if (unitIcon != null) {
                        g2d.drawImage(unitIcon.getImage(), unitXPos, unitYPos, null);
                    }
                }
            }
            attacks = null;
        }
        //</editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Sector/Continent drawing (Foreground)">

        g2d.setStroke(new BasicStroke(0.5f));
        if (showSectors) {
            g2d.setColor(Color.BLACK);
            for (Integer xs : xSectors) {
                g2d.drawLine(xs, 0, xs, hb);
            }
            for (Integer ys : ySectors) {
                g2d.drawLine(0, ys, wb, ys);
            }
        }
        g2d.setStroke(new BasicStroke(1.0f));
        if (showContinents) {
            g2d.setColor(Color.YELLOW);
            for (Integer xs : xContinents) {
                g2d.drawLine(xs, 0, xs, hb);
            }
            for (Integer ys : yContinents) {
                g2d.drawLine(0, ys, wb, ys);
            }
        }
        //</editor-fold>


        //"paint.tag.icons"

        /*
        g2d.setColor(Color.GREEN);
        Composite a = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2d.setComposite(a);
        g2d.fillOval(xC, yC, width, height);
         */

        //Cursor problem workaround
        /*Point pos = MapPanel.getSingleton().getMousePosition();
        if (pos != null) {
        g2d.drawImage(ImageManager.getUnitIcon(ImageManager.ICON_AXE).getImage(), pos.x, pos.y, null);
        }
         */

        // <editor-fold defaultstate="collapsed" desc=" Misc foreground drawing">

        //mark current player village
        if (markX >= 0 && markY >= 0) {
            g2d.drawImage(mMarkerImage, markX, markY - mMarkerImage.getHeight(null), null);
        }

        Enumeration<Point> troopMarkKeys = troopMarkPoints.keys();
        while (troopMarkKeys.hasMoreElements()) {
            Point next = troopMarkKeys.nextElement();
            Image mark = troopMarkPoints.get(next);
            mark = mark.getScaledInstance((int) Math.rint(mark.getWidth(null) / DSWorkbenchMainFrame.getSingleton().getZoomFactor()), (int) Math.rint(mark.getHeight(null) / DSWorkbenchMainFrame.getSingleton().getZoomFactor()), width);
            g2d.drawImage(mark, next.x - mark.getWidth(null) / 2, next.y - mark.getHeight(null), null);
        }
//</editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Troop movement (Foreground)">

        boolean showTroopInfo = false;
        try {
            showTroopInfo = Boolean.parseBoolean(GlobalOptions.getProperty("show.troop.info"));
        } catch (Exception e) {
            showTroopInfo = false;
        }
        if (showTroopInfo) {
            if (mouseVillage != null) {
                VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(mouseVillage);
                if ((holder != null) && (!holder.getTroops().isEmpty())) {
                    //get half the units for the current server
                    int unitCount = DataHolder.getSingleton().getUnits().size();
                    FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
                    // int fontHeight = metrics.getHeight();
                    Point pos = MapPanel.getSingleton().getMousePosition();
                    //number format without fraction digits
                    NumberFormat numFormat = NumberFormat.getInstance();
                    numFormat.setMaximumFractionDigits(0);
                    numFormat.setMinimumFractionDigits(0);
                    //default width for unit number
                    int unitWidth = metrics.stringWidth("1.234.567");
                    //get largest unit value
                    for (Integer i : holder.getTroops()) {
                        int w = metrics.stringWidth(numFormat.format(i));
                        if (w > unitWidth) {
                            unitWidth = w;
                        }
                    }

                    int textHeight = metrics.getHeight();
                    int unitHeight = ImageManager.getUnitIcon(0).getImage().getHeight(null);

                    g2d.setColor(Constants.DS_BACK_LIGHT);
                    int popupWidth = 12 + unitWidth + unitHeight;
                    int popupHeight = unitCount * unitHeight + 10 + textHeight + 2;
                    g2d.fill3DRect(pos.x - popupWidth, pos.y, popupWidth, popupHeight, true);

                    g2d.setColor(Color.BLACK);

                    //draw state
                    String state = "(" + new SimpleDateFormat("dd.MM.yyyy").format(holder.getState()) + ")";
                    double dY = metrics.getStringBounds(state, g2d).getY();
                    g2d.drawString(state, pos.x - popupWidth + 5, pos.y - (int) Math.rint(dY) + 5);

                    double sx = textHeight / (double) ImageManager.getUnitIcon(0).getImage().getHeight(null);
                    for (int i = 0; i < unitCount; i++) {
                        //draw unit with a border of 5px
                        AffineTransform xform = AffineTransform.getTranslateInstance(pos.x - popupWidth + 5, pos.y + i * unitHeight + 5 + textHeight + 2);
                        xform.scale(sx, sx);
                        g2d.drawImage(ImageManager.getUnitIcon(i).getImage(), xform, null);
                        //draw the unit count
                        dY = metrics.getStringBounds(numFormat.format(holder.getTroops().get(i)), g2d).getY();
                        g2d.drawString(numFormat.format(holder.getTroops().get(i)), pos.x - popupWidth + 5 + unitHeight + 2, pos.y + i * unitHeight - (int) Math.rint(dY) + 5 + textHeight + 2);
                    }
                } else {
                    Point pos = MapPanel.getSingleton().getMousePosition();
                    if (pos != null) {
                        String noInfo = "keine Informationen";
                        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
                        int textWidth = metrics.stringWidth(noInfo);
                        int popupX = pos.x - textWidth - 10;
                        int popupY = pos.y;
                        Rectangle2D bounds = metrics.getStringBounds(noInfo, g2d);

                        g2d.setColor(Constants.DS_BACK_LIGHT);
                        g2d.fill3DRect(popupX, popupY, 10 + textWidth, metrics.getHeight() + 4, true);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(noInfo, popupX + 5, popupY - (int) Math.rint(bounds.getY()) + 2);
                    }
                }
            }
        }
        // </editor-fold>

        g2d.dispose();
    //g2df.dispose();
    }
    /*
    private void showVillageInfo(Graphics g2d) {
    Village v = MapPanel.getSingleton().getVillageAtMousePos();
    if (v == null) {
    return;
    }
    String aInfo = "-kein Stamm-";
    String tInfo = "Barbaren";
    String vInfo = v.getHTMLInfo();
    Tribe t = v.getTribe();
    if (t != null) {
    tInfo = v.getTribe().getHTMLInfo();
    Ally a = t.getAlly();
    if (a != null) {
    aInfo = v.getTribe().getAlly().getHTMLInfo();
    }
    }

    int wV = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), vInfo);
    int wT = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), tInfo);
    int wA = SwingUtilities.computeStringWidth(g2d.getFontMetrics(), aInfo);
    int width = (wV > wT) ? ((wV > wA) ? wV : wA) : ((wT > wA) ? wT : wA);
    int height = g2d.getFontMetrics().getHeight();
    g2d.setColor(Constants.DS_BACK_LIGHT);
    try {
    Point p = MouseInfo.getPointerInfo().getLocation();
    SwingUtilities.convertPointFromScreen(p, MapPanel.getSingleton());
    g2d.fillRect(p.x, p.y, width + 10, 3 * height + 6 + 10);

    } catch (Exception e) {
    //point outside
    }
    }

    private Hashtable<Village, TagAnimator> mAnimators = new Hashtable<Village, TagAnimator>();

    private void updateTagMovement(Graphics2D pG2d) {
    Village current = MapPanel.getSingleton().getVillageAtMousePos();
    Enumeration<Village> keys = mAnimators.keys();
    while (keys.hasMoreElements()) {
    //for (TagAnimator t : mAnimators) {
    TagAnimator t = mAnimators.get(keys.nextElement());
    if ((current == null) || (!t.getVillage().equals(current))) {
    t.setRise(false);
    } else {
    if (t.getVillage().equals(current)) {
    t.setRise(true);
    }
    }
    t.update(pG2d);
    }

    //cleanup
    keys = mAnimators.keys();
    while (keys.hasMoreElements()) {
    current = keys.nextElement();
    if (mAnimators.get(current).isFinished()) {
    mAnimators.remove(current);
    }
    }*/

    public static void main(String[] args) {
        int level = 0;
        int ID_MAP_REDRAW = 1;
        int ID_MARKER_REDRAW = 2;
        int ID_BASIC_DECORATION_REDRAW = 4;
        int ID_ATTACK_REDRAW = 8;
        int ID_EXTENDED_DECORATION_REDRAW = 16;
        System.out.println((level | ID_MAP_REDRAW | ID_MARKER_REDRAW|ID_BASIC_DECORATION_REDRAW ));
        System.out.println((level | ID_MARKER_REDRAW));
        System.out.println((level | ID_BASIC_DECORATION_REDRAW));
        System.out.println((level |ID_ATTACK_REDRAW));
        System.out.println((level | ID_EXTENDED_DECORATION_REDRAW));
    }
}

/*
class TagAnimator {

private Village mVillage = null;
private int iX = 0;
private int iY = 0;
private int iDistance = 0;
private boolean pRise = false;
private boolean bFinished = false;

public TagAnimator(Village pVillage, int pVillageX, int pVillageY) {
mVillage = pVillage;
iX = pVillageX;
iY = pVillageY;
iDistance = 1;
pRise = true;
}

public Village getVillage() {
return mVillage;
}

public void setRise(boolean pValue) {
pRise = pValue;
}

public boolean isFinished() {
return bFinished;
}

public void update(Graphics2D g2d) {
if (pRise) {
if (iDistance < 51) {
iDistance += 25;
}
} else {
iDistance -= 25;
if (iDistance <= 0) {
bFinished = true;
iDistance = 0;
}
}

//degree for every village to get a circle
double deg = 360 / TagManager.getSingleton().getTags(mVillage).size();
int cnt = 0;
for (String tag : TagManager.getSingleton().getTags(mVillage)) {
Image tagImage = TagManager.getSingleton().getUserTagIcon(tag);
//take next degree
double cd = cnt * deg;
int xv = (int) Math.rint(iX + iDistance * Math.cos(2 * Math.PI * cd / 360));
int yv = (int) Math.rint(iY + iDistance * Math.sin(2 * Math.PI * cd / 360));
int width = (int) Math.rint(tagImage.getWidth(null) * iDistance * 0.05);
int height = (int) Math.rint(tagImage.getHeight(null) * iDistance * 0.05);
if (width < tagImage.getWidth(null) || height < tagImage.getHeight(null)) {
width = tagImage.getWidth(null);
height = tagImage.getHeight(null);
}
g2d.drawImage(tagImage.getScaledInstance(width, height, Image.SCALE_FAST), xv, yv, null);
cnt++;
}
}
}
}
 */

