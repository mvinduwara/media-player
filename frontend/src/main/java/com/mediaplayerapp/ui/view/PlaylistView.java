package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.PlaylistService;
import com.mediaplayerapp.ui.component.SearchBar;
import com.mediaplayerapp.ui.component.TrackCard;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.function.Consumer;

public class PlaylistView extends VBox {

    private final MediaPlayerService playerService;
    private final PlaylistService playlistService;

    private Playlist currentPlaylist;
    private final ObservableList<Track> playlistTracks = FXCollections.observableArrayList();
    private final FilteredList<Track> filteredTracks = new FilteredList<>(playlistTracks, t -> true);

    private final Label playlistNameLabel = new Label("Select a playlist");
    private final Label trackCountLabel = new Label("0 tracks");
    private final Label durationLabel = new Label("");
    private ListView<Track> trackListView;

    private Consumer<Playlist> onPlaylistDeleted;
    private Consumer<Playlist> onPlaylistRenamed;

    public PlaylistView(MediaPlayerService playerService, PlaylistService playlistService) {
        this.playerService = playerService;
        this.playlistService = playlistService;
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
    }

    private void buildUI() {
        getChildren().addAll(buildHeader(), buildTrackList());
    }

    private VBox buildHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(24, 28, 16, 28));

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setMinSize(56, 56);
        iconBox.setMaxSize(56, 56);
        iconBox.setStyle("-fx-background-color: rgba(124, 58, 237, 0.18); -fx-background-radius: 12;");
        iconBox.getChildren().add(FXUtils.icon("fas-list-music", 24, "#7C3AED"));

        VBox titleInfo = new VBox(4);
        Label typeLabel = new Label("PLAYLIST");
        typeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #5A5A6A;");
        playlistNameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 700; -fx-text-fill: #EDEDF0;");

        HBox metaRow = new HBox(10);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        trackCountLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9898A6;");
        durationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A5A6A;");
        Label sep = new Label("·");
        sep.setStyle("-fx-text-fill: #5A5A6A;");
        metaRow.getChildren().addAll(trackCountLabel, sep, durationLabel);

        titleInfo.getChildren().addAll(typeLabel, playlistNameLabel, metaRow);
        HBox.setHgrow(titleInfo, Priority.ALWAYS);

        Region spacer = FXUtils.spacer();

        Button playBtn = new Button("Play All");
        playBtn.getStyleClass().add("btn-primary");
        playBtn.setGraphic(FXUtils.icon("fas-play", 12, "white"));
        playBtn.setGraphicTextGap(8);
        playBtn.setOnAction(e -> playAll());

        Button shuffleBtn = new Button("Shuffle");
        shuffleBtn.getStyleClass().add("btn-ghost");
        shuffleBtn.setGraphic(FXUtils.icon("fas-shuffle", 13, "#9898A6"));
        shuffleBtn.setGraphicTextGap(8);
        shuffleBtn.setOnAction(e -> shufflePlay());

        Button menuBtn = new Button();
        menuBtn.getStyleClass().add("btn-icon");
        menuBtn.setGraphic(FXUtils.icon("fas-ellipsis", 14, "#9898A6"));
        menuBtn.setOnAction(e -> showPlaylistMenu(menuBtn));

        topRow.getChildren().addAll(iconBox, titleInfo, spacer, playBtn, shuffleBtn, menuBtn);

        SearchBar searchBar = new SearchBar("Search in playlist…");
        searchBar.setOnSearch(this::filterTracks);

        header.getChildren().addAll(topRow, searchBar);
        return header;
    }

    private VBox buildTrackList() {
        trackListView = new ListView<>(filteredTracks);
        trackListView.getStyleClass().addAll("track-table");
        trackListView.setPlaceholder(buildEmptyState());
        VBox.setVgrow(trackListView, Priority.ALWAYS);

        trackListView.setCellFactory(lv -> new ListCell<>() {
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
                    TrackCard card = new TrackCard(track, true);
                    card.setPlaying(playing);
                    card.setPrefWidth(lv.getWidth() - 20);
                    setGraphic(card);
                    setPadding(new Insets(4, 12, 4, 12));
                    setStyle("-fx-background-color: transparent;");
                    setContextMenu(buildTrackContextMenu(track));
                }
            }
        });

        trackListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Track t = trackListView.getSelectionModel().getSelectedItem();
                if (t != null) playFrom(t);
            }
        });

        playerService.currentTrackProperty().addListener((obs, old, t) -> trackListView.refresh());

        VBox wrapper = new VBox(trackListView);
        VBox.setVgrow(trackListView, Priority.ALWAYS);
        return wrapper;
    }

    private ContextMenu buildTrackContextMenu(Track track) {
        ContextMenu menu = new ContextMenu();

        MenuItem playNow = new MenuItem("Play Now");
        playNow.setGraphic(FXUtils.icon("fas-play", 12, "#9898A6"));
        playNow.setOnAction(e -> playFrom(track));

        MenuItem addToQueue = new MenuItem("Add to Queue");
        addToQueue.setGraphic(FXUtils.icon("fas-list", 12, "#9898A6"));
        addToQueue.setOnAction(e -> playerService.addToQueue(track));

        MenuItem removeItem = new MenuItem("Remove from Playlist");
        removeItem.setGraphic(FXUtils.icon("fas-trash", 12, "#EF4444"));
        removeItem.setStyle("-fx-text-fill: #EF4444;");
        removeItem.setOnAction(e -> {
            if (currentPlaylist != null) {
                playlistService.removeTrackFromPlaylist(currentPlaylist.getId(), track.getId());
                playlistTracks.remove(track);
                updateMeta();
            }
        });

        menu.getItems().addAll(playNow, addToQueue, new SeparatorMenuItem(), removeItem);
        return menu;
    }

    private void showPlaylistMenu(Button anchor) {
        ContextMenu menu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename Playlist");
        renameItem.setGraphic(FXUtils.icon("fas-pen", 12, "#9898A6"));
        renameItem.setOnAction(e -> renamePlaylist());

        MenuItem deleteItem = new MenuItem("Delete Playlist");
        deleteItem.setGraphic(FXUtils.icon("fas-trash", 12, "#EF4444"));
        deleteItem.setStyle("-fx-text-fill: #EF4444;");
        deleteItem.setOnAction(e -> deletePlaylist());

        menu.getItems().addAll(renameItem, new SeparatorMenuItem(), deleteItem);
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 4);
    }

    private void renamePlaylist() {
        if (currentPlaylist == null) return;
        TextInputDialog dialog = new TextInputDialog(currentPlaylist.getName());
        dialog.setTitle("Rename Playlist");
        dialog.setHeaderText(null);
        dialog.setContentText("New name:");
        dialog.getDialogPane().setStyle("-fx-background-color: #1C1C1F;");
        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                playlistService.renamePlaylist(currentPlaylist.getId(), newName);
                currentPlaylist.setName(newName);
                playlistNameLabel.setText(newName);
                if (onPlaylistRenamed != null) onPlaylistRenamed.accept(currentPlaylist);
            }
        });
    }

    private void deletePlaylist() {
        if (currentPlaylist == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Playlist");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + currentPlaylist.getName() + "\"? This cannot be undone.");
        confirm.getDialogPane().setStyle("-fx-background-color: #1C1C1F; -fx-text-fill: #EDEDF0;");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                playlistService.deletePlaylist(currentPlaylist.getId());
                if (onPlaylistDeleted != null) onPlaylistDeleted.accept(currentPlaylist);
            }
        });
    }

    private void filterTracks(String query) {
        if (query == null || query.isBlank()) {
            filteredTracks.setPredicate(t -> true);
        } else {
            String lower = query.toLowerCase();
            filteredTracks.setPredicate(t ->
                    t.getTitle().toLowerCase().contains(lower) ||
                            t.getArtist().toLowerCase().contains(lower)
            );
        }
    }

    private void playAll() {
        if (!playlistTracks.isEmpty()) {
            playerService.setQueue(List.copyOf(playlistTracks), 0);
        }
    }

    private void shufflePlay() {
        if (!playlistTracks.isEmpty()) {
            playerService.shuffleProperty().set(false);
            playerService.setQueue(List.copyOf(playlistTracks), 0);
            playerService.toggleShuffle();
        }
    }

    private void playFrom(Track track) {
        int idx = playlistTracks.indexOf(track);
        playerService.setQueue(List.copyOf(playlistTracks), idx);
    }

    private void updateMeta() {
        trackCountLabel.setText(playlistTracks.size() + " tracks");
        if (currentPlaylist != null) {
            currentPlaylist.getTracks().setAll(playlistTracks);
            durationLabel.setText(currentPlaylist.getFormattedTotalDuration());
        }
    }

    private Label buildEmptyState() {
        Label lbl = new Label("This playlist is empty.\nAdd tracks from your library.");
        lbl.setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 14px; -fx-text-alignment: center;");
        lbl.setWrapText(true);
        return lbl;
    }

    public void loadPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        playlistNameLabel.setText(playlist.getName());
        List<Track> tracks = playlistService.getPlaylistWithTracks(playlist.getId()).getTracks();
        playlistTracks.setAll(tracks);
        filteredTracks.setPredicate(t -> true);
        updateMeta();
        FXUtils.fadeIn(this, 180);
    }

    public void setOnPlaylistDeleted(Consumer<Playlist> handler) { this.onPlaylistDeleted = handler; }
    public void setOnPlaylistRenamed(Consumer<Playlist> handler) { this.onPlaylistRenamed = handler; }
}