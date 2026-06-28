package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.model.AppSettings;
import com.mediaplayerapp.service.LibraryScannerService;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.SettingsService;
import com.mediaplayerapp.ui.view.SettingsView;

import java.io.File;
import java.util.function.Consumer;

public class SettingsController {

    private final SettingsView view;
    private final SettingsService settingsService;
    private final MediaPlayerService playerService;
    private final LibraryScannerService scannerService;

    private Consumer<AppSettings> onSettingsSaved;

    public SettingsController(SettingsView view,
                              SettingsService settingsService,
                              MediaPlayerService playerService,
                              LibraryScannerService scannerService) {
        this.view = view;
        this.settingsService = settingsService;
        this.playerService = playerService;
        this.scannerService = scannerService;
    }

    public void initialize() {
        AppSettings settings = settingsService.getSettings();
        applyToServices(settings);
    }

    public void saveSettings(AppSettings updated) {
        validateAndNormalize(updated);
        settingsService.save(updated);
        applyToServices(updated);
        if (onSettingsSaved != null) onSettingsSaved.accept(updated);
    }

    private void applyToServices(AppSettings settings) {
        playerService.setVolume(settings.getVolume() / 100.0);
        playerService.shuffleProperty().set(settings.isShuffle());
        playerService.repeatModeProperty().set(settings.getRepeatMode());
    }

    private void validateAndNormalize(AppSettings settings) {
        if (settings.getVolume() < 0) settings.setVolume(0);
        if (settings.getVolume() > 100) settings.setVolume(100);

        if (settings.getCrossfadeSeconds() < 0) settings.setCrossfadeSeconds(0);
        if (settings.getCrossfadeSeconds() > 10) settings.setCrossfadeSeconds(10);

        if (settings.getRepeatMode() == null ||
                (!settings.getRepeatMode().equals("NONE") &&
                        !settings.getRepeatMode().equals("ONE") &&
                        !settings.getRepeatMode().equals("ALL"))) {
            settings.setRepeatMode("NONE");
        }

        if (settings.getAccentColor() == null || settings.getAccentColor().isBlank()) {
            settings.setAccentColor("#7C3AED");
        }

        String folder = settings.getDefaultMusicFolder();
        if (folder != null && !folder.isBlank()) {
            File f = new File(folder);
            if (!f.exists() || !f.isDirectory()) {
                settings.setDefaultMusicFolder("");
            }
        }
    }

    public void triggerDefaultFolderScan(Consumer<Void> onComplete) {
        String folder = settingsService.getSettings().getDefaultMusicFolder();
        if (folder == null || folder.isBlank()) return;
        File dir = new File(folder);
        if (!dir.exists()) return;
        scannerService.scanFolder(dir, null, () -> {
            if (onComplete != null) onComplete.accept(null);
        });
    }

    public AppSettings getCurrentSettings() {
        return settingsService.getSettings();
    }

    public void updateVolume(int volume) {
        settingsService.update(s -> s.setVolume(volume));
        playerService.setVolume(volume / 100.0);
    }

    public void updateShuffle(boolean shuffle) {
        settingsService.update(s -> s.setShuffle(shuffle));
        playerService.shuffleProperty().set(shuffle);
    }

    public void updateRepeatMode(String mode) {
        settingsService.update(s -> s.setRepeatMode(mode));
        playerService.repeatModeProperty().set(mode);
    }

    public void updateAccentColor(String hex) {
        settingsService.update(s -> s.setAccentColor(hex));
    }

    public void setOnSettingsSaved(Consumer<AppSettings> handler) {
        this.onSettingsSaved = handler;
    }
}
