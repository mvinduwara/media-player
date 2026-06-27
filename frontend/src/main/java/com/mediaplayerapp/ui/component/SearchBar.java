package com.mediaplayerapp.ui.component;

import com.mediaplayerapp.ui.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class SearchBar extends StackPane {

    private final TextField field;

    public SearchBar(String promptText) {
        field = new TextField();
        field.setPromptText(promptText);
        field.getStyleClass().add("search-field");

        FontIcon searchIcon = FXUtils.icon("fas-magnifying-glass", 13, "#5A5A6A");
        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(searchIcon, new Insets(0, 0, 0, 12));

        StackPane.setAlignment(field, Pos.CENTER);
        getChildren().addAll(field, searchIcon);
        setAlignment(Pos.CENTER_LEFT);
        setPickOnBounds(false);
    }

    public void setOnSearch(Consumer<String> handler) {
        field.textProperty().addListener((obs, old, text) -> handler.accept(text));
    }

    public void clear() {
        field.clear();
    }

    public String getText() {
        return field.getText();
    }

    public TextField getField() {
        return field;
    }
}