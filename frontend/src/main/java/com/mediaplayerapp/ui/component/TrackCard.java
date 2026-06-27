package com.mediaplayerapp.ui.component;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;

public class TrackCard extends HBox {

    private final Track track;
    private final ImageView coverView = new ImageView();
    private final Label titleLabel = new Label();
    private final Label artistLabel = new Label();
    private final Label albumLabel = new Label();
    private final Label durationLabel = new Label();
    private final FontIcon playingIcon = FXUtils.icon("fas-volume-high", 12, "#9D6FEF");

    public TrackCard(Track track, boolean showAlbum) {
        this.track = track;
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI(showAlbum);
    }

    private void buildUI(boolean showAlbum) {
        coverView.setFitWidth(36);
        coverView.setFitHeight(36);
        coverView.setPreserveRatio(false);
        coverView.setSmooth(true);

        Rectangle clip = new Rectangle(36, 36);
        clip.setArcWidth(6);
        clip.setArcHeight(6);
        coverView.setClip(clip);

        loadCover();

        StackPane coverStack = new StackPane(FXUtils.icon("fas-music", 13, "#5A5A6A"), coverView);
        coverStack.setMinSize(36, 36);
        coverStack.setMaxSize(36, 36);
        coverStack.setStyle("-fx-background-color: #242428; -fx-background-radius: 6;");
        coverStack.setAlignment(Pos.CENTER);
        coverStack.setPadding(new Insets(0));

        titleLabel.setText(track.getTitle());
        titleLabel.getStyleClass().add("label-primary");
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        titleLabel.setMaxWidth(220);

        artistLabel.setText(track.getArtist());
        artistLabel.getStyleClass().add("label-secondary");
        artistLabel.setStyle("-fx-font-size: 12px;");
        artistLabel.setMaxWidth(180);

        playingIcon.setVisible(false);

        VBox info = new VBox(2);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox titleRow = new HBox(8, titleLabel, playingIcon);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        info.getChildren().addAll(titleRow, artistLabel);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setPadding(new Insets(0, 0, 0, 12));

        durationLabel.setText(track.getFormattedDuration());
        durationLabel.getStyleClass().add("label-muted");
        durationLabel.setStyle("-fx-font-size: 12px; -fx-font-family: 'Consolas', monospace;");
        durationLabel.setMinWidth(40);
        durationLabel.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(coverStack, info, durationLabel);

        if (showAlbum) {
            albumLabel.setText(track.getAlbum());
            albumLabel.getStyleClass().add("label-secondary");
            albumLabel.setStyle("-fx-font-size: 12px;");
            albumLabel.setMinWidth(160);
            albumLabel.setMaxWidth(160);
            albumLabel.setPadding(new Insets(0, 16, 0, 16));
            getChildren().add(getChildren().size() - 1, albumLabel);
        }
    }

    private void loadCover() {
        String path = track.getCoverArtPath();
        if (path != null && !path.isEmpty()) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    Image img = new Image(new FileInputStream(f), 36, 36, false, true);
                    coverView.setImage(img);
                }
            } catch (Exception ignored) {}
        }
    }

    public void setPlaying(boolean playing) {
        playingIcon.setVisible(playing);
        if (playing) {
            titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #9D6FEF;");
        } else {
            titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        }
    }

    public Track getTrack() { return track; }
}