package com.kelvinconnect.discord;

import com.google.common.util.concurrent.FutureCallback;
import com.kelvinconnect.discord.command.HelpCommand;
import com.kelvinconnect.discord.command.InfoCommand;
import com.kelvinconnect.discord.command.TicketCommand;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;

/**
 * Entry point for KC Discord Bot
 *
 * Created by Adam on 14/03/2017.
 */
public class KCDiscordBot {

    public static void main(String[] args) {
        Login l = new TokenFileLogin("src/main/resources/loginToken.txt");
        DiscordAPI api = l.login();

        if (null == api) {
            return;
        }

        registerCommands(api);
        start(api);
    }

    private static void registerCommands(DiscordAPI api) {
        CommandHandler handler = new JavacordHandler(api);
        handler.registerCommand(new HelpCommand(handler));
        handler.registerCommand(new InfoCommand());
        handler.registerCommand(new TicketCommand());
    }

    private static void start(DiscordAPI api) {
        api.setWaitForServersOnStartup(false);
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(final DiscordAPI api) {
                // do what you want now
                GameRandomiser gameRandomiser = new GameRandomiser();
                gameRandomiser.start(api);
            }

            public void onFailure(Throwable t) {
                // login failed
                t.printStackTrace();
            }
        });
    }
}
