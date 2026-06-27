package com.mediaplayerapp.service;

import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.shared.AppConstants;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class MediaPlayerService {

    private static final Logger log = LoggerFactory.getLogger(MediaPlayerService.class);

    private final TrackDao trackDao;

    private MediaPlayer mediaPlayer;
    private final ObservableList<Track> queue = FXCollections.observableArrayList();

    private final ObjectProperty<Track> currentTrack = new SimpleObjectProperty<>();
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final BooleanProperty shuffle = new SimpleBooleanProperty(false);
    private final StringProperty repeatMode = new SimpleStringProperty(AppConstants.REPEAT_NONE);
    private final DoubleProperty volume = new SimpleDoubleProperty(0.7);
    private final BooleanProperty muted = new SimpleBooleanProperty(false);
    private final DoubleProperty currentTime = new SimpleDoubleProperty(0);
    private final DoubleProperty totalTime = new SimpleDoubleProperty(0);
    private final StringProperty currentTimeFormatted = new SimpleStringProperty("0:00");
    private final StringProperty totalTimeFormatted = new SimpleStringProperty("0:00");
    private final BooleanProperty mediaReady = new SimpleBooleanProperty(false);

    private int currentQueueIndex = -1;
    private final List<Integer> shuffleOrder = new ArrayList<>();
    private int shuffleIndex = 0;

    private Runnable onTrackEnd;
    private Runnable onTrackChange;

    public MediaPlayerService(TrackDao trackDao) {
        this.trackDao = trackDao;
    }

    public void play(Track track) {
        if (track == null) return;
        stopCurrent();
        currentTrack.set(track);
        currentQueueIndex = queue.indexOf(track);

        File file = new File(track.getFilePath());
        if (!file.exists()) {
            log.warn("File not found: {}", track.getFilePath());
            return;
        }

        try {
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume.get());
            mediaPlayer.setMute(muted.get());
            mediaReady.set(false);

            mediaPlayer.setOnReady(() -> {
                totalTime.set(mediaPlayer.getTotalDuration().toMillis());
                totalTimeFormatted.set(formatTime(mediaPlayer.getTotalDuration()));
                mediaReady.set(true);
                mediaPlayer.play();
                playing.set(true);
                trackDao.incrementPlayCount(track.getId());
                if (onTrackChange != null) onTrackChange.run();
                log.info("Playing: {} - {}", track.getArtist(), track.getTitle());
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
                currentTime.set(newVal.toMillis());
                currentTimeFormatted.set(formatTime(newVal));
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                playing.set(false);
                if (onTrackEnd != null) onTrackEnd.run();
                handleTrackEnd();
            });

            mediaPlayer.setOnError(() -> {
                log.error("Media error for track: {}", track.getTitle());
                playing.set(false);
            });

            volume.addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) mediaPlayer.setVolume(newVal.doubleValue());
            });

            muted.addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) mediaPlayer.setMute(newVal);
            });

        } catch (Exception e) {
            log.error("Failed to play track: {}", track.getFilePath(), e);
        }
    }

    public void pause() {
        if (mediaPlayer != null && playing.get()) {
            mediaPlayer.pause();
            playing.set(false);
        }
    }

    public void resume() {
        if (mediaPlayer != null && !playing.get()) {
            mediaPlayer.play();
            playing.set(true);
        }
    }

    public void togglePlayPause() {
        if (playing.get()) pause();
        else resume();
    }

    public void stop() {
        stopCurrent();
        currentTrack.set(null);
        currentTime.set(0);
        currentTimeFormatted.set("0:00");
    }

    private void stopCurrent() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            playing.set(false);
        }
    }

    public void seek(double millis) {
        if (mediaPlayer != null && mediaReady.get()) {
            mediaPlayer.seek(Duration.millis(millis));
        }
    }

    public void next() {
        if (queue.isEmpty()) return;
        if (shuffle.get()) {
            shuffleIndex = (shuffleIndex + 1) % shuffleOrder.size();
            play(queue.get(shuffleOrder.get(shuffleIndex)));
        } else {
            int next = currentQueueIndex + 1;
            if (next >= queue.size()) {
                if (repeatMode.get().equals(AppConstants.REPEAT_ALL)) next = 0;
                else return;
            }
            play(queue.get(next));
        }
    }

    public void previous() {
        if (queue.isEmpty()) return;
        if (currentTime.get() > 3000) {
            seek(0);
            return;
        }
        if (shuffle.get()) {
            shuffleIndex = Math.max(0, shuffleIndex - 1);
            play(queue.get(shuffleOrder.get(shuffleIndex)));
        } else {
            int prev = currentQueueIndex - 1;
            if (prev < 0) prev = queue.size() - 1;
            play(queue.get(prev));
        }
    }

    private void handleTrackEnd() {
        String mode = repeatMode.get();
        if (AppConstants.REPEAT_ONE.equals(mode)) {
            Track t = currentTrack.get();
            if (t != null) play(t);
        } else {
            next();
        }
    }

    public void setQueue(List<Track> tracks, int startIndex) {
        queue.setAll(tracks);
        buildShuffleOrder();
        if (startIndex >= 0 && startIndex < tracks.size()) {
            play(tracks.get(startIndex));
        }
    }

    public void addToQueue(Track track) {
        queue.add(track);
        shuffleOrder.add(queue.size() - 1);
    }

    private void buildShuffleOrder() {
        shuffleOrder.clear();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < queue.size(); i++) indices.add(i);
        Collections.shuffle(indices);
        shuffleOrder.addAll(indices);
        shuffleIndex = 0;
    }

    public void toggleShuffle() {
        shuffle.set(!shuffle.get());
        if (shuffle.get()) buildShuffleOrder();
    }

    public void cycleRepeatMode() {
        switch (repeatMode.get()) {
            case AppConstants.REPEAT_NONE -> repeatMode.set(AppConstants.REPEAT_ALL);
            case AppConstants.REPEAT_ALL -> repeatMode.set(AppConstants.REPEAT_ONE);
            default -> repeatMode.set(AppConstants.REPEAT_NONE);
        }
    }

    private String formatTime(Duration d) {
        if (d == null || d.isUnknown()) return "0:00";
        long secs = (long) d.toSeconds();
        return String.format("%d:%02d", secs / 60, secs % 60);
    }

    public void setOnTrackEnd(Runnable r) { this.onTrackEnd = r; }
    public void setOnTrackChange(Runnable r) { this.onTrackChange = r; }

    public ObjectProperty<Track> currentTrackProperty() { return currentTrack; }
    public BooleanProperty playingProperty() { return playing; }
    public BooleanProperty shuffleProperty() { return shuffle; }
    public StringProperty repeatModeProperty() { return repeatMode; }
    public DoubleProperty volumeProperty() { return volume; }
    public BooleanProperty mutedProperty() { return muted; }
    public DoubleProperty currentTimeProperty() { return currentTime; }
    public DoubleProperty totalTimeProperty() { return totalTime; }
    public StringProperty currentTimeFormattedProperty() { return currentTimeFormatted; }
    public StringProperty totalTimeFormattedProperty() { return totalTimeFormatted; }
    public BooleanProperty mediaReadyProperty() { return mediaReady; }
    public ObservableList<Track> getQueue() { return queue; }

    public Track getCurrentTrack() { return currentTrack.get(); }
    public boolean isPlaying() { return playing.get(); }
    public double getVolume() { return volume.get(); }
    public void setVolume(double v) { volume.set(Math.max(0, Math.min(1, v))); }
    public boolean isMuted() { return muted.get(); }
    public void setMuted(boolean m) { muted.set(m); }

    public void dispose() {
        stopCurrent();
    }
}
