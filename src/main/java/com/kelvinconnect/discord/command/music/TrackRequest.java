package com.kelvinconnect.discord.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.javacord.api.entity.message.MessageAuthor;

import java.util.Objects;

public class TrackRequest {
    private final AudioTrack track;
    private final MessageAuthor user;

    public TrackRequest(AudioTrack track, MessageAuthor user) {
        this.track = Objects.requireNonNull(track);
        this.user = Objects.requireNonNull(user);
    }

    public AudioTrack getTrack() {
        return track;
    }

    public String getLabel() {
        AudioTrackInfo info = track.getInfo();
        return String.format("**%s** requested by *%s*", info.title, user.getDisplayName());
    }

    @Override
    public String toString() {
        return "TrackRequest{" + "track=" + track + ", user=" + user + '}';
    }
}
