package com.mediaplayerapp.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;

public class Playlist {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObservableList<Track> tracks = FXCollections.observableArrayList();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Playlist() {}

    public Playlist(String name) {
        this.name.set(name);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public ObservableList<Track> getTracks() { return tracks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getTrackCount() { return tracks.size(); }

    public long getTotalDurationMillis() {
        return tracks.stream().mapToLong(Track::getDurationMillis).sum();
    }

    public String getFormattedTotalDuration() {
        long totalSeconds = getTotalDurationMillis() / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }

    @Override
    public String toString() {
        return "Playlist{id=" + getId() + ", name='" + getName() + "', tracks=" + getTrackCount() + "}";
    }
}