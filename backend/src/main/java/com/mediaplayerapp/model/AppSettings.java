package com.mediaplayerapp.model;

public class AppSettings {

    private String defaultMusicFolder = "";
    private int volume = 70;
    private boolean shuffle = false;
    private String repeatMode = "NONE";
    private double crossfadeSeconds = 2.0;
    private boolean equalizerEnabled = false;
    private long lastActiveEQPresetId = -1;
    private long lastPlayedTrackId = -1;
    private long lastPlayedPositionMillis = 0;
    private long lastActivePlaylistId = -1;
    private String accentColor = "#7C3AED";
    private String theme = "DARK";

    public AppSettings() {}

    public String getDefaultMusicFolder() { return defaultMusicFolder; }
    public void setDefaultMusicFolder(String defaultMusicFolder) { this.defaultMusicFolder = defaultMusicFolder; }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public boolean isShuffle() { return shuffle; }
    public void setShuffle(boolean shuffle) { this.shuffle = shuffle; }

    public String getRepeatMode() { return repeatMode; }
    public void setRepeatMode(String repeatMode) { this.repeatMode = repeatMode; }

    public double getCrossfadeSeconds() { return crossfadeSeconds; }
    public void setCrossfadeSeconds(double crossfadeSeconds) { this.crossfadeSeconds = crossfadeSeconds; }

    public boolean isEqualizerEnabled() { return equalizerEnabled; }
    public void setEqualizerEnabled(boolean equalizerEnabled) { this.equalizerEnabled = equalizerEnabled; }

    public long getLastActiveEQPresetId() { return lastActiveEQPresetId; }
    public void setLastActiveEQPresetId(long lastActiveEQPresetId) { this.lastActiveEQPresetId = lastActiveEQPresetId; }

    public long getLastPlayedTrackId() { return lastPlayedTrackId; }
    public void setLastPlayedTrackId(long lastPlayedTrackId) { this.lastPlayedTrackId = lastPlayedTrackId; }

    public long getLastPlayedPositionMillis() { return lastPlayedPositionMillis; }
    public void setLastPlayedPositionMillis(long lastPlayedPositionMillis) { this.lastPlayedPositionMillis = lastPlayedPositionMillis; }

    public long getLastActivePlaylistId() { return lastActivePlaylistId; }
    public void setLastActivePlaylistId(long lastActivePlaylistId) { this.lastActivePlaylistId = lastActivePlaylistId; }

    public String getAccentColor() { return accentColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}
