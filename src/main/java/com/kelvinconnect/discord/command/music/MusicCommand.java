package com.kelvinconnect.discord.command.music;

import com.kelvinconnect.discord.DiscordUtils;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

public class MusicCommand implements CommandExecutor {

    private static final long MUSIC_VOICE_CHANNEL_ID = 902665967258189906L;
    private static final long MUSIC_TEXT_CHANNEL_ID = 902700344683921499L;

    private final ServerVoiceChannel voiceChannel;
    private final MusicPlayer player;

    public MusicCommand(DiscordApi api) {
        Server server = api.getServerById(DiscordUtils.KC_SERVER_ID)
                .orElseThrow(() -> new RuntimeException("Failed to find KC server."));
        voiceChannel = server.getVoiceChannelById(MUSIC_VOICE_CHANNEL_ID)
                .orElseThrow(() -> new RuntimeException("Failed to find music voice channel."));
        ServerTextChannel textChannel = server.getTextChannelById(MUSIC_TEXT_CHANNEL_ID)
                .orElseThrow(() -> new RuntimeException("Failed to find music text channel."));
        player = new MusicPlayer(api, textChannel);
    }

    @Command(aliases = "!play", description = "Plays a song at the given YouTube URL", usage = "!play https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    public void onPlay(String[] args, DiscordApi api, Message message) {
        if (MUSIC_TEXT_CHANNEL_ID != message.getChannel().getId()) {
            return;
        }

        if (args.length != 1) {
            message.getChannel().sendMessage("Invalid arguments");
            return;
        }

        player.addSong(args[0]);

        if (!player.isConnected()) {
            voiceChannel.connect().thenAccept(connection -> {
                player.setAudioConnection(connection);
                player.next();
            });
        }
    }

    @Command(aliases = "!skip", description = "Skips the current song", usage = "!skip", showInHelpPage = false)
    public void onSkip(Message message) {
        if (MUSIC_TEXT_CHANNEL_ID != message.getChannel().getId()) {
            return;
        }

        player.getCurrentTrack().ifPresent(track -> message.getChannel().sendMessage("Skipped track " + track));

        player.next();
    }
}
