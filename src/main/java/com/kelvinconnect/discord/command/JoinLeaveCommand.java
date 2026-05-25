package com.kelvinconnect.discord.command;

import com.kelvinconnect.discord.DOMUtils;
import com.kelvinconnect.discord.DiscordUtils;
import com.kelvinconnect.discord.Parameters;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class JoinLeaveCommand implements BotCommand {
    private static final Logger logger = LogManager.getLogger(JoinLeaveCommand.class);

    private static final String INVALID_CHANNEL_NAME =
            "Invalid channel name, try '!channels' for a list of all channels.";
    private static final String CANT_LEAVE_CHANNEL = "Sorry, you can't leave this channel.";
    private static final String DEFAULT_CHANNEL_URL =
            "https://raw.githubusercontent.com/UKLooneyJr/kc-discord-bot/master/resources/channels.xml";

    private static final String KC_CHANNEL_LIST_ELEMENT = "KCChannelList";
    private static final String KC_CHANNEL_ELEMENT = "KCChannel";
    private static final String CHANNEL_ID_NODE = "ChannelId";
    private static final String ROLE_ID_NODE = "RoleId";
    private static final String ALIASES_NODE = "Aliases";
    private static final String ALIAS_NODE = "Name";

    private final Guild kcServer;
    private final String kcChannelListURL;
    private final long botOwnerId;

    private List<KCChannel> channels = new ArrayList<>();

    public JoinLeaveCommand(JDA jda, long botOwnerId) {
        Parameters p = Parameters.getInstance();
        kcChannelListURL = p.getChannelListLocation().orElse(DEFAULT_CHANNEL_URL);
        this.botOwnerId = botOwnerId;

        kcServer =
                Optional.ofNullable(jda.getGuildById(DiscordUtils.KC_SERVER_ID))
                        .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        initChannels();
    }

    @Override
    public String[] getAliases() {
        return new String[] {"!join", "!leave", "!channels", "!giveall"};
    }

    @Override
    public String getDescription() {
        return "Join or leave optional channels.";
    }

    @Override
    public String getUsage() {
        return "!join [<channel-name>] | !leave [<channel-name>] | !channels";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String command = event.getMessage().getContentRaw().trim().split("\\s+")[0].toLowerCase();
        switch (command) {
            case "!join":
                onJoinCommand(args, event);
                break;
            case "!leave":
                onLeaveCommand(args, event);
                break;
            case "!channels":
                onChannelsCommand(event);
                break;
            case "!giveall":
                onGiveAllCommand(args, event);
                break;
            default:
                break;
        }
    }

    private static void debugPrintChannels(Guild server) {
        if (logger.isInfoEnabled()) {
            for (Role role : server.getRoles()) {
                logger.info(() -> String.format("%s - %s", role.getName(), role.getId()));
            }
        }
    }

    private void initChannels() {
        try {
            channels = loadChannelsFromURL();
        } catch (Exception e) {
            logger.error(
                    () -> String.format("Error loading channel list from URL %s", kcChannelListURL),
                    e);
            if (null == channels) {
                logger.debug("Loading default channels");
                channels = getDefaultChannels();
            }
        }
    }

    private List<KCChannel> loadChannelsFromURL()
            throws IOException, SAXException, ParserConfigurationException {
        URLConnection urlConnection = new URL(kcChannelListURL).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");

        DocumentBuilder documentBuilder = DOMUtils.newDocumentBuilder();
        Document doc = documentBuilder.parse(urlConnection.getInputStream());
        Node rootNode = doc.getDocumentElement();

        return loadChannelsFromXml(rootNode);
    }

    private List<KCChannel> loadChannelsFromXml(Node rootNode) {
        if (KC_CHANNEL_LIST_ELEMENT.equals(rootNode.getNodeName())) {
            return DOMUtils.toStream(rootNode.getChildNodes())
                    .filter(Element.class::isInstance)
                    .map(Element.class::cast)
                    .filter(element -> element.getNodeName().equals(KC_CHANNEL_ELEMENT))
                    .map(this::createChannelFromNode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid XML, expected root element <%s> but got <%s>",
                            KC_CHANNEL_LIST_ELEMENT, rootNode.getNodeName()));
        }
    }

    private KCChannel createChannelFromNode(Element element) {
        try {
            long channelId =
                    Long.parseLong(
                            element.getElementsByTagName(CHANNEL_ID_NODE).item(0).getTextContent());
            long roleId =
                    Long.parseLong(
                            element.getElementsByTagName(ROLE_ID_NODE).item(0).getTextContent());
            Element aliasesElement = (Element) element.getElementsByTagName(ALIASES_NODE).item(0);
            String[] aliases =
                    DOMUtils.toStream(aliasesElement.getElementsByTagName(ALIAS_NODE))
                            .map(Node::getTextContent)
                            .toArray(String[]::new);
            return new KCChannel(channelId, roleId, aliases);
        } catch (Exception e) {
            logger.error(
                    () ->
                            String.format(
                                    "Error parsing channel from XML: %s",
                                    DOMUtils.toString(element)),
                    e);
        }

        return null;
    }

    private List<KCChannel> getDefaultChannels() {
        return Arrays.asList(
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
                new KCChannel(874359730540253204L, 874359350209151046L, "ffxiv", "ff14"));
    }

    private void onJoinCommand(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            onChannelsCommand(event);
            return;
        } else if (args.length > 1) {
            event.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE).queue();
            return;
        }

        if ("all".equals(args[0])) {
            channels.forEach(channel -> assignRole(channel, event, false));
        } else {
            getChannelFromAlias(args[0])
                    .<Runnable>map(id -> () -> assignRole(id, event, true))
                    .orElse(() -> event.getChannel().sendMessage(INVALID_CHANNEL_NAME).queue())
                    .run();
        }
    }

    private void assignRole(KCChannel channel, MessageReceivedEvent event, boolean showWelcomeMessage) {
        Member member = event.getMember();
        if (member == null) return;

        List<Role> roles = member.getRoles();

        if (!roles.contains(channel.role)) {
            if (showWelcomeMessage) {
                kcServer.addRoleToMember(member, channel.role)
                        .queue(
                                v ->
                                        channel.channel
                                                .sendMessage(
                                                        "Welcome "
                                                                + DiscordUtils.getAuthorShortUserName(
                                                                        event.getMessage())
                                                                + "!")
                                                .queue());
            } else {
                kcServer.addRoleToMember(member, channel.role).queue();
            }
        }
    }

    private void onLeaveCommand(String[] args, MessageReceivedEvent event) {
        switch (args.length) {
            case 0:
                getCurrentChannel(event)
                        .<Runnable>map(id -> () -> unassignRole(id, event))
                        .orElse(
                                () ->
                                        event.getChannel()
                                                .sendMessage(CANT_LEAVE_CHANNEL)
                                                .queue())
                        .run();
                break;
            case 1:
                getChannelFromAlias(args[0])
                        .<Runnable>map(id -> () -> unassignRole(id, event))
                        .orElse(
                                () ->
                                        event.getChannel()
                                                .sendMessage(INVALID_CHANNEL_NAME)
                                                .queue())
                        .run();
                break;
            default:
                event.getChannel().sendMessage(DiscordUtils.INVALID_ARGUMENTS_MESSAGE).queue();
                break;
        }
    }

    private void unassignRole(KCChannel channel, MessageReceivedEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        List<Role> roles = member.getRoles();

        if (roles.contains(channel.role)) {
            kcServer.removeRoleFromMember(member, channel.role)
                    .queue(
                            v ->
                                    channel.channel
                                            .sendMessage(
                                                    "Bye "
                                                            + DiscordUtils.getAuthorShortUserName(
                                                                    event.getMessage()))
                                            .queue());
        }
    }

    private Optional<KCChannel> getCurrentChannel(MessageReceivedEvent event) {
        return channels.stream()
                .filter(c -> c.channel.getIdLong() == event.getChannel().getIdLong())
                .findFirst();
    }

    private Optional<KCChannel> getChannelFromAlias(String alias) {
        return channels.stream().filter(c -> c.aliases.contains(alias.toLowerCase())).findFirst();
    }

    private void onChannelsCommand(MessageReceivedEvent event) {
        initChannels();
        debugPrintChannels(kcServer);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("KC Discord Channels");
        StringBuilder description = new StringBuilder();
        description.append(
                "To join a channel use the command '!join <channel-name>' in any channel, or in a ");
        description.append("direct message to KCBot.\n\n");
        description.append(
                "To leave a channel use the command '!leave <channel-name>' in any channel, or in a ");
        description.append(
                "direct message to KCBot; or simply use the command '!leave' in the channel you wish to leave.\n\n");
        description.append("To join all available channels, use the command '!join all'.\n\n");
        description.append("Channel list:\n");
        for (KCChannel c : channels) {
            description.append("• ");
            description.append(String.join(" | ", c.aliases));
            description.append("\n");
        }
        embed.setDescription(description.toString());
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private void onGiveAllCommand(String[] args, MessageReceivedEvent event) {
        Member authorMember = event.getMember();
        if (authorMember == null) return;

        if (authorMember.getIdLong() != botOwnerId) {
            event.getChannel().sendMessage("Only the party master can initiate party time.").queue();
            return;
        }

        if (args.length == 0) {
            event.getChannel().sendMessage("Must provide a channel name.").queue();
            return;
        }

        Optional<KCChannel> channelOptional = getChannelFromAlias(args[0]);
        if (!channelOptional.isPresent()) {
            event.getChannel()
                    .sendMessage("Unable to find channel for alias " + args[0] + ".")
                    .queue();
            return;
        }

        KCChannel channel = channelOptional.get();

        for (Member member : kcServer.getMembers()) {
            List<Role> roles = member.getRoles();
            if (!roles.contains(channel.role)) {
                List<Role> updatedRoles = new ArrayList<>(roles);
                updatedRoles.add(channel.role);
                kcServer.modifyMemberRoles(member, updatedRoles, Collections.emptyList()).queue();
            }
        }
    }

    private class KCChannel {
        final List<String> aliases;
        final Role role;
        final GuildMessageChannel channel;

        KCChannel(long channelId, long roleId, String... aliases) {
            channel =
                    Optional.ofNullable(
                                    kcServer.getChannelById(GuildMessageChannel.class, channelId))
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Failed to find channel "
                                                            + channelId
                                                            + ". alias="
                                                            + aliases[0]));
            role =
                    Optional.ofNullable(kcServer.getRoleById(roleId))
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Failed to find role "
                                                            + roleId
                                                            + ". alias="
                                                            + aliases[0]));
            this.aliases = Arrays.asList(aliases);
        }
    }
}
