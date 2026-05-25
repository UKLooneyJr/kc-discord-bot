package com.kelvinconnect.discord.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDispatcher extends ListenerAdapter {

    private static final Logger logger = LogManager.getLogger(CommandDispatcher.class);
    private static final String PREFIX = "!";

    private final List<BotCommand> commands = new ArrayList<>();
    private final Map<String, BotCommand> commandMap = new HashMap<>();

    public void register(BotCommand command) {
        commands.add(command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias.toLowerCase(), command);
        }
    }

    public List<BotCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String content = event.getMessage().getContentRaw().trim();
        if (!content.startsWith(PREFIX)) {
            return;
        }

        String[] parts = content.substring(PREFIX.length()).split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return;
        }

        String commandKey = PREFIX + parts[0].toLowerCase();
        BotCommand cmd = commandMap.get(commandKey);
        if (cmd == null) {
            return;
        }

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        try {
            cmd.execute(event, args);
        } catch (Exception e) {
            logger.error("Error executing command {}", commandKey, e);
        }
    }
}
