package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.LibraryScannerService;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.PlaylistService;
import com.mediaplayerapp.ui.view.LibraryView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;

public class LibraryController {

    private final LibraryView view;
    private final TrackDao trackDao;
    private final MediaPlayerService playerService;
    private final LibraryScannerService scannerService;
    private final PlaylistService playlistService;

    private final ObservableList<Track> libraryTracks = FXCollections.observableArrayList();

    public LibraryController(LibraryView view,
                             TrackDao trackDao,
                             MediaPlayerService playerService,
                             LibraryScannerService scannerService,
                             PlaylistService playlistService) {
        this.view = view;
        this.trackDao = trackDao;
        this.playerService = playerService;
        this.scannerService = scannerService;
        this.playlistService = playlistService;
    }

    public void initialize() {
        loadAllTracks();
    }

    public void loadAllTracks() {
        List<Track> tracks = trackDao.findAll();
        libraryTracks.setAll(tracks);
        view.setTracks(tracks);
    }

    public void scanFolder(File folder) {
        scannerService.scanFolder(
                folder,
                track -> Platform.runLater(() -> {
                    libraryTracks.add(track);
                    view.addTrack(track);
                }),
                () -> Platform.runLater(this::onScanComplete)
        );
    }

    private void onScanComplete() {
        List<Track> refreshed = trackDao.findAll();
        libraryTracks.setAll(refreshed);
        view.setTracks(refreshed);
    }

    public void playTrack(Track track) {
        playerService.setQueue(List.copyOf(libraryTracks), libraryTracks.indexOf(track));
    }

    public void playAll() {
        if (!libraryTracks.isEmpty()) {
            playerService.setQueue(List.copyOf(libraryTracks), 0);
        }
    }

    public void addToQueue(Track track) {
        playerService.addToQueue(track);
    }

    public void toggleFavourite(Track track) {
        boolean newState = !track.isFavourite();
        track.setFavourite(newState);
        trackDao.toggleFavourite(track.getId(), newState);
    }

    public void addTrackToPlaylist(long playlistId, Track track) {
        playlistService.addTrackToPlaylist(playlistId, track.getId());
    }

    public void deleteTrack(Track track) {
        trackDao.delete(track.getId());
        libraryTracks.remove(track);
        view.setTracks(List.copyOf(libraryTracks));
    }

    public void refreshPlaylists(List<Playlist> playlists) {
        view.setAvailablePlaylists(playlists);
    }

    public ObservableList<Track> getLibraryTracks() {
        return libraryTracks;
    }

    public int getTrackCount() {
        return libraryTracks.size();
    }
}