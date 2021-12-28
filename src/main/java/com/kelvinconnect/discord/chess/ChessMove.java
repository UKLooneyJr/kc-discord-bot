package com.kelvinconnect.discord.chess;

import com.kelvinconnect.discord.chess.piece.ChessPiece;

import java.util.Objects;
import java.util.Optional;

public class ChessMove {
    private final ChessPiece piece;
    private final Vector2D from;
    private final Vector2D to;
    private final ChessPiece taken;

    public ChessMove(ChessPiece piece, Vector2D from, Vector2D to, ChessPiece taken) {
        this.piece = Objects.requireNonNull(piece);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.taken = taken;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public Vector2D getFrom() {
        return from;
    }

    public Vector2D getTo() {
        return to;
    }

    public Optional<ChessPiece> getTaken() {
        return Optional.ofNullable(taken);
    }
}
