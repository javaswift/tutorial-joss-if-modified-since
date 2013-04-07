package org.javaswift.joss.tutorial;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage placeText(BufferedImage image, String text) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        int fontSize = 12;
        g.setFont(new Font( "SansSerif", Font.BOLD, fontSize));
        for(int i=1; i<=image.getHeight(); i=i+fontSize) {
            for(int j=1; j<=image.getWidth(); j=j+(fontSize*text.length())) {
                g.drawString(text, j, i);
            }
        }
        return image;
    }
}
