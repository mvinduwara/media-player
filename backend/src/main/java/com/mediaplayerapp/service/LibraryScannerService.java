package com.mediaplayerapp.service;

import com.mediaplayerapp.dao.TrackDao;
import com.mediaplayerapp.model.Track;
import com.mediaplayerapp.util.FileUtils;
import com.mediaplayerapp.util.MetadataReader;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class LibraryScannerService {

    private static final Logger log = LoggerFactory.getLogger(LibraryScannerService.class);

    private final TrackDao trackDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "library-scanner");
        t.setDaemon(true);
        return t;
    });

    private final BooleanProperty scanning = new SimpleBooleanProperty(false);
    private final StringProperty scanStatus = new SimpleStringProperty("");
    private final DoubleProperty scanProgress = new SimpleDoubleProperty(0);

    public LibraryScannerService(TrackDao trackDao) {
        this.trackDao = trackDao;
    }

    public void scanFolder(File folder, Consumer<Track> onTrackAdded, Runnable onComplete) {
        executor.submit(() -> {
            Platform.runLater(() -> {
                scanning.set(true);
                scanStatus.set("Scanning " + folder.getName() + "...");
                scanProgress.set(0);
            });

            try {
                List<File> files = FileUtils.scanDirectory(folder);
                int total = files.size();
                int[] processed = {0};

                log.info("Found {} media files in {}", total, folder.getAbsolutePath());

                for (File file : files) {
                    String existingCheck = file.getAbsolutePath();
                    if (trackDao.findByFilePath(existingCheck).isPresent()) {
                        processed[0]++;
                        continue;
                    }

                    Track track = MetadataReader.readMetadata(file);
                    Track saved = trackDao.insert(track);

                    processed[0]++;
                    int progress = processed[0];
                    int pct = total > 0 ? (progress * 100 / total) : 100;

                    Platform.runLater(() -> {
                        scanStatus.set("Importing " + progress + "/" + total + " — " + saved.getTitle());
                        scanProgress.set(pct / 100.0);
                        if (onTrackAdded != null) onTrackAdded.accept(saved);
                    });
                }
            } catch (Exception e) {
                log.error("Error during library scan", e);
            } finally {
                Platform.runLater(() -> {
                    scanning.set(false);
                    scanStatus.set("Scan complete");
                    scanProgress.set(1.0);
                    if (onComplete != null) onComplete.run();
                });
            }
        });
    }

    public BooleanProperty scanningProperty() { return scanning; }
    public StringProperty scanStatusProperty() { return scanStatus; }
    public DoubleProperty scanProgressProperty() { return scanProgress; }

    public void shutdown() {
        executor.shutdownNow();
    }
}
