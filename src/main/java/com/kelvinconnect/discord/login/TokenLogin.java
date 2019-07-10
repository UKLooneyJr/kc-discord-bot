package com.kelvinconnect.discord.login;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class TokenLogin implements Login {

    private final String token;

    public TokenLogin(String token) {
        this.token = token;
    }

    public DiscordApi login() {
        return new DiscordApiBuilder().setToken(token).login().join();
    }
}
