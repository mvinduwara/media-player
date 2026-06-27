package com.mediaplayerapp.ui.util;

import javafx.scene.Scene;

public class UITheme {

    private static final String THEME_CSS = "/com/mediaplayerapp/ui/css/theme.css";

    private UITheme() {}

    public static void apply(Scene scene) {
        String css = UITheme.class.getResource(THEME_CSS).toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }

    public static String getThemeCss() {
        return UITheme.class.getResource(THEME_CSS).toExternalForm();
    }
}