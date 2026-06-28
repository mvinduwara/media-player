package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.ui.component.TrackCard;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.util.List;

public class FavouritesView extends VBox {

    private final MediaPlayerService playerService;
    private final ObservableList<Track> favourites = FXCollections.observableArrayList();
    private ListView<Track> listView;
    private final Label countLabel = new Label("0 tracks");

    public FavouritesView(MediaPlayerService playerService) {
        this.playerService = playerService;
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
    }

    private void buildUI() {
        getChildren().addAll(buildHeader(), buildList());
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setPadding(new Insets(24, 28, 16, 28));
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Favourites");
        title.getStyleClass().add("page-title");
        countLabel.getStyleClass().add("label-secondary");
        countLabel.setStyle("-fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, countLabel);

        Region spacer = FXUtils.spacer();

        Button playAllBtn = new Button("Play All");
        playAllBtn.getStyleClass().add("btn-primary");
        playAllBtn.setGraphic(FXUtils.icon("fas-play", 12, "white"));
        playAllBtn.setGraphicTextGap(8);
        playAllBtn.setOnAction(e -> {
            if (!favourites.isEmpty()) playerService.setQueue(List.copyOf(favourites), 0);
        });

        header.getChildren().addAll(titleBox, spacer, playAllBtn);
        return header;
    }

    private VBox buildList() {
        listView = new ListView<>(favourites);
        listView.getStyleClass().add("track-table");
        listView.setPlaceholder(buildEmptyState());
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean playing = playerService.getCurrentTrack() != null
                            && playerService.getCurrentTrack().getId() == track.getId();
                    TrackCard card = new TrackCard(track, true);
                    card.setPlaying(playing);
                    card.setPrefWidth(lv.getWidth() - 20);

                    Button unfavBtn = new Button();
                    unfavBtn.getStyleClass().add("btn-icon");
                    unfavBtn.setGraphic(FXUtils.icon("fas-heart", 14, "#7C3AED"));
                    unfavBtn.setOpacity(0);
                    unfavBtn.setOnAction(e -> {
                        track.setFavourite(false);
                        favourites.remove(track);
                        updateCount();
                    });

                    HBox row = new HBox(card, unfavBtn);
                    row.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(card, Priority.ALWAYS);

                    row.setOnMouseEntered(ev -> unfavBtn.setOpacity(1));
                    row.setOnMouseExited(ev -> unfavBtn.setOpacity(0));

                    setGraphic(row);
                    setPadding(new Insets(4, 12, 4, 12));
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Track t = listView.getSelectionModel().getSelectedItem();
                if (t != null) {
                    int idx = favourites.indexOf(t);
                    playerService.setQueue(List.copyOf(favourites), idx);
                }
            }
        });

        playerService.currentTrackProperty().addListener((obs, old, t) -> listView.refresh());

        VBox wrapper = new VBox(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return wrapper;
    }

    private VBox buildEmptyState() {
        VBox empty = new VBox(16);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        Label icon = FXUtils.icon("far-heart", 48, "#2E2E33");
        Label title = new Label("No favourites yet");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #5A5A6A;");
        Label sub = new Label("Heart a track to add it here");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #3A3A44;");
        empty.getChildren().addAll(icon, title, sub);
        return empty;
    }

    private void updateCount() {
        int n = favourites.size();
        countLabel.setText(n + " track" + (n == 1 ? "" : "s"));
    }

    public void setTracks(List<Track> tracks) {
        favourites.setAll(tracks);
        updateCount();
    }
}
