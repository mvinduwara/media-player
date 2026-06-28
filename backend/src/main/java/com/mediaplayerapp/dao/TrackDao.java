package com.mediaplayerapp.dao;

import com.mediaplayerapp.db.DatabaseManager;
import com.mediaplayerapp.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrackDao {

    private static final Logger log = LoggerFactory.getLogger(TrackDao.class);

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public Track insert(Track track) {
        String sql =
                "INSERT INTO tracks (title, artist, album, genre, release_year, file_path," +
                        "    duration_millis, track_number, file_size, cover_art_path)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, track.getTitle());
            ps.setString(2, track.getArtist());
            ps.setString(3, track.getAlbum());
            ps.setString(4, track.getGenre());
            ps.setString(5, track.getYear());
            ps.setString(6, track.getFilePath());
            ps.setLong(7, track.getDurationMillis());
            ps.setInt(8, track.getTrackNumber());
            ps.setLong(9, track.getFileSize());
            ps.setString(10, track.getCoverArtPath());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) track.setId(keys.getLong(1));
        } catch (SQLException e) {
            log.error("Failed to insert track: {}", track.getFilePath(), e);
        }
        return track;
    }

    public void update(Track track) {
        String sql =
                "UPDATE tracks SET title=?, artist=?, album=?, genre=?, release_year=?," +
                        "    duration_millis=?, track_number=?, cover_art_path=?," +
                        "    play_count=?, favourite=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, track.getTitle());
            ps.setString(2, track.getArtist());
            ps.setString(3, track.getAlbum());
            ps.setString(4, track.getGenre());
            ps.setString(5, track.getYear());
            ps.setLong(6, track.getDurationMillis());
            ps.setInt(7, track.getTrackNumber());
            ps.setString(8, track.getCoverArtPath());
            ps.setInt(9, track.getPlayCount());
            ps.setBoolean(10, track.isFavourite());
            ps.setLong(11, track.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to update track id={}", track.getId(), e);
        }
    }

    public void delete(long id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM tracks WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete track id={}", id, e);
        }
    }

    public Optional<Track> findById(long id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM tracks WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Failed to find track by id={}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Track> findByFilePath(String filePath) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM tracks WHERE file_path=?")) {
            ps.setString(1, filePath);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            log.error("Failed to find track by path={}", filePath, e);
        }
        return Optional.empty();
    }

    public List<Track> findAll() {
        return query("SELECT * FROM tracks ORDER BY artist, album, track_number, title");
    }

    public List<Track> findFavourites() {
        return query("SELECT * FROM tracks WHERE favourite=TRUE ORDER BY artist, title");
    }

    public List<Track> search(String query) {
        String like = "%" + query.toLowerCase() + "%";
        String sql =
                "SELECT * FROM tracks WHERE" +
                        "    LOWER(title) LIKE ? OR LOWER(artist) LIKE ? OR LOWER(album) LIKE ?" +
                        "ORDER BY artist, title";
        List<Track> results = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Search failed for query: {}", query, e);
        }
        return results;
    }

    public List<Track> findByPlaylist(long playlistId) {
        String sql =
                "SELECT t.* FROM tracks t" +
                        "    JOIN playlist_tracks pt ON t.id = pt.track_id" +
                        "    WHERE pt.playlist_id = ?" +
                        "    ORDER BY pt.sort_position";
        List<Track> results = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setLong(1, playlistId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) results.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Failed to find tracks for playlist id={}", playlistId, e);
        }
        return results;
    }

    public void incrementPlayCount(long trackId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE tracks SET play_count = play_count + 1 WHERE id=?")) {
            ps.setLong(1, trackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to increment play count for track id={}", trackId, e);
        }
    }

    public void toggleFavourite(long trackId, boolean favourite) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE tracks SET favourite=? WHERE id=?")) {
            ps.setBoolean(1, favourite);
            ps.setLong(2, trackId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to toggle favourite for track id={}", trackId, e);
        }
    }

    private List<Track> query(String sql) {
        List<Track> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            log.error("Query failed: {}", sql, e);
        }
        return list;
    }

    private Track mapRow(ResultSet rs) throws SQLException {
        Track t = new Track();
        t.setId(rs.getLong("id"));
        t.setTitle(rs.getString("title"));
        t.setArtist(rs.getString("artist"));
        t.setAlbum(rs.getString("album"));
        t.setGenre(rs.getString("genre"));
        t.setYear(rs.getString("release_year"));
        t.setFilePath(rs.getString("file_path"));
        t.setDurationMillis(rs.getLong("duration_millis"));
        t.setTrackNumber(rs.getInt("track_number"));
        t.setFileSize(rs.getLong("file_size"));
        t.setCoverArtPath(rs.getString("cover_art_path"));
        t.setPlayCount(rs.getInt("play_count"));
        t.setFavourite(rs.getBoolean("favourite"));
        return t;
    }
}