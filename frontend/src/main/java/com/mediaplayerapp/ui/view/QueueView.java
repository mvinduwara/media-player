package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.ui.component.TrackCard;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class QueueView extends VBox {

    private final MediaPlayerService playerService;
    private ListView<Track> queueList;
    private final Label countLabel = new Label("0 tracks");

    public QueueView(MediaPlayerService playerService) {
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
        Label title = new Label("Queue");
        title.getStyleClass().add("page-title");
        countLabel.getStyleClass().add("label-secondary");
        countLabel.setStyle("-fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, countLabel);

        Region spacer = FXUtils.spacer();

        Button clearBtn = new Button("Clear Queue");
        clearBtn.getStyleClass().add("btn-ghost");
        clearBtn.setGraphic(FXUtils.icon("fas-trash", 13, "#9898A6"));
        clearBtn.setGraphicTextGap(8);
        clearBtn.setOnAction(e -> {
            playerService.getQueue().clear();
            updateCount();
        });

        header.getChildren().addAll(titleBox, spacer, clearBtn);
        return header;
    }

    private VBox buildList() {
        queueList = new ListView<>(playerService.getQueue());
        queueList.getStyleClass().add("track-table");
        VBox.setVgrow(queueList, Priority.ALWAYS);
        queueList.setPlaceholder(buildEmptyState());

        queueList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Track track, boolean empty) {
                super.updateItem(track, empty);
                if (empty || track == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean playing = playerService.getCurrentTrack() != null
                            && playerService.getCurrentTrack().getId() == track.getId();

                    HBox row = new HBox(12);
                    row.setAlignment(Pos.CENTER_LEFT);

                    Label indexLbl = new Label(String.valueOf(getIndex() + 1));
                    indexLbl.setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px; -fx-min-width: 28;");

                    TrackCard card = new TrackCard(track, false);
                    card.setPlaying(playing);
                    HBox.setHgrow(card, Priority.ALWAYS);

                    Button removeBtn = new Button();
                    removeBtn.getStyleClass().add("btn-icon");
                    removeBtn.setGraphic(FXUtils.icon("fas-xmark", 12, "#5A5A6A"));
                    removeBtn.setOpacity(0);
                    removeBtn.setOnAction(e -> {
                        playerService.getQueue().remove(track);
                        updateCount();
                    });

                    row.setOnMouseEntered(e -> removeBtn.setOpacity(1));
                    row.setOnMouseExited(e -> removeBtn.setOpacity(0));

                    row.getChildren().addAll(indexLbl, card, removeBtn);
                    setGraphic(row);
                    setPadding(new Insets(4, 12, 4, 12));
                    setStyle("-fx-background-color: transparent;");
                    setContextMenu(buildRowContextMenu(track));
                }
            }
        });

        queueList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Track t = queueList.getSelectionModel().getSelectedItem();
                if (t != null) {
                    int idx = playerService.getQueue().indexOf(t);
                    playerService.setQueue(playerService.getQueue(), idx);
                }
            }
        });

        playerService.getQueue().addListener((javafx.collections.ListChangeListener<Track>) c -> updateCount());
        playerService.currentTrackProperty().addListener((obs, old, t) -> queueList.refresh());

        VBox wrapper = new VBox(queueList);
        VBox.setVgrow(queueList, Priority.ALWAYS);
        return wrapper;
    }

    private ContextMenu buildRowContextMenu(Track track) {
        ContextMenu menu = new ContextMenu();
        MenuItem playNow = new MenuItem("Play Now");
        playNow.setOnAction(e -> {
            int idx = playerService.getQueue().indexOf(track);
            if (idx >= 0) playerService.setQueue(playerService.getQueue(), idx);
        });
        MenuItem removeItem = new MenuItem("Remove from Queue");
        removeItem.setOnAction(e -> {
            playerService.getQueue().remove(track);
            updateCount();
        });
        menu.getItems().addAll(playNow, new SeparatorMenuItem(), removeItem);
        return menu;
    }

    private VBox buildEmptyState() {
        VBox empty = new VBox(12);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));
        FontIcon icon = FXUtils.icon("fas-list", 48, "#2E2E33");
        Label title = new Label("Queue is empty");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #5A5A6A;");
        Label sub = new Label("Play tracks or playlists to fill the queue");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #3A3A44;");
        empty.getChildren().addAll(icon, title, sub);
        return empty;
    }

    private void updateCount() {
        int n = playerService.getQueue().size();
        countLabel.setText(n + " track" + (n == 1 ? "" : "s"));
    }
}