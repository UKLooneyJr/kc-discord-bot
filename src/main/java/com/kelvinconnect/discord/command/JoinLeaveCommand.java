package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.*;
import java.util.stream.Collectors;

public class JoinLeaveCommand implements CommandExecutor {

    private final static String INVALID_CHANNEL_NAME = "Invalid channel name, try '!channels' for a list of all channels.";

    private static class KCChannel {
        String id;
        List<String> aliases;

        KCChannel(String id, String... aliases) {
            this.id = id;
            this.aliases = Arrays.asList(aliases);
        }
    }

    private List<KCChannel> channels;

    public JoinLeaveCommand() {
        initChannels();
    }

    private void initChannels() {
        channels = Arrays.asList(
                new KCChannel("365039527607140353", "niche"),
                new KCChannel("365039532829179904", "pnc"),
                new KCChannel("365039576521244672", "storm"),
                new KCChannel("365039600210804738", "nspis"),
                new KCChannel("365039626362290179", "compass"),
                new KCChannel("365039651721052161", "qas"),
                new KCChannel("365039660184895489", "singlepoint"),
                new KCChannel("365039678212276224", "connect"),
                new KCChannel("365039717017976835", "unifi"),
                new KCChannel("365039737402294274", "crash"),
                new KCChannel("365039767152492545", "compact"),
                new KCChannel("365039790028226563", "socrates")
        );
    }

    @Command(aliases = "!join", description = "Joins a channel.", usage = "!join <channel-name>")
    public void onJoinCommand(String args[], DiscordApi api, Message message) {
        if (args.length != 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
        getIdFromAlias(args[0]).<Runnable>map(id -> () -> assignRole(id, message))
                .orElse(() -> channelNotFound(message))
                .run();
    }

    private void assignRole(String roleId, Message message) {
        Server server = message.getServer().orElseThrow(() -> new RuntimeException("Failed to get Server"));
        Role role = server.getRoleById(roleId).orElseThrow(() -> new RuntimeException("Failed to get Role: " + roleId));
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(server);
        if (!roles.contains(role)) {
            roles.add(role);
        }

        server.updateRoles(user, roles);
    }

    @Command(aliases = "!leave", description = "Leaves a channel.", usage = "!leave [<channel-name>]")
    public void onLeaveCommand(String args[], DiscordApi api, Message message) {
        if (args.length > 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
        getIdFromAlias(args[0]).<Runnable>map(id -> () -> unassignRole(id, message))
                .orElse(() -> channelNotFound(message))
                .run();
    }

    private void unassignRole(String roleId, Message message) {
        Server server = message.getServer().orElseThrow(() -> new RuntimeException("Failed to get Server"));
        Role role = server.getRoleById(roleId).orElseThrow(() -> new RuntimeException("Failed to get Role"));
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(server);
        roles.remove(role);

        server.updateRoles(user, roles);
    }

    private void channelNotFound(Message message) {
        message.getChannel().sendMessage(INVALID_CHANNEL_NAME);
    }

    private Optional<String> getIdFromAlias(String alias) {
        for (KCChannel c : channels) {
            for (String channelAlias : c.aliases) {
                if (channelAlias.equals(alias)) {
                    return Optional.of(c.id);
                }
            }
        }
        return Optional.empty();
    }

    @Command(aliases = "!channels", description = "Shows all channels that can be joined.", usage = "!channel")
    public void onChannelsCommand(Message message) {
        message.getServer().ifPresent(JoinLeaveCommand::debugPrintChannels);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("KC Discord Channels");
        StringBuilder channelList = new StringBuilder();
        for (KCChannel c : channels) {
            channelList.append("* ");
            channelList.append(c.aliases.stream().collect(Collectors.joining(" | ")));
            channelList.append("\n");
        }
        embed.setDescription(channelList.toString());
        message.getChannel().sendMessage(embed);
    }

    private static void debugPrintChannels(Server server) {
        for (Role role : server.getRoles()) {
            System.out.println(role.getName() + " - " + role.getIdAsString());
        }
    }
}
