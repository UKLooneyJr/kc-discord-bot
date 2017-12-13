package com.kelvinconnect.discord.login;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.DiscordApiBuilder;
import de.btobastian.javacord.Javacord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Logs in with a Token read from a file
 *
 * Created by Adam on 14/03/2017.
 */
public class TokenFileLogin implements Login {

    private final String filepath;

    public TokenFileLogin(String filepath) {
        this.filepath = filepath;
    }

    public DiscordApi login() {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String token;
        try {
            token = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            System.out.println("Could not read file " + filepath);
            return null; // TODO: Make this throw an appropriate exception rather than returning null
        }
        return new DiscordApiBuilder().setToken(token).login().join();
    }
}
