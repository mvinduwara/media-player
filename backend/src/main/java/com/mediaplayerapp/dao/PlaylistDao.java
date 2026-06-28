package com.mediaplayerapp.dao;

import com.mediaplayerapp.db.DatabaseManager;
import com.mediaplayerapp.model.Playlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlaylistDao {

    private static final Logger log = LoggerFactory.getLogger(PlaylistDao.class);

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public Playlist insert(Playlist playlist) {
        String sql = "INSERT INTO playlists (playlist_name, description) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, playlist.getName());
            ps.setString(2, playlist.getDescription());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) playlist.setId(keys.getLong(1));
        } catch (SQLException e) {
            log.error("Failed to insert playlist: {}", playlist.getName(), e);
        }
        return playlist;
    }

    public void update(Playlist playlist) {
        String sql = "UPDATE playlists SET playlist_name=?, description=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, playlist.getName());
            ps.setString(2, playlist.getDescription());
            ps.setLong(3, playlist.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update playlist id={}", playlist.getId(), e);
        }
    }

    public void delete(long id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM playlists WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete playlist id={}", id, e);
        }
    }

    public Optional<Playlist> findById(long id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM playlists WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Failed to find playlist id={}", id, e);
        }
        return Optional.empty();
    }

    public List<Playlist> findAll() {
        List<Playlist> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM playlists ORDER BY playlist_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Failed to list playlists", e);
        }
        return list;
    }

    public void addTrack(long playlistId, long trackId) {
        String sql =
                "MERGE INTO playlist_tracks (playlist_id, track_id, sort_position) " +
                        "KEY(playlist_id, track_id) " +
                        "VALUES (?, ?, (SELECT COALESCE(MAX(sort_position), 0) + 1 FROM playlist_tracks WHERE playlist_id=?))";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            ps.setLong(2, trackId);
            ps.setLong(3, playlistId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to add track {} to playlist {}", trackId, playlistId, e);
        }
    }

    public void removeTrack(long playlistId, long trackId) {
        String sql = "DELETE FROM playlist_tracks WHERE playlist_id=? AND track_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            ps.setLong(2, trackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to remove track {} from playlist {}", trackId, playlistId, e);
        }
    }

    public void reorderTrack(long playlistId, long trackId, int newPosition) {
        String sql = "UPDATE playlist_tracks SET sort_position=? WHERE playlist_id=? AND track_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, newPosition);
            ps.setLong(2, playlistId);
            ps.setLong(3, trackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to reorder track {} in playlist {}", trackId, playlistId, e);
        }
    }

    public int getTrackCount(long playlistId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM playlist_tracks WHERE playlist_id=?")) {
            ps.setLong(1, playlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            log.error("Failed to count tracks for playlist id={}", playlistId, e);
        }
        return 0;
    }

    private Playlist mapRow(ResultSet rs) throws SQLException {
        Playlist p = new Playlist();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("playlist_name"));
        p.setDescription(rs.getString("description"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        if (created != null) p.setCreatedAt(created.toLocalDateTime());
        if (updated != null) p.setUpdatedAt(updated.toLocalDateTime());
        return p;
    }
}