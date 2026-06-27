package com.mediaplayerapp.service;

import com.mediaplayerapp.model.EQPreset;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EqualizerService {

    private static final Logger log = LoggerFactory.getLogger(EqualizerService.class);

    private final BooleanProperty enabled = new SimpleBooleanProperty(false);
    private EQPreset activePreset;
    private final double[] currentGains = new double[10];
    private final List<EQPreset> builtInPresets;

    public EqualizerService() {
        builtInPresets = new ArrayList<>();
        builtInPresets.add(EQPreset.flat());
        builtInPresets.add(EQPreset.bassBoost());
        builtInPresets.add(EQPreset.rock());
        builtInPresets.add(EQPreset.pop());
        builtInPresets.add(EQPreset.jazz());
        builtInPresets.add(EQPreset.classical());
        builtInPresets.add(EQPreset.vocal());
        builtInPresets.add(EQPreset.electronic());
        activePreset = builtInPresets.get(0);
        System.arraycopy(activePreset.getGains(), 0, currentGains, 0, 10);
    }

    public void applyPreset(EQPreset preset) {
        if (preset == null) return;
        this.activePreset = preset;
        double[] gains = preset.getGains();
        for (int i = 0; i < Math.min(gains.length, currentGains.length); i++) {
            currentGains[i] = gains[i];
        }
        log.info("Applied EQ preset: {}", preset.getName());
    }

    public void setBandGain(int band, double gainDb) {
        if (band >= 0 && band < currentGains.length) {
            currentGains[band] = gainDb;
        }
    }

    public double getBandGain(int band) {
        if (band >= 0 && band < currentGains.length) return currentGains[band];
        return 0;
    }

    public double[] getCurrentGains() { return currentGains.clone(); }

    public List<EQPreset> getBuiltInPresets() { return builtInPresets; }

    public EQPreset getActivePreset() { return activePreset; }

    public boolean isEnabled() { return enabled.get(); }
    public void setEnabled(boolean enabled) { this.enabled.set(enabled); }
    public BooleanProperty enabledProperty() { return enabled; }
}
