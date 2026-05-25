package com.kelvinconnect.discord.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class LavaplayerAudioSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public LavaplayerAudioSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
