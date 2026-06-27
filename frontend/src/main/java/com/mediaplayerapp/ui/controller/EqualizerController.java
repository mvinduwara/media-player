package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.model.EQPreset;
import com.mediaplayerapp.service.EqualizerService;
import com.mediaplayerapp.service.SettingsService;
import com.mediaplayerapp.ui.view.EqualizerView;

import java.util.List;

public class EqualizerController {

    private final EqualizerView view;
    private final EqualizerService equalizerService;
    private final SettingsService settingsService;

    public EqualizerController(EqualizerView view,
                               EqualizerService equalizerService,
                               SettingsService settingsService) {
        this.view = view;
        this.equalizerService = equalizerService;
        this.settingsService = settingsService;
    }

    public void initialize() {
        boolean eqEnabled = settingsService.getSettings().isEqualizerEnabled();
        equalizerService.setEnabled(eqEnabled);

        equalizerService.enabledProperty().addListener((obs, was, now) ->
                settingsService.update(s -> s.setEqualizerEnabled(now))
        );
    }

    public void applyPreset(EQPreset preset) {
        equalizerService.applyPreset(preset);
        settingsService.update(s -> s.setLastActiveEQPresetId(preset.getId()));
    }

    public void setBandGain(int band, double gainDb) {
        equalizerService.setBandGain(band, gainDb);
    }

    public double getBandGain(int band) {
        return equalizerService.getBandGain(band);
    }

    public void setEnabled(boolean enabled) {
        equalizerService.setEnabled(enabled);
        settingsService.update(s -> s.setEqualizerEnabled(enabled));
    }

    public boolean isEnabled() {
        return equalizerService.isEnabled();
    }

    public void resetToFlat() {
        List<EQPreset> presets = equalizerService.getBuiltInPresets();
        if (!presets.isEmpty()) applyPreset(presets.get(0));
    }

    public List<EQPreset> getBuiltInPresets() {
        return equalizerService.getBuiltInPresets();
    }

    public EQPreset getActivePreset() {
        return equalizerService.getActivePreset();
    }

    public double[] getCurrentGains() {
        return equalizerService.getCurrentGains();
    }
}