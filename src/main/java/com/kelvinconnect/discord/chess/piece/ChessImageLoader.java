package com.kelvinconnect.discord.chess.piece;

import com.kelvinconnect.discord.chess.ChessTeam;
import com.kelvinconnect.discord.utils.DiscordUtils;
import org.javacord.api.entity.server.Server;

import static com.kelvinconnect.discord.utils.ImageUtils.getImageForString;

public class ChessImageLoader {

    private ChessImageLoader() {
        throw new UnsupportedOperationException("this class should not be instantiated");
    }

    public static void unicodeImages() {
        Pawn.setImageForTeam(ChessTeam.WHITE, getImageForString("♙"));
        Pawn.setImageForTeam(ChessTeam.BLACK, getImageForString("♟"));
        Rook.setImageForTeam(ChessTeam.WHITE, getImageForString("♖"));
        Rook.setImageForTeam(ChessTeam.BLACK, getImageForString("♜"));
        Knight.setImageForTeam(ChessTeam.WHITE, getImageForString("♘"));
        Knight.setImageForTeam(ChessTeam.BLACK, getImageForString("♞"));
        Bishop.setImageForTeam(ChessTeam.WHITE, getImageForString("♗"));
        Bishop.setImageForTeam(ChessTeam.BLACK, getImageForString("♝"));
        Queen.setImageForTeam(ChessTeam.WHITE, getImageForString("♕"));
        Queen.setImageForTeam(ChessTeam.BLACK, getImageForString("♛"));
        King.setImageForTeam(ChessTeam.WHITE, getImageForString("♔"));
        King.setImageForTeam(ChessTeam.BLACK, getImageForString("♚"));
    }

    public static void textImages() {
        Pawn.setImageForTeam(ChessTeam.WHITE, getImageForString("P"));
        Pawn.setImageForTeam(ChessTeam.BLACK, getImageForString("p"));
        Rook.setImageForTeam(ChessTeam.WHITE, getImageForString("R"));
        Rook.setImageForTeam(ChessTeam.BLACK, getImageForString("r"));
        Knight.setImageForTeam(ChessTeam.WHITE, getImageForString("Kn"));
        Knight.setImageForTeam(ChessTeam.BLACK, getImageForString("kn"));
        Bishop.setImageForTeam(ChessTeam.WHITE, getImageForString("B"));
        Bishop.setImageForTeam(ChessTeam.BLACK, getImageForString("b"));
        Queen.setImageForTeam(ChessTeam.WHITE, getImageForString("Q"));
        Queen.setImageForTeam(ChessTeam.BLACK, getImageForString("q"));
        King.setImageForTeam(ChessTeam.WHITE, getImageForString("K"));
        King.setImageForTeam(ChessTeam.BLACK, getImageForString("k"));
    }

    public static void serverEmojiImages(Server server) {
        // pawn
        DiscordUtils.getEmojiImage(server, "colin")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Pawn.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "baffo")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Pawn.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));

        // bishop
        DiscordUtils.getEmojiImage(server, "minto")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Bishop.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "pokerface")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Bishop.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));

        // knight
        DiscordUtils.getEmojiImage(server, "tizzle")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Knight.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "gregg")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Knight.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));

        // rook
        DiscordUtils.getEmojiImage(server, "sam")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Rook.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "samrage")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Rook.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));

        // queen
        DiscordUtils.getEmojiImage(server, "mainMan")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Queen.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "bobbett")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                Queen.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));

        // king
        DiscordUtils.getEmojiImage(server, "party_adam")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                King.setImageForTeam(
                                                        ChessTeam.WHITE, bufferedImage)));
        DiscordUtils.getEmojiImage(server, "pilotfraser")
                .ifPresent(
                        f ->
                                f.thenAccept(
                                        bufferedImage ->
                                                King.setImageForTeam(
                                                        ChessTeam.BLACK, bufferedImage)));
    }
}
