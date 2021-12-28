package com.kelvinconnect.discord.chess;

import com.kelvinconnect.discord.chess.parser.ChessParserException;
import com.kelvinconnect.discord.chess.piece.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public class ChessBoard {
    private static final int BOARD_SIZE = 8;
    private static final int BOARD_PADDING = 100;
    private static final int SQUARE_SIZE = 64;
    private static final int SQUARE_PADDING = 8;
    private static final int IMAGE_SIZE = (BOARD_PADDING * 2) + (SQUARE_SIZE * BOARD_SIZE);
    private static final int PIECE_SIZE = SQUARE_SIZE - (SQUARE_PADDING * 2);
    private static final int ARROW_SIZE = 10;

    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(21, 162, 202);
    private static final Color DEFAULT_BLACK_TILE_COLOR = new Color(181, 136, 99);
    private static final Color DEFAULT_WHITE_TILE_COLOR = new Color(240, 217, 181);
    private static final Color SELECTED_HIGHLIGHT_COLOR = new Color(0, 128, 255, 80);
    private static final Color MOVE_HIGHLIGHT_COLOR = new Color(0, 255, 0, 80);
    private static final Color TAKE_HIGHLIGHT_COLOR = new Color(255, 0, 0, 80);

    private ChessPiece[][] board;
    private final Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private final Color blackTileColor = DEFAULT_BLACK_TILE_COLOR;
    private final Color whiteTileColor = DEFAULT_WHITE_TILE_COLOR;

    private List<ChessMove> moves;
    private ChessPiece highlight;
    private ChessTeam turn;

    public ChessBoard() {
        defaultBoard();
        startGame();
    }

    /**
     * Constructs a board with an initial piece setup defined by an array of 8 strings, each
     * representing a single row of the board. Each row should be 8 characters long, a space is used
     * to represent an empty space, upper case characters are white pieces, and lower case
     * characters are black pieces. The letter for each piece is as follows:
     *
     * <ul>
     *   <li>{@code "K"} - King
     *   <li>{@code "Q"} - Queen
     *   <li>{@code "R"} - Rook
     *   <li>{@code "N"} - Knight
     *   <li>{@code "B"} - Bishop
     *   <li>{@code "P"} - Pawn
     *   <li>{@code " "} - No piece
     * </ul>
     *
     * For example, a default board can be created using this constructor by calling the following:
     *
     * <pre>{@code
     * ChessBoard board = new ChessBoard("rnbqkbnr",
     *                                   "pppppppp",
     *                                   "        ",
     *                                   "        ",
     *                                   "        ",
     *                                   "        ",
     *                                   "PPPPPPPP",
     *                                   "RNBQKBNR");
     * }</pre>
     *
     * @param rows an array of 8 strings, each string should be 8 characters long and contain only
     *     the characters specified above
     * @throws ChessParserException if too many or too few rows are provided, if any of the rows are
     *     too short or two long, or if an invalid character is used in any of the rows
     */
    public ChessBoard(String... rows) throws ChessParserException {
        if (rows.length != BOARD_SIZE) {
            throw new ChessParserException("Invalid number of rows: " + rows.length);
        }
        board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < rows.length; ++i) {
            String row = rows[i];
            if (row.length() != BOARD_SIZE) {
                throw new ChessParserException(
                        "Invalid number of columns in row [" + i + "]: " + row.length());
            }
            board[i] = parseRow(row, this);
        }

        // rotate array since we set columns, not rows
        ChessPiece[][] rotated = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                rotated[i][j] = board[BOARD_SIZE - j - 1][i];
            }
        }
        board = rotated;

        startGame();
    }

    private static ChessPiece[] parseRow(String row, ChessBoard board) throws ChessParserException {
        try {
            return Arrays.stream(row.split(""))
                    .map(
                            (Function<String, ChessPiece>)
                                    s -> {
                                        if (s.trim().isEmpty()) {
                                            return null;
                                        }
                                        ChessTeam team =
                                                Character.isUpperCase(s.charAt(0))
                                                        ? ChessTeam.WHITE
                                                        : ChessTeam.BLACK;
                                        switch (s.toLowerCase(Locale.ROOT)) {
                                            case "p":
                                                return new Pawn(team, board);
                                            case "r":
                                                return new Rook(team, board);
                                            case "b":
                                                return new Bishop(team, board);
                                            case "n":
                                                return new Knight(team, board);
                                            case "q":
                                                return new Queen(team, board);
                                            case "k":
                                                return new King(team, board);
                                            default:
                                                throw new IllegalArgumentException(
                                                        "Unknown piece: '" + s + "'");
                                        }
                                    })
                    .toArray(ChessPiece[]::new);
        } catch (IllegalArgumentException e) {
            throw new ChessParserException(e.getMessage(), e);
        }
    }

    private void startGame() {
        moves = new ArrayList<>();
        turn = ChessTeam.WHITE;
    }

    private void defaultBoard() {
        this.board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        // pawns
        for (int i = 0; i < BOARD_SIZE; ++i) {
            board[i][1] = new Pawn(ChessTeam.WHITE, this);
            board[i][6] = new Pawn(ChessTeam.BLACK, this);
        }
        // white
        board[0][0] = new Rook(ChessTeam.WHITE, this);
        board[7][0] = new Rook(ChessTeam.WHITE, this);
        board[1][0] = new Knight(ChessTeam.WHITE, this);
        board[6][0] = new Knight(ChessTeam.WHITE, this);
        board[2][0] = new Bishop(ChessTeam.WHITE, this);
        board[5][0] = new Bishop(ChessTeam.WHITE, this);
        board[3][0] = new Queen(ChessTeam.WHITE, this);
        board[4][0] = new King(ChessTeam.WHITE, this);
        // black
        board[0][7] = new Rook(ChessTeam.BLACK, this);
        board[7][7] = new Rook(ChessTeam.BLACK, this);
        board[1][7] = new Knight(ChessTeam.BLACK, this);
        board[6][7] = new Knight(ChessTeam.BLACK, this);
        board[2][7] = new Bishop(ChessTeam.BLACK, this);
        board[5][7] = new Bishop(ChessTeam.BLACK, this);
        board[3][7] = new Queen(ChessTeam.BLACK, this);
        board[4][7] = new King(ChessTeam.BLACK, this);
    }

    public Vector2D getPosition(ChessPiece piece) {
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                ChessPiece p = board[x][y];
                if (p == piece) {
                    return new Vector2D(x, y);
                }
            }
        }
        throw new IllegalArgumentException("ChessPiece " + piece + " is not on this board.");
    }

    public boolean isValid(Vector2D pos) {
        int x = pos.getX();
        int y = pos.getY();
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public Optional<ChessPiece> getPieceAt(Vector2D pos) {
        if (isValid(pos)) {
            return Optional.ofNullable(board[pos.getX()][pos.getY()]);
        }
        return Optional.empty();
    }

    public void move(Vector2D from, Vector2D to) {
        ChessPiece piece =
                getPieceAt(from)
                        .orElseThrow(() -> new IllegalArgumentException("No piece at " + from));
        if (!piece.getAvailableMoves().contains(to)) {
            throw new IllegalArgumentException(piece + " cannot move to " + to);
        }

        ChessPiece oldPiece = board[to.getX()][to.getY()];

        board[from.getX()][from.getY()] = null;
        board[to.getX()][to.getY()] = piece;
        piece.onMove();

        moves.add(new ChessMove(piece, from, to, oldPiece));
    }

    /** @return true if a piece was highlighted, false otherwise */
    public boolean setHighlight(Vector2D pos) {
        Optional<ChessPiece> piece = getPieceAt(pos);
        if (piece.isPresent()) {
            highlight = piece.get();
            return true;
        } else {
            highlight = null;
            return false;
        }
    }

    public BufferedImage draw() {
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();

        // fill background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        // draw board
        for (int y = 0; y < BOARD_SIZE; ++y) {
            for (int x = 0; x < BOARD_SIZE; ++x) {
                int xPos = getXPos(x);
                int yPos = getYPos(y);
                g2d.setColor((x + y) % 2 == 0 ? blackTileColor : whiteTileColor);
                g2d.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
                ChessPiece piece = board[x][y];
                if (null != piece) {
                    g2d.drawImage(
                            piece.getImage(),
                            xPos + SQUARE_PADDING,
                            yPos + SQUARE_PADDING,
                            PIECE_SIZE,
                            PIECE_SIZE,
                            null);
                }
            }
        }

        // draw last move
        if (!moves.isEmpty()) {
            ChessMove lastMove = moves.get(moves.size() - 1);

            Graphics2D g = (Graphics2D) g2d.create();
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(3.f));

            // draw line
            int xFrom = getXPos(lastMove.getFrom().getX()) + SQUARE_SIZE / 2;
            int yFrom = getYPos(lastMove.getFrom().getY()) + SQUARE_SIZE / 2;
            int xTo = getXPos(lastMove.getTo().getX()) + SQUARE_SIZE / 2;
            int yTo = getYPos(lastMove.getTo().getY()) + SQUARE_SIZE / 2;
            Line2D.Double line = new Line2D.Double(xFrom, yFrom, xTo, yTo);
            g.draw(line);

            // draw arrow head
            Polygon arrowHead = new Polygon();
            arrowHead.addPoint(0, ARROW_SIZE);
            arrowHead.addPoint(-ARROW_SIZE, -ARROW_SIZE);
            arrowHead.addPoint(ARROW_SIZE, -ARROW_SIZE);

            AffineTransform tx = new AffineTransform();
            tx.setToIdentity();
            double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
            tx.translate(line.x2, line.y2);
            tx.rotate((angle - Math.PI / 2d));
            g.setTransform(tx);
            g.fill(arrowHead);

            g.dispose();
        }

        // draw highlighted piece
        if (null != highlight) {
            List<Vector2D> availableMoves = highlight.getAvailableMoves();
            for (Vector2D move : availableMoves) {
                int xPos = getXPos(move.getX());
                int yPos = getYPos(move.getY());
                boolean occupied = null != board[move.getX()][move.getY()];
                g2d.setColor(occupied ? TAKE_HIGHLIGHT_COLOR : MOVE_HIGHLIGHT_COLOR);
                g2d.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
            }
            Vector2D pos = getPosition(highlight);
            g2d.setColor(SELECTED_HIGHLIGHT_COLOR);
            g2d.fillRect(getXPos(pos.getX()), getYPos(pos.getY()), SQUARE_SIZE, SQUARE_SIZE);
        }

        // draw border around board
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(3.f));
        g2d.drawRect(BOARD_PADDING, BOARD_PADDING, SQUARE_SIZE * 8, SQUARE_SIZE * 8);

        g2d.dispose();

        return image;
    }

    private int getXPos(int x) {
        return BOARD_PADDING + (x * SQUARE_SIZE);
    }

    private int getYPos(int y) {
        // flip y so origin is at bottom left
        return BOARD_PADDING + ((BOARD_SIZE - y - 1) * SQUARE_SIZE);
    }
}
