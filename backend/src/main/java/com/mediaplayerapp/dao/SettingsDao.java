package com.mediaplayerapp.dao;

import com.mediaplayerapp.db.DatabaseManager;
import com.mediaplayerapp.model.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SettingsDao {

    private static final Logger log = LoggerFactory.getLogger(SettingsDao.class);

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    private void set(String key, String value) {
        String sql = "MERGE INTO app_settings (setting_key, setting_value) KEY(setting_key) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to set setting {}={}", key, value, e);
        }
    }

    private String get(String key, String defaultValue) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT setting_value FROM app_settings WHERE setting_key=?")) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String v = rs.getString("setting_value");
                return v != null ? v : defaultValue;
            }
        } catch (SQLException e) {
            log.error("Failed to get setting key={}", key, e);
        }
        return defaultValue;
    }

    public void save(AppSettings settings) {
        set("default_music_folder",      settings.getDefaultMusicFolder());
        set("volume",                    String.valueOf(settings.getVolume()));
        set("shuffle",                   String.valueOf(settings.isShuffle()));
        set("repeat_mode",               settings.getRepeatMode());
        set("crossfade_seconds",         String.valueOf(settings.getCrossfadeSeconds()));
        set("equalizer_enabled",         String.valueOf(settings.isEqualizerEnabled()));
        set("last_active_eq_preset_id",  String.valueOf(settings.getLastActiveEQPresetId()));
        set("last_played_track_id",      String.valueOf(settings.getLastPlayedTrackId()));
        set("last_played_position_millis", String.valueOf(settings.getLastPlayedPositionMillis()));
        set("last_active_playlist_id",   String.valueOf(settings.getLastActivePlaylistId()));
        set("accent_color",              settings.getAccentColor());
        set("theme",                     settings.getTheme());
    }

    public AppSettings load() {
        AppSettings s = new AppSettings();
        s.setDefaultMusicFolder(get("default_music_folder", ""));
        s.setVolume(intVal(get("volume", "70")));
        s.setShuffle(Boolean.parseBoolean(get("shuffle", "false")));
        s.setRepeatMode(get("repeat_mode", "NONE"));
        s.setCrossfadeSeconds(doubleVal(get("crossfade_seconds", "2.0")));
        s.setEqualizerEnabled(Boolean.parseBoolean(get("equalizer_enabled", "false")));
        s.setLastActiveEQPresetId(longVal(get("last_active_eq_preset_id", "-1")));
        s.setLastPlayedTrackId(longVal(get("last_played_track_id", "-1")));
        s.setLastPlayedPositionMillis(longVal(get("last_played_position_millis", "0")));
        s.setLastActivePlaylistId(longVal(get("last_active_playlist_id", "-1")));
        s.setAccentColor(get("accent_color", "#7C3AED"));
        s.setTheme(get("theme", "DARK"));
        return s;
    }

    private int intVal(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    private long longVal(String s) {
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return -1L; }
    }

    private double doubleVal(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 2.0; }
    }
}