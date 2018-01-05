package com.kelvinconnect.discord;

import com.kelvinconnect.discord.command.*;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import com.kelvinconnect.discord.scheduler.PubChatAlert;
import com.kelvinconnect.discord.scheduler.TaskScheduler;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.utils.logging.LoggerUtil;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.http.concurrent.FutureCallback;
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
        DiscordApi api = l.login();

        if (null == api) {
            return;
        }

        registerCommands(api);
        start(api);
    }

    private static void registerCommands(DiscordApi api) {
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
    }

    private static void startTasks(DiscordApi api) {
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.runWeekly("pub chat", new PubChatAlert(api), 6, 16, 0);
    }

    private static void start(DiscordApi api) {
//        api.setWaitForServersOnStartup(false);
//        api.connect(new FutureCallback<DiscordApi>() {
//            @Override
//            public void completed(DiscordApi discordApi) {
//                GameRandomiser gameRandomiser = new GameRandomiser();
//                gameRandomiser.start(api);
//                startTasks(api);
//            }
//
//            @Override
//            public void failed(Exception e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void cancelled() {
//
//            }
//        });
    }
}
