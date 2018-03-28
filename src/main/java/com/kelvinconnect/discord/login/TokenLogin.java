package com.kelvinconnect.discord.login;

import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.DiscordApiBuilder;

public class TokenLogin implements Login {

    private final String token;

    public TokenLogin(String token) {
        this.token = token;
    }

    public DiscordApi login() {
        return new DiscordApiBuilder().setToken(token).login().join();
    }
}
