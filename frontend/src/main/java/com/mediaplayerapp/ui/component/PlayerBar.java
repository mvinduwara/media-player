package com.mediaplayerapp.ui.component;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;


import java.io.File;
import java.io.FileInputStream;

public class PlayerBar extends HBox {

    private final MediaPlayerService playerService;

    private final Label titleLabel       = new Label("No track selected");
    private final Label artistLabel      = new Label("");
    private final Label currentTimeLabel = new Label("0:00");
    private final Label totalTimeLabel   = new Label("0:00");
    private final Slider seekSlider      = new Slider(0, 1, 0);
    private final Slider volumeSlider    = new Slider(0, 1, 0.7);
    private final Button playPauseBtn    = new Button();
    private final Button prevBtn         = new Button();
    private final Button nextBtn         = new Button();
    private final Button shuffleBtn      = new Button();
    private final Button repeatBtn       = new Button();
    private final Button muteBtn         = new Button();
    private final Button favouriteBtn    = new Button();
    private final Button queueBtn        = new Button();
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
        HBox left   = buildLeftSection();
        HBox center = buildCenterSection();
        HBox right  = buildRightSection();

        HBox.setHgrow(left,   Priority.ALWAYS);
        HBox.setHgrow(center, Priority.NEVER);
        HBox.setHgrow(right,  Priority.ALWAYS);
        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(left, center, right);
    }

    private HBox buildLeftSection() {
        HBox box = new HBox(14);
        box.setAlignment(Pos.CENTER_LEFT);

        coverArtView.setFitWidth(54);
        coverArtView.setFitHeight(54);
        coverArtView.setPreserveRatio(false);
        coverArtView.setSmooth(true);
        Rectangle clip = new Rectangle(54, 54);
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        coverArtView.setClip(clip);

        StackPane coverBox = new StackPane();
        coverBox.setMinSize(54, 54);
        coverBox.setMaxSize(54, 54);
        coverBox.getStyleClass().add("player-cover-box");
        coverBox.setAlignment(Pos.CENTER);
        coverBox.getChildren().addAll(FXUtils.icon("fas-music", 18, "#5A5A6A"), coverArtView);

        titleLabel.getStyleClass().add("player-track-title");
        titleLabel.setMaxWidth(200);
        artistLabel.getStyleClass().add("player-track-artist");
        artistLabel.setMaxWidth(200);

        VBox info = new VBox(2, titleLabel, artistLabel);
        info.setAlignment(Pos.CENTER_LEFT);

        favouriteBtn.getStyleClass().add("btn-favourite");
        favouriteBtn.setGraphic(FXUtils.icon("far-heart", 14, "#5A5A6A"));
        FXUtils.tooltip(favouriteBtn, "Add to favourites");

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

        shuffleBtn.getStyleClass().add("btn-transport");
        shuffleBtn.setGraphic(FXUtils.icon("fas-shuffle", 14, "#9898A6"));
        FXUtils.tooltip(shuffleBtn, "Shuffle");

        prevBtn.getStyleClass().add("btn-step");
        prevBtn.setGraphic(FXUtils.icon("fas-backward-step", 18, "#EDEDF0"));
        FXUtils.tooltip(prevBtn, "Previous");

        playPauseBtn.getStyleClass().add("btn-play");
        playPauseBtn.setGraphic(FXUtils.icon("fas-play", 16, "white"));
        FXUtils.tooltip(playPauseBtn, "Play");

        nextBtn.getStyleClass().add("btn-step");
        nextBtn.setGraphic(FXUtils.icon("fas-forward-step", 18, "#EDEDF0"));
        FXUtils.tooltip(nextBtn, "Next");

        repeatBtn.getStyleClass().add("btn-transport");
        repeatBtn.setGraphic(FXUtils.icon("fas-repeat", 14, "#9898A6"));
        FXUtils.tooltip(repeatBtn, "Repeat");

        controls.getChildren().addAll(shuffleBtn, prevBtn, playPauseBtn, nextBtn, repeatBtn);

        seekSlider.getStyleClass().add("seek-slider");
        seekSlider.setMin(0);
        seekSlider.setMax(1);
        seekSlider.setValue(0);
        HBox.setHgrow(seekSlider, Priority.ALWAYS);

        currentTimeLabel.getStyleClass().addAll("time-label", "current");
        totalTimeLabel.getStyleClass().add("time-label");

        HBox seekRow = new HBox(8, currentTimeLabel, seekSlider, totalTimeLabel);
        seekRow.setAlignment(Pos.CENTER);
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

        queueBtn.getStyleClass().add("btn-queue");
        queueBtn.setGraphic(FXUtils.icon("fas-list", 14, "#9898A6"));
        FXUtils.tooltip(queueBtn, "Queue");

        muteBtn.getStyleClass().add("btn-mute");
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

        muteBtn.setOnAction(e -> {
            playerService.setMuted(!playerService.isMuted());
            updateMuteBtn();
        });

        favouriteBtn.setOnAction(e -> {
            Track t = playerService.getCurrentTrack();
            if (t == null) return;
            boolean newState = !t.isFavourite();
            t.setFavourite(newState);
            updateFavouriteBtn(newState);
        });

        playerService.playingProperty().addListener((obs, was, isNow) -> updatePlayBtn(isNow));

        playerService.currentTrackProperty().addListener((obs, old, track) -> {
            if (track != null) {
                titleLabel.setText(track.getTitle());
                artistLabel.setText(track.getArtist());
                loadCover(track);
                updateFavouriteBtn(track.isFavourite());
            } else {
                titleLabel.setText("No track selected");
                artistLabel.setText("");
                coverArtView.setImage(null);
                updateFavouriteBtn(false);
            }
        });

        playerService.currentTimeProperty().addListener((obs, old, millis) -> {
            if (!seekDragging) {
                double total = playerService.totalTimeProperty().get();
                if (total > 0) seekSlider.setValue(millis.doubleValue() / total);
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
    }

    private void updatePlayBtn(boolean playing) {
        playPauseBtn.setGraphic(playing
                ? FXUtils.icon("fas-pause", 16, "white")
                : FXUtils.icon("fas-play",  16, "white"));
        FXUtils.tooltip(playPauseBtn, playing ? "Pause" : "Play");
    }

    private void updateShuffleBtn() {
        boolean on = playerService.shuffleProperty().get();
        shuffleBtn.getStyleClass().remove("active");
        if (on) shuffleBtn.getStyleClass().add("active");
        shuffleBtn.setGraphic(FXUtils.icon("fas-shuffle", 14, on ? "#9D6FEF" : "#9898A6"));
        FXUtils.tooltip(shuffleBtn, on ? "Shuffle on" : "Shuffle off");
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
                repeatBtn.setGraphic(FXUtils.icon("fas-repeat", 14, "#C084FC"));
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
        muteBtn.getStyleClass().remove("muted");
        if (muted) muteBtn.getStyleClass().add("muted");
        muteBtn.setGraphic(FXUtils.icon(
                muted ? "fas-volume-xmark" : "fas-volume-high", 14,
                muted ? "#EF4444" : "#9898A6"
        ));
        FXUtils.tooltip(muteBtn, muted ? "Unmute" : "Mute");
    }

    private void updateFavouriteBtn(boolean fav) {
        favouriteBtn.getStyleClass().remove("active");
        if (fav) favouriteBtn.getStyleClass().add("active");
        favouriteBtn.setGraphic(FXUtils.icon(
                fav ? "fas-heart" : "far-heart", 14,
                fav ? "#7C3AED" : "#5A5A6A"
        ));
        FXUtils.tooltip(favouriteBtn, fav ? "Remove from favourites" : "Add to favourites");
    }

    private void loadCover(Track track) {
        String path = track.getCoverArtPath();
        if (path != null && !path.isEmpty()) {
            try {
                File f = new File(path);
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
