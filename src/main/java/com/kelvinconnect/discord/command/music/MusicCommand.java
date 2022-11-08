package com.kelvinconnect.discord.command.music;

import com.kelvinconnect.discord.utils.DiscordUtils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

public class MusicCommand implements CommandExecutor {

    private static final String PLAY_INVALID_ARGS =
            "Invalid arguments, please enter URL of YouTube video or playlist";
    private static final int MAX_PLAYLIST_MESSAGE_LENGTH = 2000;

    private static final long MUSIC_VOICE_CHANNEL_ID = 902665967258189906L;
    private static final long PLAYLIST_TEXT_CHANNEL_ID = 903337539077832736L;

    private final ServerVoiceChannel voiceChannel;
    private final MusicPlayer player;

    public MusicCommand(DiscordApi api) {
        Server server =
                api.getServerById(DiscordUtils.KC_SERVER_ID)
                        .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        voiceChannel =
                server.getVoiceChannelById(MUSIC_VOICE_CHANNEL_ID)
                        .orElseThrow(
                                () -> new RuntimeException("Failed to find music voice channel."));
        ServerTextChannel textChannel =
                server.getTextChannelById(PLAYLIST_TEXT_CHANNEL_ID)
                        .orElseThrow(
                                () -> new RuntimeException("Failed to find music text channel."));
        player = new MusicPlayer(api, textChannel);
    }

    @Command(
            aliases = "!play",
            description = "Requests the track at the given YouTube URL",
            usage = "!play [<url>]")
    public String onPlay(String[] args, DiscordApi api, Message message) {
        if (args.length != 1) {
            return PLAY_INVALID_ARGS;
        }

        String url = args[0];

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return PLAY_INVALID_ARGS;
        }

        int remaining = player.getRemainingRequestCount();

        player.addRequest(args[0], message.getAuthor());

        if (!player.isConnected()) {
            voiceChannel
                    .connect()
                    .thenAccept(
                            connection -> {
                                player.setAudioConnection(connection);
                                player.next();
                            });
        }

        StringBuilder reply = new StringBuilder();
        reply.append("Thanks for the request");
        if (url.contains("list=")) {
            reply.append("s");
        }
        reply.append(" ");
        reply.append(DiscordUtils.getAuthorShortUserName(message));
        reply.append(", ");

        if (remaining == 0) {
            reply.append("playing it now!");
        } else if (remaining == 1) {
            reply.append("coming up next!");
        } else {
            reply.append("it'll be on soon");
        }

        return reply.toString();
    }

    @Command(
            aliases = {"!playing", "!playlist"},
            description = "Shows all upcoming tracks in the playlist",
            usage = "!playing")
    public void onNowPlaying(Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(player.getRemainingRequestCount() == 0 ? Color.RED : Color.GREEN);

        StringBuilder description = new StringBuilder();

        description.append("*Now Playing*\n\n - ");
        description.append(player.getCurrentRequest().orElse("Nothing"));

        List<String> nextRequests = player.getNextRequests();
        if (!nextRequests.isEmpty()) {
            description.append("\n\n*Coming next*\n");

            for (int i = 0; i < nextRequests.size(); ++i) {
                description.append("\n");
                String request = nextRequests.get(i);
                if (description.length() + request.length() > MAX_PLAYLIST_MESSAGE_LENGTH) {
                    description.append(" - and ");
                    description.append(nextRequests.size() - i);
                    description.append(" more!");
                    break;
                }
                description.append(" ");
                description.append(i + 1);
                description.append(". ");
                description.append(request);
            }
        }

        eb.setDescription(description.toString());

        message.getChannel().sendMessage(eb);
    }

    @Command(
            aliases = "!skip",
            description = "Skips the current track",
            usage = "!skip",
            showInHelpPage = false)
    public void onSkip(String[] args, Message message) {
        if (PLAYLIST_TEXT_CHANNEL_ID != message.getChannel().getId()) {
            return;
        }

        boolean skipAll = args.length > 0 && "all".equalsIgnoreCase(args[0]);

        if (skipAll) {
            message.getChannel().sendMessage("Clearing playlist");
            player.clear();
            player.disconnect();
        } else {
            player.getCurrentRequest()
                    .ifPresent(track -> message.getChannel().sendMessage("Skipped track " + track));
            player.next();
        }
    }
}
