package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.channels.ServerChannel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.*;
import java.util.stream.Collectors;

public class JoinLeaveCommand implements CommandExecutor {

    private final static String INVALID_CHANNEL_NAME = "Invalid channel name, try '!channels' for a list of all channels.";

    private class KCChannel {
        List<String> aliases;
        Role role;
        ServerChannel channel;

        KCChannel(long channelId, long roleId, String... aliases) {
            channel = kcServer.getChannelById(channelId).orElseThrow(() ->
                    new RuntimeException("Failed to find channel " + channelId + ". alias=" + aliases[0]));
            role = kcServer.getRoleById(roleId).orElseThrow(() ->
                    new RuntimeException("Failed to find role " + roleId + ". alias=" + aliases[0]));
            this.aliases = Arrays.asList(aliases);
        }
    }

    private Server kcServer;
    private List<KCChannel> channels;

    public JoinLeaveCommand(DiscordApi api) {
        kcServer = api.getServerById(239013363387072514L)
                .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        initChannels();
    }

    private void initChannels() {
        channels = Arrays.asList(
                new KCChannel(365038764738871297L, 365039527607140353L, "niche"),
                new KCChannel(365040110867054592L, 365039532829179904L, "pnc"),
                new KCChannel(365040137974972428L, 365039576521244672L, "storm", "neilstorm"),
                new KCChannel(365040187689926656L, 365039600210804738L, "nspis"),
                new KCChannel(365040239867330572L, 365039626362290179L, "compass"),
                new KCChannel(365040284398256139L, 365039651721052161L, "qas"),
                new KCChannel(365040322176090126L, 365039660184895489L, "singlepoint"),
                new KCChannel(365040358179995658L, 365039678212276224L, "connect"),
                new KCChannel(365040398416084992L, 365039717017976835L, "unifi"),
                new KCChannel(365040428598165516L, 365039737402294274L, "crash"),
                new KCChannel(365040459027841024L, 365039767152492545L, "compact"),
                new KCChannel(365040501339979776L, 365039790028226563L, "socrates")
        );
    }

    @Command(aliases = "!join", description = "Joins a channel.", usage = "!join <channel-name>")
    public void onJoinCommand(String args[], DiscordApi api, Message message) {
        if (args.length != 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
        getChannelFromAlias(args[0]).<Runnable>map(id -> () -> assignRole(id, message))
                .orElse(() -> channelNotFound(message))
                .run();
    }

    private void assignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(kcServer);
        if (!roles.contains(channel.role)) {
            roles.add(channel.role);
        }

        kcServer.updateRoles(user, roles);
    }

    @Command(aliases = "!leave", description = "Leaves a channel.", usage = "!leave [<channel-name>]")
    public void onLeaveCommand(String args[], DiscordApi api, Message message) {
        if (args.length > 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
        getChannelFromAlias(args[0]).<Runnable>map(id -> () -> unassignRole(id, message))
                .orElse(() -> channelNotFound(message))
                .run();
    }

    private void unassignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(kcServer);
        roles.remove(channel.role);

        kcServer.updateRoles(user, roles);
    }

    private void channelNotFound(Message message) {
        message.getChannel().sendMessage(INVALID_CHANNEL_NAME);
    }

    private Optional<KCChannel> getChannelFromAlias(String alias) {
        for (KCChannel c : channels) {
            for (String channelAlias : c.aliases) {
                if (channelAlias.equals(alias)) {
                    return Optional.of(c);
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
