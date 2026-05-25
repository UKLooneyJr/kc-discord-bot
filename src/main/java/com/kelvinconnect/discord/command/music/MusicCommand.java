package com.kelvinconnect.discord.command.music;

import com.kelvinconnect.discord.DiscordUtils;
import com.kelvinconnect.discord.command.BotCommand;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MusicCommand implements BotCommand {

    private static final String PLAY_INVALID_ARGS =
            "Invalid arguments, please enter URL of YouTube video or playlist";
    private static final int MAX_PLAYLIST_MESSAGE_LENGTH = 2000;

    private static final long MUSIC_VOICE_CHANNEL_ID = 902665967258189906L;
    private static final long PLAYLIST_TEXT_CHANNEL_ID = 903337539077832736L;

    private final VoiceChannel voiceChannel;
    private final MusicPlayer player;

    public MusicCommand(JDA jda) {
        Guild server =
                java.util.Optional.ofNullable(jda.getGuildById(DiscordUtils.KC_SERVER_ID))
                        .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        voiceChannel =
                java.util.Optional.ofNullable(
                                server.getChannelById(VoiceChannel.class, MUSIC_VOICE_CHANNEL_ID))
                        .orElseThrow(
                                () -> new RuntimeException("Failed to find music voice channel."));
        TextChannel textChannel =
                java.util.Optional.ofNullable(
                                server.getChannelById(TextChannel.class, PLAYLIST_TEXT_CHANNEL_ID))
                        .orElseThrow(
                                () -> new RuntimeException("Failed to find music text channel."));
        player = new MusicPlayer(server.getAudioManager(), textChannel);
    }

    @Override
    public String[] getAliases() {
        return new String[] {"!play", "!playing", "!playlist", "!skip"};
    }

    @Override
    public String getDescription() {
        return "Music player commands.";
    }

    @Override
    public String getUsage() {
        return "!play [<url>] | !playing | !skip";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String command = event.getMessage().getContentRaw().trim().split("\\s+")[0].toLowerCase();
        switch (command) {
            case "!play":
                onPlay(args, event);
                break;
            case "!playing":
            case "!playlist":
                onNowPlaying(event);
                break;
            case "!skip":
                onSkip(args, event);
                break;
            default:
                break;
        }
    }

    private void onPlay(String[] args, MessageReceivedEvent event) {
        if (args.length != 1) {
            event.getChannel().sendMessage(PLAY_INVALID_ARGS).queue();
            return;
        }

        String url = args[0];

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            event.getChannel().sendMessage(PLAY_INVALID_ARGS).queue();
            return;
        }

        int remaining = player.getRemainingRequestCount();
        player.addRequest(url, event.getAuthor());

        if (!player.isConnected()) {
            player.connect(voiceChannel);
            player.next();
        }

        StringBuilder reply = new StringBuilder();
        reply.append("Thanks for the request");
        if (url.contains("list=")) {
            reply.append("s");
        }
        reply.append(" ");
        reply.append(DiscordUtils.getAuthorShortUserName(event.getMessage()));
        reply.append(", ");

        if (remaining == 0) {
            reply.append("playing it now!");
        } else if (remaining == 1) {
            reply.append("coming up next!");
        } else {
            reply.append("it'll be on soon");
        }

        event.getChannel().sendMessage(reply.toString()).queue();
    }

    private void onNowPlaying(MessageReceivedEvent event) {
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
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    private void onSkip(String[] args, MessageReceivedEvent event) {
        if (PLAYLIST_TEXT_CHANNEL_ID != event.getChannel().getIdLong()) {
            return;
        }

        boolean skipAll = args.length > 0 && "all".equalsIgnoreCase(args[0]);

        if (skipAll) {
            event.getChannel().sendMessage("Clearing playlist").queue();
            player.clear();
            player.disconnect();
        } else {
            player.getCurrentRequest()
                    .ifPresent(
                            track ->
                                    event.getChannel()
                                            .sendMessage("Skipped track " + track)
                                            .queue());
            player.next();
        }
    }
}
