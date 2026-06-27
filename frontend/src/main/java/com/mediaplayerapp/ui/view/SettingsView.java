package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.AppSettings;
import com.mediaplayerapp.service.SettingsService;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class SettingsView extends VBox {

    private final SettingsService settingsService;
    private AppSettings settings;

    private final TextField folderField = new TextField();
    private final Slider crossfadeSlider = new Slider(0, 10, 2);
    private final Label crossfadeLabel = new Label("2.0s");
    private final ColorPicker accentPicker = new ColorPicker();
    private final CheckBox eqEnabledCheck = new CheckBox("Enable equalizer by default");

    public SettingsView(SettingsService settingsService) {
        this.settingsService = settingsService;
        this.settings = settingsService.getSettings();
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
        loadValues();
    }

    private void buildUI() {
        Label pageTitle = new Label("Settings");
        pageTitle.getStyleClass().add("page-title");
        HBox header = new HBox(pageTitle);
        header.setPadding(new Insets(24, 28, 20, 28));

        ScrollPane scroll = new ScrollPane(buildContent());
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(header, scroll);
    }

    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(0, 28, 28, 28));
        content.setMaxWidth(640);

        content.getChildren().addAll(
                sectionCard("Library", buildLibrarySection()),
                sectionCard("Playback", buildPlaybackSection()),
                sectionCard("Appearance", buildAppearanceSection()),
                buildSaveBtn()
        );

        return content;
    }

    private VBox sectionCard(String title, VBox inner) {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20, 24, 20, 24));

        Label heading = new Label(title.toUpperCase());
        heading.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #5A5A6A; -fx-letter-spacing: 1px;");

        Separator sep = new Separator();

        card.getChildren().addAll(heading, sep, inner);
        return card;
    }

    private VBox buildLibrarySection() {
        VBox section = new VBox(14);

        Label folderLabel = new Label("Default Music Folder");
        folderLabel.setStyle("-fx-text-fill: #EDEDF0; -fx-font-size: 13px; -fx-font-weight: 600;");
        Label folderHint = new Label("Waveline will scan this folder on startup");
        folderHint.setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px;");

        folderField.getStyleClass().add("text-field");
        folderField.setPromptText("Select a folder…");
        folderField.setEditable(false);
        folderField.setMaxWidth(Double.MAX_VALUE);

        Button browseBtn = new Button("Browse");
        browseBtn.getStyleClass().add("btn-ghost");
        browseBtn.setGraphic(FXUtils.icon("fas-folder-open", 13, "#9898A6"));
        browseBtn.setGraphicTextGap(8);
        browseBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Music Folder");
            Window window = getScene() != null ? getScene().getWindow() : null;
            File selected = chooser.showDialog(window);
            if (selected != null) folderField.setText(selected.getAbsolutePath());
        });

        HBox folderRow = new HBox(8, folderField, browseBtn);
        folderRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(folderField, Priority.ALWAYS);

        section.getChildren().addAll(folderLabel, folderHint, folderRow);
        return section;
    }

    private VBox buildPlaybackSection() {
        VBox section = new VBox(14);

        Label crossfadeLabel2 = new Label("Crossfade Duration");
        crossfadeLabel2.setStyle("-fx-text-fill: #EDEDF0; -fx-font-size: 13px; -fx-font-weight: 600;");
        Label crossfadeHint = new Label("Smooth transition between tracks");
        crossfadeHint.setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px;");

        crossfadeSlider.setBlockIncrement(0.5);
        crossfadeSlider.setMajorTickUnit(2);
        crossfadeSlider.setShowTickMarks(true);
        crossfadeSlider.setMaxWidth(360);
        crossfadeSlider.valueProperty().addListener((obs, old, val) ->
                crossfadeLabel.setText(String.format("%.1fs", val.doubleValue()))
        );

        crossfadeLabel.setStyle("-fx-text-fill: #9898A6; -fx-font-size: 13px; -fx-font-family: 'Consolas', monospace;");

        HBox crossfadeRow = new HBox(12, crossfadeSlider, crossfadeLabel);
        crossfadeRow.setAlignment(Pos.CENTER_LEFT);

        eqEnabledCheck.setStyle("-fx-text-fill: #EDEDF0; -fx-font-size: 13px;");

        section.getChildren().addAll(crossfadeLabel2, crossfadeHint, crossfadeRow, eqEnabledCheck);
        return section;
    }

    private VBox buildAppearanceSection() {
        VBox section = new VBox(14);

        Label accentLabel = new Label("Accent Color");
        accentLabel.setStyle("-fx-text-fill: #EDEDF0; -fx-font-size: 13px; -fx-font-weight: 600;");
        Label accentHint = new Label("Personalize the player's primary color");
        accentHint.setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px;");

        accentPicker.getStyleClass().add("button");
        accentPicker.setStyle("-fx-pref-width: 140px;");

        HBox colorPresets = new HBox(8);
        colorPresets.setAlignment(Pos.CENTER_LEFT);
        String[] presetColors = {"#7C3AED", "#2563EB", "#059669", "#DC2626", "#D97706", "#DB2777"};
        for (String hex : presetColors) {
            Button swatch = new Button();
            swatch.setMinSize(28, 28);
            swatch.setMaxSize(28, 28);
            swatch.setStyle("-fx-background-color: " + hex + "; -fx-background-radius: 50%; -fx-cursor: hand;");
            swatch.setOnAction(e -> accentPicker.setValue(Color.web(hex)));
            colorPresets.getChildren().add(swatch);
        }

        section.getChildren().addAll(accentLabel, accentHint, accentPicker, colorPresets);
        return section;
    }

    private HBox buildSaveBtn() {
        Button saveBtn = new Button("Save Settings");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setGraphic(FXUtils.icon("fas-floppy-disk", 13, "white"));
        saveBtn.setGraphicTextGap(8);
        saveBtn.setOnAction(e -> saveSettings());

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 13px;");
        saveBtn.setOnAction(e -> {
            saveSettings();
            statusLabel.setText("✓ Settings saved");
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.5))
            {{ setOnFinished(ev -> statusLabel.setText("")); play(); }};
        });

        HBox row = new HBox(12, saveBtn, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void loadValues() {
        folderField.setText(settings.getDefaultMusicFolder());
        crossfadeSlider.setValue(settings.getCrossfadeSeconds());
        crossfadeLabel.setText(String.format("%.1fs", settings.getCrossfadeSeconds()));
        eqEnabledCheck.setSelected(settings.isEqualizerEnabled());
        try {
            accentPicker.setValue(Color.web(settings.getAccentColor()));
        } catch (Exception e) {
            accentPicker.setValue(Color.web("#7C3AED"));
        }
    }

    private void saveSettings() {
        settings.setDefaultMusicFolder(folderField.getText());
        settings.setCrossfadeSeconds(crossfadeSlider.getValue());
        settings.setEqualizerEnabled(eqEnabledCheck.isSelected());
        Color c = accentPicker.getValue();
        settings.setAccentColor(String.format("#%02X%02X%02X",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255)));
        settingsService.save(settings);
    }
}