package com.kelvinconnect.discord.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.Objects;
import net.dv8tion.jda.api.entities.User;

public class TrackRequest {
    private final AudioTrack track;
    private final User user;

    public TrackRequest(AudioTrack track, User user) {
        this.track = Objects.requireNonNull(track);
        this.user = Objects.requireNonNull(user);
    }

    public AudioTrack getTrack() {
        return track;
    }

    public String getLabel() {
        AudioTrackInfo info = track.getInfo();
        return String.format("**%s** requested by *%s*", info.title, user.getEffectiveName());
    }

    @Override
    public String toString() {
        return "TrackRequest{" + "track=" + track + ", user=" + user + '}';
    }
}
