package com.mediaplayerapp.model;

import javafx.beans.property.*;

public class Track {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty artist = new SimpleStringProperty("Unknown Artist");
    private final StringProperty album = new SimpleStringProperty("Unknown Album");
    private final StringProperty genre = new SimpleStringProperty("");
    private final StringProperty year = new SimpleStringProperty("");
    private final StringProperty filePath = new SimpleStringProperty("");
    private final LongProperty durationMillis = new SimpleLongProperty(0);
    private final IntegerProperty trackNumber = new SimpleIntegerProperty(0);
    private final LongProperty fileSize = new SimpleLongProperty(0);
    private final StringProperty coverArtPath = new SimpleStringProperty("");
    private final IntegerProperty playCount = new SimpleIntegerProperty(0);
    private final BooleanProperty favourite = new SimpleBooleanProperty(false);

    public Track() {}

    public Track(String filePath, String title, String artist, String album) {
        this.filePath.set(filePath);
        this.title.set(title);
        this.artist.set(artist);
        this.album.set(album);
    }

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getArtist() { return artist.get(); }
    public void setArtist(String artist) { this.artist.set(artist); }
    public StringProperty artistProperty() { return artist; }

    public String getAlbum() { return album.get(); }
    public void setAlbum(String album) { this.album.set(album); }
    public StringProperty albumProperty() { return album; }

    public String getGenre() { return genre.get(); }
    public void setGenre(String genre) { this.genre.set(genre); }
    public StringProperty genreProperty() { return genre; }

    public String getYear() { return year.get(); }
    public void setYear(String year) { this.year.set(year); }
    public StringProperty yearProperty() { return year; }

    public String getFilePath() { return filePath.get(); }
    public void setFilePath(String filePath) { this.filePath.set(filePath); }
    public StringProperty filePathProperty() { return filePath; }

    public long getDurationMillis() { return durationMillis.get(); }
    public void setDurationMillis(long durationMillis) { this.durationMillis.set(durationMillis); }
    public LongProperty durationMillisProperty() { return durationMillis; }

    public int getTrackNumber() { return trackNumber.get(); }
    public void setTrackNumber(int trackNumber) { this.trackNumber.set(trackNumber); }
    public IntegerProperty trackNumberProperty() { return trackNumber; }

    public long getFileSize() { return fileSize.get(); }
    public void setFileSize(long fileSize) { this.fileSize.set(fileSize); }
    public LongProperty fileSizeProperty() { return fileSize; }

    public String getCoverArtPath() { return coverArtPath.get(); }
    public void setCoverArtPath(String coverArtPath) { this.coverArtPath.set(coverArtPath); }
    public StringProperty coverArtPathProperty() { return coverArtPath; }

    public int getPlayCount() { return playCount.get(); }
    public void setPlayCount(int playCount) { this.playCount.set(playCount); }
    public IntegerProperty playCountProperty() { return playCount; }

    public boolean isFavourite() { return favourite.get(); }
    public void setFavourite(boolean favourite) { this.favourite.set(favourite); }
    public BooleanProperty favouriteProperty() { return favourite; }

    public String getFormattedDuration() {
        long totalSeconds = durationMillis.get() / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return "Track{id=" + getId() + ", title='" + getTitle() + "', artist='" + getArtist() + "'}";
    }
}