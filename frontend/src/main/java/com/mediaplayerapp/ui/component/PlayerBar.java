package com.mediaplayerapp.ui.component;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;

public class PlayerBar extends HBox {

    private final MediaPlayerService playerService;

    private final Label titleLabel = new Label("No track selected");
    private final Label artistLabel = new Label("");
    private final Label currentTimeLabel = new Label("0:00");
    private final Label totalTimeLabel = new Label("0:00");
    private final Slider seekSlider = new Slider(0, 1, 0);
    private final Slider volumeSlider = new Slider(0, 1, 0.7);
    private final Button playPauseBtn = new Button();
    private final Button prevBtn = new Button();
    private final Button nextBtn = new Button();
    private final Button shuffleBtn = new Button();
    private final Button repeatBtn = new Button();
    private final Button muteBtn = new Button();
    private final ImageView coverArtView = new ImageView();

    private boolean seekDragging = false;

    public PlayerBar(MediaPlayerService playerService) {
        this.playerService = playerService;
        getStyleClass().add("player-bar");
        setAlignment(Pos.CENTER);
        setSpacing(0);
        buildUI();
        bindToService();
    }

    private void buildUI() {
        HBox leftSection = buildLeftSection();
        HBox centerSection = buildCenterSection();
        HBox rightSection = buildRightSection();

        HBox.setHgrow(leftSection, Priority.ALWAYS);
        leftSection.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(centerSection, Priority.NEVER);
        HBox.setHgrow(rightSection, Priority.ALWAYS);
        rightSection.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(leftSection, centerSection, rightSection);
    }

