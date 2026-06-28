package com.mediaplayerapp.ui.component;

import com.mediaplayerapp.model.Playlist;
import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;


import java.util.List;
import java.util.function.Consumer;

public class SideNav extends VBox {

    public enum NavItem {
        LIBRARY, NOW_PLAYING, QUEUE, EQUALIZER, FAVOURITES, SETTINGS
    }

    private NavItem selectedItem = NavItem.LIBRARY;
    private Consumer<NavItem> onNavSelect;
    private Consumer<Playlist> onPlaylistSelect;
    private Runnable onNewPlaylist;
    private final VBox playlistContainer = new VBox();

    public SideNav() {
        super();
        getStyleClass().add("side-nav");
        setPrefWidth(220);
        setMinWidth(220);
        setMaxWidth(220);
        buildUI();
    }

    private void buildUI() {
        getChildren().addAll(
                buildLogo(),
                buildMainNav(),
                buildPlaylistSection(),
                FXUtils.spacer()
        );
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private HBox buildLogo() {
        HBox logoBox = new HBox(8);
        logoBox.getStyleClass().add("nav-logo-box");
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label waveIcon = FXUtils.icon("fas-wave-square", 20, "#7C3AED");
        Text logoText = new Text("Wave");
        logoText.getStyleClass().add("nav-logo-text");
        Text logoAccent = new Text("line");
        logoAccent.getStyleClass().addAll("nav-logo-text", "nav-logo-dot");

        HBox textBox = new HBox(0, logoText, logoAccent);
        textBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().addAll(waveIcon, textBox);
        return logoBox;
    }

    private VBox buildMainNav() {
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 0, 0, 0));

        Label libLabel = navSectionLabel("Library");
        nav.getChildren().add(libLabel);
        nav.getChildren().add(navBtn("fas-music",        "Library",     NavItem.LIBRARY));
        nav.getChildren().add(navBtn("fas-compact-disc", "Now Playing", NavItem.NOW_PLAYING));
        nav.getChildren().add(navBtn("fas-list",         "Queue",       NavItem.QUEUE));
        nav.getChildren().add(navBtn("fas-heart",        "Favourites",  NavItem.FAVOURITES));

        Label toolsLabel = navSectionLabel("Tools");
        nav.getChildren().add(toolsLabel);
        nav.getChildren().add(navBtn("fas-sliders-h",   "Equalizer",   NavItem.EQUALIZER));
        nav.getChildren().add(navBtn("fas-gear",         "Settings",    NavItem.SETTINGS));

        return nav;
    }

    private VBox buildPlaylistSection() {
        VBox section = new VBox(2);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label label = navSectionLabel("Playlists");

        Button addBtn = new Button();
        addBtn.getStyleClass().add("btn-icon");
        addBtn.setGraphic(FXUtils.icon("fas-plus", 11, "#9898A6"));
        addBtn.setOnAction(e -> { if (onNewPlaylist != null) onNewPlaylist.run(); });
        FXUtils.tooltip(addBtn, "New playlist");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(label, spacer, addBtn);
        header.setPadding(new Insets(0, 8, 0, 0));

        playlistContainer.setSpacing(1);

        ScrollPane scroll = new ScrollPane(playlistContainer);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefHeight(200);
        scroll.setMaxHeight(300);

        section.getChildren().addAll(header, scroll);
        return section;
    }

    private Label navSectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.getStyleClass().add("nav-section-label");
        l.setPadding(new Insets(16, 20, 4, 20));
        return l;
    }

    private Button navBtn(String iconName, String label, NavItem item) {
        Button btn = new Button();
        btn.getStyleClass().add("nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        Label ico = FXUtils.icon(iconName, 14, "#9898A6");
        Label lbl = new Label(label);
        lbl.setMouseTransparent(true);
        content.getChildren().addAll(ico, lbl);
        btn.setGraphic(content);

        if (item == selectedItem) {
            btn.getStyleClass().add("selected");
        }

        btn.setOnAction(e -> {
            selectItem(item);
            if (onNavSelect != null) onNavSelect.accept(item);
        });

        btn.setUserData(item);
        return btn;
    }

    private void selectItem(NavItem item) {
        this.selectedItem = item;
        getChildren().forEach(node -> updateButtonStyles(node, item));
    }

    private void updateButtonStyles(javafx.scene.Node node, NavItem selected) {
        if (node instanceof VBox vbox) {
            vbox.getChildren().forEach(child -> updateButtonStyles(child, selected));
        } else if (node instanceof HBox hbox) {
            hbox.getChildren().forEach(child -> updateButtonStyles(child, selected));
        } else if (node instanceof Button btn && btn.getUserData() instanceof NavItem ni) {
            btn.getStyleClass().remove("selected");
            if (ni == selected) btn.getStyleClass().add("selected");
        }
    }

    public void setPlaylists(List<Playlist> playlists) {
        playlistContainer.getChildren().clear();
        for (Playlist pl : playlists) {
            Button btn = new Button(pl.getName());
            btn.getStyleClass().add("nav-playlist-item");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setGraphic(FXUtils.icon("fas-list", 12, "#5A5A6A"));
            btn.setGraphicTextGap(10);
            btn.setOnAction(e -> { if (onPlaylistSelect != null) onPlaylistSelect.accept(pl); });
            playlistContainer.getChildren().add(btn);
        }
    }

    public void setOnNavSelect(Consumer<NavItem> handler)      { this.onNavSelect = handler; }
    public void setOnPlaylistSelect(Consumer<Playlist> handler){ this.onPlaylistSelect = handler; }
    public void setOnNewPlaylist(Runnable handler)              { this.onNewPlaylist = handler; }
}
