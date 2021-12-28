package com.kelvinconnect.discord.chess;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Vector2DTest {

    @Test
    public void parsePosition() {
        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                int yOffset = y + 1;

                Vector2D expected = new Vector2D(x, y);

                char lowerCase = (char) ('a' + x);
                char upperCase = (char) ('A' + x);

                assertEquals(expected, Vector2D.parse(String.valueOf(lowerCase) + yOffset));
                assertEquals(expected, Vector2D.parse(String.valueOf(upperCase) + yOffset));
                assertEquals(expected, Vector2D.parse(lowerCase + "," + yOffset));
                assertEquals(expected, Vector2D.parse(upperCase + "," + yOffset));
                assertEquals(expected, Vector2D.parse(lowerCase + "/" + yOffset));
                assertEquals(expected, Vector2D.parse(upperCase + "/" + yOffset));
            }
        }
    }
}
