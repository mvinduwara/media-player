package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.EqualizerService;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.SettingsService;
import com.mediaplayerapp.shared.AppConstants;
import com.mediaplayerapp.ui.component.PlayerBar;
import javafx.beans.property.*;

import java.util.List;
import java.util.function.Consumer;

public class PlayerController {

    private final MediaPlayerService playerService;
    private final EqualizerService equalizerService;
    private final SettingsService settingsService;
    private final TrackDao trackDao;

    private PlayerBar playerBar;

    private Consumer<Track> onTrackChanged;
    private Runnable onPlaybackStarted;
    private Runnable onPlaybackStopped;

    public PlayerController(MediaPlayerService playerService,
                            EqualizerService equalizerService,
                            SettingsService settingsService,
                            TrackDao trackDao) {
        this.playerService = playerService;
        this.equalizerService = equalizerService;
        this.settingsService = settingsService;
        this.trackDao = trackDao;
    }

    public void initialize() {
        restoreLastState();
        bindCallbacks();
    }

    private void restoreLastState() {
        long lastTrackId = settingsService.getSettings().getLastPlayedTrackId();
        long lastPos = settingsService.getSettings().getLastPlayedPositionMillis();
        String repeatMode = settingsService.getSettings().getRepeatMode();
        boolean shuffle = settingsService.getSettings().isShuffle();
        int volume = settingsService.getSettings().getVolume();

        playerService.setVolume(volume / 100.0);
        playerService.repeatModeProperty().set(repeatMode);
        playerService.shuffleProperty().set(shuffle);

        if (lastTrackId > 0) {
            trackDao.findById(lastTrackId).ifPresent(track -> {
                playerService.setQueue(List.of(track), 0);
                if (lastPos > 0) {
                    playerService.mediaReadyProperty().addListener((obs, was, ready) -> {
                        if (ready) playerService.seek(lastPos);
                    });
                }
            });
        }
    }

    private void bindCallbacks() {
        playerService.setOnTrackChange(() -> {
            Track t = playerService.getCurrentTrack();
            if (t != null) {
                persistLastTrack(t.getId());
                if (onTrackChanged != null) onTrackChanged.accept(t);
            }
        });

        playerService.playingProperty().addListener((obs, was, playing) -> {
            if (playing && onPlaybackStarted != null) onPlaybackStarted.run();
            if (!playing && onPlaybackStopped != null) onPlaybackStopped.run();
        });

        playerService.currentTimeProperty().addListener((obs, old, millis) -> {
            if (playerService.isPlaying()) {
                settingsService.update(s -> s.setLastPlayedPositionMillis(millis.longValue()));
            }
        });
    }

    public void play(Track track) {
        playerService.play(track);
    }

    public void play(List<Track> tracks, int startIndex) {
        playerService.setQueue(tracks, startIndex);
    }

    public void pause() {
        playerService.pause();
    }

    public void resume() {
        playerService.resume();
    }

    public void togglePlayPause() {
        playerService.togglePlayPause();
    }

    public void stop() {
        playerService.stop();
    }

    public void next() {
        playerService.next();
    }

    public void previous() {
        playerService.previous();
    }

    public void seek(double millis) {
        playerService.seek(millis);
        settingsService.update(s -> s.setLastPlayedPositionMillis((long) millis));
    }

    public void setVolume(double volume) {
        playerService.setVolume(volume);
        settingsService.update(s -> s.setVolume((int) (volume * 100)));
    }

    public void toggleMute() {
        playerService.setMuted(!playerService.isMuted());
    }

    public void toggleShuffle() {
        playerService.toggleShuffle();
        settingsService.update(s -> s.setShuffle(playerService.shuffleProperty().get()));
    }

    public void cycleRepeat() {
        playerService.cycleRepeatMode();
        settingsService.update(s -> s.setRepeatMode(playerService.repeatModeProperty().get()));
    }

    public void addToQueue(Track track) {
        playerService.addToQueue(track);
    }

    public void clearQueue() {
        playerService.getQueue().clear();
    }

    private void persistLastTrack(long trackId) {
        settingsService.update(s -> {
            s.setLastPlayedTrackId(trackId);
            s.setLastPlayedPositionMillis(0);
        });
        trackDao.incrementPlayCount(trackId);
    }

    public void saveStateOnShutdown() {
        settingsService.update(s -> {
            s.setShuffle(playerService.shuffleProperty().get());
            s.setRepeatMode(playerService.repeatModeProperty().get());
            s.setVolume((int) (playerService.getVolume() * 100));
            s.setEqualizerEnabled(equalizerService.isEnabled());
            Track current = playerService.getCurrentTrack();
            if (current != null) {
                s.setLastPlayedTrackId(current.getId());
                s.setLastPlayedPositionMillis((long) playerService.currentTimeProperty().get());
            }
        });
    }

    public void setPlayerBar(PlayerBar bar) {
        this.playerBar = bar;
    }

    public void setOnTrackChanged(Consumer<Track> handler) { this.onTrackChanged = handler; }
    public void setOnPlaybackStarted(Runnable handler) { this.onPlaybackStarted = handler; }
    public void setOnPlaybackStopped(Runnable handler) { this.onPlaybackStopped = handler; }

    public Track getCurrentTrack() { return playerService.getCurrentTrack(); }
    public boolean isPlaying() { return playerService.isPlaying(); }
    public BooleanProperty playingProperty() { return playerService.playingProperty(); }
    public ObjectProperty<Track> currentTrackProperty() { return playerService.currentTrackProperty(); }
    public DoubleProperty volumeProperty() { return playerService.volumeProperty(); }
    public BooleanProperty shuffleProperty() { return playerService.shuffleProperty(); }
    public StringProperty repeatModeProperty() { return playerService.repeatModeProperty(); }
    public DoubleProperty currentTimeProperty() { return playerService.currentTimeProperty(); }
    public DoubleProperty totalTimeProperty() { return playerService.totalTimeProperty(); }
    public MediaPlayerService getPlayerService() { return playerService; }
}