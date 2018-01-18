package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.javacord.DiscordApi;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.channels.ServerChannel;
import de.btobastian.javacord.entities.channels.TextChannel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JoinLeaveCommand implements CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JoinLeaveCommand.class);

    private final static String INVALID_CHANNEL_NAME = "Invalid channel name, try '!channels' for a list of all channels.";
    private final static String CANT_LEAVE_CHANNEL = "Sorry, you can't leave this channel.";

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

    // keep references to pubchat and music channels as we will use them elsewhere
    // (maybe want to group these under party channels? will do that if we end up having more similar channels)
    private KCChannel pubchatChannel;
    private KCChannel musicChannel;

    public JoinLeaveCommand(DiscordApi api) {
        kcServer = api.getServerById(239013363387072514L)
                .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        initChannels();
    }

    private void initChannels() {
        pubchatChannel = new KCChannel(276318041443270657L, 403663671148150797L, "pubchat");
        musicChannel = new KCChannel(380822484855029760L, 403663738177191938L, "music");
        channels = Arrays.asList(pubchatChannel, musicChannel,
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

    @Command(aliases = "!join", description = "Joins a channel.", usage = "!join [<channel-name>]")
    public void onJoinCommand(String args[], DiscordApi api, Message message) {
        if (args.length == 0) {
            // Show the join help command I suppose
            onChannelsCommand(message);
            return;
        } else if (args.length > 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
        getChannelFromAlias(args[0]).<Runnable>map(id -> () -> assignRole(id, message))
                .orElse(() -> channelNotFound(message))
                .run();
    }

    private void assignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        boolean dirty = false;

        Collection<Role> roles = user.getRoles(kcServer);
        if (!roles.contains(channel.role)) {
            dirty = true;
            roles.add(channel.role);
        }

        if (dirty) {
            ((TextChannel)channel.channel).sendMessage("Welcome " + DiscordUtils.getAuthorShortUserName(message) + "!");
            kcServer.updateRoles(user, roles);
        }
    }

    @Command(aliases = "!leave", description = "Leaves a named channel. Leaves the current channel if none specified.",
            usage = "!leave [<channel-name>]")
    public void onLeaveCommand(String args[], DiscordApi api, Message message) {
        if (args.length == 0) {
            getCurrentChannel(message).<Runnable>map(id -> () -> unassignRole(id, message))
                    .orElse(() -> cantLeaveChannel(message))
                    .run();
        } else if (args.length == 1) {
            getChannelFromAlias(args[0]).<Runnable>map(id -> () -> unassignRole(id, message))
                    .orElse(() -> channelNotFound(message))
                    .run();
        } else {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }
    }

    private void unassignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(kcServer);
        boolean dirty = roles.remove(channel.role);

        if (dirty) {
            ((TextChannel)channel.channel).sendMessage("Bye " + DiscordUtils.getAuthorShortUserName(message));
            kcServer.updateRoles(user, roles);
        }
    }

    private void channelNotFound(Message message) {
        message.getChannel().sendMessage(INVALID_CHANNEL_NAME);
    }

    private void cantLeaveChannel(Message message) {
        message.getChannel().sendMessage(CANT_LEAVE_CHANNEL);
    }

    private Optional<KCChannel> getCurrentChannel(Message message) {
        long currentChannelId = message.getChannel().getId();
        for (KCChannel c : channels) {
            if (c.channel.getId() == currentChannelId) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    private Optional<KCChannel> getChannelFromAlias(String alias) {
        String lcAlias = alias.toLowerCase();
        for (KCChannel c : channels) {
            for (String channelAlias : c.aliases) {
                if (channelAlias.equals(lcAlias)) {
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

    /**
     * Adds all users on the server to the pubchat and music channels. Can only be ran by the bot owner (Adamin)
     */
    @Command(aliases = "!partytime")
    public void onPartyTimeCommand(Message message) {
        User authorUser = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));
        if (!authorUser.isBotOwner()) {
            message.getChannel().sendMessage("Only the party master can initiate party time.");
            return;
        }
        Role everyone = authorUser.getRoles(kcServer).stream()
                .filter(Role::isEveryoneRole)
                .collect(Collectors.toList())
                .get(0);
        for (User user : everyone.getUsers()) {
            // keep track of whether the roles are dirty to prevent hitting rate limits
            boolean dirty = false;
            Collection<Role> roles = user.getRoles(kcServer);
            if (!roles.contains(pubchatChannel.role)) {
                roles.add(pubchatChannel.role);
                dirty = true;
            }
            if (!roles.contains(musicChannel.role)) {
                roles.add(musicChannel.role);
                dirty = true;
            }
            if (dirty) {
                kcServer.updateRoles(user, roles);
            }
        }
    }

    private static void debugPrintChannels(Server server) {
        if (logger.isInfoEnabled()) {
            for (Role role : server.getRoles()) {
                logger.info(role.getName() + " - " + role.getIdAsString());
            }
        }
    }
}
