package com.mediaplayerapp.util;

import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.shared.AppConstants;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class MetadataReader {

    private static final Logger log = LoggerFactory.getLogger(MetadataReader.class);
    private static final String COVERS_DIR = AppConstants.APP_DIR + "/covers";

    static {
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.SEVERE);
    }

    public static Track readMetadata(File file) {
        Track track = new Track();
        track.setFilePath(file.getAbsolutePath());
        track.setTitle(stripExtension(file.getName()));
        track.setFileSize(file.length());

        try {
            AudioFile af = AudioFileIO.read(file);
            AudioHeader header = af.getAudioHeader();
            track.setDurationMillis((long) header.getTrackLength() * 1000);

            Tag tag = af.getTag();
            if (tag != null) {
                String title = getTag(tag, FieldKey.TITLE);
                if (!title.isEmpty()) track.setTitle(title);

                String artist = getTag(tag, FieldKey.ARTIST);
                if (!artist.isEmpty()) track.setArtist(artist);

                String album = getTag(tag, FieldKey.ALBUM);
                if (!album.isEmpty()) track.setAlbum(album);

                String genre = getTag(tag, FieldKey.GENRE);
                if (!genre.isEmpty()) track.setGenre(genre);

                String year = getTag(tag, FieldKey.YEAR);
                if (!year.isEmpty()) track.setYear(year);

                String trackNum = getTag(tag, FieldKey.TRACK);
                if (!trackNum.isEmpty()) {
                    try { track.setTrackNumber(Integer.parseInt(trackNum.split("/")[0])); }
                    catch (NumberFormatException ignored) {}
                }

                extractCoverArt(tag, track);
            }
        } catch (Exception e) {
            log.warn("Could not read metadata from {}: {}", file.getName(), e.getMessage());
        }

        return track;
    }

    private static String getTag(Tag tag, FieldKey key) {
        try {
            String val = tag.getFirst(key);
            return val != null ? val.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static void extractCoverArt(Tag tag, Track track) {
        try {
            Artwork artwork = tag.getFirstArtwork();
            if (artwork == null) return;

            File coversDir = new File(COVERS_DIR);
            if (!coversDir.exists()) coversDir.mkdirs();

            String safeArtist = track.getArtist().replaceAll("[^a-zA-Z0-9]", "_");
            String safeAlbum = track.getAlbum().replaceAll("[^a-zA-Z0-9]", "_");
            String coverFileName = safeArtist + "_" + safeAlbum + ".jpg";
            Path coverPath = Paths.get(COVERS_DIR, coverFileName);

            if (!Files.exists(coverPath)) {
                byte[] imageData = artwork.getBinaryData();
                if (imageData != null && imageData.length > 0) {
                    try (FileOutputStream fos = new FileOutputStream(coverPath.toFile())) {
                        fos.write(imageData);
                    }
                }
            }
            track.setCoverArtPath(coverPath.toAbsolutePath().toString());
        } catch (Exception e) {
            log.debug("Could not extract cover art for track: {}", track.getTitle());
        }
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}
