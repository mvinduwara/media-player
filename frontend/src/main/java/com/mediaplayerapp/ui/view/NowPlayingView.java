package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.ui.component.SpectrumVisualizer;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;

public class NowPlayingView extends VBox {

    private final MediaPlayerService playerService;

    private final ImageView coverView = new ImageView();
    private final Label titleLabel = new Label("Nothing playing");
    private final Label artistLabel = new Label("—");
    private final Label albumLabel = new Label("");
    private final Label yearLabel = new Label("");
    private final Button favouriteBtn = new Button();
    private SpectrumVisualizer visualizer;

    public NowPlayingView(MediaPlayerService playerService) {
        this.playerService = playerService;
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
        bindService();
    }

    private void buildUI() {
        Label pageTitle = new Label("Now Playing");
        pageTitle.getStyleClass().add("page-title");
        HBox header = new HBox(pageTitle);
        header.setPadding(new Insets(24, 28, 20, 28));

        HBox content = new HBox(40);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(0, 40, 0, 40));
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox leftPanel = buildCoverPanel();
        VBox rightPanel = buildInfoPanel();
        HBox.setHgrow(leftPanel, Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        content.getChildren().addAll(leftPanel, rightPanel);
        getChildren().addAll(header, content);
    }

    private VBox buildCoverPanel() {
        VBox panel = new VBox(24);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(340);
        panel.setMinWidth(280);
        panel.setMaxWidth(380);

        coverView.setFitWidth(300);
        coverView.setFitHeight(300);
        coverView.setPreserveRatio(false);
        coverView.setSmooth(true);

        Rectangle clip = new Rectangle(300, 300);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        coverView.setClip(clip);

        StackPane coverStack = new StackPane();
        coverStack.setMinSize(300, 300);
        coverStack.setMaxSize(300, 300);
        coverStack.setStyle("-fx-background-color: #1C1C1F; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 30, 0.3, 0, 8);");
        coverStack.setAlignment(Pos.CENTER);

        FontIcon musicIcon = FXUtils.icon("fas-music", 64, "#2E2E33");
        coverStack.getChildren().addAll(musicIcon, coverView);

        visualizer = new SpectrumVisualizer(300, 80);

        panel.getChildren().addAll(coverStack, visualizer);
        return panel;
    }

    private VBox buildInfoPanel() {
        VBox panel = new VBox(0);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(20, 0, 0, 0));

        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #EDEDF0; -fx-wrap-text: true;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(460);

        artistLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: #7C3AED; -fx-font-weight: 600;");
        artistLabel.setPadding(new Insets(10, 0, 4, 0));

        albumLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #9898A6;");

        HBox metaRow = new HBox(10);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        metaRow.setPadding(new Insets(8, 0, 0, 0));
        yearLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5A6A;");
        metaRow.getChildren().add(yearLabel);

        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        actionRow.setPadding(new Insets(28, 0, 0, 0));

        favouriteBtn.getStyleClass().add("btn-ghost");
        favouriteBtn.setGraphic(FXUtils.icon("far-heart", 14, "#9898A6"));
        favouriteBtn.setText("Favourite");
        favouriteBtn.setGraphicTextGap(8);

        Button addToQueueBtn = new Button("Add to Queue");
        addToQueueBtn.getStyleClass().add("btn-ghost");
        addToQueueBtn.setGraphic(FXUtils.icon("fas-list", 14, "#9898A6"));
        addToQueueBtn.setGraphicTextGap(8);
        addToQueueBtn.setOnAction(e -> {
            Track t = playerService.getCurrentTrack();
            if (t != null) playerService.addToQueue(t);
        });

        actionRow.getChildren().addAll(favouriteBtn, addToQueueBtn);

        VBox statsBox = buildStatsBox();

        Region spacer1 = new Region();
        spacer1.setPrefHeight(24);
        Region spacer2 = new Region();
        spacer2.setPrefHeight(20);

        panel.getChildren().addAll(
                titleLabel, artistLabel, albumLabel, metaRow,
                actionRow, spacer2, statsBox
        );
        return panel;
    }

    private VBox buildStatsBox() {
        VBox box = new VBox(12);
        box.setStyle("-fx-background-color: #1C1C1F; -fx-background-radius: 14; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-border-width: 1;");
        box.setPadding(new Insets(18, 24, 18, 24));
        box.setMaxWidth(360);

        Label heading = new Label("Track Details");
        heading.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #5A5A6A; -fx-text-transform: uppercase;");

        HBox row1 = statRow("Genre", "—");
        HBox row2 = statRow("Duration", "—");
        HBox row3 = statRow("Play Count", "0");

        box.getChildren().addAll(heading, row1, row2, row3);

        playerService.currentTrackProperty().addListener((obs, old, t) -> {
            if (t != null) {
                updateStatRow(row1, "Genre", t.getGenre().isEmpty() ? "Unknown" : t.getGenre());
                updateStatRow(row2, "Duration", t.getFormattedDuration());
                updateStatRow(row3, "Play Count", t.getPlayCount() + " plays");
            } else {
                updateStatRow(row1, "Genre", "—");
                updateStatRow(row2, "Duration", "—");
                updateStatRow(row3, "Play Count", "0");
            }
        });

        return box;
    }

    private HBox statRow(String key, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label keyLbl = new Label(key);
        keyLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5A6A;");
        keyLbl.setMinWidth(100);
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #EDEDF0;");
        valLbl.getProperties().put("val", true);
        Region sp = FXUtils.spacer();
        row.getChildren().addAll(keyLbl, sp, valLbl);
        return row;
    }

    private void updateStatRow(HBox row, String key, String value) {
        row.getChildren().stream()
                .filter(n -> n instanceof Label && n.getProperties().containsKey("val"))
                .map(n -> (Label) n)
                .findFirst()
                .ifPresent(l -> l.setText(value));
    }

    private void bindService() {
        playerService.currentTrackProperty().addListener((obs, old, track) -> updateTrack(track));
        playerService.playingProperty().addListener((obs, was, playing) -> {
            if (visualizer != null) visualizer.setActive(playing);
        });
        favouriteBtn.setOnAction(e -> {
            Track t = playerService.getCurrentTrack();
            if (t != null) {
                t.setFavourite(!t.isFavourite());
                updateFavBtn(t.isFavourite());
            }
        });
    }

    private void updateTrack(Track track) {
        if (track == null) {
            titleLabel.setText("Nothing playing");
            artistLabel.setText("—");
            albumLabel.setText("");
            yearLabel.setText("");
            coverView.setImage(null);
            updateFavBtn(false);
            return;
        }
        titleLabel.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        albumLabel.setText(track.getAlbum());
        yearLabel.setText(track.getYear().isEmpty() ? "" : "· " + track.getYear());
        loadCover(track);
        updateFavBtn(track.isFavourite());
        FXUtils.scaleIn(this, 200);
    }

    private void updateFavBtn(boolean fav) {
        favouriteBtn.setGraphic(FXUtils.icon(fav ? "fas-heart" : "far-heart", 14, fav ? "#7C3AED" : "#9898A6"));
        favouriteBtn.setText(fav ? "Favourited" : "Favourite");
    }

    private void loadCover(Track track) {
        String path = track.getCoverArtPath();
        if (path != null && !path.isEmpty()) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    Image img = new Image(new FileInputStream(f), 300, 300, false, true);
                    coverView.setImage(img);
                    return;
                }
            } catch (Exception ignored) {}
        }
        coverView.setImage(null);
    }

    public void dispose() {
        if (visualizer != null) visualizer.dispose();
    }
}