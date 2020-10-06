package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JoinLeaveCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(JoinLeaveCommand.class);

    private final static String INVALID_CHANNEL_NAME = "Invalid channel name, try '!channels' for a list of all channels.";
    private final static String CANT_LEAVE_CHANNEL = "Sorry, you can't leave this channel.";
    private final Server kcServer;
    private List<KCChannel> channels;
    // keep references to pubchat and music channels as we will use them elsewhere
    // (maybe want to group these under party channels? will do that if we end up having more similar channels)
    private KCChannel pubchatChannel;

    public JoinLeaveCommand(DiscordApi api) {
        kcServer = api.getServerById(239013363387072514L)
                .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        initChannels();
    }

    private static void debugPrintChannels(Server server) {
        if (logger.isInfoEnabled()) {
            for (Role role : server.getRoles()) {
                logger.info(role.getName() + " - " + role.getIdAsString());
            }
        }
    }

    private void initChannels() {
        pubchatChannel = new KCChannel(276318041443270657L, 403663671148150797L, "pubchat");
        channels = Arrays.asList(pubchatChannel,
                new KCChannel(689418031151054944L ,763085327270543381L, "corona", "covid"),
                new KCChannel(550614902532997131L, 763085269435678791L, "spamdo"),
                new KCChannel(421223895513956352L, 421225268057735168L, "music"),
                new KCChannel(694117175392469032L, 763083545451167774L, "film", "tv"),
                new KCChannel(408933031064371200L, 763083371193696267L, "roll", "role"),
                new KCChannel(425314799455436801L, 425314649458737163L, "anime", "weeb"),
                new KCChannel(763082945979744278L, 763082196985970709L, "pets"),
                new KCChannel(763091730605408307L, 763091623386021888L, "programming"),
                new KCChannel(763091960541478912L, 763091847559118868L, "botdev", "kcbot"),
                new KCChannel(763083029794390017L, 763082257429692458L, "tech"),
                new KCChannel(763083100594110524L, 763082324748533790L, "sport"),
                new KCChannel(763086481560305704L, 763086365835395123L, "politics"),
                new KCChannel(421623339145232404L, 421623272434565130L, "games"),
                new KCChannel(753587144991178792L, 753586719999393872L, "lol"),
                new KCChannel(763082784784515092L, 763082509952876546L, "wow"),
                new KCChannel(763089542152323112L, 763089285632884756L, "tetris"));
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
        getChannelFromAlias(args[0]).<Runnable> map(id -> () -> assignRole(id, message))
                .orElse(() -> channelNotFound(message)).run();
    }

    private void assignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(kcServer);

        if (!roles.contains(channel.role)) {
            kcServer.addRoleToUser(user, channel.role).thenRun(() -> ((TextChannel) channel.channel)
                    .sendMessage("Welcome " + DiscordUtils.getAuthorShortUserName(message) + "!"));
        }
    }

    @Command(aliases = "!leave", description = "Leaves a named channel. Leaves the current channel if none specified.", usage = "!leave [<channel-name>]")
    public void onLeaveCommand(String args[], DiscordApi api, Message message) {
        switch (args.length) {
        case 0:
            getCurrentChannel(message).<Runnable> map(id -> () -> unassignRole(id, message))
                    .orElse(() -> cantLeaveChannel(message)).run();
            break;
        case 1:
            getChannelFromAlias(args[0]).<Runnable> map(id -> () -> unassignRole(id, message))
                    .orElse(() -> channelNotFound(message)).run();
            break;
        default:
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
            break;
        }
    }

    private void unassignRole(KCChannel channel, Message message) {
        User user = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));

        Collection<Role> roles = user.getRoles(kcServer);

        if (roles.contains(channel.role)) {
            kcServer.removeRoleFromUser(user, channel.role).thenRun(() -> ((TextChannel) channel.channel)
                    .sendMessage("Bye " + DiscordUtils.getAuthorShortUserName(message)));
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

    @Command(aliases = "!channels", description = "Shows all channels that can be joined.", usage = "!channels")
    public void onChannelsCommand(Message message) {
        message.getServer().ifPresent(JoinLeaveCommand::debugPrintChannels);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("KC Discord Channels");
        StringBuilder channelList = new StringBuilder();
        for (KCChannel c : channels) {
            channelList.append("• ");
            channelList.append(String.join(" | ", c.aliases));
            channelList.append("\n");
        }
        embed.setDescription(channelList.toString());
        message.getChannel().sendMessage(embed);
    }

    /**
     * Adds all users on the server to the pubchat channel. Can only be ran by the bot owner (Adamin)
     */
    @Command(aliases = "!partytime", showInHelpPage = false)
    public void onPartyTimeCommand(Message message) {
        User authorUser = message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));
        if (!authorUser.isBotOwner()) {
            message.getChannel().sendMessage("Only the party master can initiate party time.");
            return;
        }
        Role everyone = authorUser.getRoles(kcServer).stream().filter(Role::isEveryoneRole).collect(Collectors.toList())
                .get(0);
        for (User user : everyone.getUsers()) {
            // keep track of whether the roles are dirty to prevent hitting rate limits
            boolean dirty = false;
            Collection<Role> roles = user.getRoles(kcServer);
            if (!roles.contains(pubchatChannel.role)) {
                roles.add(pubchatChannel.role);
                dirty = true;
            }
            if (dirty) {
                kcServer.updateRoles(user, roles);
            }
        }
    }

    private class KCChannel {
        final List<String> aliases;
        final Role role;
        final ServerChannel channel;

        KCChannel(long channelId, long roleId, String... aliases) {
            channel = kcServer.getChannelById(channelId).orElseThrow(
                    () -> new RuntimeException("Failed to find channel " + channelId + ". alias=" + aliases[0]));
            role = kcServer.getRoleById(roleId)
                    .orElseThrow(() -> new RuntimeException("Failed to find role " + roleId + ". alias=" + aliases[0]));
            this.aliases = Arrays.asList(aliases);
        }
    }
}
