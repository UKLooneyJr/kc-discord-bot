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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageAuthor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class MusicPlayer {
    private static final Logger logger = LogManager.getLogger(MusicPlayer.class);

    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private final AudioSource source;
    private final Queue<TrackRequest> requestQueue;
    private final ServerTextChannel textChannel;
    private AudioConnection audioConnection;
    private TrackRequest currentTrackRequest;

    public MusicPlayer(DiscordApi api, ServerTextChannel tc) {
        this.textChannel = tc;
        requestQueue = new LinkedBlockingQueue<>();

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        player = playerManager.createPlayer();

        source = new LavaplayerAudioSource(api, player);

        player.addListener(new AudioEventHandler() {
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
                logger.warn(() -> "Error playing track" + event.track.getInfo().title, event.exception);
                next();
            }

            @Override
            protected void onPlayerResume(PlayerResumeEvent event) {
                logger.info("Resuming");
                textChannel.sendMessage("Music resumed");
            }

            @Override
            protected void onPlayerPause(PlayerPauseEvent event) {
                logger.info("Pausing");
                textChannel.sendMessage("Music paused");
            }
        });
    }

    public boolean isConnected() {
        return null != audioConnection;
    }

    public void setAudioConnection(AudioConnection connection) {
        if (Objects.equals(audioConnection, connection)) {
            return;
        }

        if (null != audioConnection) {
            audioConnection.removeAudioSource();
        }

        audioConnection = connection;
        audioConnection.setAudioSource(source);
    }

    public void disconnect() {
        if (null != audioConnection) {
            audioConnection.removeAudioSource();
            audioConnection.close();
            audioConnection = null;
        }

        currentTrackRequest = null;
    }

    public void addRequest(String url, MessageAuthor user) {
        playerManager.loadItem(url, new AudioLoadResultHandler() {
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
                textChannel.sendMessage("No songs found at url `" + url + "`");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.warn(() -> "Failed to load track at url " + url, exception);
                textChannel.sendMessage("Failed to load track at url `" + url + "`");
            }
        });
    }

    /**
     * Plays the next track, if no tracks are left disconnects.
     */
    public void next() {
        currentTrackRequest = requestQueue.poll();

        if (null == currentTrackRequest) {
            disconnect();
            return;
        }

        textChannel.sendMessage("Now playing " + currentTrackRequest.getLabel());
        player.playTrack(currentTrackRequest.getTrack());
    }

    /**
     * Removes all requests from the queue.
     */
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
}
