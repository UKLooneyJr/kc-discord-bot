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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class JoinLeaveCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(JoinLeaveCommand.class);

    private static final String INVALID_CHANNEL_NAME = "Invalid channel name, try '!channels' for a list of all channels.";
    private static final String CANT_LEAVE_CHANNEL = "Sorry, you can't leave this channel.";
    private static final long KC_SERVER_ID = 239013363387072514L;

    private static final String KC_CHANNEL_LIST_URL = "https://raw.githubusercontent.com/UKLooneyJr/kc-discord-bot/master/resources/channels.xml";
    private static final String KC_CHANNEL_LIST_ELEMENT = "KCChannelList";
    private static final String KC_CHANNEL_ELEMENT = "KCChannel";
    private static final String CHANNEL_ID_NODE = "ChannelId";
    private static final String ROLE_ID_NODE = "RoleId";
    private static final String ALIASES_NODE = "Aliases";
    private static final String ALIAS_NODE = "Name";

    private final Server kcServer;
    private List<KCChannel> channels = new ArrayList<>();

    public JoinLeaveCommand(DiscordApi api) {
        kcServer = api.getServerById(KC_SERVER_ID)
                .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        initChannels();
    }

    private static void debugPrintChannels(Server server) {
        if (logger.isInfoEnabled()) {
            for (Role role : server.getRoles()) {
                logger.info(() -> String.format("%s - %s", role.getName(), role.getIdAsString()));
            }
        }
    }

    private void initChannels() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            URLConnection urlConnection = new URL(KC_CHANNEL_LIST_URL).openConnection();
            urlConnection.addRequestProperty("Accept", "application/xml");
            Document channelListDocument = documentBuilder.parse(urlConnection.getInputStream());
            String root = channelListDocument.getDocumentElement().getNodeName();
            if (root.equals(KC_CHANNEL_LIST_ELEMENT)) {
                Node rootNode = channelListDocument.getDocumentElement();
                loadFromXml(rootNode);
            } else {
                loadFromDefaults();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadFromDefaults();
        }

    }

    private void loadFromXml(Node rootNode) {
        channels = new ArrayList<>();
        //NodeList not iterable for some reason
        for (int i = 0; i < rootNode.getChildNodes().getLength(); i++) {
            Node thisNode = rootNode.getChildNodes().item(i);
            if (thisNode instanceof Element) {
                Element channelElement = (Element) thisNode;
                if (channelElement.getNodeName().equals(KC_CHANNEL_ELEMENT)) {
                    Element channelIdElement = (Element) channelElement.getElementsByTagName(CHANNEL_ID_NODE).item(0);
                    long channelId = Long.parseLong(channelIdElement.getTextContent());
                    Element roleIdElement = (Element) channelElement.getElementsByTagName(ROLE_ID_NODE).item(0);
                    long roleId = Long.parseLong(roleIdElement.getTextContent());
                    Element aliasesElement = (Element) channelElement.getElementsByTagName(ALIASES_NODE).item(0);
                    NodeList aliasList = aliasesElement.getElementsByTagName(ALIAS_NODE);
                    String[] aliases = new String[aliasList.getLength()];
                    for (int j = 0; j < aliasList.getLength(); j++) {
                        aliases[j] = (aliasList.item(j).getTextContent());
                    }
                    KCChannel newChannel = new KCChannel(channelId, roleId, aliases);
                    channels.add(newChannel);
                }
            }
        }
    }

    private void loadFromDefaults() {
        if (channels.isEmpty()) {
            channels.addAll(Arrays.asList(
                    new KCChannel(276318041443270657L, 763082445524041761L, "pubchat"),
                    new KCChannel(689418031151054944L, 763085327270543381L, "corona", "covid"),
                    new KCChannel(550614902532997131L, 763085269435678791L, "spamdo"),
                    new KCChannel(421223895513956352L, 421225268057735168L, "music"),
                    new KCChannel(694117175392469032L, 763083545451167774L, "film", "tv"),
                    new KCChannel(408933031064371200L, 763083371193696267L, "roll", "role"),
                    new KCChannel(425314799455436801L, 425314649458737163L, "anime", "weeb"),
                    new KCChannel(763082945979744278L, 763082196985970709L, "pets"),
                    new KCChannel(770330765133086751L, 770330666411360299L, "food"),
                    new KCChannel(763091730605408307L, 763091623386021888L, "programming"),
                    new KCChannel(763091960541478912L, 763091847559118868L, "botdev", "kcbot"),
                    new KCChannel(763083029794390017L, 763082257429692458L, "tech"),
                    new KCChannel(763083100594110524L, 763082324748533790L, "sport"),
                    new KCChannel(763086481560305704L, 763086365835395123L, "politics"),
                    new KCChannel(421623339145232404L, 421623272434565130L, "games"),
                    new KCChannel(763089542152323112L, 763089285632884756L, "tetris"),
                    new KCChannel(753587144991178792L, 753586719999393872L, "lol"),
                    new KCChannel(763082784784515092L, 763082509952876546L, "wow"),
                    new KCChannel(874359730540253204L, 874359350209151046L, "ffxiv", "ff14"))
            );
        }
    }


    @Command(aliases = "!join", description = "Joins a channel.", usage = "!join [<channel-name>]")
    public void onJoinCommand(String[] args, DiscordApi api, Message message) {
        if (args.length == 0) {
            // Show the join help command I suppose
            onChannelsCommand(message);
            return;
        } else if (args.length > 1) {
            message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
        }

        if ("all".equals(args[0])) {
            channels.forEach(channel -> assignRole(channel, message, false));
        } else {
            getChannelFromAlias(args[0]).<Runnable>map(id -> () -> assignRole(id, message, true))
                    .orElse(() -> channelNotFound(message)).run();
        }

    }

    private void assignRole(KCChannel channel, Message message, boolean showWelcomeMessage) {
        User user = getMessageAuthor(message);

        Collection<Role> roles = user.getRoles(kcServer);

        if (!roles.contains(channel.role)) {
            kcServer.addRoleToUser(user, channel.role).thenRun(() -> {
                if (showWelcomeMessage) {
                    ((TextChannel) channel.channel)
                            .sendMessage("Welcome " + DiscordUtils.getAuthorShortUserName(message) + "!");
                }
            });
        }
    }

    private User getMessageAuthor(Message message) {
        return message.getUserAuthor().orElseThrow(() -> new RuntimeException("Failed to get User"));
    }

    @Command(aliases = "!leave", description = "Leaves a named channel. Leaves the current channel if none specified.", usage = "!leave [<channel-name>]")
    public void onLeaveCommand(String[] args, DiscordApi api, Message message) {
        switch (args.length) {
            case 0:
                getCurrentChannel(message).<Runnable>map(id -> () -> unassignRole(id, message))
                        .orElse(() -> cantLeaveChannel(message)).run();
                break;
            case 1:
                getChannelFromAlias(args[0]).<Runnable>map(id -> () -> unassignRole(id, message))
                        .orElse(() -> channelNotFound(message)).run();
                break;
            default:
                message.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE);
                break;
        }
    }

    private void unassignRole(KCChannel channel, Message message) {
        User user = getMessageAuthor(message);

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
        initChannels();
        message.getServer().ifPresent(JoinLeaveCommand::debugPrintChannels);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("KC Discord Channels");
        StringBuilder description = new StringBuilder();
        description.append("To join a channel use the command '!join <channel-name>' in any channel, or in a ");
        description.append("direct message to KCBot.\n\n");
        description.append("To leave a channel use the command '!leave <channel-name>' in any channel, or in a ");
        description.append("direct message to KCBot; or simply use the command '!leave' in the channel you wish to leave.\n\n");
        description.append("To join all available channels, use the command '!join all'.\n\n");
        description.append("Channel list:\n");
        for (KCChannel c : channels) {
            description.append("• ");
            description.append(String.join(" | ", c.aliases));
            description.append("\n");
        }
        embed.setDescription(description.toString());
        message.getChannel().sendMessage(embed);
    }

    /**
     * Adds all users on the server to the specified channel. Can only be ran by the bot owner (Adamin)
     */
    @Command(aliases = "!giveall", showInHelpPage = false)
    public void onGiveAllCommand(String[] args, Message message) {
        User authorUser = getMessageAuthor(message);
        if (!authorUser.isBotOwner()) {
            message.getChannel().sendMessage("Only the party master can initiate party time.");
            return;
        }

        if (args.length == 0) {
            message.getChannel().sendMessage("Must provide a channel name.");
            return;
        }

        Optional<KCChannel> channelOptional = getChannelFromAlias(args[0]);
        if (!channelOptional.isPresent()) {
            message.getChannel().sendMessage("Unable to find channel for alias " + args[0] + ".");
            return;
        }

        KCChannel channel = channelOptional.get();

        Role everyone = authorUser.getRoles(kcServer).stream().filter(Role::isEveryoneRole).collect(Collectors.toList())
                .get(0);
        for (User user : everyone.getUsers()) {
            // keep track of whether the roles are dirty to prevent hitting rate limits
            boolean dirty = false;
            Collection<Role> roles = new ArrayList<>(user.getRoles(kcServer));
            if (!roles.contains(channel.role)) {
                roles.add(channel.role);
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
