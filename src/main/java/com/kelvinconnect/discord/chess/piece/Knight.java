package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessBoard;
import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.chess.Vector2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Knight extends AbstractChessPiece {

    private static final EnumMap<ChessTeam, Image> images = new EnumMap<>(ChessTeam.class);

    public Knight(ChessTeam team, ChessBoard board) {
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
        List<Vector2D> moves = new ArrayList<>();

        Vector2D position = getPosition();

        for (int i = 0; i < 8; ++i) {
            int x = (1 & i) == 0 ? 1 : 2;
            int y = (1 & i) == 0 ? 2 : 1;
            x = (2 & i) == 0 ? x : -x;
            y = (4 & i) == 0 ? y : -y;
            Vector2D direction = new Vector2D(x, y);
            Vector2D target = position.add(direction);
            if (canMove(target)) {
                moves.add(target);
            }
        }

        return moves;
    }
}
