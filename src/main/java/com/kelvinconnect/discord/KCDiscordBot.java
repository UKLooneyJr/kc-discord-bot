package com.kelvinconnect.discord;

import com.google.common.util.concurrent.FutureCallback;
import com.kelvinconnect.discord.command.*;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import com.kelvinconnect.discord.scheduler.PubChatAlert;
import com.kelvinconnect.discord.scheduler.TaskScheduler;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.utils.LoggerUtil;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.slf4j.Logger;

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
        handler.registerCommand(new BangCommand());
        handler.registerCommand(new RobertCommand());
        handler.registerCommand(new StandoCommand());

        VotingBooth votingBooth = new VotingBooth();
        handler.registerCommand(new PubCommand(votingBooth));
        handler.registerCommand(new VoteCommand(votingBooth));
    }

    private static void startTasks(DiscordAPI api) {
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.runWeekly("pub chat", new PubChatAlert(api), 6, 16, 0);
    }

    private static void start(DiscordAPI api) {
        api.setWaitForServersOnStartup(false);
        api.connect(new FutureCallback<DiscordAPI>() {
            public void onSuccess(final DiscordAPI api) {
                // do what you want now
                GameRandomiser gameRandomiser = new GameRandomiser();
                gameRandomiser.start(api);
                startTasks(api);
            }

            public void onFailure(Throwable t) {
                // login failed
                t.printStackTrace();
            }
        });
    }
}
