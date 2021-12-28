package com.kelvinconnect.discord.chess;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vector2D {
    private final int x;
    private final int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2D parse(String str) throws IllegalArgumentException {
        Pattern pattern = Pattern.compile("([a-hA-H])[,/]?([1-8])");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            char c = matcher.group(1).toLowerCase(Locale.ROOT).charAt(0);
            int x = c - 'a';
            int y = Integer.parseInt(matcher.group(2)) - 1;
            return new Vector2D(x, y);
        }
        throw new IllegalArgumentException("Could not parse position '" + str + "'");
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.getX(), y + other.getY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vector2D position = (Vector2D) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "" + (char) ('A' + x) + (y + 1);
    }
}
