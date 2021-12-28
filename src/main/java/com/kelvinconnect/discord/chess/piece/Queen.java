package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessBoard;
import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.chess.Vector2D;
import java.awt.*;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class Queen extends AbstractChessPiece {

    private static final EnumMap<ChessTeam, Image> images = new EnumMap<>(ChessTeam.class);

    public Queen(ChessTeam team, ChessBoard board) {
        super(team, board);
    }

    public static void setImageForTeam(ChessTeam team, Image image) {
        images.put(team, image);
    }

    @Override
    public Image getImage() {
        return images.get(getTeam());
    }

    @Override
    public List<Vector2D> getAvailableMoves() {
        List<Vector2D> directions =
                Arrays.asList(
                        new Vector2D(1, 0),
                        new Vector2D(0, 1),
                        new Vector2D(-1, 0),
                        new Vector2D(0, -1),
                        new Vector2D(1, -1),
                        new Vector2D(1, 1),
                        new Vector2D(-1, -1),
                        new Vector2D(-1, 1));

        return getMovesForDirections(directions);
    }
}
