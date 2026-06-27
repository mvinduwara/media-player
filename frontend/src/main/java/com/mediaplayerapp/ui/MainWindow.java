package com.mediaplayerapp.ui;

import com.mediaplayerapp.ui.component.PlayerBar;
import com.mediaplayerapp.ui.component.SideNav;
import com.mediaplayerapp.ui.controller.MainController;
import com.mediaplayerapp.ui.util.FXUtils;
import com.mediaplayerapp.ui.util.UITheme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainWindow {

    private final Stage stage;
    private final BorderPane root;
    private final SideNav sideNav;
    private final StackPane contentHolder;
    private MainController controller;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.sideNav = new SideNav();
        this.contentHolder = new StackPane();

        root.setStyle("-fx-background-color: #141416;");
        contentHolder.setStyle("-fx-background-color: #141416;");

        root.setLeft(sideNav);
        root.setCenter(contentHolder);
    }

    public void show() {
        Scene scene = new Scene(root, 1200, 760);
        UITheme.apply(scene);
        stage.setScene(scene);
        stage.setTitle("Waveline");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();

        controller = new MainController(this);
        controller.initialize();

        stage.setOnCloseRequest(e -> controller.onShutdown());
    }

    public void showView(Node view) {
        contentHolder.getChildren().setAll(view);
        FXUtils.fadeIn(view, 160);
    }

    public void setPlayerBar(PlayerBar playerBar) {
        root.setBottom(playerBar);
    }

    public SideNav getSideNav() {
        return sideNav;
    }

    public void setTitle(String title) {
        stage.setTitle(title + " · Waveline");
    }
}
package com.mediaplayerapp.ui;

import com.mediaplayerapp.ui.component.PlayerBar;
import com.mediaplayerapp.ui.component.SideNav;
import com.mediaplayerapp.ui.controller.MainController;
import com.mediaplayerapp.ui.util.FXUtils;
import com.mediaplayerapp.ui.util.UITheme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
        import javafx.stage.Stage;

public class MainWindow {

    private final Stage stage;
    private final BorderPane root;
    private final SideNav sideNav;
    private final StackPane contentHolder;
    private MainController controller;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.sideNav = new SideNav();
        this.contentHolder = new StackPane();

        root.setStyle("-fx-background-color: #141416;");
        contentHolder.setStyle("-fx-background-color: #141416;");

        root.setLeft(sideNav);
        root.setCenter(contentHolder);
    }

    public void show() {
        Scene scene = new Scene(root, 1200, 760);
        UITheme.apply(scene);
        stage.setScene(scene);
        stage.setTitle("Waveline");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();

        controller = new MainController(this);
        controller.initialize();

        stage.setOnCloseRequest(e -> controller.onShutdown());
    }

    public void showView(Node view) {
        contentHolder.getChildren().setAll(view);
        FXUtils.fadeIn(view, 160);
    }

    public void setPlayerBar(PlayerBar playerBar) {
        root.setBottom(playerBar);
    }

    public SideNav getSideNav() {
        return sideNav;
    }

    public void setTitle(String title) {
        stage.setTitle(title + " · Waveline");
    }
}
package com.mediaplayerapp.ui;

import com.mediaplayerapp.ui.component.PlayerBar;
import com.mediaplayerapp.ui.component.SideNav;
import com.mediaplayerapp.ui.controller.MainController;
import com.mediaplayerapp.ui.util.FXUtils;
import com.mediaplayerapp.ui.util.UITheme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
        import javafx.stage.Stage;

public class MainWindow {

    private final Stage stage;
    private final BorderPane root;
    private final SideNav sideNav;
    private final StackPane contentHolder;
    private MainController controller;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.sideNav = new SideNav();
        this.contentHolder = new StackPane();

        root.setStyle("-fx-background-color: #141416;");
        contentHolder.setStyle("-fx-background-color: #141416;");

        root.setLeft(sideNav);
        root.setCenter(contentHolder);
    }

    public void show() {
        Scene scene = new Scene(root, 1200, 760);
        UITheme.apply(scene);
        stage.setScene(scene);
        stage.setTitle("Waveline");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();

        controller = new MainController(this);
        controller.initialize();

        stage.setOnCloseRequest(e -> controller.onShutdown());
    }

    public void showView(Node view) {
        contentHolder.getChildren().setAll(view);
        FXUtils.fadeIn(view, 160);
    }

    public void setPlayerBar(PlayerBar playerBar) {
        root.setBottom(playerBar);
    }

    public SideNav getSideNav() {
        return sideNav;
    }

    public void setTitle(String title) {
        stage.setTitle(title + " · Waveline");
    }
}
package com.mediaplayerapp.ui;

import com.mediaplayerapp.ui.component.PlayerBar;
import com.mediaplayerapp.ui.component.SideNav;
import com.mediaplayerapp.ui.controller.MainController;
import com.mediaplayerapp.ui.util.FXUtils;
import com.mediaplayerapp.ui.util.UITheme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
        import javafx.stage.Stage;

public class MainWindow {

    private final Stage stage;
    private final BorderPane root;
    private final SideNav sideNav;
    private final StackPane contentHolder;
    private MainController controller;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.sideNav = new SideNav();
        this.contentHolder = new StackPane();

        root.setStyle("-fx-background-color: #141416;");
        contentHolder.setStyle("-fx-background-color: #141416;");

        root.setLeft(sideNav);
        root.setCenter(contentHolder);
    }

    public void show() {
        Scene scene = new Scene(root, 1200, 760);
        UITheme.apply(scene);
        stage.setScene(scene);
        stage.setTitle("Waveline");
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();

        controller = new MainController(this);
        controller.initialize();

        stage.setOnCloseRequest(e -> controller.onShutdown());
    }

    public void showView(Node view) {
        contentHolder.getChildren().setAll(view);
        FXUtils.fadeIn(view, 160);
    }

    public void setPlayerBar(PlayerBar playerBar) {
        root.setBottom(playerBar);
    }

    public SideNav getSideNav() {
        return sideNav;
    }

    public void setTitle(String title) {
        stage.setTitle(title + " · Waveline");
    }
}
