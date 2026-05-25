package com.kelvinconnect.discord.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MusicPlayer {
    private static final Logger logger = LogManager.getLogger(MusicPlayer.class);
    private static final boolean SEND_PLAYLIST_UPDATE_MESSAGES = false;

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final LavaplayerAudioSendHandler sendHandler;
    private final Queue<TrackRequest> requestQueue;
    private final TextChannel textChannel;
    private final AudioManager audioManager;
    private TrackRequest currentTrackRequest;

    public MusicPlayer(AudioManager audioManager, TextChannel tc) {
        this.audioManager = audioManager;
        this.textChannel = tc;
        requestQueue = new LinkedBlockingQueue<>();

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        player = playerManager.createPlayer();
        sendHandler = new LavaplayerAudioSendHandler(player);

        player.addListener(
                new AudioEventHandler() {
                    @Override
                    protected void onTrackStart(TrackStartEvent event) {
                        logger.info("Now playing {}", event.track.getInfo().title);
                    }

                    @Override
                    protected void onTrackEnd(TrackEndEvent event) {
                        logger.info("Finished playing {}", event.track.getInfo().title);
                        if (event.endReason.mayStartNext) {
                            next();
                        }
                    }

                    @Override
                    protected void onTrackStuck(TrackStuckEvent event) {
                        logger.warn("Track stuck {}", event.track.getInfo().title);
                        next();
                    }

                    @Override
                    protected void onTrackException(TrackExceptionEvent event) {
                        logger.warn(
                                () -> "Error playing track" + event.track.getInfo().title,
                                event.exception);
                        next();
                    }

                    @Override
                    protected void onPlayerResume(PlayerResumeEvent event) {
                        logger.info("Resuming");
                        sendTextChannelMessage(() -> "Music resumed");
                    }

                    @Override
                    protected void onPlayerPause(PlayerPauseEvent event) {
                        logger.info("Pausing");
                        sendTextChannelMessage(() -> "Music paused");
                    }
                });
    }

    public boolean isConnected() {
        return audioManager.isConnected();
    }

    public void connect(VoiceChannel voiceChannel) {
        audioManager.setSendingHandler(sendHandler);
        audioManager.openAudioConnection(voiceChannel);
    }

    public void disconnect() {
        audioManager.setSendingHandler(null);
        audioManager.closeAudioConnection();
        currentTrackRequest = null;
    }

    public void addRequest(String url, User user) {
        playerManager.loadItem(
                url,
                new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        requestQueue.add(new TrackRequest(track, user));
                        logger.info("Added '{}' to queue", track.getInfo().title);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        for (AudioTrack track : playlist.getTracks()) {
                            trackLoaded(track);
                        }
                    }

                    @Override
                    public void noMatches() {
                        logger.warn("No matches found at url '{}'", url);
                        sendTextChannelMessage(() -> "No songs found at url `" + url + "`");
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {
                        logger.warn(() -> "Failed to load track at url " + url, exception);
                        sendTextChannelMessage(() -> "Failed to load track at url `" + url + "`");
                    }
                });
    }

    public void next() {
        currentTrackRequest = requestQueue.poll();

        if (null == currentTrackRequest) {
            disconnect();
            return;
        }

        sendTextChannelMessage(() -> "Now playing " + currentTrackRequest.getLabel());
        player.playTrack(currentTrackRequest.getTrack());
    }

    public void clear() {
        requestQueue.clear();
    }

    public Optional<String> getCurrentRequest() {
        return Optional.ofNullable(currentTrackRequest).map(TrackRequest::getLabel);
    }

    public List<String> getNextRequests() {
        return requestQueue.stream().map(TrackRequest::getLabel).collect(Collectors.toList());
    }

    public int getRemainingRequestCount() {
        if (currentTrackRequest != null) {
            return requestQueue.size() + 1;
        } else {
            return requestQueue.size();
        }
    }

    private void sendTextChannelMessage(Supplier<String> message) {
        if (SEND_PLAYLIST_UPDATE_MESSAGES) {
            textChannel.sendMessage(message.get()).queue();
        }
    }
}
