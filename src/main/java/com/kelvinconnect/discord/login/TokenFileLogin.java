package com.kelvinconnect.discord.login;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logs in with a Token read from a file
 *
 * <p>Created by Adam on 14/03/2017.
 */
public class TokenFileLogin implements Login {
    private static final Logger logger = LogManager.getLogger(TokenFileLogin.class);

    private final String filepath;

    public TokenFileLogin(String filepath) {
        this.filepath = filepath;
    }

    public JDA login() {
        logger.info(() -> "Working Directory = " + System.getProperty("user.dir"));
        String token;
        try {
            token = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            logger.error(() -> "Could not read file " + filepath, e);
            return null;
        }
        return new TokenLogin(token.trim()).login();
    }
}
