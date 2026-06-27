package com.mediaplayerapp.util;

import com.mediaplayerapp.shared.AppConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private FileUtils() {}

    public static List<File> scanDirectory(File directory) {
        List<File> mediaFiles = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return mediaFiles;
        scanRecursive(directory, mediaFiles);
        return mediaFiles;
    }

    private static void scanRecursive(File dir, List<File> results) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanRecursive(file, results);
            } else if (isMediaFile(file)) {
                results.add(file);
            }
        }
    }

    public static boolean isMediaFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : AppConstants.SUPPORTED_AUDIO_EXTENSIONS) {
            if (name.endsWith(ext)) return true;
        }
        for (String ext : AppConstants.SUPPORTED_VIDEO_EXTENSIONS) {
            if (name.endsWith(ext)) return true;
        }
        return false;
    }

    public static boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : AppConstants.SUPPORTED_AUDIO_EXTENSIONS) {
            if (name.endsWith(ext)) return true;
        }
        return false;
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
