package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessBoard;
import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.chess.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractChessPiece implements ChessPiece {
    private final ChessTeam team;
    private final ChessBoard board;

    protected AbstractChessPiece(ChessTeam team, ChessBoard board) {
        this.team = team;
        this.board = board;
    }

    @Override
    public ChessTeam getTeam() {
        return team;
    }

    protected Vector2D getPosition() {
        return board.getPosition(this);
    }

    protected ChessBoard getBoard() {
        return board;
    }

    protected List<Vector2D> getMovesForDirections(List<Vector2D> directions) {
        List<Vector2D> moves = new ArrayList<>();
        for (Vector2D dir : directions) {
            moves.addAll(getMovesForDirection(dir));
        }
        return moves;
    }

    protected List<Vector2D> getMovesForDirection(Vector2D dir) {
        List<Vector2D> moves = new ArrayList<>();
        Vector2D target = getPosition().add(dir);
        while (canMove(target)) {
            if (isBlocked(target)) {
                return moves;
            }
            moves.add(target);
            target = target.add(dir);
            if (canTake(target)) {
                return moves;
            }
        }
        return moves;
    }

    protected boolean canMove(Vector2D target) {
        if (!board.isValid(target)) {
            return false;
        }
        Optional<ChessPiece> targetPiece = board.getPieceAt(target);
        return targetPiece.map(piece -> piece.getTeam() != team).orElse(true);
    }

    protected boolean isBlocked(Vector2D target) {
        if (!board.isValid(target)) {
            return true;
        }
        Optional<ChessPiece> targetPiece = board.getPieceAt(target);
        return targetPiece.map(piece -> piece.getTeam() == team).orElse(false);
    }

    protected boolean canTake(Vector2D target) {
        if (!board.isValid(target)) {
            return false;
        }
        Optional<ChessPiece> targetPiece = board.getPieceAt(target);
        return targetPiece.map(piece -> piece.getTeam() != team).orElse(false);
    }

    @Override
    public String toString() {
        return team.toString() + " " + getClass().getSimpleName() + " " + getPosition();
    }
}
