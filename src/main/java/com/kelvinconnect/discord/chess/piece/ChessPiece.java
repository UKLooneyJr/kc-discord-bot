package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.chess.Vector2D;
import java.awt.Image;
import java.util.List;

public interface ChessPiece {

    ChessTeam getTeam();

    Image getImage();

    List<Vector2D> getAvailableMoves();

    default void onMove() {}
}
