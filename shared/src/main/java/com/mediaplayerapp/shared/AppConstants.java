package com.mediaplayerapp.shared;

public final class AppConstants {

    private AppConstants() {}

    public static final String APP_NAME = "Waveline";
    public static final String APP_VERSION = "1.0.0";
    public static final String DB_NAME = "waveline";
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String APP_DIR = USER_HOME + "/.waveline";
    public static final String DB_PATH = APP_DIR + "/" + DB_NAME;

    public static final String[] SUPPORTED_AUDIO_EXTENSIONS = {
            ".mp3", ".wav", ".aac", ".m4a", ".flac", ".ogg"
    };

    public static final String[] SUPPORTED_VIDEO_EXTENSIONS = {
            ".mp4", ".m4v", ".mov", ".avi", ".mkv"
    };

    public static final int DEFAULT_VOLUME = 70;
    public static final double DEFAULT_CROSSFADE_SECONDS = 2.0;
    public static final int EQ_BANDS = 10;

    public static final int[] EQ_FREQUENCIES = {
            32, 64, 125, 250, 500, 1000, 2000, 4000, 8000, 16000
    };

    public static final String REPEAT_NONE = "NONE";
    public static final String REPEAT_ONE = "ONE";
    public static final String REPEAT_ALL = "ALL";
}