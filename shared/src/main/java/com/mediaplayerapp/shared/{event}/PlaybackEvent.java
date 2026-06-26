package com.mediaplayerapp.shared.event;

public class PlaybackEvent {

    public enum Type {
        PLAY, PAUSE, STOP, NEXT, PREVIOUS, SEEK, VOLUME_CHANGE,
        SHUFFLE_TOGGLE, REPEAT_CHANGE, TRACK_END, QUEUE_CHANGED
    }

    private final Type type;
    private final Object data;

    public PlaybackEvent(Type type) {
        this.type = type;
        this.data = null;
    }

    public PlaybackEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PlaybackEvent{type=" + type + ", data=" + data + "}";
    }
}