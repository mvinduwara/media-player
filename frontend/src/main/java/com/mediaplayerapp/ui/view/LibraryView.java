package com.mediaplayerapp.ui.view;

import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.LibraryScannerService;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.PlaylistService;
import com.mediaplayerapp.ui.component.SearchBar;
import com.mediaplayerapp.ui.component.TrackCard;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;


import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class LibraryView extends VBox {

    private final MediaPlayerService playerService;
    private final LibraryScannerService scannerService;
    private final PlaylistService playlistService;

    private final ObservableList<Track> allTracks = FXCollections.observableArrayList();
    private final FilteredList<Track> filteredTracks = new FilteredList<>(allTracks, t -> true);

    private TableView<Track> trackTable;
    private final Label trackCountLabel = new Label("0 tracks");
    private final Label scanStatusLabel = new Label("");
    private final ProgressBar scanProgress = new ProgressBar(0);
    private Consumer<Track> onTrackPlay;
    private List<Playlist> availablePlaylists;

    public LibraryView(MediaPlayerService playerService,
                       LibraryScannerService scannerService,
                       PlaylistService playlistService) {
        this.playerService = playerService;
        this.scannerService = scannerService;
        this.playlistService = playlistService;
        getStyleClass().add("content-area");
        setFillWidth(true);
        VBox.setVgrow(this, Priority.ALWAYS);
        buildUI();
        bindScanner();
    }

    private void buildUI() {
        getChildren().addAll(buildHeader(), buildScanBar(), buildTable());
    }

    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.getStyleClass().add("content-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24, 28, 16, 28));

        VBox titleBox = new VBox(3);
        Label title = new Label("Library");
        title.getStyleClass().add("page-title");
        trackCountLabel.getStyleClass().add("label-secondary");
        trackCountLabel.setStyle("-fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, trackCountLabel);

        Region spacer = FXUtils.spacer();

        SearchBar searchBar = new SearchBar("Search tracks, artists, albums…");
        searchBar.setOnSearch(this::filterTracks);

        Button addFolderBtn = new Button("Add Folder");
        addFolderBtn.getStyleClass().add("btn-primary");
        addFolderBtn.setGraphic(FXUtils.icon("fas-folder-plus", 13, "white"));
        addFolderBtn.setGraphicTextGap(8);
        addFolderBtn.setOnAction(e -> openFolderChooser());

        Button addFileBtn = new Button();
        addFileBtn.getStyleClass().add("btn-ghost");
        addFileBtn.setGraphic(FXUtils.icon("fas-file-audio", 14, "#9898A6"));
        FXUtils.tooltip(addFileBtn, "Add files");

        header.getChildren().addAll(titleBox, spacer, searchBar, addFileBtn, addFolderBtn);
        return header;
    }

    private HBox buildScanBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 28, 8, 28));
        bar.setVisible(false);
        bar.managedProperty().bind(bar.visibleProperty());

        scanProgress.getStyleClass().add("progress-bar");
        scanProgress.setPrefWidth(200);
        scanProgress.setMinHeight(6);
        scanProgress.setMaxHeight(6);
        scanProgress.progressProperty().bind(scannerService.scanProgressProperty());

        scanStatusLabel.getStyleClass().add("label-secondary");
        scanStatusLabel.setStyle("-fx-font-size: 12px;");
        scanStatusLabel.textProperty().bind(scannerService.scanStatusProperty());

        scannerService.scanningProperty().addListener((obs, was, scanning) -> {
            bar.setVisible(scanning);
        });

        bar.getChildren().addAll(scanProgress, scanStatusLabel);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTable() {
        trackTable = new TableView<>(filteredTracks);
        trackTable.getStyleClass().add("track-table");
        trackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        trackTable.setPlaceholder(buildEmptyState());
        VBox.setVgrow(trackTable, Priority.ALWAYS);

        TableColumn<Track, String> numCol = new TableColumn<>("#");
        numCol.setMinWidth(44);
        numCol.setMaxWidth(44);
        numCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Track t = getTableView().getItems().get(getIndex());
                    boolean isPlaying = playerService.getCurrentTrack() != null
                            && playerService.getCurrentTrack().getId() == t.getId();
                    if (isPlaying) {
                        setGraphic(FXUtils.icon("fas-volume-high", 11, "#9D6FEF"));
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(String.valueOf(getIndex() + 1));
                        setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px;");
                    }
                }
            }
        });

        TableColumn<Track, Void> titleCol = new TableColumn<>("TITLE");
        titleCol.setMinWidth(240);
        titleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Track t = getTableView().getItems().get(getIndex());
                    boolean playing = playerService.getCurrentTrack() != null
                            && playerService.getCurrentTrack().getId() == t.getId();
                    TrackCard card = new TrackCard(t, false);
                    card.setPlaying(playing);
                    setGraphic(card);
                    setPadding(new Insets(6, 0, 6, 4));
                }
            }
        });

        TableColumn<Track, String> artistCol = new TableColumn<>("ARTIST");
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        artistCol.setMinWidth(160);
        artistCol.setCellFactory(col -> styledCell("#9898A6"));

        TableColumn<Track, String> albumCol = new TableColumn<>("ALBUM");
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        albumCol.setMinWidth(160);
        albumCol.setCellFactory(col -> styledCell("#9898A6"));

        TableColumn<Track, String> genreCol = new TableColumn<>("GENRE");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setMinWidth(100);
        genreCol.setCellFactory(col -> styledCell("#5A5A6A"));

        TableColumn<Track, Void> durCol = new TableColumn<>("TIME");
        durCol.setMinWidth(60);
        durCol.setMaxWidth(70);
        durCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); setText(null);
                } else {
                    Track t = getTableView().getItems().get(getIndex());
                    setText(t.getFormattedDuration());
                    setStyle("-fx-text-fill: #5A5A6A; -fx-font-size: 12px; -fx-font-family: 'Consolas', monospace;");
                    setAlignment(Pos.CENTER_RIGHT);
                    setPadding(new Insets(0, 12, 0, 0));
                }
            }
        });

        trackTable.getColumns().addAll(numCol, titleCol, artistCol, albumCol, genreCol, durCol);

        trackTable.setRowFactory(tv -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    playTrack(row.getItem());
                }
            });
            row.setContextMenu(buildContextMenu(row));
            return row;
        });

        playerService.currentTrackProperty().addListener((obs, old, t) -> trackTable.refresh());

        VBox wrapper = new VBox(trackTable);
        VBox.setVgrow(trackTable, Priority.ALWAYS);
        wrapper.setPadding(new Insets(0, 0, 0, 0));
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private TableCell<Track, String> styledCell(String color) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
                }
            }
        };
    }

    private ContextMenu buildContextMenu(TableRow<Track> row) {
        ContextMenu menu = new ContextMenu();

        MenuItem playItem = new MenuItem("Play Now");
        playItem.setGraphic(FXUtils.icon("fas-play", 12, "#9898A6"));
        playItem.setOnAction(e -> playTrack(row.getItem()));

        MenuItem queueItem = new MenuItem("Add to Queue");
        queueItem.setGraphic(FXUtils.icon("fas-list", 12, "#9898A6"));
        queueItem.setOnAction(e -> playerService.addToQueue(row.getItem()));

        MenuItem favItem = new MenuItem("Add to Favourites");
        favItem.setGraphic(FXUtils.icon("fas-heart", 12, "#9898A6"));
        favItem.setOnAction(e -> row.getItem().setFavourite(true));

        Menu addToPlaylistMenu = new Menu("Add to Playlist");
        addToPlaylistMenu.setGraphic(FXUtils.icon("fas-list-music", 12, "#9898A6"));

        if (availablePlaylists != null) {
            for (Playlist pl : availablePlaylists) {
                MenuItem pItem = new MenuItem(pl.getName());
                pItem.setOnAction(e -> playlistService.addTrackToPlaylist(pl.getId(), row.getItem().getId()));
                addToPlaylistMenu.getItems().add(pItem);
            }
        }

        SeparatorMenuItem sep = new SeparatorMenuItem();
        MenuItem infoItem = new MenuItem("Track Info");
        infoItem.setGraphic(FXUtils.icon("fas-circle-info", 12, "#9898A6"));

        menu.getItems().addAll(playItem, queueItem, sep, favItem, addToPlaylistMenu, new SeparatorMenuItem(), infoItem);
        menu.setOnShowing(e -> menu.setStyle("-fx-background-color: #242428;"));
        return menu;
    }

    private VBox buildEmptyState() {
        VBox empty = new VBox(16);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));

        Label icon = FXUtils.icon("fas-music", 48, "#2E2E33");
        Label title = new Label("Your library is empty");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #5A5A6A;");
        Label sub = new Label("Click \"Add Folder\" to scan your music collection");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #3A3A44;");

        empty.getChildren().addAll(icon, title, sub);
        return empty;
    }

    private void openFolderChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Music Folder");
        Window window = getScene().getWindow();
        File selected = chooser.showDialog(window);
        if (selected != null) {
            scannerService.scanFolder(selected,
                    track -> Platform.runLater(() -> {
                        allTracks.add(track);
                        updateTrackCount();
                    }),
                    () -> Platform.runLater(this::updateTrackCount)
            );
        }
    }

    private void playTrack(Track track) {
        List<Track> queue = filteredTracks;
        playerService.setQueue(queue, filteredTracks.indexOf(track));
        if (onTrackPlay != null) onTrackPlay.accept(track);
    }

    private void filterTracks(String query) {
        if (query == null || query.isBlank()) {
            filteredTracks.setPredicate(t -> true);
        } else {
            String lower = query.toLowerCase();
            filteredTracks.setPredicate(t ->
                    t.getTitle().toLowerCase().contains(lower) ||
                            t.getArtist().toLowerCase().contains(lower) ||
                            t.getAlbum().toLowerCase().contains(lower)
            );
        }
        updateTrackCount();
    }

    private void updateTrackCount() {
        int showing = filteredTracks.size();
        int total = allTracks.size();
        if (showing == total) {
            trackCountLabel.setText(total + " tracks");
        } else {
            trackCountLabel.setText(showing + " of " + total + " tracks");
        }
    }

    private void bindScanner() {
        scannerService.scanningProperty().addListener((obs, was, scanning) -> {
            if (!scanning) updateTrackCount();
        });
    }

    public void setTracks(List<Track> tracks) {
        allTracks.setAll(tracks);
        updateTrackCount();
    }

    public void addTrack(Track track) {
        allTracks.add(track);
        updateTrackCount();
    }

    public void setAvailablePlaylists(List<Playlist> playlists) {
        this.availablePlaylists = playlists;
    }

    public void setOnTrackPlay(Consumer<Track> handler) {
        this.onTrackPlay = handler;
    }
}
