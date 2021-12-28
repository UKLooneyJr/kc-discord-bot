package com.kelvinconnect.discord;

import com.kelvinconnect.discord.chess.ChessCommand;
import com.kelvinconnect.discord.command.*;
import com.kelvinconnect.discord.command.music.MusicCommand;
import com.kelvinconnect.discord.command.pub.PubCommand;
import com.kelvinconnect.discord.command.stando.StandoCommand;
import com.kelvinconnect.discord.login.Login;
import com.kelvinconnect.discord.login.TokenFileLogin;
import com.kelvinconnect.discord.login.TokenLogin;
import com.kelvinconnect.discord.scheduler.PubChatAlert;
import com.kelvinconnect.discord.scheduler.TaskScheduler;
import com.kelvinconnect.discord.ui.BotUI;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.JavacordHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;

import java.time.Instant;
import java.util.function.Function;

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
        Login l = parameters.getToken().map((Function<String, Login>) TokenLogin::new)
                .orElse(new TokenFileLogin("resources/loginToken.txt"));
        return l.login();
    }

    private static void registerCommands(DiscordApi api) {
        CommandHandler handler = new JavacordHandler(api);
        registerCommand(handler, new HelpCommand(handler));
        registerCommand(handler, new InfoCommand());
        registerCommand(handler, new UptimeCommand(Instant.now()));
        registerCommand(handler, new SlackCommand());
        registerCommand(handler, new BangCommand());
        registerCommand(handler, new RobertCommand());
        registerCommand(handler, new StandoCommand());
        registerCommand(handler, new PubCommand());
        registerCommand(handler, new RollCommand());
        registerCommand(handler, new JoinLeaveCommand(api));
        registerCommand(handler, new StockCommand());
        registerCommand(handler, new MusicCommand(api));
        registerCommand(handler, new ChessCommand(api));
    }

    private static void registerCommand(CommandHandler handler, CommandExecutor executor) {
        try {
            handler.registerCommand(executor);
        } catch (Exception e) {
            logger.error(() -> "Error registering command(s) for class " + executor.getClass().getSimpleName(), e);
        }
    }

    private static void startTasks(DiscordApi api) {
        GameRandomiser gameRandomiser = new GameRandomiser();
        gameRandomiser.start(api);
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.runWeekly("pub chat", new PubChatAlert(api), 6, 16, 0);
    }

    private static void initUI() {
        BotUI ui = new BotUI();
        ui.show();
    }
}
