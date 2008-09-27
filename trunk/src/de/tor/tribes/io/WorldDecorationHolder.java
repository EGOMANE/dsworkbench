/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.io;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 *
 * @author Charon
 */
public class WorldDecorationHolder {

    private static Logger logger = Logger.getLogger(WorldDecorationHolder.class);
    private static byte[] decoration = new byte[1000000];
    private static List<BufferedImage> mTextures = null;

    public static void initialize() throws FileNotFoundException, Exception {
        loadWorld();
    }

    private static void loadWorld() throws FileNotFoundException, Exception {
        try {
            GZIPInputStream fin = new GZIPInputStream(new FileInputStream("world.dat.gz"));
            // FileInputStream fin = new FileInputStream("world.dat");
            ByteBuffer bb = ByteBuffer.allocate(1000000);
            byte[] d = new byte[1024];
            int c = 0;
            while ((c = fin.read(d)) != -1) {
                bb.put(d, 0, c);
            }
            decoration = bb.array();
            fin.close();
        } catch (Exception e) {
            logger.error("Failed to read world.dat.gz");
            throw new Exception("Unable to read world.dat.gz", e);
        }
        loadTextures();
    }

    private static void loadTextures() throws Exception {
        mTextures = new LinkedList<BufferedImage>();
        try {
            mTextures.add(ImageIO.read(new File("graphics/world/gras1.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras2.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras3.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras4.png")));
            //dummy values
            mTextures.add(ImageIO.read(new File("graphics/world/gras4.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras4.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras4.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/gras4.png")));

            mTextures.add(ImageIO.read(new File("graphics/world/berg1.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/berg2.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/berg3.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/berg4.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/see.png")));
            //dummy values
            mTextures.add(ImageIO.read(new File("graphics/world/see.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/see.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/see.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0000.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0001.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0010.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0011.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0100.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0101.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0110.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest0111.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1000.png")));
            mTextures.add(ImageIO.read(new File("./graphics/world/forest1001.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1010.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1011.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1100.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1101.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1110.png")));
            mTextures.add(ImageIO.read(new File("graphics/world/forest1111.png")));
        } catch (Exception e) {
            throw new Exception("Failed to load world textures", e);
        }
    }

    public static Image getTexture(int pX, int pY, double pScale) {
        return mTextures.get(decoration[pY * 1000 + pX]).getScaledInstance((int) Math.rint(mTextures.get(0).getWidth() / pScale), (int) Math.rint(mTextures.get(0).getHeight() / pScale), BufferedImage.SCALE_FAST);
    }
}
