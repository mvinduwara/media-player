package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.EQPreset;
import com.mediaplayerapp.service.EqualizerService;
import com.mediaplayerapp.shared.AppConstants;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.util.ArrayList;
import java.util.List;

public class EqualizerView extends VBox {

    private final EqualizerService eqService;
    private final List<Slider> bandSliders = new ArrayList<>();
    private ComboBox<EQPreset> presetCombo;
    private final CheckBox enableToggle = new CheckBox("Equalizer Enabled");

    public EqualizerView(EqualizerService eqService) {
        this.eqService = eqService;
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
    }

    private void buildUI() {
        getChildren().addAll(buildHeader(), buildEQCard());
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setPadding(new Insets(24, 28, 16, 28));
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Equalizer");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Fine-tune your audio with 10-band EQ");
        sub.getStyleClass().add("label-secondary");
        sub.setStyle("-fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, sub);

        header.getChildren().addAll(titleBox);
        return header;
    }

    private VBox buildEQCard() {
        VBox card = new VBox(24);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(28));
        card.setMaxWidth(780);
        VBox.setMargin(card, new Insets(0, 28, 28, 28));

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        enableToggle.getStyleClass().add("toggle-switch");
        enableToggle.setSelected(eqService.isEnabled());
        enableToggle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #EDEDF0;");
        enableToggle.selectedProperty().addListener((obs, was, now) -> eqService.setEnabled(now));

        Region spacer = FXUtils.spacer();

        presetCombo = new ComboBox<>();
        presetCombo.getItems().addAll(eqService.getBuiltInPresets());
        presetCombo.setValue(eqService.getActivePreset());
        presetCombo.setStyle("-fx-pref-width: 160px;");
        presetCombo.setOnAction(e -> applyPreset(presetCombo.getValue()));

        Button resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("btn-ghost");
        resetBtn.setGraphic(FXUtils.icon("fas-rotate-left", 12, "#9898A6"));
        resetBtn.setGraphicTextGap(7);
        resetBtn.setOnAction(e -> resetToFlat());

        topRow.getChildren().addAll(enableToggle, spacer, new Label("Preset:") {{
            setStyle("-fx-text-fill: #9898A6; -fx-font-size: 13px;");
        }}, presetCombo, resetBtn);

        Separator sep = new Separator();

        HBox bandsBox = buildBands();

        HBox freqLabels = buildFreqLabels();

        card.getChildren().addAll(topRow, sep, bandsBox, freqLabels);
        return card;
    }

    private HBox buildBands() {
        HBox bandsBox = new HBox(0);
        bandsBox.setAlignment(Pos.BOTTOM_CENTER);
        bandsBox.setPrefHeight(200);

        for (int i = 0; i < AppConstants.EQ_BANDS; i++) {
            VBox bandCol = new VBox(8);
            bandCol.setAlignment(Pos.CENTER);
            HBox.setHgrow(bandCol, Priority.ALWAYS);

            Slider slider = new Slider(-12, 12, eqService.getBandGain(i));
            slider.setOrientation(Orientation.VERTICAL);
            slider.getStyleClass().add("eq-slider");
            slider.setPrefHeight(160);
            slider.setShowTickMarks(true);
            slider.setMajorTickUnit(6);
            slider.setSnapToTicks(false);
            slider.setBlockIncrement(1);

            final int band = i;
            final Label gainLabel = new Label(formatGain(eqService.getBandGain(i)));
            gainLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5A5A6A; -fx-font-family: 'Consolas', monospace;");
            gainLabel.setMinWidth(38);
            gainLabel.setAlignment(Pos.CENTER);

            slider.valueProperty().addListener((obs, old, val) -> {
                eqService.setBandGain(band, val.doubleValue());
                gainLabel.setText(formatGain(val.doubleValue()));
            });

            bandSliders.add(slider);

            Label zeroLine = new Label("0");
            zeroLine.setStyle("-fx-font-size: 10px; -fx-text-fill: #3A3A44;");

            bandCol.getChildren().addAll(gainLabel, slider);
            bandsBox.getChildren().add(bandCol);
        }

        return bandsBox;
    }

    private HBox buildFreqLabels() {
        HBox freqRow = new HBox(0);
        freqRow.setAlignment(Pos.CENTER);

        for (int freq : AppConstants.EQ_FREQUENCIES) {
            Label lbl = new Label(freqLabel(freq));
            lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #5A5A6A; -fx-alignment: center;");
            lbl.setAlignment(Pos.CENTER);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            lbl.setMaxWidth(Double.MAX_VALUE);
            freqRow.getChildren().add(lbl);
        }

        return freqRow;
    }

    private void applyPreset(EQPreset preset) {
        if (preset == null) return;
        eqService.applyPreset(preset);
        for (int i = 0; i < bandSliders.size(); i++) {
            bandSliders.get(i).setValue(preset.getGain(i));
        }
    }

    private void resetToFlat() {
        for (Slider s : bandSliders) s.setValue(0);
        presetCombo.setValue(eqService.getBuiltInPresets().get(0));
        eqService.applyPreset(eqService.getBuiltInPresets().get(0));
    }

    private String formatGain(double gain) {
        if (Math.abs(gain) < 0.05) return "0 dB";
        return String.format("%+.0f dB", gain);
    }

    private String freqLabel(int freq) {
        if (freq >= 1000) return (freq / 1000) + "k";
        return String.valueOf(freq);
    }
}
