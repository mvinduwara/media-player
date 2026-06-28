package com.mediaplayerapp.ui.controller;

import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.service.MediaPlayerService;
import com.mediaplayerapp.service.PlaylistService;
import com.mediaplayerapp.service.SettingsService;
import com.mediaplayerapp.ui.view.PlaylistView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Consumer;

public class PlaylistController {

    private final PlaylistView view;
    private final PlaylistService playlistService;
    private final MediaPlayerService playerService;
    private final SettingsService settingsService;
    private final TrackDao trackDao;

    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private Playlist activePlaylist;

    private Consumer<List<Playlist>> onPlaylistsChanged;
    private Consumer<Playlist> onPlaylistSelected;

    public PlaylistController(PlaylistView view,
                              PlaylistService playlistService,
                              MediaPlayerService playerService,
                              SettingsService settingsService,
                              TrackDao trackDao) {
        this.view = view;
        this.playlistService = playlistService;
        this.playerService = playerService;
        this.settingsService = settingsService;
        this.trackDao = trackDao;
    }

    public void initialize() {
        loadPlaylists();
        restoreLastActivePlaylist();

        view.setOnPlaylistDeleted(pl -> {
            playlists.remove(pl);
            if (activePlaylist != null && activePlaylist.getId() == pl.getId()) {
                activePlaylist = null;
            }
            notifyPlaylistsChanged();
        });

        view.setOnPlaylistRenamed(pl -> notifyPlaylistsChanged());
    }

    public void loadPlaylists() {
        List<Playlist> all = playlistService.getAllPlaylists();
        playlists.setAll(all);
    }

    public Playlist createPlaylist(String name) {
        Playlist pl = playlistService.createPlaylist(name);
        playlists.add(pl);
        notifyPlaylistsChanged();
        return pl;
    }

    public void renamePlaylist(long playlistId, String newName) {
        playlistService.renamePlaylist(playlistId, newName);
        playlists.stream()
                .filter(p -> p.getId() == playlistId)
                .findFirst()
                .ifPresent(p -> {
                    p.setName(newName);
                    notifyPlaylistsChanged();
                });
    }

    public void deletePlaylist(long playlistId) {
        playlistService.deletePlaylist(playlistId);
        playlists.removeIf(p -> p.getId() == playlistId);
        if (activePlaylist != null && activePlaylist.getId() == playlistId) {
            activePlaylist = null;
        }
        notifyPlaylistsChanged();
    }

    public void selectPlaylist(Playlist playlist) {
        this.activePlaylist = playlist;
        view.loadPlaylist(playlist);
        settingsService.update(s -> s.setLastActivePlaylistId(playlist.getId()));
        if (onPlaylistSelected != null) onPlaylistSelected.accept(playlist);
    }

    public void addTrackToActivePlaylist(Track track) {
        if (activePlaylist == null) return;
        playlistService.addTrackToPlaylist(activePlaylist.getId(), track.getId());
        view.loadPlaylist(activePlaylist);
    }

    public void addTrackToPlaylist(long playlistId, long trackId) {
        playlistService.addTrackToPlaylist(playlistId, trackId);
        if (activePlaylist != null && activePlaylist.getId() == playlistId) {
            view.loadPlaylist(activePlaylist);
        }
    }

    public void removeTrackFromActivePlaylist(Track track) {
        if (activePlaylist == null) return;
        playlistService.removeTrackFromPlaylist(activePlaylist.getId(), track.getId());
        view.loadPlaylist(activePlaylist);
    }

    public void playActivePlaylist() {
        if (activePlaylist == null || activePlaylist.getTracks().isEmpty()) return;
        playerService.setQueue(List.copyOf(activePlaylist.getTracks()), 0);
        settingsService.update(s -> s.setLastActivePlaylistId(activePlaylist.getId()));
    }

    public void shuffleActivePlaylist() {
        if (activePlaylist == null || activePlaylist.getTracks().isEmpty()) return;
        playerService.shuffleProperty().set(false);
        playerService.setQueue(List.copyOf(activePlaylist.getTracks()), 0);
        playerService.toggleShuffle();
    }

    public void playTrackInPlaylist(Track track) {
        if (activePlaylist == null) return;
        List<Track> tracks = List.copyOf(activePlaylist.getTracks());
        int idx = tracks.indexOf(track);
        if (idx >= 0) playerService.setQueue(tracks, idx);
    }

    private void restoreLastActivePlaylist() {
        long lastId = settingsService.getSettings().getLastActivePlaylistId();
        if (lastId > 0) {
            playlists.stream()
                    .filter(p -> p.getId() == lastId)
                    .findFirst()
                    .ifPresent(p -> activePlaylist = p);
        }
    }

    private void notifyPlaylistsChanged() {
        if (onPlaylistsChanged != null) onPlaylistsChanged.accept(List.copyOf(playlists));
    }

    public ObservableList<Playlist> getPlaylists() { return playlists; }
    public Playlist getActivePlaylist() { return activePlaylist; }

    public void setOnPlaylistsChanged(Consumer<List<Playlist>> handler) { this.onPlaylistsChanged = handler; }
    public void setOnPlaylistSelected(Consumer<Playlist> handler) { this.onPlaylistSelected = handler; }
}
