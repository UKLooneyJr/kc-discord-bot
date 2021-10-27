package com.kelvinconnect.discord.command.music;

import com.sedmelluq.discord.lavaplayer.player.event.*;

public abstract class AudioEventHandler implements AudioEventListener {
    @Override
    public void onEvent(AudioEvent event) {
        if (event instanceof TrackStartEvent) {
            onTrackStart((TrackStartEvent) event);
            return;
        }
        if (event instanceof TrackEndEvent) {
            onTrackEnd((TrackEndEvent) event);
            return;
        }
        if (event instanceof TrackStuckEvent) {
            onTrackStuck((TrackStuckEvent) event);
            return;
        }
        if (event instanceof TrackExceptionEvent) {
            onTrackException((TrackExceptionEvent) event);
            return;
        }
        if (event instanceof PlayerResumeEvent) {
            onPlayerResume((PlayerResumeEvent) event);
            return;
        }
        if (event instanceof PlayerPauseEvent) {
            onPlayerPause((PlayerPauseEvent) event);
            return;
        }

        onUnknownEvent(event);
    }

    protected abstract void onTrackStart(TrackStartEvent event);

    protected abstract void onTrackEnd(TrackEndEvent event);

    protected abstract void onTrackStuck(TrackStuckEvent event);

    protected abstract void onTrackException(TrackExceptionEvent event);

    protected abstract void onPlayerResume(PlayerResumeEvent event);

    protected abstract void onPlayerPause(PlayerPauseEvent event);

    protected void onUnknownEvent(AudioEvent event) {
        throw new IllegalStateException(
                "Cannot handle AudioEvent [" + event.getClass().getSimpleName() + "]: " + event);
    }
}
