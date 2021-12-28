package com.kelvinconnect.discord.chess;

import com.kelvinconnect.discord.DiscordUtils;
import com.kelvinconnect.discord.chess.piece.ChessImageLoader;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.kelvinconnect.discord.DiscordUtils.listStartsWith;

public class ChessCommand implements CommandExecutor {

    private ChessBoard board;

    public ChessCommand(DiscordApi api) {
        api.getServerById(DiscordUtils.KC_SERVER_ID).ifPresent(ChessImageLoader::loadImages);

        board = new ChessBoard();
    }

    @Command(aliases = { "!chess" }, description = "Chess.")
    public void onChessCommand(Message message) {
        List<String> args = DiscordUtils.parseArgsFromMessage(message);

        Reply reply = handleInputs(args);

        MessageBuilder messageBuilder = new MessageBuilder();

        if (null != reply.message) {
            messageBuilder.append(reply.message);
        }

        if (reply.drawBoard) {
            BufferedImage image = board.draw();

            messageBuilder.addFile(image, "chess.png");
        }

        messageBuilder.send(message.getChannel());
    }

    private Reply handleInputs(List<String> args) {
        if (listStartsWith(args, "new", "game")) {
            newGame();
            return new Reply("Starting new game");
        }
        if (listStartsWith(args, "show", "moves")) {
            return showMoves(args);
        }
        if (listStartsWith(args, "move")) {
            return move(args);
        }
        return Reply.Standard;
    }

    private void newGame() {
        board = new ChessBoard();
    }

    private Reply showMoves(List<String> args) {
        if (args.size() != 3) {
            return Reply.Error;
        }
        try {
            Vector2D position = Vector2D.parse(args.get(2));
            if (board.setHighlight(position)) {
                return Reply.Standard;
            } else {
                return new Reply("No piece at position " + position);
            }
        } catch (IllegalArgumentException e) {
            return new Reply(e.getMessage(), false);
        }
    }

    private Reply move(List<String> args) {
        if (args.size() != 3) {
            return Reply.Error;
        }
        try {
            Vector2D from = Vector2D.parse(args.get(1));
            Vector2D to = Vector2D.parse(args.get(2));
            board.move(from, to);
        } catch (IllegalArgumentException e) {
            return new Reply(e.getMessage(), false);
        }
        return Reply.Standard;
    }

    private static class Reply {
        final String message;
        final boolean drawBoard;

        public Reply(String message) {
            this(message, true);
        }

        Reply(String message, boolean drawBoard) {
            this.drawBoard = drawBoard;
            this.message = message;
        }

        static final Reply Standard = new Reply(null, true);
        static final Reply Error = new Reply("Could not parse command", false);
    }
}