    private HBox buildLeftSection() {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 0, 0));

        coverArtView.setFitWidth(54);
        coverArtView.setFitHeight(54);
        coverArtView.setPreserveRatio(false);
        coverArtView.setSmooth(true);

        Rectangle clip = new Rectangle(54, 54);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        coverArtView.setClip(clip);

        StackPane coverBox = new StackPane(coverArtView);
        coverBox.setMinSize(54, 54);
        coverBox.setMaxSize(54, 54);
        coverBox.setStyle("-fx-background-color: #242428; -fx-background-radius: 8;");
        coverBox.setAlignment(Pos.CENTER);

        FontIcon musicIcon = FXUtils.icon("fas-music", 18, "#5A5A6A");
        coverBox.getChildren().add(0, musicIcon);

        titleLabel.getStyleClass().add("player-track-title");
        titleLabel.setMaxWidth(220);
        artistLabel.getStyleClass().add("player-track-artist");
        artistLabel.setMaxWidth(220);

        Button favouriteBtn = new Button();
        favouriteBtn.getStyleClass().add("btn-icon");
        FontIcon heartIcon = FXUtils.icon("far-heart", 14, "#5A5A6A");
        favouriteBtn.setGraphic(heartIcon);
        FXUtils.tooltip(favouriteBtn, "Add to favourites");

        VBox info = new VBox(2, titleLabel, artistLabel);
        info.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(coverBox, info, favouriteBtn);
        return box;
    }

    private HBox buildCenterSection() {
        VBox vbox = new VBox(8);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(460);
        vbox.setMinWidth(380);

        HBox controls = new HBox(6);
        controls.setAlignment(Pos.CENTER);

        shuffleBtn.getStyleClass().add("btn-icon");
        shuffleBtn.setGraphic(FXUtils.icon("fas-shuffle", 14, "#9898A6"));
        FXUtils.tooltip(shuffleBtn, "Shuffle");

        prevBtn.getStyleClass().add("btn-icon");
        prevBtn.setGraphic(FXUtils.icon("fas-backward-step", 18, "#EDEDF0"));
        FXUtils.tooltip(prevBtn, "Previous");

        FontIcon playIcon = FXUtils.icon("fas-play", 16, "white");
        playPauseBtn.getStyleClass().add("btn-play");
        playPauseBtn.setGraphic(playIcon);
        FXUtils.tooltip(playPauseBtn, "Play");

        nextBtn.getStyleClass().add("btn-icon");
        nextBtn.setGraphic(FXUtils.icon("fas-forward-step", 18, "#EDEDF0"));
        FXUtils.tooltip(nextBtn, "Next");

        repeatBtn.getStyleClass().add("btn-icon");
        repeatBtn.setGraphic(FXUtils.icon("fas-repeat", 14, "#9898A6"));
        FXUtils.tooltip(repeatBtn, "Repeat");

        controls.getChildren().addAll(shuffleBtn, prevBtn, playPauseBtn, nextBtn, repeatBtn);

        seekSlider.getStyleClass().add("seek-slider");
        seekSlider.setMin(0);
        seekSlider.setMax(1);
        seekSlider.setValue(0);
        seekSlider.setPrefWidth(360);
        HBox.setHgrow(seekSlider, Priority.ALWAYS);

        currentTimeLabel.getStyleClass().add("time-label");
        totalTimeLabel.getStyleClass().add("time-label");

        HBox seekRow = new HBox(8);
        seekRow.setAlignment(Pos.CENTER);
        seekRow.getChildren().addAll(currentTimeLabel, seekSlider, totalTimeLabel);
        HBox.setHgrow(seekSlider, Priority.ALWAYS);

        vbox.getChildren().addAll(controls, seekRow);

        HBox wrapper = new HBox(vbox);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(0, 24, 0, 24));
        return wrapper;
    }

    private HBox buildRightSection() {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(0, 0, 0, 0));

        Button queueBtn = new Button();
        queueBtn.getStyleClass().add("btn-icon");
        queueBtn.setGraphic(FXUtils.icon("fas-list", 14, "#9898A6"));
        FXUtils.tooltip(queueBtn, "Queue");

        muteBtn.getStyleClass().add("btn-icon");
        muteBtn.setGraphic(FXUtils.icon("fas-volume-high", 14, "#9898A6"));
        FXUtils.tooltip(muteBtn, "Mute");

        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.setPrefWidth(90);
        volumeSlider.setValue(0.7);

        box.getChildren().addAll(queueBtn, muteBtn, volumeSlider);
        return box;
    }

    private void bindToService() {
        playPauseBtn.setOnAction(e -> playerService.togglePlayPause());
        prevBtn.setOnAction(e -> playerService.previous());
        nextBtn.setOnAction(e -> playerService.next());

        shuffleBtn.setOnAction(e -> {
            playerService.toggleShuffle();
            updateShuffleBtn();
        });

        repeatBtn.setOnAction(e -> {
            playerService.cycleRepeatMode();
            updateRepeatBtn();
        });

        playerService.playingProperty().addListener((obs, was, isNow) -> updatePlayBtn(isNow));

        playerService.currentTrackProperty().addListener((obs, old, track) -> {
            if (track != null) {
                titleLabel.setText(track.getTitle());
                artistLabel.setText(track.getArtist());
                loadCover(track);
            } else {
                titleLabel.setText("No track selected");
                artistLabel.setText("");
                coverArtView.setImage(null);
            }
        });

        playerService.currentTimeProperty().addListener((obs, old, millis) -> {
            if (!seekDragging) {
                double total = playerService.totalTimeProperty().get();
                if (total > 0) {
                    seekSlider.setValue(millis.doubleValue() / total);
                }
            }
        });

        currentTimeLabel.textProperty().bind(playerService.currentTimeFormattedProperty());
        totalTimeLabel.textProperty().bind(playerService.totalTimeFormattedProperty());

        seekSlider.setOnMousePressed(e -> seekDragging = true);
        seekSlider.setOnMouseReleased(e -> {
            seekDragging = false;
            double total = playerService.totalTimeProperty().get();
            playerService.seek(seekSlider.getValue() * total);
        });
        seekSlider.valueChangingProperty().addListener((obs, was, changing) -> {
            if (!changing) {
                double total = playerService.totalTimeProperty().get();
                playerService.seek(seekSlider.getValue() * total);
            }
        });

        volumeSlider.valueProperty().bindBidirectional(playerService.volumeProperty());

        muteBtn.setOnAction(e -> {
            playerService.setMuted(!playerService.isMuted());
            updateMuteBtn();
        });
    }

    private void updatePlayBtn(boolean playing) {
        FontIcon icon = playing
                ? FXUtils.icon("fas-pause", 16, "white")
                : FXUtils.icon("fas-play", 16, "white");
        playPauseBtn.setGraphic(icon);
        FXUtils.tooltip(playPauseBtn, playing ? "Pause" : "Play");
    }

    private void updateShuffleBtn() {
        boolean on = playerService.shuffleProperty().get();
        shuffleBtn.getStyleClass().remove("active");
        if (on) shuffleBtn.getStyleClass().add("active");
        shuffleBtn.setGraphic(FXUtils.icon("fas-shuffle", 14, on ? "#9D6FEF" : "#9898A6"));
    }

    private void updateRepeatBtn() {
        String mode = playerService.repeatModeProperty().get();
        repeatBtn.getStyleClass().remove("active");
        switch (mode) {
            case "ALL" -> {
                repeatBtn.getStyleClass().add("active");
                repeatBtn.setGraphic(FXUtils.icon("fas-repeat", 14, "#9D6FEF"));
                FXUtils.tooltip(repeatBtn, "Repeat all");
            }
            case "ONE" -> {
                repeatBtn.getStyleClass().add("active");
                repeatBtn.setGraphic(FXUtils.icon("fas-repeat-1", 14, "#9D6FEF"));
                FXUtils.tooltip(repeatBtn, "Repeat one");
            }
            default -> {
                repeatBtn.setGraphic(FXUtils.icon("fas-repeat", 14, "#9898A6"));
                FXUtils.tooltip(repeatBtn, "Repeat off");
            }
        }
    }

    private void updateMuteBtn() {
        boolean muted = playerService.isMuted();
        String ico = muted ? "fas-volume-xmark" : "fas-volume-high";
        muteBtn.setGraphic(FXUtils.icon(ico, 14, "#9898A6"));
    }

    private void loadCover(Track track) {
        if (track.getCoverArtPath() != null && !track.getCoverArtPath().isEmpty()) {
            try {
                File f = new File(track.getCoverArtPath());
                if (f.exists()) {
                    Image img = new Image(new FileInputStream(f), 54, 54, false, true);
                    coverArtView.setImage(img);
                    return;
                }
            } catch (Exception ignored) {}
        }
        coverArtView.setImage(null);
    }
}