package com.kelvinconnect.discord;

import com.kelvinconnect.discord.command.*;
import com.kelvinconnect.discord.command.pub.PubCommand;
import com.kelvinconnect.discord.command.stando.StandoCommand;
import com.kelvinconnect.discord.command.trac.ChangeSetCommand;
import com.kelvinconnect.discord.command.trac.TicketCommand;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import com.kelvinconnect.discord.login.TokenLogin;
import com.kelvinconnect.discord.rss.TracTimeline;
import com.kelvinconnect.discord.scheduler.PubChatAlert;
import com.kelvinconnect.discord.scheduler.TaskScheduler;
import com.kelvinconnect.discord.ui.BotUI;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;

import java.time.Instant;

/**
 * Entry point for KC Discord Bot
 * <p>
 * Created by Adam on 14/03/2017.
 */
public class KCDiscordBot {
    private static final Logger logger = LogManager.getLogger(KCDiscordBot.class);

    public static void main(String[] args) {
        Parameters parameters = Parameters.getInstance();
        parameters.parseCommandLine(args);

        DiscordApi api = login(parameters);

        if (null == api) {
            logger.error("Error logging in.");
            return;
        }

        registerCommands(api);
        startTasks(api);
        initUI();
    }

    private static DiscordApi login(Parameters parameters) {
        String token = parameters.getToken();
        Login l;
        if (token != null) {
            l = new TokenLogin(token);
        } else {
            l = new TokenFileLogin("resources/loginToken.txt");
        }
        return l.login();
    }

    private static void registerCommands(DiscordApi api) {
        CommandHandler handler = new JavacordHandler(api);
        handler.registerCommand(new HelpCommand(handler));
        handler.registerCommand(new InfoCommand());
        handler.registerCommand(new UptimeCommand(Instant.now()));
        handler.registerCommand(new TicketCommand());
        handler.registerCommand(new ChangeSetCommand());
        handler.registerCommand(new SlackCommand());
        handler.registerCommand(new BangCommand());
        handler.registerCommand(new RobertCommand());
        handler.registerCommand(new StandoCommand());
        handler.registerCommand(new PubCommand());
        handler.registerCommand(new RollCommand());
        handler.registerCommand(new JoinLeaveCommand(api));
    }

    private static void startTasks(DiscordApi api) {
        GameRandomiser gameRandomiser = new GameRandomiser();
        gameRandomiser.start(api);
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.runWeekly("pub chat", new PubChatAlert(api), 6, 16, 0);
        scheduler.runMinutely("timeline", new TracTimeline(api));
    }

    private static void initUI() {
        BotUI f = new BotUI();
        f.setVisible(true);
    }
}
