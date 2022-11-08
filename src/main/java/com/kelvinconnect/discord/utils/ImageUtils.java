package com.kelvinconnect.discord.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("this class should not be instantiated");
    }

    public static BufferedImage getImageForString(String text) {
        // Because font metrics is based on a graphics context, we need to create a small, temporary
        // image so we can ascertain the width and height of the final image
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        // Lucida Sans should be installed on every version of Windows
        Font font = new Font("Lucida Sans", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        return img;
    }

}
