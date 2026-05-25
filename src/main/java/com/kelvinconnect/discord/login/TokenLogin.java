package com.kelvinconnect.discord.login;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class TokenLogin implements Login {

    private final String token;

    public TokenLogin(String token) {
        this.token = token;
    }

    public JDA login() {
        try {
            return JDABuilder.createDefault(token)
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_VOICE_STATES)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to log in to Discord", e);
        }
    }
}
