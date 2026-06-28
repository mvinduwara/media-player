package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.dao.PlaylistDao;
import com.mediaplayerapp.dao.SettingsDao;
import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.db.DatabaseManager;
import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.*;
import com.mediaplayerapp.ui.MainWindow;
import com.mediaplayerapp.ui.component.PlayerBar;
import com.mediaplayerapp.ui.component.SideNav;
import com.mediaplayerapp.ui.view.*;
import javafx.application.Platform;
import javafx.scene.control.TextInputDialog;

import java.util.List;

public class MainController {

    private final MainWindow mainWindow;

    // ── Infrastructure ────────────────────────────────
    private final DatabaseManager dbManager;
    private final TrackDao trackDao;
    private final PlaylistDao playlistDao;
    private final SettingsDao settingsDao;

    // ── Services ──────────────────────────────────────
    private final MediaPlayerService playerService;
    private final LibraryScannerService scannerService;
    private final PlaylistService playlistService;
    private final EqualizerService equalizerService;
    private final SettingsService settingsService;

    // ── Sub-controllers ───────────────────────────────
    private LibraryController libraryController;
    private PlayerController playerController;
    private PlaylistController playlistController;
    private EqualizerController equalizerController;
    private SettingsController settingsController;

    // ── Views ─────────────────────────────────────────
    private LibraryView libraryView;
    private NowPlayingView nowPlayingView;
    private PlaylistView playlistView;
    private QueueView queueView;
    private EqualizerView equalizerView;
    private FavouritesView favouritesView;
    private SettingsView settingsView;

    public MainController(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        dbManager = DatabaseManager.getInstance();
        dbManager.init();

        trackDao    = new TrackDao();
        playlistDao = new PlaylistDao();
        settingsDao = new SettingsDao();

        settingsService  = new SettingsService(settingsDao);
        settingsService.load();

        playerService    = new MediaPlayerService(trackDao);
        scannerService   = new LibraryScannerService(trackDao);
        playlistService  = new PlaylistService(playlistDao, trackDao);
        equalizerService = new EqualizerService();
    }

    public void initialize() {
        buildViews();
        buildControllers();
        initControllers();
        wireSideNav();
        wireWindowTitle();

        PlayerBar playerBar = new PlayerBar(playerService);
        mainWindow.setPlayerBar(playerBar);
        mainWindow.showView(libraryView);
    }

    // ── View construction ─────────────────────────────

    private void buildViews() {
        libraryView    = new LibraryView(playerService, scannerService, playlistService);
        nowPlayingView = new NowPlayingView(playerService);
        playlistView   = new PlaylistView(playerService, playlistService);
        queueView      = new QueueView(playerService);
        equalizerView  = new EqualizerView(equalizerService);
        favouritesView = new FavouritesView(playerService);
        settingsView   = new SettingsView(settingsService);
    }

    // ── Controller construction ───────────────────────

    private void buildControllers() {
        libraryController = new LibraryController(
                libraryView, trackDao, playerService, scannerService, playlistService
        );

        playerController = new PlayerController(
                playerService, equalizerService, settingsService, trackDao
        );

        playlistController = new PlaylistController(
                playlistView, playlistService, playerService, settingsService, trackDao
        );

        equalizerController = new EqualizerController(
                equalizerView, equalizerService, settingsService
        );

        settingsController = new SettingsController(
                settingsView, settingsService, playerService, scannerService
        );
    }

    // ── Controller initialization ─────────────────────

    private void initControllers() {
        playerController.initialize();

        playerController.setOnTrackChanged(track -> {
            mainWindow.setTitle(track.getTitle() + " — " + track.getArtist());
            libraryView.setAvailablePlaylists(playlistController.getPlaylists());
        });

        libraryController.initialize();
        libraryController.refreshPlaylists(playlistController.getPlaylists());

        settingsController.initialize();
        settingsController.setOnSettingsSaved(settings -> {
            settingsController.triggerDefaultFolderScan(v ->
                    Platform.runLater(libraryController::loadAllTracks)
            );
        });

        playlistController.initialize();
        playlistController.setOnPlaylistsChanged(playlists -> {
            mainWindow.getSideNav().setPlaylists(playlists);
            libraryController.refreshPlaylists(playlists);
        });
        playlistController.setOnPlaylistSelected(pl ->
                mainWindow.showView(playlistView)
        );

        List<Playlist> initialPlaylists = playlistController.getPlaylists();
        mainWindow.getSideNav().setPlaylists(initialPlaylists);
        libraryController.refreshPlaylists(initialPlaylists);

        equalizerController.initialize();

        libraryView.setOnTrackPlay(track -> mainWindow.showView(nowPlayingView));

        playlistView.setOnPlaylistDeleted(pl -> {
            playlistController.getPlaylists().remove(pl);
            mainWindow.getSideNav().setPlaylists(playlistController.getPlaylists());
            mainWindow.showView(libraryView);
        });

        playlistView.setOnPlaylistRenamed(pl ->
                mainWindow.getSideNav().setPlaylists(playlistController.getPlaylists())
        );

        settingsController.triggerDefaultFolderScan(v ->
                Platform.runLater(libraryController::loadAllTracks)
        );
    }

    // ── Side nav wiring ───────────────────────────────

    private void wireSideNav() {
        SideNav nav = mainWindow.getSideNav();

        nav.setOnNavSelect(item -> {
            switch (item) {
                case LIBRARY     -> mainWindow.showView(libraryView);
                case NOW_PLAYING -> mainWindow.showView(nowPlayingView);
                case QUEUE       -> mainWindow.showView(queueView);
                case FAVOURITES  -> {
                    favouritesView.setTracks(trackDao.findFavourites());
                    mainWindow.showView(favouritesView);
                }
                case EQUALIZER   -> mainWindow.showView(equalizerView);
                case SETTINGS    -> mainWindow.showView(settingsView);
            }
        });

        nav.setOnPlaylistSelect(pl -> playlistController.selectPlaylist(pl));

        nav.setOnNewPlaylist(() -> {
            TextInputDialog dialog = new TextInputDialog("New Playlist");
            dialog.setTitle("Create Playlist");
            dialog.setHeaderText(null);
            dialog.setContentText("Playlist name:");
            dialog.getDialogPane().setStyle("-fx-background-color: #1C1C1F;");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.isBlank()) {
                    playlistController.createPlaylist(name);
                }
            });
        });
    }

    // ── Window title binding ──────────────────────────

    private void wireWindowTitle() {
        playerService.currentTrackProperty().addListener((obs, old, t) -> {
            if (t != null) mainWindow.setTitle(t.getTitle() + " — " + t.getArtist());
            else           mainWindow.setTitle("Waveline");
        });
    }

    // ── Shutdown ──────────────────────────────────────

    public void onShutdown() {
        playerController.saveStateOnShutdown();
        nowPlayingView.dispose();
        playerService.dispose();
        scannerService.shutdown();
        dbManager.close();
    }
}
