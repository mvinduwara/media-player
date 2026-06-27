package com.mediaplayerapp.service;

import com.mediaplayerapp.dao.PlaylistDao;
import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlaylistService {

    private static final Logger log = LoggerFactory.getLogger(PlaylistService.class);

    private final PlaylistDao playlistDao;
    private final TrackDao trackDao;

    public PlaylistService(PlaylistDao playlistDao, TrackDao trackDao) {
        this.playlistDao = playlistDao;
        this.trackDao = trackDao;
    }

    public Playlist createPlaylist(String name) {
        Playlist p = new Playlist(name);
        return playlistDao.insert(p);
    }

    public void renamePlaylist(long id, String newName) {
        playlistDao.findById(id).ifPresent(p -> {
            p.setName(newName);
            playlistDao.update(p);
        });
    }

    public void deletePlaylist(long id) {
        playlistDao.delete(id);
        log.info("Deleted playlist id={}", id);
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistDao.findAll();
        for (Playlist p : playlists) {
            List<Track> tracks = trackDao.findByPlaylist(p.getId());
            p.getTracks().setAll(tracks);
        }
        return playlists;
    }

    public Playlist getPlaylistWithTracks(long id) {
        return playlistDao.findById(id).map(p -> {
            p.getTracks().setAll(trackDao.findByPlaylist(p.getId()));
            return p;
        }).orElse(null);
    }

    public void addTrackToPlaylist(long playlistId, long trackId) {
        playlistDao.addTrack(playlistId, trackId);
    }

    public void removeTrackFromPlaylist(long playlistId, long trackId) {
        playlistDao.removeTrack(playlistId, trackId);
    }
}
