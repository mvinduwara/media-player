package com.mediaplayerapp.shared.event;

public class TrackChangeEvent {

    private final long previousTrackId;
    private final long newTrackId;
    private final String newTrackTitle;
    private final String newTrackArtist;

    public TrackChangeEvent(long previousTrackId, long newTrackId,
                            String newTrackTitle, String newTrackArtist) {
        this.previousTrackId = previousTrackId;
        this.newTrackId = newTrackId;
        this.newTrackTitle = newTrackTitle;
        this.newTrackArtist = newTrackArtist;
    }

    public long getPreviousTrackId() { return previousTrackId; }
    public long getNewTrackId() { return newTrackId; }
    public String getNewTrackTitle() { return newTrackTitle; }
    public String getNewTrackArtist() { return newTrackArtist; }
}