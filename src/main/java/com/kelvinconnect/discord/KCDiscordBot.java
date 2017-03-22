package com.kelvinconnect.discord;

import com.google.common.util.concurrent.FutureCallback;
import com.kelvinconnect.discord.command.DebugCommand;
import com.kelvinconnect.discord.command.HelpCommand;
import com.kelvinconnect.discord.command.InfoCommand;
import com.kelvinconnect.discord.command.TicketCommand;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import de.btobastian.javacord.utils.LoggerUtil;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * Entry point for KC Discord Bot
 *
 * Created by Adam on 14/03/2017.
 */
public class KCDiscordBot {
    private static final Logger logger = LoggerUtil.getLogger(KCDiscordBot.class);

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
        handler.registerCommand(new DebugCommand());
    }

    private static void start(DiscordAPI api) {
        api.setWaitForServersOnStartup(false);
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(final DiscordAPI api) {
                // do what you want now
                GameRandomiser gameRandomiser = new GameRandomiser();
                gameRandomiser.start(api);

                PubChatAlert pubChatAlert = new PubChatAlert(api);
                pubChatAlert.start();
            }

            public void onFailure(Throwable t) {
                // login failed
                t.printStackTrace();
            }
        });
    }
}
