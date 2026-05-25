package com.kelvinconnect.discord;

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
import java.time.Instant;
import java.util.function.Function;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for KC Discord Bot
 *
 * <p>Created by Adam on 14/03/2017.
 */
public class KCDiscordBot {
    private static final Logger logger = LogManager.getLogger(KCDiscordBot.class);

    public static void main(String[] args) {
        Parameters parameters = Parameters.getInstance();
        parameters.parseCommandLine(args);

        JDA jda = login(parameters);

        if (null == jda) {
            logger.error("Error logging in.");
            return;
        }

        registerCommands(jda);
        startTasks(jda);
        initUI();
    }

    private static JDA login(Parameters parameters) {
        Login l =
                parameters
                        .getToken()
                        .map((Function<String, Login>) TokenLogin::new)
                        .orElse(new TokenFileLogin("resources/loginToken.txt"));
        return l.login();
    }

    private static void registerCommands(JDA jda) {
        long botOwnerId =
                jda.retrieveApplicationInfo().complete().getOwner().getIdLong();

        CommandDispatcher dispatcher = new CommandDispatcher();
        dispatcher.register(new HelpCommand(dispatcher));
        dispatcher.register(new InfoCommand());
        dispatcher.register(new UptimeCommand(Instant.now()));
        dispatcher.register(new SlackCommand());
        dispatcher.register(new BangCommand());
        dispatcher.register(new RobertCommand());
        dispatcher.register(new StandoCommand());
        dispatcher.register(new PubCommand());
        dispatcher.register(new RollCommand());
        dispatcher.register(new JoinLeaveCommand(jda, botOwnerId));
        dispatcher.register(new StockCommand());
        dispatcher.register(new MusicCommand(jda));
        jda.addEventListener(dispatcher);
    }

    private static void startTasks(JDA jda) {
        GameRandomiser gameRandomiser = new GameRandomiser();
        gameRandomiser.start(jda);
        TaskScheduler scheduler = new TaskScheduler();
        scheduler.runWeekly("pub chat", new PubChatAlert(jda), 6, 16, 0);
    }

    private static void initUI() {
        BotUI ui = new BotUI();
        ui.show();
    }
}
