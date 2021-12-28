package com.kelvinconnect.discord.chess;

import com.kelvinconnect.discord.chess.parser.ChessParserException;
import com.kelvinconnect.discord.chess.piece.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChessBoardTest {

    private static final boolean WRITE_TEST_IMAGES = true;
    private static final boolean USE_UNICODE_IMAGES = true;

    public static Path imageDir = Paths.get("testresults", ChessBoardTest.class.getSimpleName());

    @BeforeClass
    public static void createImageDir() throws IOException {
        Files.createDirectories(imageDir);
    }

    @BeforeClass
    public static void setChessPieceImages() {
        if (USE_UNICODE_IMAGES) {
            ChessImageLoader.unicodeImages();
        } else {
            ChessImageLoader.textImages();
        }
    }

    @Test
    public void test() throws ChessParserException, IOException {
        ChessBoard board =
                new ChessBoard(
                        "rnbqkbnr",
                        "pppppppp",
                        "        ",
                        "        ",
                        "        ",
                        "        ",
                        "PPPPPPPP",
                        "RNBQKBNR");

        drawBoard(board, "board");

        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                board.setHighlight(new Vector2D(x, y));
                drawBoard(board, x + "x" + y);
            }
        }
    }

    private void drawBoard(ChessBoard board, String name) throws IOException {
        BufferedImage img = board.draw();
        if (WRITE_TEST_IMAGES) {
            // get test method name from the stack trace
            File file =
                    Arrays.stream(Thread.currentThread().getStackTrace())
                            .filter(x -> ChessBoardTest.class.getName().equals(x.getClassName()))
                            .reduce((a, b) -> b)
                            .map(ste -> ste.getMethodName() + "_" + name + ".gif")
                            .map(path -> imageDir.resolve(path))
                            .orElse(imageDir.resolve(name + ".gif"))
                            .toFile();
            ImageIO.write(img, "gif", file);
        }
    }
}
