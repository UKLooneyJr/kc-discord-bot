package com.kelvinconnect.discord.chess.parser;

public class ChessParserException extends Exception {
    public ChessParserException() {}

    public ChessParserException(String message) {
        super(message);
    }

    public ChessParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChessParserException(Throwable cause) {
        super(cause);
    }
}
