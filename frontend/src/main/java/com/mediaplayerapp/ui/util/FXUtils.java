package com.mediaplayerapp.ui.util;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class FXUtils {

    private FXUtils() {}

    // ── Unicode symbol map (replaces Ikonli entirely) ─
    private static final Map<String, String> ICONS = Map.ofEntries(
            Map.entry("fas-music",           "♪"),
            Map.entry("fas-compact-disc",    "◉"),
            Map.entry("fas-list",            "≡"),
            Map.entry("fas-heart",           "♥"),
            Map.entry("far-heart",           "♡"),
            Map.entry("fas-sliders-h",       "⚙"),
            Map.entry("fas-gear",            "⚙"),
            Map.entry("fas-cog",             "⚙"),
            Map.entry("fas-plus",            "+"),
            Map.entry("fas-magnifying-glass","🔍"),
            Map.entry("fas-folder-open",     "📂"),
            Map.entry("fas-folder-plus",     "📁"),
            Map.entry("fas-file-audio",      "🎵"),
            Map.entry("fas-play",            "▶"),
            Map.entry("fas-pause",           "⏸"),
            Map.entry("fas-stop",            "⏹"),
            Map.entry("fas-backward-step",   "⏮"),
            Map.entry("fas-forward-step",    "⏭"),
            Map.entry("fas-step-backward",   "⏮"),
            Map.entry("fas-step-forward",    "⏭"),
            Map.entry("fas-shuffle",         "⇄"),
            Map.entry("fas-repeat",          "↺"),
            Map.entry("fas-repeat-1",        "↻"),
            Map.entry("fas-volume-high",     "🔊"),
            Map.entry("fas-volume-up",       "🔊"),
            Map.entry("fas-volume-mute",     "🔇"),
            Map.entry("fas-volume-xmark",    "🔇"),
            Map.entry("fas-volume-off",      "🔈"),
            Map.entry("fas-xmark",           "✕"),
            Map.entry("fas-trash",           "🗑"),
            Map.entry("fas-pen",             "✎"),
            Map.entry("fas-circle-info",     "ℹ"),
            Map.entry("fas-ellipsis",        "•••"),
            Map.entry("fas-floppy-disk",     "💾"),
            Map.entry("fas-rotate-left",     "↺"),
            Map.entry("fas-water",           "≈"),
            Map.entry("fas-wave-square",     "≈"),
            Map.entry("fas-list-music",      "♫"),
            Map.entry("far-folder-open",     "📂"),
            Map.entry("fas-folder",          "📁")
    );

    public static Label icon(String name, int size) {
        return icon(name, size, "#9898A6");
    }

    public static Label icon(String name, int size, String color) {
        String symbol = ICONS.getOrDefault(name, "•");
        Label lbl = new Label(symbol);
        lbl.setStyle(
                "-fx-font-size: " + size + "px;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-padding: 0;"
        );
        lbl.setMouseTransparent(true);
        return lbl;
    }

    public static Label iconLabel(String name, int size) {
        return icon(name, size);
    }

    public static ImageView loadCoverArt(String path, double size) {
        ImageView iv = new ImageView();
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(false);
        iv.setSmooth(true);

        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(14);
        clip.setArcHeight(14);
        iv.setClip(clip);

        if (path != null && !path.isEmpty()) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    Image img = new Image(new FileInputStream(f), size, size, false, true);
                    iv.setImage(img);
                    return iv;
                }
            } catch (Exception ignored) {}
        }
        return iv;
    }

    public static void fadeIn(Node node, double durationMs) {
        node.setOpacity(0);
        javafx.animation.FadeTransition ft =
                new javafx.animation.FadeTransition(javafx.util.Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void scaleIn(Node node, double durationMs) {
        node.setScaleX(0.94);
        node.setScaleY(0.94);
        javafx.animation.ScaleTransition st =
                new javafx.animation.ScaleTransition(javafx.util.Duration.millis(durationMs), node);
        st.setFromX(0.94);
        st.setFromY(0.94);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public static void tooltip(Node node, String text) {
        Tooltip tt = new Tooltip(text);
        Tooltip.install(node, tt);
    }

    public static Region spacer() {
        Region r = new Region();
        javafx.scene.layout.HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.VBox.setVgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    public static javafx.geometry.Insets insets(double all) {
        return new Insets(all);
    }

    public static javafx.geometry.Insets insets(double topBottom, double leftRight) {
        return new Insets(topBottom, leftRight, topBottom, leftRight);
    }
}