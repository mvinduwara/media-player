package com.mediaplayerapp.ui.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;

public class FXUtils {

    private FXUtils() {}

    public static FontIcon icon(String ikonName, int size) {
        FontIcon fi = new FontIcon(ikonName);
        fi.setIconSize(size);
        return fi;
    }

    public static FontIcon icon(String ikonName, int size, String color) {
        FontIcon fi = new FontIcon(ikonName);
        fi.setIconSize(size);
        fi.setIconColor(Color.web(color));
        return fi;
    }

    public static Label iconLabel(String ikonName, int size) {
        FontIcon fi = icon(ikonName, size);
        Label lbl = new Label();
        lbl.setGraphic(fi);
        return lbl;
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

        iv.setImage(null);
        return iv;
    }

    public static void fadeIn(Node node, double durationMs) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(durationMs), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void scaleIn(Node node, double durationMs) {
        node.setScaleX(0.94);
        node.setScaleY(0.94);
        ScaleTransition st = new ScaleTransition(Duration.millis(durationMs), node);
        st.setFromX(0.94);
        st.setFromY(0.94);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    public static void tooltip(Node node, String text) {
        Tooltip tt = new Tooltip(text);
        tt.setStyle("-fx-font-size: 12px;");
        Tooltip.install(node, tt);
    }

    public static Region spacer() {
        Region r = new Region();
        javafx.scene.layout.HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.VBox.setVgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    public static Insets insets(double all) {
        return new Insets(all);
    }

    public static Insets insets(double topBottom, double leftRight) {
        return new Insets(topBottom, leftRight, topBottom, leftRight);
    }
}