package com.mediaplayerapp.service;

import com.mediaplayerapp.dao.SettingsDao;
import com.mediaplayerapp.model.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final SettingsDao settingsDao;
    private AppSettings settings;

    public SettingsService(SettingsDao settingsDao) {
        this.settingsDao = settingsDao;
    }

    public AppSettings load() {
        settings = settingsDao.load();
        log.info("Settings loaded");
        return settings;
    }

    public void save(AppSettings settings) {
        this.settings = settings;
        settingsDao.save(settings);
        log.info("Settings saved");
    }

    public AppSettings getSettings() {
        if (settings == null) load();
        return settings;
    }

    public void update(java.util.function.Consumer<AppSettings> updater) {
        if (settings == null) load();
        updater.accept(settings);
        save(settings);
    }
}
