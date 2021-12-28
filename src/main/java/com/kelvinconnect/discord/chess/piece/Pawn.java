package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessBoard;
import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.chess.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Pawn extends AbstractChessPiece {

    private static final EnumMap<ChessTeam, Image> images = new EnumMap<>(ChessTeam.class);

    private boolean moved;

    public Pawn(ChessTeam team, ChessBoard board) {
        super(team, board);
        moved = false;
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
        Vector2D pos = getPosition();
        ChessTeam team = getTeam();

        int direction = ChessTeam.WHITE == team ? 1 : -1;
        Vector2D fwd1 = new Vector2D(pos.getX(), pos.getY() + direction);
        Vector2D fwd2 = new Vector2D(pos.getX(), pos.getY() + 2 * direction);
        Vector2D fwdLeft = new Vector2D(pos.getX() - 1, pos.getY() + direction);
        Vector2D fwdRight = new Vector2D(pos.getX() + 1, pos.getY() + direction);

        List<Vector2D> moves = new ArrayList<>();

        getBoard().getPieceAt(fwdLeft).filter(p -> p.getTeam() != team).ifPresent(piece -> moves.add(fwdLeft));
        getBoard().getPieceAt(fwdRight).filter(p -> p.getTeam() != team).ifPresent(piece -> moves.add(fwdRight));

        if (moves.isEmpty()) {
            moves.add(fwd1);
            if (!moved) {
                moves.add(fwd2);
            }
        }

        return moves;
    }

    @Override
    public void onMove() {
        moved = true;
    }
}
